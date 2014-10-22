package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.Pago
import mx.lux.pos.model.Parametro
import mx.lux.pos.model.TipoParametro
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.MainWindow
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.PaymentController
import mx.lux.pos.ui.model.*
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

class PaymentMultyDialog extends JDialog {

  private static Double ZERO_TOLERANCE = 0.005
  private static final String TAG_FORMA_PAGO_EFECTIVO = 'EFECTIVO'

  private SwingBuilder sb
  private Payment tmpPayment
  private Payment payment
  private Order order
  private Order secondOrder
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
  private BigDecimal minimumAmount
  private Pago pagoN
  private ShowOrderPanel orderP

  private String amountTotal
  private String armazonString = null
  private Boolean advanceOnlyInventariable

  private Boolean otherPayment
  private Boolean totalPayment

  private Boolean isOtherAmount

  private static final TAG_CUPON = 'CUPON'

    Pago getPagoN() {
        return pagoN
    }



    private static final String DOLARES = 'USD Recibidos'

    PaymentMultyDialog(Component parent, Order order, final Payment payment){
      this.orderP = orderP
      this.order = order
      //this.minimumAmount = minimumAmount
      this.secondOrder = secondOrder
      this.payment = payment
      sb = new SwingBuilder()
      defaultPaymentType = PaymentController.findDefaultPaymentType()
      paymentTypes = PaymentController.findActivePaymentTypesToMultypayment()
      issuingBanks = PaymentController.findIssuingBanks()
      terminals = PaymentController.findTerminals()
      plans = [ ]
      tmpPayment = payment ?: new Payment()
      tmpPayment.paymentTypeId = tmpPayment.paymentTypeId ?: defaultPaymentType?.id
      tmpPayment.paymentType = tmpPayment.paymentType ?: defaultPaymentType?.description
      buildUI( parent )
      doBindings()
  }

    PaymentMultyDialog( Component parent, Order order, Order secondOrder, final Payment payment, BigDecimal amount,
                        Boolean otherPayment, Boolean total, Boolean isOtherAmount ) {
    this.isOtherAmount = isOtherAmount
    this.otherPayment = otherPayment
    this.totalPayment = total
    this.order = order
    this.minimumAmount = amount
    this.secondOrder = secondOrder
    this.payment = payment
    sb = new SwingBuilder()
    defaultPaymentType = PaymentController.findDefaultPaymentType()
    paymentTypes = PaymentController.findActivePaymentTypesToMultypayment()
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
    boolean fieldsEnabled = tmpPayment?.id ? false : true
    NumberFormat formatter = NumberFormat.getInstance( Locale.US )
    if ( tmpPayment?.id ) {
      formatter = NumberFormat.getCurrencyInstance( Locale.US )
    }
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
          //text: String.format( "%,.2f", this.minimumAmount),
          //inputVerifier: new NotEmptyVerifier(),
          horizontalAlignment: JTextField.RIGHT,
          enabled: fieldsEnabled
      )

      label( 'Tipo' )
      paymentType = comboBox( items: paymentTypes*.description, enabled: fieldsEnabled, itemStateChanged: typeChanged )

      mediumLabel = label( visible: false, constraints: 'hidemode 3' )
      medium = textField( visible: false,
          enabled: fieldsEnabled,
          document: new UpperCaseDocument(),
          //inputVerifier: new NotEmptyVerifier(),
          constraints: 'hidemode 3'
      )

      codeLabel = label( visible: false, constraints: 'hidemode 3' )
      code = textField( visible: false,
          enabled: fieldsEnabled,
          document: new UpperCaseDocument(),
          //inputVerifier: new NotEmptyVerifier(),
          constraints: 'hidemode 3'
      )

      issuerLabel = label( visible: false, constraints: 'hidemode 3' )
      issuer = comboBox( visible: false,
          enabled: fieldsEnabled,
          items: issuingBanks*.name,
          itemStateChanged: issuerChanged,
          //inputVerifier: new NotEmptyVerifier(),
          constraints: 'hidemode 3'
      )

      terminalLabel = label( visible: false, constraints: 'hidemode 3' )
      terminal = comboBox( visible: false,
          items: terminals*.description,
          enabled: fieldsEnabled,
          //inputVerifier: new IsSelectedVerifier(),
          itemStateChanged: terminalChanged,
          constraints: 'hidemode 3'
      )

