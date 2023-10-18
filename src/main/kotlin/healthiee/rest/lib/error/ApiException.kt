package healthiee.rest.lib.error

data class ApiException(
    val errorCode: ErrorCode,
) : RuntimeException()