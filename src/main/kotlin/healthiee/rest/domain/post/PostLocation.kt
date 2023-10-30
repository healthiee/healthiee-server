package healthiee.rest.domain.post

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "post_locations")
class PostLocation(
    _kakaoId: String,
    _latitude: Double,
    _longitude: Double,
    _placeName: String,
    _addressName: String,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    val id: Long = 0L

    @Column
    var kakaoId: String = _kakaoId
        private set

    @Column
    var latitude: Double = _latitude // x
        private set

    @Column
    var longitude: Double = _longitude // y
        private set

    @Column
    var placeName: String = _placeName
        private set

    @Column
    var addressName: String = _addressName
        private set

    companion object {
        fun createLocation(
            kakaoId: String,
            latitude: Double,
            longitude: Double,
            placeName: String,
            addressName: String,
        ) = PostLocation(kakaoId, latitude, longitude, placeName, addressName)
    }

    override fun toString(): String {
        return "PostLocation(id=$id, kakaoId='$kakaoId', latitude='$latitude', longitude='$longitude', placeName='$placeName', addressName='$addressName')"
    }

}