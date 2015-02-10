package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.CuponMv
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.ui.controller.AccessController
import mx.lux.pos.ui.controller.CancellationController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.PaymentController
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.model.Payment
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.driver.PromotionDriver
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.util.List

class CancellationDialog extends JDialog {

  private SwingBuilder sb
  private Order order
  private JLabel billField
  private JLabel customerField
  private JTextArea itemsField
  private JTextArea commentsField
  private JComboBox reasonField
  private JButton transferButton
  private JButton returnButton
  private List<String> reasons
  private Boolean total
  private JScrollPane scrollPane
  private JLabel lblDevoluciones
  private List<Payment> payments
    private BigDecimal totalAmount
    private BigDecimal totalPending
    private BigDecimal totalReturn
    private BigDecimal totalTransfer
    private List<LinkedHashMap<String, Object>> transitions
    private List<String> refundMethods

  private static final String DATE_FORMAT = 'dd-MM-yyyy'
  private static final String GENERICO_ARMAZON = 'A'
  private static final String TAG_SURTE_SUCURSAL = 'S'
  private static final String TAG_SURTE_PINO = 'P'
  private static final String TAG_RAZON_CAMBIO_FORMA_PAGO = 'CAMBIO DE FORMA DE PAGO'

  private static final Integer TAG_TIPO_TRANS_CAN = 1

  CancellationDialog( Component parent, String orderId, Boolean total ) {
    sb = new SwingBuilder()
    this.total = total
    order = OrderController.getOrder( orderId )
    reasons = CancellationController.findAllCancellationReasons()

      refundMethods = [ 'EFECTIVO', 'ORIGINAL' ]
      transitions = [ ]
      totalAmount = BigDecimal.ZERO
      totalPending = BigDecimal.ZERO
      totalReturn = BigDecimal.ZERO
      totalTransfer = BigDecimal.ZERO
      payments = PaymentController.findPaymentsByOrderId( orderId )
      /*payments.retainAll { Payment payment ->
          payment?.refundable
      }*/
      payments.each { Payment payment ->
          totalAmount += payment?.amount ?: 0
          totalPending += payment?.refundable ?: 0
          transitions.add( [
                  method: payment?.paymentTypeId,
                  amount: payment?.refundable,
                  type: 'p',
                  date: payment?.date?.format( DATE_FORMAT )
          ] )
      }

    buildUI( parent )
    doBindings()
  }

  private void buildUI( Component parent ) {
    sb.dialog( this,
        title: 'Cancelaci\u00f3n',
        location: parent.locationOnScreen,
        resizable: false,
        modal: true,
        pack: true,
        layout: new MigLayout( 'fill,wrap 2', '[][fill]', '[fill]' )
    ) {
      label( 'Ticket' )
      billField = label()

      label( 'Cliente' )
      customerField = label()

      label( 'Art\u00edculos' )
      scrollPane( constraints: 'h 40!' ) {
        itemsField = textArea( lineWrap: true, editable: false )
      }

        lblDevoluciones = label( 'Devoluciones', constraints: 'hidemode 3' )
        scrollPane = scrollPane( constraints: 'h 100!,w 285!,hidemode 3' ) {
            table( selectionMode: ListSelectionModel.SINGLE_SELECTION ) {
                tableModel( list: payments ) {
                    closureColumn( header: 'Forma Pago', read: {Payment pmt -> pmt?.paymentType} )
                    closureColumn( header: 'Monto', read: {Payment pmt -> pmt?.amount}, cellRenderer: new MoneyCellRenderer() )
                    closureColumn(
                            header: 'Forma Devoluci\u00f3n',
                            read: {Payment pmt -> pmt?.refundMethod},
                            write: {Payment pmt, String val -> pmt?.refundMethod = val},
                            cellEditor: new DefaultCellEditor( comboBox( items: refundMethods ) )
                    )
                }
            }
        }

      label( 'Pagos' )
      scrollPane( constraints: 'h 100!,w 285!' ) {
        table( selectionMode: ListSelectionModel.SINGLE_SELECTION ) {
          tableModel( list: order.payments ) {
            closureColumn( header: 'Fecha', read: {Payment tmp -> tmp?.date}, cellRenderer: new DateCellRenderer() )
            closureColumn( header: 'Tipo', read: {Payment tmp -> tmp?.paymentTypeId} )
            closureColumn( header: 'Monto', read: {Payment tmp -> tmp?.amount}, cellRenderer: new MoneyCellRenderer() )
          }
        }
      }

      label( 'Raz\u00f3n' )
      reasonField = comboBox( items: reasons, constraints: 'w 285!' )

      label( 'Observaciones' )
      scrollPane( constraints: 'h 40!' ) {
        commentsField = textArea( document: new UpperCaseDocument(), lineWrap: true )
      }

      label( " " )
      panel( layout: new MigLayout( 'right', '[fill,100!]' ), constraints: 'span' ) {
        transferButton = button( 'Aplicar', actionPerformed: doTransfer, constraints: 'hidemode 3', preferredSize: UI_Standards.BUTTON_SIZE )
        returnButton = button( 'Devoluci\u00f3n', actionPerformed: doRefund, constraints: 'hidemode 3', preferredSize: UI_Standards.BUTTON_SIZE )
        button( 'Cerrar', actionPerformed: {dispose()}, preferredSize: UI_Standards.BUTTON_SIZE )
      }
    }
  }

