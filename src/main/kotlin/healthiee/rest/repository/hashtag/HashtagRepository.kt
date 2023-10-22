package healthiee.rest.repository.hashtag

import healthiee.rest.domain.hashtag.Hashtag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HashtagRepository : JpaRepository<Hashtag, Long> {

    fun findByName(name: String): Hashtag?

}