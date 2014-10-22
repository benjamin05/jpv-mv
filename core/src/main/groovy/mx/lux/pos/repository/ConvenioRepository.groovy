package mx.lux.pos.repository

import mx.lux.pos.model.InstitucionIc
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface ConvenioRepository extends JpaRepository<InstitucionIc, String>, QueryDslPredicateExecutor<InstitucionIc> {

  List<InstitucionIc> findById( String convenio )

}
