package mx.lux.pos.ui.model

import mx.lux.pos.java.repository.ClientesJava
import mx.lux.pos.java.repository.DetalleNotaVentaJava
import mx.lux.pos.java.repository.NotaVentaJava
import org.apache.commons.lang3.StringUtils

class OrderActive implements Comparable<OrderActive> {

  private NotaVentaJava order
  private ClientesJava customer
  OrderActive(NotaVentaJava pOrder, ClientesJava pCustomer) {
    this.order = pOrder
    this.customer = pCustomer
  }

  NotaVentaJava getOrder() {
    return this.order
  }

  ClientesJava getCustomer() {
    return this.customer
  }

  String getCustomerName() {
    return this.customer.nombreCompleto
  }

  String getPartList() {
    StringBuffer sb = new StringBuffer()
    for (DetalleNotaVentaJava orderLine : this.order.detalles) {
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
