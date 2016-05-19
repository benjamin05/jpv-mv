package mx.lux.pos.repository

import mx.lux.pos.model.CausaDev
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface CausaDevRepository extends JpaRepository<CausaDev, Integer>, QueryDslPredicateExecutor<CausaDev> {

}

