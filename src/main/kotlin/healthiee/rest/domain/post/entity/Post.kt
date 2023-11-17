package healthiee.rest.domain.post.entity

import healthiee.rest.domain.common.entity.base.BaseEntity
import healthiee.rest.domain.code.entity.Code
import healthiee.rest.domain.member.entity.Member
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "posts")
class Post(
    _postId: UUID,
    _category: Code?,
    _member: Member,
    _content: String,
    _location: PostLocation?,
) : BaseEntity() {

    @Id
    @Column(name = "post_id")
    val id: UUID = _postId

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_id", nullable = true)
    var category: Code? = _category
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member = _member

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String = _content
        private set

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL])
    var medias: MutableList<PostMedia> = mutableListOf()
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = true)
    var location: PostLocation? = _location
        private set

    @Column(nullable = false)
    var likeCount: Int = 0
        private set

    @OneToMany(mappedBy = "post")
    var postLikes: MutableList<PostLike> = mutableListOf()
        private set

    @Column(nullable = false)
    var commentCount: Int = 0
        private set

    fun changeContent(
        category: Code?,
        location: PostLocation?,
        content: String,
        mediaIds: List<Long>,
    ) {
        this.category = category
        this.location = location
        this.content = content
        this.medias = medias.filter { mediaIds.contains(it.id) }.toMutableList()
    }

    fun addMedia(media: PostMedia) {
        this.medias.add(media)
        media.post = this
    }

    fun updateMedias(medias: List<PostMedia>) {
        this.medias.clear()
        this.medias.addAll(medias)
        medias.forEach { it.post = this }
    }

    fun updatePostLikes(postLikes: List<PostLike>) {
        this.postLikes.clear()
        this.postLikes.addAll(postLikes)
        postLikes.forEach { it.post = this }
    }

    fun increaseLikeCount() {
        this.likeCount = this.likeCount + 1
    }

    fun increaseCommentCount() {
        this.commentCount = this.commentCount + 1
    }

    fun decreaseLikeCount() {
        if (this.likeCount == 0) throw IllegalStateException("좋아요 갯수 동기화 오류")
        this.likeCount = this.likeCount - 1
    }

    fun decreaseCommentCount() {
        if (this.commentCount == 0) throw IllegalStateException("댓글 갯수 동기화 오류")
        this.commentCount = this.commentCount - 1
    }

    override fun delete() {
        medias.forEach { it.delete() }
        super.delete()
    }

    companion object {
        fun createPost(
            postId: UUID = UUID.randomUUID(),
            category: Code?,
            member: Member,
            content: String,
            location: PostLocation?,
            vararg postMedias: PostMedia,
        ) = Post(postId, category, member, content, location).apply {
            postMedias.forEach { addMedia(it) }
        }
    }

    override fun toString(): String {
        return "Post(id=$id, category='$category', member='$member', content='$content', location='$location', medias='$medias, likeCount='$likeCount')"
    }

}