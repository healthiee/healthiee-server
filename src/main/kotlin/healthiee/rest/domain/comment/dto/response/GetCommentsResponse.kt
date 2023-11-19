package healthiee.rest.domain.comment.dto.response

import healthiee.rest.domain.comment.dto.CommentDto
import java.util.*

data class GetCommentsResponse(
    val postId: UUID,
    val comments: List<CommentDto>,
)