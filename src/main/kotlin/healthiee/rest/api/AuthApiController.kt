package healthiee.rest.api

import healthiee.rest.dto.auth.AuthenticationDto
import healthiee.rest.dto.auth.request.AuthRequest
import healthiee.rest.dto.auth.request.CodeLoginRequest
import healthiee.rest.dto.auth.request.RegisterRequest
import healthiee.rest.dto.auth.response.AuthResponse
import healthiee.rest.dto.auth.response.VerifyCodeResponse
import healthiee.rest.lib.response.BaseResponse
import healthiee.rest.service.AuthService
import jakarta.servlet.http.Cookie
import jakarta.validation.Valid
import org.springframework.http.CacheControl.maxAge
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("v1/auth")
class AuthApiController(
    private val authService: AuthService,
) {

    @PostMapping("")
    fun auth(@Valid @RequestBody request: AuthRequest): ResponseEntity<BaseResponse<AuthResponse>> {
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                data = authService.auth(request),
            )
        )
    }

    @PostMapping("login")
    fun codeLogin(@Valid @RequestBody request: CodeLoginRequest): ResponseEntity<BaseResponse<AuthenticationDto>> {
        val authentication = authService.codeLogin(request)

        return ResponseEntity.ok()
            .headers {
                it.set("Set-Cookie", createCookie(authentication.refreshToken).toString())
            }
            .body(
                BaseResponse(
                    code = HttpStatus.OK.value(),
                    data = AuthenticationDto(authentication.memberId, authentication.token),
                )
            )
    }

    @GetMapping("verify/{code}")
    fun verifyCode(@PathVariable("code") code: UUID): ResponseEntity<BaseResponse<VerifyCodeResponse>> {
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                data = authService.verifyCode(code)
            )
        )
    }

    @PostMapping(
        value = ["register"],
        consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun register(
        @Valid @RequestPart("data") request: RegisterRequest,
        @RequestPart("image") image: MultipartFile?,
    ): ResponseEntity<BaseResponse<AuthenticationDto>> {
        val authentication = authService.register(request, image)

        return ResponseEntity.ok(
        ).headers {
            it.set("Set-Cookie", createCookie(authentication.refreshToken).toString())
        }
            .body(
                BaseResponse(
                    code = HttpStatus.OK.value(),
                    data = AuthenticationDto(authentication.memberId, authentication.token)
                )
            )
    }

    @PostMapping("refresh")
    fun refreshToken(
        @RequestHeader(value = "cookie") cookie: String,
    ): ResponseEntity<BaseResponse<AuthenticationDto>> {
        val refreshToken = cookie.substring(13)
        val authentication = authService.refreshToken(refreshToken)

        return ResponseEntity
            .ok()
            .headers {
                it.set("Set-Cookie", createCookie(authentication.refreshToken).toString())
            }
            .body(
                BaseResponse(
                    code = HttpStatus.OK.value(),
                    data = AuthenticationDto(authentication.memberId, authentication.token)
                )
            )
    }

    private fun createCookie(refreshToken: String): ResponseCookie {
        return ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
            .maxAge(REFRESH_TOKEN_MAX_AGE.toLong())
            .path("/")
//            .secure(true) // https
            .sameSite("None")
            .httpOnly(true)
            .build()
    }

    companion object {
        private const val REFRESH_TOKEN_KEY = "refreshToken"
        private const val REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 14 // 2주
    }

}