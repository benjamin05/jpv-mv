package mx.lux.pos.repository

import mx.lux.pos.model.Ciudades
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface CiudadesRepository extends JpaRepository<Ciudades, String>, QueryDslPredicateExecutor<Ciudades> {

}

