package healthiee.rest.dto.post

import healthiee.rest.domain.post.Post
import healthiee.rest.dto.common.MediaDto
import healthiee.rest.dto.common.toDto
import healthiee.rest.dto.member.MemberSummaryDto
import healthiee.rest.dto.member.toSummaryDto
import java.time.LocalDateTime
import java.util.*

data class PostSummaryDto(
    val postId: UUID,
    val category: PostCategoryDto?,
    val content: String,
    val medias: List<MediaDto>,
    val member: MemberSummaryDto,
    val likeCount: Int,
    val commentCount: Int,
    val createdDate: LocalDateTime,
)

fun Post.toSummaryDto() = PostSummaryDto(
    this.id,
    this.category?.toCategoryDto(),
    this.content,
    this.medias.map { it.toDto() },
    this.member.toSummaryDto(),
    1,
    3,
    this.createdDate,
)