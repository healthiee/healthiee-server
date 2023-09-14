package healthiee.rest.api.base

import org.springframework.http.ResponseEntity

sealed class ServiceResponse<out T : Any> {
    data class Success<out T : Any>(val date: T) : ServiceResponse<T>()
    data class Failure(val type: FailureType) : ServiceResponse<Nothing>()
}

enum class FailureType(val message: String) {
    /** 403 */
    INVALID_REGISTER_TOKEN("유효하지 않는 회원가입 토큰입니다."),
    INVALID_REFRESH_TOKEN("유효하지 않는 리프레쉬 토큰입니다."),

    /** 404 */
    NOT_FOUND_CODE("코드를 찾을 수 없습니다."),
    NOT_FOUND_MEMBER("멤버를 찾을 수 없습니다."),
    NOT_FOUND_AUTH_TOKEN("인증 토큰을 찾을 수 없습니다."),
}

fun <T> FailureType.toResponse(): ResponseEntity<BaseResponse<T>> {
    return when (this) {
        /** 403 */
        FailureType.INVALID_REGISTER_TOKEN,
        FailureType.INVALID_REFRESH_TOKEN,
        -> BaseResponse.forbidden(this.message)

        /** 404 */
        FailureType.NOT_FOUND_CODE,
        FailureType.NOT_FOUND_MEMBER,
        FailureType.NOT_FOUND_AUTH_TOKEN,
        -> BaseResponse.notFound(this.message)
    }
}