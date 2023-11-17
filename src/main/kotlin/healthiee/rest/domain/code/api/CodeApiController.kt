package healthiee.rest.domain.code.api

import healthiee.rest.domain.code.dto.CodeDto
import healthiee.rest.domain.code.dto.request.SaveCodeRequest
import healthiee.rest.domain.code.dto.request.UpdateCodeRequest
import healthiee.rest.lib.response.BaseResponse
import healthiee.rest.domain.code.service.CodeService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/codes")
class CodeApiController(
    private val codeService: CodeService,
) {

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    fun save(@RequestBody @Valid request: SaveCodeRequest): ResponseEntity<BaseResponse<Any>> {
        codeService.save(request)
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                message = "코드 생성이 완료되었습니다"
            )
        )
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER')")
    fun findAll(): ResponseEntity<BaseResponse<List<CodeDto>>> {
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                data = codeService.findAll(),
            )
        )
    }

    @PutMapping("{codeId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(
        @PathVariable("codeId") codeId: Long,
        @RequestBody @Valid request: UpdateCodeRequest,
    ): ResponseEntity<BaseResponse<Any>> {
        codeService.update(codeId, request)
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                message = "코드 수정이 완료되었습니다"
            )
        )
    }

    @DeleteMapping("{codeId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable("codeId") codeId: Long): ResponseEntity<BaseResponse<Any>> {
        codeService.delete(codeId)
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                message = "코드 삭제가 완료되었습니다"
            )
        )
    }

}