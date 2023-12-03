package healthiee.rest.domain.workout.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class WorkoutDto(
    val workoutId: Long,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val workoutDate: LocalDateTime,
)