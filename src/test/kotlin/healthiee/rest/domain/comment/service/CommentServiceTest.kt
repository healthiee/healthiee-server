package healthiee.rest.domain.comment.service

import healthiee.rest.domain.comment.dto.request.CreateCommentRequest
import healthiee.rest.domain.comment.dto.request.UpdateCommentRequest
import healthiee.rest.domain.comment.entity.PostComment
import healthiee.rest.domain.comment.repository.CommentRepository
import healthiee.rest.domain.member.repository.MemberRepository
import healthiee.rest.domain.post.repository.PostRepository
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@Transactional(readOnly = true)
class CommentServiceTest {

    @Autowired
    private lateinit var service: CommentService

    @Autowired
    private lateinit var repository: CommentRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var em: EntityManager

    @Test
    @Transactional
    @DisplayName("유효하지 않은 게시글 ID를 입력하면 에러가 발생해야 한다")
    fun createTestFailureInvalidPostId() {
        // given
        val member = memberRepository.findByIdOrNull(UUID.fromString("736cf454-2818-4fd9-a077-300b6f5efe64"))
            ?: throw IllegalStateException("Member가 존재하지 않습니다.")
        val request = CreateCommentRequest(INVALID_POST_ID, "댓글 입력 #1")

        // when & then
        val exception: ApiException = assertThrows(ApiException::class.java) { service.createComment(request, member) }
        Assertions.assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
    }

    @Test
    @Transactional
    @DisplayName("유효하지 않은 부모 댓글 ID를 입력하면 에러가 발생해야 한다")
    fun createTestFailureInvalidParentCommentId() {
        // given
        val member = memberRepository.findByIdOrNull(UUID.fromString("736cf454-2818-4fd9-a077-300b6f5efe64"))
            ?: throw IllegalStateException("Member가 존재하지 않습니다")
        val request = CreateCommentRequest(VALID_POST_ID, "댓글 입력 #1", Integer.MAX_VALUE.toLong())

        // when & then
        val exception: ApiException = assertThrows(ApiException::class.java) { service.createComment(request, member) }
        Assertions.assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
    }

    @Test
    @Transactional
    fun updateTest() {
        // given
        val member = memberRepository.findByIdOrNull(UUID.fromString("736cf454-2818-4fd9-a077-300b6f5efe64"))
            ?: throw IllegalStateException("Member가 존재하지 않습니다.")
        val post = postRepository.findByIdOrNull(VALID_POST_ID) ?: throw IllegalStateException("Post가 존재하지 않습니다")
        val updateRequest = UpdateCommentRequest("댓글 #2")

        val postComment = PostComment.createPostComment(
            member,
            post,
            "댓글 #1",
        )
        repository.save(postComment)
        em.flush()
        em.clear()

        // when
        service.updateComment(postComment.id, updateRequest, member)
        em.flush()
        em.clear()

        // then
        val findPost = repository.findByIdOrNull(postComment.id) ?: throw IllegalStateException("Post가 존재하지 않습니다")
        Assertions.assertThat(findPost.content).isEqualTo("댓글 #2")
    }

    @Test
    @Transactional
    @DisplayName("작성자 외의 멤버는 댓글을 변경할 수 없다")
    fun updateTestFailure_memberException() {
        val member1 = memberRepository.findByIdOrNull(UUID.fromString("736cf454-2818-4fd9-a077-300b6f5efe64"))
            ?: throw IllegalStateException("Member가 존재하지 않습니다.")
        val member2 = memberRepository.findByIdOrNull(UUID.fromString("3ab17fd7-a9cd-4996-9b01-9b2344a2ffdf"))
            ?: throw IllegalStateException("Member가 존재하지 않습니다.")
        val post = postRepository.findByIdOrNull(VALID_POST_ID) ?: throw IllegalStateException("Post가 존재하지 않습니다")
        val updateRequest = UpdateCommentRequest("댓글 #2")

        val postComment = PostComment.createPostComment(
            member1,
            post,
            "댓글 #1",
        )
        repository.save(postComment)
        em.flush()
        em.clear()

        // when & then
        val exception: ApiException =
            assertThrows(ApiException::class.java) { service.updateComment(postComment.id, updateRequest, member2) }
        Assertions.assertThat(exception.errorCode).isEqualTo(ErrorCode.FORBIDDEN)
    }

    @Test
    @Transactional
    @DisplayName("작성자 외의 멤버는 댓글을 삭제할 수 없다")
    fun deleteTestFailure_memberException() {
        val member1 = memberRepository.findByIdOrNull(UUID.fromString("736cf454-2818-4fd9-a077-300b6f5efe64"))
            ?: throw IllegalStateException("Member가 존재하지 않습니다.")
        val member2 = memberRepository.findByIdOrNull(UUID.fromString("3ab17fd7-a9cd-4996-9b01-9b2344a2ffdf"))
            ?: throw IllegalStateException("Member가 존재하지 않습니다.")
        val post = postRepository.findByIdOrNull(VALID_POST_ID) ?: throw IllegalStateException("Post가 존재하지 않습니다")

        val postComment = PostComment.createPostComment(
            member1,
            post,
            "댓글 #1",
        )
        repository.save(postComment)
        em.flush()
        em.clear()

        // when & then
        val exception: ApiException =
            assertThrows(ApiException::class.java) { service.deleteComment(postComment.id, member2) }
        Assertions.assertThat(exception.errorCode).isEqualTo(ErrorCode.FORBIDDEN)
    }

    companion object {
        private val VALID_POST_ID: UUID = UUID.fromString("87f3748d-1e0d-410a-8a53-3811208bfa24")
        private val INVALID_POST_ID: UUID = UUID.fromString("d52a4216-0acc-43d7-ba00-d3ffdeecc59b")
    }

}