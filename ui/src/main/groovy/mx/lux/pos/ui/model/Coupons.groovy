package mx.lux.pos.ui.model

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import mx.lux.pos.model.CuponMv
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import org.apache.commons.lang.StringUtils

@Slf4j
@Bindable
@ToString
@EqualsAndHashCode
class Coupons {
  String clave
  String billApplied
  Date dateApplied
  BigDecimal monto
  List<Payment> lstPayments
  List<OrderItem> items = [ ]
  Order orderApplied

  Order setOrderApplied( ) {
    if(StringUtils.trimToEmpty(billApplied).length() > 0 && orderApplied == null ){
      Order orderTarget = OrderController.findOrderByTicket( StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+StringUtils.trimToEmpty(billApplied) )
      if(orderTarget != null){
        return orderTarget
      } else {
        return null
      }
    } else {
      return orderApplied
    }
  }

  String getPayments( ) {
    String payments = ""
    orderApplied = setOrderApplied()
    if( orderApplied != null ){
      for(Payment payment : orderApplied.payments){
        payments = payments+","+StringUtils.trimToEmpty(payment.paymentTypeId)+"-"+String.format('$%.02f',payment.amount)
      }
      if( payments.startsWith(",") ){
        payments = payments.replaceFirst(",","")
      }
    }
    return payments
  }

  String getArticles( ){
    String articles = ""
    orderApplied = setOrderApplied()
    if( orderApplied != null ){
      for(OrderItem orderItem : orderApplied.items){
        articles = articles+","+StringUtils.trimToEmpty(orderItem.item.name)
      }
      if( articles.startsWith(",") ){
        articles = articles.replaceFirst(",","")
      }
    }
    return articles
  }


  String getClient( ){
    String client = ""
    orderApplied = setOrderApplied()
    if( orderApplied != null ){
      client = orderApplied.customer.fullName
    }
    return client
  }


  String getBalance( ){
    String balance = ""
    orderApplied = setOrderApplied()
    if( orderApplied != null ){
      balance = String.format('$%.02f', orderApplied.total.subtract(orderApplied.paid))
    }
    return balance
  }

  Date getDateDeliver( ){
    Date dateDeliver = null
    orderApplied = setOrderApplied()
    if( orderApplied != null ){
      dateDeliver = orderApplied.deliveryDate
    }
    return dateDeliver
  }


  static Coupons toCoupon( CuponMv cuponMv ) {
    if ( cuponMv?.claveDescuento ) {
      Coupons coupon = new Coupons(
          clave: cuponMv?.claveDescuento,
          billApplied: cuponMv?.facturaDestino,
          dateApplied: cuponMv?.fechaAplicacion,
          monto: cuponMv?.montoCupon,
          lstPayments: cuponMv?.notaVenta != null ? cuponMv?.notaVenta?.pagos?.collect {Payment.toPaymment( it )} : new ArrayList<>(),
          items: cuponMv?.notaVenta != null ? cuponMv?.notaVenta?.detalles?.collect {OrderItem.toOrderItem( it )} : new ArrayList<>()
      )
      return coupon
    }
    return null
  }

}
