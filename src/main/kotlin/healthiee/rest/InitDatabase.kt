package healthiee.rest

import healthiee.rest.domain.auth.EmailAuth
import healthiee.rest.domain.member.Member
import healthiee.rest.domain.member.RoleType
import healthiee.rest.repository.auth.EmailAuthRepository
import healthiee.rest.repository.member.MemberRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class InitDatabase(
    private val memberRepository: MemberRepository,
    private val emailAuthRepository: EmailAuthRepository,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        saveAdmin()
        saveMembers()
        saveEmailAuths()
    }

    private fun saveAdmin() {
        val adminEmail = "admin@healthiee.net"
        val adminNickname = "admin"

        if (memberRepository.findByEmail(adminEmail) == null) {
            memberRepository.save(
                Member.createMember(
                    Member.MemberParam(
                        id = UUID.fromString("f31ab48f-ae64-45dc-a6f5-969baefa0d2a"),
                        email = adminEmail,
                        password = passwordEncoder.encode(adminNickname),
                        name = adminNickname,
                        nickname = adminNickname,
                        roleType = RoleType.ADMIN,
                    )
                )
            )
        }

        if (emailAuthRepository.findByEmail(adminEmail) == null) {
            emailAuthRepository.save(
                EmailAuth.createEmailAuth(
                    adminEmail,
                    UUID.fromString("ae8ba0f3-6717-4c54-a76f-db2e92bdfd7f"),
                )
            )
        }
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