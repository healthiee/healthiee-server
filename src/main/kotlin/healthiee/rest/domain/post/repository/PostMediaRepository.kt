package healthiee.rest.domain.post.repository

import healthiee.rest.domain.post.entity.PostMedia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostMediaRepository : JpaRepository<PostMedia, Long>