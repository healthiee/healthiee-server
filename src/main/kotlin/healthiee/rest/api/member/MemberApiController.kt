package healthiee.rest.api.member

import healthiee.rest.api.base.BaseResponse
import healthiee.rest.api.base.ServiceResponse
import healthiee.rest.api.base.toResponse
import healthiee.rest.api.member.dto.MemberDto
import healthiee.rest.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class MemberApiController(
    @Autowired private val memberService: MemberService,
) {

    @GetMapping("/v1/members/{memberId}")
    fun getMember(
        @PathVariable("memberId") memberId: UUID,
    ): ResponseEntity<BaseResponse<MemberDto>> {
        return when (val response = memberService.getMember(memberId)) {
            is ServiceResponse.Success -> BaseResponse.ok(response.date)
            is ServiceResponse.Failure -> response.type.toResponse()
        }
    }
}