package mx.lux.pos.repository

import mx.lux.pos.model.Acuse
import mx.lux.pos.model.LogAsignaSubgerente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface LogAsignaSubgerenteRepository extends JpaRepository<LogAsignaSubgerente, Integer>, QueryDslPredicateExecutor<LogAsignaSubgerente> {

  @Query( value = "SELECT * FROM log_asigna_subgerente ORDER BY id_log DESC LIMIT 1", nativeQuery = true)
  LogAsignaSubgerente findLastSubmanager()

}

