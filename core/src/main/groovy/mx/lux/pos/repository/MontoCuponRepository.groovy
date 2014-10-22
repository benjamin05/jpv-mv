package mx.lux.pos.repository

import mx.lux.pos.model.MontoCupon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface MontoCuponRepository extends JpaRepository<MontoCupon, Integer>, QueryDslPredicateExecutor<MontoCupon> {

}

