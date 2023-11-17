package healthiee.rest.domain.post.dto

import healthiee.rest.domain.code.entity.Code

data class PostCategoryDto(
    val categoryId: Long,
    val name: String,
)

fun Code.toCategoryDto() = PostCategoryDto(this.id, this.name)