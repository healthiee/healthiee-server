package healthiee.rest.domain.auth.repository

import healthiee.rest.domain.auth.entity.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TokenRepository : JpaRepository<Token, UUID> {

    @Query("select t from Token t join fetch t.member where t.id = :id")
    fun findByIdIncludeMember(@Param("id") id: UUID): Token?

}