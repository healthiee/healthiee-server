package healthiee.rest.dto.auth

data class TokenDto(
    val accessToken: String,
    val refreshToken: String,
)