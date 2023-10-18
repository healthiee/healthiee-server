package healthiee.rest.api.member

import healthiee.rest.api.member.dto.MemberDto
import healthiee.rest.lib.response.BaseResponse
import healthiee.rest.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
class MemberApiController(
    private val memberService: MemberService,
) {

    @GetMapping("/v1/members/{memberId}")
    fun getMember(
        @PathVariable("memberId") memberId: UUID,
    ): ResponseEntity<BaseResponse<MemberDto>> =
        ResponseEntity.ok(BaseResponse(code = HttpStatus.OK.value(), data = memberService.getMember(memberId)))
}