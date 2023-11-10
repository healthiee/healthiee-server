package healthiee.rest.api

import healthiee.rest.domain.member.Member
import healthiee.rest.dto.post.PostSummaryDto
import healthiee.rest.dto.post.request.SavePostRequest
import healthiee.rest.lib.response.BaseResponse
import healthiee.rest.service.PostService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("v1/posts")
class PostApiController(
    private val postService: PostService,
) {

    @PostMapping(
        value = [""],
        consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    @PreAuthorize("hasRole('MEMBER')")
    fun save(
        @Valid @RequestPart("data") request: SavePostRequest,
        @RequestPart("images") images: List<MultipartFile>,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<BaseResponse<Any>> {
        postService.save(request, images, member)
        return ResponseEntity.ok(BaseResponse(code = HttpStatus.OK.value(), message = "포스트 등록이 완료되었습니다"))
    }

    @GetMapping("")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun getMembers(): ResponseEntity<BaseResponse<List<PostSummaryDto>>> {
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                data = postService.findAll(),
            )
        )
    }

    @GetMapping("{postId}")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun getMember(
        @PathVariable("postId") postId: UUID,
    ): ResponseEntity<BaseResponse<Any>> {
        return ResponseEntity.ok(BaseResponse(code = HttpStatus.OK.value(), message = "포스트 조회"))
    }

}