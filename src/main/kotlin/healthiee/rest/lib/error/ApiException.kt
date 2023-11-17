package healthiee.rest.lib.error

data class ApiException(
    val errorCode: ErrorCode,
    val errorMessage: String? = null,
) : RuntimeException()