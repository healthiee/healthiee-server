package healthiee.rest.domain.member.api

import healthiee.rest.domain.common.dto.base.Response
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.member.repository.MemberRepository
import healthiee.rest.domain.member.service.MemberService
import healthiee.rest.domain.post.dto.PostDto
import healthiee.rest.domain.post.dto.PostSearchCondition
import healthiee.rest.domain.post.service.PostService
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("v1/members/{memberId}/posts")
class MemberPostApiController(
    private val memberService: MemberService,
    private val postService: PostService,
    private val memberRepository: MemberRepository,
) {

    @GetMapping("")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun getMemberPosts(
        pageable: Pageable,
        @PathVariable("memberId") memberId: UUID,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Page<PostDto>>> {
        val findMember =
            memberRepository.findByIdOrNull(memberId) ?: throw ApiException(ErrorCode.NOT_FOUND, "멤버 정보를 찾을 수 없습니다")
        memberService.getMember(memberId)
        val searchCondition = PostSearchCondition(
            members = listOf(findMember),
        )

        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = postService.getPosts(pageable, searchCondition, member),
            )
        )

    }

}