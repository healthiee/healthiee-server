package healthiee.rest.domain.post.dto

import healthiee.rest.domain.common.dto.media.MediaDto
import healthiee.rest.domain.common.dto.media.toDto
import healthiee.rest.domain.member.dto.MemberSummaryDto
import healthiee.rest.domain.member.dto.toSummaryDto
import healthiee.rest.domain.post.entity.Post
import java.time.LocalDateTime
import java.util.*

abstract class PostBaseDto {
    abstract val postId: UUID
}

data class PostDefaultDto(
    override val postId: UUID,
) : PostBaseDto()


data class PostDto(
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
        ) = PostDto(
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