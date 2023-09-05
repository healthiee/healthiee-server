package healthiee.rest.service

import healthiee.rest.repository.EmailAuthRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class EmailAuthServiceTest {

    @Autowired
    private lateinit var emailAuthService: EmailAuthService

    @Autowired
    private lateinit var emailAuthRepository: EmailAuthRepository

    @Test
    fun sendAuthEmail() {

    }

}