package healthiee.rest.domain.post.repository

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.post.entity.PostMedia
import healthiee.rest.domain.post.entity.QPostMedia.postMedia
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PostMediaQueryRepository : QuerydslRepositorySupport(PostMedia::class.java) {

    fun findByPostId(postId: UUID): List<PostMedia> {
        return selectFrom(postMedia)
            .where(
                postIdEq(postId),
                deletedEq(false)
            )
            .fetch()
    }

    private fun postIdEq(postId: UUID): BooleanExpression {
        return postMedia.post.id.eq(postId)
    }

    private fun deletedEq(deleted: Boolean): BooleanExpression {
        return if (deleted) postMedia.deleted.isTrue
        else postMedia.deleted.isFalse
    }

}