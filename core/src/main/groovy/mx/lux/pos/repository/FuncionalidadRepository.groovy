package mx.lux.pos.repository

import mx.lux.pos.model.Funcionalidad
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface FuncionalidadRepository
    extends JpaRepository<Funcionalidad, String>,
            QueryDslPredicateExecutor<Funcionalidad> {
}
