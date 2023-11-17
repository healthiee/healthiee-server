package healthiee.rest.domain.auth.entity

import healthiee.rest.domain.common.entity.base.BaseEntity
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
    _code: UUID,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    val id: Long = 0L

    @Column(unique = true, nullable = false)
    val code: UUID = _code

    @Column
    var disabled: Boolean = false
        private set

    fun used() {
        this.disabled = true
    }

    companion object {
        fun createEmailAuth(
            email: String,
            code: UUID = UUID.randomUUID(),
        ): EmailAuth = EmailAuth(email, code)

    }

}