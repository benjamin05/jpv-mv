package mx.lux.pos.repository

import mx.lux.pos.model.ModeloLc
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface ModeloLcRepository extends JpaRepository<ModeloLc, String>, QueryDslPredicateExecutor<ModeloLc> {
}

