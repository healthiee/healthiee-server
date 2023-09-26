package healthiee.rest.lib.authority

import healthiee.rest.repository.MemberRepository
import healthiee.rest.repository.TokenRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityUserDetailsService(
    private val memberRepository: MemberRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return try {
            // access token
            val member = memberRepository.findByIdOrNull(UUID.fromString(username))
                ?: throw NoSuchElementException("멤버를 찾을 수 없습니다.")
            User(
                member.id.toString(),
                passwordEncoder.encode(""),
                member.memberRoles.map { SimpleGrantedAuthority("ROLE_${it.role}") },
            )
        } catch (e: NoSuchElementException) {
            // refresh token
            val findToken = tokenRepository.findByIdOrNull(UUID.fromString(username))
                ?: throw RuntimeException("Auth 토큰을 찾을 수 없습니다.")
            User(
                findToken.id.toString(),
                passwordEncoder.encode(""),
                listOf(),
            )
        } catch (e: IllegalArgumentException) {
            // register token
            User(
                username,
                passwordEncoder.encode(""),
                listOf(),
            )
        }
    }
}