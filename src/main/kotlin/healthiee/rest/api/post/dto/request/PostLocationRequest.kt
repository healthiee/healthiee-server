package healthiee.rest.api.post.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class PostLocationRequest(
    @field:NotNull(message = "The id value must not be null")
    @JsonProperty("id")
    private val _id: String?,
    @field:NotNull(message = "The latitude value must not be null")
    @JsonProperty("latitude")
    private val _latitude: Double?,
    @field:NotNull(message = "The longitude value must not be null")
    @JsonProperty("longitude")
    private val _longitude: Double?,
    @field:NotNull(message = "The placeName value must not be null")
    @JsonProperty("placeName")
    private val _placeName: String?,
    @field:NotNull(message = "The addressName value must not be null")
    @JsonProperty("addressName")
    private val _addressName: String?,
) {

    val id: String
        get() = _id!!

    val latitude: Double
        get() = _latitude!!

    val longitude: Double
        get() = _longitude!!

    val placeName: String
        get() = _placeName!!

    val addressName: String
        get() = _addressName!!

}