package healthiee.rest.lib.mail.template

import healthiee.rest.lib.mail.model.MailBuilderParams

interface MailBuilder {

    fun makeSubject(params: MailBuilderParams): String
    fun makeBody(params: MailBuilderParams): String

}