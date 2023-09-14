package healthiee.rest.api.auth

import healthiee.rest.api.auth.dto.AuthenticationDto
import healthiee.rest.api.auth.dto.request.AuthRequest
import healthiee.rest.api.auth.dto.request.CodeLoginRequest
import healthiee.rest.api.auth.dto.request.RefreshTokenRequest
import healthiee.rest.api.auth.dto.request.RegisterRequest
import healthiee.rest.api.auth.dto.response.AuthResponse
import healthiee.rest.api.auth.dto.response.VerifyCodeResponse
import healthiee.rest.api.base.BaseResponse
import healthiee.rest.api.base.ServiceResponse
import healthiee.rest.api.base.toResponse
import healthiee.rest.service.AuthService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AuthApiController(
    @Autowired private val authService: AuthService,
) {

    @PostMapping("/v1/auth")
    fun auth(@Valid @RequestBody request: AuthRequest): ResponseEntity<BaseResponse<AuthResponse>> {
        return BaseResponse.ok(authService.auth(request))
    }

    @PostMapping("/v1/auth/login")
    fun codeLogin(@Valid @RequestBody request: CodeLoginRequest): ResponseEntity<BaseResponse<AuthenticationDto>> {
        return when (val response = authService.codeLogin(request)) {
            is ServiceResponse.Success -> BaseResponse.ok(response.date)
            is ServiceResponse.Failure -> response.type.toResponse()
        }
    }

    @GetMapping("/v1/auth/verify/{code}")
    fun verifyCode(@PathVariable("code") code: UUID): ResponseEntity<BaseResponse<VerifyCodeResponse>> {
        return when (val response = authService.verifyCode(code)) {
            is ServiceResponse.Success -> BaseResponse.ok(response.date)
            is ServiceResponse.Failure -> response.type.toResponse()
        }
    }

    @PostMapping("/v1/auth/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<BaseResponse<AuthenticationDto>> {
        return when (val response = authService.register(request)) {
            is ServiceResponse.Success -> BaseResponse.ok(response.date)
            is ServiceResponse.Failure -> response.type.toResponse()
        }
    }

    @PostMapping("/v1/auth/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<BaseResponse<AuthenticationDto>> {
        return when (val response = authService.refreshToken(request)) {
            is ServiceResponse.Success -> BaseResponse.ok(response.date)
            is ServiceResponse.Failure -> response.type.toResponse()
        }
    }

}