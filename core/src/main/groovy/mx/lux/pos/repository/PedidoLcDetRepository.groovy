package mx.lux.pos.repository

import mx.lux.pos.model.PedidoLcDet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import org.springframework.transaction.annotation.Transactional

interface PedidoLcDetRepository extends JpaRepository<PedidoLcDet, Integer>, QueryDslPredicateExecutor<PedidoLcDet> {

    List<PedidoLcDet> findById( String pId )

    @Modifying
    @Transactional
    @Query( value = "DELETE FROM pedido_lc_det WHERE id_pedido = ?1", nativeQuery = true )
    void deleteByIdFactura( String pIdFactura )
    /*List<PedidoLcDet> findBySku( Integer pSku )

    List<PedidoLcDet> findBySkuIn( Collection<Integer> pSkus )*/
}

