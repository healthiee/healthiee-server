package healthiee.rest.repository.post.query

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.post.PostLike
import healthiee.rest.domain.post.QPostLike.postLike
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PostLikeQueryRepository : QuerydslRepositorySupport(PostLike::class.java) {

    fun findByMemberAndPost(memberId: UUID, postId: UUID): PostLike? =
        selectFrom(postLike)
            .where(
                memberIdEq(memberId),
                postIdEq(postId),
                deletedEq(false),
            )
            .fetchOne()

    fun findByPostId(id: UUID): List<PostLike> {
        return selectFrom(postLike)
            .where(
                postIdEq(id),
                deletedEq(false),
            )
            .fetch()
    }

    private fun memberIdEq(id: UUID): BooleanExpression =
        postLike.member.id.eq(id)

    private fun postIdEq(postId: UUID): BooleanExpression =
        postLike.post.id.eq(postId)

    private fun deletedEq(deleted: Boolean): BooleanExpression = postLike.deleted.eq(deleted)

}