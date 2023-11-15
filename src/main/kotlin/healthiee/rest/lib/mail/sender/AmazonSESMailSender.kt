package healthiee.rest.lib.mail.sender

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import healthiee.rest.lib.mail.model.MailSenderParams
import org.springframework.stereotype.Component

@Component
class AmazonSESMailSender(
    private val ses: AmazonSimpleEmailService,
) : MailSender {

    override fun send(params: MailSenderParams) {
        val mailRequest = SendEmailRequest()
            .withSource("no-reply@healthiee.net")
            .withDestination(Destination().withToAddresses(params.targetEmail))
            .withMessage(
                Message()
                    .withSubject(Content(params.subject).withCharset(Charsets.UTF_8.name()))
                    .withBody(Body().withHtml(Content(params.body)))
            )

        ses.sendEmail(mailRequest)
    }

}