package healthiee.rest.lib.authority

import com.google.gson.GsonBuilder
import healthiee.rest.domain.common.dto.base.Response
import healthiee.rest.lib.gson.LocalDateTimeSerializer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime

@Component
class JwtAuthenticationFilter constructor(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {

    private val gsonBuilder by lazy {
        return@lazy GsonBuilder().apply {
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
        }
    }

    private val gson by lazy {
        return@lazy gsonBuilder.setPrettyPrinting().create()
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // whitelist
        if (request.servletPath.contains("/v1/auth") ||
            (request.servletPath.contains("/v1/members") && request.servletPath.contains("check"))
        ) {
            filterChain.doFilter(request, response)
            return
        }
        // authorization 없을시 401 에러 반환
        if (request.getHeader("Authorization") == null) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = "application/json; charset=utf-8;"
            response.characterEncoding = "utf-8"

            val errorResponse = Response<Any>(
                code = HttpStatus.UNAUTHORIZED.value(),
                data = null,
                message = "권한이 없습니다",
                timestamp = LocalDateTime.now(),
            )

            response.writer.write(gson.toJson(errorResponse))
            return
        }
        // authorization parsing
        val authHeader = request.getHeader("Authorization")
        val jwt = authHeader.substring(7)
        val memberId = jwtTokenProvider.extractUsername(jwt)
        if (memberId != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(memberId)
            if (jwtTokenProvider.isTokenValid(jwt, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities,
                ).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
        filterChain.doFilter(request, response)
    }

}