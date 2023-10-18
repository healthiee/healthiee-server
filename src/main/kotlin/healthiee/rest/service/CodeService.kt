package healthiee.rest.service

import healthiee.rest.api.code.dto.CodeDto
import healthiee.rest.api.code.dto.request.SaveCodeRequest
import healthiee.rest.api.code.dto.request.UpdateCodeRequest
import healthiee.rest.api.code.dto.toDto
import healthiee.rest.domain.code.Code
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ApplicationErrorCode.NOT_FOUND_CODE
import healthiee.rest.repository.code.CodeRepository
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
            ?: throw ApiException(NOT_FOUND_CODE)
    }

    @Transactional
    fun update(id: Long, request: UpdateCodeRequest) {
        val findCode = codeRepository.findByIdOrNull(id) ?: throw ApiException(NOT_FOUND_CODE)
        findCode.changeCode(request.name, request.active)
    }

    @Transactional
    fun delete(id: Long) {
        codeRepository.deleteById(id)
    }

}