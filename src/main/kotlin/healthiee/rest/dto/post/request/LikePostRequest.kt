package healthiee.rest.dto.post.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import java.util.*

data class LikePostRequest(
    @field:NotNull(message = "The post id must not be null")
    @JsonProperty("postId")
    private val _postId: UUID?,
) {
    val postId: UUID
        get() = _postId!!
}