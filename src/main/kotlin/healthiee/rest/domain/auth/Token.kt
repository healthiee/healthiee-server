package healthiee.rest.domain.auth

import healthiee.rest.domain.base.BaseEntity
import healthiee.rest.domain.member.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "tokens")
class Token(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
) : BaseEntity() {

    @Id
    @Column(name = "token_id")
    val id: UUID = UUID.randomUUID()

    @Column(nullable = false)
    var rotationCounter: Int = 1
        private set

    @Column(nullable = false)
    var blocked: Boolean = false
        private set

    fun block() {
        blocked = true
    }

    fun increaseRotationCounter() {
        rotationCounter += 1
    }

    companion object {
        fun createToken(member: Member) = Token(member)
    }

}