package healthiee.rest.config

import healthiee.rest.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*


@Configuration
class ApplicationConfig(
    @Autowired private val memberRepository: MemberRepository,
) {

    @Bean
    fun userDetailsService(): UserDetailsService =
        UserDetailsService { userName: String? ->
            userName ?: throw UsernameNotFoundException("Security userName cannot be null")
            return@UserDetailsService memberRepository.findByIdOrNull(UUID.fromString(userName))
                ?: throw UsernameNotFoundException("Security User not found")
        }

}
