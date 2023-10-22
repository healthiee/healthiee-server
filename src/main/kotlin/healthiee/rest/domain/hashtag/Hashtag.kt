package healthiee.rest.domain.hashtag

import healthiee.rest.domain.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "hashtags")
class Hashtag(
    _name: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    val id: Long = 0L

    @Column(nullable = false)
    var name: String = _name
        private set

    companion object {
        fun createHashtag(name: String) = Hashtag(name)
    }

}