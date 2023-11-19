package healthiee.rest.domain.member.dto

import healthiee.rest.domain.member.entity.Member
import java.util.*

abstract class MemberBaseDto {
    abstract val memberId: UUID
    abstract val email: String
    abstract val name: String
    abstract val nickname: String
    abstract val profileUrl: String?
}

data class MemberDefaultDto(
    override val memberId: UUID,
    override val email: String,
    override val name: String,
    override val nickname: String,
    override val profileUrl: String?,
) : MemberBaseDto()

data class MemberSummaryDto(
    override val memberId: UUID,
    override val email: String,
    override val name: String,
    override val nickname: String,
    override val profileUrl: String?,
    val workouts: List<String>,
) : MemberBaseDto()

data class MemberDto(
    override val memberId: UUID,
    override val email: String,
    override val name: String,
    override val nickname: String,
    val bio: String?,
    override val profileUrl: String?,
    val workouts: List<String>,
    val followingCount: Int,
    val followerCount: Int,
) : MemberBaseDto() {

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

fun Member.toSummaryDto() = MemberSummaryDto(
    this.id,
    this.email,
    this.name,
    this.nickname,
    this.profileUrl,
    this.workoutHashtags.map { it.name },
)

fun Member.toDefaultDto() = MemberDefaultDto(
    this.id,
    this.email,
    this.name,
    this.nickname,
    this.profileUrl,
)