package healthiee.rest.domain.workout.dto

import java.time.LocalDateTime
import java.util.*

data class SearchCondition(
    val memberId: UUID,
    val workoutDate: LocalDateTime,
)