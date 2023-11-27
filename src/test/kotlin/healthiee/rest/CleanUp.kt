package healthiee.rest

import jakarta.persistence.EntityManager
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CleanUp(
    private var jdbcTemplate: JdbcTemplate,
    private var entityManager: EntityManager,
) {

    @Transactional
    fun all() {
        val tables = entityManager.metamodel.entities.map { it.name }

        tables.forEach { table -> byTable(table) }
    }

    @Transactional
    fun byTable(table: String) {
        jdbcTemplate.execute("TRUNCATE table $table")
    }
}