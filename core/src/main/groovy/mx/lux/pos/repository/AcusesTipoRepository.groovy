package mx.lux.pos.repository

import mx.lux.pos.model.AcusesTipo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface AcusesTipoRepository extends JpaRepository<AcusesTipo, String>, QueryDslPredicateExecutor<AcusesTipo> {



}

