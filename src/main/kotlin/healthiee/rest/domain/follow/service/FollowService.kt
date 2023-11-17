package healthiee.rest.domain.follow.service

import healthiee.rest.domain.follow.entity.Follow
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ApplicationErrorCode.BAD_REQUEST_ALREADY_EXIST_FOLLOW
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_FOLLOW
import healthiee.rest.domain.follow.repository.FollowRepository
import healthiee.rest.domain.follow.repository.FollowQueryRepository
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
        findFollow?.let { throw ApiException(BAD_REQUEST_ALREADY_EXIST_FOLLOW) }

        followRepository.save(Follow.createFollow(member, targetMember))
    }

    @Transactional
    fun unfollow(member: Member, targetMember: Member) {
        val findFollow = queryRepository.findByMember(member.id, targetMember.id)
            ?: throw ApiException(NOT_FOUND_FOLLOW)

        findFollow.delete()
    }

    fun getFollowingCount(member: Member): Int = followRepository.countByMember(member)

    fun getFollowerCount(member: Member): Int = followRepository.countByTargetMember(member)

}