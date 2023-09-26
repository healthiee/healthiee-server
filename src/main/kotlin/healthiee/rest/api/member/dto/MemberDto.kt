package healthiee.rest.api.member.dto

import java.util.*

data class MemberDto(
    val id: UUID,
    val email: String,
    val name: String,
    val nickname: String,
    val bio: String?,
    val profileUrl: String?,
    val workouts: List<String>,
)