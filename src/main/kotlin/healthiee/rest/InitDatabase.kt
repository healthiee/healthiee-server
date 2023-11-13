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
import org.springframework.data.repository.findByIdOrNull
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
        savePosts()
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
        val content1 =
            "1위는 맨체스터 시티의 수비수 후벵 디아스가 뽑혔다. 디아스는 2022-2023시즌 프리미어리그, FA컵, 유럽축구연맹(UEFA) 챔피언스리그(UCL) 우승으로 트레블을 달성했다. 펩 과르디올라 감독 아래 세계 최고라는 평가를 받고 있다.\n" +
                    "\n" +
                    "2위는 괴물 수비수 김민재였다. 김민재는 지난 시즌 이탈리아 세리에A 나폴리에서 최우수 수비상, 리그 우승컵까지 들어 올렸다. 나폴리의 33년 만에 우승은 김민재가 합류한 뒤에 이뤄졌다. 이후 뮌헨으로 이적해서도 김민재는 주전 센터백으로 자리매김했다.\n" +
                    "\n" +
                    "출처 : 아던트뉴스(https://www.ardentnews.co.kr)"
        val postId1 = UUID.fromString("87f3748d-1e0d-410a-8a53-3811208bfa24")
        if (postRepository.findByIdOrNull(postId1) == null) {
            val findMember1 = memberRepository.findByEmail(member1Email) ?: return
            val code = codeRepository.findByName(code1Name)

            val medias = listOf(
                PostMedia.createPostMedia(MediaType.IMAGE, "url1"),
                PostMedia.createPostMedia(MediaType.IMAGE, "url2")
            )

            val post = Post.createPost(
                postId = postId1,
                category = code,
                member = findMember1,
                content = content1,
                location = null,
                postMedias = medias.toTypedArray(),
            )
            postRepository.save(post)
        }

        val member2Email = "member2@gmail.com"
        val content2 =
            "개발자의 고객은 누구라고 생각하시나요? 우리 제품을 사용하는 사용자(End-user)죠. 그런데 또 다른 고객이 있어요. 컴파일 타임의 고객, 바로 동료 개발자입니다. 복잡하고 나쁜 코드는 사용자 고객에게는 버그와 장애를, 개발자 고객에게는 낮은 생산성을 줍니다. 이번 시리즈에서는 사용자 고객뿐 아니라 개발자 고객을 위한 코드 복잡성 관리에 대해 이야기해 볼게요.\n" +
                    "\n" +
                    "먼저 내 코드가 얼마나 복잡한지 체크리스트로 확인해 볼게요.\n" +
                    "\n" +
                    "✅ 코드를 읽고 있을 때 누군가 말을 걸면 어디까지 읽었는지 놓쳐서 처음부터 다시 읽어야 한다.\n" +
                    "✅ 코드 한 줄을 바꾸기 위해 바꿔야 할 다른 코드가 많다.\n" +
                    "✅ 새로운 사람이 팀에 합류하면 그 사람이 몇 주 내내 프로젝트 코드를 읽을 시간을 확보해야 한다.\n" +
                    "✅ 메서드 인자에 값을 전달하기 위해 지나가는 모든 메서드 인자 값을 추가한 적이 있다. 혹은 이 문제를 해결하기 위해 전역 변수를 사용하고 싶은 유혹을 받은 적이 있다.\n" +
                    "✅ 프로젝트 코드가 너무 복잡해서 처음부터 다시 만들면 적어도 지금보단 나았을 거라는 생각을 해본 적이 있다."
        val postId2 = UUID.fromString("d157882d-2058-4bab-a8b4-5c33d2ff414c")

        if (postRepository.findByIdOrNull(postId2) == null) {
            val findMember2 = memberRepository.findByEmail(member2Email) ?: return
            val post2 = Post.createPost(postId = postId2, null, findMember2, content2, null)
            postRepository.save(post2)
        }

        val member3Email = "member3@gmail.com"
        val content3 =
            "스프린트 참가자 중에 뒷풀이 중에서 '컨퍼런스를 해보고 싶어요' 는 말에 내가 해줄 수 있는 것은 무엇일까 고민하다가 무작정 시작해보게 된 TEOConf. 그리고 그때 함께 했던 경험들이 너무나 즐거웠기에 두번째 컨퍼런스 행사를 또 시작하게 되었습니다.\n" +
                    "\n" +
                    "처음 컨퍼런스 행사는 2월에 했었는데 이렇게 10월에 또 새로운 시작을 하고 시작을 해서 또 이렇게 새로운 사람들을 만나고 또 새로운 컨셉의 컨퍼런스를 만들고 함께 진행해보고 또 멋지게 잘 마무리를 해서 너무나 기분이 좋습니다.\n" +
                    "\n" +
                    "새로운 컨퍼런스를 준비하게 되면서 새로운 스피커분들과 함께 준비해나가던 과정과 기존의 스태프와 함께 새로운 도전을 함께 해보려고 했던 이야기 그리고 당일날 있었던 이야기들을 한번 이야기해보려고 합니다."
        val postId3 = UUID.fromString("19d41504-3942-456a-be6e-fc5678542d53")

        if (postRepository.findByIdOrNull(postId3) == null) {
            val findMember3 = memberRepository.findByEmail(member3Email) ?: return
            val post3 = Post.createPost(postId = postId3, null, findMember3, content3, null)
            postRepository.save(post3)
        }
    }

}