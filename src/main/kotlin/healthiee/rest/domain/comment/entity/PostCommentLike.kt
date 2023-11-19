package healthiee.rest.domain.comment.entity

import healthiee.rest.domain.common.entity.base.BaseEntity
import healthiee.rest.domain.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "post_comment_likes")
class PostCommentLike(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_comment_id")
    var postComment: PostComment,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_like_id")
    val id: Long = 0L

    companion object {
        fun createPostCommentLike(member: Member, postComment: PostComment) = PostCommentLike(member, postComment)
    }

}