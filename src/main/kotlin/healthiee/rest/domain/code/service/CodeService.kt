package healthiee.rest.domain.code.service

import healthiee.rest.domain.code.dto.CodeDto
import healthiee.rest.domain.code.dto.request.SaveCodeRequest
import healthiee.rest.domain.code.dto.request.UpdateCodeRequest
import healthiee.rest.domain.code.dto.toDto
import healthiee.rest.domain.code.entity.Code
import healthiee.rest.domain.code.repository.CodeRepository
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CodeService(
    private val codeRepository: CodeRepository,
) {

    @Transactional
    fun save(request: SaveCodeRequest) {
        codeRepository.save(Code.createCode(request.type, request.name))
    }

    fun findAll(): List<CodeDto> {
        return codeRepository.findAllByActive(active = true).map { it.toDto() }
    }

    fun findById(id: Long): CodeDto {
        return codeRepository.findByIdOrNull(id)?.toDto()
            ?: throw ApiException(NOT_FOUND, "코드를 찾을 수 없습니다")
    }

    @Transactional
    fun update(id: Long, request: UpdateCodeRequest) {
        val findCode = codeRepository.findByIdOrNull(id) ?: throw ApiException(NOT_FOUND, "코드를 찾을 수 없습니다")
        findCode.changeCode(request.name, request.active)
    }

    @Transactional
    fun delete(id: Long) {
        codeRepository.deleteById(id)
    }

}