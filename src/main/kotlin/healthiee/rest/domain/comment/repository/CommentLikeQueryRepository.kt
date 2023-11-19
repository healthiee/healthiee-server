package healthiee.rest.domain.comment.repository

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.comment.entity.PostCommentLike
import healthiee.rest.domain.comment.entity.QPostCommentLike.postCommentLike
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CommentLikeQueryRepository : QuerydslRepositorySupport(PostCommentLike::class.java) {
    fun findByMemberAndComment(memberId: UUID, commentId: Long): PostCommentLike? =
        selectFrom(postCommentLike)
            .where(
                memberIdEq(memberId),
                commentIdEq(commentId),
                deletedEq(false),
            )
            .fetchOne()

    private fun memberIdEq(id: UUID): BooleanExpression =
        postCommentLike.member.id.eq(id)

    private fun commentIdEq(commentId: Long): BooleanExpression =
        postCommentLike.postComment.id.eq(commentId)

    private fun deletedEq(deleted: Boolean): BooleanExpression = postCommentLike.deleted.eq(deleted)
}