package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.AccessController
import mx.lux.pos.ui.controller.CancellationController
import mx.lux.pos.ui.controller.InvTrController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.PaymentController
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.model.Payment
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.model.Order
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.util.List

class AuthorizationCanDialog extends JDialog {

  private SwingBuilder sb
  private String definedMessage
  private JTextField username
  private JPasswordField password
  private JLabel fullName
  private JLabel messages
  private boolean authorized
  private JComboBox reasonField
  private List<String> reasons
  private Order order

  private static final String GENERICO_ARMAZON = 'A'
  private static final String TAG_SURTE_PINO = 'P'
  private static final String TAG_RAZON_CAMBIO_FORMA_PAGO = 'CAMBIO DE FORMA DE PAGO'
  private static final String TAG_FORMA_PAGO_TC = 'TC'
  private String dataDev

  private static final Integer TAG_TIPO_TRANS_DEV = 2

  private static final String DATE_FORMAT = 'dd-MM-yyyy'
  private Boolean alreadyCan

    AuthorizationCanDialog( Component parent, String message, Order order, String devData, Boolean alreadyCan ) {
    sb = new SwingBuilder()
    this.order = order
    dataDev = devData
    reasons = CancellationController.findAllCancellationReasons()
    definedMessage = message ?: ''
    authorized = false
    this.alreadyCan = alreadyCan
    buildUI( parent )
  }

  boolean isAuthorized( ) {
    return authorized
  }

  private void buildUI( Component parent ) {
    sb.dialog( this,
        title: "Autorizar Operaci\u00f3n",
        location: parent.locationOnScreen,
        resizable: true,
        modal: true,
        pack: true,
        layout: new MigLayout( 'fill,wrap,center', '[fill,grow]' )
    ) {
      label( definedMessage, font: new Font( '', Font.BOLD, 14 ) )

      panel( border: titledBorder( 'Ingresar datos:' ),
          layout: new MigLayout( 'fill,wrap 3', '[][fill,grow][fill]', '[fill,25!]' )
      ) {
        label( 'Empleado' )
        username = textField( document: new UpperCaseDocument(),
            horizontalAlignment: JTextField.CENTER,
            keyReleased: usernameChanged,
            constraints: 'w 150!'
        )
        label( " " )
        fullName = label( constraints: 'span' )

        label( 'Contrase\u00f1a' )
        password = passwordField( document: new UpperCaseDocument(), horizontalAlignment: JTextField.CENTER,
                constraints: 'w 150!' )
        label( " " )
        label( constraints: 'span' )

        label( 'Raz\u00f3n' )
        reasonField = comboBox( items: reasons, constraints: 'w 285!,span 2' )

        messages = label( foreground: Color.RED, constraints: 'span' )
      }

      panel( layout: new MigLayout( 'right', '[fill,100!]' ) ) {
        button( 'Aceptar', defaultButton: true, actionPerformed: doRefund )
        button( 'Cancelar', actionPerformed: {dispose()} )
      }
    }
  }

  private def usernameChanged = { KeyEvent ev ->
    JTextField source = ev.source as JTextField
    sb.doOutside {
      User user = AccessController.getUser( source.text )
      fullName.text = user?.fullName ?: null
    }
    pack()
  }

  private Boolean hasValidData() {
    Boolean valid = true
    authorized = AccessController.canAuthorize( username.text, password.text )
    if ( authorized ) {
      messages.visible = false
    } else {
      messages.text = 'Empleado/Contrase\u00f1a incorrectos'
      messages.visible = true
      password.text = null
      valid = false
    }
    if( reasonField.selectedItem != null && StringUtils.trimToEmpty(reasonField.selectedItem.toString()).length() > 0 ){
      messages.visible = false
    } else {
      messages.text = 'Seleccione una causa de cancelacion valida'
      messages.visible = true
      valid = false
    }
    return valid
  }


