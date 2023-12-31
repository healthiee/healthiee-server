package healthiee.rest.domain.post.repository

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.code.entity.Code
import healthiee.rest.domain.hashtag.entity.Hashtag
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.post.dto.PostSearchCondition
import healthiee.rest.domain.post.entity.Post
import healthiee.rest.domain.post.entity.QPost.post
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PostQueryRepository : QuerydslRepositorySupport(Post::class.java) {

    fun findByHashtag(pageable: Pageable, content: String, hashtag: Hashtag?): Page<Post> {
        return applyPagination(
            pageable,
            {
                it.selectFrom(post)
                    .where(
                        contentLike(content).or(hashtagEq(hashtag))
                    )
                    .offset(pageable.offset)
                    .limit(pageable.pageSize.toLong())
                    .orderBy(post.createdDate.desc())
            }, {
                it.select(post.count())
                    .from(post)
                    .where(
                        contentLike(content).or(hashtagEq(hashtag))
                    )
            }
        )
    }

    fun findAll(
        pageable: Pageable,
        searchCondition: PostSearchCondition,
    ): Page<Post> {
        return applyPagination(
            pageable,
            {
                it.selectFrom(post)
                    .leftJoin(post.category)
                    .fetchJoin()
                    .leftJoin(post.location)
                    .fetchJoin()
                    .join(post.member)
                    .fetchJoin()
                    .where(
                        membersIn(searchCondition.members),
                        categoriesIn(searchCondition.categories),
                        deletedEq(false),
                    )
                    .offset(pageable.offset)
                    .limit(pageable.pageSize.toLong())
                    .orderBy(post.createdDate.desc())
            }, {
                it.select(post.count())
                    .from(post)
                    .where(
                        membersIn(searchCondition.members),
                        categoriesIn(searchCondition.categories),
                        deletedEq(false),
                    )
            }
        )
    }

    fun findById(postId: UUID): Post? {
        return selectFrom(post)
            .leftJoin(post.category)
            .fetchJoin()
            .leftJoin(post.location)
            .fetchJoin()
            .join(post.member)
            .fetchJoin()
            .where(
                idEq(postId),
                deletedEq(false)
            )
            .fetchOne()
    }

    private fun idEq(id: UUID): BooleanExpression = post.id.eq(id)

    private fun membersIn(members: List<Member>): BooleanExpression? =
        if (members.isEmpty()) null
        else post.member.`in`(members)

    private fun categoriesIn(categories: List<Code>): BooleanExpression? =
        if (categories.isEmpty()) null
        else post.category.`in`(categories)

    private fun deletedEq(condition: Boolean): BooleanExpression = post.deleted.eq(condition)

    private fun contentLike(content: String): BooleanExpression =
        post.content.contains(content)

    private fun hashtagEq(hashtag: Hashtag?): BooleanExpression? =
        if (hashtag != null) post.postHashTags.contains(hashtag)
        else null

}