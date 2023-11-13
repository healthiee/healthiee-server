package healthiee.rest.dto.post

import healthiee.rest.domain.post.PostLocation

data class PostLocationDto(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val placeName: String,
    val addressName: String,
)

fun PostLocation.toDto() = PostLocationDto(
    kakaoId,
    latitude,
    longitude,
    placeName,
    addressName
)