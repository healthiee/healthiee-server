package healthiee.rest.lib.email

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class MailService(
    private val templateEngine: SpringTemplateEngine,
    private val ses: AmazonSimpleEmailService,
) {

    fun send(to: String, registered: Boolean) {
        val code = generateRandomCode()

        val context = Context()
        context.setVariable("code", code)

        val body = Content(templateEngine.process("code", context))
        val subjectContent = "[Healthiee] ${if(registered) "로그인" else "회원가입"}"

        val request = SendEmailRequest()
            .withSource("no-reply@healthiee.net")
            .withDestination(Destination().withToAddresses(to))
            .withMessage(
                Message()
                    .withSubject(Content(subjectContent).withCharset(Charsets.UTF_8.name()))
                    .withBody(Body().withHtml(body))
            )

        ses.sendEmail(request)
    }

    private fun generateRandomCode(): String {
        return (1..5).map { (0..9).random() }.joinToString("")
    }

}