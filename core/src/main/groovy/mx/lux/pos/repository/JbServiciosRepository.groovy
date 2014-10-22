package mx.lux.pos.repository

import mx.lux.pos.model.JbServicios
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface JbServiciosRepository extends JpaRepository<JbServicios, String>, QueryDslPredicateExecutor<JbServicios> {


}

