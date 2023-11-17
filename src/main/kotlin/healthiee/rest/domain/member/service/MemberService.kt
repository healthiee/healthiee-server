package healthiee.rest.domain.member.service

import healthiee.rest.domain.follow.service.FollowService
import healthiee.rest.domain.member.dto.MemberDto
import healthiee.rest.domain.member.repository.MemberRepository
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
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
            memberRepository.findByIdOrNull(id) ?: throw ApiException(NOT_FOUND, "멤버 정보를 찾을 수 없습니다")

        return MemberDto.create(
            MemberDto.Params(
                findMember,
                followService.getFollowingCount(findMember),
                followService.getFollowerCount(findMember),
            )
        )
    }

}