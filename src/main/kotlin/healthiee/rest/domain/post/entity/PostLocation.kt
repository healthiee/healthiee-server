package healthiee.rest.domain.post.entity

import healthiee.rest.domain.common.entity.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "post_locations")
class PostLocation(
    _latitude: Double,
    _longitude: Double,
    _addressName: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    val id: Long = 0L

    @Column
    var latitude: Double = _latitude // x, 위도
        private set

    @Column
    var longitude: Double = _longitude // y, 경도
        private set

    @Column
    var addressName: String = _addressName
        private set

    companion object {
        fun createLocation(
            latitude: Double,
            longitude: Double,
            addressName: String,
        ) = PostLocation(latitude, longitude, addressName)
    }

    fun changeLocation(
        latitude: Double,
        longitude: Double,
        addressName: String,
    ) {
        this.latitude = latitude
        this.longitude = longitude
        this.addressName = addressName
    }

    override fun toString(): String {
        return "PostLocation(id=$id, latitude='$latitude', longitude='$longitude', addressName='$addressName')"
    }

}