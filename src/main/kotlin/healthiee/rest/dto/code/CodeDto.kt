package healthiee.rest.dto.code

import healthiee.rest.domain.code.Code

data class CodeDto(
    val codeId: Long,
    val type: Code.Type,
    val name: String,
)

fun Code.toDto() = CodeDto(id, type, name)