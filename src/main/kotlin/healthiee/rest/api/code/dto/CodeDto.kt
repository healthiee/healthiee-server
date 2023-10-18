package healthiee.rest.api.code.dto

import healthiee.rest.domain.code.Code

data class CodeDto(
    val id: Long,
    val type: Code.Type,
    val name: String,
)

fun Code.toDto() = CodeDto(id, type, name)