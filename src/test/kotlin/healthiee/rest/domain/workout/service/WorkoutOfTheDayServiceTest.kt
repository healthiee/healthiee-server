package healthiee.rest.domain.workout.service

import healthiee.rest.CleanUp
import healthiee.rest.domain.member.entity.Member
import healthiee.rest.domain.member.repository.MemberRepository
import healthiee.rest.domain.workout.entity.WorkoutOfTheDay
import healthiee.rest.domain.workout.repository.WorkoutOfTheDayRepository
import healthiee.rest.lib.error.ApiException
import healthiee.rest.lib.error.ErrorCode
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.util.*

@SpringBootTest
class WorkoutOfTheDayServiceTest {

    @Autowired
    private lateinit var cleanUp: CleanUp

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var service: WorkoutOfTheDayService

    @Autowired
    private lateinit var repository: WorkoutOfTheDayRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @AfterEach
    fun tearDown() {
        cleanUp.byTable("workout_of_the_days")
    }

    @Test
    @DisplayName("오운완 성공")
    fun create() {
        // given
        val member = validMember()

        // when
        val createdId = service.create(member)
        em.clear()

        // then
        val workoutOfTheDay = validWorkoutOfTheDay(createdId)
        assertThat(workoutOfTheDay.member.name).isEqualTo("member1")
    }

    @Test
    @DisplayName("오운완을 한 이력이 있을 경우 BAD_REQUEST 반환")
    fun create_DuplicateWorkoutOfTheDay() {
        // given
        val member = validMember()
        service.create(member)
        em.clear()

        // when & then
        val exception: ApiException = assertThrows(ApiException::class.java) {
            service.create(member)
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.BAD_REQUEST)
    }


    private fun validWorkoutOfTheDay(id: Long): WorkoutOfTheDay {
        return repository.findByIdOrNull(id) ?: throw IllegalStateException("Workout of the day Not Found")
    }

    private fun validMember(): Member {
        // member1
        return memberRepository.findByIdOrNull(UUID.fromString("736cf454-2818-4fd9-a077-300b6f5efe64"))
            ?: throw IllegalStateException("Member Not Found")
    }
}