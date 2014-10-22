package mx.lux.pos.repository

import mx.lux.pos.model.Descuento
import mx.lux.pos.repository.custom.DescuentoRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QueryDslPredicateExecutor

public interface DescuentoRepository extends JpaRepository<Descuento, Integer>, QueryDslPredicateExecutor<Descuento>, DescuentoRepositoryCustom {

  List<Descuento> findByClave( String pClave )
  
  List<Descuento> findByIdFactura( String pIdFactura )

  @Query( value = "SELECT * FROM descuentos WHERE id_factura NOT IN (SELECT id_factura FROM nota_venta) or id_factura IN (SELECT id_factura FROM nota_venta WHERE factura = '' or factura is null)", nativeQuery = true )
  List<Descuento> findDiscountsWithoutBill()
}
