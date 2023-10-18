package healthiee.rest.lib.response

import java.time.LocalDateTime

data class BaseResponse<T>(
    val code: Int,
    val data: T? = null,
    val message: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)