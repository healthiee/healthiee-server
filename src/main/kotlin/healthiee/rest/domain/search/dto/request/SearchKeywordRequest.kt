package healthiee.rest.domain.search.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import java.util.*

data class SearchKeywordRequest(
    @field:NotNull(message = "The keyword must not be null")
    @JsonProperty("keyword")
    val keyword: String?,
)