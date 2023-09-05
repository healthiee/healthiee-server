package healthiee.rest.repository

import healthiee.rest.domain.EmailAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailAuthRepository : JpaRepository<EmailAuth, Long>