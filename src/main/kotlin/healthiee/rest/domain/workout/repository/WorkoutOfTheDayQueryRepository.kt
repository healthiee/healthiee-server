package healthiee.rest.domain.workout.repository

import com.querydsl.core.types.dsl.BooleanExpression
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
                deletedEq(false),
            )
            .fetch()
    }

    private fun memberIdEq(memberId: UUID): BooleanExpression {
        return workoutOfTheDay.member.id.eq(memberId)
    }

    private fun workoutDateEq(workoutDate: LocalDateTime): BooleanExpression {
        return workoutOfTheDay.workoutDate.eq(workoutDate)
    }

    private fun deletedEq(deleted: Boolean): BooleanExpression {
        return workoutOfTheDay.deleted.eq(deleted)
    }

}