package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import mx.lux.pos.model.CuponMv
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.ui.controller.OrderController
import org.apache.commons.lang.StringUtils

@Bindable
@ToString
@EqualsAndHashCode
class OrderDiscount implements IPromotion {

  NotaVenta notaVenta = new NotaVenta()
  NotaVentaJava notaVentaJ = new NotaVentaJava()
  private OrderDiscount( ) { }

  private static final String DESCRIPCION = "%s%% descuento sobre venta"
  private static final String TAG_PROMO_EDAD = "PREDAD";

  static IPromotion toPromotions( NotaVenta notaVenta ) {
    if ( (notaVenta != null) && ( notaVenta.por100Descuento > 0 ) && ( notaVenta.montoDescuento > 0 ) ) {
      OrderDiscount promotion = new OrderDiscount()
      promotion.notaVenta = notaVenta
      return promotion
    }
    return null
  }

  static IPromotion toPromotions( NotaVentaJava notaVenta ) {
    if ( (notaVenta != null) && ( notaVenta.por100Descuento > 0 ) && ( notaVenta.montoDescuento > 0 ) ) {
      OrderDiscount promotion = new OrderDiscount()
      promotion.notaVentaJ = notaVenta
      return promotion
    }
    return null
  }

  String getDescripcion( ) {
    String desc = "N/A"
    if ( notaVenta != null ) {
      if( notaVenta.desc != null &&
              (notaVenta?.desc?.descuentosClave != null &&
                      !notaVenta?.desc?.descuentosClave?.descripcion_descuento.trim().equalsIgnoreCase("NA")) ){
        desc = notaVenta.desc.descuentosClave.descripcion_descuento
      } else if(notaVenta != null && notaVenta.desc != null ) {
        CuponMv cuponMv = OrderController.obtenerCuponMvByClave(StringUtils.trimToEmpty(notaVenta.desc.clave) )
        if( cuponMv != null ){
          if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("F") ){
            desc = String.format( "Amigos y Familiares %s" , StringUtils.trimToEmpty(cuponMv.montoCupon.toString()).replace(".00","") )
          } else if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("H") ){
            desc = String.format( "Cupon %s LC" , StringUtils.trimToEmpty(cuponMv.montoCupon.toString()).replace(".00","") )
          } else {
            desc = String.format( "Cupon %s" , StringUtils.trimToEmpty(cuponMv.montoCupon.toString()).replace(".00","") )
          }
        } else if( StringUtils.trimToEmpty(notaVenta?.desc?.clave).length() == 11 && StringUtils.trimToEmpty(notaVenta?.desc?.tipoClave).equalsIgnoreCase("DIRECCION") ){
          desc = String.format( "Descuento CRM" )
        } else if( StringUtils.trimToEmpty(notaVenta?.desc?.clave).equalsIgnoreCase(TAG_PROMO_EDAD) ){

        } else {
          desc = String.format( DESCRIPCION, notaVenta.por100Descuento.toString() )
        }
      } else {
        desc = String.format( DESCRIPCION, notaVenta.por100Descuento.toString() )
      }
    }
    return desc
  }

  String getArticulo( ) {
    String art = '*'
    return art
  }

  BigDecimal getPrecioLista( ) {
    BigDecimal listPrice = BigDecimal.ZERO
    if ( notaVenta != null ) {
      if( notaVenta!= null && notaVenta.ventaNeta != null ){
        listPrice = notaVenta.montoDescuento.add( notaVenta.ventaNeta )
      } else if(notaVentaJ!= null && notaVentaJ.ventaNeta != null) {
        listPrice = notaVentaJ.montoDescuento.add( notaVentaJ.ventaNeta )
      }
    }
    return listPrice
  }

  BigDecimal getDescuento( ) {
    BigDecimal discount = BigDecimal.ZERO
    if ( notaVenta != null ) {
      discount = notaVenta.montoDescuento
    }
    return discount
  }

  BigDecimal getPrecioNeto( ) {
    BigDecimal netPrice = BigDecimal.ZERO
    if ( notaVenta != null ) {
      netPrice = notaVenta.ventaNeta
    }
    return netPrice
  }

}
