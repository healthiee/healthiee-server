package healthiee.rest.dto.auth

import java.util.*

data class AuthenticationDto(
    val memberId: UUID,
    val token: String,
)

data class AuthenticationTempDto(
    val memberId: UUID,
    val token: String,
    val refreshToken: String,
)
