package healthiee.rest.api.auth.dto.response

data class VerifyCodeResponse(
    val email: String,
    val registerToken: String,
)