package healthiee.rest.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "codes")
class Code(
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: Type,
    @Column(nullable = false)
    val name: String,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    val id: Long = 0L

    enum class Type {
        POST_CATEGORY,
    }

}