package healthiee.rest.api.auth.dto

import java.util.*

data class AuthenticationDto(
    val memberId: UUID,
    val tokens: TokenDto,
)