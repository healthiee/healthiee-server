package healthiee.rest.domain.hashtag.repository

import healthiee.rest.domain.hashtag.entity.Hashtag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HashtagRepository : JpaRepository<Hashtag, Long> {

    fun findByName(name: String): Hashtag?

}