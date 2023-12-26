package healthiee.rest.domain.member.api

import healthiee.rest.domain.common.dto.base.Response
import healthiee.rest.domain.follow.service.FollowService
import healthiee.rest.domain.member.dto.MemberDefaultDto
import healthiee.rest.domain.member.dto.MemberDto
import healthiee.rest.domain.member.dto.MemberSearchCondition
import healthiee.rest.domain.member.dto.request.SearchConditionRequest
import healthiee.rest.domain.member.dto.request.UpdateMemberRequest
import healthiee.rest.domain.member.dto.response.CheckMemberResponse
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.member.repository.MemberRepository
import healthiee.rest.domain.member.service.MemberService
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("v1/members")
class MemberApiController(
    private val memberService: MemberService,
    private val followService: FollowService,
    private val memberRepository: MemberRepository,
) {

    @GetMapping("")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun getMembers(
        @Validated request: SearchConditionRequest,
        pageable: Pageable,
    ): ResponseEntity<Response<Page<MemberDefaultDto>>> {
        val searchCondition = MemberSearchCondition(request.nickname!!)
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = memberService.getMembers(pageable, searchCondition)
            )
        )
    }

    @GetMapping("{memberId}")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun getMember(
        @PathVariable("memberId") memberId: UUID,
    ): ResponseEntity<Response<MemberDto>> =
        ResponseEntity.ok(Response(code = HttpStatus.OK.value(), data = memberService.getMember(memberId)))

    @DeleteMapping("")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun dropMember(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        memberService.drop(member)
        return ResponseEntity.ok()
            .headers { it.set("Set-Cookie", createEmptyCookie().toString()) }
            .body(
                Response(
                    code = HttpStatus.OK.value(),
                    message = "회원 탈퇴가 완료되었습니다"
                )
            )
    }

    @PutMapping(
        value = ["{memberId}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun updateMember(
        @PathVariable("memberId") memberId: UUID,
        @Validated @RequestPart("data") request: UpdateMemberRequest,
        @RequestPart("image") image: MultipartFile?,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        memberService.updateMember(memberId, request, image, member)
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                message = "멤버 수정이 완료되었습니다",
            )
        )
    }

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

    private fun createEmptyCookie(): ResponseCookie {
        return ResponseCookie.from(REFRESH_TOKEN_KEY)
            .path("/")
//            .secure(true) // https
            .sameSite("None")
            .httpOnly(true)
            .build()
    }

    companion object {
        private const val REFRESH_TOKEN_KEY = "refreshToken"
    }

}