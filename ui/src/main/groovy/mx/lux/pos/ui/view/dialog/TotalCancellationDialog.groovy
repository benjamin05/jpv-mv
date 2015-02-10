package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.PaymentController
import mx.lux.pos.ui.model.Coupons
import mx.lux.pos.ui.model.DevBank
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
  private JTextField txtClaveAccount1
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
  private String TAG_FORMA_PAGO_C1 = "C1"

  private String TAG_DESC_FORMA_PAGO_EF = "EFECTIVO"
  private String TAG_DESC_FORMA_PAGO_TC = "TARJETA CREDITO"
  private String TAG_DESC_FORMA_PAGO_TD = "TARJETA DEBITO"
  private String TAG_DESC_FORMA_PAGO_C1 = "REDENCION SEGUROS"

  public boolean button = false

    TotalCancellationDialog( Component parent, String orderId ) {
    order = OrderController.getOrder( orderId )
    email = CustomerController.findCustomerEmail( order.customer.id )
    payments = PaymentController.findPaymentsByOrderId( orderId ) as List<Payment>
    devBank = OrderController.findDevBanks( )
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
        title: "Cancelaci\u00f3n",
        location: parent.locationOnScreen,
        resizable: true,
        pack: true,
        modal: true,
        layout: new MigLayout( 'fill,wrap', '[]', '[fill]' )
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap", "[fill,grow]", "[]10[]" ) ) {
          def displayFont = new Font( '', Font.BOLD, 14 )
          label( text: "Datos de la factura ${order.bill}", font: displayFont )
          //label( text: " ", constraints: "span 2" )
          //label( text: "Factura:" )
          //txtBill = textField( editable: false )
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
            scrollPane( constraints: 'h 70!,hidemode 3', visible: coupons.size() > 0 ) {
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
            scrollPaneCoupons = scrollPane( constraints: 'h 60!,hidemode 3', visible: coupons.size() > 0 ) {
                tblDetCoupons = table( selectionMode: ListSelectionModel.SINGLE_SELECTION ) {
                  couponsDetModel = tableModel( list: couponsDet ) {
                        closureColumn( header: 'Cliente', read: {Coupons cmv -> cmv?.client}, maxWidth: 150 )
                        closureColumn( header: 'Articulos', read: {Coupons cmv -> cmv?.articles}, maxWidth: 150 )
                        closureColumn( header: 'Pagos', read: {Coupons cmv -> cmv?.payments}, maxWidth: 200 )
                    } as DefaultTableModel
                }
            }
          panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap', '[grow,center]', '[]' ) ) {
            label( text: "DEVOLUCION:", font: displayFont )
            label( text: devAmount, font: displayFont )
          }
          panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap 2', '[]', '[]' ), visible: StringUtils.trimToEmpty(devAmountTd).length() > 0 ) {
            panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap', '[]', '[]' ) ) {
              label( text: "DEVOLUCION:", font: displayFont )
              label( text: devAmountTd, font: displayFont )
            }
            panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap', '[]', '[]' ) ) {
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
                    lblBank.visible = true
                    lblClaveAccount.visible = true
                  }
              })
            }
          }
          panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap 3', '[fill][fill,grow]', '[]' ),
                  visible: StringUtils.trimToEmpty(devAmountTd).length() > 0, constraints: 'hidemode 3' ) {
            label( text: "Nombre:" )
            txtName = textField( text: order.customer.onlyFullName, constraints: 'span 2' )
            lblBank = label( text: "Banco:", constraints: 'hidemode 3', visible: false )
            cbBank = comboBox( items: devBank*.name, constraints: 'hidemode 3,span 2', visible: false )
            lblClaveAccount = label( text: "Cta./CLABE:", constraints: 'hidemode 3', visible: false )
            txtClaveAccount = textField( constraints: 'hidemode 3', visible: false )
            txtClaveAccount1 = textField( constraints: 'hidemode 3', visible: false )
            label( text: "Correo:" )
            txtEmail = textField( text: email, constraints: 'span 2' )
          }
          panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap', '[grow,center]', '[]' ) ) {
            lblVerifTarjeta = label( text: "    Verificar que el cliente traiga la Tarjeta de Credito.", constraints: 'hidemode 3', font: displayFont )
            lblVerifMaterial = label( text: "        Verificar que el cliente traiga el material.", constraints: 'hidemode 3', font: displayFont )
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
      if( payment.amount.doubleValue() >= couponsAmount ){
        String tipoPago = "EF"
        if( StringUtils.trimToEmpty(payment.paymentTypeId).equalsIgnoreCase(TAG_FORMA_PAGO_TC) ){
          tipoPago = 'TC'
        }
        //amount = amount+", "+String.format( '$%.2f-%s',payment.amount.subtract(couponsAmount),tipoPago)
      if( StringUtils.trimToEmpty(payment.paymentTypeId).equalsIgnoreCase(TAG_FORMA_PAGO_TC) ){
          amountNbrTc = (amountNbrTc.add(payment.amount)).subtract(couponsAmount)
        } else if( StringUtils.trimToEmpty(payment.paymentTypeId).equalsIgnoreCase(TAG_FORMA_PAGO_C1) ) {
          amountNbrC1 = (amountNbrC1.add(payment.amount)).subtract(couponsAmount)
        } else if( StringUtils.trimToEmpty(payment.paymentTypeId).equalsIgnoreCase(TAG_FORMA_PAGO_TD) ) {
          amountNbrTd = (amountNbrTd.add(payment.amount)).subtract(couponsAmount)
        } else {
          amountNbrEf = (amountNbrEf.add(payment.amount)).subtract(couponsAmount)
        }
      }
      couponsAmount = couponsAmount.doubleValue()-payment.amount.doubleValue() < 0.00 ? BigDecimal.ZERO : couponsAmount.doubleValue()-payment.amount.doubleValue()
    }
    amount = (amountNbrEf.doubleValue() > 0 ? String.format('$%.2f-%s',amountNbrEf,TAG_DESC_FORMA_PAGO_EF) : "")+" "+
            (amountNbrTc.doubleValue() > 0 ? String.format('$%.2f-%s',amountNbrTc,TAG_DESC_FORMA_PAGO_TC) : "")+" "+
            //(amountNbrTd.doubleValue() > 0 ? String.format('$%.2f-%s',amountNbrTd,TAG_DESC_FORMA_PAGO_TD) : "")+" "+
            (amountNbrC1.doubleValue() > 0 ? String.format('$%.2f-%s',amountNbrC1,TAG_DESC_FORMA_PAGO_C1) : "")
    devAmountTd = amountNbrTd.doubleValue() > 0 ? String.format('$%.2f-%s',amountNbrTd,TAG_DESC_FORMA_PAGO_TD) : ""

    if( StringUtils.trimToEmpty(amount).length() <= 0 || StringUtils.trimToEmpty(amount).equalsIgnoreCase(",,,") ){
      amount = '$0.00'
    }

    return amount
  }


  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    if( validDevTd() ){
      DevBank selection = cbBank.selectedObjects as DevBank
      Integer selectedBank = selection.id
      String dataDev = "${StringUtils.trimToEmpty(txtName.text)},${StringUtils.trimToEmpty(selectedBank.text)}"
      AuthorizationCanDialog authDialog = new AuthorizationCanDialog( this, "Cancelaci\u00f3n requiere autorizaci\u00f3n", order )
      authDialog.show()
      dispose()
    }
  }

  protected void onButtonPrint( ) {
    OrderController.printResumeCancCoupon( StringUtils.trimToEmpty(order.id), devAmount )
  }

  Boolean validDevTd( ){
    Boolean valid = true
    String pattern= '[A-Za-z0-9]+';
    if( StringUtils.trimToEmpty(txtName.text).length() <= 0 ||
          !StringUtils.trimToEmpty(txtName.text).replace(" ","").matches(pattern) ){
      valid = false
      txtName.foreground = UI_Standards.WARNING_FOREGROUND
    }


    if( StringUtils.trimToEmpty(txtEmail.text).length() <= 0 ){
      valid = false
      txtEmail.foreground = UI_Standards.WARNING_FOREGROUND
    } else {
      String[] emailData = StringUtils.trimToEmpty(txtEmail.text).split("@")
      if( emailData.length != 2 ){
        /*if( !StringUtils.trimToEmpty(emailData[0]).matches(pattern) ||
                !StringUtils.trimToEmpty(emailData[1]).matches(pattern) ){*/
          valid = false
          txtEmail.foreground = UI_Standards.WARNING_FOREGROUND
        //}
      } /*else {
        valid = false
      }*/
    }
    if( txtClaveAccount.visible ){
      if( StringUtils.trimToEmpty(txtClaveAccount.text).length() <= 0 ||
            (StringUtils.trimToEmpty(txtClaveAccount1.text).length() < 18 ||
              StringUtils.trimToEmpty(txtClaveAccount1.text).length() < 18)){
        valid = false
        txtClaveAccount.foreground = UI_Standards.WARNING_FOREGROUND
        txtClaveAccount1.foreground = UI_Standards.WARNING_FOREGROUND
        txtClaveAccount.text = "VERIFIQUE"
        txtClaveAccount1.text = "LOS DATOS"
      } else {
        if( !StringUtils.trimToEmpty(txtClaveAccount.text).isNumber() ||
                !StringUtils.trimToEmpty(txtClaveAccount1.text).isNumber() ){
          valid = false
          txtClaveAccount1.foreground = UI_Standards.WARNING_FOREGROUND
          txtClaveAccount.text = "VERIFIQUE"
          txtClaveAccount1.text = "LOS DATOS"
        }
      }
    }
    return valid
  }


}
