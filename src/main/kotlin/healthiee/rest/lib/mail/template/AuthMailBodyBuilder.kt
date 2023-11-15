package healthiee.rest.lib.mail.template

import healthiee.rest.lib.mail.model.MailBuilderParams
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Component
class AuthMailBodyBuilder(
    private val templateEngine: SpringTemplateEngine,
) : MailBuilder {

    override fun makeSubject(params: MailBuilderParams): String {
        return "[Healthiee] ${if (params.registered) "로그인" else "회원가입"}"
    }

    override fun makeBody(params: MailBuilderParams): String {
        val endpoint =
            if (params.registered) "email-login"
            else "authcompleted"
        val context = Context().apply {
            setVariable("url", "http://localhost:3000/${endpoint}?code=${params.code}")
            setVariable("text", if (params.registered) "로그인" else "회원가입")
        }
        return templateEngine.process("code", context)
    }

}