package healthiee.rest.domain.workout.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import healthiee.rest.domain.workout.dto.WorkoutDto
import org.springframework.format.annotation.DateTimeFormat
import java.time.YearMonth

data class GetWorkoutsResponse(
    @DateTimeFormat(pattern = "yyyy-MM")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "Asia/Seoul")
    val date: YearMonth,
    val totalCount: Int, // 오운완을 한 갯수
    val isWorkedOut: Boolean,
    val rateForMonth: Int,
    val totalCountForMonth: Int, // 그 달 일 갯수
    val workoutTotalCountForMonth: Int, // 그 달 동안 오운완을 한 갯수
    val workouts: List<WorkoutDto>,
)