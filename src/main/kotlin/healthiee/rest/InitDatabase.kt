package healthiee.rest

import healthiee.rest.domain.auth.EmailAuth
import healthiee.rest.domain.code.Code
import healthiee.rest.domain.common.MediaType
import healthiee.rest.domain.hashtag.Hashtag
import healthiee.rest.domain.member.Member
import healthiee.rest.domain.member.RoleType
import healthiee.rest.domain.post.Post
import healthiee.rest.domain.post.PostMedia
import healthiee.rest.repository.auth.EmailAuthRepository
import healthiee.rest.repository.code.CodeRepository
import healthiee.rest.repository.hashtag.HashtagRepository
import healthiee.rest.repository.member.MemberRepository
import healthiee.rest.repository.post.PostRepository
import jakarta.persistence.EntityManager
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class InitDatabase(
    private val memberRepository: MemberRepository,
    private val emailAuthRepository: EmailAuthRepository,
    private val hashtagRepository: HashtagRepository,
    private val codeRepository: CodeRepository,
    private val postRepository: PostRepository,
    private val entityMange: EntityManager,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        saveCodes()
        saveAdmin()
        saveMembers()
        saveEmailAuths()
//        savePosts()
    }

    private fun saveCodes() {
        val code1Name = "오운완"
        val code2Name = "핫플레이스"
        val code3Name = "일상"

        if (codeRepository.findByName(code1Name) == null) {
            codeRepository.save(Code.createCode(Code.Type.POST_CATEGORY, code1Name))
        }
        if (codeRepository.findByName(code2Name) == null) {
            codeRepository.save(Code.createCode(Code.Type.POST_CATEGORY, code2Name))
        }
        if (codeRepository.findByName(code3Name) == null) {
            codeRepository.save(Code.createCode(Code.Type.POST_CATEGORY, code3Name))
        }
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

        val hashtag = Hashtag.createHashtag("크로스핏")
        if (hashtagRepository.findByName("크로스핏") == null) {
            hashtagRepository.save(hashtag)
        }

        if (memberRepository.findByEmail(member1Email) == null) {
            memberRepository.save(
                Member.createMember(
                    Member.MemberParam(
                        id = UUID.fromString("736cf454-2818-4fd9-a077-300b6f5efe64"),
                        email = member1Email,
                        password = passwordEncoder.encode(member1Nickname),
                        name = member1Nickname,
                        nickname = member1Nickname,
                        workoutHashtags = listOf(hashtag)
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

    @Transactional
    private fun savePosts() {
        val member1Email = "member1@gmail.com"
        val code1Name = "오운완"
        val content =
            "1위는 맨체스터 시티의 수비수 후벵 디아스가 뽑혔다. 디아스는 2022-2023시즌 프리미어리그, FA컵, 유럽축구연맹(UEFA) 챔피언스리그(UCL) 우승으로 트레블을 달성했다. 펩 과르디올라 감독 아래 세계 최고라는 평가를 받고 있다.\n" +
                    "\n" +
                    "2위는 괴물 수비수 김민재였다. 김민재는 지난 시즌 이탈리아 세리에A 나폴리에서 최우수 수비상, 리그 우승컵까지 들어 올렸다. 나폴리의 33년 만에 우승은 김민재가 합류한 뒤에 이뤄졌다. 이후 뮌헨으로 이적해서도 김민재는 주전 센터백으로 자리매김했다.\n" +
                    "\n" +
                    "출처 : 아던트뉴스(https://www.ardentnews.co.kr)"

        val findMember = memberRepository.findByEmail(member1Email) ?: return
        val code = codeRepository.findByName(code1Name)

        val media1 = PostMedia.createPostMedia(MediaType.IMAGE, "url1")
        val media2 = PostMedia.createPostMedia(MediaType.IMAGE, "url2")

        val post = Post.createPost(code, findMember, content, null, media1, media2)
        postRepository.save(post)
    }

}