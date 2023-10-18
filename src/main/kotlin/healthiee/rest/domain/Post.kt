package healthiee.rest.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "posts")
class Post(
    content: String,
) {

    @Id
    @Column(name = "post_id")
    val id: UUID = UUID.randomUUID()

    @Column
    var content: String = content
        private set

}