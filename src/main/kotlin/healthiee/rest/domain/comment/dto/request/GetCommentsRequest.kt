package healthiee.rest.domain.comment.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import java.util.*

data class GetCommentsRequest(
    @field:NotNull(message = "The postId is required")
    @JsonProperty("postId")
    val postId: UUID,
)