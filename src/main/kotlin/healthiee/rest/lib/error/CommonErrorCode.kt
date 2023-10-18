package healthiee.rest.lib.error

import org.springframework.http.HttpStatus

enum class CommonErrorCode(
    private val _httpStatus: HttpStatus,
    private val _message: String,
) : ErrorCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    INTERNAL_SERVER_ERROR(HttpStatus.BAD_REQUEST, "Internal server error"),
    ;

    override val httpStatus: HttpStatus
        get() = _httpStatus
    override val message: String
        get() = _message
}