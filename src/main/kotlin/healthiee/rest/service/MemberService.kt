package healthiee.rest.service

import healthiee.rest.api.member.dto.MemberDto
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_MEMBER
import healthiee.rest.repository.FollowRepository
import healthiee.rest.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasRole('MEMBER')")
class MemberService @Autowired constructor(
    val memberRepository: MemberRepository,
    val followRepository: FollowRepository,
) {

    fun getMember(id: UUID): MemberDto {
        val findMember =
            memberRepository.findByIdOrNull(id) ?: throw ApiException(NOT_FOUND_MEMBER)

        val followingCount = followRepository.countByMember(findMember)
        val followerCount = followRepository.countByTargetMember(findMember)

        return MemberDto.create(MemberDto.Params(findMember, followingCount, followerCount))
    }

}