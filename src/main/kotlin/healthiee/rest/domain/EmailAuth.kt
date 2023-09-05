package healthiee.rest.domain

import healthiee.rest.domain.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "email_auth")
class EmailAuth(
    @Column(nullable = false)
    val email: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    val id: Long = 0L

    @Column(unique = true, nullable = false)
    val code: UUID = UUID.randomUUID()

    @Column
    val disabled: Boolean = false

    companion object {
        fun createEmailAuth(email: String): EmailAuth =
            EmailAuth(email)

    }

}