package healthiee.rest.dto.post

import healthiee.rest.domain.code.Code
import healthiee.rest.domain.member.Member

data class PostSearchCondition(
    val members: List<Member> = listOf(),
    val categories: List<Code> = listOf(),
)