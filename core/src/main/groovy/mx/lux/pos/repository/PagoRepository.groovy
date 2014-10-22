package mx.lux.pos.repository

import mx.lux.pos.model.Pago
import mx.lux.pos.repository.custom.PagoRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import org.springframework.transaction.annotation.Transactional

@Transactional( readOnly = true )
interface PagoRepository extends JpaRepository<Pago, Integer>, QueryDslPredicateExecutor<Pago>, PagoRepositoryCustom {

  List<Pago> findByIdFactura( String idFactura )

  List<Pago> findByReferenciaPago( String referencia )

  List<Pago> findByIdFacturaOrderByFechaAsc( String idFactura )

  List<Pago> findByFechaBetween( Date fechaInicio, Date fechaFin )

  List<Pago> findByFechaBetweenOrderByFechaAsc( Date fechaInicio, Date fechaFin )

  List<Pago> findByFechaBetweenAndIdTerminalIn( Date fechaInicio, Date fechaFin, Collection<String> idTerminal )

  @Modifying
  @Transactional
  @Query( value = 'DELETE FROM pagos WHERE id_factura = ?1', nativeQuery = true )
  void deleteByIdFactura( String pIdFactura )

    @Query( value = "SELECT NEXTVAL('id_recibo_seq')", nativeQuery = true )
    BigInteger getReciboSequence( )


  //  @Query(value = "select cast(replace(replace(cast(sum(monto_pago) as varchar),'\$',''),',','') as numeric) from pagos where id_factura = ?1 and id_recibo <> ?2 ", nativeQuery = true)
   // BigDecimal getPagosAnteriores(String idFactura, String idRecibo)


}
