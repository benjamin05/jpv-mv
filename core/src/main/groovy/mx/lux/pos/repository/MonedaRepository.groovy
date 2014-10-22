package mx.lux.pos.repository

import mx.lux.pos.model.Moneda
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

public interface MonedaRepository extends JpaRepository<Moneda, String>, QueryDslPredicateExecutor<Moneda> {

}