package healthiee.rest.lib.exception

import healthiee.rest.api.base.BaseResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.stream.Collectors

@ControllerAdvice
@Suppress("UNCHECKED_CAST")
class ExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult
            .fieldErrors
            .stream()
            .map { it.defaultMessage }
            .collect(Collectors.toList())

        return BaseResponse.badRequest<Any>(errors.joinToString(", ")) as ResponseEntity<Any>
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        return BaseResponse.internalServerError<Any>("알 수 없는 서버 오류 발생") as ResponseEntity<Any>
    }

}