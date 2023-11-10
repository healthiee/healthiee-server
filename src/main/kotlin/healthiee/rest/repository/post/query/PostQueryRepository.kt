package healthiee.rest.repository.post.query

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.post.Post
import healthiee.rest.domain.post.QPost.post
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PostQueryRepository(
    private val postMediaQueryRepository: PostMediaQueryRepository,
) : QuerydslRepositorySupport(Post::class.java) {

    fun findAll(): List<Post> {
        val findPost = selectFrom(post)
            .leftJoin(post.category)
            .fetchJoin()
            .leftJoin(post.location)
            .fetchJoin()
            .join(post.member)
            .fetchJoin()
            .fetch()

        findPost.forEach { it.updateMedias(postMediaQueryRepository.findByPostId(it.id)) }

        return findPost
    }

    private fun idEq(id: UUID): BooleanExpression = post.id.eq(id)

}