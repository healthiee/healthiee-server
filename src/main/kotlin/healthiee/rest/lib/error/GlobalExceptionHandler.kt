package healthiee.rest.lib.error

import healthiee.rest.domain.common.dto.base.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // Exception Handler
    /**
     * Developer Custom Exception: 직접 정의한 ApiException 에러 클래스에 대한 예외 처리
     */
    @ExceptionHandler(ApiException::class)
    fun handleException(e: ApiException): ResponseEntity<Any> {
        println("handleException: $e")
        return handleExceptionInternal(e)
    }

    /**
     * MethodArgumentNotValidException(BindingResult 처리)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        println("handleMethodArgumentNotValid: $ex")
        val stringBuilder = StringBuilder()
        ex.bindingResult.fieldErrors.forEach {
            stringBuilder.append("[")
            stringBuilder.append(it.field)
            stringBuilder.append("] parameter is not valid(")
            stringBuilder.append(it.defaultMessage)
            stringBuilder.append(") rejected value=")
            stringBuilder.append(it.rejectedValue)
        }

        return handleExceptionInternal(ApiException(ErrorCode.INVALID_PARAMETER, stringBuilder.toString()))
    }

    @ExceptionHandler(Exception::class)
    fun handleAllException(e: Exception): ResponseEntity<Any> {
        println("Exception: $e")
        return handleExceptionInternal(ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "[예상치 못한 오류가 발생] 관리자에게 문의해주세요"))
    }

    private fun handleExceptionInternal(exception: ApiException): ResponseEntity<Any> {
        return ResponseEntity
            .status(exception.errorCode.httpStatus)
            .body(makeErrorResponse(exception.errorCode, exception.errorMessage))
    }

    // Error Response
    private fun makeErrorResponse(errorCode: ErrorCode, message: String? = null): Response<Any> {
        return Response(
            code = errorCode.httpStatus.value(),
            message = message ?: errorCode.defaultMessage,
        )
    }
}