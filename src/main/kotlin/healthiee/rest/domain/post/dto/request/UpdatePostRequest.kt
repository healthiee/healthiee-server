package healthiee.rest.domain.post.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class UpdatePostRequest(
    @field:NotNull(message = "The content value must not be null")
    @JsonProperty("content")
    private val _content: String?,
    @field:NotNull(message = "The medias value must not be null")
    @field:NotEmpty(message = "The medias value must not be empty")
    @JsonProperty("mediaIds")
    private val _mediaIds: List<Long>?,
    val categoryId: Long?,
    val location: PostLocationRequest?,
    val hashtags: List<String>?,
) {
    val content: String
        get() = _content!!

    val mediaIds: List<Long>
        get() = _mediaIds!!

}