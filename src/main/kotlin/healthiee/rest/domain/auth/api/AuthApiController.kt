package healthiee.rest.domain.auth.api

import healthiee.rest.domain.auth.dto.AuthenticationDto
import healthiee.rest.domain.auth.dto.request.AuthRequest
import healthiee.rest.domain.auth.dto.request.CodeLoginRequest
import healthiee.rest.domain.auth.dto.request.RegisterRequest
import healthiee.rest.domain.auth.dto.response.AuthResponse
import healthiee.rest.domain.auth.dto.response.VerifyCodeResponse
import healthiee.rest.domain.auth.service.AuthService
import healthiee.rest.domain.common.dto.base.Response
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.BAD_REQUEST
import jakarta.validation.Valid
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
    fun auth(@Valid @RequestBody request: AuthRequest): ResponseEntity<Response<AuthResponse>> {
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = authService.auth(request),
            )
        )
    }

    @PostMapping("login")
    fun codeLogin(@Valid @RequestBody request: CodeLoginRequest): ResponseEntity<Response<AuthenticationDto>> {
        val authentication = authService.codeLogin(request)

        return ResponseEntity.ok()
            .headers {
                it.set("Set-Cookie", createCookie(authentication.refreshToken).toString())
            }
            .body(
                Response(
                    code = HttpStatus.OK.value(),
                    data = AuthenticationDto(authentication.memberId, authentication.token),
                )
            )
    }

    @PostMapping("logout")
    fun logout(): ResponseEntity<Response<Any>> {
        return ResponseEntity.ok()
            .headers { it.set("Set-Cookie", createEmptyCookie().toString()) }
            .body(
                Response(
                    code = HttpStatus.OK.value(),
                    message = "로그아웃이 완료되었습니다",
                )
            )
    }

    @GetMapping("verify/{code}")
    fun verifyCode(@PathVariable("code") code: UUID): ResponseEntity<Response<VerifyCodeResponse>> {
        return ResponseEntity.ok(
            Response(
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
    ): ResponseEntity<Response<AuthenticationDto>> {
        val authentication = authService.register(request, image)

        return ResponseEntity.ok()
            .headers {
                it.set("Set-Cookie", createCookie(authentication.refreshToken).toString())
            }
            .body(
                Response(
                    code = HttpStatus.OK.value(),
                    data = AuthenticationDto(authentication.memberId, authentication.token)
                )
            )
    }

    @PostMapping("refresh")
    fun refreshToken(
        @RequestHeader(value = "cookie") cookie: String,
    ): ResponseEntity<Response<AuthenticationDto>> {
        val refreshToken = cookie.substring(13)
        if (refreshToken.trim().isEmpty()) {
            throw ApiException(BAD_REQUEST, "유효하지 않는 토큰입니다")
        }
        val authentication = authService.refreshToken(refreshToken)

        return ResponseEntity
            .ok()
            .headers {
                it.set("Set-Cookie", createCookie(authentication.refreshToken).toString())
            }
            .body(
                Response(
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

    private fun createEmptyCookie(): ResponseCookie {
        return ResponseCookie.from(REFRESH_TOKEN_KEY)
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