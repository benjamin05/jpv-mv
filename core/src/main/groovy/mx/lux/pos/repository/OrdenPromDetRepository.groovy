package mx.lux.pos.repository

import mx.lux.pos.model.OrdenPromDet
import mx.lux.pos.repository.custom.OrdenPromDetRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import org.springframework.transaction.annotation.Transactional

interface OrdenPromDetRepository
extends JpaRepository<OrdenPromDet, Integer>,
    QueryDslPredicateExecutor<OrdenPromDet>, OrdenPromDetRepositoryCustom {

  List<OrdenPromDet> findByIdArticulo( Integer pIdArticulo )

  List<OrdenPromDet> findByIdFactura( String pIdFactura )

  List<OrdenPromDet> findByIdOrdenProm( Integer pIdOrdenProm )

  List<OrdenPromDet> findByIdPromocion( Integer pIdPromocion )

  @Modifying
  @Transactional
  @Query( value = "DELETE FROM orden_prom_det WHERE id_factura = ?1", nativeQuery = true )
  void deleteByIdFactura( String pIdFactura )

}
