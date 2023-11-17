package healthiee.rest.lib.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val defaultMessage: String,
) {
    /*
     * 400 BAD_REQUEST: 잘못된 요청
     * 400 INVALID_PARAMETER: 파라미터 검증 오류
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),

    /*
     * 401 UNAUTHORIZED: 인증되지 않은 사용자의 요청
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized request"),

    /*
    * 403 FORBIDDEN: 권한이 없는 사용자의 요청
    */
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden request"),

    /*
     * 404 NOT_FOUND: 리소스를 찾을 수 없음
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"),

    /*
     * 405 METHOD_NOT_ALLOWED: 허용되지 않은 Request Method 호출
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Not allowed method"),

    /*
     * 500 INTERNAL_SERVER_ERROR: 내부 서버 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
}