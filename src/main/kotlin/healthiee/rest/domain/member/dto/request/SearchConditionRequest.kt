package healthiee.rest.domain.member.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class SearchConditionRequest(
    @field:NotNull(message = "The nickname must not be null")
    @JsonProperty("nickname")
    val nickname: String?,
)