package healthiee.rest.lib.error

import org.springframework.http.HttpStatus

enum class ApplicationErrorCode(
    private val _httpStatus: HttpStatus,
    private val _message: String,
) : ErrorCode {
    // Not Found
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다"),
    NOT_FOUND_CODE(HttpStatus.NOT_FOUND, "코드를 찾을 수 없습니다"),
    NOT_FOUND_FOLLOW(HttpStatus.NOT_FOUND, "팔로우를 찾을 수 없습니다"),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "포스트를 찾을 수 없습니다"),
    NOT_FOUND_POST_LIKE(HttpStatus.NOT_FOUND, "포스트 좋아요 이력이 없습니다"),

    // Forbidden
    FORBIDDEN_INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않는 리프레쉬 토큰입니다"),
    FORBIDDEN_NO_PERMISSION(HttpStatus.FORBIDDEN, "수정할 권한이 없습니다"),

    // Bad Request
    BAD_REQUEST_ALREADY_EXIST_FOLLOW(HttpStatus.BAD_REQUEST, "이미 팔로우가 되어 있습니다"),
    BAD_REQUEST_ALREADY_EXIST_LIKE_POST(HttpStatus.BAD_REQUEST, "이미 좋아요가 되어 있습니다"),
    BAD_REQUEST_EMPTY_POST_IMAGES(HttpStatus.BAD_REQUEST, "포스트 이미지를 넣어주세요"),
    ;

    override val httpStatus: HttpStatus
        get() = _httpStatus
    override val message: String
        get() = _message
}