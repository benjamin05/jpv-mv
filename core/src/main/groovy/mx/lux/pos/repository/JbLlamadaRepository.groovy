package mx.lux.pos.repository

import mx.lux.pos.model.JbLlamada
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import org.springframework.transaction.annotation.Transactional

interface JbLlamadaRepository extends JpaRepository<JbLlamada, String>, QueryDslPredicateExecutor<JbLlamada> {

    @Modifying
    @Transactional
    @Query( value = "DELETE from jb_llamada where rx = ?1", nativeQuery = true )
    void deleteByJbLlamada( String rx )
}
