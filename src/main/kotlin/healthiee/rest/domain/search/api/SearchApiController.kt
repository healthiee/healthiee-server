package healthiee.rest.domain.search.api

import healthiee.rest.domain.common.dto.base.Response
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.post.dto.PostDto
import healthiee.rest.domain.search.dto.request.SearchKeywordRequest
import healthiee.rest.domain.search.service.SearchService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/search")
class SearchApiController(
    private val searchService: SearchService,
) {

    @GetMapping("")
    @PreAuthorize("hasRole('MEMBER') or hasRole('ADMIN')")
    fun searchKeyword(
        @Validated request: SearchKeywordRequest,
        pageable: Pageable,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Page<PostDto>>> {
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = searchService.searchKeyword(pageable, request, member)
            )
        )
    }

}