  private void doBindings( ) {
    sb.build {
      bean( billField, text: bind {order.ticket} )
      bean( customerField, text: bind {order.customer?.fullName} )
      bean( itemsField, text: bind {order.items*.item*.name} )
      bean( transferButton, enabled: bind {!'T'.equalsIgnoreCase( order.status )}, visible: bind {!total} )
      bean( returnButton, enabled: bind {!'T'.equalsIgnoreCase( order.status )}, visible: bind {total} )
      bean( scrollPane, visible: bind {total} )
      bean( lblDevoluciones, visible: bind {total} )
    }
  }

  private boolean allowLateCancellation( ) {
    if ( CancellationController.allowLateCancellation( order.id ) ) {
      return true
    } else {
      sb.optionPane(
          message: "No se permite cancelaci\u00f3n posterior \na la fecha de compra: ${order.date?.format( 'dd-MM-yyyy HH:mm' )}",
          optionType: JOptionPane.DEFAULT_OPTION
      ).createDialog( this, "No se permite cancelaci\u00f3n" )
          .show()
    }
    return false
  }

  private boolean cancelOrder( ) {
      if ( CancellationController.cancelOrder( order.id, reasonField.selectedItem as String, commentsField.text, false ) ) {
          CancellationController.updateJb( order.id )
          CancellationController.generatedAcuses( order.id )
          CancellationController.printCancellationPlan( order.id )
          try{
              OrderController.runScriptBckpOrder( order )
          } catch ( Exception e ){
              println e
          }
          return true
      } else {
          sb.optionPane( message: "Ocurrio un error al cancelar", optionType: JOptionPane.DEFAULT_OPTION )
                  .createDialog( this, "Error" )
                  .show()
          return false
      }
  }

