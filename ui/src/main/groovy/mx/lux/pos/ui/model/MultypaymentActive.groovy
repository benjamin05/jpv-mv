package mx.lux.pos.ui.model

import mx.lux.pos.model.Cliente
import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.Pago
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.PaymentController
import net.sf.ehcache.search.expression.Or
import org.apache.commons.lang3.StringUtils

class MultypaymentActive implements Comparable<MultypaymentActive> {

  private List<NotaVenta> order
  private Cliente customer

  private static final TAG_CUPON_PLATA = 'PLATA'
  private static final TAG_CUPON_ORO = 'ORO'
  private static final TAG_CUPON_PLATINO = 'PLATINO'
  private static final TAG_CUPON_DIAMANTE = 'DIAMANTE'

    MultypaymentActive(List<NotaVenta> pOrders, Cliente pCustomer) {
    this.order = pOrders
    this.customer = pCustomer
  }

  List<NotaVenta> getOrder() {
    return this.order
  }

  Cliente getCustomer() {
    return this.customer
  }

  String getCustomerName() {
    return this.customer.nombreCompleto
  }

  String getPartList() {
    StringBuffer sb = new StringBuffer()
    for( NotaVenta o : this.order ){
      for (DetalleNotaVenta orderLine : o.detalles) {
          if (sb.length() > 0) {
              sb.append( ', ')
          }
          sb.append( StringUtils.trimToEmpty( orderLine.articulo.articulo ) )
      }
    }
    return sb.toString()
  }

  BigDecimal getAmount() {
    BigDecimal amount = BigDecimal.ZERO
    for(NotaVenta nv : this.order){
      amount = amount.add(nv.ventaNeta)
    }
    BigDecimal cuponAmount = BigDecimal.ZERO
    Collections.sort( this.order, new Comparator<NotaVenta>() {
        @Override
        int compare(NotaVenta o1, NotaVenta o2) {
            return o1.fechaHoraFactura.compareTo(o2.fechaHoraFactura)
        }
    })
    NotaVenta firstOrder = new NotaVenta()
    NotaVenta secondOrder = new NotaVenta()
    if( this.order.size() > 1 ){
      firstOrder = this.order.get(0)
      secondOrder = this.order.get(1)
    }
    BigDecimal firstcupon = OrderController.getCuponAmount( firstOrder.id )
    BigDecimal secondcupon = OrderController.getCuponAmount( secondOrder.id )
    amount = amount.subtract( Math.max(firstcupon,secondcupon))
    return amount
  }

  // Comparable
  int compareTo( MultypaymentActive order ) {
    return this.getCustomerName().compareToIgnoreCase( order.getCustomerName() )
  }

  String toString( ) {
    //return String.format( 'Order:%s  Customer:%s  Amount:%,.2f', this.getOrder().id, this.getCustomerName(),this.amount)
      return String.format( 'Customer:%s  Amount:%,.2f', this.getCustomerName(),this.amount)
  }
}
