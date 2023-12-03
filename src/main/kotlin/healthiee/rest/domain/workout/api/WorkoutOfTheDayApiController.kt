package healthiee.rest.domain.workout.api

import healthiee.rest.domain.common.dto.base.Response
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.workout.dto.request.SearchConditionRequest
import healthiee.rest.domain.workout.dto.response.CreateWorkoutOfTheDayResponse
import healthiee.rest.domain.workout.dto.response.GetWorkoutsResponse
import healthiee.rest.domain.workout.service.WorkoutOfTheDayService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/workouts")
class WorkoutOfTheDayApiController(
    private val service: WorkoutOfTheDayService,
) {

    @PostMapping("")
    fun create(
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<CreateWorkoutOfTheDayResponse>> {
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = CreateWorkoutOfTheDayResponse(service.create(member)),
            )
        )
    }

    @DeleteMapping("{workoutId}")
    fun delete(
        @PathVariable("workoutId") workoutId: Long,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<Any>> {
        service.delete(workoutId, member)
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(), message = "오운완 삭제가 완료되었습니다"
            )
        )
    }

    @GetMapping("")
    fun getWorkouts(
        request: SearchConditionRequest,
        @AuthenticationPrincipal member: Member,
    ): ResponseEntity<Response<GetWorkoutsResponse>> {
        return ResponseEntity.ok(
            Response(
                code = HttpStatus.OK.value(),
                data = service.getWorkouts(request, member),
            )
        )
    }

}