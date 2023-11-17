package healthiee.rest.domain.follow.service

import healthiee.rest.domain.follow.entity.Follow
import healthiee.rest.domain.follow.repository.FollowQueryRepository
import healthiee.rest.domain.follow.repository.FollowRepository
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.BAD_REQUEST
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FollowService(
    private val followRepository: FollowRepository,
    private val queryRepository: FollowQueryRepository,
) {

    @Transactional
    fun follow(member: Member, targetMember: Member) {
        val findFollow = queryRepository.findByMember(member.id, targetMember.id)
        findFollow?.let { throw ApiException(BAD_REQUEST, "이미 팔로우를 한 이력이 있습니다") }

        followRepository.save(Follow.createFollow(member, targetMember))
    }

    @Transactional
    fun unfollow(member: Member, targetMember: Member) {
        val findFollow = queryRepository.findByMember(member.id, targetMember.id)
            ?: throw ApiException(NOT_FOUND, "팔로우한 이력이 없습니다")

        findFollow.delete()
    }

    fun getFollowingCount(member: Member): Int = followRepository.countByMember(member)

    fun getFollowerCount(member: Member): Int = followRepository.countByTargetMember(member)

}