package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.Pago
import mx.lux.pos.ui.controller.CancellationController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.PaymentController
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.view.panel.OrderPanel
import mx.lux.pos.ui.view.panel.ShowOrderPanel
import mx.lux.pos.ui.view.verifier.IsSelectedVerifier
import mx.lux.pos.ui.view.verifier.NotEmptyVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.text.NumberFormat
import java.util.List

class EditPaymentDialog extends JDialog {

  private static Double ZERO_TOLERANCE = 0.005
  private static final String TAG_FORMA_PAGO_EFECTIVO = 'EFECTIVO'
  private static final String TAG_FORMA_PAGO_CUPON = 'CUPON'
  private static final Integer TAG_TIPO_TRANS_CAMBIO_F_PAGO = 3

  private SwingBuilder sb
  private Payment tmpPayment
  private Payment payment
  private Order order
  private JFormattedTextField amount
  private JLabel mediumLabel
  private JLabel codeLabel
  private JLabel issuerLabel
  private JLabel terminalLabel
  private JLabel planLabel
  private JLabel messagesLabel
  private JLabel messages
  private JLabel dollarsReceivedLabel
  private JTextField medium
  private JTextField dollarsReceived
  private JTextField code
  private JComboBox paymentType
  private JComboBox issuer
  private JComboBox terminal
  private JComboBox plan
  private PaymentType defaultPaymentType
  private List<PaymentType> paymentTypes
  private List<Bank> issuingBanks
  private List<Terminal> terminals
  private List<Plan> plans
  private BigDecimal cambio
  private Pago pagoN
  private ShowOrderPanel orderP
  //private OrderPanel orderPanel
  private CuponMvView cuponMvView

    Pago getPagoN() {
        return pagoN
    }

  private static final String DOLARES = 'USD Recibidos'

    EditPaymentDialog(Component parent, Order order, final Payment payment, Component orderP){
      this.orderP = orderP
      this.order = order
      this.payment = payment
      sb = new SwingBuilder()
      defaultPaymentType = PaymentController.findDefaultPaymentType()
      paymentTypes = PaymentController.findActivePaymentTypes( BigDecimal.ZERO, order.id, 0 )
      issuingBanks = PaymentController.findIssuingBanks()
      terminals = PaymentController.findTerminals()
      plans = [ ]
      tmpPayment = payment ?: new Payment()
      tmpPayment.paymentTypeId = tmpPayment.paymentTypeId ?: defaultPaymentType?.id
      tmpPayment.paymentType = tmpPayment.paymentType ?: defaultPaymentType?.description
      buildUI( parent )
      doBindings()
  }

    EditPaymentDialog( Component parent, Order order, final Payment payment, CuponMvView cuponMvView ) {
    this.order = order
    this.payment = payment
    //this.orderPanel = orderPanel
    this.cuponMvView = cuponMvView
    sb = new SwingBuilder()
    defaultPaymentType = PaymentController.findDefaultPaymentType()
    paymentTypes = PaymentController.findActivePaymentTypes( cuponMvView.amount, order.id, order.customer.id )
    issuingBanks = PaymentController.findIssuingBanks()
    terminals = PaymentController.findTerminals()
    plans = [ ]
    tmpPayment = payment ?: new Payment()
    tmpPayment.paymentTypeId = tmpPayment.paymentTypeId ?: defaultPaymentType?.id
    tmpPayment.paymentType = tmpPayment.paymentType ?: defaultPaymentType?.description
    buildUI( parent )
    doBindings()
  }

