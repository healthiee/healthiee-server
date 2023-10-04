package healthiee.rest.service

import healthiee.rest.api.auth.dto.AuthenticationDto
import healthiee.rest.api.auth.dto.TokenDto
import healthiee.rest.api.auth.dto.request.AuthRequest
import healthiee.rest.api.auth.dto.request.CodeLoginRequest
import healthiee.rest.api.auth.dto.request.RefreshTokenRequest
import healthiee.rest.api.auth.dto.request.RegisterRequest
import healthiee.rest.api.auth.dto.response.AuthResponse
import healthiee.rest.api.auth.dto.response.VerifyCodeResponse
import healthiee.rest.api.base.FailureType
import healthiee.rest.api.base.ServiceResponse
import healthiee.rest.domain.EmailAuth
import healthiee.rest.domain.Token
import healthiee.rest.domain.member.Member
import healthiee.rest.lib.authority.JwtTokenProvider
import healthiee.rest.lib.authority.TokenType
import healthiee.rest.lib.mail.MailSender
import healthiee.rest.repository.EmailAuthRepository
import healthiee.rest.repository.MemberRepository
import healthiee.rest.repository.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional(readOnly = true)
class AuthService(
    @Autowired private val emailAuthRepository: EmailAuthRepository,
    @Autowired private val memberRepository: MemberRepository,
    @Autowired private val tokenRepository: TokenRepository,
    @Autowired private val mailSender: MailSender,
    @Autowired private val jwtTokenProvider: JwtTokenProvider,
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
    fun codeLogin(request: CodeLoginRequest): ServiceResponse<AuthenticationDto> {
        val findEmailAuth = emailAuthRepository.findByCode(request.code)
        findEmailAuth ?: return ServiceResponse.Failure(FailureType.NOT_FOUND_CODE)
        if (findEmailAuth.disabled) return ServiceResponse.Failure(FailureType.NOT_FOUND_CODE)

        val diff = Duration.between(findEmailAuth.createdDate, LocalDateTime.now())
        if (diff.toHours() >= 24) return ServiceResponse.Failure(FailureType.NOT_FOUND_CODE)

        val findMember = memberRepository.findByEmail(findEmailAuth.email)
        findMember ?: return ServiceResponse.Failure(FailureType.NOT_FOUND_MEMBER)

        val token = Token.createToken(findMember)
        tokenRepository.save(token)

        val refreshClaims = mapOf("rotationCounter" to 1, "tokenId" to token.id)
        val accessToken = jwtTokenProvider.generateToken(findMember)
        val refreshToken = jwtTokenProvider.generateRefreshToken(refreshClaims, findMember)

        findEmailAuth.used()

        return ServiceResponse.Success(
            AuthenticationDto(
                memberId = findMember.id,
                tokens = TokenDto(accessToken, refreshToken)
            )
        )
    }

    fun verifyCode(code: UUID): ServiceResponse<VerifyCodeResponse> {
        val findEmailAuth = emailAuthRepository.findByCode(code)
        findEmailAuth ?: return ServiceResponse.Failure(FailureType.NOT_FOUND_CODE)
        if (findEmailAuth.disabled) return ServiceResponse.Failure(FailureType.NOT_FOUND_CODE)

        val diff = Duration.between(findEmailAuth.createdDate, LocalDateTime.now())
        if (diff.toHours() >= 24) return ServiceResponse.Failure(FailureType.NOT_FOUND_CODE)

        return ServiceResponse.Success(VerifyCodeResponse(findEmailAuth.email))
    }

    @Transactional
    fun register(request: RegisterRequest): ServiceResponse<AuthenticationDto> {
        val findEmailAuth = emailAuthRepository.findByCode(UUID.fromString(request.code))
        findEmailAuth ?: return ServiceResponse.Failure(FailureType.NOT_FOUND_CODE)
        if (findEmailAuth.disabled) return ServiceResponse.Failure(FailureType.NOT_FOUND_CODE)

        val diff = Duration.between(findEmailAuth.createdDate, LocalDateTime.now())
        if (diff.toHours() >= 24) return ServiceResponse.Failure(FailureType.NOT_FOUND_CODE)

        val member = Member.createMember(
            Member.MemberParam(
                email = findEmailAuth.email,
                name = request.name,
                nickname = request.nickname,
                bio = request.bio,
                profileUrl = request.profileUrl,
                workouts = request.workouts,
            )
        )
        memberRepository.save(member)

        val token = Token.createToken(member)
        tokenRepository.save(token)

        val refreshClaims = mapOf("rotationCounter" to 1, "tokenId" to token.id)
        val accessToken = jwtTokenProvider.generateToken(member)
        val refreshToken = jwtTokenProvider.generateRefreshToken(refreshClaims, member)

        findEmailAuth.used()

        return ServiceResponse.Success(
            AuthenticationDto(
                memberId = member.id,
                tokens = TokenDto(accessToken, refreshToken)
            )
        )
    }

    @Transactional
    fun refreshToken(request: RefreshTokenRequest): ServiceResponse<AuthenticationDto> {
        val type: String = jwtTokenProvider.extractClaim(request.refreshToken) {
            it.get("type", String::class.java)
        } ?: return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        val tokenId: String = jwtTokenProvider.extractClaim(request.refreshToken) {
            it.get("tokenId", String::class.java)
        } ?: return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        val memberId: String = jwtTokenProvider.extractUsername(request.refreshToken) ?: return ServiceResponse.Failure(
            FailureType.INVALID_REFRESH_TOKEN
        )
        val rotationCounter: Int = jwtTokenProvider.extractClaim(request.refreshToken) {
            it.get("rotationCounter", Integer::class.java)
        }?.toInt() ?: return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)

        if (type != TokenType.REFRESH_TOKEN.name.lowercase()) {
            return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        }

        val findMember = memberRepository.findByIdOrNull(UUID.fromString(memberId))
            ?: return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        val findToken = tokenRepository.findByIdIncludeMember(UUID.fromString(tokenId))
            ?: return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        if (findToken.blocked) return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        if (findToken.rotationCounter != rotationCounter) {
            findToken.block()
            return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        }

        findToken.increaseRotationCounter()

        val refreshClaims = mapOf("rotationCounter" to findToken.rotationCounter, "tokenId" to tokenId)
        val accessToken = jwtTokenProvider.generateToken(findMember)
        val refreshToken = jwtTokenProvider.generateRefreshToken(refreshClaims, findMember)

        return ServiceResponse.Success(
            AuthenticationDto(
                findMember.id,
                TokenDto(accessToken, refreshToken)
            )
        )
    }

}