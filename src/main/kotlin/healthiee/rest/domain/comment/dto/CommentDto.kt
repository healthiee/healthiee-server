package healthiee.rest.domain.comment.dto

import healthiee.rest.domain.comment.entity.PostComment
import healthiee.rest.domain.member.dto.MemberDefaultDto
import healthiee.rest.domain.member.dto.toDefaultDto
import java.time.LocalDateTime

abstract class CommentBaseDto {
    abstract val commentId: Long
    abstract val content: String
}

data class CommentDto(
    override val commentId: Long,
    override val content: String,
    val member: MemberDefaultDto,
    val likeCount: Int,
    val liked: Boolean,
    val createdDate: LocalDateTime,
    val childCommentCount: Int,
    val childComments: List<RecommentDto>,
) : CommentBaseDto() {

    companion object {
        fun create(
            comment: PostComment,
            recomments: List<RecommentDto>,
            liked: Boolean,
        ) = CommentDto(
            comment.id,
            comment.content,
            comment.member.toDefaultDto(),
            comment.likeCount,
            liked,
            comment.createdDate,
            comment.childComments.size,
            recomments,
        )
    }
}

data class RecommentDto(
    override val commentId: Long,
    override val content: String,
    val member: MemberDefaultDto,
    val likeCount: Int,
    val liked: Boolean,
    val createdDate: LocalDateTime,
) : CommentBaseDto() {
    companion object {
        fun create(
            comment: PostComment,
            liked: Boolean,
        ) = RecommentDto(
            comment.id,
            comment.content,
            comment.member.toDefaultDto(),
            comment.likeCount,
            liked,
            comment.createdDate,
        )
    }
}
