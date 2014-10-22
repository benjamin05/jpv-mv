package mx.lux.pos.repository

import mx.lux.pos.model.JbNotas
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface JbNotasRepository extends JpaRepository<JbNotas, Integer>, QueryDslPredicateExecutor<JbNotas> {


}

