package healthiee.rest.domain.comment.service

import healthiee.rest.domain.comment.dto.CommentDto
import healthiee.rest.domain.comment.dto.RecommentDto
import healthiee.rest.domain.comment.dto.request.CreateCommentRequest
import healthiee.rest.domain.comment.dto.request.GetCommentsRequest
import healthiee.rest.domain.comment.dto.request.UpdateCommentRequest
import healthiee.rest.domain.comment.dto.response.CreateCommentResponse
import healthiee.rest.domain.comment.dto.response.GetCommentsResponse
import healthiee.rest.domain.comment.entity.PostComment
import healthiee.rest.domain.comment.entity.PostCommentLike
import healthiee.rest.domain.comment.repository.CommentLikeQueryRepository
import healthiee.rest.domain.comment.repository.CommentLikeRepository
import healthiee.rest.domain.comment.repository.CommentQueryRepository
import healthiee.rest.domain.comment.repository.CommentRepository
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.post.entity.Post
import healthiee.rest.domain.post.repository.PostQueryRepository
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.BAD_REQUEST
import healthiee.rest.lib.error.ErrorCode.FORBIDDEN
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = false)
class CommentService(
    private val repository: CommentRepository,
    private val queryRepository: CommentQueryRepository,
    private val postCommentLikeRepository: CommentLikeRepository,
    private val postQueryRepository: PostQueryRepository,
    private val postCommentLikeQueryRepository: CommentLikeQueryRepository,
) {

    @Transactional
    fun createComment(request: CreateCommentRequest, member: Member): CreateCommentResponse {
        val validPost = getValidPost(request.postId)
        val parentComment: PostComment? = request.parentCommentId?.let {
            getValidComment(it, false).also { postComment ->
                if (postComment.parentComment != null) throw ApiException(BAD_REQUEST, "대댓글에 댓글을 달 수 없습니다")
            }
        }
        val postComment = PostComment.createPostComment(
            member,
            validPost,
            request.content,
            parentComment,
        )
        repository.save(postComment)
        validPost.increaseCommentCount()
        return CreateCommentResponse(postComment.id)
    }

    @Transactional
    fun updateComment(commentId: Long, request: UpdateCommentRequest, member: Member) {
        val validComment = getValidComment(commentId)
        validateUpdateComment(validComment, member)

        validComment.changeContent(request.content)
    }

    @Transactional
    fun deleteComment(commentId: Long, member: Member) {
        val validComment = getValidComment(commentId)
        validateUpdateComment(validComment, member)

        validComment.delete()
        validComment.post.decreaseCommentCount()
    }

    fun getComments(request: GetCommentsRequest, member: Member): GetCommentsResponse {
        val validPost = getValidPost(request.postId)
        val parentComments = queryRepository.findParentCommentsByPostId(validPost.id)
        val comments = parentComments.map {
            it.updateChildComments(queryRepository.findAllByParentCommentId(it.id))

            CommentDto.create(
                comment = it,
                recomments = it.childComments.map { postComment ->
                    RecommentDto.create(
                        postComment,
                        postComment.commentLikes.any { commentLike -> commentLike.member.id == member.id })
                },
                liked = it.commentLikes.any { commentLike -> commentLike.member.id == member.id })
        }

        return GetCommentsResponse(
            validPost.id,
            comments,
        )
    }

    fun getComment(commentId: Long, member: Member): CommentDto {
        val validComment = getValidComment(commentId)
        validComment.updateChildComments(queryRepository.findAllByParentCommentId(validComment.id))

        return CommentDto.create(
            comment = validComment,
            recomments = validComment.childComments.map { postComment ->
                RecommentDto.create(
                    postComment,
                    postComment.commentLikes.any { commentLike -> commentLike.member.id == member.id })
            },
            liked = validComment.commentLikes.any { commentLike -> commentLike.member.id == member.id }
        )
    }

    @Transactional
    fun like(commentId: Long, member: Member) {
        val findComment = getValidComment(commentId)
        val findCommentLike = postCommentLikeQueryRepository.findByMemberAndComment(member.id, commentId)
        findCommentLike?.let { throw ApiException(BAD_REQUEST, "이미 좋아요를 누른 이력이 있습니다") }
        postCommentLikeRepository.save(PostCommentLike.createPostCommentLike(member, findComment))
        findComment.increaseLikeCount()
    }

    @Transactional
    fun cancelLike(commentId: Long, member: Member) {
        val findComment = getValidComment(commentId)
        val findCommentLike = postCommentLikeQueryRepository.findByMemberAndComment(member.id, commentId)
            ?: throw ApiException(NOT_FOUND, "좋아요를 누른 이력이 없습니다")
        findCommentLike.delete()
        findComment.decreaseLikeCount()
    }

    private fun getValidComment(commentId: Long, fetchQuery: Boolean = true): PostComment {
        val exception = ApiException(NOT_FOUND, "댓글을 찾을 수 없습니다")
        return if (fetchQuery) queryRepository.findById(commentId) ?: throw exception
        else repository.findByIdOrNull(commentId) ?: throw exception
    }

    private fun getValidPost(postId: UUID): Post {
        return postQueryRepository.findById(postId) ?: throw ApiException(NOT_FOUND, "게시물을 찾을 수 없습니다")
    }

    private fun validateUpdateComment(postComment: PostComment, member: Member) {
        if (postComment.member.id != member.id) {
            throw ApiException(FORBIDDEN, "댓글을 업데이트할 수 없습니다")
        }
    }

}