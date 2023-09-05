package healthiee.rest.service

import healthiee.rest.domain.EmailAuth
import healthiee.rest.lib.email.MailService
import healthiee.rest.repository.EmailAuthRepository
import healthiee.rest.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class EmailAuthService(
    private val emailAuthRepository: EmailAuthRepository,
    private val memberRepository: MemberRepository,
    private val mailService: MailService,
) {

    @Transactional
    fun createAuthEmail(email: String): Long {
        val emailAuth = EmailAuth.createEmailAuth(email)
        emailAuthRepository.save(emailAuth)

        return emailAuth.id
//
//        mailService.send(email, registered)
    }

    fun verifyCode(code: String) {

    }

}