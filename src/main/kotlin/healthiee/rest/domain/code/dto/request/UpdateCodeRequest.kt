package healthiee.rest.domain.code.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class UpdateCodeRequest(
    @field:NotNull(message = "The name value must not be null")
    @JsonProperty("name")
    private val _name: String?,
    @field:NotNull(message = "The active value must not be null")
    @JsonProperty("active")
    private val _active: Boolean?,
) {
    val name: String
        get() = _name!!

    val active: Boolean
        get() = _active!!

}