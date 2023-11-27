package healthiee.rest.domain.workout.repository

import healthiee.rest.domain.workout.entity.WorkoutOfTheDay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutOfTheDayRepository : JpaRepository<WorkoutOfTheDay, Long>