package healthiee.rest.repository.post

import healthiee.rest.domain.post.PostLocation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostLocationRepository : JpaRepository<PostLocation, Long>