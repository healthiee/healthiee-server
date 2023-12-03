package healthiee.rest.domain.workout.repository

import com.querydsl.core.types.dsl.BooleanExpression
import healthiee.rest.domain.member.entity.QMember.member
import healthiee.rest.domain.workout.dto.SearchCondition
import healthiee.rest.domain.workout.entity.QWorkoutOfTheDay.workoutOfTheDay
import healthiee.rest.domain.workout.entity.WorkoutOfTheDay
import healthiee.rest.lib.querydsl.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class WorkoutOfTheDayQueryRepository : QuerydslRepositorySupport(WorkoutOfTheDay::class.java) {

    fun findAll(searchCondition: SearchCondition): List<WorkoutOfTheDay> {
        return selectFrom(workoutOfTheDay)
            .where(
                memberIdEq(searchCondition.memberId),
                workoutDateEq(searchCondition.workoutDate),
                workoutMonthEq(searchCondition.month),
                deletedEq(false),
            )
            .fetch()
    }

    fun findById(workoutOfTheDayId: Long): WorkoutOfTheDay? {
        return selectFrom(workoutOfTheDay)
            .join(workoutOfTheDay.member, member)
            .where(deletedEq(false))
            .fetchJoin()
            .fetchOne()
    }

    private fun memberIdEq(memberId: UUID): BooleanExpression {
        return workoutOfTheDay.member.id.eq(memberId)
    }

    private fun workoutDateEq(workoutDate: LocalDateTime?): BooleanExpression? =
        if (workoutDate != null) workoutOfTheDay.workoutDate.eq(workoutDate)
        else null

    private fun workoutMonthEq(month: Int?): BooleanExpression? =
        if (month != null) workoutOfTheDay.workoutDate.month().eq(month)
        else null

    private fun deletedEq(deleted: Boolean): BooleanExpression {
        return workoutOfTheDay.deleted.eq(deleted)
    }

}