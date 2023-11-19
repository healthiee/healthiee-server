package healthiee.rest.domain.post.service

import healthiee.rest.domain.code.entity.Code
import healthiee.rest.domain.code.repository.CodeQueryRepository
import healthiee.rest.domain.code.repository.CodeRepository
import healthiee.rest.domain.common.entity.media.MediaType
import healthiee.rest.domain.follow.repository.FollowQueryRepository
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.post.dto.PostDto
import healthiee.rest.domain.post.dto.PostSearchCondition
import healthiee.rest.domain.post.dto.request.SavePostRequest
import healthiee.rest.domain.post.dto.request.SearchConditionRequest
import healthiee.rest.domain.post.dto.request.UpdatePostRequest
import healthiee.rest.domain.post.dto.response.SavePostResponse
import healthiee.rest.domain.post.entity.Post
import healthiee.rest.domain.post.entity.PostLike
import healthiee.rest.domain.post.entity.PostLocation
import healthiee.rest.domain.post.entity.PostMedia
import healthiee.rest.domain.post.repository.PostLikeQueryRepository
import healthiee.rest.domain.post.repository.PostLikeRepository
import healthiee.rest.domain.post.repository.PostLocationRepository
import healthiee.rest.domain.post.repository.PostMediaQueryRepository
import healthiee.rest.domain.post.repository.PostMediaRepository
import healthiee.rest.domain.post.repository.PostQueryRepository
import healthiee.rest.domain.post.repository.PostRepository
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.BAD_REQUEST
import healthiee.rest.lib.error.ErrorCode.FORBIDDEN
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
import healthiee.rest.lib.uploader.MediaDomainType
import healthiee.rest.lib.uploader.S3Uploader
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
    private val postMediaRepository: PostMediaRepository,
    private val codeRepository: CodeRepository,
    private val postLocationRepository: PostLocationRepository,
    private val postLikeRepository: PostLikeRepository,
    private val postQueryRepository: PostQueryRepository,
    private val codeQueryRepository: CodeQueryRepository,
    private val followQueryRepository: FollowQueryRepository,
    private val postLikeQueryRepository: PostLikeQueryRepository,
    private val postMediaQueryRepository: PostMediaQueryRepository,
    private val s3Uploader: S3Uploader,
) {

    @Transactional
    fun save(request: SavePostRequest, images: List<MultipartFile>, member: Member): SavePostResponse {
        if (images.isEmpty()) throw ApiException(BAD_REQUEST, "포스트 이미지를 넣어주세요")
        val categoryCode: Code? =
            request.categoryId?.let {
                codeRepository.findByIdOrNull(it) ?: throw ApiException(
                    NOT_FOUND,
                    "카테고리를 찾을 수 없습니다"
                )
            }

        val location: PostLocation? = request.location?.let {
            it.id?.let { id ->
                postLocationRepository.findByIdOrNull(id)
            } ?: PostLocation.createLocation(
                it.latitude,
                it.longitude,
                it.addressName,
            ).also { post -> postLocationRepository.save(post) }
        }

        val medias = mutableListOf<PostMedia>()
        images.forEach {
            if (!it.isEmpty) {
                val url = s3Uploader.upload(it, MediaType.IMAGE, MediaDomainType.POST)
                medias.add(PostMedia.createPostMedia(MediaType.IMAGE, url))
            }
        }

        postMediaRepository.saveAll(medias)

        val post = Post.createPost(
            category = categoryCode,
            member = member,
            content = request.content,
            location = location,
            postMedias = medias.toTypedArray()
        )
        postRepository.save(post)

        return SavePostResponse(post.id)
    }

    @Transactional
    fun update(postId: UUID, request: UpdatePostRequest, member: Member) {
        val post: Post = getValidPost(postId)
        validateUpdatePermission(post, member)

        val category: Code? =
            request.categoryId?.let {
                codeRepository.findByIdOrNull(it) ?: throw ApiException(
                    NOT_FOUND,
                    "카테고리를 찾을 수 없습니다"
                )
            }
        val location: PostLocation? = request.location?.let {
            it.id?.let { id ->
                postLocationRepository.findByIdOrNull(id)
                    ?.apply { changeLocation(it.latitude, it.longitude, it.addressName) }
            } ?: PostLocation.createLocation(
                it.latitude,
                it.longitude,
                it.addressName,
            ).also { post -> postLocationRepository.save(post) }
        }
        post.changeContent(category, location, request.content, request.mediaIds)
    }

    @Transactional
    fun delete(postId: UUID, member: Member) {
        val post: Post = getValidPost(postId)
        validateUpdatePermission(post, member)
        post.delete()
    }

    fun getPosts(
        pageable: Pageable,
        request: SearchConditionRequest,
        member: Member,
    ): Page<PostDto> {
        val categories: MutableList<Code> = mutableListOf()
        if (request.categoryIds.isNotEmpty()) {
            categories.addAll(codeQueryRepository.findAllByIds(request.categoryIds))
        }

        val targetMembers = followQueryRepository.findTargetMembersByMember(member.id)
        val searchCondition = PostSearchCondition(
            members = targetMembers,
            categories = categories,
        )

        return postQueryRepository.findAll(pageable, searchCondition).map {
            PostDto.create(it, it.postLikes.any { postLike -> postLike.member.id == member.id })
        }
    }

    @Transactional
    fun like(postId: UUID, member: Member) {
        val findPost = getValidPost(postId)
        val findPostLike = postLikeQueryRepository.findByMemberAndPost(member.id, postId)
        findPostLike?.let { throw ApiException(BAD_REQUEST, "이미 좋아요를 누른 이력이 있습니다") }
        postLikeRepository.save(PostLike.createPostLike(member, findPost))
        findPost.increaseLikeCount()
    }

    @Transactional
    fun cancelLike(postId: UUID, member: Member) {
        val findPost = getValidPost(postId)
        val findPostLike = postLikeQueryRepository.findByMemberAndPost(member.id, postId)
            ?: throw ApiException(NOT_FOUND, "좋아요를 누른 이력이 없습니다")
        findPostLike.delete()
        findPost.decreaseLikeCount()
    }

    fun findById(postId: UUID): Post {
        return getValidPost(postId)
    }

    private fun getValidPost(postId: UUID): Post {
        val validPost = postQueryRepository.findById(postId) ?: throw ApiException(NOT_FOUND, "게시물을 찾을 수 없습니다")
        validPost.updateMedias(postMediaQueryRepository.findByPostId(validPost.id))
        return validPost

    }

    private fun validateUpdatePermission(post: Post, member: Member) {
        if (post.member.id != member.id) throw ApiException(FORBIDDEN, "수정할 권한이 없습니다")
    }

}