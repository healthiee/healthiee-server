package healthiee.rest.domain.post.dto

import healthiee.rest.domain.post.entity.PostLocation

data class PostLocationDto(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val addressName: String,
)

fun PostLocation.toDto() = PostLocationDto(
    id,
    latitude,
    longitude,
    addressName,
)