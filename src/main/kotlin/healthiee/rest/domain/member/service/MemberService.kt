package healthiee.rest.domain.member.service

import healthiee.rest.domain.common.entity.media.MediaType
import healthiee.rest.domain.follow.service.FollowService
import healthiee.rest.domain.hashtag.entity.Hashtag
import healthiee.rest.domain.hashtag.repository.HashtagRepository
import healthiee.rest.domain.member.dto.MemberDefaultDto
import healthiee.rest.domain.member.dto.MemberDto
import healthiee.rest.domain.member.dto.MemberSearchCondition
import healthiee.rest.domain.member.dto.request.UpdateMemberRequest
import healthiee.rest.domain.member.dto.toDefaultDto
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.member.repository.MemberQueryRepository
import healthiee.rest.domain.member.repository.MemberRepository
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode
import healthiee.rest.lib.error.ErrorCode.FORBIDDEN
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
import healthiee.rest.lib.uploader.MediaDomainType
import healthiee.rest.lib.uploader.S3Uploader
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberQueryRepository: MemberQueryRepository,
    private val hashtagRepository: HashtagRepository,
    private val followService: FollowService,
    private val s3Uploader: S3Uploader,
    private val passwordEncoder: PasswordEncoder
) {

    fun getMember(id: UUID): MemberDto {
        val findMember = validMember(id)

        return MemberDto.create(
            MemberDto.Params(
                findMember,
                followService.getFollowingCount(findMember),
                followService.getFollowerCount(findMember),
            )
        )
    }

    fun getMembers(pageable: Pageable, searchCondition: MemberSearchCondition): Page<MemberDefaultDto> {
        return memberQueryRepository.findAll(pageable, searchCondition).map { it.toDefaultDto() }
    }

    @Transactional
    fun updateMember(memberId: UUID, request: UpdateMemberRequest, image: MultipartFile?, member: Member) {
        if (memberId != member.id) {
            throw ApiException(FORBIDDEN, "멤버 정보를 수정할 권한이 없습니다")
        }
        val findMember = validMember(memberId)
        if (findMember.nickname != request.nickname) {
            validateExistNickname(request.nickname)
        }

        var profileUrl: String? = null
        if (image != null && !image.isEmpty) {
            profileUrl = s3Uploader.upload(image, MediaType.IMAGE, MediaDomainType.PROFILE_IMAGE)
        }

        val workoutHashtags = mutableListOf<Hashtag>()
        if (request.workouts != null) {
            val newHashtags = mutableListOf<Hashtag>()
            request.workouts.forEach {
                hashtagRepository.findByName(it)?.let { hashtag ->
                    workoutHashtags.add(hashtag)
                } ?: run {
                    newHashtags.add(Hashtag.createHashtag(it))
                }
            }
            hashtagRepository.saveAll(newHashtags)
            workoutHashtags.addAll(newHashtags)
        }

        findMember.changeMember(
            request.name,
            request.nickname,
            request.bio,
            profileUrl,
            workoutHashtags,
            passwordEncoder.encode(request.nickname),
        )
    }

    @Transactional
    fun drop(member: Member) {
        val findMember = validMember(member.id)
        findMember.delete()
    }

    private fun validMember(memberId: UUID): Member {
        return memberRepository.findByIdOrNull(memberId) ?: throw ApiException(NOT_FOUND, "멤버 정보를 찾을 수 없습니다")
    }

    private fun validateExistNickname(nickname: String) {
        if (memberRepository.findByNickname(nickname) != null) {
            throw ApiException(ErrorCode.BAD_REQUEST, "이미 존재하는 닉네임입니다")
        }
    }

}