package healthiee.rest

import healthiee.rest.domain.EmailAuth
import healthiee.rest.domain.member.Member
import healthiee.rest.repository.EmailAuthRepository
import healthiee.rest.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class InitDatabase : ApplicationRunner {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var emailAuthRepository: EmailAuthRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun run(args: ApplicationArguments?) {
        saveMembers()
        saveEmailAuths()
    }

    private fun saveMembers() {
        val member1Email = "member1@gmail.com"
        val member1Nickname = "member1"
        val member2Email = "member2@gmail.com"
        val member2Nickname = "member2"
        val member3Email = "member3@gmail.com"
        val member3Nickname = "member3"

        if (memberRepository.findByEmail(member1Email) == null) {
            memberRepository.save(
                Member.createMember(
                    Member.MemberParam(
                        id = UUID.fromString("736cf454-2818-4fd9-a077-300b6f5efe64"),
                        email = member1Email,
                        password = passwordEncoder.encode(member1Nickname),
                        name = member1Nickname,
                        nickname = member1Nickname,
                    )
                )
            )
        }
        if (memberRepository.findByEmail(member2Email) == null) {
            memberRepository.save(
                Member.createMember(
                    Member.MemberParam(
                        id = UUID.fromString("3ab17fd7-a9cd-4996-9b01-9b2344a2ffdf"),
                        email = member2Email,
                        password = passwordEncoder.encode(member2Nickname),
                        name = member2Nickname,
                        nickname = member2Nickname,
                    )
                )
            )
        }
        if (memberRepository.findByEmail(member3Email) == null) {
            memberRepository.save(
                Member.createMember(
                    Member.MemberParam(
                        id = UUID.fromString("095fdc8c-0017-4107-ac82-39504990db20"),
                        email = member3Email,
                        password = passwordEncoder.encode(member3Nickname),
                        name = member3Nickname,
                        nickname = member3Nickname,
                    )
                )
            )
        }
    }

    private fun saveEmailAuths() {
        val member1Email = "member1@gmail.com"
        val member2Email = "member2@gmail.com"
        val member3Email = "member3@gmail.com"

        if (emailAuthRepository.findByEmail(member1Email) == null) {
            emailAuthRepository.save(
                EmailAuth.createEmailAuth(
                    member1Email,
                    UUID.fromString("2188a8c8-1918-4163-9471-33608833f780"),
                )
            )
        }
        if (emailAuthRepository.findByEmail(member2Email) == null) {
            emailAuthRepository.save(
                EmailAuth.createEmailAuth(
                    member2Email,
                    UUID.fromString("ae1541bf-6f48-46c8-9bee-bff264f279e2"),
                )
            )
        }
        if (emailAuthRepository.findByEmail(member3Email) == null) {
            emailAuthRepository.save(
                EmailAuth.createEmailAuth(
                    member3Email,
                    UUID.fromString("4e31c2f8-9998-4500-b5c1-291429d0656e"),
                )
            )
        }
    }
}