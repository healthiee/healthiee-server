package healthiee.rest.lib.authority

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val secretKey: String,
    @Value("\${jwt.access-token-expiration}")
    private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long,
) {

    fun extractUsername(token: String): String? = extractClaim(token, Claims::getSubject)

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver.invoke(claims)
    }

    fun generateToken(userDetails: UserDetails): String = generateToken(
        mapOf("type" to TokenType.ACCESS_TOKEN.name.lowercase()),
        userDetails,
    )

    fun generateToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
    ): String {
        val updatedClaims = extraClaims.toMutableMap().apply { put("type", TokenType.ACCESS_TOKEN.name.lowercase()) }
        return buildToken(updatedClaims, userDetails, accessTokenExpiration)
    }

    fun generateRefreshToken(userDetails: UserDetails): String = generateRefreshToken(
        mapOf("type" to TokenType.REFRESH_TOKEN.name.lowercase()),
        userDetails,
    )

    fun generateRefreshToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
    ): String {
        val updatedClaims = extraClaims.toMutableMap().apply { put("type", TokenType.REFRESH_TOKEN.name.lowercase()) }
        return buildToken(updatedClaims, userDetails, refreshTokenExpiration)
    }

    private fun buildToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
        expiration: Long,
    ): String = Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.username)
        .setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact()

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username) && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date =
        extractClaim(token, Claims::getExpiration)

    private fun extractAllClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .body

    private fun getSignInKey(): Key =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

}

enum class TokenType {
    ACCESS_TOKEN,
    REFRESH_TOKEN,
}