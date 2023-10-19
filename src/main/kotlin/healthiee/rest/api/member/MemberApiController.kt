package healthiee.rest.api.member

import healthiee.rest.api.member.dto.MemberDto
import healthiee.rest.api.member.dto.response.CheckMemberResponse
import healthiee.rest.lib.response.BaseResponse
import healthiee.rest.repository.member.MemberRepository
import healthiee.rest.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class MemberApiController(
    private val memberService: MemberService,
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

}