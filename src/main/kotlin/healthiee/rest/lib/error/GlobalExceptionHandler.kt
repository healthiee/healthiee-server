package healthiee.rest.lib.error

import healthiee.rest.lib.response.BaseResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    // Exception Handler
    @ExceptionHandler(ApiException::class)
    fun handleException(e: ApiException): ResponseEntity<Any> {
        println("handleException: $e")
        return handleExceptionInternal(e.errorCode)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<Any> {
        println("handleIllegalArgument: $e")
        return handleExceptionInternal(CommonErrorCode.INVALID_PARAMETER, e.localizedMessage)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException): ResponseEntity<Any> {
        println("AccessDeniedException: $e")
        return handleExceptionInternal(CommonErrorCode.ACCESS_DENIED)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        println("handleMethodArgumentNotValid: $ex")
        return handleExceptionInternal(CommonErrorCode.INVALID_PARAMETER, ex)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllException(e: Exception): ResponseEntity<Any> {
        println("Exception: $e")
        return handleExceptionInternal(CommonErrorCode.INTERNAL_SERVER_ERROR)
    }

    private fun handleExceptionInternal(errorCode: ErrorCode): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus).body(makeErrorResponse(errorCode))
    }

    private fun handleExceptionInternal(errorCode: ErrorCode, message: String): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus).body(makeErrorResponse(errorCode, message))
    }

    private fun handleExceptionInternal(errorCode: ErrorCode, e: BindException): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus).body(makeErrorResponse(errorCode, e))
    }

    // Error Response
    private fun makeErrorResponse(errorCode: ErrorCode): BaseResponse<Any> {
        return BaseResponse(
            code = errorCode.httpStatus.value(),
            message = errorCode.message,
        )
    }

    private fun makeErrorResponse(errorCode: ErrorCode, message: String): BaseResponse<Any> {
        return BaseResponse(
            code = errorCode.httpStatus.value(),
            message = message,
        )
    }

    private fun makeErrorResponse(errorCode: ErrorCode, e: BindException): BaseResponse<Any> {
        return BaseResponse(
            code = errorCode.httpStatus.value(),
            message = e.fieldError?.defaultMessage ?: "[Global] Unknown Error Occurred",
        )
    }

}