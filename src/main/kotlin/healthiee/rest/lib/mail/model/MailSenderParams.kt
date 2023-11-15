package healthiee.rest.lib.mail.model

data class MailSenderParams(
    val targetEmail: String,
    val subject: String,
    val body: String,
)