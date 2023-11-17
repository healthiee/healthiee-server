package healthiee.rest.domain.post.repository

import healthiee.rest.domain.post.entity.PostLocation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostLocationRepository : JpaRepository<PostLocation, Long>