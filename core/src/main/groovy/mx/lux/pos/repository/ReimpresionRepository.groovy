package mx.lux.pos.repository

import mx.lux.pos.model.Reimpresion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface ReimpresionRepository extends JpaRepository<Reimpresion, Integer>, QueryDslPredicateExecutor<Reimpresion> {
   /*
    @Query( value = "SELECT next_folio('nota_venta_id_factura')", nativeQuery = true )
    String getReimpresionSequence( )
    */

    @Query( nativeQuery = true,
            value = "select count(id) from reimpresion where factura = ?1 and nota = 'Rx'" )
    BigInteger noReimpresiones( String facturaNotaVenta )



}


