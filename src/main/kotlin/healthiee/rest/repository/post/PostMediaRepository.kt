package healthiee.rest.repository.post

import healthiee.rest.domain.post.PostMedia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostMediaRepository : JpaRepository<PostMedia, Long>