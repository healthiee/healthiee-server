package healthiee.rest.domain.post.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import java.util.*

data class SavePostRequest(
    @field:NotNull(message = "The content value must not be null")
    @JsonProperty("content")
    private val _content: String?,
    val categoryId: Long?,
    val location: PostLocationRequest?,
    val hashtags: List<String>?,
) {
    val content: String
        get() = _content!!

}