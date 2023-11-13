package healthiee.rest.repository.code.query

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.code.Code
import healthiee.rest.domain.code.QCode.code
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class CodeQueryRepository : QuerydslRepositorySupport(Code::class.java) {

    fun findAllByIds(ids: List<Long>): List<Code> {
        return selectFrom(code)
            .where(idsIn(ids))
            .fetch()
    }

    private fun idsIn(ids: List<Long>): BooleanExpression? =
        if (ids.isEmpty()) null
        else code.id.`in`(ids)

}