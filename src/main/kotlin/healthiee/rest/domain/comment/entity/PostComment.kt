package healthiee.rest.domain.comment.entity

import healthiee.rest.domain.common.entity.base.BaseEntity
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.post.entity.Post
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "post_comments")
class PostComment(
    _member: Member,
    _post: Post,
    _content: String,
    _parentComment: PostComment?,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_id")
    val id: Long = 0L

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_post_comment_id")
    var parentComment: PostComment? = _parentComment

    @OneToMany(mappedBy = "parentComment")
    val childComments: MutableList<PostComment> = mutableListOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member = _member

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: Post = _post

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String = _content
        private set

    @Column(nullable = false)
    var likeCount: Int = 0
        private set

    @OneToMany(mappedBy = "postComment")
    var commentLikes: MutableList<PostCommentLike> = mutableListOf()
        private set

    companion object {
        fun createPostComment(
            member: Member,
            post: Post,
            content: String,
            parentComment: PostComment? = null,
        ): PostComment = PostComment(member, post, content, parentComment)
    }

    fun changeContent(content: String) {
        this.content = content
    }

    fun increaseLikeCount() {
        this.likeCount = this.likeCount + 1
    }

    fun decreaseLikeCount() {
        if (this.likeCount == 0) throw IllegalStateException("좋아요 갯수 동기화 오류")
        this.likeCount = this.likeCount - 1
    }

    fun addChildComment(child: PostComment) {
        this.childComments.add(child)
        child.parentComment = this
    }

    fun updateChildComments(child: List<PostComment>) {
        this.childComments.clear()
        this.childComments.addAll(child)
        child.forEach { it.parentComment = this }
    }
}
