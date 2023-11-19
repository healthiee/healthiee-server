package healthiee.rest.domain.comment.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class UpdateCommentRequest(
    @field:NotNull(message = "The content is required")
    @JsonProperty("content")
    private val _content: String?,
) {
    val content: String
        get() = _content!!
}