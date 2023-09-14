package healthiee.rest.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import healthiee.rest.domain.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
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
    var bio: String? = params.bio
        private set

    @Column(nullable = true)
    var profileUrl: String? = params.profileUrl
        private set

    @Column(nullable = false)
    var workouts: List<String> = params.workouts
        private set

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    val memberRoles: List<MemberRole> = listOf()

    data class MemberParam(
        val email: String,
        val name: String,
        val nickname: String,
        val bio: String? = null,
        val profileUrl: String? = null,
        val workouts: List<String> = listOf()
    )

    companion object {
        fun createMember(params: MemberParam): Member {
            return Member(params)
        }
    }

}