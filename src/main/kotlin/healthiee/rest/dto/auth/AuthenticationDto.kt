package healthiee.rest.dto.auth

import java.util.*

data class AuthenticationDto(
    val memberId: UUID,
    val tokens: TokenDto,
)