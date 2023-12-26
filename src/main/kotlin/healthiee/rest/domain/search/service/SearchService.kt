package healthiee.rest.domain.search.service

import healthiee.rest.domain.hashtag.repository.HashtagRepository
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.post.dto.PostDto
import healthiee.rest.domain.post.repository.PostQueryRepository
import healthiee.rest.domain.search.dto.request.SearchKeywordRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = false)
class SearchService(
    private val postQueryRepository: PostQueryRepository,
    private val hashtagRepository: HashtagRepository,
) {
    fun searchKeyword(pageable: Pageable, request: SearchKeywordRequest, member: Member): Page<PostDto> {
        val hashtag = hashtagRepository.findByName(request.keyword!!)

        return postQueryRepository.findByHashtag(pageable, request.keyword, hashtag)
            .map { PostDto.create(it, it.postLikes.any { postLike -> postLike.member.id == member.id }) }
    }
}