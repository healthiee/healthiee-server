package healthiee.rest.domain.follow.repository

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.follow.entity.Follow
import healthiee.rest.domain.follow.entity.QFollow.follow
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class FollowQueryRepository : QuerydslRepositorySupport(Follow::class.java) {

    fun findByMember(memberId: UUID, targetMemberId: UUID): Follow? =
        selectFrom(follow)
            .where(
                memberIdEq(memberId),
                targetMemberIdEq(targetMemberId),
                deletedEq(false),
            )
            .fetchOne()

    fun findTargetMembersByMember(memberId: UUID): List<Member> {
        return select(follow.targetMember)
            .from(follow)
            .where(
                memberIdEq(memberId),
                deletedEq(false),
            )
            .fetch()
    }

    private fun memberIdEq(id: UUID): BooleanExpression =
        follow.member.id.eq(id)

    private fun memberNicknameEq(nickname: String): BooleanExpression =
        follow.member.nickname.eq(nickname)

    private fun targetMemberIdEq(targetMemberId: UUID): BooleanExpression =
        follow.targetMember.id.eq(targetMemberId)

    private fun deletedEq(deleted: Boolean): BooleanExpression = follow.deleted.eq(deleted)

}