package mx.lux.pos.repository

import mx.lux.pos.model.Acuse
import mx.lux.pos.model.LogAsignaSubgerente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface LogAsignaSubgerenteRepository extends JpaRepository<LogAsignaSubgerente, Integer>, QueryDslPredicateExecutor<LogAsignaSubgerente> {

  @Query( value = "SELECT * FROM log_asigna_subgerente ORDER BY id_log DESC LIMIT 1", nativeQuery = true)
  LogAsignaSubgerente findLastSubmanager()

  @Query( value = "SELECT * FROM log_asigna_subgerente WHERE (fecha_inicial IS NOT NULL AND fecha_inicial >= CAST(NOW() AS DATE)) OR (fecha_inicial IS NULL AND fecha >= CAST(NOW() AS DATE)) ORDER BY id_log ASC", nativeQuery = true)
  List<LogAsignaSubgerente> findSubmanagersProgrammed()
}

