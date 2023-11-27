package healthiee.rest.domain.workout.entity

import healthiee.rest.domain.common.entity.base.BaseEntity
import healthiee.rest.domain.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "workout_of_the_days")
class WorkoutOfTheDay(
    _member: Member,
    _workoutDate: LocalDateTime,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workout_of_the_day_id")
    val id: Long = 0L

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member = _member

    @Column
    val workoutDate: LocalDateTime = _workoutDate

    companion object {
        fun createWorkoutOfTheDay(member: Member): WorkoutOfTheDay {
            val now = LocalDateTime.now()
            val workoutDate = LocalDateTime.of(now.year, now.monthValue, now.dayOfMonth, 0, 0, 0)
            return WorkoutOfTheDay(member, workoutDate)
        }
    }

}