package healthiee.rest.domain.workout.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class SearchConditionRequest(
    @JsonProperty("month")
    val month: Int?,
)