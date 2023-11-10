package healthiee.rest.dto.common

import healthiee.rest.domain.common.MediaType
import healthiee.rest.domain.post.PostMedia

data class MediaDto(
    val id: Long,
    val type: MediaType,
    val url: String,
)

fun PostMedia.toDto() = MediaDto(id, type, url)