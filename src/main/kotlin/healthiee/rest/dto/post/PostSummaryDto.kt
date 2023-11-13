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
    val location: PostLocationDto?,
    val likeCount: Int,
    val commentCount: Int,
    val liked: Boolean,
    val createdDate: LocalDateTime,
) {

    companion object {
        fun create(
            post: Post,
            liked: Boolean,
        ) = PostSummaryDto(
            post.id,
            post.category?.toCategoryDto(),
            post.content,
            post.medias.map { it.toDto() },
            post.member.toSummaryDto(),
            post.location?.toDto(),
            post.likeCount,
            post.commentCount,
            liked,
            post.createdDate,
        )
    }

}