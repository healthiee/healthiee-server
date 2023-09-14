package healthiee.rest.api.auth.dto

data class TokenDto(
    val accessToken: String,
    val refreshToken: String,
)