package healthiee.rest.domain.comment.api

import healthiee.rest.domain.comment.dto.CommentDto
import healthiee.rest.domain.comment.dto.request.CreateCommentRequest
import healthiee.rest.domain.comment.dto.request.GetCommentsRequest
import healthiee.rest.domain.comment.dto.request.UpdateCommentRequest
import healthiee.rest.domain.comment.dto.response.CreateCommentResponse
import healthiee.rest.domain.comment.dto.response.GetCommentsResponse
import healthiee.rest.domain.comment.service.CommentService
import healthiee.rest.domain.common.dto.base.Response
import healthiee.rest.domain.member.entity.Member
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("v1/comments")
class CommentApiController(
    private val commentService: CommentService,
) {

    @PostMapping("")
    fun createComment(
        @Validated @RequestBody request: CreateCommentRequest,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<CreateCommentResponse>> {
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = commentService.createComment(request, member),
                message = "게시글 생성이 완료되었습니다"
            )
        )
    }

    @PatchMapping("{commentId}")
    fun updateComment(
        @PathVariable("commentId") commentId: Long,
        @Validated @RequestBody request: UpdateCommentRequest,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        commentService.updateComment(commentId, request, member)
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                message = "게시글 수정이 완료되었습니다",
            )
        )
    }

    @DeleteMapping("{commentId}")
    fun deleteComment(
        @PathVariable("commentId") commentId: Long,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        commentService.deleteComment(commentId, member)
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                message = "게시글 삭제가 완료되었습니다",
            )
        )
    }

    @GetMapping("")
    fun getComments(
        @Validated request: GetCommentsRequest,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<GetCommentsResponse>> {
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = commentService.getComments(request, member)
            )
        )
    }

    @GetMapping("{commentId}")
    fun getComment(
        @PathVariable("commentId") commentId: Long,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<CommentDto>> {
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = commentService.getComment(commentId, member)
            )
        )
    }

    /**
     * 댓글 좋아요
     */
    @PostMapping("{commentId}/like")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun likeComment(
        @PathVariable("commentId") commentId: Long,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        commentService.like(commentId, member)
        return ResponseEntity.ok(Response(code = HttpStatus.OK.value(), message = "댓글 좋아요가 완료되었습니다"))
    }

    @DeleteMapping("{commentId}/like")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun cancelLikeComment(
        @PathVariable("commentId") commentId: Long,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        commentService.cancelLike(commentId, member)
        return ResponseEntity.ok(Response(code = HttpStatus.OK.value(), message = "댓글 좋아요 취소가 완료되었습니다"))
    }

}