package healthiee.rest.domain.member.repository

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.member.dto.MemberSearchCondition
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.member.entity.QMember.member
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class MemberQueryRepository : QuerydslRepositorySupport(Member::class.java) {

    fun findAll(pageable: Pageable, searchCondition: MemberSearchCondition): Page<Member> {
        return applyPagination(
            pageable,
            {
                it.selectFrom(member)
                    .where(nicknameLike(searchCondition.nickname))
                    .offset(pageable.offset)
                    .limit(pageable.pageSize.toLong())
                    .orderBy(member.createdDate.desc())
            }, {
                it.select(member.count())
                    .from(member)
                    .where(nicknameLike(searchCondition.nickname))
            }
        )
    }

    private fun nicknameLike(nickname: String): BooleanExpression =
        member.nickname.contains(nickname)

}