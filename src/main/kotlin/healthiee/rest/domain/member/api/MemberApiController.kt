package healthiee.rest.domain.member.api

import healthiee.rest.domain.common.dto.base.Response
import healthiee.rest.domain.follow.service.FollowService
import healthiee.rest.domain.member.dto.MemberDto
import healthiee.rest.domain.member.dto.response.CheckMemberResponse
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.member.repository.MemberRepository
import healthiee.rest.domain.member.service.MemberService
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("v1/members")
class MemberApiController(
    private val memberService: MemberService,
    private val followService: FollowService,
    private val memberRepository: MemberRepository,
) {

    @GetMapping("{memberId}")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun getMember(
        @PathVariable("memberId") memberId: UUID,
    ): ResponseEntity<Response<MemberDto>> =
        ResponseEntity.ok(Response(code = HttpStatus.OK.value(), data = memberService.getMember(memberId)))

    @GetMapping("{nickname}/check")
    fun checkMember(
        @PathVariable("nickname") nickname: String,
    ): ResponseEntity<Response<CheckMemberResponse>> {
        val exist: Boolean = memberRepository.findByNickname(nickname) != null
        return ResponseEntity.ok(Response(code = HttpStatus.OK.value(), data = CheckMemberResponse(exist)))
    }

    @PostMapping("{memberId}/follow")
    @PreAuthorize("hasRole('MEMBER')")
    fun followMember(
        @AuthenticationPrincipal member: Member,
        @PathVariable("memberId") targetMemberId: UUID,
    ): ResponseEntity<Response<Any>> {
        val findMember = memberRepository.findByIdOrNull(targetMemberId)
            ?: throw ApiException(NOT_FOUND, "멤버 정보를 찾을 수 없습니다")

        followService.follow(member, findMember)

        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                message = "팔로우가 완료 되었습니다",
            )
        )
    }

    @DeleteMapping("{memberId}/follow")
    @PreAuthorize("hasRole('MEMBER')")
    fun unfollowMember(
        @AuthenticationPrincipal member: Member,
        @PathVariable("memberId") targetMemberId: UUID,
    ): ResponseEntity<Response<Any>> {
        val findMember = memberRepository.findByIdOrNull(targetMemberId)
            ?: throw ApiException(NOT_FOUND, "멤버 정보를 찾을 수 없습니다")

        followService.unfollow(member, findMember)

        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                message = "팔로우가 취소 되었습니다",
            )
        )
    }

}