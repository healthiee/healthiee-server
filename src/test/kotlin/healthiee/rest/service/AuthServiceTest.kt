package healthiee.rest.service

import healthiee.rest.repository.auth.EmailAuthRepository
import healthiee.rest.repository.member.MemberRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional(readOnly = true)
class AuthServiceTest {

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var emailAuthRepository: EmailAuthRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    @DisplayName("코드 로그인 성공")
    fun codeLoginTest() {
//        // given
//        val member = Member.createMember(Member.MemberParam("jihoi.kang@gmail.com", "강지회", "aloha.jihoi"))
//        memberRepository.save(member)
//        val emailAuth = EmailAuth.createEmailAuth("jihoi.kang@gmail.com")
//        emailAuthRepository.save(emailAuth)
//        val request = CodeLoginRequest(emailAuth.code)
//
//        // when
//        authService.codeLogin(request)
//
//        // then
//        val findEmailAuth = emailAuthRepository.findByIdOrNull(emailAuth.id)
//        Assertions.assertThat(findEmailAuth?.disabled).isEqualTo(true)
    }

}