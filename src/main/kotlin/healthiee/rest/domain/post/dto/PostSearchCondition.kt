package healthiee.rest.domain.post.dto

import healthiee.rest.domain.code.entity.Code
import healthiee.rest.domain.member.entity.Member

data class PostSearchCondition(
    val members: List<Member> = listOf(),
    val categories: List<Code> = listOf(),
)