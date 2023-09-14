package healthiee.rest.api.base

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class BaseResponse<T>(
    val statusCode: Int,
    val data: T? = null,
    val errorMessage: String? = null,
) {
    companion object {
        fun <T> ok(data: T): ResponseEntity<BaseResponse<T>> {
            return ResponseEntity.ok(BaseResponse(statusCode = 200, data = data))
        }

        fun <T> badRequest(errorMessage: String?): ResponseEntity<BaseResponse<T>> {
            val body = BaseResponse<T>(statusCode = 400, errorMessage = errorMessage)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
        }

        fun <T> unauthorized(errorMessage: String?): ResponseEntity<BaseResponse<T>> {
            val body = BaseResponse<T>(statusCode = 401, errorMessage = errorMessage)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body)
        }

        fun <T> forbidden(errorMessage: String?): ResponseEntity<BaseResponse<T>> {
            val body = BaseResponse<T>(statusCode = 403, errorMessage = errorMessage)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body)
        }

        fun <T> notFound(errorMessage: String?): ResponseEntity<BaseResponse<T>> {
            val body = BaseResponse<T>(statusCode = 404, errorMessage = errorMessage)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
        }

        fun <T> internalServerError(errorMessage: String?): ResponseEntity<BaseResponse<T>> {
            val body = BaseResponse<T>(statusCode = 500, errorMessage = errorMessage)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
        }
    }

}