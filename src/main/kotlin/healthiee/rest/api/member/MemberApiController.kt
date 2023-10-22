package healthiee.rest.api.member

import healthiee.rest.api.member.dto.MemberDto
import healthiee.rest.api.member.dto.response.CheckMemberResponse
import healthiee.rest.domain.member.Member
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_MEMBER
import healthiee.rest.lib.response.BaseResponse
import healthiee.rest.repository.member.MemberRepository
import healthiee.rest.service.FollowService
import healthiee.rest.service.MemberService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class MemberApiController(
    private val memberService: MemberService,
    private val followService: FollowService,
    private val memberRepository: MemberRepository,
) {

    @GetMapping("/v1/members/{memberId}")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun getMember(
        @PathVariable("memberId") memberId: UUID,
    ): ResponseEntity<BaseResponse<MemberDto>> =
        ResponseEntity.ok(BaseResponse(code = HttpStatus.OK.value(), data = memberService.getMember(memberId)))

    @GetMapping("/v1/members/{nickname}/check")
    fun checkMember(
        @PathVariable("nickname") nickname: String,
    ): ResponseEntity<BaseResponse<CheckMemberResponse>> {
        val exist: Boolean = memberRepository.findByNickname(nickname) != null
        return ResponseEntity.ok(BaseResponse(code = HttpStatus.OK.value(), data = CheckMemberResponse(exist)))
    }

    @PostMapping("/v1/members/{memberId}/follow")
    @PreAuthorize("hasRole('MEMBER')")
    fun followMember(
        @AuthenticationPrincipal member: Member,
        @PathVariable("memberId") targetMemberId: UUID,
    ): ResponseEntity<BaseResponse<Any>> {
        val findMember = memberRepository.findByIdOrNull(targetMemberId)
            ?: throw ApiException(NOT_FOUND_MEMBER)

        followService.follow(member, findMember)

        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                message = "팔로우가 완료 되었습니다",
            )
        )
    }

    @DeleteMapping("/v1/members/{memberId}/follow")
    @PreAuthorize("hasRole('MEMBER')")
    fun unfollowMember(
        @AuthenticationPrincipal member: Member,
        @PathVariable("memberId") targetMemberId: UUID,
    ): ResponseEntity<BaseResponse<Any>> {
        val findMember = memberRepository.findByIdOrNull(targetMemberId)
            ?: throw ApiException(NOT_FOUND_MEMBER)

        followService.unfollow(member, findMember)

        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                message = "팔로우가 취소 되었습니다",
            )
        )
    }

}