  private void buildUI( Component parent ) {
    boolean fieldsEnabled = true
    NumberFormat formatter = NumberFormat.getInstance( Locale.US )
    /*if ( tmpPayment?.id ) {
      formatter = NumberFormat.getCurrencyInstance( Locale.US )
    }*/
    sb.dialog( this,
        title: tmpPayment?.id ? "Detalle Pago" : 'Agregar Pago',
        location: parent.locationOnScreen,
        resizable: false,
        modal: true,
        pack: true,
        layout: new MigLayout( 'wrap 2', '[85!][fill]', '[fill]' )
    ) {
      label( 'Importe' )
      amount = formattedTextField( font: new Font( '', Font.BOLD, 24 ),
          format: formatter,
          horizontalAlignment: JTextField.RIGHT,
          enabled: false
      )

      label( 'Tipo' )
      paymentType = comboBox( items: paymentTypes*.description, enabled: fieldsEnabled, itemStateChanged: typeChanged )

      mediumLabel = label( visible: false, constraints: 'hidemode 3' )
      medium = textField( visible: false,
          enabled: fieldsEnabled,
          document: new UpperCaseDocument(),
          constraints: 'hidemode 3'
      )

      codeLabel = label( visible: false, constraints: 'hidemode 3' )
      code = textField( visible: false,
          enabled: fieldsEnabled,
          document: new UpperCaseDocument(),
          constraints: 'hidemode 3'
      )

      issuerLabel = label( visible: false, constraints: 'hidemode 3' )
      issuer = comboBox( visible: false,
          enabled: fieldsEnabled,
          items: issuingBanks*.name,
          itemStateChanged: issuerChanged,
          constraints: 'hidemode 3'
      )

      terminalLabel = label( visible: false, constraints: 'hidemode 3' )
      terminal = comboBox( visible: false,
          items: terminals*.description,
          enabled: fieldsEnabled,
          itemStateChanged: terminalChanged,
          constraints: 'hidemode 3'
      )

      planLabel = label( visible: false, constraints: 'hidemode 3' )
      plan = comboBox( visible: false,
          enabled: fieldsEnabled,
          itemStateChanged: planChanged,
          constraints: 'hidemode 3'
      )

      dollarsReceivedLabel = label( visible: false, constraints: 'hidemode 3' )
      dollarsReceived = textField( visible: false,
          enabled: fieldsEnabled,
          constraints: 'hidemode 3' )

      messagesLabel = label( text: 'Verificar datos requeridos', constraints: 'span, hidemode 3', visible: false )
      messages = label( constraints: 'span, hidemode 3', visible: false )

      panel( layout: new MigLayout( 'right', '[fill,100!]' ), constraints: 'span' ) {
        button( 'Borrar', visible: !fieldsEnabled, actionPerformed: doDelete )
        button( 'Aplicar', defaultButton: true, visible: fieldsEnabled, actionPerformed: doSubmit )
        button( 'Cancelar', actionPerformed: {dispose()} )
      }
    }
  }