      planLabel = label( visible: false, constraints: 'hidemode 3' )
      plan = comboBox( visible: false,
          enabled: fieldsEnabled,
          //inputVerifier: new IsSelectedVerifier(),
          itemStateChanged: planChanged,
          constraints: 'hidemode 3'
      )

      dollarsReceivedLabel = label( visible: false, constraints: 'hidemode 3' )
      dollarsReceived = textField( visible: false,
          enabled: fieldsEnabled,
          //inputVerifier: new NotEmptyVerifier(),
          constraints: 'hidemode 3' )

      messagesLabel = label( text: 'Verificar datos requeridos', constraints: 'span, hidemode 3', visible: false )
      messages = label( constraints: 'span, hidemode 3', visible: false )

      panel( layout: new MigLayout( 'right', '[fill,100!]' ), constraints: 'span' ) {
        button( 'Borrar', visible: !fieldsEnabled && !tmpPayment.paymentType.contains(TAG_CUPON), actionPerformed: doDelete )
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
  }

  private def typeChanged = { ItemEvent ev ->
    boolean isNewPayment = tmpPayment?.id ? false : true
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
    } else {
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
    BigDecimal amountView = BigDecimal.ZERO
    try{
      amountView = NumberFormat.getInstance().parse( amount.text.trim() )
    } catch ( NumberFormatException ex ){
      println ex
    }
    if( amountView.doubleValue() <= minimumAmount.doubleValue() ){
      minimumAmount = amountView
      Boolean saveFirst = flujoImprimir( order, false )
      if( saveFirst ){
        flujoImprimir( secondOrder, true )
      }
    } else {
      sb.optionPane(
          message: 'El pago debe ser menor al monto por pagar',
          messageType: JOptionPane.ERROR_MESSAGE
      ).createDialog(this, 'Pago incorrecto')
          .show()
    }
    source.enabled = true
  }

  private BigDecimal amountToPayFirstOrder( Order order ){
    BigDecimal porcentaje = Registry.advancePct
    //BigDecimal amountSecondMinimum = (secondOrder.total.subtract(secondOrder.paid)).multiply(porcentaje)
    BigDecimal amountSecondMinimum = (secondOrder.total.multiply(porcentaje)).subtract(secondOrder.paid)
    BigDecimal toPay = BigDecimal.ZERO
    BigDecimal cuponAmount = BigDecimal.ZERO
    for(Payment pay : secondOrder.payments){
      if( !pay.paymentTypeId.equalsIgnoreCase('C1') && pay.paymentTypeId.startsWith('C') ){
       cuponAmount = cuponAmount.add( pay.amount )
      }
    }
    BigDecimal minimum = (order.total.multiply(porcentaje)).subtract(order.paid)
    if( order.paid.doubleValue() >= minimum.doubleValue() && (secondOrder.paid.subtract(cuponAmount)).doubleValue() < amountSecondMinimum.doubleValue() ){
      if( isOtherAmount ){
        BigDecimal amountTmp = minimumAmount.subtract(amountSecondMinimum)
        if( amountTmp.compareTo(BigDecimal.ZERO) >= 0 ){
          toPay = amountTmp
        } else {
          toPay = BigDecimal.ZERO
        }
      } else {
        toPay = BigDecimal.ZERO
      }
    } else if( (secondOrder.paid.doubleValue()-cuponAmount.doubleValue()) >= amountSecondMinimum.doubleValue() ){
      if( order.paid.doubleValue() < order.total ){
        if( minimumAmount.doubleValue() <= (order.due) ){
          toPay = minimumAmount
        } else {
          toPay = order.total.subtract(order.paid)
        }
      } else {
        toPay = BigDecimal.ZERO
      }
    } else {
      if( minimumAmount.compareTo(minimum) <= 0 ){
        toPay = minimumAmount
      } else {
        if( minimumAmount.doubleValue() >= order.total.subtract(order.paid) ){
          toPay = order.total.subtract(order.paid)
        } else {
          BigDecimal amountToPay = BigDecimal.ZERO
          BigDecimal amountSecond = amountSecondMinimum.subtract(secondOrder.paid.subtract(cuponAmount))
          amountToPay = minimumAmount.subtract( amountSecond )
          if( amountToPay.doubleValue().compareTo(minimum) < 0 ){
            amountToPay = minimum
          }
          toPay = amountToPay//minimum.subtract(order.paid)
        }
      }
    }
    return toPay
  }

