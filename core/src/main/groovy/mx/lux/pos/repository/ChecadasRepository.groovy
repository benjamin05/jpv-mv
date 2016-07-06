package mx.lux.pos.repository

import mx.lux.pos.model.Checadas
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface ChecadasRepository extends JpaRepository<Checadas, Integer>, QueryDslPredicateExecutor<Checadas> {

}