  private void doBindings( ) {
    sb.build {
      bean( amount, value: bind( source: tmpPayment, sourceProperty: 'amount', mutual: true ) )
      bean( paymentType, selectedItem: bind( source: tmpPayment, sourceProperty: 'paymentType', mutual: true ) )
      bean( medium, text: bind( source: tmpPayment, sourceProperty: 'paymentReference', mutual: true ) )
      bean( code, text: bind( source: tmpPayment, sourceProperty: 'codeReference', mutual: true ) )
      bean( issuer, selectedItem: bind( source: tmpPayment, sourceProperty: 'issuerBankId', mutual: true ) )
      bean( terminal, selectedItem: bind( source: tmpPayment, sourceProperty: 'terminal', mutual: true ) )
      bean( plan, selectedItem: bind( source: tmpPayment, sourceProperty: 'plan', mutual: true ) )
      bean( dollarsReceived, text: bind( source: tmpPayment, sourceProperty: 'planId', mutual: true ) )
    }
    if( StringUtils.trimToEmpty(tmpPayment?.issuerBankId).length() > 0 ){
      Integer idBank = 0
      try{
        idBank = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(tmpPayment?.issuerBankId))
      } catch ( NumberFormatException e ){
        println e
      }
      issuer.selectedIndex = idBank
    }
  }

  private def typeChanged = { ItemEvent ev ->
    boolean isNewPayment = true
    if ( isNewPayment ) {
      if ( ev.stateChange == ItemEvent.SELECTED ) {
        PaymentType paymentType = paymentTypes.find { PaymentType tmp ->
          tmp.description.equalsIgnoreCase( ev.item as String )
        }
          println('Tipo de pago: '+paymentType?.description)
          println('Tipo de pago ID: '+paymentType?.id)
        if ( 'TR'.equalsIgnoreCase( paymentType?.id ) ) {
          dispose()
          if ( order.due ) {
            new TransferDialog( this, order?.id ).show()
          } else {
            sb.optionPane(
                message: 'No hay saldo para aplicar pago',
                messageType: JOptionPane.ERROR_MESSAGE
            ).createDialog( this, 'Pago sin saldo' )
                .show()
          }
        } else if(paymentType.description.contains(TAG_FORMA_PAGO_CUPON)){
          /*BigDecimal montoCupon = OrderController.getCuponAmount( cuponMvView.idOrderSource )
          orderPanel.promotionDriver.addCouponDiscount( order, cuponMvView.amount, cuponMvView.idOrderSource, montoCupon )
          orderPanel.promotionDriver.requestPromotionSave(order?.id, false)
          orderPanel.promotionDriver.updatePromotionList()
          orderPanel.refreshData()
          dispose()*/
        } else {
          tmpPayment.paymentTypeId = paymentType?.id
          if ( StringUtils.isNotBlank( paymentType?.f1 ) ) {
            mediumLabel.visible = true
            mediumLabel.text = paymentType.f1
            medium.visible = true
          }
          if ( StringUtils.isNotBlank( paymentType?.f2 ) ) {
            codeLabel.visible = true
            codeLabel.text = paymentType.f2
            code.visible = true
          }
          if ( StringUtils.isNotBlank( paymentType?.f3 ) ) {
            issuerLabel.visible = true
            issuerLabel.text = paymentType.f3
            issuer.visible = true
          }
          if ( StringUtils.isNotBlank( paymentType?.f4 ) ) {
            terminalLabel.visible = true
            terminalLabel.text = paymentType.f4
            terminal.visible = true
          }
          if ( StringUtils.isNotBlank( paymentType?.f5 ) ) {
            planLabel.visible = true
            planLabel.text = paymentType.f5
            plan.visible = true
          }

          if( PaymentController.findTypePaymentsDollar(paymentType?.id) ){
            dollarsReceivedLabel.visible = true
            dollarsReceivedLabel.text = DOLARES
            dollarsReceived.visible = true
            planLabel.visible = false
            plan.visible = false
            if( paymentType?.f1.trim().equalsIgnoreCase( 'USD Recibidos' )){
              mediumLabel.visible = false
              medium.visible = false
            }
          }
          pack()
        }
      } else {
        tmpPayment.paymentTypeId = null
        hideNonDefault()
      }
    }
  }

  private void hideNonDefault( ) {
    mediumLabel.visible = false
    mediumLabel.text = null
    medium.visible = false
    medium.text = null
    codeLabel.visible = false
    codeLabel.text = null
    code.visible = false
    code.text = null
    issuerLabel.visible = false
    issuerLabel.text = null
    issuer.visible = false
    issuer.selectedIndex = -1
    terminalLabel.visible = false
    terminalLabel.text = null
    terminal.visible = false
    terminal.selectedIndex = -1
    planLabel.visible = false
    planLabel.text = null
    plan.visible = false
    plan.selectedIndex = -1
    dollarsReceivedLabel.visible = false
    dollarsReceivedLabel.text = null
    dollarsReceived.visible = false
    dollarsReceived.text = null
  }

  private def issuerChanged = { ItemEvent ev ->
    if ( ev.stateChange == ItemEvent.SELECTED ) {
      Bank bank = issuingBanks.find { Bank tmp ->
        tmp?.name?.equalsIgnoreCase( ev.item as String )
      }
      tmpPayment.issuerBankId = bank?.id
    } else if( StringUtils.trimToEmpty(tmpPayment.issuerBankId).length() > 0 ){
      Bank bank = issuingBanks.find { Bank tmp ->
        StringUtils.trimToEmpty(tmp?.id?.toString()).equalsIgnoreCase( StringUtils.trimToEmpty(tmpPayment.issuerBankId) )
      }
      tmpPayment.issuerBankId = bank?.id
    }
    else {
      tmpPayment.issuerBankId = null
    }
  }

  private def terminalChanged = { ItemEvent ev ->
    if ( ev.stateChange == ItemEvent.SELECTED ) {
      Terminal terminalTmp = terminals.find { Terminal tmp ->
        tmp?.description?.equalsIgnoreCase( ev.item as String )
      }
      tmpPayment.terminalId = terminalTmp?.id
      plans = PaymentController.findPlansByTerminal( terminalTmp?.id )
      plans?.each { Plan tmp ->
        plan.addItem( tmp?.description )
      }
      plan.selectedIndex = -1
    } else {
      tmpPayment.terminalId = null
      plan.removeAllItems()
    }
  }

  private def planChanged = { ItemEvent ev ->
    if ( ev.stateChange == ItemEvent.SELECTED ) {
      Plan planTmp = plans.find { Plan tmp ->
        tmp?.description?.equalsIgnoreCase( ev.item as String )
      }
      tmpPayment.planId = planTmp?.id
    } else {
      tmpPayment.planId = null
    }
  }

  private def doDelete = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    OrderController.removePaymentFromOrder( order.id, payment )
    dispose()
  }

  private def doSubmit = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    AuthorizationDialog authDialog = new AuthorizationDialog(this, "Esta operacion requiere autorizaci\u00f3n")
    authDialog.show()
    if (authDialog.authorized) {
      if ( isValid( order ) ) {
        CancellationController.registerLogAuth( StringUtils.trimToEmpty(order.id), TAG_TIPO_TRANS_CAMBIO_F_PAGO, tmpPayment.id )
        pagoN = OrderController.updatePaymentToOrder( order.id, tmpPayment )
        dispose()
      } else {
        source.enabled = true
      }
    } else {
      OrderController.notifyAlert('Se requiere autorizacion para esta operacion', 'Se requiere autorizacion para esta operacion')
      source.enabled = true
    }
  }

  private boolean isValid( Order order ) {
      boolean valid = true
      NotEmptyVerifier notEmptyVerifier = new NotEmptyVerifier()
      IsSelectedVerifier isSelectedVerifier = new IsSelectedVerifier()
      if ( tmpPayment.amount > 0 ) {
          Double diff = tmpPayment.amount.doubleValue() - order.total.doubleValue()
          if ( diff < ZERO_TOLERANCE ) {
              if( PaymentController.findTypePaymentsDollar(tmpPayment?.paymentTypeId)){
                  if(dollarsReceived.text != ''){
                      messages.text = null
                      valid &= true
                  } else {
                      valid &= false
                  }
              } else {
                  messages.text = null
                  valid &= true
              }
          } else {
              if( paymentType.selectedItem.equals(TAG_FORMA_PAGO_EFECTIVO) ){
                  amount.text = order.due.toString()
                  BigDecimal cambio = tmpPayment.amount.subtract(order.due)
                  new ChangeDialog( cambio, tmpPayment.amount, order.due ).show()
                  tmpPayment.amount = order.due
                  valid = true
              } else {
                  messages.text = "- El pago debe ser menor al saldo pendiente"
                  valid = false
              }
          }
      } else {
          messages.text = null
          notEmptyVerifier.verify( amount )
          valid = false
      }
      if ( paymentTypes*.id.contains( tmpPayment.paymentTypeId ) ) {
          valid &= true
      } else {
          isSelectedVerifier.verify( paymentType )
          valid = false
      }
      valid &= medium.visible ? ( notEmptyVerifier.verify( medium ) ) : true
      valid &= code.visible ? ( notEmptyVerifier.verify( code ) && ( code.text?.length() < 32 ) ) : true
      valid &= issuer.visible ? isSelectedVerifier.verify( issuer ) : true
      valid &= terminal.visible ? isSelectedVerifier.verify( terminal ) : true
      valid &= plan.visible ? isSelectedVerifier.verify( plan ) : true
      valid &= amount.visible ? notEmptyVerifier.verify( amount ) : true
      valid &= paymentType.visible ? isSelectedVerifier.verify( paymentType ) : true
      //valid &= issuer.visible ? notEmptyVerifier.verify( issuer ) : true
      valid &= terminal.visible ? isSelectedVerifier.verify( terminal ) : true
      valid &= dollarsReceived.visible ? notEmptyVerifier.verify( dollarsReceived ) : true
      if ( !valid ) {
          messagesLabel.foreground = Color.RED
          messagesLabel.visible = true
          messages.foreground = Color.RED
          messages.visible = true
      } else {
          messagesLabel.visible = false
          messages.visible = false
          messages.text = null
      }
      pack()
      return valid
  }

}
