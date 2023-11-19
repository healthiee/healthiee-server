package healthiee.rest.domain.comment.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateCommentRequest(
    @field:NotNull(message = "The postId is required")
    @JsonProperty("postId")
    private val _postId: UUID?,
    @field:NotNull(message = "The content is required")
    @JsonProperty("content")
    private val _content: String?,
    @JsonProperty("parentCommentId")
    val parentCommentId: Long? = null,
) {
    val postId: UUID
        get() = _postId!!

    val content: String
        get() = _content!!

}