package mx.lux.pos.ui.view.dialog

import com.sun.java.util.jar.pack.Attribute.FormatException
import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.model.Articulo
import mx.lux.pos.model.CuponMv
import mx.lux.pos.model.IPromotionAvailable
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.Parametro
import mx.lux.pos.model.PromotionAvailable
import mx.lux.pos.model.PromotionDiscount
import mx.lux.pos.model.TipoParametro
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.MainWindow
import mx.lux.pos.ui.controller.CancellationController
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.DailyCloseController
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.PaymentController
import mx.lux.pos.ui.model.Branch
import mx.lux.pos.ui.model.IPromotion
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.model.OrderLinePromotion
import mx.lux.pos.ui.model.Payment
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.driver.PromotionDriver
import mx.lux.pos.ui.view.panel.OrderPanel
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.MouseEvent
import java.util.List

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class MultypaymentDialog extends JDialog implements FocusListener {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JLabel lblFirstBill
  private JLabel lblSecondBill
  private JLabel lblAmountFirst
  private JLabel lblAmountSecond
  private JLabel lblAmountCupon
  private JLabel lblAmountBalance
  private JLabel lblAmountTotalBalance

  private ButtonGroup bgAmountPayment
  private JRadioButton rbMinimum
  private JRadioButton rbTotal
  private JRadioButton rbOther
  private JTextField txtMinimum
  private JTextField txtTotal
  private JTextField txtOther
  private JLabel lblWarning
  private JButton btnCerrar

  private JTextField txtByPay
  private DefaultTableModel paymentsModel
  private Order firstOrder
  private Order secondOrder
  private Integer idClient

  private BigDecimal amountCupon = BigDecimal.ZERO
  private BigDecimal amountBalance = BigDecimal.ZERO
  private BigDecimal amountBalanceTotal = BigDecimal.ZERO

  private BigDecimal minimumAmount = BigDecimal.ZERO
  private BigDecimal totalAmount = BigDecimal.ZERO

  private List<Payment> lstPayments = new ArrayList<Payment>()
  private BigDecimal forPay = BigDecimal.ZERO
  private BigDecimal amountPayments = BigDecimal.ZERO

  private String armazonString = null
  private Boolean advanceOnlyInventariable
  private BigDecimal amountCuponSecondOrder
  private Boolean validClave = true

  private static final TAG_GENERICO_B = 'B'
  private static final TAG_GENERICO_A = 'A'
  private static final TAG_SUBTYPE_N = 'N'
  private static final TAG_C1 = 'C1'

  public boolean button = false

    MultypaymentDialog( Integer idCliente, Order firstOrder, Order secondOrder, OrderPanel orderPanel ) {
    this.firstOrder = firstOrder
    this.secondOrder = secondOrder
    this.idClient = idCliente
    Boolean hasPaymentCupon = false
    hasPaymentCupon = OrderController.hasCuponMv( secondOrder.id )
    List<CuponMv> cuponMv = OrderController.obtenerCuponMvByTargetOrder( StringUtils.trimToEmpty(firstOrder.id) )
    if( cuponMv.size() > 0 ){
      validClave = false
    }


    if( !hasPaymentCupon && validClave ){
      OrderController.deletePromotion( StringUtils.trimToEmpty(secondOrder.id) )
      amountCuponSecondOrder = OrderController.getCuponAmount(firstOrder.id )
      PromotionDriver promotionDriver = PromotionDriver.instance
      updateOrder()
      promotionDriver.addCouponDiscount( this.secondOrder, amountCupon().amount, firstOrder.id, amountCuponSecondOrder )
      orderPanel.promotionDriver.requestPromotionSave(secondOrder?.id, false)
      //OrderController.addPaymentToOrder( secondOrder.id, amountCupon() )
      updateOrder()
    }
    lstPayments = OrderController.listPayments( firstOrder, secondOrder )
    buildUI()
    doBindings()
    updateForPay()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Captura de Pagos",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 360, 680 ],
        location: [ 70, 35 ],
        layout: new MigLayout( 'fill', '[fill,grow]', '[fill]' ),
        undecorated: true
    ) {
      NumberFormat formatter = NumberFormat.getInstance( Locale.US )
      panel(layout: new MigLayout( "wrap","[fill,grow]","" )) {
        //borderLayout()
        panel( border: titledBorder( "Ventas" ), layout: new MigLayout( "wrap 3", "5[grow,fill][grow,fill][grow,fill]5", "[][]" ) ) {
          label( text: "1er. Par", border: titledBorder( "" ) )
          lblFirstBill = label( text: " ", border: titledBorder( "" ) )
          lblAmountFirst = label( text: " ", border: titledBorder( "" ) )
          label( text: "2do. Par", border: titledBorder( "" ) )
          lblSecondBill = label( text: " ", border: titledBorder( "" ) )
          lblAmountSecond = label( text: " ", border: titledBorder( "" ) )
          label( )
          label( text: "Cupon", border: titledBorder( "" ) )
          lblAmountCupon = label( text: " ", border: titledBorder( "" ))
          label( )
          label( "Saldo", border: titledBorder( "" ) )
          lblAmountBalance = label( text: " ", border: titledBorder( "" ) )
          label( text: "Saldo Total: ", constraints: "span 2", border: titledBorder( "" ) )
          lblAmountTotalBalance = label( text: " ", border: titledBorder( "" ) )
        }


        panel( border: titledBorder( "Por Pagar" ), layout: new MigLayout( "wrap 3", "5[][grow,fill][grow,fill]5", "[][]" ) ) {
          bgAmountPayment = buttonGroup( )
          rbMinimum = radioButton( border: titledBorder( "" ), buttonGroup: bgAmountPayment,
                  actionPerformed: {onRadioButton()})
          rbMinimum.addActionListener( new ActionListener() {
              @Override
              void actionPerformed(ActionEvent ev) {
                doBindings()
              }
          })
          label( text: "Pago Minimo" )
          txtMinimum = textField( " ", editable: false )
          rbTotal = radioButton( border: titledBorder( "" ), buttonGroup: bgAmountPayment,
                  selected: true, actionPerformed: {onRadioButton()} )
          rbTotal.addActionListener( new ActionListener() {
              @Override
              void actionPerformed(ActionEvent e) {
                  doBindings()
                  lblWarning.setText("")
              }
          })
          label( text: "Pago Total" )
          txtTotal = textField( " ", editable: false )
          rbOther = radioButton( border: titledBorder( "" ), buttonGroup: bgAmountPayment, actionPerformed: {onRadioButton()} )
          rbOther.addActionListener( new ActionListener() {
              @Override
              void actionPerformed(ActionEvent e) {
                  updateOrder()
                  updateForPay()
                  doBindings()
                  lblWarning.setText("")
                  onRadioButton()
              }
          })
          label( text: "Otra Cantidad" )
          txtOther = textField( " ", editable: rbOther.selected )
          txtOther.addFocusListener(this)
          lblWarning = label( constraints: 'span', foreground: UI_Standards.WARNING_FOREGROUND )
        }

        panel( border: titledBorder( "Pagos" ), layout: new MigLayout( "wrap 2", "5[][grow,fill]5", "[][]" ) ) {
          label( "Por Pagar" )
          txtByPay = textField( " ", editable: false )
          scrollPane(border: titledBorder(title: ''), constraints: 'span 2', mouseClicked: doNewPaymentClick) {
            table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowPaymentClick) {
                paymentsModel = tableModel(list: lstPayments) {
                  closureColumn(header: 'Descripci\u00f3n', read: { Payment tmp -> tmp?.description })
                  closureColumn(header: 'Monto', read: { Payment tmp -> tmp?.amount }, maxWidth: 100, cellRenderer: new MoneyCellRenderer())
                } as DefaultTableModel
            }
          }
        }

        panel(  ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Imprimir", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonOk() }
            )
            button( text: "Cerrar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonCancel() }
            )
          }
        }
      }
    }
  }


  private void doBindings() {
    sb.build {
        amountOrders()
        amountAmountPayments()
        amountTotalPayments()
        updateAmountPayments()
        updateForPay()
        BigDecimal amountDiscount = BigDecimal.ZERO
        for(IPromotion promo: this.secondOrder.deals){
          amountDiscount = amountDiscount.add(promo.descuento)
        }
        bean(lblFirstBill, text: bind { firstOrder.id })
        bean(lblSecondBill, text: bind { secondOrder.id })
        bean(lblAmountFirst, text: bind { String.format( "\$%,.2f", this.firstOrder.total) })
        bean(lblAmountSecond, text: bind { String.format( "\$%,.2f", this.secondOrder.total.add(amountDiscount)) })
        bean(lblAmountCupon, text: bind{String.format( "\$%,.2f", this.amountCupon )})
        bean(lblAmountBalance, text: bind{String.format( "\$%,.2f", this.amountBalance )})
        bean(lblAmountTotalBalance, text: bind{String.format( "\$%,.2f", this.amountBalanceTotal )})
        bean(txtMinimum, text: bind{String.format( "\$%,.2f", this.minimumAmount )})
        bean(txtTotal, text: bind{String.format( "\$%,.2f", this.totalAmount )})
        bean(txtByPay, text: bind{String.format( "\$%,.2f", this.forPay )})
        bean(paymentsModel.rowsModel, value: bind{ lstPayments })
    }
    paymentsModel.fireTableDataChanged()
   }
  // UI Management
  protected void refreshUI( ) {

  }

  // Public Methods
  void activate( ) {
    refreshUI()
    setVisible( true )
  }

  void setDefaultDates( Date pDateStart, Date pDateEnd ) {

  }

  // UI Response
  protected void onButtonCancel( ) {
    button = false
    updateOrder()
    if( StringUtils.trimToEmpty(firstOrder.bill).length() <= 0 ){
      for(Payment payment : firstOrder.payments){
        OrderController.removePaymentFromOrder( firstOrder.id, payment )
      }
    }
    if( StringUtils.trimToEmpty(secondOrder.bill).length() <= 0 ){
      for(Payment payment : secondOrder.payments){
        OrderController.removePaymentFromOrder( secondOrder.id, payment )
      }
      OrderController.deleteCuponMultypayment( StringUtils.trimToEmpty(secondOrder.id) )
    }
    dispose()
  }

  protected void onButtonOk( ) {
      //WaitDialog dialog = new WaitDialog( "Multipago", "Guardando informacion de las ventas." )
      //sb.doOutside {
          Boolean succesSell = false
          Boolean valid1 = false
          Boolean valid2 = false
          firstOrder = OrderController.updateFirstOrder( firstOrder.id )
          secondOrder = OrderController.updateFirstOrder( secondOrder.id )
          valid1 = validPayments( firstOrder )
          if( valid1 ){
              valid2 = validPayments( secondOrder )
          }
          if( forPay.compareTo(BigDecimal.ZERO) <= 0 ){
              if( valid1 && valid2 ){
                  succesSell = printOrder()
                  CustomerController.deletedClienteProceso( idClient )
                  button = true
                  dispose()
              }
          } else {
              sb.optionPane(
                      message: 'Debe cubrirse el saldo por pagar',
                      messageType: JOptionPane.ERROR_MESSAGE
              ).createDialog(this, 'Pagos')
                      .show()
          }
          //dialog.dispose()
          /*if ( succesSell ) {
              sb.optionPane().showMessageDialog( null, 'Se generaron las ventas correctamente', 'Ok', JOptionPane.INFORMATION_MESSAGE )
              dispose()
          } else {
              sb.optionPane().showMessageDialog( null, 'Error al generar las ventas', 'Error', JOptionPane.ERROR_MESSAGE )
          }*/
      //}
      //dialog.show()
  }


  protected void onRadioButton( ) {
    if( rbMinimum.selected ){
      txtOther.editable = false
      txtOther.text = ''
    }
    if( rbTotal.selected ){
      txtOther.editable = false
      txtOther.text = ''
    }
    if( rbOther.selected ){
      txtOther.editable = true
    }
  }


  private void amountOrders (){
    Boolean couponLc = false
    for(OrderItem det : firstOrder.items){
      if( StringUtils.trimToEmpty(det.item.type).equalsIgnoreCase("H") ){
        couponLc = true
      }
    }
    BigDecimal firstCupon = OrderController.getCuponAmount( firstOrder.id )
    BigDecimal secondCupon = OrderController.getCuponAmount( secondOrder.id )
    if( couponLc && firstCupon.compareTo(BigDecimal.ZERO) > 0 ){
      amountCupon = firstOrder.deals.size() <= 0 ? firstCupon : BigDecimal.ZERO
    } else {
      amountCupon = validClave ? Math.max(firstCupon,secondCupon) : BigDecimal.ZERO
    }
    amountBalance = (secondOrder.total).compareTo(BigDecimal.ZERO) > 0 ? secondOrder.total : BigDecimal.ZERO
    amountBalanceTotal = firstOrder.total.add(amountBalance)
  }


  private Payment amountCupon (){
    Boolean couponLc = false
    for(OrderItem det : firstOrder.items){
      if( StringUtils.trimToEmpty(det.item.type).equalsIgnoreCase("H") ){
        couponLc = true
      }
    }
    BigDecimal firstCupon = OrderController.getCuponAmount( firstOrder.id )
    BigDecimal secondCupon = OrderController.getCuponAmount( secondOrder.id )
    BigDecimal amountCupon = BigDecimal.ZERO
    if( couponLc && firstCupon.compareTo(BigDecimal.ZERO) > 0 ){
      amountCupon = firstCupon
    } else {
      amountCupon = Math.max(firstCupon,secondCupon)
    }
    Payment payment = new Payment()
    payment.paymentReference = firstOrder.id
    payment.amount = amountCupon
    if( amountCupon.doubleValue() == new BigDecimal( 400 ).doubleValue() ){
      payment.paymentType = PaymentController.findTypePaymentByIdPago('C2')
      payment.paymentTypeId = 'C2'
    } else if( amountCupon.doubleValue() == new BigDecimal( 600 ).doubleValue() ){
      payment.paymentType = PaymentController.findTypePaymentByIdPago('C3')
      payment.paymentTypeId = 'C3'
    } else if( amountCupon.doubleValue() == new BigDecimal( 800 ).doubleValue() ){
      payment.paymentType = PaymentController.findTypePaymentByIdPago('C4')
      payment.paymentTypeId = 'C4'
    }
    if( payment.amount.doubleValue() > secondOrder.due ){
      payment.amount = secondOrder.due
    }
    return payment
  }


  private void amountAmountPayments (){
    minimumAmount =  OrderController.getMinimumPayment( firstOrder, secondOrder, amountCupon )
  }

  private void amountTotalPayments (){
    totalAmount = amountBalanceTotal
  }

  private void amountByPay (){
    forPay = amountBalanceTotal.subtract(amountPayments)
  }

  public void focusGained(FocusEvent e) {

  }

  public void focusLost(FocusEvent e) {
    updateOtherAmountField( )
  }

  private def doShowPaymentClick = { MouseEvent ev ->
    if (SwingUtilities.isLeftMouseButton(ev)) {
      Payment payment = ev.source.selectedElement as Payment
        if (ev.clickCount == 2) {
          Order order = new Order()
          order = OrderController.findOrderByIdOrder( payment.order )
          new PaymentMultyDialog(ev.component, order, payment).show()
          updateForPay()
          doBindings()
        }
    }
  }

  private def doNewPaymentClick = { MouseEvent ev ->
    if (SwingUtilities.isLeftMouseButton(ev)) {
        if (ev.clickCount == 1) {
            updateOtherAmountField()
            firstOrder = OrderController.updateFirstOrder( firstOrder.id )
            secondOrder = OrderController.updateFirstOrder( secondOrder.id )
            if (firstOrder.due || secondOrder.due) {
                if( rbMinimum.selected ){
                  new PaymentMultyDialog( ev.component, firstOrder, secondOrder, null, forPay, false, false, false).show()
                } else if( rbTotal.selected ){
                  new PaymentMultyDialog( ev.component, firstOrder, secondOrder, null, forPay, false, true, false).show()
                } else if( rbOther.selected ){
                  BigDecimal otherAmount = BigDecimal.ZERO
                  String amount = txtOther.text.trim().replace( "\$",'' )
                  amount = amount.replace( ',','' )
                  if( amount.length() > 0 && amount.isNumber() ){
                    try{
                      otherAmount = new BigDecimal(NumberFormat.getInstance().parse(amount).doubleValue())
                    } catch (FormatException ex ){
                      println ex
                    }
                  }
                  if( otherAmount.doubleValue() > minimumAmount.doubleValue() ){
                    lblWarning.setText("")
                    new PaymentMultyDialog( ev.component, firstOrder, secondOrder, null, forPay, true, false, true).show()
                  } else {
                    lblWarning.setText( "El monto debe ser mayor al minimo" )
                  }
                }
                updateOrder()
                updateForPay()
                doBindings()
                onRadioButton()
            } else {
                sb.optionPane(
                        message: 'No hay saldo para aplicar pago',
                        messageType: JOptionPane.ERROR_MESSAGE
                ).createDialog(this, 'Pago sin saldo')
                        .show()
            }
        }
    }
  }


  private void updateForPay(){
    //if( lstPayments.size() > 0 ){
      amountPayments = BigDecimal.ZERO
    //}
    for(Payment pay : lstPayments){
      amountPayments = amountPayments.add( pay.amount )
    }
    if( rbMinimum.selected ){
      forPay = new BigDecimal(minimumAmount.doubleValue()-amountPayments.doubleValue())
    } else if( rbTotal.selected ){
      forPay = new BigDecimal((firstOrder.total.doubleValue()+secondOrder.total.doubleValue())-amountPayments.doubleValue())
    } else if( rbOther.selected ){
      String amountText = txtOther.text.trim().replace( "\$",'' )
      amountText = amountText.replace( ',','' )
      Double amount = 0.00
      if( amountText.trim().length() > 0 ){
        try{
          amount = NumberFormat.getInstance().parse(amountText.trim())
        } catch ( NumberFormatException ex ){
          println ex
        }
        forPay = amount - (amountPayments.doubleValue())
      } else {
        forPay = minimumAmount
      }
    }
    txtByPay.text = String.format( "\$%,.2f", forPay )
  }

  private void updateOrder(){
    firstOrder = OrderController.updateFirstOrder( firstOrder.id )
    secondOrder = OrderController.updateSecondOrder( secondOrder.id )
  }

  private void updateAmountPayments(){
    lstPayments = OrderController.listAllPayments( firstOrder, secondOrder )
  }



    private Boolean printOrder() {
      Boolean succes = false
        Integer artCount1 = 0
        Integer artCount2 = 0
        for(OrderItem itemFirst : firstOrder.items){
            String artString = itemFirst.item.name
            if (artString.trim().equals('SV')) {
                artCount1 = artCount1 + 1
            } else if (artString.trim().equals('B')) {
                artCount1 = artCount1 + 1
            } else if (artString.trim().equals('P')) {
                artCount1 = artCount1 + 1
            }
        }
        for(OrderItem itemFirst : secondOrder.items){
            String artString = itemFirst.item.name
            if (artString.trim().equals('SV')) {
                artCount2 = artCount2 + 1
            } else if (artString.trim().equals('B')) {
                artCount2 = artCount2 + 1
            } else if (artString.trim().equals('P')) {
                artCount2 = artCount2 + 1
            }
        }
        Parametro diaIntervalo = Registry.find(TipoParametro.DIA_PRO)
        Date diaPrometido = new Date() + diaIntervalo?.valor.toInteger()
        if( artCount1 > 0 ){
          OrderController.savePromisedDate(firstOrder?.id, diaPrometido)
        }
        if( artCount2 > 0 ){
            OrderController.savePromisedDate(secondOrder?.id, diaPrometido)
        }
        User user = Session.get(SessionItem.USER) as User
        String vendedor = user.username
        if( OrderController.showValidEmployee() ){
            CambiaVendedorDialog cambiaVendedor = new CambiaVendedorDialog(this,user?.username)
            cambiaVendedor.show()
            vendedor = cambiaVendedor?.vendedor
        }

        Order newOrder1 = OrderController.placeOrder(firstOrder, vendedor, true)
        Order newOrder2 = OrderController.placeOrder(secondOrder, vendedor, true)
        if( newOrder1.rx != null ){
            OrderController.updateExam( newOrder1 )
        }
        if( newOrder2.rx != null ){
            OrderController.updateExam( newOrder2 )
        }
        Boolean cSaldo = false
        Boolean askContactFirst = false
        Boolean askContactSecond = false
        Boolean contact = false
        for(OrderItem item : firstOrder.items){
          if(item.item.type.equalsIgnoreCase(TAG_GENERICO_B)){
            askContactFirst = true
            contact = true
          }
        }
        OrderController.validaEntregaMultipago(newOrder1?.bill.trim(),newOrder1?.branch?.id.toString(), true, contact)
        for(OrderItem item : secondOrder.items){
            if(item.item.type.equalsIgnoreCase(TAG_GENERICO_B)){
              askContactSecond = true
            }
        }
        if( !contact && askContactSecond ){
          contact = true
        } else {
          contact = false
        }
        OrderController.validaEntregaMultipago(newOrder2?.bill.trim(),newOrder2?.branch?.id.toString(), true, contact)
        OrderController.creaJb(newOrder1?.ticket.trim(), cSaldo)
        OrderController.creaJb(newOrder2?.ticket.trim(), cSaldo)
        OrderController.validaSurtePorGenerico( firstOrder )
        OrderController.validaSurtePorGenerico( secondOrder )
        ItemController.updateLenteContacto( firstOrder.id )
        ItemController.updateLenteContacto( secondOrder.id )
        if(isLc(newOrder1)){
            OrderController.creaJbLc( newOrder1.id )
        }
        if( isOnlyInventariable( newOrder1 ) ){
            OrderController.creaJbAnticipoInventariablesMultypayment( newOrder1?.id )
        }
        if(isLc(newOrder2)){
            OrderController.creaJbLc( newOrder2.id )
        }
        /*if( OrderController.hasOrderLc(newOrder1.bill) ){
            OrderController.createAcuse( newOrder1.id )
        }
        if( OrderController.hasOrderLc(newOrder2.bill) ){
            OrderController.createAcuse( newOrder2.id )
        }*/
        if( isOnlyInventariable( newOrder2 ) ){
            OrderController.creaJbAnticipoInventariables( newOrder2?.id )
        }
        OrderController.creaJbFam( newOrder1.id, newOrder2.id )
        if (StringUtils.isNotBlank(newOrder1?.id) && StringUtils.isNotBlank(newOrder2?.id)) {
            Branch branch = Session.get(SessionItem.BRANCH) as Branch
            OrderController.insertaAcuseAPAR(newOrder1, branch)
            OrderController.insertaAcuseAPAR(newOrder2, branch)
            Boolean montaje1 = false
            Boolean montaje2 = false
            List<OrderItem> items1 = newOrder1?.items
            List<OrderItem> items2 = newOrder2?.items
            Iterator iterator1 = items1.iterator()
            while (iterator1.hasNext()) {
                Item item = iterator1.next().item
                if (item?.name.trim().equals('MONTAJE')) {
                    montaje1 = true
                }
            }
            Iterator iterator2 = items2.iterator()
            while (iterator2.hasNext()) {
                Item item = iterator2.next().item
                if (item?.name.trim().equals('MONTAJE')) {
                    montaje2 = true
                }
            }
            if (montaje1 == true) {
                Boolean registroTmp = OrderController.revisaTmpservicios(newOrder1?.id)
                User u = Session.get(SessionItem.USER) as User
                if (registroTmp == false) {
                    CapturaSuyoDialog capturaSuyoDialog = new CapturaSuyoDialog(firstOrder, u,false)
                    capturaSuyoDialog.show()
                }

                OrderController.printSuyo(newOrder1,u)
            }

            if (montaje2 == true) {
                Boolean registroTmp = OrderController.revisaTmpservicios(newOrder2?.id)
                User u = Session.get(SessionItem.USER) as User
                if (registroTmp == false) {
                    CapturaSuyoDialog capturaSuyoDialog = new CapturaSuyoDialog(secondOrder, u,false)
                    capturaSuyoDialog.show()
                }

                OrderController.printSuyo(newOrder2,u)
            }

            List<CuponMv> cuponMv = OrderController.obtenerCuponMvByTargetOrder( StringUtils.trimToEmpty(firstOrder.id) )

            if( cuponMv.size() > 0 ){
                Integer numeroCupon = cuponMv.first().claveDescuento.startsWith("8") ? 2 : 3
                OrderController.updateCuponMv( cuponMv.first().facturaOrigen, firstOrder.id, cuponMv.first().montoCupon, numeroCupon, false)
              if( StringUtils.trimToEmpty(cuponMv.first().claveDescuento).startsWith("F") ){
                generatedCoupon( newOrder1, newOrder2 )
              }
            } else if( validClave ) {
              generatedCoupon( newOrder1, newOrder2 )
            }
            OrderController.printOrder(newOrder1.id)
            OrderController.printOrder(newOrder2.id)
            if (newOrder1.dioptra != null && newOrder1.dioptra.trim().length() > 0) {
                OrderController.printRx(newOrder1.id, false)
                OrderController.fieldRX(newOrder1.id)
            }
            if (newOrder2.dioptra != null && newOrder2.dioptra.trim().length() > 0) {
                OrderController.printRx(newOrder2.id, false)
                OrderController.fieldRX(newOrder2.id)
            }
            reviewForTransfers(newOrder1.id)
            reviewForTransfers(newOrder2.id)
            succes = true
            sb.doOutside {
                try{
                    OrderController.runScriptBckpOrder( newOrder1 )
                    OrderController.runScriptBckpOrder( newOrder2 )
                } catch ( Exception e ){
                    println e
                }
            }
        } else {
            sb.optionPane(
                    message: 'Ocurrio un error al registrar la venta, intentar nuevamente',
                    messageType: JOptionPane.ERROR_MESSAGE
            ).createDialog(this, 'No se puede registrar la venta')
                    .show()
        }
      return succes
    }





    private void reviewForTransfers(String newOrderId) {
        if (CancellationController.orderHasTransfers(newOrderId)) {
            List<Order> lstOrders = CancellationController.findOrderToResetValues(newOrderId)
            for (Order order : lstOrders) {
                CancellationController.resetValuesofCancellation(order.id)
            }
            List<String> sources = CancellationController.findSourceOrdersWithCredit(newOrderId)
            if (sources?.any()) {
                new TotalCancellationDialog( this, sources.first(), true, false ).show()
                //new RefundDialog(this, sources.first()).show()
                Boolean reuse = CancellationController.printReUse( newOrderId )
                if( !reuse ){
                    //CancellationController.printMaterialReception( sources.first() )
                    //CancellationController.printMaterialReturn( sources.first() )
                }
            } else {
                Boolean reuse = CancellationController.printCancellationsFromOrder(sources.first())
                if( !reuse ){
                    String idSource = CancellationController.findSourceOrder( newOrderId )
                    if( idSource.trim().length() > 0 ){
                        //CancellationController.printMaterialReception( idSource )
                        //CancellationController.printMaterialReturn( idSource )
                    }
                }
            }
        }
    }


    private Boolean validPayments( Order order ){
        Boolean save = false
        int artCount = 0
        String tipoArt = null
        Double pAnticipo = Registry.getAdvancePct()
        for (int row = 0; row < order.items.size(); row++) {
            String artString = order.items.get(row).item.name
            if (artString.trim().equals('SV')) {
              artCount = artCount + 1
            } else if (artString.trim().equals('B')) {
              artCount = artCount + 1
            } else if (artString.trim().equals('P')) {
              artCount = artCount + 1
            }
        }

        armazonString = null
        Boolean validOrder = true
        if (artCount != 0) {
            Boolean onlyInventariable = OrderController.validOnlyInventariable( order )
            if( onlyInventariable && order?.paid < order?.total ){
                AuthorizationDialog authDialog = new AuthorizationDialog(MainWindow.instance, "Anticipo de nota ${order.id} requiere autorizaci\u00f3n")
                authDialog.show()
                if (authDialog.authorized) {
                    advanceOnlyInventariable = true
                    validOrder = true
                } else {
                    validOrder = false
                    sb.optionPane(
                            message: 'Datos no validos',
                            messageType: JOptionPane.ERROR_MESSAGE
                    ).createDialog(this, 'No se puede registrar la venta')
                            .show()
                }
            } else if (order?.paid < (order?.total * pAnticipo)) {
                Boolean requierAuth = OrderController.requiereAuth( order )
                if( requierAuth ){
                    AuthorizationDialog authDialog = new AuthorizationDialog(MainWindow.instance, "Anticipo menor al permitido en orden ${order.id}, esta operacion requiere autorizaci\u00f3n")
                    authDialog.show()
                    if (authDialog.authorized) {
                        validOrder = true
                    } else {
                        validOrder = false
                        sb.optionPane(
                                message: 'El monto del anticipo en orden '+order.id+' tiene que ser minimo de: $' + (order?.total * pAnticipo),
                                messageType: JOptionPane.ERROR_MESSAGE
                        ).createDialog(this, 'No se puede registrar la venta')
                                .show()
                    }
                } else {
                    validOrder = false
                    sb.optionPane(
                            message: 'El monto del anticipo en orden '+order.id+' tiene que ser minimo de: $' + (order?.total * pAnticipo),
                            messageType: JOptionPane.ERROR_MESSAGE
                    ).createDialog(this, 'No se puede registrar la venta')
                            .show()
                }
            } else {
                validOrder = true
            }
        } else if( OrderController.validGenericNoDelivered( order.id ) ){
            Boolean requierAuth = OrderController.requiereAuth( order )
            if(order?.paid < (order?.total * pAnticipo)){
                if( requierAuth ){
                    AuthorizationDialog authDialog = new AuthorizationDialog(this, "Anticipo menor al permitido, esta operacion requiere autorizaci\u00f3n")
                    authDialog.show()
                    if (authDialog.authorized) {
                        validOrder = true
                    } else {
                        validOrder = false
                        sb.optionPane(
                                message: 'El monto del anticipo tiene que ser minimo de: $' + (order?.total * pAnticipo),
                                messageType: JOptionPane.ERROR_MESSAGE
                        ).createDialog(this, 'No se puede registrar la venta')
                                .show()
                    }
                } else {
                    validOrder = false
                    sb.optionPane(
                            message: 'El monto del anticipo tiene que ser minimo de: $' + (order?.total * pAnticipo),
                            messageType: JOptionPane.ERROR_MESSAGE
                    ).createDialog(this, 'No se puede registrar la venta')
                            .show()
                }
            } else {
                validOrder = true
            }
        } else {
            validOrder = true
        }
        if (validOrder) {
          Boolean hasKidFrame = false
          Boolean hasEnsureKid = false
          Boolean hasC1 = false
          for(Payment payment : order.payments){
            if( StringUtils.trimToEmpty(payment.paymentTypeId).equalsIgnoreCase(TAG_C1)){
              hasC1 = true
            }
          }
          for(OrderItem det : order.items){
            Articulo art = ItemController.findArticle( det.item.id )
            String type = StringUtils.trimToEmpty(art?.subtipo).length() > 0 ? StringUtils.trimToEmpty(art?.subtipo) : StringUtils.trimToEmpty(art?.idGenSubtipo)
            if( StringUtils.trimToEmpty(type).startsWith(TAG_SUBTYPE_N) ){
              hasKidFrame = true
            }
            if( StringUtils.trimToEmpty(det.item.name).equalsIgnoreCase("SEG") ){
              hasEnsureKid = true
            }
          }
          if( order.deals.size() > 0 ){
            println order.deals.first().descripcion
          }
          if( hasKidFrame && !hasEnsureKid && !hasC1 ){
            List<Item> results = ItemController.findItemsByQuery("SEG")
            if (results?.any()) {
              User user = Session.get(SessionItem.USER) as User
              String vendedor = user.username
              order = OrderController.addItemToOrder(order, results.first(), "")
              order = OrderController.placeOrder(order, vendedor, false)
              OrderController.insertSegKig = false
            }
          }
          Boolean warranty = false
          if( true ){
            NotaVenta notaWarranty = OrderController.ensureOrder( StringUtils.trimToEmpty(order.id) )
            warranty = OrderController.validWarranty( OrderController.findOrderByidOrder(StringUtils.trimToEmpty(order.id)), true, null, notaWarranty.id, false )
          } else {
            warranty = true
          }
          if( warranty ){
            Boolean noDelivered = OrderController.validGenericNoDelivered( order.id )
            Boolean onlyInventariable = OrderController.validOnlyInventariable( order )
            BigDecimal totalOrder = order?.total * pAnticipo
            BigDecimal diference =  minimumAmount.subtract(totalOrder.subtract(order.paid) )
            if( onlyInventariable && order?.paid < order?.total && !noDelivered ){
                AuthorizationDialog authDialog = new AuthorizationDialog(MainWindow.instance, "Anticipo en orden ${order.id} requiere autorizaci\u00f3n")
                authDialog.show()
                if (authDialog.authorized) {
                    advanceOnlyInventariable = true
                    save = true
                } else {
                    validOrder = false
                    sb.optionPane(
                            message: 'Datos no validos',
                            messageType: JOptionPane.ERROR_MESSAGE
                    ).createDialog(this, 'No se puede registrar la venta')
                            .show()
                }
            } else {
                save = true
            }
          } else {
            if( OrderController.MSJ_ERROR_WARRANTY.length() > 0 ){
              sb.optionPane(
                 message: 'Error al asignar seguro',
                 messageType: JOptionPane.ERROR_MESSAGE
              ).createDialog(this, OrderController.MSJ_ERROR_WARRANTY)
                .show()
            }
          }
        }
      return save
    }



  private Boolean isOnlyInventariable( Order order ){
    return OrderController.validOnlyInventariable( order )
  }


    private Boolean isLc(Order pOrder){
        Boolean isLc = false
        for(OrderItem item : pOrder.items){
            if(ItemController.esLenteContacto(item.item.id)){
                isLc = true
            }
        }
        return isLc
    }


  private void updateOtherAmountField(){
    if(txtOther.text.trim().length() > 0 && txtOther.text.isNumber()){
      Double amount = 0.00
      try{
        amount = NumberFormat.getInstance().parse(txtOther.text.trim())
      } catch ( NumberFormatException ex ){
        println ex
      }
      if( amount.compareTo(minimumAmount) > 0 ){
        txtOther.text = String.format( "\$%,.2f", amount)
        doBindings()
      } else {
        lblWarning.setText( "El monto debe ser mayor al minimo" )
        txtOther.setText( "" )
      }
    }
  }



  private void generatedCoupon ( Order newOrder1, Order newOrder2 ){
    OrderController.updateCuponMv( newOrder1.id, newOrder2.id, amountCuponSecondOrder, 2, false )
    if( Registry.tirdthPairValid() ){
      BigDecimal montoCupon = OrderController.getCuponAmountThirdPair( newOrder1.id )
      CuponMv cupon = OrderController.obtenerCuponMv( StringUtils.trimToEmpty(newOrder1.bill), "", montoCupon, 3 )
      if( montoCupon.compareTo(BigDecimal.ZERO) > 0 ){
        OrderController.printCuponTicket(cupon, "CUPON TERCER PAR", montoCupon)
      }
    }
  }


}
