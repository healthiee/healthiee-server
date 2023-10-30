package healthiee.rest.api.code.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import healthiee.rest.domain.code.Code
import jakarta.validation.constraints.NotNull

data class SaveCodeRequest(
    @field:NotNull(message = "The value must be codeType('POST_CATEGORY')")
    @JsonProperty("type")
    private val _type: Code.Type?,
    @field:NotNull(message = "The name value must not be null")
    @JsonProperty("name")
    private val _name: String?,
) {

    val type: Code.Type
        get() = _type!!

    val name: String
        get() = _name!!

}