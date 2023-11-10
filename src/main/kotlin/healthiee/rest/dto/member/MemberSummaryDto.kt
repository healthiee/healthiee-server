package healthiee.rest.dto.member

import healthiee.rest.domain.member.Member
import java.util.*

data class MemberSummaryDto(
    val memberId: UUID,
    val email: String,
    val name: String,
    val nickname: String,
    val profileUrl: String?,
    val workouts: List<String>,
)


fun Member.toSummaryDto() = MemberSummaryDto(
    this.id,
    this.email,
    this.name,
    this.nickname,
    this.profileUrl,
    this.workoutHashtags.map { it.name },
)