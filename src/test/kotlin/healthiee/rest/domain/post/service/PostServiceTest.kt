package healthiee.rest.domain.post.service

import healthiee.rest.domain.code.repository.CodeRepository
import healthiee.rest.domain.common.entity.media.MediaType
import healthiee.rest.domain.member.repository.MemberRepository
import healthiee.rest.domain.post.dto.request.PostLocationRequest
import healthiee.rest.domain.post.dto.request.UpdatePostRequest
import healthiee.rest.domain.post.entity.Post
import healthiee.rest.domain.post.entity.PostLocation
import healthiee.rest.domain.post.entity.PostMedia
import healthiee.rest.domain.post.repository.PostLocationRepository
import healthiee.rest.domain.post.repository.PostMediaRepository
import healthiee.rest.domain.post.repository.PostQueryRepository
import healthiee.rest.domain.post.repository.PostRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional(readOnly = true)
class PostServiceTest {

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var postService: PostService

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var postMediaRepository: PostMediaRepository

    @Autowired
    private lateinit var postLocationRepository: PostLocationRepository

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

        val post = Post.createPost(
            category = null,
            member = findMember,
            content = content,
            location = null,
        )
        postRepository.save(post)

        val medias = listOf(
            PostMedia.createPostMedia(MediaType.IMAGE, "url1", post),
            PostMedia.createPostMedia(MediaType.IMAGE, "url2", post),
        )
        postMediaRepository.saveAll(medias)
        em.clear()

        // when
        val findPost = postService.findById(post.id)

        // then
        Assertions.assertThat(findPost.id).isEqualTo(post.id)
        Assertions.assertThat(findPost.medias[0].url).isEqualTo("url1")
    }

    @Test
    @Transactional
    fun getPostLocationTest() {
        // given
        val member1Email = "member1@gmail.com"
        val content =
            "1위는 맨체스터 시티의 수비수 후벵 디아스가 뽑혔다. 디아스는 2022-2023시즌 프리미어리그, FA컵, 유럽축구연맹(UEFA) 챔피언스리그(UCL) 우승으로 트레블을 달성했다. 펩 과르디올라 감독 아래 세계 최고라는 평가를 받고 있다.\n" +
                    "\n" +
                    "2위는 괴물 수비수 김민재였다. 김민재는 지난 시즌 이탈리아 세리에A 나폴리에서 최우수 수비상, 리그 우승컵까지 들어 올렸다. 나폴리의 33년 만에 우승은 김민재가 합류한 뒤에 이뤄졌다. 이후 뮌헨으로 이적해서도 김민재는 주전 센터백으로 자리매김했다.\n" +
                    "\n" +
                    "출처 : 아던트뉴스(https://www.ardentnews.co.kr)"
        val latitude = 37.48223625654306
        val longitude = 126.91468278697884
        val addressName = "서울특별시 관악구 난곡로 316"

        val findMember = memberRepository.findByEmail(member1Email) ?: return

        val location = PostLocation.createLocation(
            latitude = latitude,
            longitude = longitude,
            addressName = addressName,
        )
        postLocationRepository.save(location)

        val post = Post.createPost(
            category = null,
            member = findMember,
            content = content,
            location = location,
        )
        postRepository.save(post)

        val medias = listOf(PostMedia.createPostMedia(MediaType.IMAGE, "url1", post))
        postMediaRepository.saveAll(medias)

        // when
        val findPost = postService.findById(post.id)

        // then
        Assertions.assertThat(findPost.id).isEqualTo(post.id)
        Assertions.assertThat(findPost.location?.latitude).isEqualTo(latitude)
        Assertions.assertThat(findPost.location?.longitude).isEqualTo(longitude)
        Assertions.assertThat(findPost.location?.addressName).isEqualTo(addressName)
    }

    @Test
    @Transactional
    fun updateLocationTest() {
        // given
        val member1Email = "member1@gmail.com"
        val content =
            "1위는 맨체스터 시티의 수비수 후벵 디아스가 뽑혔다. 디아스는 2022-2023시즌 프리미어리그, FA컵, 유럽축구연맹(UEFA) 챔피언스리그(UCL) 우승으로 트레블을 달성했다. 펩 과르디올라 감독 아래 세계 최고라는 평가를 받고 있다.\n" +
                    "\n" +
                    "2위는 괴물 수비수 김민재였다. 김민재는 지난 시즌 이탈리아 세리에A 나폴리에서 최우수 수비상, 리그 우승컵까지 들어 올렸다. 나폴리의 33년 만에 우승은 김민재가 합류한 뒤에 이뤄졌다. 이후 뮌헨으로 이적해서도 김민재는 주전 센터백으로 자리매김했다.\n" +
                    "\n" +
                    "출처 : 아던트뉴스(https://www.ardentnews.co.kr)"
        val latitude = 37.48223625654306
        val longitude = 126.91468278697884
        val addressName = "서울특별시 관악구 난곡로 316"

        val findMember = memberRepository.findByEmail(member1Email) ?: return

        val location = PostLocation.createLocation(
            latitude = latitude,
            longitude = longitude,
            addressName = addressName,
        )
        postLocationRepository.save(location)

        val post = Post.createPost(
            category = null,
            member = findMember,
            content = content,
            location = location,
        )
        postRepository.save(post)
        val medias = listOf(PostMedia.createPostMedia(MediaType.IMAGE, "url1", post))
        postMediaRepository.saveAll(medias)

        // when
        val request = UpdatePostRequest(
            "new content",
            listOf(1L),
            null,
            PostLocationRequest(location.id, 37.47950249307712, 126.92237388636343, "서울특별시 관악구 남부순환로156길 39"),
            listOf(),
        )
        postService.update(post.id, request, findMember)
        val findPost = postService.findById(post.id)

        // then
        Assertions.assertThat(findPost.id).isEqualTo(post.id)
        Assertions.assertThat(findPost.location?.latitude).isEqualTo(37.47950249307712)
        Assertions.assertThat(findPost.location?.longitude).isEqualTo(126.92237388636343)
        Assertions.assertThat(findPost.location?.addressName).isEqualTo("서울특별시 관악구 남부순환로156길 39")
    }

}