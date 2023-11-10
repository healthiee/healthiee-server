package healthiee.rest.dto.post

import healthiee.rest.domain.code.Code

data class PostCategoryDto(
    val categoryId: Long,
    val name: String,
)

fun Code.toCategoryDto() = PostCategoryDto(this.id, this.name)