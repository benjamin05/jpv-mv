package mx.lux.pos.ui.model

import mx.lux.pos.model.Cliente
import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.model.NotaVenta
import org.apache.commons.lang3.StringUtils

class OrderActive implements Comparable<OrderActive> {

  private NotaVenta order
  private Cliente customer
  OrderActive(NotaVenta pOrder, Cliente pCustomer) {
    this.order = pOrder
    this.customer = pCustomer
  }

  NotaVenta getOrder() {
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
    for (DetalleNotaVenta orderLine : this.order.detalles) {
      if (sb.length() > 0) {
        sb.append( ', ')
      }
      sb.append( StringUtils.trimToEmpty( orderLine.articulo.articulo ) )
    }
    return sb.toString()
  }

  BigDecimal getAmount() {
    return this.order.ventaNeta
  }

  // Comparable
  int compareTo( OrderActive order ) {
    return this.getCustomerName().compareToIgnoreCase( order.getCustomerName() )
  }

  String toString( ) {
    return String.format( 'Order:%s  Customer:%s  Amount:%,.2f', this.getOrder().id, this.getCustomerName(),
        this.amount)
  }
}
