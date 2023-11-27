package healthiee.rest.domain.workout.service

import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.workout.dto.SearchCondition
import healthiee.rest.domain.workout.entity.WorkoutOfTheDay
import healthiee.rest.domain.workout.repository.WorkoutOfTheDayQueryRepository
import healthiee.rest.domain.workout.repository.WorkoutOfTheDayRepository
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.BAD_REQUEST
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class WorkoutOfTheDayService(
    private val repository: WorkoutOfTheDayRepository,
    private val queryRepository: WorkoutOfTheDayQueryRepository,
) {

    @Transactional
    fun create(member: Member): Long {
        validateExistWorkoutHistory(member)

        val workoutOfTheDay = WorkoutOfTheDay.createWorkoutOfTheDay(member)
        repository.save(workoutOfTheDay)

        return workoutOfTheDay.id
    }

    private fun validateExistWorkoutHistory(member: Member) {
        val now = LocalDateTime.now()
        val workoutDate = LocalDateTime.of(now.year, now.monthValue, now.dayOfMonth, 0, 0, 0)
        val findAll = queryRepository.findAll(SearchCondition(member.id, workoutDate))
        if (findAll.isNotEmpty()) {
            throw ApiException(BAD_REQUEST, "오운완은 하루에 한번씩만 가능합니다")
        }
    }

}