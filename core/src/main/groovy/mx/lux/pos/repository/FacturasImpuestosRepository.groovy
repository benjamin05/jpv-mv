package mx.lux.pos.repository

import mx.lux.pos.model.FacturasImpuestos
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

public interface FacturasImpuestosRepository extends JpaRepository<FacturasImpuestos, String>, QueryDslPredicateExecutor<FacturasImpuestos> {

}

