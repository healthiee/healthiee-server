package healthiee.rest.domain.common.dto.base

import java.time.LocalDateTime

data class Response<T>(
    val code: Int,
    val data: T? = null,
    val message: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)