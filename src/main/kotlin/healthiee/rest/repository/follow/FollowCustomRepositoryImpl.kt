package healthiee.rest.repository.follow

import com.querydsl.jpa.impl.JPAQueryFactory
import healthiee.rest.domain.follow.Follow
import healthiee.rest.domain.follow.QFollow.follow
import healthiee.rest.domain.member.Member
import java.util.*

class FollowCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : FollowCustomRepository {

    override fun findByMember(member: Member, targetMember: Member): Follow? {
        return queryFactory.selectFrom(follow)
            .where(
                follow.member.eq(member),
                follow.targetMember.eq(targetMember),
                follow.deleted.eq(false)
            )
            .fetchOne()

    }
}