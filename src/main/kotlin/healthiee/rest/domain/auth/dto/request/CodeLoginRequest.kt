package healthiee.rest.domain.auth.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import java.util.*

data class CodeLoginRequest(
    @field:NotNull(message = "The code is required")
    @JsonProperty("code")
    private val _code: UUID?,
) {

    val code: UUID
        get() = _code!!

}
