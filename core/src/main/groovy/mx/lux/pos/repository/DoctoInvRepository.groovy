package mx.lux.pos.repository

import mx.lux.pos.model.DoctoInv
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface DoctoInvRepository extends JpaRepository<DoctoInv, String>, QueryDslPredicateExecutor<DoctoInv> {
}

