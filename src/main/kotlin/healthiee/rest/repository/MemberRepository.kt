package healthiee.rest.repository

import healthiee.rest.domain.member.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository : JpaRepository<Member, UUID> {

    fun findByEmail(email: String): Member?

}