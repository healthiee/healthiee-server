package healthiee.rest.api.auth.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class RegisterRequest(
    @field:NotBlank(message = "The code is required")
    @JsonProperty("code")
    private val _code: String?,
    @field:NotBlank(message = "The name is required")
    @JsonProperty("name")
    private val _name: String?,
    @field:NotBlank(message = "The nickname is required")
    @JsonProperty("nickname")
    private val _nickname: String?,
    val bio: String?,
    val workouts: List<String>?,
) {
    val code: String
        get() = _code!!
    val name: String
        get() = _name!!
    val nickname: String
        get() = _nickname!!
}