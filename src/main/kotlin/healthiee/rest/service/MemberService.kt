package healthiee.rest.service

import healthiee.rest.dto.member.MemberDto
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_MEMBER
import healthiee.rest.repository.member.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class MemberService(
    val memberRepository: MemberRepository,
    val followService: FollowService,
) {

    fun getMember(id: UUID): MemberDto {
        val findMember =
            memberRepository.findByIdOrNull(id) ?: throw ApiException(NOT_FOUND_MEMBER)

        return MemberDto.create(
            MemberDto.Params(
                findMember,
                followService.getFollowingCount(findMember),
                followService.getFollowerCount(findMember),
            )
        )
    }

}