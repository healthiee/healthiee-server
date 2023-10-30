package healthiee.rest.domain.post

import com.fasterxml.jackson.annotation.JsonIgnore
import healthiee.rest.domain.base.BaseEntity
import healthiee.rest.domain.common.MediaType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "post_medias")
class PostMedia private constructor(
    _type: MediaType,
    _url: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_media_id")
    val id: Long = 0L

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnore
    lateinit var post: Post

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: MediaType = _type

    @Column(nullable = false)
    val url: String = _url

    companion object {
        fun createPostMedia(
            type: MediaType,
            url: String,
        ): PostMedia = PostMedia(type, url)
    }

    override fun toString(): String {
        return "PostMedia(id=$id, type='$type', url='$url')"
    }

}