    private def doRefund = { ActionEvent ev ->
        JButton source = ev.source as JButton
        source.enabled = false
        if ( allowLateCancellation() ) {
          boolean authorized = true
          if ( authorized && hasValidData() ) {
            String idReason = CancellationController.idReason( reasonField.selectedItem as String )
            if( CancellationController.orderHasValidStatus(order.id, idReason) ){
                if ( cancelOrder() ) {
                    CancellationController.reassignCoupons( StringUtils.trimToEmpty(order.id) )
                    CancellationController.refreshOrder( order )
                    CancellationController.refoundCoupons( order.id )
                    Map<Integer, String> creditRefunds = [ : ]
                        order.payments.each { Payment pmt ->
                          if( pmt.refundable.compareTo(BigDecimal.ZERO) > 0 ){
                            creditRefunds.put( pmt?.id, PaymentController.findReturnTypeDev( pmt.id, dataDev )  )
                          }
                        }
                        Order orderCom = null
                        if( order.payments.size() > 0 ){
                            orderCom = OrderController.findOrderByIdOrder(order.payments.first().order)
                        }
                        String orderDate = orderCom != null ? orderCom.date.format(DATE_FORMAT) : order.date.format(DATE_FORMAT)
                        String currentDate = new Date().format(DATE_FORMAT)
                        if(currentDate.trim().equalsIgnoreCase(orderDate.trim())){
                            if ( CancellationController.refundPaymentsCreditFromOrder( order.id, creditRefunds, dataDev ) ) {
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
                          BigDecimal refundable = BigDecimal.ZERO
                          if( orderCom != null ){
                            for(Payment payment : orderCom.payments){
                              refundable = refundable.add(payment.refundable)
                            }
                          }
                          if( refundable.compareTo(BigDecimal.ZERO) > 0 ){
                            orderCom = OrderController.findOrderByIdOrder(order.id)
                            printCancellationNotToday( orderCom, creditRefunds )
                          }
                        }
                        try{
                            if( !StringUtils.trimToEmpty(reasonField.selectedItem.toString()).equalsIgnoreCase(TAG_RAZON_CAMBIO_FORMA_PAGO) ){
                                CancellationController.sendCancellationOrderLc( StringUtils.trimToEmpty( order.bill ) )
                            }
                            Order newOrder = OrderController.findOrderByIdOrder( order.id )
                            CancellationController.registerLogAuth( StringUtils.trimToEmpty(order.id), TAG_TIPO_TRANS_DEV, -1 )
                            OrderController.runScriptBckpOrder( newOrder )
                        } catch ( Exception e ){
                            println e
                        }
                    dispose()
                }
            } else {
              sb.optionPane(
                    message: 'Trabajo en Laboratorio, no se puede cancelar. Espere a recibirlo.',
                    messageType: JOptionPane.ERROR_MESSAGE
              ).createDialog( this, 'No se puede cancelar' ).show()
            }
          }
        }
        source.enabled = true
    }

    private boolean cancelOrder( ) {
      if( !alreadyCan ){
        if ( CancellationController.cancelOrder( order.id, reasonField.selectedItem as String, "", false ) ) {
            if( !StringUtils.trimToEmpty(reasonField.selectedItem.toString()).equalsIgnoreCase(TAG_RAZON_CAMBIO_FORMA_PAGO) ){
              CancellationController.outputContactLens( order.id )
            }
            InvTrController controllerInv = InvTrController.instance
            controllerInv.automaticIssue( StringUtils.trimToEmpty(order.id), false )
            CancellationController.updateJb( order.id )
            CancellationController.generatedAcuses( order.id )
            //CancellationController.printCancellationPlan( order.id )
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
      } else {
        return true
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
            if(CancellationController.refundPaymentsCreditFromOrder( order.id, creditRefunds, dataDev )){
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
                    //CancellationController.printMaterialReturn( order.id )
                    //CancellationController.printMaterialReception( order.id )
                } else {
                    //CancellationController.printPinoNotStocked(order.id)
                }
            } else {
                //CancellationController.printMaterialReturn( order.id )
                //CancellationController.printMaterialReception( order.id )
            }
            if(CancellationController.refundPaymentsCreditFromOrder( order.id, creditRefunds, dataDev )){
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
            if(CancellationController.refundPaymentsCreditFromOrder( order.id, creditRefunds, dataDev )){
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
}
