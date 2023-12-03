package healthiee.rest.domain.workout.dto.response

import healthiee.rest.domain.workout.dto.WorkoutDto

data class GetWorkoutsResponse(
    val workouts: List<WorkoutDto>,
)