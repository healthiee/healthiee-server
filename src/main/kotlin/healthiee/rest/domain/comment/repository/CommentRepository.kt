package healthiee.rest.domain.comment.repository

import healthiee.rest.domain.comment.entity.PostComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<PostComment, Long>