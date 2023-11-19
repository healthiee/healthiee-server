package healthiee.rest.domain.comment.repository

import healthiee.rest.domain.comment.entity.PostCommentLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentLikeRepository : JpaRepository<PostCommentLike, Long>