  private def doTransfer = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    if ( allowLateCancellation() ) {
      if ( cancelOrder() ) {
        CancellationController.refoundCoupons( order.id )
        CancellationController.freeCoupon( order.id )
        CancellationController.registerLogAuth( StringUtils.trimToEmpty(order.id), TAG_TIPO_TRANS_CAN, -1 )
        String orderDate = order.date.format(DATE_FORMAT)
        String currentDate = new Date().format(DATE_FORMAT)
        dispose()
      }
    }
    source.enabled = true
  }

  private def doRefund = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    if ( allowLateCancellation() ) {
      boolean authorized
      if ( AccessController.authorizerInSession ) {
        authorized = true
      } else {
        AuthorizationDialog authDialog = new AuthorizationDialog( this, "Cancelaci\u00f3n requiere autorizaci\u00f3n" )
        authDialog.show()
        authorized = authDialog.authorized
      }
      if ( authorized && hasValidData() ) {
        if ( cancelOrder() ) {
          CancellationController.refoundCoupons( order.id )
            if ( hasValidData() ) {
                Map<Integer, String> creditRefunds = [ : ]
                payments.each { Payment pmt ->
                    creditRefunds.put( pmt?.id, pmt?.refundMethod )
                }
                Order orderCom = null
                if( payments.size() > 0 ){
                  orderCom = OrderController.findOrderByIdOrder(payments.first().order)
                }
                String orderDate = orderCom != null ? orderCom.date.format(DATE_FORMAT) : order.date.format(DATE_FORMAT)
                String currentDate = new Date().format(DATE_FORMAT)
                if(currentDate.trim().equalsIgnoreCase(orderDate.trim())){
                    if ( CancellationController.refundPaymentsCreditFromOrder( order.id, creditRefunds, "" ) ) {
                        CancellationController.printOrderCancellation( order.id )
                        dispose()
                    } else {
                        sb.optionPane(
                                message: 'Ocurrio un error al registrar devoluciones',
                                messageType: JOptionPane.ERROR_MESSAGE
                        ).createDialog( this, 'No se registran devoluciones' )
                                .show()
                    }
                } else {
                    orderCom = OrderController.findOrderByIdOrder(order.id)
                    printCancellationNotToday( orderCom, creditRefunds )
                }
                try{
                  if( !StringUtils.trimToEmpty(reasonField.selectedItem.toString()).equalsIgnoreCase(TAG_RAZON_CAMBIO_FORMA_PAGO) ){
                    CancellationController.sendCancellationOrderLc( StringUtils.trimToEmpty( order.bill ) )
                  }
                  Order newOrder = OrderController.findOrderByIdOrder( order.id )
                  OrderController.runScriptBckpOrder( newOrder )
                } catch ( Exception e ){
                    println e
                }
            }
          dispose()
          //new RefundDialog( this, order.id ).show()
        }
      }
    }
    source.enabled = true
  }


    private def printCancellationNotToday(Order orderCom){
        Item item = new Item()
        String surte = ''
        for(OrderItem i : orderCom.items){
            if(i.item.type.trim().equalsIgnoreCase(GENERICO_ARMAZON)){
                surte = i.delivers.trim()
                item = i.item
            }
        }
        if(item.id != null && !surte.equalsIgnoreCase(TAG_SURTE_PINO)){
            //CancellationController.printMaterialReturn( order.id )
            //CancellationController.printMaterialReception( order.id )
        } else if(item.id != null && surte.equalsIgnoreCase(TAG_SURTE_PINO)){
          if( order.deliveryDate == null ){
            if(CancellationController.verificaPino(order.id) ){
                //CancellationController.printMaterialReturn( order.id )
                //CancellationController.printMaterialReception( order.id )
            } else {
                //CancellationController.printPinoNotStocked(order.id)
            }
          } else {
              //CancellationController.printMaterialReturn( order.id )
              //CancellationController.printMaterialReception( order.id )
          }
        }
    }


    private void refund( ){
      Map<Integer, String> creditRefunds = [ : ]
      order.payments.each { Payment pmt ->
        creditRefunds.put( pmt?.id, pmt?.refundMethod )
      }
      Order orderCom = OrderController.findOrderByIdOrder(order.payments.first().order)
      String orderDate = orderCom.date.format(DATE_FORMAT)
      String currentDate = new Date().format(DATE_FORMAT)
      if(currentDate.trim().equalsIgnoreCase(orderDate.trim())){
        if ( CancellationController.refundPaymentsCreditFromOrder( order.id, creditRefunds, "" ) ) {
          CancellationController.printOrderCancellation( order.id )
        } else {
          sb.optionPane(
            message: 'Ocurrio un error al registrar devoluciones',
            messageType: JOptionPane.ERROR_MESSAGE
          ).createDialog( this, 'No se registran devoluciones' )
            .show()
        }
      } else {
        printCancellationNotToday( orderCom, creditRefunds )
      }
      try{
        Order newOrder = OrderController.findOrderByIdOrder( order.id )
        OrderController.runScriptBckpOrder( newOrder )
      } catch ( Exception e ){
        println e
      }
    }



    private def printCancellationNotToday(Order orderCom, Map<Integer, String> creditRefunds){
        Item item = new Item()
        String surte = ''
        for(OrderItem i : orderCom.items){
            if(i.item.type.trim().equalsIgnoreCase(GENERICO_ARMAZON)){
                surte = i.delivers.trim()
                item = i.item
            }
        }
        if(item.id != null && !surte.equalsIgnoreCase(TAG_SURTE_PINO)){
            if(CancellationController.refundPaymentsCreditFromOrder( order.id, creditRefunds, "" )){
                //CancellationController.updateJb( orderId )
                //CancellationController.printMaterialReturn( order.id )
                //CancellationController.printMaterialReception( order.id )
                CancellationController.printOrderCancellation( order.id )
                dispose()
            } else {
                sb.optionPane(
                        message: 'Ocurrio un error al registrar devoluciones',
                        messageType: JOptionPane.ERROR_MESSAGE
                ).createDialog( this, 'No se registran devoluciones' )
                        .show()
            }
        } else if(item.id != null && surte.equalsIgnoreCase(TAG_SURTE_PINO)){
            Order order = OrderController.findOrderByIdOrder( order.id.trim() )
            if( order.deliveryDate == null ){
                if(CancellationController.verificaPino(order.id) ){
                    //CancellationController.updateJb(orderId)
                    //CancellationController.printMaterialReturn( order.id )
                    //CancellationController.printMaterialReception( order.id )
                } else {
                    //CancellationController.printPinoNotStocked(order.id)
                    //CancellationController.updateJb(orderId)
                }
            } else {
                //CancellationController.updateJb(orderId)
                //CancellationController.printMaterialReturn( order.id )
                //CancellationController.printMaterialReception( order.id )
            }
            if(CancellationController.refundPaymentsCreditFromOrder( order.id, creditRefunds, "" )){
                CancellationController.printOrderCancellation( order.id )
                dispose()
            } else {
                sb.optionPane(
                        message: 'Ocurrio un error al registrar devoluciones',
                        messageType: JOptionPane.ERROR_MESSAGE
                ).createDialog( this, 'No se registran devoluciones' )
                        .show()
            }
        } else if( item.id == null ){
            if(CancellationController.refundPaymentsCreditFromOrder( order.id, creditRefunds, "" )){
                CancellationController.printOrderCancellation( order.id )
                dispose()
            } else {
                sb.optionPane(
                        message: 'Ocurrio un error al registrar devoluciones',
                        messageType: JOptionPane.ERROR_MESSAGE
                ).createDialog( this, 'No se registran devoluciones' )
                        .show()
            }
        }
    }



    private boolean hasValidData( ) {
        boolean result = true
        payments.each { Payment pmt ->
            String tmpRefundMethod = pmt?.refundMethod
            if ( StringUtils.isBlank( tmpRefundMethod ) || !refundMethods.contains( tmpRefundMethod ) ) {
                result = false
            }
        }
        if ( !result ) {
            sb.optionPane(
                    message: 'Se deben registar todas las formas de devoluci\u00f3n',
                    messageType: JOptionPane.ERROR_MESSAGE
            ).createDialog( this, 'Formas de devoluci\u00f3n inv\u00e1lidas' )
                    .show()
        }
        return result
    }


    private void validTransferCuponMv( ){
        PromotionDriver promotionDriver = PromotionDriver.instance
        for(Payment pay : order.payments){
            if( org.apache.commons.lang3.StringUtils.trimToEmpty(pay.paymentTypeId).equalsIgnoreCase(TAG_PAYMENT_TYPE_TRANSF) ){
                NotaVenta notaVenta = OrderController.findOrderByidOrder( org.apache.commons.lang3.StringUtils.trimToEmpty(pay.paymentReference) )
                List<CuponMv> lstCupon = OrderController.obtenerCuponMvByTargetOrder( org.apache.commons.lang3.StringUtils.trimToEmpty(notaVenta.factura) )
                if( lstCupon.size() > 0 ){
                    promotionDriver.addCouponDiscountTransf( order, lstCupon.first().montoCupon, lstCupon.first().claveDescuento, lstCupon.first().montoCupon )
                }
            }
        }
    }


}
