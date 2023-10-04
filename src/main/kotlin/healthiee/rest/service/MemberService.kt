package healthiee.rest.service

import healthiee.rest.api.base.FailureType
import healthiee.rest.api.base.ServiceResponse
import healthiee.rest.api.member.dto.MemberDto
import healthiee.rest.domain.member.toDto
import healthiee.rest.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class MemberService(
    @Autowired val memberRepository: MemberRepository,
) {

    fun getInfo(id: UUID): ServiceResponse<MemberDto> {
        val findMember =
            memberRepository.findByIdOrNull(id) ?: return ServiceResponse.Failure(FailureType.NOT_FOUND_MEMBER)
        return ServiceResponse.Success(findMember.toDto())
    }

}