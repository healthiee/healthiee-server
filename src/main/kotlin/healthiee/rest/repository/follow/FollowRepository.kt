package healthiee.rest.repository.follow

import healthiee.rest.domain.follow.Follow
import healthiee.rest.domain.member.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowRepository : JpaRepository<Follow, Long>, FollowCustomRepository {
    fun countByMember(member: Member): Int // 팔로우 수
    fun countByTargetMember(member: Member): Int // 팔로워 수
}