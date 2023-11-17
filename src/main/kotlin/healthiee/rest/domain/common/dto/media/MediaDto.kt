package healthiee.rest.domain.common.dto.media

import healthiee.rest.domain.common.entity.media.MediaType
import healthiee.rest.domain.post.entity.PostMedia

data class MediaDto(
    val id: Long,
    val type: MediaType,
    val url: String,
)

fun PostMedia.toDto() = MediaDto(id, type, url)