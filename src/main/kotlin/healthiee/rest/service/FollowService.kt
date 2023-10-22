package healthiee.rest.service

import healthiee.rest.domain.follow.Follow
import healthiee.rest.domain.member.Member
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ApplicationErrorCode.BAD_REQUEST_ALREADY_EXIST_FOLLOW
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_FOLLOW
import healthiee.rest.repository.follow.FollowRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FollowService(
    val followRepository: FollowRepository,
) {

    @Transactional
    fun follow(member: Member, targetMember: Member) {
        val findFollow = followRepository.findByMember(member, targetMember)
        findFollow?.let { throw ApiException(BAD_REQUEST_ALREADY_EXIST_FOLLOW) }

        followRepository.save(Follow.createFollow(member, targetMember))
    }

    @Transactional
    fun unfollow(member: Member, targetMember: Member) {
        val findFollow = followRepository.findByMember(member, targetMember)
            ?: throw ApiException(NOT_FOUND_FOLLOW)

        findFollow.delete()
    }

    fun getFollowingCount(member: Member): Int = followRepository.countByMember(member)

    fun getFollowerCount(member: Member): Int = followRepository.countByTargetMember(member)

}