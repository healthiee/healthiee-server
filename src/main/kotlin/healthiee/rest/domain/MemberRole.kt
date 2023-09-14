package healthiee.rest.domain

import healthiee.rest.domain.base.BaseEntity
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
@Table(name = "member_roles")
class MemberRole(
    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val role: RoleType,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    val id: Long = 0L

    companion object {
        fun createMemberRole(member: Member) = MemberRole(RoleType.MEMBER, member)
    }

    enum class RoleType {
        MEMBER,
    }

}