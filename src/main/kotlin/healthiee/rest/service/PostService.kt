package healthiee.rest.service

import healthiee.rest.domain.code.Code
import healthiee.rest.domain.common.MediaType
import healthiee.rest.domain.member.Member
import healthiee.rest.domain.post.Post
import healthiee.rest.domain.post.PostLike
import healthiee.rest.domain.post.PostLocation
import healthiee.rest.domain.post.PostMedia
import healthiee.rest.dto.post.PostSearchCondition
import healthiee.rest.dto.post.PostSummaryDto
import healthiee.rest.dto.post.request.SavePostRequest
import healthiee.rest.dto.post.request.SearchConditionRequest
import healthiee.rest.dto.post.request.UpdatePostRequest
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ApplicationErrorCode.BAD_REQUEST_ALREADY_EXIST_LIKE_POST
import healthiee.rest.lib.error.ApplicationErrorCode.BAD_REQUEST_EMPTY_POST_IMAGES
import healthiee.rest.lib.error.ApplicationErrorCode.FORBIDDEN_NO_PERMISSION
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_CODE
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_POST
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_POST_LIKE
import healthiee.rest.lib.uploader.MediaDomainType
import healthiee.rest.lib.uploader.S3Uploader
import healthiee.rest.repository.code.CodeRepository
import healthiee.rest.repository.code.query.CodeQueryRepository
import healthiee.rest.repository.follow.query.FollowQueryRepository
import healthiee.rest.repository.post.PostLikeRepository
import healthiee.rest.repository.post.PostLocationRepository
import healthiee.rest.repository.post.PostMediaRepository
import healthiee.rest.repository.post.PostRepository
import healthiee.rest.repository.post.query.PostLikeQueryRepository
import healthiee.rest.repository.post.query.PostQueryRepository
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
    private val s3Uploader: S3Uploader,
) {

    @Transactional
    fun save(request: SavePostRequest, images: List<MultipartFile>, member: Member) {
        if (images.isEmpty()) throw ApiException(BAD_REQUEST_EMPTY_POST_IMAGES)
        val categoryCode: Code? =
            request.categoryId?.let { codeRepository.findByIdOrNull(it) ?: throw ApiException(NOT_FOUND_CODE) }

        val location: PostLocation? = request.location?.let {
            postLocationRepository.findByKakaoId(it.id)
                ?: PostLocation.createLocation(
                    it.id,
                    it.latitude,
                    it.longitude,
                    it.placeName,
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
    }

    @Transactional
    fun update(postId: UUID, request: UpdatePostRequest, member: Member) {
        val post: Post = getValidPost(postId)
        validateUpdatePermission(post, member)

        val category: Code? =
            request.categoryId?.let { codeRepository.findByIdOrNull(it) ?: throw ApiException(NOT_FOUND_CODE) }
        val location: PostLocation? = request.location?.let {
            postLocationRepository.findByKakaoId(it.id)
                ?: PostLocation.createLocation(
                    it.id,
                    it.latitude,
                    it.longitude,
                    it.placeName,
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

    fun findAll(
        pageable: Pageable,
        request: SearchConditionRequest,
        member: Member,
    ): Page<PostSummaryDto> {
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
            PostSummaryDto.create(it, it.postLikes.any { postLike -> postLike.member.id == member.id })
        }
    }

    @Transactional
    fun like(postId: UUID, member: Member) {
        val findPost = getValidPost(postId)
        val findPostLike = postLikeQueryRepository.findByMemberAndPost(member.id, postId)
        findPostLike?.let { throw ApiException(BAD_REQUEST_ALREADY_EXIST_LIKE_POST) }
        postLikeRepository.save(PostLike.createPostLike(member, findPost))
        findPost.increaseLikeCount()
    }

    @Transactional
    fun cancelLike(postId: UUID, member: Member) {
        val findPost = getValidPost(postId)
        val findPostLike = postLikeQueryRepository.findByMemberAndPost(member.id, postId)
            ?: throw ApiException(NOT_FOUND_POST_LIKE)
        findPostLike.delete()
        findPost.decreaseLikeCount()
    }

    fun findById(postId: UUID): Post {
        return getValidPost(postId)
    }

    fun getValidPost(postId: UUID): Post {
        return postQueryRepository.findById(postId) ?: throw ApiException(NOT_FOUND_POST)
    }

    private fun validateUpdatePermission(post: Post, member: Member) {
        if (post.member.id != member.id) throw ApiException(FORBIDDEN_NO_PERMISSION)
    }

}