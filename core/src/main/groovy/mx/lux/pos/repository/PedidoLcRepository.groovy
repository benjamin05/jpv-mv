package mx.lux.pos.repository

import mx.lux.pos.model.Acuse
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.model.TransInv
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

interface PedidoLcRepository extends JpaRepository<PedidoLc, String>, QueryDslPredicateExecutor<PedidoLc> {

    List<PedidoLc> findByIdAndFolio( String pIdPedido, Integer pFolio )

    List<PedidoLc> findById( String pIdPedido )

    List<PedidoLc> findByFechaAltaBetween( Date pFromDate, Date pToDate )

    @Query( value = "SELECT * FROM pedido_lc plc WHERE EXISTS ( SELECT * FROM nota_venta nv WHERE nv.factura = plc.id_pedido ) ORDER BY fecha_alta DESC LIMIT 10",
            nativeQuery = true)
    List<PedidoLc> findLastTen()

    /*List<PedidoLc> findByIdTipoTrans( String pIdTipoTrans )

    List<PedidoLc> findByIdTipoTransAndReferencia( String pIdTipoTrans, String pReferencia )

    List<PedidoLc> findBySucursal( String pSucursalDestino )*/
}

