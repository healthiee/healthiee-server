package healthiee.rest.service

import healthiee.rest.domain.common.MediaType
import healthiee.rest.domain.post.Post
import healthiee.rest.domain.post.PostMedia
import healthiee.rest.repository.code.CodeRepository
import healthiee.rest.repository.member.MemberRepository
import healthiee.rest.repository.post.query.PostQueryRepository
import healthiee.rest.repository.post.PostRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional(readOnly = true)
class PostServiceTest {

    @Autowired
    private lateinit var postService: PostService

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var postQueryRepository: PostQueryRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var codeRepository: CodeRepository

    @Test
    @Transactional
    fun getPost() {
        // given
        val member1Email = "member1@gmail.com"
        val content =
            "1위는 맨체스터 시티의 수비수 후벵 디아스가 뽑혔다. 디아스는 2022-2023시즌 프리미어리그, FA컵, 유럽축구연맹(UEFA) 챔피언스리그(UCL) 우승으로 트레블을 달성했다. 펩 과르디올라 감독 아래 세계 최고라는 평가를 받고 있다.\n" +
                    "\n" +
                    "2위는 괴물 수비수 김민재였다. 김민재는 지난 시즌 이탈리아 세리에A 나폴리에서 최우수 수비상, 리그 우승컵까지 들어 올렸다. 나폴리의 33년 만에 우승은 김민재가 합류한 뒤에 이뤄졌다. 이후 뮌헨으로 이적해서도 김민재는 주전 센터백으로 자리매김했다.\n" +
                    "\n" +
                    "출처 : 아던트뉴스(https://www.ardentnews.co.kr)"

        val findMember = memberRepository.findByEmail(member1Email) ?: return

        val media1 = PostMedia.createPostMedia(MediaType.IMAGE, "url1")
        val media2 = PostMedia.createPostMedia(MediaType.IMAGE, "url2")

        val post = Post.createPost(null, findMember, content, null, media1, media2)
        postRepository.save(post)

        // when
        val findPost = postService.findById(post.id)

        // then
        println("findPost = $findPost")
        Assertions.assertThat(findPost.id).isEqualTo(post.id)
        Assertions.assertThat(findPost.medias[0].url).isEqualTo("url1")

    }

}