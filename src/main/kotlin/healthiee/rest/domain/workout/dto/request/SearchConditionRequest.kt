package healthiee.rest.domain.workout.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.util.*

data class SearchConditionRequest(
    @JsonProperty("date")
    @DateTimeFormat(pattern = "yyyy-MM")
    val date: LocalDate?,
)