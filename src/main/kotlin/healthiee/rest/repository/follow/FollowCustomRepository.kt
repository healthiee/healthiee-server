package healthiee.rest.repository.follow

import healthiee.rest.domain.follow.Follow
import healthiee.rest.domain.member.Member

interface FollowCustomRepository {

    fun findByMember(member: Member, targetMember: Member): Follow?

}