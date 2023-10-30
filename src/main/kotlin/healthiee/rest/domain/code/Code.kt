package healthiee.rest.domain.code

import healthiee.rest.domain.base.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "codes")
class Code(
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: Type,
    _name: String,
    _active: Boolean,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    val id: Long = 0L

    @Column(nullable = false)
    var name: String = _name
        private set

    @Column(nullable = false)
    var active: Boolean = _active
        private set

    fun changeCode(name: String, active: Boolean) {
        this.name = name
        this.active = active
    }

    enum class Type {
        POST_CATEGORY,
    }

    companion object {
        fun createCode(
            type: Type,
            name: String,
            active: Boolean = true,
        ) = Code(type, name, active)
    }

    override fun toString(): String {
        return "Code(id=$id, name='$name', active=$active)"
    }

}