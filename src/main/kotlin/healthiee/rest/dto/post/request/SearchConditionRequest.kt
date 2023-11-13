package healthiee.rest.dto.post.request

data class SearchConditionRequest(
    val categoryIds: List<Long> = listOf(),
)