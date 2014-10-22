package mx.lux.pos.repository

import mx.lux.pos.model.AutorizaMov
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface AutorizaMovRepository extends JpaRepository<AutorizaMov, Date>, QueryDslPredicateExecutor<AutorizaMov> {

}

