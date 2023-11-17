package healthiee.rest.domain.post.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class PostLocationRequest(
    @JsonProperty("id")
    val id: Long? = null,
    @field:NotNull(message = "The latitude value must not be null")
    @JsonProperty("latitude")
    private val _latitude: Double?,
    @field:NotNull(message = "The longitude value must not be null")
    @JsonProperty("longitude")
    private val _longitude: Double?,
    @field:NotNull(message = "The addressName value must not be null")
    @JsonProperty("addressName")
    private val _addressName: String?,
) {

    val latitude: Double
        get() = _latitude!!

    val longitude: Double
        get() = _longitude!!

    val addressName: String
        get() = _addressName!!

}