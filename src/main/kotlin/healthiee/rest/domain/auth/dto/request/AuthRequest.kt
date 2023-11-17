package healthiee.rest.domain.auth.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email

data class AuthRequest(
    @field:Email(message = "The value must be email format")
    @JsonProperty("email")
    private val _email: String?,
) {

    val email: String
        get() = _email!!

}