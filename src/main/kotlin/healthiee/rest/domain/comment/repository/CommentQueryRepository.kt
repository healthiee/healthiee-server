package healthiee.rest.domain.comment.repository

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.comment.entity.PostComment
import healthiee.rest.domain.comment.entity.QPostComment.postComment
import healthiee.rest.domain.member.entity.QMember.member
import healthiee.rest.domain.post.entity.QPost.post
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CommentQueryRepository : QuerydslRepositorySupport(PostComment::class.java) {

    fun findById(commentId: Long): PostComment? {
        return selectFrom(postComment)
            .join(postComment.member, member)
            .fetchJoin()
            .join(postComment.post, post)
            .fetchJoin()
            .where(
                idEq(commentId),
                deletedEq(false)
            )
            .fetchOne()
    }

    fun findParentCommentsByPostId(postId: UUID): List<PostComment> {
        return selectFrom(postComment)
            .join(postComment.member, member)
            .fetchJoin()
            .where(
                postIdEq(postId),
                parenCommentId(null),
                deletedEq(false),
            )
            .orderBy(postComment.createdDate.desc())
            .fetch()
    }

    fun findAllByParentCommentId(commentId: Long): List<PostComment> {
        return selectFrom(postComment)
            .join(postComment.member, member)
            .fetchJoin()
            .where(
                parenCommentId(commentId),
                deletedEq(false),
            )
            .orderBy(postComment.createdDate.asc())
            .fetch()
    }

    private fun idEq(commentId: Long): BooleanExpression {
        return postComment.id.eq(commentId)
    }

    private fun postIdEq(postId: UUID): BooleanExpression {
        return postComment.post.id.eq(postId)
    }

    private fun parenCommentId(parentCommentId: Long? = null): BooleanExpression {
        return if (parentCommentId != null) postComment.parentComment.id.eq(parentCommentId)
        else postComment.parentComment.isNull
    }

    private fun deletedEq(deleted: Boolean): BooleanExpression {
        return postComment.deleted.eq(deleted)
    }

}