package healthiee.rest.config

import healthiee.rest.lib.authority.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationProvider: AuthenticationProvider,
    @Value("\${spring.profiles.active}")
    private val profile: String,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val requestMatchers = mutableListOf<RequestMatcher>(
            AntPathRequestMatcher("/v1/auth/**"),
            AntPathRequestMatcher("/v1/members/**/check"),
        )
        if (profile == "local") {
            requestMatchers.add(toH2Console())
        }
        http
            .csrf { it.disable() }
            .cors {
                it.configurationSource {
                    return@configurationSource CorsConfiguration().apply {
                        addAllowedOriginPattern("*")
                        addAllowedHeader("*")
                        addAllowedMethod("*")
                        allowCredentials = true
                    }
                }
            }
            .headers { it.frameOptions { it.disable() } }
            .authorizeHttpRequests {
                it.requestMatchers(*requestMatchers.toTypedArray())
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
//            .logout {
//                it.logoutUrl("/api/v1/auth/logout")
//                    .addLogoutHandler(logoutHandler)
//                    .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
//            }


        return http.build()
    }

}