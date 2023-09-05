package healthiee.rest.api

import healthiee.rest.api.request.auth.AuthRequest
import healthiee.rest.api.request.auth.AuthResponse
import healthiee.rest.lib.email.MailService
import healthiee.rest.repository.MemberRepository
import healthiee.rest.service.EmailAuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthApiController(
    private val emailAuthService: EmailAuthService,
    private val mailService: MailService,
    private val memberRepository: MemberRepository,
) {

    @PostMapping("/api/v1/auth")
    fun auth(@RequestBody @Valid request: AuthRequest): ResponseEntity<AuthResponse> {
        val findMember = memberRepository.findByEmail(request.email)
        val registered = findMember != null

        emailAuthService.createAuthEmail(request.email)
        mailService.send(request.email, registered)

        return ResponseEntity.ok(AuthResponse(registered))
    }

}