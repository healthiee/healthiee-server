package healthiee.rest.domain

import healthiee.rest.domain.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "members")
class Member(params: MemberParam) : BaseEntity() {

    @Id
    @Column(name = "member_id")
    val id: UUID = UUID.randomUUID()

    @Column(unique = true, nullable = false)
    var email: String = params.email
        private set

    @Column(nullable = false)
    var name: String = params.name
        private set

    @Column(unique = true, nullable = false)
    var nickname: String = params.nickname
        private set

    @Column(nullable = true)
    val bio: String? = null

    @Column(nullable = true)
    val profileUrl: String? = null

    data class MemberParam(
        val email: String,
        val name: String,
        val nickname: String,
    )

    companion object {
        fun createMember(params: MemberParam): Member {
            return Member(params)
        }
    }

}