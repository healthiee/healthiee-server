package healthiee.rest.domain.post.dto.request

data class SearchConditionRequest(
    val categoryIds: List<Long> = listOf(),
)