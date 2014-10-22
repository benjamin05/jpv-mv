package mx.lux.pos.repository

import mx.lux.pos.model.TipoContacto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface TipoContactoRepository extends JpaRepository<TipoContacto, Integer>, QueryDslPredicateExecutor<TipoContacto> {



}
