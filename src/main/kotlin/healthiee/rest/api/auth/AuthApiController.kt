package healthiee.rest.api.auth

import healthiee.rest.api.auth.dto.AuthenticationDto
import healthiee.rest.api.auth.dto.request.AuthRequest
import healthiee.rest.api.auth.dto.request.CodeLoginRequest
import healthiee.rest.api.auth.dto.request.RefreshTokenRequest
import healthiee.rest.api.auth.dto.request.RegisterRequest
import healthiee.rest.api.auth.dto.response.AuthResponse
import healthiee.rest.api.auth.dto.response.VerifyCodeResponse
import healthiee.rest.lib.response.BaseResponse
import healthiee.rest.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AuthApiController(
    private val authService: AuthService,
) {

    @PostMapping("/v1/auth")
    fun auth(@Valid @RequestBody request: AuthRequest): ResponseEntity<BaseResponse<AuthResponse>> {
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                data = authService.auth(request),
            )
        )
    }

    @PostMapping("/v1/auth/login")
    fun codeLogin(@Valid @RequestBody request: CodeLoginRequest): ResponseEntity<BaseResponse<AuthenticationDto>> {
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                data = authService.codeLogin(request)
            )
        )
    }

    @GetMapping("/v1/auth/verify/{code}")
    fun verifyCode(@PathVariable("code") code: UUID): ResponseEntity<BaseResponse<VerifyCodeResponse>> {
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                data = authService.verifyCode(code)
            )
        )
    }

    @PostMapping("/v1/auth/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<BaseResponse<AuthenticationDto>> {
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                data = authService.register(request)
            )
        )
    }

    @PostMapping("/v1/auth/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<BaseResponse<AuthenticationDto>> {
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                data = authService.refreshToken(request)
            )
        )
    }

}