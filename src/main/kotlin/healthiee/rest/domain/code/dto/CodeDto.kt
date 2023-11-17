package healthiee.rest.domain.code.dto

import healthiee.rest.domain.code.entity.Code

data class CodeDto(
    val codeId: Long,
    val type: Code.Type,
    val name: String,
)

fun Code.toDto() = CodeDto(id, type, name)