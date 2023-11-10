package healthiee.rest.domain.post

import healthiee.rest.domain.base.BaseEntity
import healthiee.rest.domain.code.Code
import healthiee.rest.domain.member.Member
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
    _category: Code?,
    _member: Member,
    _content: String,
    _location: PostLocation?,
) : BaseEntity() {

    @Id
    @Column(name = "post_id")
    val id: UUID = UUID.randomUUID()

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
    val medias: MutableList<PostMedia> = mutableListOf()

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = true)
    var location: PostLocation? = _location
        private set

    fun changeCategory(category: Code?) {
        this.category = category
    }

    fun changeLocation(location: PostLocation?) {
        this.location = location
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

    companion object {
        fun createPost(
            category: Code?,
            member: Member,
            content: String,
            location: PostLocation?,
            vararg postMedias: PostMedia,
        ) = Post(category, member, content, location).apply {
            postMedias.forEach { addMedia(it) }
        }
    }

    override fun toString(): String {
        return "Post(id=$id, category='$category', member='$member', content='$content', location='$location', medias='$medias')"
    }

}