package mx.lux.pos.repository

import mx.lux.pos.model.DescuentoClave
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface DescuentoClaveRepository extends JpaRepository<DescuentoClave, String>, QueryDslPredicateExecutor<DescuentoClave> {



}

