package healthiee.rest.domain.post.api

import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.post.dto.PostSummaryDto
import healthiee.rest.domain.post.dto.request.SavePostRequest
import healthiee.rest.domain.post.dto.request.SearchConditionRequest
import healthiee.rest.domain.post.dto.request.UpdatePostRequest
import healthiee.rest.domain.common.dto.base.Response
import healthiee.rest.domain.post.service.PostService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
    ): ResponseEntity<Response<Any>> {
        postService.save(request, images, member)
        return ResponseEntity.ok(Response(code = HttpStatus.OK.value(), message = "포스트 등록이 완료되었습니다"))
    }

    @PatchMapping("{postId}")
    @PreAuthorize("hasRole('MEMBER')")
    fun update(
        @PathVariable("postId") postId: UUID,
        @Valid @RequestBody request: UpdatePostRequest,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        postService.update(postId, request, member)
        return ResponseEntity.ok(Response(code = HttpStatus.OK.value(), message = "포스트 수정이 완료되었습니다"))
    }

    @DeleteMapping("{postId}")
    fun delete(
        @PathVariable("postId") postId: UUID,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        postService.delete(postId, member)
        return ResponseEntity.ok(Response(code = HttpStatus.OK.value(), message = "포스트 삭제가 완료되었습니다"))
    }

    @GetMapping("")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun getPosts(
        pageable: Pageable,
        searchCondition: SearchConditionRequest,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Page<PostSummaryDto>>> {
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = postService.findAll(pageable, searchCondition, member),
            )
        )
    }

    @GetMapping("{postId}")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun getPost(
        @PathVariable("postId") postId: UUID,
    ): ResponseEntity<Response<Any>> {
        return ResponseEntity.ok(Response(code = HttpStatus.OK.value(), message = "포스트 조회"))
    }

    @PostMapping("{postId}/like")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun likePost(
        @PathVariable("postId") postId: UUID,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        postService.like(postId, member)
        return ResponseEntity.ok(Response(code = HttpStatus.OK.value(), message = "포스트 좋아요가 완료되었습니다"))
    }

    @DeleteMapping("{postId}/like")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun cancelLikePost(
        @PathVariable("postId") postId: UUID,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        postService.cancelLike(postId, member)
        return ResponseEntity.ok(Response(code = HttpStatus.OK.value(), message = "포스트 좋아요 취소가 완료되었습니다"))
    }

}