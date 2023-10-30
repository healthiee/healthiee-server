package healthiee.rest.repository.code

import healthiee.rest.domain.code.Code
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CodeRepository : JpaRepository<Code, Long> {

    @Query("select c from Code c where c.active = :active")
    fun findAllByActive(active: Boolean): List<Code>

    fun findByName(name: String): Code?

}