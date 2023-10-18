package healthiee.rest.lib.error

import org.springframework.http.HttpStatus

interface ErrorCode {
    val name: String
    val httpStatus: HttpStatus
    val message: String
}