  private BigDecimal amountToPaySecondOrder( Order order ){
    BigDecimal toPay = BigDecimal.ZERO
    BigDecimal porcentaje = Registry.advancePct
    BigDecimal minimum = order.total.multiply(porcentaje)
    if( order.paid <= minimum.doubleValue() ){
      if( minimumAmount.compareTo(minimum) <= 0 ){
        toPay = minimumAmount
      } else {
        if( minimumAmount.doubleValue() >= order.total.subtract(order.paid) ){
          toPay = order.total.subtract(order.paid)
        } else {
          toPay = minimum
        }
      }
    } else {
      if( minimumAmount.doubleValue() <= (order.due) ){
        toPay = minimumAmount
      } else {
        toPay = order.total.subtract(order.paid)
      }
    }

    return toPay
  }


    /*private BigDecimal otherAmountToPayFirstOrder( Order order ){
      BigDecimal amountForPay = BigDecimal.ZERO
      BigDecimal amountFirst = BigDecimal.ZERO
      BigDecimal toPay = BigDecimal.ZERO
      Order orderTmp = OrderController.updateFirstOrder( order.id )
      amountForPay = orderTmp.total.subtract(orderTmp.paid)
      String amountFormat =  this.amount.text.trim().replace( ",","" )
      if( amountFormat.length() > 0 && amountFormat.isNumber() ){
        try{
          toPay = new BigDecimal(NumberFormat.getInstance().parse( amountFormat ).doubleValue())
        } catch ( NumberFormatException ex ){
          println ex
        }
      }
      if( amountForPay.compareTo(toPay) >= 0 ){
        amountFirst = toPay
      } else {
        amountFirst = amountForPay
      }
      return amountFirst
    }



    private BigDecimal otherAmountToPaySecondOrder( Order order ){
      BigDecimal amountForPay = BigDecimal.ZERO
      BigDecimal amountSecond = BigDecimal.ZERO
      BigDecimal toPay = BigDecimal.ZERO
      Order orderFirst = OrderController.updateFirstOrder( this.order.id )
      Order orderTmp = OrderController.updateFirstOrder( order.id )
      toPay = this.minimumAmount
      toPay = toPay.subtract( orderFirst.total.subtract(orderFirst.paid) )
      amountForPay = orderTmp.total.subtract(orderTmp.paid)
      if(toPay.compareTo(BigDecimal.ZERO) > 0){
        amountSecond = toPay
      }
      return amountSecond
    }*/


  private boolean isValid( Order order ) {
    boolean valid = true
    NotEmptyVerifier notEmptyVerifier = new NotEmptyVerifier()
    IsSelectedVerifier isSelectedVerifier = new IsSelectedVerifier()
    /*if ( tmpPayment.amount > 0 ) {
      Double diff = tmpPayment.amount.doubleValue() - order.due.doubleValue()
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
    }*/
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



    private Boolean flujoImprimir(Order order, Boolean secondOrder) {
      Boolean save = false
      doBindings()
      saveOrder( order, secondOrder )
      save = true
      return save
    }

  private saveOrder( Order order, Boolean second ){
      BigDecimal amountFirst = BigDecimal.ZERO
      BigDecimal amountSecond = BigDecimal.ZERO
      Payment firstPay = tmpPayment
      Payment secondPay = tmpPayment
      BigDecimal totalOrders = order.total.add(secondOrder.total)
      BigDecimal totalPaids = OrderController.amountPayments( order.id, secondOrder.id )
      order = OrderController.updateFirstOrder( order.id )
      secondOrder = OrderController.updateFirstOrder( secondOrder.id )
      firstPay.amount = amountToPayFirstOrder( order )
      secondPay.amount = amountToPaySecondOrder( secondOrder )

      tmpPayment.setAmount( amountFirst )
        if ( isValid( order ) && !second ) {
          amountFirst = amountToPayFirstOrder( order )
          firstPay.setAmount( amountFirst )
          this.minimumAmount = this.minimumAmount.subtract( amountFirst )
          pagoN = OrderController.addPaymentToOrder( order.id, firstPay )
      }
      amountSecond = amountToPaySecondOrder( secondOrder )
      if ( isValid( order ) && amountSecond > 0 && second ) {
        //amountSecond = amountToPaySecondOrder( secondOrder )
        secondPay.setAmount( amountSecond )
        tmpPayment = secondPay
        pagoN = OrderController.addPaymentToOrder( secondOrder.id, secondPay )
        dispose()
      } else {
        dispose()
      }
  }


}
