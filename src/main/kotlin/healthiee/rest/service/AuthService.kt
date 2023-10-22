package healthiee.rest.service

import healthiee.rest.api.auth.dto.AuthenticationDto
import healthiee.rest.api.auth.dto.TokenDto
import healthiee.rest.api.auth.dto.request.AuthRequest
import healthiee.rest.api.auth.dto.request.CodeLoginRequest
import healthiee.rest.api.auth.dto.request.RefreshTokenRequest
import healthiee.rest.api.auth.dto.request.RegisterRequest
import healthiee.rest.api.auth.dto.response.AuthResponse
import healthiee.rest.api.auth.dto.response.VerifyCodeResponse
import healthiee.rest.domain.auth.EmailAuth
import healthiee.rest.domain.auth.Token
import healthiee.rest.domain.hashtag.Hashtag
import healthiee.rest.domain.member.Member
import healthiee.rest.lib.authority.JwtTokenProvider
import healthiee.rest.lib.authority.TokenType
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ApplicationErrorCode.FORBIDDEN_INVALID_REFRESH_TOKEN
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_CODE
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_MEMBER
import healthiee.rest.lib.mail.MailSender
import healthiee.rest.lib.uploader.MediaDomainType
import healthiee.rest.lib.uploader.MediaType
import healthiee.rest.lib.uploader.S3Uploader
import healthiee.rest.repository.auth.EmailAuthRepository
import healthiee.rest.repository.auth.TokenRepository
import healthiee.rest.repository.hashtag.HashtagRepository
import healthiee.rest.repository.member.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional(readOnly = true)
class AuthService(
    private val emailAuthRepository: EmailAuthRepository,
    private val memberRepository: MemberRepository,
    private val tokenRepository: TokenRepository,
    private val hashtagRepository: HashtagRepository,
    private val mailSender: MailSender,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val s3Uploader: S3Uploader,
) {

    @Transactional
    fun auth(request: AuthRequest): AuthResponse {
        val findMember = memberRepository.findByEmail(request.email)
        val registered = findMember != null

        val emailAuth = EmailAuth.createEmailAuth(request.email)
        emailAuthRepository.save(emailAuth)
        mailSender.send(request.email, registered, emailAuth.code)

        return AuthResponse(registered)
    }

    @Transactional
    fun codeLogin(request: CodeLoginRequest): AuthenticationDto {
        val findEmailAuth = emailAuthRepository.findByCode(request.code)
        findEmailAuth ?: throw ApiException(NOT_FOUND_CODE)
        if (findEmailAuth.disabled) throw ApiException(NOT_FOUND_CODE)

        val diff = Duration.between(findEmailAuth.createdDate, LocalDateTime.now())
        if (diff.toHours() >= 24) throw ApiException(NOT_FOUND_CODE)

        val findMember = memberRepository.findByEmail(findEmailAuth.email)
        findMember ?: throw ApiException(NOT_FOUND_MEMBER)

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(findMember.id, findMember.nickname)
        )

        val token = Token.createToken(findMember)
        tokenRepository.save(token)

        val refreshClaims = mapOf("rotationCounter" to 1, "tokenId" to token.id)
        val accessToken = jwtTokenProvider.generateToken(findMember)
        val refreshToken = jwtTokenProvider.generateRefreshToken(refreshClaims, findMember)

        findEmailAuth.used()

        return AuthenticationDto(
            memberId = findMember.id,
            tokens = TokenDto(accessToken, refreshToken)
        )
    }

    fun verifyCode(code: UUID): VerifyCodeResponse {
        val findEmailAuth = emailAuthRepository.findByCode(code)
        findEmailAuth ?: throw ApiException(NOT_FOUND_CODE)
        if (findEmailAuth.disabled) throw ApiException(NOT_FOUND_CODE)

        val diff = Duration.between(findEmailAuth.createdDate, LocalDateTime.now())
        if (diff.toHours() >= 24) throw ApiException(NOT_FOUND_CODE)

        return VerifyCodeResponse(findEmailAuth.email)
    }

    @Transactional
    fun register(request: RegisterRequest, image: MultipartFile?): AuthenticationDto {
        val findEmailAuth = emailAuthRepository.findByCode(UUID.fromString(request.code))
        findEmailAuth ?: throw ApiException(NOT_FOUND_CODE)
        if (findEmailAuth.disabled) throw ApiException(NOT_FOUND_CODE)

        val diff = Duration.between(findEmailAuth.createdDate, LocalDateTime.now())
        if (diff.toHours() >= 24) throw ApiException(NOT_FOUND_CODE)

        var profileUrl: String? = null
        if (image != null && !image.isEmpty) {
            profileUrl = s3Uploader.upload(image, MediaType.IMAGE, MediaDomainType.PROFILE_IMAGE)
        }

        val workoutHashtags = mutableListOf<Hashtag>()
        if (request.workouts != null) {
            val newHashtags = mutableListOf<Hashtag>()
            request.workouts.forEach {
                hashtagRepository.findByName(it)?.let { hashtag ->
                    workoutHashtags.add(hashtag)
                } ?: run {
                    newHashtags.add(Hashtag.createHashtag(it))
                }
            }
            hashtagRepository.saveAll(newHashtags)
            workoutHashtags.addAll(newHashtags)
        }

        val member = Member.createMember(
            Member.MemberParam(
                email = findEmailAuth.email,
                password = passwordEncoder.encode(request.nickname),
                name = request.name,
                nickname = request.nickname,
                bio = request.bio,
                profileUrl = profileUrl,
                workoutHashtags = workoutHashtags,
            )
        )
        memberRepository.save(member)

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(member.id, member.nickname)
        )

        val token = Token.createToken(member)
        tokenRepository.save(token)

        val refreshClaims = mapOf("rotationCounter" to 1, "tokenId" to token.id)
        val accessToken = jwtTokenProvider.generateToken(member)
        val refreshToken = jwtTokenProvider.generateRefreshToken(refreshClaims, member)

        findEmailAuth.used()

        return AuthenticationDto(
            memberId = member.id,
            tokens = TokenDto(accessToken, refreshToken)
        )
    }

    @Transactional
    fun refreshToken(request: RefreshTokenRequest): AuthenticationDto {
        val type: String = jwtTokenProvider.extractClaim(request.refreshToken) {
            it.get("type", String::class.java)
        } ?: throw ApiException(FORBIDDEN_INVALID_REFRESH_TOKEN)
        val tokenId: String = jwtTokenProvider.extractClaim(request.refreshToken) {
            it.get("tokenId", String::class.java)
        } ?: throw ApiException(FORBIDDEN_INVALID_REFRESH_TOKEN)
        val memberId: String = jwtTokenProvider.extractUsername(request.refreshToken)
            ?: throw ApiException(FORBIDDEN_INVALID_REFRESH_TOKEN)
        val rotationCounter: Int = jwtTokenProvider.extractClaim(request.refreshToken) {
            it.get("rotationCounter", Integer::class.java)
        }?.toInt() ?: throw ApiException(FORBIDDEN_INVALID_REFRESH_TOKEN)

        if (type != TokenType.REFRESH_TOKEN.name.lowercase()) {
            throw ApiException(FORBIDDEN_INVALID_REFRESH_TOKEN)
        }

        val findMember = memberRepository.findByIdOrNull(UUID.fromString(memberId))
            ?: throw ApiException(FORBIDDEN_INVALID_REFRESH_TOKEN)
        val findToken = tokenRepository.findByIdIncludeMember(UUID.fromString(tokenId))
            ?: throw ApiException(FORBIDDEN_INVALID_REFRESH_TOKEN)
        if (findToken.blocked) throw ApiException(FORBIDDEN_INVALID_REFRESH_TOKEN)
        if (findToken.rotationCounter != rotationCounter) {
            findToken.block()
            throw ApiException(FORBIDDEN_INVALID_REFRESH_TOKEN)
        }

        findToken.increaseRotationCounter()

        val refreshClaims = mapOf("rotationCounter" to findToken.rotationCounter, "tokenId" to tokenId)
        val accessToken = jwtTokenProvider.generateToken(findMember)
        val refreshToken = jwtTokenProvider.generateRefreshToken(refreshClaims, findMember)

        return AuthenticationDto(
            findMember.id,
            TokenDto(accessToken, refreshToken)
        )
    }

}