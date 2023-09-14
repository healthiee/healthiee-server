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
import healthiee.rest.domain.Member
import healthiee.rest.domain.MemberRole
import healthiee.rest.domain.Token
import healthiee.rest.lib.authority.JwtTokenProvider
import healthiee.rest.lib.authority.JwtTokenProvider.TokenType
import healthiee.rest.lib.mail.MailSender
import healthiee.rest.repository.EmailAuthRepository
import healthiee.rest.repository.MemberRepository
import healthiee.rest.repository.MemberRoleRepository
import healthiee.rest.repository.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
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
    @Autowired private val memberRoleRepository: MemberRoleRepository,
    @Autowired private val tokenRepository: TokenRepository,
    @Autowired private val mailSender: MailSender,
    @Autowired private val authenticationManagerBuilder: AuthenticationManagerBuilder,
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

        val accessTokenAuth = authenticationManagerBuilder
            .`object`
            .authenticate(UsernamePasswordAuthenticationToken(findMember.id, ""))
        val accessToken = jwtTokenProvider.createAccessToken(accessTokenAuth)

        val token = Token.createToken(findMember)
        tokenRepository.save(token)

        val refreshTokenAuth = authenticationManagerBuilder
            .`object`
            .authenticate(UsernamePasswordAuthenticationToken(token.id, ""))
        val refreshToken = jwtTokenProvider.createRefreshToken(refreshTokenAuth, mutableMapOf("rotationCounter" to 1))

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

        val registerTokenAuth = authenticationManagerBuilder
            .`object`
            .authenticate(UsernamePasswordAuthenticationToken(findEmailAuth.email, ""))
        val claims = mutableMapOf<String, Any>("authId" to findEmailAuth.id)
        val registerToken = jwtTokenProvider.createRegisterToken(registerTokenAuth, claims)

        return ServiceResponse.Success(VerifyCodeResponse(findEmailAuth.email, registerToken))
    }

    @Transactional
    fun register(request: RegisterRequest): ServiceResponse<AuthenticationDto> {
        val claims = jwtTokenProvider.getClaims(request.registerToken)
        if (claims["type"] != TokenType.REGISTER_TOKEN.name.lowercase()) return ServiceResponse.Failure(FailureType.INVALID_REGISTER_TOKEN)
        val email: String =
            (claims["id"] as? String) ?: return ServiceResponse.Failure(FailureType.INVALID_REGISTER_TOKEN)
        val emailAuthId: Int =
            (claims["authId"] as? Int) ?: return ServiceResponse.Failure(FailureType.INVALID_REGISTER_TOKEN)
        val findEmailAuth =
            emailAuthRepository.findByIdOrNull(emailAuthId.toLong())
                ?: return ServiceResponse.Failure(FailureType.INVALID_REGISTER_TOKEN)

        val member = Member.createMember(
            Member.MemberParam(
                email,
                request.name,
                request.nickname,
                request.bio,
                request.profileUrl,
                request.workouts,
            )
        )
        memberRepository.save(member)

        val memberRole = MemberRole.createMemberRole(member)
        memberRoleRepository.save(memberRole)

        val accessTokenAuth = authenticationManagerBuilder
            .`object`
            .authenticate(UsernamePasswordAuthenticationToken(member.id, ""))
        val accessToken = jwtTokenProvider.createAccessToken(accessTokenAuth)

        val token = Token.createToken(member)
        tokenRepository.save(token)

        val refreshTokenAuth = authenticationManagerBuilder
            .`object`
            .authenticate(UsernamePasswordAuthenticationToken(token.id, ""))
        val refreshToken = jwtTokenProvider.createRefreshToken(refreshTokenAuth, mutableMapOf("rotationCounter" to 1))

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
        val claims = jwtTokenProvider.getClaims(request.refreshToken)
        if (claims["type"] != TokenType.REFRESH_TOKEN.name.lowercase()) return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        val tokenId: UUID =
            UUID.fromString(
                (claims["id"] as? String) ?: return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
            )
        val rotationCounter: Int =
            (claims["rotationCounter"] as? Int) ?: return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        val findToken = tokenRepository.findByIdIncludeMember(tokenId)
            ?: return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        if (findToken.blocked) return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        if (findToken.rotationCounter != rotationCounter) {
            findToken.block()
            return ServiceResponse.Failure(FailureType.INVALID_REFRESH_TOKEN)
        }

        findToken.increaseRotationCounter()

        val accessTokenAuth = authenticationManagerBuilder
            .`object`
            .authenticate(UsernamePasswordAuthenticationToken(findToken.member.id, ""))
        val accessToken = jwtTokenProvider.createAccessToken(accessTokenAuth)

        val refreshTokenAuth = authenticationManagerBuilder
            .`object`
            .authenticate(UsernamePasswordAuthenticationToken(findToken.id, ""))
        val refreshToken = jwtTokenProvider.createRefreshToken(
            refreshTokenAuth,
            mutableMapOf("rotationCounter" to findToken.rotationCounter)
        )

        return ServiceResponse.Success(
            AuthenticationDto(
                findToken.member.id,
                TokenDto(accessToken, refreshToken)
            )
        )
    }

}