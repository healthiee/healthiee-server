package healthiee.rest.service

import healthiee.rest.repository.member.MemberRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    @DisplayName("회원가입 테스트")
    @Rollback(false)
    fun joinTest() {
//        // given
//        val request = JoinRequest(PlatformType.KAKAO, "12345")
//
//        // when
//        val savedId = memberService.join(request)
//        println("savedId: $savedId")
//
//        // then
//        val findMember: Member = memberRepository.findById(savedId).get()
//        println("{findMember.id}: ${findMember.id}")
//        Assertions.assertThat(findMember.id).isEqualTo(savedId)
    }
}

