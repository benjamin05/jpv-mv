package mx.lux.pos.repository

import mx.lux.pos.model.CuponMv
import mx.lux.pos.model.MoneyAdapter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import org.springframework.transaction.annotation.Transactional

import java.sql.Timestamp

interface CuponMvRepository extends JpaRepository<CuponMv, String>, QueryDslPredicateExecutor<CuponMv> {

  @Modifying
  @Transactional
  @Query( value = "INSERT INTO cupon_mv(clave_descuento,factura_origen,factura_destino,fecha_aplicacion,fecha_vigencia) VALUES(?1,?2,?3,?4,?5)", nativeQuery = true )
  void insertClave( String clave, String factOri, String factDest, Date aplicacion, Date vigencia )

}

