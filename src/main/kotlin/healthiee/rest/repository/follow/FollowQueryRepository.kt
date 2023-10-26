package healthiee.rest.repository.follow

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.follow.Follow
import healthiee.rest.domain.follow.QFollow.follow
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class FollowQueryRepository : QuerydslRepositorySupport(Follow::class.java) {

    fun findByMember(memberNickname: String, targetMemberNickname: String): Follow? =
        selectFrom(follow)
            .where(
                memberNicknameEq(memberNickname),
                targetMemberNicknameEq(targetMemberNickname),
                deletedEq(false),
            )
            .fetchOne()

    private fun memberNicknameEq(nickname: String): BooleanExpression =
        follow.member.nickname.eq(nickname)

    private fun targetMemberNicknameEq(targetNickname: String): BooleanExpression =
        follow.targetMember.nickname.eq(targetNickname)

    private fun deletedEq(deleted: Boolean): BooleanExpression = follow.deleted.eq(deleted)

}