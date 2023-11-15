package healthiee.rest.lib.mail.model

import java.util.*

data class MailBuilderParams(
    val registered: Boolean,
    val code: UUID,
)