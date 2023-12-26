package healthiee.rest.domain.member.entity

import healthiee.rest.domain.common.entity.base.BaseEntity
import healthiee.rest.domain.hashtag.entity.Hashtag
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
@Table(name = "members")
class Member(params: MemberParam) : BaseEntity(), UserDetails {

    @Id
    @Column(name = "member_id")
    val id: UUID = params.id

    @Column(unique = true, nullable = false)
    var email: String = params.email
        private set

    @Column(name = "password", nullable = false)
    var jwtPassword: String = params.password
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

    @Enumerated(EnumType.STRING)
    var role: RoleType = params.roleType
        private set

    @ManyToMany
    @JoinTable(
        name = "member_hashtag",
        joinColumns = [JoinColumn(name = "member_id")],
        inverseJoinColumns = [JoinColumn(name = "hashtag_id")],
    )
    val workoutHashtags: MutableList<Hashtag> = mutableListOf()

    fun changeMember(
        name: String,
        nickname: String,
        bio: String?,
        profileUrl: String?,
        workoutHashtags: List<Hashtag> = listOf(),
        password: String,
    ) {
        this.name = name
        this.nickname = nickname
        this.bio = bio
        this.profileUrl = profileUrl
        this.workoutHashtags.clear()
        this.workoutHashtags.addAll(workoutHashtags)
        this.jwtPassword = password
    }

    data class MemberParam(
        val id: UUID = UUID.randomUUID(),
        val email: String,
        val password: String,
        val name: String,
        val nickname: String,
        val roleType: RoleType = RoleType.MEMBER,
        val bio: String? = null,
        val profileUrl: String? = null,
        val workoutHashtags: List<Hashtag> = listOf(),
    )

    override fun getAuthorities(): Collection<GrantedAuthority> = role.getAuthorities()

    override fun getPassword(): String = jwtPassword

    override fun getUsername(): String = id.toString()

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    companion object {
        fun createMember(params: MemberParam): Member {
            return Member(params).apply {
                this.workoutHashtags.addAll(params.workoutHashtags)
            }
        }
    }

    override fun toString(): String {
        return "Member(id=$id, email='$email', name='$name', nickname='$nickname', bio='$bio', profileUrl='$profileUrl')"
    }

}