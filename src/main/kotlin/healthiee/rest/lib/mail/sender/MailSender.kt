package healthiee.rest.lib.mail.sender

import healthiee.rest.lib.mail.model.MailSenderParams


interface MailSender {

    fun send(params: MailSenderParams)

}