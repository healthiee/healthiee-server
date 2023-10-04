package healthiee.rest.domain.member

import healthiee.rest.api.member.dto.MemberDto
import healthiee.rest.domain.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
@Table(name = "members")
class Member(params: MemberParam) : BaseEntity(), UserDetails {

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
    var bio: String? = params.bio
        private set

    @Column(nullable = true)
    var profileUrl: String? = params.profileUrl
        private set

    @Column(nullable = false)
    var workouts: List<String> = params.workouts
        private set

    @Enumerated(EnumType.STRING)
    var role: RoleType = params.roleType
        private set

    data class MemberParam(
        val email: String,
        val name: String,
        val nickname: String,
        val roleType: RoleType = RoleType.MEMBER,
        val bio: String? = null,
        val profileUrl: String? = null,
        val workouts: List<String> = listOf(),
    )

    override fun getAuthorities(): Collection<GrantedAuthority> = role.getAuthorities()

    override fun getPassword(): String = "$id$email"

    override fun getUsername(): String = id.toString()

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    companion object {
        fun createMember(params: MemberParam): Member {
            return Member(params)
        }
    }

}

fun Member.toDto() = MemberDto(
    id,
    email,
    name,
    nickname,
    bio,
    profileUrl,
    workouts,
)