package healthiee.rest.domain.auth.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

data class AuthRequest(
    @field:Email(message = "The value must be email format")
    @field:NotNull(message = "The value must not be null")
    @JsonProperty("email")
    private val _email: String?,
) {

    val email: String
        get() = _email!!

}