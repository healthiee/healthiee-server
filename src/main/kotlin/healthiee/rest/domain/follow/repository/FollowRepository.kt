package healthiee.rest.domain.follow.repository

import healthiee.rest.domain.follow.entity.Follow
import healthiee.rest.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowRepository : JpaRepository<Follow, Long> {
    fun countByMember(member: Member): Int // 팔로우 수
    fun countByTargetMember(member: Member): Int // 팔로워 수
}