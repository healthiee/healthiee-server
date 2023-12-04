package healthiee.rest.domain.workout.service

import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.workout.dto.SearchCondition
import healthiee.rest.domain.workout.dto.WorkoutDto
import healthiee.rest.domain.workout.dto.response.GetWorkoutsResponse
import healthiee.rest.domain.workout.entity.WorkoutOfTheDay
import healthiee.rest.domain.workout.repository.WorkoutOfTheDayQueryRepository
import healthiee.rest.domain.workout.repository.WorkoutOfTheDayRepository
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode.BAD_REQUEST
import healthiee.rest.lib.error.ErrorCode.FORBIDDEN
import healthiee.rest.lib.error.ErrorCode.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.time.YearMonth

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

    @Transactional
    fun delete(workoutOfTheDayId: Long, member: Member) {
        val workout = validWorkout(workoutOfTheDayId)
        if (workout.member.id != member.id) {
            throw ApiException(FORBIDDEN, "삭제할 수 없습니다")
        }
        workout.delete()
    }

    fun getWorkouts(date: YearMonth, member: Member): GetWorkoutsResponse {
        val condition = SearchCondition(memberId = member.id, yearMonth = date)
        val findCountCondition = SearchCondition(memberId = member.id)
        val now = LocalDateTime.now()
        val workoutDate = LocalDateTime.of(now.year, now.monthValue, now.dayOfMonth, 0, 0, 0)
        val workedOutCondition = SearchCondition(memberId = member.id, workoutDate = workoutDate)

        val totalFindAll = queryRepository.findAll(findCountCondition)
        val findAll = queryRepository.findAll(condition)
        val workedOutFindAll = queryRepository.findAll(workedOutCondition)

        // 오운완 비율
        val totalDays = Duration.between(
            LocalDateTime.of(date.year, date.monthValue, 1, 0, 0, 0),
            LocalDateTime.of(date.year, date.monthValue, date.atEndOfMonth().dayOfMonth, 0, 0, 0)
        ).toDays()
        val rateForMonth = ((findAll.size / totalDays.toDouble()) * 100).toInt()

        return GetWorkoutsResponse(
            date = date,
            rateForMonth = rateForMonth,
            isWorkedOut = workedOutFindAll.isNotEmpty(),
            totalCount = totalFindAll.size,
            totalCountForMonth = totalDays.toInt(),
            workoutTotalCountForMonth = findAll.size,
            workouts = findAll.map { WorkoutDto(it.id, it.workoutDate) },
        )
    }

    private fun validWorkout(workoutOfTheDayId: Long): WorkoutOfTheDay {
        return queryRepository.findById(workoutOfTheDayId)
            ?: throw ApiException(NOT_FOUND, "오운완 이력이 없습니다")
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