package healthiee.rest.api.auth.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

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
    val profileUrl: String?,
    @field:NotNull(message = "The workouts cannot be null")
    @JsonProperty("workouts")
    private val _workouts: List<String>?,
) {
    val code: String
        get() = _code!!
    val name: String
        get() = _name!!
    val nickname: String
        get() = _nickname!!
    val workouts: List<String>
        get() = _workouts!!
}