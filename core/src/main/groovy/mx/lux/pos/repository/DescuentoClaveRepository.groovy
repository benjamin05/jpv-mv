package mx.lux.pos.repository

import mx.lux.pos.model.DescuentoClave
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface DescuentoClaveRepository extends JpaRepository<DescuentoClave, String>, QueryDslPredicateExecutor<DescuentoClave> {

    @Query( value = "SELECT * FROM descuentos_clave WHERE clave_descuento = ?1", nativeQuery = true )
    DescuentoClave descuentoClave( String clave )

}

