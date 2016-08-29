package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.CancellationController
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.PaymentController
import mx.lux.pos.ui.model.Coupons
import mx.lux.pos.ui.model.DevBank
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.model.Payment
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.Contact

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.List

class TotalCancellationDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()
  private static final String DATE_FORMAT = 'dd-MM-yyyy'
  private static final String GENERICO_ARMAZON = 'A'
  private static final String TAG_SURTE_PINO = 'P'
  private static final String TAG_RAZON_CAMBIO_FORMA_PAGO = 'CAMBIO DE FORMA DE PAGO'
  private static final String TAG_CANCELADA = 'T'
  private static final Integer TAG_TIPO_TRANS_DEV = 2

  private JTextField txtBill
  private JLabel lblVerifTarjeta
  private JLabel lblVerifMaterial

  private JRadioButton rbCheck
  private JRadioButton rbBankTransf
  private static ButtonGroup bgDev
  private JTextField txtName
  private JTextField txtEmail
  private JComboBox cbBank
  private JTextField txtClaveAccount
  private JLabel lblSlash
  private JTextField txtClaveAccount1
  private JPanel pnlDevCash
  private JPanel pnlDevOriginal
  private JLabel lblBank
  private JLabel lblClaveAccount

  private Boolean hasTC = false
  private Order order
  private JScrollPane scrollPane
  private JScrollPane scrollPaneCoupons
  private DefaultTableModel couponsDetModel
  private JTable tblDetCoupons
  private List<Payment> payments
  private List<Coupons> coupons
  private List<Coupons> couponsDet
  private String devAmount
  private String devAmountTd
  private BigDecimal paymentsAmount = BigDecimal.ZERO
  private BigDecimal couponsAmount = BigDecimal.ZERO
  private String couponsAmountTmp = ""
  private String totalPayments = ""
  private String strPayments = ""
  private List<DevBank> devBank

  private String email
  NumberFormat formatter = NumberFormat.getCurrencyInstance( Locale.US )

  private String TAG_FORMA_PAGO_TC = "TC"
  private String TAG_FORMA_PAGO_TD = "TD"
  private String TAG_FORMA_PAGO_EF = "EF"
  private String TAG_FORMA_PAGO_C1 = "C1"

  private String TAG_DESC_FORMA_PAGO_EF = "EFECTIVO"
  private String TAG_DESC_FORMA_PAGO_TC = "TARJETA CREDITO"
  private String TAG_DESC_FORMA_PAGO_TD = "TARJETA DEBITO"
  private String TAG_DESC_FORMA_PAGO_C1 = "REDENCION SEGUROS"

  public boolean button = false
  public Boolean askAuth
  public Boolean alreadyCanc

    TotalCancellationDialog( Component parent, String orderId, Boolean transf, Boolean askAuth ) {
    order = OrderController.getOrder( orderId )
    email = CustomerController.findCustomerEmail( order.customer.id )
    payments = PaymentController.findPaymentsToCancellByOrderId( orderId ) as List<Payment>
    devBank = OrderController.findDevBanks( )
    alreadyCanc = transf
    this.askAuth = askAuth
    for(Payment payment : payments){
      paymentsAmount = paymentsAmount.add(payment.amount)
      if(StringUtils.trimToEmpty(payment.paymentTypeId).equalsIgnoreCase(TAG_FORMA_PAGO_TC) ){
        hasTC = true
      }
    }
    totalPayments = formatter.format(paymentsAmount)
    coupons = OrderController.appliedCouponsByOrderSource( order.bill )
    for(Coupons cuponMv : coupons){
      couponsAmount = couponsAmount.add(cuponMv.monto)
    }
    couponsAmountTmp = formatter.format(couponsAmount)
    couponsDet = new ArrayList<>()
    devAmount = devAmount()
    buildUI( parent )
    doBindings()
  }

  // UI Layout Definition
  void buildUI( Component parent ) {
    sb.dialog( this,
        title: "Datos de la factura ${order.bill}",
        location: parent.locationOnScreen,
        resizable: true,
        pack: true,
        modal: true,
        layout: new MigLayout( 'fill,wrap', '[]', '[fill]' )
    ) {
      panel( autoscrolls: true ) {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap", "[fill,grow]", "[]0[]" ), autoscrolls: true ) {
          def displayFont = new Font( '', Font.BOLD, 14 )
          label( text: "Pagos:" )
          scrollPane = scrollPane( constraints: 'h 80!,hidemode 3' ) {
            table( selectionMode: ListSelectionModel.SINGLE_SELECTION ) {
              tableModel( list: payments ) {
                closureColumn( header: 'Forma Pago', read: {Payment pmt -> pmt?.paymentType} )
                closureColumn( header: 'Monto', read: {Payment pmt -> pmt?.amount}, cellRenderer: new MoneyCellRenderer() )
              }
            }
          }
            panel( layout: new MigLayout( 'wrap', '[grow,right]', '[]' ) ) {
              label( text: "Total: ${totalPayments}" )
            }

            label( text: "Cupones Aplicados:", visible: coupons.size() > 0 )
            scrollPane( constraints: 'h 60!,hidemode 3', visible: coupons.size() > 0 ) {
                table( selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doSelect ) {
                    tableModel( list: coupons ) {
                        closureColumn( header: 'Factura', read: {Coupons cmv -> cmv?.billApplied}, maxWidth: 70 )
                        closureColumn( header: 'Fecha', read: {Coupons cmv -> cmv?.dateApplied}, cellRenderer: new DateCellRenderer(), maxWidth: 90 )
                        closureColumn( header: 'Monto', read: {Coupons cmv -> cmv?.monto}, cellRenderer: new MoneyCellRenderer(), maxWidth: 70 )
                        closureColumn( header: 'Saldo', read: {Coupons cmv -> cmv?.balance}, maxWidth: 70 )
                        closureColumn( header: 'Fecha Entrega', read: {Coupons cmv -> cmv?.dateDeliver}, cellRenderer: new DateCellRenderer(), maxWidth: 150 )
                    }
                }
            }
            panel( layout: new MigLayout( 'wrap', '[grow,right]', '[]' ) ) {
              label( text: "Total Cupones: ${couponsAmountTmp}", visible: coupons.size() > 0, constraints: 'hidemode 3' )
            }

            label( text: "Detalles:", visible: coupons.size() > 0, constraints: 'hidemode 3' )
            scrollPaneCoupons = scrollPane( constraints: 'h 50!,hidemode 3', visible: coupons.size() > 0 ) {
                tblDetCoupons = table( selectionMode: ListSelectionModel.SINGLE_SELECTION ) {
                  couponsDetModel = tableModel( list: couponsDet ) {
                        closureColumn( header: 'Cliente', read: {Coupons cmv -> cmv?.client}, maxWidth: 150 )
                        closureColumn( header: 'Articulos', read: {Coupons cmv -> cmv?.articles}, maxWidth: 150 )
                        closureColumn( header: 'Pagos', read: {Coupons cmv -> cmv?.payments}, maxWidth: 200 )
                    } as DefaultTableModel
                }
            }
          pnlDevOriginal = panel( border: titledBorder( title: 'DEVOLUCION' ), layout: new MigLayout( 'wrap', '[grow,center]', '[]1[]' ),
                  constraints: 'hidemode 3', visible: StringUtils.trimToEmpty(devAmount).length() > 0 ) {
            //label( text: "DEVOLUCION:", font: displayFont )
            label( text: devAmount, font: displayFont )
          }
          pnlDevCash = panel( border: titledBorder( title: 'DEVOLUCION EFECTIVO' ), layout: new MigLayout( 'wrap 2', '[fill,grow][fill,grow]', '[]1[]' ),
                  constraints: 'hidemode 3', visible: StringUtils.trimToEmpty(devAmountTd).length() > 0 ) {
            panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap', '[]', '[]' ) ) {
              //label( text: "DEVOLUCION:", font: displayFont )
              label( text: devAmountTd, font: displayFont )
            }
            panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap', '[]', '[]' ), constraints: 'hidemode 3' ) {
              bgDev = buttonGroup()
              rbCheck = radioButton( text: "Cheque", buttonGroup: bgDev, selected: true )
              rbBankTransf = radioButton( text: "Transferencia Bancaria", buttonGroup: bgDev )
              rbCheck.addActionListener( new ActionListener() {
                  @Override
                  void actionPerformed(ActionEvent e) {
                    if( rbCheck.selected ){
                      cbBank.visible = false
                      txtClaveAccount.visible = false
                      txtClaveAccount1.visible = false
                      lblSlash.visible = false
                      lblBank.visible = false
                      lblClaveAccount.visible = false
                    }
                  }
              })
              rbBankTransf.addActionListener( new ActionListener() {
                  @Override
                  void actionPerformed(ActionEvent e) {
                    cbBank.visible = true
                    txtClaveAccount.visible = true
                    txtClaveAccount1.visible = true
                    lblSlash.visible = true
                    lblBank.visible = true
                    lblClaveAccount.visible = true
                  }
              })
            }
          }
          panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap 4', '[fill][fill,grow][][fill,grow]', '[]' ),
                  visible: StringUtils.trimToEmpty(devAmountTd).length() > 0, constraints: 'hidemode 3' ) {
            label( text: "Nombre:" )
            txtName = textField( text: order.customer.onlyFullName, constraints: 'span 3' )
            lblBank = label( text: "Banco:", visible: false )
            cbBank = comboBox( items: devBank*.name, constraints: 'span 3', visible: false )
            lblClaveAccount = label( text: "Cta./CLABE:", visible: false )
            txtClaveAccount = textField( visible: false )
            lblSlash = label( text: "/", visible: false )
            txtClaveAccount1 = textField( visible: false )
            label( text: "Correo:" )
            txtEmail = textField( text: email, constraints: 'span 3' )
          }
          panel( constraints: 'hidemode 3', visible: hasTC, border: loweredEtchedBorder(), layout: new MigLayout( 'wrap', '[grow,center]', '[]' ) ) {
            lblVerifTarjeta = label( text: "    Verificar tarjeta y material del cliente.", constraints: 'hidemode 3', font: displayFont )
            //lblVerifMaterial = label( text: "        Verificar que el cliente traiga el material.", constraints: 'hidemode 3', font: displayFont )
          }
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Imprimir", preferredSize: UI_Standards.BUTTON_SIZE,
                    visible: coupons.size() > 0,
                    constraints: "hidemode 3",
                    actionPerformed: { onButtonPrint() }
            )
            button( text: "Aplicar", preferredSize: UI_Standards.BUTTON_SIZE,
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


  private void doBindings( ) {
    sb.build {
      //bean( txtBill, text: bind {order.bill} )
      bean( lblVerifTarjeta, visible: bind {hasTC} )
      couponsDetModel.fireTableDataChanged()
    }
  }


  private def doSelect = { MouseEvent ev ->
    if ( SwingUtilities.isLeftMouseButton( ev ) && ev.source.selectedElement != null ) {
      Coupons coupon = ev.source.selectedElement
        if( coupon != null ){
          couponsDet.clear()
          couponsDet.add( coupon )
          tblDetCoupons.setToolTipText( "${coupon.client}  ${coupon.articles}  ${coupon.payments}" )
          couponsDetModel.fireTableDataChanged()
      }
    }
  }

  protected String devAmount( ){
    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US)
    String amount = ""
    BigDecimal amountNbrEf = BigDecimal.ZERO
    BigDecimal amountNbrTc = BigDecimal.ZERO
    BigDecimal amountNbrTd = BigDecimal.ZERO
    BigDecimal amountNbrC1 = BigDecimal.ZERO
    Collections.sort(order.payments, new Comparator<Payment>() {
        @Override
        int compare(Payment o1, Payment o2) {
            return o1.paymentTypeId.compareTo(o2.paymentTypeId)
        }
    })
    for(Payment payment : order.payments){
      Boolean alreadyCanc = StringUtils.trimToEmpty(order.status).equalsIgnoreCase(TAG_CANCELADA)
      BigDecimal paymentForDev = alreadyCanc ? payment.refundable : payment.amount
      if( paymentForDev.doubleValue() >= couponsAmount ){
        String tipoPago = "EF"
        if( StringUtils.trimToEmpty(payment.paymentTypeOri).equalsIgnoreCase(TAG_FORMA_PAGO_TC) ){
          tipoPago = 'TC'
        }
        //amount = amount+", "+String.format( '$%.2f-%s',payment.amount.subtract(couponsAmount),tipoPago)
      if( StringUtils.trimToEmpty(payment.paymentTypeOri).equalsIgnoreCase(TAG_FORMA_PAGO_TC) ){
          amountNbrTc = (amountNbrTc.add(paymentForDev)).subtract(couponsAmount)
        } /*else if( StringUtils.trimToEmpty(payment.paymentTypeOri).equalsIgnoreCase(TAG_FORMA_PAGO_C1) ) {
          amountNbrC1 = (amountNbrC1.add(paymentForDev)).subtract(couponsAmount)
        } */else if( StringUtils.trimToEmpty(payment.paymentTypeOri).equalsIgnoreCase(TAG_FORMA_PAGO_TD) ||
              StringUtils.trimToEmpty(payment.paymentTypeOri).equalsIgnoreCase(TAG_FORMA_PAGO_EF)) {
          amountNbrTd = (amountNbrTd.add(paymentForDev)).subtract(couponsAmount)
        } else {
          amountNbrEf = (amountNbrEf.add(paymentForDev)).subtract(couponsAmount)
        }
      }
      couponsAmount = couponsAmount.doubleValue()-paymentForDev.doubleValue() < 0.00 ? BigDecimal.ZERO : couponsAmount.doubleValue()-paymentForDev.doubleValue()
    }
    amount = (amountNbrTc.doubleValue() > 0 ? String.format('$%.2f-%s',amountNbrTc,TAG_DESC_FORMA_PAGO_TC) : "")+" "+
             (amountNbrC1.doubleValue() > 0 ? String.format('$%.2f-%s',amountNbrC1,TAG_DESC_FORMA_PAGO_C1) : "")
    //devAmountTd = amountNbrTd.doubleValue() > 0 ? String.format('$%.2f-%s',amountNbrTd,TAG_DESC_FORMA_PAGO_TD) : ""
    devAmountTd = amountNbrTd.doubleValue() > 0 ? nf.format(amountNbrTd) : ""

    if( StringUtils.trimToEmpty(amount).length() <= 0 || StringUtils.trimToEmpty(amount).equalsIgnoreCase(",,,") ){
      amount = ''
    }

    return amount
  }


  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    if( validDevTd() ){
      DevBank selection = null
      for( DevBank devBank1 : devBank ){
        if( StringUtils.trimToEmpty(cbBank.selectedItem.toString()).equalsIgnoreCase(devBank1.name) ){
          selection = devBank1
        }
      }
      String selectedBank = (selection != null && cbBank.visible) ? selection.id.toString() : ""
      String dataDev = "${StringUtils.trimToEmpty(txtName.text)},${StringUtils.trimToEmpty(selectedBank)}," +
              "${StringUtils.trimToEmpty(txtClaveAccount.text)},${StringUtils.trimToEmpty(txtClaveAccount1.text)},${StringUtils.trimToEmpty(txtEmail.text)}"
      if( askAuth ){
        AuthorizationCanDialog authDialog = new AuthorizationCanDialog( this, "Cancelaci\u00f3n requiere autorizaci\u00f3n", order, dataDev, alreadyCanc )
        authDialog.show()
      } else {
        cancelWithoutAuth( dataDev )
      }
      dispose()
    }
  }

  protected void onButtonPrint( ) {
    List<String> dev = new ArrayList<>()
    if( StringUtils.trimToEmpty(devAmount).length() > 0 ){
      dev.add( devAmount )
    }
    if( StringUtils.trimToEmpty(devAmountTd).length() > 0 ){
      dev.add( devAmountTd+"-EFECTIVO" )
    }
    OrderController.printResumeCancCoupon( StringUtils.trimToEmpty(order.id), dev )
  }

  Boolean validDevTd( ){
    Boolean valid = true
    txtName.foreground = UI_Standards.NORMAL_FOREGROUND
    txtName.setBorder(BorderFactory.createEmptyBorder())
    txtEmail.foreground = UI_Standards.NORMAL_FOREGROUND
    txtEmail.setBorder(BorderFactory.createEmptyBorder())
    txtClaveAccount.foreground = UI_Standards.NORMAL_FOREGROUND
    txtClaveAccount.setBorder(BorderFactory.createEmptyBorder())
    txtClaveAccount1.foreground = UI_Standards.NORMAL_FOREGROUND
    txtClaveAccount1.setBorder(BorderFactory.createEmptyBorder())
    cbBank.foreground = UI_Standards.NORMAL_FOREGROUND
    cbBank.setBorder(BorderFactory.createEmptyBorder())
    if( pnlDevCash.visible ){
      String pattern= '[A-Za-z0-9]+';
      if( StringUtils.trimToEmpty(txtName.text).length() <= 0 ||
            !StringUtils.trimToEmpty(txtName.text).replace(" ","").matches(pattern) ){
            valid = false
            txtName.foreground = UI_Standards.WARNING_FOREGROUND
            txtName.setBorder(BorderFactory.createLineBorder(Color.RED))
      }

      if( StringUtils.trimToEmpty(txtEmail.text).length() <= 0 ){
            valid = false
            txtEmail.foreground = UI_Standards.WARNING_FOREGROUND
            txtEmail.setBorder(BorderFactory.createLineBorder(Color.RED))
            //txtEmail.text = "DATO OBLIGATORIO"
      } else {
            String[] emailData = StringUtils.trimToEmpty(txtEmail.text).split("@")
            if( emailData.length != 2 ){
                /*if( !StringUtils.trimToEmpty(emailData[0]).matches(pattern) ||
                        !StringUtils.trimToEmpty(emailData[1]).matches(pattern) ){*/
                valid = false
                txtEmail.foreground = UI_Standards.WARNING_FOREGROUND
                txtEmail.setBorder(BorderFactory.createLineBorder(Color.RED))
                //txtEmail.text = "FORMATO INCORRECTO"
                //}
            } /*else {
        txtEmail.foreground = UI_Standards.WARNING_FOREGROUND
        txtEmail.text = "FORMATO INCORRECTO"
      }*/
      }
      if( txtClaveAccount.visible ){
            if( StringUtils.trimToEmpty(txtClaveAccount.text).length() <= 0 ){
                valid = false
                txtClaveAccount.foreground = UI_Standards.WARNING_FOREGROUND
                txtClaveAccount.setBorder(BorderFactory.createLineBorder(Color.RED))
                //txtClaveAccount.text = "DATO OBLIGATORIO"
            } else {
                if( !StringUtils.trimToEmpty(txtClaveAccount.text).isNumber() ){
                    valid = false
                    txtClaveAccount.foreground = UI_Standards.WARNING_FOREGROUND
                    txtClaveAccount.setBorder(BorderFactory.createLineBorder(Color.RED))
                    //txtClaveAccount.text = "VERIFIQUE LOS DATOS"
                }
            }

            if(StringUtils.trimToEmpty(txtClaveAccount1.text).length() <= 0){
                valid = false
                txtClaveAccount1.foreground = UI_Standards.WARNING_FOREGROUND
                txtClaveAccount1.setBorder(BorderFactory.createLineBorder(Color.RED))
                //txtClaveAccount1.text = "DATO OBLIGATORIO"
            } else if( (StringUtils.trimToEmpty(txtClaveAccount1.text).length() < 18 ||
                    StringUtils.trimToEmpty(txtClaveAccount1.text).length() < 18) ){
                valid = false
                txtClaveAccount1.foreground = UI_Standards.WARNING_FOREGROUND
                txtClaveAccount1.setBorder(BorderFactory.createLineBorder(Color.RED))
                //txtClaveAccount1.text = "FORMATO INCORRECTO"
            } else {
                if( !StringUtils.trimToEmpty(txtClaveAccount1.text).isNumber() ){
                    valid = false
                    txtClaveAccount1.foreground = UI_Standards.WARNING_FOREGROUND
                    txtClaveAccount1.setBorder(BorderFactory.createLineBorder(Color.RED))
                    //txtClaveAccount1.text = "VERIFIQUE LOS DATOS"
                }
            }
      }

      if( cbBank.visible && cbBank.selectedItem == null ){
            valid = false
            cbBank.foreground = UI_Standards.WARNING_FOREGROUND
          cbBank.setBorder(BorderFactory.createLineBorder(Color.RED))
      }
    }
    return valid
  }



  void cancelWithoutAuth( String dataDev ){
    String reasonCan = CancellationController.reasonCancellation( StringUtils.trimToEmpty(order.id) )
    if ( allowLateCancellation() ) {
          boolean authorized = true
          CancellationController.reassignCoupons( org.apache.commons.lang3.StringUtils.trimToEmpty(order.id) )
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
                          printCancellationNotToday( orderCom, creditRefunds, dataDev )
                      }
                  }
      try{
        if( !StringUtils.trimToEmpty(reasonCan).equalsIgnoreCase(TAG_RAZON_CAMBIO_FORMA_PAGO) ){
          CancellationController.sendCancellationOrderLc( org.apache.commons.lang3.StringUtils.trimToEmpty( order.bill ) )
        }
        Order newOrder = OrderController.findOrderByIdOrder( order.id )
        CancellationController.registerLogAuth( StringUtils.trimToEmpty(order.id), TAG_TIPO_TRANS_DEV, -1 )
        OrderController.runScriptBckpOrder( newOrder )
      } catch ( Exception e ){
        println e
      }
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



    private def printCancellationNotToday(Order orderCom, Map<Integer, String> creditRefunds, String dataDev){
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

                } else {
                }
            } else {

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
