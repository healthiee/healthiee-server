package healthiee.rest.api.member

import healthiee.rest.api.base.BaseResponse
import healthiee.rest.api.base.ServiceResponse
import healthiee.rest.api.base.toResponse
import healthiee.rest.api.member.dto.MemberDto
import healthiee.rest.domain.member.Member
import healthiee.rest.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.*

@RestController
class MemberApiController(
    @Autowired private val memberService: MemberService,
) {

    @GetMapping("/v1/members/info")
    fun getInfo(
        connectedUser: Principal,
    ): ResponseEntity<BaseResponse<MemberDto>> {
        val member = ((connectedUser as UsernamePasswordAuthenticationToken).principal as Member)
        return when (val response = memberService.getInfo(UUID.fromString(member.id.toString()))) {
            is ServiceResponse.Success -> BaseResponse.ok(response.date)
            is ServiceResponse.Failure -> response.type.toResponse()
        }
    }
}