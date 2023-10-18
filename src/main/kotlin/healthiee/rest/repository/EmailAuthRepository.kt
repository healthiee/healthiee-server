package healthiee.rest.repository

import healthiee.rest.domain.EmailAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EmailAuthRepository : JpaRepository<EmailAuth, Long> {
    fun findByCode(code: UUID): EmailAuth?
    fun findByEmail(email: String): EmailAuth?
}