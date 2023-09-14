package healthiee.rest.lib.authority

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider {

    @Value("\${jwt.secret}")
    lateinit var secretKey: String

    private val key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    fun createAccessToken(authentication: Authentication): String =
        createToken(authentication, TokenType.ACCESS_TOKEN)

    fun createRefreshToken(authentication: Authentication, claims: MutableMap<String, Any>): String =
        createToken(authentication, TokenType.REFRESH_TOKEN, claims)

    fun createRegisterToken(authentication: Authentication, claims: MutableMap<String, Any>): String =
        createToken(authentication, TokenType.REGISTER_TOKEN, claims)

    private fun createToken(
        authentication: Authentication,
        type: TokenType,
        claims: MutableMap<String, Any> = mutableMapOf(),
    ): String {
        if (authentication.authorities.isNotEmpty()) {
            val authorities: String = authentication
                .authorities
                .joinToString(",", transform = GrantedAuthority::getAuthority)
            claims["auth"] = authorities
        }
        claims["type"] = type.name.lowercase()
        claims["id"] = authentication.name

        val now = Date()
        val tokenExpiration = Date(now.time + type.expiration)

        return Jwts.builder()
            .setSubject(authentication.name)
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(tokenExpiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = getClaims(token)
        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰입니다.")

        val authorities = (auth as String)
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun validateToken(token: String): Boolean {
        try {
            getClaims(token)
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {} // Invalid JWT Token
                is MalformedJwtException -> {} // Invalid JWT Token
                is ExpiredJwtException -> {} // Expired JWT Token
                is UnsupportedJwtException -> {} // Unsupported JWT Token
                is IllegalArgumentException -> {} // JWT claims string is empty
                else -> {} // else
            }
            println(e.message)
        }
        return false
    }

    fun getClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

    enum class TokenType(val expiration: Long) {
        ACCESS_TOKEN(1000 * 60 * 30), // 30분
        REFRESH_TOKEN(1000 * 60 * 60 * 24 * 14), // 2주
        REGISTER_TOKEN(1000 * 60 * 60), // 1시간
    }

}