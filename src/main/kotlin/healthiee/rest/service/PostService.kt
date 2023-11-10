package healthiee.rest.service

import healthiee.rest.domain.code.Code
import healthiee.rest.domain.common.MediaType
import healthiee.rest.domain.member.Member
import healthiee.rest.domain.post.Post
import healthiee.rest.domain.post.PostLocation
import healthiee.rest.domain.post.PostMedia
import healthiee.rest.dto.post.PostSummaryDto
import healthiee.rest.dto.post.request.SavePostRequest
import healthiee.rest.dto.post.toSummaryDto
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ApplicationErrorCode.BAD_REQUEST_EMPTY_POST_IMAGES
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_CODE
import healthiee.rest.lib.uploader.MediaDomainType
import healthiee.rest.lib.uploader.S3Uploader
import healthiee.rest.repository.code.CodeRepository
import healthiee.rest.repository.post.PostLocationRepository
import healthiee.rest.repository.post.PostMediaRepository
import healthiee.rest.repository.post.PostRepository
import healthiee.rest.repository.post.query.PostQueryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
    private val postMediaRepository: PostMediaRepository,
    private val codeRepository: CodeRepository,
    private val postLocationRepository: PostLocationRepository,
    private val postQueryRepository: PostQueryRepository,
    private val s3Uploader: S3Uploader,
) {

    @Transactional
    fun save(request: SavePostRequest, images: List<MultipartFile>, member: Member) {
        if (images.isEmpty()) throw ApiException(BAD_REQUEST_EMPTY_POST_IMAGES)
        val categoryCode: Code? =
            request.categoryId?.let { codeRepository.findByIdOrNull(it) ?: throw ApiException(NOT_FOUND_CODE) }

        val location: PostLocation? = request.location?.let {
            PostLocation.createLocation(
                it.id,
                it.latitude,
                it.longitude,
                it.placeName,
                it.addressName,
            )
        }?.also {
            postLocationRepository.save(it)
        }

        val medias = mutableListOf<PostMedia>()
        images.forEach {
            if (!it.isEmpty) {
                val url = s3Uploader.upload(it, MediaType.IMAGE, MediaDomainType.POST)
                medias.add(PostMedia.createPostMedia(MediaType.IMAGE, url))
            }
        }

        postMediaRepository.saveAll(medias)

        val post = Post.createPost(categoryCode, member, request.content, location, *medias.toTypedArray())
        postRepository.save(post)
    }

    fun findAll(): List<PostSummaryDto> {
        return postQueryRepository.findAll().map { it.toSummaryDto() }
    }

}