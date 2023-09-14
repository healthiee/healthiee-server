package healthiee.rest.api.auth.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class RefreshTokenRequest(
    @field:NotNull(message = "The refreshToken is required")
    @JsonProperty("refreshToken")
    private val _refreshToken: String?,
) {
    val refreshToken: String
        get() = _refreshToken!!

}