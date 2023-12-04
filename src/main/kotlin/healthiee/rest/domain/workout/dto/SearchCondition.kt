package healthiee.rest.domain.workout.dto

import java.time.LocalDateTime
import java.time.YearMonth
import java.util.*

data class SearchCondition(
    val memberId: UUID,
    val workoutDate: LocalDateTime? = null, // 특정날짜와 일치
    val yearMonth: YearMonth? = null, // 특정 연(년) & 월(달)과 일치
)