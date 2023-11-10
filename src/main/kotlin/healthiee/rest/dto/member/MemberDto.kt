package healthiee.rest.dto.member

import healthiee.rest.domain.member.Member
import java.util.*

data class MemberDto(
    val memberId: UUID,
    val email: String,
    val name: String,
    val nickname: String,
    val bio: String?,
    val profileUrl: String?,
    val workouts: List<String>,
    val followingCount: Int,
    val followerCount: Int,
) {

    data class Params(
        val member: Member,
        val followingCount: Int,
        val followerCount: Int,
    )

    companion object {
        fun create(params: Params) = MemberDto(
            params.member.id,
            params.member.email,
            params.member.name,
            params.member.nickname,
            params.member.bio,
            params.member.profileUrl,
            params.member.workoutHashtags.map { it.name },
            params.followingCount,
            params.followerCount,
        )
    }

}