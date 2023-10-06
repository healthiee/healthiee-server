package healthiee.rest.service

import healthiee.rest.api.base.FailureType
import healthiee.rest.api.base.ServiceResponse
import healthiee.rest.api.member.dto.MemberDto
import healthiee.rest.repository.FollowRepository
import healthiee.rest.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class MemberService @Autowired constructor(
    val memberRepository: MemberRepository,
    val followRepository: FollowRepository,
) {

    fun getMember(id: UUID): ServiceResponse<MemberDto> {
        val findMember =
            memberRepository.findByIdOrNull(id) ?: return ServiceResponse.Failure(FailureType.NOT_FOUND_MEMBER)

        val followingCount = followRepository.countByMember(findMember)
        val followerCount = followRepository.countByTargetMember(findMember)

        return ServiceResponse.Success(MemberDto.create(MemberDto.Params(findMember, followingCount, followerCount)))
    }

}