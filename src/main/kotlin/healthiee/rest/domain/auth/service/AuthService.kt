package healthiee.rest.domain.auth.service

import healthiee.rest.domain.auth.dto.AuthenticationTempDto
import healthiee.rest.domain.auth.dto.request.AuthRequest
import healthiee.rest.domain.auth.dto.request.CodeLoginRequest
import healthiee.rest.domain.auth.dto.request.RegisterRequest
import healthiee.rest.domain.auth.dto.response.AuthResponse
import healthiee.rest.domain.auth.dto.response.VerifyCodeResponse
import healthiee.rest.domain.auth.entity.EmailAuth
import healthiee.rest.domain.auth.entity.Token
import healthiee.rest.domain.auth.repository.EmailAuthRepository
import healthiee.rest.domain.auth.repository.TokenRepository
import healthiee.rest.domain.common.entity.media.MediaType
import healthiee.rest.domain.hashtag.entity.Hashtag
import healthiee.rest.domain.hashtag.repository.HashtagRepository
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.member.repository.MemberRepository
import healthiee.rest.lib.authority.JwtTokenProvider
import healthiee.rest.lib.authority.TokenType
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.FORBIDDEN
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
import healthiee.rest.lib.mail.model.MailBuilderParams
import healthiee.rest.lib.mail.model.MailSenderParams
import healthiee.rest.lib.mail.sender.MailSender
import healthiee.rest.lib.mail.template.MailBuilder
import healthiee.rest.lib.uploader.MediaDomainType
import healthiee.rest.lib.uploader.S3Uploader
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
    private val mailBuilder: MailBuilder,
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

        val mailBuilderParams = MailBuilderParams(registered, emailAuth.code)

        mailSender.send(
            MailSenderParams(
                request.email,
                mailBuilder.makeSubject(mailBuilderParams),
                mailBuilder.makeBody(mailBuilderParams),
            )
        )

        return AuthResponse(registered)
    }

    @Transactional
    fun codeLogin(request: CodeLoginRequest): AuthenticationTempDto {
        val findEmailAuth = emailAuthRepository.findByCode(request.code)
        findEmailAuth ?: throw ApiException(NOT_FOUND, "인증 코드를 찾을 수 없습니다")
        if (findEmailAuth.disabled) throw ApiException(NOT_FOUND, "인증 코드를 찾을 수 없습니다")

        val diff = Duration.between(findEmailAuth.createdDate, LocalDateTime.now())
        if (diff.toHours() >= 24) throw ApiException(NOT_FOUND, "인증 코드를 찾을 수 없습니다")

        val findMember = memberRepository.findByEmail(findEmailAuth.email)
        findMember ?: throw ApiException(NOT_FOUND, "인증 코드를 찾을 수 없습니다")

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(findMember.id, findMember.nickname)
        )

        val token = Token.createToken(findMember)
        tokenRepository.save(token)

        val refreshClaims = mapOf("rotationCounter" to 1, "tokenId" to token.id)
        val accessToken = jwtTokenProvider.generateToken(findMember)
        val refreshToken = jwtTokenProvider.generateRefreshToken(refreshClaims, findMember)

        findEmailAuth.used()

        return AuthenticationTempDto(
            memberId = findMember.id,
            token = accessToken,
            refreshToken = refreshToken,
        )
    }

    fun verifyCode(code: UUID): VerifyCodeResponse {
        val findEmailAuth = emailAuthRepository.findByCode(code)
        findEmailAuth ?: throw ApiException(NOT_FOUND, "인증 코드를 찾을 수 없습니다")
        if (findEmailAuth.disabled) throw ApiException(NOT_FOUND, "인증 코드를 찾을 수 없습니다")

        val diff = Duration.between(findEmailAuth.createdDate, LocalDateTime.now())
        if (diff.toHours() >= 24) throw ApiException(NOT_FOUND, "인증 코드를 찾을 수 없습니다")

        return VerifyCodeResponse(findEmailAuth.email)
    }

    @Transactional
    fun register(request: RegisterRequest, image: MultipartFile?): AuthenticationTempDto {
        val findEmailAuth =
            emailAuthRepository.findByCode(UUID.fromString(request.code)) ?: throw ApiException(
                NOT_FOUND,
                "인증 코드를 찾을 수 없습니다"
            )
        if (findEmailAuth.disabled) throw ApiException(NOT_FOUND, "인증 코드를 찾을 수 없습니다")

        val diff = Duration.between(findEmailAuth.createdDate, LocalDateTime.now())
        if (diff.toHours() >= 24) throw ApiException(NOT_FOUND, "인증 코드를 찾을 수 없습니다")

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

        return AuthenticationTempDto(
            memberId = member.id,
            token = accessToken,
            refreshToken = refreshToken,
        )
    }

    @Transactional
    fun refreshToken(refreshToken: String): AuthenticationTempDto {
        val type: String = jwtTokenProvider.extractClaim(refreshToken) {
            it.get("type", String::class.java)
        } ?: throw ApiException(FORBIDDEN, "유효하지 않는 토큰입니다")
        val tokenId: String = jwtTokenProvider.extractClaim(refreshToken) {
            it.get("tokenId", String::class.java)
        } ?: throw ApiException(FORBIDDEN, "유효하지 않는 토큰입니다")
        val memberId: String = jwtTokenProvider.extractUsername(refreshToken)
            ?: throw ApiException(FORBIDDEN, "유효하지 않는 토큰입니다")
        val rotationCounter: Int = jwtTokenProvider.extractClaim(refreshToken) {
            it.get("rotationCounter", Integer::class.java)
        }?.toInt() ?: throw ApiException(FORBIDDEN, "유효하지 않는 토큰입니다")

        if (type != TokenType.REFRESH_TOKEN.name.lowercase()) {
            throw ApiException(FORBIDDEN, "유효하지 않는 토큰입니다")
        }

        val findMember = memberRepository.findByIdOrNull(UUID.fromString(memberId))
            ?: throw ApiException(FORBIDDEN, "유효하지 않는 토큰입니다")
        val findToken = tokenRepository.findByIdIncludeMember(UUID.fromString(tokenId))
            ?: throw ApiException(FORBIDDEN, "유효하지 않는 토큰입니다")
        if (findToken.blocked) throw ApiException(FORBIDDEN, "유효하지 않는 토큰입니다")
        if (findToken.rotationCounter != rotationCounter) {
            findToken.block()
            throw ApiException(FORBIDDEN, "유효하지 않는 토큰입니다")
        }

        findToken.increaseRotationCounter()

        val refreshClaims = mapOf("rotationCounter" to findToken.rotationCounter, "tokenId" to tokenId)
        val accessToken = jwtTokenProvider.generateToken(findMember)
        val newRefreshToken = jwtTokenProvider.generateRefreshToken(refreshClaims, findMember)

        return AuthenticationTempDto(
            findMember.id,
            accessToken,
            newRefreshToken,
        )
    }

}