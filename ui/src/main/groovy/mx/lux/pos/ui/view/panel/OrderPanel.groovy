package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.model.*
import mx.lux.pos.repository.NotaVentaJava
import mx.lux.pos.repository.RecetaJava
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.MainWindow
import mx.lux.pos.ui.controller.*
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.component.DiscountContextMenu
import mx.lux.pos.ui.view.dialog.*
import mx.lux.pos.ui.view.driver.PromotionDriver
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.runtime.InvokerInvocationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.*
import java.awt.event.*
import java.text.NumberFormat
import java.util.List

class OrderPanel extends JPanel
implements IPromotionDrivenPanel, FocusListener, CustomerListener {


    static final String MSG_INPUT_QUOTE_ID = 'Indique el número de cotización'
    static final String TXT_QUOTE_TITLE = 'Seleccionar cotización'
    static final String TXT_INVALID_PAYMENT_TITLE = 'Los pagos no cumplen con la política comercial.'
    private static final String TXT_BTN_CLOSE = 'Vendedor'
    private static final String TXT_BTN_QUOTE = 'Cotizar'
    private static final String TXT_BTN_PRINT = 'Imprimir'
    private static final String TXT_BTN_NEW_ORDER = 'Otra venta'
    private static final String TXT_BTN_CANCEL_ORDER = '<html><p align="center">Anular venta</p></html>'
    private static final String TXT_BTN_CONTINUE = 'Continuar'
    private static final String TXT_NO_ORDER_PRESENT = 'Se debe agregar al menos un artículo.'
    private static final String TXT_PAYMENTS_PRESENT = 'Elimine los pagos registrados y reintente.'
    private static final String MSJ_VENTA_NEGATIVA = 'No se pueden agregar artículos sin existencia.'
    private static final String MSJ_PAQUETE_INVALIDO = 'No se pueden agregar el paquete, ya existe uno.'
    private static final String MSJ_LENTE_INVALIDO = 'No se pueden agregar el lente, ya existe uno.'
    private static final String MSJ_SEGURO_APLICADO = 'No se pueden agregar el seguro, existe uno aplicado.'
    private static final String TXT_VENTA_NEGATIVA_TITULO = 'Error al agregar artículo'
    private static final String TXT_PAQUETE_INVALIDO = 'Error al agregar paquete'
    private static final String TXT_LENTE_INVALIDO = 'Error al agregar lente'
    private static final String TXT_REQUEST_NEW_ORDER = 'Solicita nueva orden a mismo cliente.'
    private static final String TXT_REQUEST_CONTINUE = 'Solicita nueva orden a otro cliente.'
    private static final String TXT_REQUEST_QUOTE = 'Cotizar orden actual.'
    private static final String MSJ_QUITAR_PAGOS = 'Elimine los pagos antes de cerrar la sesion.'
    private static final String TXT_QUITAR_PAGOS = 'Error al cerrar sesion.'
    private static final String MSJ_CAMBIAR_VENDEDOR = 'Esta seguro que desea salir de esta sesion.'
    private static final String TXT_CAMBIAR_VENDEDOR = 'Cerrar Sesion'
    private static final String TAG_ARTICULO_B = 'B'
    private static final String TAG_ARTICULO_P = 'P'
    private static final String TAG_PAQUETE = 'Q'
    private static final String TAG_FORMA_PAGO_C1 = 'C1'
    private static final String TAG_REUSO = 'R'
    private static final String TAG_COTIZACION = 'Cotización'
    private static final String TAG_ARTICULO_NO_VIGENTE = 'C'
    private static final String TAG_PAYMENT_TYPE_TRANSF = 'TR'
    private static final String TAG_GENERICO_SEGUROS = 'J'
    private static final String TAG_GENERICO_ARMAZON = 'A'
    private static final String TAG_GENERICO_LENTE = 'B'
    private static final String TAG_GENERICO_LENTE_CONTACTO = 'H'
    private static final String TAG_SEGUROS_ARMAZON = 'SS'
    private static final String TAG_SEGUROS_OFTALMICO = 'SEG'
    private static final String TAG_SUBTIPO_NINO = 'N'
    private static final String TAG_RECETA_LC = 'LC'

    private Logger logger = LoggerFactory.getLogger(this.getClass())
    private SwingBuilder sb
    private Order order
    private Customer customer
    private JComboBox operationType
    private JButton customerName
    private JButton closeButton
    private JButton quoteButton
    private JButton printButton
    private JButton continueButton
    private JButton newOrderButton
    private JButton cancelOrderButton
    private JTextArea comments
    private JTextField itemSearch
    private List<IPromotionAvailable> promotionList
    private List<IPromotionAvailable> promotionListTmp
    private List<OperationType> lstCustomers = OperationType.values()
    private Collection<OperationType> customerTypes = new ArrayList<OperationType>()
    private List<Warranty> lstWarranty = new ArrayList<>()
    private DefaultTableModel itemsModel
    private DefaultTableModel paymentsModel
    private DefaultTableModel promotionModel
    private JLabel folio
    private JLabel bill
    private JLabel date
    private JLabel total
    private JLabel paid
    private JLabel due
    private JLabel change

    private Integer flag = 0

    private Boolean isPaying = false

    private DiscountContextMenu discountMenu
    private OperationType currentOperationType
    private Boolean uiEnabled
    private RecetaJava rec
    private Dioptra dioptra
    private Dioptra antDioptra
    private static boolean ticketRx
    private String armazonString = null
    private Boolean activeDialogProccesCustomer = true
    private Boolean activeDialogBusquedaCliente = true
    private Boolean advanceOnlyInventariable
    private Boolean canceledWarranty
    private String sComments = ''
    private String ip
    private HelpItemSearchDialog helpItemSearchDialog

    private String MSJ_ERROR_WARRANTY = ""
    private String TXT_ERROR_WARRANTY = ""

    public Integer numQuote = 0

    OrderPanel() {
        sb = new SwingBuilder()
        order = new Order()
        dioptra = new Dioptra()
        advanceOnlyInventariable = false
        String clientesActivos = OrderController.obtieneTiposClientesActivos()
        for(OperationType customer : lstCustomers){
            if(clientesActivos.contains(customer.value)){
               customerTypes.add(customer)
            }
        }
        ip = ipCurrentMachine()
        customer = CustomerController.findDefaultCustomer()
        promotionList = new ArrayList<PromotionAvailable>()
        promotionListTmp = new ArrayList<PromotionAvailable>()
        this.promotionDriver.init(this)
        ticketRx = false
        buildUI()
        doBindings()
        itemsModel.addTableModelListener(this.promotionDriver)
        uiEnabled = true
        OperationType
    }

    PromotionDriver getPromotionDriver() {
        return PromotionDriver.instance
    }

    private void buildUI() {
        sb.panel(this, layout: new MigLayout('insets 5,fill,wrap 2', '[fill][fill,grow]', '[fill]')) {
            panel(layout: new MigLayout('insets 0,fill', '[fill,260][fill,180][fill,300!]', '[fill]'), constraints: 'span') {
                panel(border: loweredEtchedBorder(), layout: new MigLayout('wrap 2', '[][fill,220!]', '[top]')) {
                    label('Cliente')
                    customerName = button(enabled: false, actionPerformed: doCustomerSearch)

                    label('Tipo')
                    operationType = comboBox(items: customerTypes, itemStateChanged: operationTypeChanged)
                }

                panel(border: loweredEtchedBorder(), layout: new MigLayout('wrap 2', '[][grow,right]', '[top]')) {
                    def displayFont = new Font('', Font.BOLD, 19)
                    label()
                    date = label(font: displayFont)
                    label('Folio')
                    folio = label()
                    label('Factura')
                    bill = label()
                }

                panel(border: loweredEtchedBorder(), layout: new MigLayout('wrap 2', '[][grow,right]', '[top]')) {
                    def displayFont = new Font('', Font.BOLD, 22)
                    label('Venta')
                    total = label(font: displayFont)
                    label('Pagado')
                    paid = label(font: displayFont)
                    label('Saldo')
                    due = label(font: displayFont)
                }
            }

            button( text: "?", actionPerformed: doHelp )
            itemSearch = textField(font: new Font('', Font.BOLD, 16), document: new UpperCaseDocument(), actionPerformed: { doItemSearch( false ) })
            itemSearch.addFocusListener(this)

            scrollPane(border: titledBorder(title: 'Art\u00edculos'), constraints: 'span') {
                table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClick) {
                    itemsModel = tableModel(list: order.items) {
                        closureColumn(
                                header: 'Art\u00edculo',
                                read: { OrderItem tmp -> "${tmp?.item?.name} ${tmp?.item?.color ?: ''}" },
                                minWidth: 80,
                                maxWidth: 100
                        )
                        closureColumn(
                                header: 'Descripci\u00f3n',
                                read: { OrderItem tmp -> tmp?.description }
                        )
                        closureColumn(
                                header: 'Surte',
                                read: {OrderItem tmp -> tmp?.item?.surte},
                                maxWidth: 50,
                                minWidth: 30
                        )
                        closureColumn(
                                header: 'Cantidad',
                                read: { OrderItem tmp -> tmp?.quantity },
                                minWidth: 70,
                                maxWidth: 70
                        )
                        closureColumn(
                                header: 'Precio',
                                read: { OrderItem tmp -> tmp?.item?.price },
                                minWidth: 80,
                                maxWidth: 100,
                                cellRenderer: new MoneyCellRenderer()
                        )
                        closureColumn(
                                header: 'Total',
                                read: { OrderItem tmp -> tmp?.item?.price * tmp?.quantity },
                                minWidth: 80,
                                maxWidth: 100,
                                cellRenderer: new MoneyCellRenderer()
                        )
                    } as DefaultTableModel
                }
            }

            panel(layout: new MigLayout('insets 0,fill', '[fill][fill,240!]', '[fill]'), constraints: 'span') {
                scrollPane(border: titledBorder(title: "Promociones"),
                        mouseClicked: { MouseEvent ev -> onMouseClickedAtPromotions(ev) },
                        mouseReleased: { MouseEvent ev -> onMouseClickedAtPromotions(ev) }
                ) {
                    table(selectionMode: ListSelectionModel.SINGLE_SELECTION,
                            mouseClicked: { MouseEvent ev -> onMouseClickedAtPromotions(ev) },
                            mouseReleased: { MouseEvent ev -> onMouseClickedAtPromotions(ev) }
                    ) {
                        promotionModel = tableModel(list: promotionList) {
                            closureColumn(header: "", type: Boolean, maxWidth: 25,
                                    read: { row -> row.applied },
                                    write: { row, newValue ->
                                        onTogglePromotion(row, newValue)
                                    }
                            )
                            propertyColumn(header: "Descripci\u00f3n", propertyName: "description", editable: false)
                            propertyColumn(header: "Art\u00edculo", propertyName: "partNbrList", maxWidth: 100, editable: false)
                            closureColumn(header: "Precio Base",
                                    read: { IPromotionAvailable promotion -> promotion.baseAmount },
                                    maxWidth: 80,
                                    cellRenderer: new MoneyCellRenderer()
                            )
                            closureColumn(header: "Descto",
                                    read: { IPromotionAvailable promotion -> promotion.discountAmount },
                                    maxWidth: 80,
                                    cellRenderer: new MoneyCellRenderer()
                            )
                            closureColumn(header: "Promoci\u00f3n",
                                    read: { IPromotionAvailable promotion -> promotion.promotionAmount },
                                    maxWidth: 80,
                                    cellRenderer: new MoneyCellRenderer()
                            )
                        } as DefaultTableModel
                    }
                }

                scrollPane(border: titledBorder(title: 'Pagos'), mouseClicked: doNewPaymentClick) {
                    table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowPaymentClick) {
                        paymentsModel = tableModel(list: order.payments) {
                            closureColumn(header: 'Descripci\u00f3n', read: { Payment tmp -> tmp?.description })
                            closureColumn(header: 'Monto', read: { Payment tmp -> tmp?.amount }, maxWidth: 100, cellRenderer: new MoneyCellRenderer())
                        } as DefaultTableModel
                    }
                }
            }

            scrollPane(border: titledBorder(title: 'Observaciones'), constraints: 'span') {
                comments = textArea(document: new UpperCaseDocument(), lineWrap: true)
            }

            panel(minimumSize: [750, 45], border: BorderFactory.createEmptyBorder(0, 0, 0, 0), constraints: 'span') {
                borderLayout()
                panel(constraints: BorderLayout.LINE_START, border: BorderFactory.createEmptyBorder(0, 0, 0, 0)) {
                    closeButton = button(TXT_BTN_CLOSE,
                            preferredSize: UI_Standards.BUTTON_SIZE,
                            actionPerformed: doClose
                    )
                }
                change = label(foreground: UI_Standards.WARNING_FOREGROUND, constraints: BorderLayout.CENTER)
                panel(constraints: BorderLayout.LINE_END, border: BorderFactory.createEmptyBorder(0, 0, 0, 0)) {
                    cancelOrderButton = button(TXT_BTN_CANCEL_ORDER,
                            preferredSize: UI_Standards.BUTTON_SIZE,
                            actionPerformed: { fireRequestCancelOrder( ) },
                            constraints: 'hidemode 3',
                            visible: false
                    )
                    newOrderButton = button(TXT_BTN_NEW_ORDER,
                            preferredSize: UI_Standards.BUTTON_SIZE,
                            actionPerformed: { fireRequestNewOrder(itemsModel) }
                    )
                    quoteButton = button(TXT_BTN_QUOTE,
                            preferredSize: UI_Standards.BUTTON_SIZE,
                            actionPerformed: { fireRequestQuote() }
                    )
                    continueButton = button(TXT_BTN_CONTINUE,
                            preferredSize: UI_Standards.BUTTON_SIZE,
                            actionPerformed: { fireRequestContinue(itemsModel) }
                    )
                    printButton = button(TXT_BTN_PRINT,
                            preferredSize: UI_Standards.BUTTON_SIZE,
                            actionPerformed: doPrint
                    )
                }
            }
        }
    }

    private void doBindings() {
      sb.build {
        bean(customerName, text: bind { customer?.fullName })
        bean(folio, text: bind { order.id })
        bean(bill, text: bind { order.bill })
        bean(date, text: bind(source: order, sourceProperty: 'date', converter: dateConverter), alignmentX: CENTER_ALIGNMENT)
        bean(total, text: bind(source: order, sourceProperty: 'total', converter: currencyConverter))
        bean(paid, text: bind(source: order, sourceProperty: 'paid', converter: currencyConverter))
        bean(due, text: bind(source: order, sourceProperty: 'dueString'))
        bean(itemsModel.rowsModel, value: bind(source: order, sourceProperty: 'items', mutual: true))
        bean(paymentsModel.rowsModel, value: bind(source: order, sourceProperty: 'payments', mutual: true))
        bean(comments, text: bind(source: order, sourceProperty: 'comments', mutual: true))
        bean(order, customer: bind { customer })
      }
      itemsModel.fireTableDataChanged()
      paymentsModel.fireTableDataChanged()

      if (order?.id != null) {
        change.text = OrderController.requestEmployee(order?.id)
      } else {
        change.text = ''
      }
      if( isPaying ){
        for(IPromotionAvailable prom : promotionList){
          if( prom instanceof PromotionAvailable ){
            if( OrderController.esPromocionValida( order.id, prom.promotion.idPromotion ) ){
              println "Promocion activar: "+prom.promotion.dump()
              prom.applied = true
            }
          }
        }
        isPaying = false
      }
      currentOperationType = (OperationType) operationType.getSelectedItem()
      if( currentOperationType.equals(OperationType.PAYING) ){
        itemSearch.enabled = false
      } else {
        itemSearch.enabled = true
      }
      this.printButton.setVisible(!this.isPaymentListEmpty() ||
              this.promotionDriver.model?.orderDiscount?.discountPercent == 1.0 ||
              ( order.due.compareTo(BigDecimal.ZERO) <= 0 && this.promotionDriver.model?.isAnyApplied()) )
        this.continueButton.setVisible( !this.printButton.visible )
        if( order.items.size() > 0 ){
          cancelOrderButton.visible = true
        } else {
          cancelOrderButton.visible = false
        }
    }

    void updateOrder(String pOrderId) {
      String comments = ''
      if( order.comments != null && order.comments != '' ){
        comments = order.comments
      }
      Order tmp = OrderController.getOrder(pOrderId)
      if (tmp?.id) {
        if( comments.length() > 0 ){
          tmp?.comments = comments
        }
        order = tmp
        doBindings()
      }
    }

    private def dateConverter = { Date val ->
        val?.format('dd-MM-yyyy')
    }

    private def currencyConverter = {
        NumberFormat.getCurrencyInstance(Locale.US).format(it ?: 0)
    }

    private def doCustomerSearch = { ActionEvent ev ->
        JButton source = ev.source as JButton
        source.enabled = false
        if (order.customer.id == null) {
            sb.doLater {
                if (this.customer == null) {
                    this.operationType.setSelectedItem(OperationType.DEFAULT)
                }
            }
        } else {
          if ( CustomerType.FOREIGN.equals( customer.type ) ) {
              ForeignCustomerDialog dialog = new ForeignCustomerDialog( this, customer, true )
              dialog.show()
              this.customer = dialog.customer
          } else {
              NewCustomerAndRxDialog dialog = new NewCustomerAndRxDialog( this, customer, true )
              dialog.show()
              this.customer = dialog.customer
          }
          sb.doLater {
              this.doBindings()
          }
        }

        doBindings()
        source.enabled = true
    }

    private def operationTypeChanged = { ItemEvent ev ->
        if (ev.stateChange == ItemEvent.SELECTED && this.uiEnabled) {
            switch (ev.item) {
                case OperationType.DEFAULT:
                    //cleanOrder()
                    customer = CustomerController.findDefaultCustomer()
                    customerName.enabled = false
                    CustomerController.requestNewOrder( this )
                    break
                case OperationType.WALKIN:
                    customer = new Customer(type: CustomerType.DOMESTIC)
                    ForeignCustomerDialog dialog = new ForeignCustomerDialog(ev.source as Component, customer, false)
                    dialog.show()
                    if (!dialog.canceled) {
                        customer = dialog.customer
                    }
                    break
                case OperationType.FOREIGN:
                    customer = new Customer(type: CustomerType.FOREIGN)
                    ForeignCustomerDialog dialog = new ForeignCustomerDialog(ev.source as Component, customer, false)
                    dialog.show()
                    if (!dialog.canceled) {
                        customer = dialog.customer
                    }
                    break
                case OperationType.QUOTE:
                    String number = OrderController.requestOrderFromQuote(this)
                    numQuote = OrderController.getNumberQuote()
                    sb.doLater {
                        if (StringUtils.trimToNull(number.toString()) != null) {
                            Customer tmp = OrderController.getCustomerFromOrder(number)
                            if (tmp != null) {
                                customer = tmp
                            }
                            updateOrder(number.toString())
                        } else {
                            operationType.setSelectedItem(currentOperationType)
                        }
                    }
                    break
                case OperationType.AGREEMENT:
                    operationType.setSelectedItem(OperationType.DEFAULT)
                    break
                case OperationType.NEW:
                    sb.doLater {
                        if ( activeDialogBusquedaCliente ) {
                            CustomerController.requestBusquedaCliente(this)
                        }
//                        CustomerController.requestNewCustomer(this)
                    }
                    break
                case OperationType.PENDING:
                    sb.doLater {
                        if(activeDialogProccesCustomer){
                          CustomerController.requestPendingCustomer(this)
                        }
                        activeDialogProccesCustomer = true
                    }
                    break
                case OperationType.PAYING:
                    sb.doLater {
                      Boolean valid = true
                      String term = StringUtils.trimToEmpty(Registry.terminalCaja)
                      println "Ip Valida: "+term
                      if( term.length() > 0 ){
                        if( StringUtils.trimToEmpty(term).length() > 0 && (!term.contains(ip) || ip.length() <= 0) ){
                          valid = false
                        }
                      }
                      if( valid ){
                        CustomerController.requestPayingCustomer(this, OperationType.PAYING)
                        isPaying = true
                      } else {
                          sb.optionPane(
                                  message: 'Opcion valida solo en caja',
                                  messageType: JOptionPane.ERROR_MESSAGE
                          ).createDialog(this, 'Maquina Incorrecta')
                                  .show()
                        operationTypeSelected = OperationType.DEFAULT
                      }
                    }
                    break
                case OperationType.EDIT_PAYING:
                  sb.doLater {
                    Boolean valid = true

                    String term = StringUtils.trimToEmpty(Registry.terminalCaja)
                    println "Ip Valida: "+term
                    if( term.length() > 0 ){
                      if( StringUtils.trimToEmpty(term).length() > 0 && (!term.contains(ip) || ip.length() <= 0) ){
                        valid = false
                      }
                    }
                    if( valid ){
                      CustomerController.requestPayingCustomer(this, OperationType.EDIT_PAYING)
                      isPaying = true
                    } else {
                      sb.optionPane(
                                  message: 'Opcion valida solo en caja',
                                  messageType: JOptionPane.ERROR_MESSAGE
                      ).createDialog(this, 'Maquina Incorrecta')
                                  .show()
                      operationTypeSelected = OperationType.DEFAULT
                    }
                    //isPaying = true
                  }
                  break
                /*case OperationType.MULTYPAYMENT:
                    sb.doLater {
                        CustomerController.requestMultypayment(this, this)
                    }
                    break*/
            }
            if(!operationType.selectedItem.equals(OperationType.DOMESTIC)){
              operationType.removeItem( OperationType.DOMESTIC )
            }
            this.setCustomerInOrder()
            doBindings()
        } else {
            customerName.enabled = true
        }
    }


    private def doItemSearch( Boolean holdPromo ) {
      println "holdPromo: "+holdPromo
        Receta rec = new Receta()
        String input = itemSearch.text
        String article = input
        Boolean newOrder = false
        if (order?.id != null) {
            newOrder = StringUtils.isBlank(order.id)
        }
      if( OrderController.dayIsOpen() ){
        if (StringUtils.isNotBlank(input)) {
            sb.doOutside {
                if( input.contains(/$/) ){
                  String[] inputTmp = input.split(/\$/)
                  if( input.trim().contains(/$$/) ) {
                      article = inputTmp[0]
                  } else {
                      article = inputTmp[0] + ',' + inputTmp[1].substring(0,3)
                  }
                } else {
                  article = input.trim()
                }
                List<Item> results = ItemController.findItemsByQuery(article)
                if (results?.any()) {
                    Item item = new Item()
                    if (results.size() == 1) {
                        item = results.first()
                        Articulo art = ItemController.findArticle( item.id )
                        if( !art.sArticulo.equalsIgnoreCase(TAG_ARTICULO_NO_VIGENTE) ){
                            if( OrderController.validArticleGenericNoDelivered(item.id) ||
                                    StringUtils.trimToEmpty(art.idGenerico).equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO) ){
                                if( customer.id != CustomerController.findDefaultCustomer().id ){
                                  if( !appliedEnsure( art ) ){
                                    validarVentaNegativa(item, customer, holdPromo)
                                  } else {
                                    optionPane(message: MSJ_SEGURO_APLICADO, optionType: JOptionPane.DEFAULT_OPTION)
                                              .createDialog(new JTextField(), TXT_VENTA_NEGATIVA_TITULO)
                                              .show()
                                  }
                                } else {
                                    optionPane(message: "Cliente invalido, dar de alta datos", optionType: JOptionPane.DEFAULT_OPTION)
                                            .createDialog(new JTextField(), "Articulo Invalido")
                                            .show()
                                }
                            } else {
                              if( !appliedEnsure( art ) ){
                                validarVentaNegativa(item, customer, holdPromo)
                              } else {
                                optionPane(message: MSJ_SEGURO_APLICADO, optionType: JOptionPane.DEFAULT_OPTION)
                                    .createDialog(new JTextField(), TXT_VENTA_NEGATIVA_TITULO)
                                    .show()
                              }
                            }
                        } else {
                            optionPane(message: "Articulo no vigente", optionType: JOptionPane.DEFAULT_OPTION)
                                    .createDialog(new JTextField(), "Articulo Invalido")
                                    .show()
                        }
                    } else {
                        SuggestedItemsDialog dialog = new SuggestedItemsDialog(itemSearch, input, results)
                        dialog.show()
                        item = dialog.item
                        if (item?.id) {
                          Articulo art = ItemController.findArticle( item.id )
                          if( !art.sArticulo.equalsIgnoreCase(TAG_ARTICULO_NO_VIGENTE) ){
                              if( OrderController.validArticleGenericNoDelivered(item.id) ||
                                      StringUtils.trimToEmpty(art.idGenerico).equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO)){
                                  if(customer.id != CustomerController.findDefaultCustomer().id){
                                    if( !appliedEnsure( art ) ){
                                      validarVentaNegativa(item, customer, holdPromo)
                                    } else {
                                      optionPane(message: MSJ_SEGURO_APLICADO, optionType: JOptionPane.DEFAULT_OPTION)
                                          .createDialog(new JTextField(), TXT_VENTA_NEGATIVA_TITULO)
                                          .show()
                                    }
                                  } else {
                                      optionPane(message: "Cliente invalido, dar de alta datos", optionType: JOptionPane.DEFAULT_OPTION)
                                              .createDialog(new JTextField(), "Articulo Invalido")
                                              .show()
                                  }
                              } else {
                                if( !appliedEnsure( art ) ){
                                  validarVentaNegativa(item, customer, holdPromo)
                                } else {
                                  optionPane(message: MSJ_SEGURO_APLICADO, optionType: JOptionPane.DEFAULT_OPTION)
                                      .createDialog(new JTextField(), TXT_VENTA_NEGATIVA_TITULO)
                                      .show()
                                }
                              }
                          }else {
                              optionPane(message: "Articulo no vigente", optionType: JOptionPane.DEFAULT_OPTION)
                                      .createDialog(new JTextField(), "Articulo Invalido")
                                      .show()
                          }
                        }
                    }
                } else if( StringUtils.trimToEmpty(article).equalsIgnoreCase(TAG_RECETA_LC) ){
                  if( customer.id != CustomerController.findDefaultCustomer().id ){
                    if( order?.id == null ){
                      order = OrderController.openOrder(StringUtils.trimToEmpty(customer.id.toString()), order.employee)
                      updateOrder( StringUtils.trimToEmpty(order.id) )
                    }
                    Branch branch = Session.get(SessionItem.BRANCH) as Branch
                    EditRxDialog editRx = new EditRxDialog(this, new Rx(), customer?.id, branch?.id, 'Nueva Receta', "MONOFOCAL", false, false)
                    editRx.show()
                    OrderController.saveRxOrder(order?.id, this.rec.idReceta)
                  } else {
                      optionPane(message: "Cliente invalido, dar de alta datos", optionType: JOptionPane.DEFAULT_OPTION)
                              .createDialog(new JTextField(), "Articulo Invalido")
                              .show()
                  }
                } else {
                    optionPane(message: "No se encontraron resultados para: ${article}", optionType: JOptionPane.DEFAULT_OPTION)
                            .createDialog(new JTextField(), "B\u00fasqueda: ${article}")
                            .show()
                }
                if (newOrder && (StringUtils.trimToNull(order?.id) != null) && (StringUtils.trimToNull(customer?.id) != null)) {
                    this.setCustomerInOrder()
                }

            }
            sb.doLater {
                itemSearch.text = null
            }

        } else {
          sb.optionPane(message: 'Es necesario ingresar una b\u00fasqeda v\u00e1lida', optionType: JOptionPane.DEFAULT_OPTION)
                .createDialog(new JTextField(), "B\u00fasqueda inv\u00e1lida")
                .show()
        }
      } else {
          sb.optionPane(message: 'No se pueden realizar la venta. El dia esta cerrado', optionType: JOptionPane.DEFAULT_OPTION)
                  .createDialog(new JTextField(), "Dia cerrado")
                  .show()
      }
    }

    private def doShowItemClick = { MouseEvent ev ->
        OperationType operationType1 = operationType.selectedItem as OperationType
        if (SwingUtilities.isLeftMouseButton(ev) && !operationType1.equals(OperationType.PAYING )) {
            if (ev.clickCount == 2) {
                new ItemDialog(ev.component, order, ev.source.selectedElement, this).show()
                updateOrder(order?.id)
                List<IPromotionAvailable> promotionsListTmp = new ArrayList<>()
                promotionsListTmp.addAll(promotionList)
                for(IPromotionAvailable promo : promotionsListTmp){
                    this.promotionDriver.requestCancelPromotion(promo)
                    OrderController.deleteCuponMv( order.id )
                }
            }
        }
    }

    private def doNewPaymentClick = { MouseEvent ev ->
        if (SwingUtilities.isLeftMouseButton(ev)) {
          OperationType operationType1 = operationType.selectedItem as OperationType
          Boolean valid = true
          String term = StringUtils.trimToEmpty(Registry.terminalCaja)
          if( term.length() > 0 ){
            if( operationType1.equals(OperationType.DEFAULT) ){
              if( StringUtils.trimToEmpty(term).length() > 0 && (!term.contains(ip) || ip.length() <= 0) ){
                valid = false
              }
            }
          }
           if (ev.clickCount == 1 && valid && (!operationType1.equals(OperationType.PENDING) &&
                   !operationType1.equals(OperationType.EDIT_PAYING) && !operationType1.equals(OperationType.QUOTE))) {
                if (order.due) {
                  Boolean hasDiscount = false
                  for(int i=0;i<promotionList.size();i++){
                    if(promotionList.get(i) instanceof PromotionAvailable){
                      if( promotionList.get(i).applied ){
                        hasDiscount = true
                      }
                    } else if(promotionList.get(i) instanceof PromotionDiscount){
                      println "Tiene descuento"
                      hasDiscount = true
                    }
                  }
                    CuponMvView cuponMvView = OrderController.cuponValid( order.customer.id )
                    new PaymentDialog(ev.component, order, null, cuponMvView, this, hasDiscount).show()
                    updateOrder(order?.id)
                    //validTransferCuponMv()
                    doBindings()
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

    private def doShowPaymentClick = { MouseEvent ev ->
        if (SwingUtilities.isLeftMouseButton(ev)) {
          OperationType operationType1 = operationType.selectedItem as OperationType
          Boolean valid = true
          String term = StringUtils.trimToEmpty(Registry.terminalCaja)
          if( term.length() > 0 ){
                if( operationType1.equals(OperationType.DEFAULT) ){
                    if( StringUtils.trimToEmpty(term).length() > 0 && (!term.contains(ip) || ip.length() <= 0) ){
                        valid = false
                    }
                }
          }
            if (ev.clickCount == 2 && valid && (!operationType1.equals(OperationType.PENDING) &&
                    !operationType1.equals(OperationType.EDIT_PAYING) && !operationType1.equals(OperationType.QUOTE))) {
                new PaymentDialog(ev.component, order, ev.source.selectedElement, new CuponMvView(), this, false).show()
                updateOrder(order?.id)
            }
        }
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
                Boolean reuse = CancellationController.printCancellationsFromOrder(newOrderId)
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

    private RecetaJava validarGenericoB(Item item) {
      rec = null
      try {
        //Receta Nueva
        String artString = item.name
        if (artString.equals('SV') || artString.equals('P') || artString.equals('B')) {
          Branch branch = Session.get(SessionItem.BRANCH) as Branch
          EditRxDialog editRx = new EditRxDialog(this, new Rx(), customer?.id, branch?.id, 'Nueva Receta', item.description, false, false)
          editRx.show()

          this.disableUI()
          this.setCustomer(customer)
          this.setOrder(order)
          this.enableUI()
        } else {
          rec = null
          this.disableUI()
          this.setCustomer(customer)
          this.setOrder(order)
          this.enableUI()
        }
      } catch (Exception ex) {
        println ex
        rec = null
      }
      return rec
    }

    private SurteSwitch surteSu(Item item,SurteSwitch surteSwitch) {
        if (surteSwitch?.surteSucursal == false) {
            if (item?.type?.trim().equals('A') && item?.stock > 0) {
                surteSwitch?.surteSucursal = true
            } else {
                SalesWithNoInventory onSalesWithNoInventory = OrderController.requestConfigSalesWithNoInventory()
                if (SalesWithNoInventory.ALLOWED.equals(onSalesWithNoInventory)) {
                  surteSwitch?.surteSucursal = true
                } else if (SalesWithNoInventory.REQUIRE_AUTHORIZATION.equals(onSalesWithNoInventory)){
                  AuthorizationDialog authDialog = new AuthorizationDialog(this, "Esta operacion requiere autorizaci\u00f3n")
                  authDialog.show()
                  logger.debug('Autorizado: ' + authDialog.authorized)
                  if (authDialog.authorized) {
                    surteSwitch?.surteSucursal = true
                  } else {
                    OrderController.notifyAlert('Se requiere autorizacion para esta operacion', 'Se requiere autorizacion para esta operacion')
                  }
                } else if (SalesWithNoInventory.RESTRICTED.equals(onSalesWithNoInventory)){
                  surteSwitch?.surteSucursal = true
                }
            }
        }
        return surteSwitch
    }

    private void validarVentaNegativa(Item item, Customer customer, Boolean holdPromo) {
        User u = Session.get(SessionItem.USER) as User
        order.setEmployee(u.username)
        Branch branch = Session.get(SessionItem.BRANCH) as Branch
        Boolean isOnePackage = OrderController.validOnlyOnePackage( order.items, item.id )
        Boolean isOneLens = OrderController.validOnlyOneLens( order.items, item.id )
        SurteSwitch surteSwitch = OrderController.surteCallWS(branch, item, 'S', order)
        surteSwitch = surteSu(item, surteSwitch)
        Boolean esInventariable = ItemController.esInventariable( item.id )
        if( isOnePackage ){
          if( isOneLens ){
              if (surteSwitch?.agregaArticulo == true && surteSwitch?.surteSucursal == true) {
                  String surte = surteSwitch?.surte
                  if (item.stock > 0) {
                      order = OrderController.addItemToOrder(order, item, surte)
                      updateOrder( order.id )
                      validaLC(item, false)
                      controlItem(item, false)
                      List<IPromotionAvailable> promotionsListTmp = new ArrayList<>()
                      promotionsListTmp.addAll(promotionList)
                      if( !holdPromo ){
                        for(IPromotionAvailable promo : promotionsListTmp){
                          this.promotionDriver.requestCancelPromotion(promo)
                          OrderController.deleteCuponMv( order.id )
                        }
                      }
                      if (customer != null) {
                          order.customer = customer
                      }
                  } else {
                      if( esInventariable ){
                          SalesWithNoInventory onSalesWithNoInventory = OrderController.requestConfigSalesWithNoInventory()
                          order.customer = customer
                          if (SalesWithNoInventory.ALLOWED.equals(onSalesWithNoInventory)) {
                              order = OrderController.addItemToOrder(order, item, surte)
                              updateOrder( order.id )
                              validaLC(item, false)
                              controlItem(item, false)
                              List<IPromotionAvailable> promotionsListTmp = new ArrayList<>()
                              promotionsListTmp.addAll(promotionList)
                              if( !holdPromo ){
                                for(IPromotionAvailable promo : promotionsListTmp){
                                  this.promotionDriver.requestCancelPromotion(promo)
                                  OrderController.deleteCuponMv( order.id )
                                }
                              }
                          } else if (SalesWithNoInventory.REQUIRE_AUTHORIZATION.equals(onSalesWithNoInventory)) {
                              boolean authorized
                              if (AccessController.authorizerInSession) {
                                  authorized = true
                              } else {
                                  AuthorizationDialog authDialog = new AuthorizationDialog(this, " ")
                                  authDialog.show()
                                  authorized = authDialog.authorized
                              }
                              if (authorized) {
                                  order = OrderController.addItemToOrder(order, item, surte)
                                  updateOrder( order.id )
                                  validaLC(item, false)
                                  controlItem(item, false)
                                  List<IPromotionAvailable> promotionsListTmp = new ArrayList<>()
                                  promotionsListTmp.addAll(promotionList)
                                  if( !holdPromo ){
                                    for(IPromotionAvailable promo : promotionsListTmp){
                                      this.promotionDriver.requestCancelPromotion(promo)
                                      OrderController.deleteCuponMv( order.id )
                                    }
                                  }
                              }
                          } else {
                              sb.optionPane(message: MSJ_VENTA_NEGATIVA, messageType: JOptionPane.ERROR_MESSAGE,)
                                      .createDialog(this, TXT_VENTA_NEGATIVA_TITULO)
                                      .show()
                          }
                      } else {
                          order = OrderController.addItemToOrder(order, item, surte)
                          updateOrder( order.id )
                          validaLC(item, false)
                          controlItem(item, false)
                          List<IPromotionAvailable> promotionsListTmp = new ArrayList<>()
                          promotionsListTmp.addAll(promotionList)
                          if( !holdPromo ){
                            for(IPromotionAvailable promo : promotionsListTmp){
                              this.promotionDriver.requestCancelPromotion(promo)
                              OrderController.deleteCuponMv( order.id )
                            }
                          }
                      }
                  }
              }
          } else {
            sb.optionPane(message: MSJ_LENTE_INVALIDO, messageType: JOptionPane.ERROR_MESSAGE,)
                .createDialog(this, TXT_LENTE_INVALIDO)
                .show()
          }
        } else {
          sb.optionPane(message: MSJ_PAQUETE_INVALIDO, messageType: JOptionPane.ERROR_MESSAGE,)
                  .createDialog(this, TXT_PAQUETE_INVALIDO)
                  .show()
        }
    }

    private def doClose = {
        sb.doLater {
            doBindings()
            if (order.payments.size() == 0) {
                Integer question = JOptionPane.showConfirmDialog(new JDialog(), MSJ_CAMBIAR_VENDEDOR, TXT_CAMBIAR_VENDEDOR,
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)
                if (question == 0) {
                    if(order != null && order?.id != null){
                      OrderController.deleteCuponMv( StringUtils.trimToEmpty(order.id) )
                      this.promotionDriver.requestPromotionSave(order?.id, false)
                    }
                    MainWindow.instance.requestLogout()
                }
            } else {
                sb.optionPane(message: MSJ_QUITAR_PAGOS, messageType: JOptionPane.INFORMATION_MESSAGE,)
                        .createDialog(this, TXT_QUITAR_PAGOS)
                        .show()
            }
        }
    }

    private def doHelp = { ActionEvent ev ->
      if( helpItemSearchDialog == null ){
        helpItemSearchDialog = new HelpItemSearchDialog()
        helpItemSearchDialog.show()
        itemSearch.text = null
        helpItemSearchDialog = null
      }
    }

    private def doPrint = { ActionEvent ev ->
      if( OrderController.validOrderNotCancelled( StringUtils.trimToEmpty(order?.id) ) ){
        int artCount = 0
        dioptra = new Dioptra()
        Boolean hasDioptra = false
        Boolean hasLc = false
        for(OrderItem it : order.items){
            Item result = ItemController.findItemsById(it.item.id)
            if( result != null ){
              controlItem( result, true )
              if( result.indexDiotra != null && result.indexDiotra.trim().length() > 0 ){
                hasDioptra = true
              }
            }
            if( StringUtils.trimToEmpty(it.item.type).equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO) ){
              hasLc = true
            }
        }
        if( !hasDioptra ){
          order.dioptra = null
        }
        dioptra = OrderController.generaDioptra(OrderController.preDioptra(order?.dioptra))
        String dio = OrderController.codigoDioptra(dioptra)
        List<Item> itemLc = OrderController.existeLenteContacto(order)
        if( itemLc.size() > 0 ) {
          for(Item it : itemLc){
            validaLC( it, true )
          }
        }
        Boolean warranty = false
        if( true ){
            NotaVentaJava notaWarranty = OrderController.ensureOrder( StringUtils.trimToEmpty(order.id) )
            warranty = OrderController.validWarranty( OrderController.findOrderByidOrder(StringUtils.trimToEmpty(order.id)), true, null, notaWarranty.idFactura, true )
        } else {
          warranty = true
        }
        if( warranty ){
          if( validLensesPack() ){
            Boolean continueSave = true
            rec = OrderController.findRx(order, customer)
            if( hasLc && (rec == null || rec.idReceta == null) ){
              Branch branch = Session.get(SessionItem.BRANCH) as Branch
              EditRxDialog editRx = new EditRxDialog(this, new Rx(), customer?.id, branch?.id, 'Nueva Receta', "MONOFOCAL", false, true)
              editRx.show()
              try {
                if( rec != null ){
                  OrderController.saveRxOrder(order?.id, this.rec.idReceta)
                } else {
                  continueSave = false
                }
              } catch ( Exception e ){ println e }
            }
              if( continueSave ){
                if (!dioptra.getLente().equals(null)) {
                    Item i = OrderController.findArt(dio.trim())
                    if (i?.id != null || dio.trim().equals('nullnullnullnullnullnull')) {
                        String tipoArt = null
                        for (int row = 0; row <= itemsModel.rowCount; row++) {
                            String artString = itemsModel.getValueAt(row, 0).toString()
                            if (artString.trim().equals('SV')) {
                                artCount = artCount + 1
                                tipoArt = 'MONOFOCAL'
                            } else if (artString.trim().equals('B')) {
                                artCount = artCount + 1
                                tipoArt = 'BIFOCAL'
                            } else if (artString.trim().equals('P')) {
                                artCount = artCount + 1
                                tipoArt = 'PROGRESIVO'
                            }
                        }
                        armazonString = OrderController.armazonString(order?.id)

                        if (artCount == 0) {
                            JButton source = ev.source as JButton
                            source.enabled = false
                            ticketRx = false
                            flujoImprimir(artCount)
                            source.enabled = true
                        } else {
                            rec = OrderController.findRx(order, customer)
                            Order armOrder = OrderController.getOrder(order?.id)
                            if (rec.idReceta == null) {   //Receta Nueva
                                Branch branch = Session.get(SessionItem.BRANCH) as Branch
                                EditRxDialog editRx = new EditRxDialog(this, new Rx(), customer?.id, branch?.id, 'Nueva Receta', tipoArt, false, true)
                                editRx.show()
                                try {
                                    OrderController.saveRxOrder(order?.id, rec.idReceta)
                                    JButton source = ev.source as JButton
                                    source.enabled = false
                                    ticketRx = true
                                    if (armOrder?.udf2.equals('')) {
                                        ArmRxDialog armazon = new ArmRxDialog(this, order, armazonString)
                                        armazon.show()
                                        order = armazon.order
                                    }
                                    flujoImprimir(artCount)
                                    source.enabled = true
                                } catch ( Exception e) { println e }
                            } else {
                                JButton source = ev.source as JButton
                                source.enabled = false
                                ticketRx = true
                                if (armOrder?.udf2.equals('')) {
                                    ArmRxDialog armazon = new ArmRxDialog(this, order, armazonString)
                                    armazon.show()
                                    order = armazon.order
                                }
                                flujoImprimir(artCount)
                                source.enabled = true
                            }
                        }
                    } else {
                        sb.optionPane(message: "Codigo Dioptra Incorrecto", optionType: JOptionPane.DEFAULT_OPTION)
                                .createDialog(new JTextField(), "Error")
                                .show()
                    }
                } else {
                    ticketRx = false
                    flujoImprimir(artCount)
                }
            }
          } else {
                sb.optionPane(message: "Favor de capturar paquete.", optionType: JOptionPane.DEFAULT_OPTION)
                        .createDialog(new JTextField(), "Error")
                        .show()
          }
        } else {
          lstWarranty.clear()
          if( !canceledWarranty ){
            TXT_ERROR_WARRANTY = "No se puede registrar la venta"
            if( OrderController.MSJ_ERROR_WARRANTY.length() <= 0 ){
              MSJ_ERROR_WARRANTY = "Error al asignar el seguro, Verifiquelo e intente nuevamente."
            } else {
              MSJ_ERROR_WARRANTY = OrderController.MSJ_ERROR_WARRANTY
            }
            sb.optionPane(
               message: MSJ_ERROR_WARRANTY,
               messageType: JOptionPane.ERROR_MESSAGE
            ).createDialog( this, TXT_ERROR_WARRANTY )
              .show()
          }
        }
      } else {
          JOptionPane.showMessageDialog( null, "La venta ya ha sido anulada",
                  "Venta Anulada", JOptionPane.ERROR_MESSAGE )
      }
    }

    private void controlItem(Item item, Boolean itemDelete) {
      String indexDioptra = item?.indexDiotra
      logger.debug('Index Dioptra del Articulo : ' + item?.indexDiotra)
      if (!StringUtils.trimToNull(indexDioptra).equals(null) && !StringUtils.trimToNull(item?.indexDiotra).equals(null)) {
        Dioptra nuevoDioptra = OrderController.generaDioptra(item?.indexDiotra)
        dioptra = OrderController.validaDioptra(dioptra, nuevoDioptra)
        logger.debug('Dioptra Generado :' + dioptra)
        antDioptra = OrderController.addDioptra(order, OrderController.codigoDioptra(dioptra))
        order?.dioptra = OrderController.codigoDioptra(antDioptra)
      } else {
        order?.dioptra = OrderController.codigoDioptra(antDioptra)
      }
      logger.debug('Codigo Dioptra :' + antDioptra)
      if (item?.name?.trim().equals('MONTAJE') && !itemDelete) {
        User u = Session.get(SessionItem.USER) as User
        CapturaSuyoDialog capturaSuyoDialog = new CapturaSuyoDialog(order, u,true)
        capturaSuyoDialog.show()
      }

      if( !itemDelete && !StringUtils.trimToNull(indexDioptra).equals(null)){
        rec = validarGenericoB(item)
        OrderController.saveRxOrder(order?.id, rec?.idReceta)
      }
      updateOrder(order?.id)
      if (!order.customer.equals(customer)) {
        order.customer = customer
      }
    }

    private void flujoImprimir(int artCount) {
        armazonString = null
        Boolean validOrder = isValidOrder()
        if (artCount != 0) {
            Parametro diaIntervalo = Registry.find(TipoParametro.DIA_PRO)
            Date diaPrometido = new Date() + diaIntervalo?.valor.toInteger()
            OrderController.savePromisedDate(order?.id, diaPrometido)
            Double pAnticipo = Registry.getAdvancePct()

            Boolean onlyInventariable = OrderController.validOnlyInventariable( order )
            if( onlyInventariable && order?.paid < order?.total ){
              AuthorizationDialog authDialog = new AuthorizationDialog(this, "Anticipo requiere autorizaci\u00f3n")
              authDialog.show()
              if (authDialog.authorized) {
                  advanceOnlyInventariable = true
                  validOrder = isValidOrder()
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
                  AuthorizationDialog authDialog = new AuthorizationDialog(this, "Anticipo menor al permitido, esta operacion requiere autorizaci\u00f3n")
                  authDialog.show()
                  if (authDialog.authorized) {
                      validOrder = isValidOrder()
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
                validOrder = isValidOrder()
              }
        } else {
            if( OrderController.validGenericNoDelivered( order.id ) ){
                Double pAnticipo = Registry.getAdvancePct()
                Boolean requierAuth = OrderController.requiereAuth( order )
                if(order?.paid < (order?.total * pAnticipo)){
                    if( requierAuth ){
                        AuthorizationDialog authDialog = new AuthorizationDialog(this, "Anticipo menor al permitido, esta operacion requiere autorizaci\u00f3n")
                        authDialog.show()
                        if (authDialog.authorized) {
                            validOrder = isValidOrder()
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
                    validOrder = isValidOrder()
                }
            } else {
              validOrder = isValidOrder()
            }
        }
        if( !validLenses() ){
            order.dioptra = null
        }
        if (validOrder) {
            Boolean onlyInventariable = OrderController.validOnlyInventariable( order )
            Boolean noDelivered = OrderController.validGenericNoDelivered( order.id )
            if( onlyInventariable && order?.paid < order?.total && !noDelivered ){
                AuthorizationDialog authDialog = new AuthorizationDialog(this, "Anticipo requiere autorizaci\u00f3n")
                authDialog.show()
                if (authDialog.authorized) {
                    advanceOnlyInventariable = true
                    doBindings()
                    refreshDioptra()
                    if( dioptra != null ){
                      if( OrderController.validDioptra( StringUtils.trimToEmpty(order.id) ) ){
                        saveOrder()
                      } else {
                        refreshDioptra()
                        saveOrder()
                      }
                    } else {
                      saveOrder()
                    }
                } else {
                    validOrder = false
                    sb.optionPane(
                            message: 'Datos no validos',
                            messageType: JOptionPane.ERROR_MESSAGE
                    ).createDialog(this, 'No se puede registrar la venta')
                            .show()
                }
            } else {
              doBindings()
              if( dioptra.material != null || dioptra.lente != null || dioptra.tipo != null || dioptra.especial != null ||
                      dioptra.tratamiento != null || dioptra.color != null ){
                if( OrderController.validDioptra( StringUtils.trimToEmpty(order.id) ) ){
                  saveOrder()
                } else {
                  refreshDioptra()
                  saveOrder()
                }
              } else {
                  saveOrder()
              }
            }
        }
    }

    private void saveOrder() {
        User user = Session.get(SessionItem.USER) as User
        String vendedor = user.username
        if( OrderController.showValidEmployee() ){
          CambiaVendedorDialog cambiaVendedor = new CambiaVendedorDialog(this,user?.username)
          cambiaVendedor.show()
          vendedor = cambiaVendedor?.vendedor
        }

        //CuponMvView cuponMvView = OrderController.cuponValid( customer.id )
        Order newOrder = OrderController.placeOrder(order, vendedor, false)
        OrderController.genreatedEntranceSP( StringUtils.trimToEmpty(newOrder.id) )
        if( newOrder.rx != null ){
          OrderController.updateExam( newOrder )
        }

        if(numQuote > 0){
          OrderController.updateQuote( newOrder, numQuote )
          numQuote = 0
        }
        this.promotionDriver.requestPromotionSave(newOrder?.id, true)
        Boolean cSaldo = false
        OrderController.validaEntrega(StringUtils.trimToEmpty(newOrder?.bill),newOrder?.branch?.id?.toString(), true)
        Boolean needJb = OrderController.creaJb(StringUtils.trimToEmpty(newOrder?.ticket), cSaldo)

        ItemController.updateLenteContacto( newOrder.id )
        if(isLc(newOrder)){
          OrderController.creaJbLc( newOrder.id )
        }
        if( OrderController.hasOrderLc(newOrder.bill) ){
          OrderController.printTicketEnvioLc( StringUtils.trimToEmpty(newOrder.bill) )
        }
        /*String idFacturaTransLc = StringUtils.trimToEmpty(OrderController.isReuseOrderLc( StringUtils.trimToEmpty(newOrder.id) ))
        if( idFacturaTransLc.length() > 0 ){
            CancellationController.sendTransferOrderLc( idFacturaTransLc, newOrder.id )
        }*/

        OrderController.validaSurtePorGenerico( order )
        if( advanceOnlyInventariable ){
          OrderController.creaJbAnticipoInventariables( newOrder?.id )
          advanceOnlyInventariable = false
        }
        if (StringUtils.isNotBlank(newOrder?.id)) {

            Branch branch = Session.get(SessionItem.BRANCH) as Branch
            OrderController.insertaAcuseAPAR(newOrder, branch)

            Boolean montaje = false
            List<OrderItem> items = newOrder?.items
            Iterator iterator = items.iterator()
            while (iterator.hasNext()) {
                Item item = iterator.next().item
                if (item?.name.trim().equals('MONTAJE')) {
                    montaje = true
                }
            }
            if (montaje == true) {
                Boolean registroTmp = OrderController.revisaTmpservicios(newOrder?.id)
                User u = Session.get(SessionItem.USER) as User
                if (registroTmp == false) {
                    CapturaSuyoDialog capturaSuyoDialog = new CapturaSuyoDialog(order, u,false)
                    capturaSuyoDialog.show()
                }

                OrderController.printSuyo(newOrder,u)
            }
            /*if( needJb ){
                OrderController.creaJb(newOrder?.ticket.trim(), cSaldo)
            }*/
            CuponMv cuponMv = null
            Boolean validClave = true
            Boolean ensureApply = false
            Boolean ffApply = false
            Boolean hasC1 = false
            for(int i=0;i<promotionList.size();i++){
              if(promotionList.get(i) instanceof PromotionDiscount){
                cuponMv = OrderController.obtenerCuponMvByClave( StringUtils.trimToEmpty(promotionList.get(i).discountType.description) )
                if( cuponMv == null ){
                  String clave = OrderController.descuentoClavePoridFactura( order.id )
                  cuponMv = OrderController.obtenerCuponMvByClave( StringUtils.trimToEmpty(clave) )
                }
                if( StringUtils.trimToEmpty(promotionList.get(i).discountType.text).equalsIgnoreCase("Redencion de Seguro") ||
                        (StringUtils.trimToEmpty(promotionList.get(i).discountType.text).equalsIgnoreCase("DESCUENTO CUPON") &&
                                StringUtils.trimToEmpty(promotionList.get(i).discountType.description).length() >= 11) ){
                  ensureApply = true
                }
                if( cuponMv != null ){
                  if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("F") ){
                    ffApply = true
                  }
                  break
                } else if(!OrderController.generatesCoupon(promotionList.get(i).discountType.description)) {
                  validClave = false
                }
              }
            }

            if( promotionList.size() <= 0 ){
                validClave = true
            }

            if(validClave){
              for(Payment payment : newOrder.payments){
                if( Registry.paymentsTypeNoCupon.contains( payment.paymentTypeId ) ){
                  validClave = false
                }
                if( TAG_FORMA_PAGO_C1.equalsIgnoreCase( payment.paymentTypeId ) ){
                  hasC1 = true
                }
              }
            }

            if( newOrder.total.compareTo(BigDecimal.ZERO) > 0 ){
              if( cuponMv != null ){
                Integer numeroCupon = cuponMv.claveDescuento.startsWith("8") ? 2 : 3
                OrderController.updateCuponMv( cuponMv.facturaOrigen, newOrder.id, cuponMv.montoCupon, numeroCupon, false)
                /*if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("F") ){
                  generatedCoupons( validClave, newOrder )
                }*/
              } else if( !ensureApply && !ffApply ){
                generatedCoupons( validClave, newOrder )
              }
            }
            if( !ensureApply && !ffApply ){
              if( OrderController.insertSegKig && !hasC1 ){
                Boolean hasLensKid = false
                Boolean hasEnsureKid = false
                for(OrderItem oi : newOrder.items){
                  Articulo articulo = ItemController.findArticle( oi.item.id )
                  String type = StringUtils.trimToEmpty(articulo.subtipo).length() > 0 ? StringUtils.trimToEmpty(articulo.subtipo) : StringUtils.trimToEmpty(articulo.idGenSubtipo)
                  if( StringUtils.trimToEmpty(type).startsWith(TAG_SUBTIPO_NINO) ){
                    hasLensKid = true
                  }
                  if( StringUtils.trimToEmpty(articulo.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) ){
                    hasEnsureKid = true
                  }
                }
                if( hasLensKid && !hasEnsureKid ){
                  itemSearch.text = "SEG"
                  doItemSearch( true )
                  newOrder = OrderController.placeOrder(newOrder, vendedor, false)
                  OrderController.insertSegKig = false
                }
              }
            }
            OrderController.printOrder(newOrder.id)
            OrderController.printReuse( StringUtils.trimToEmpty(newOrder.id) )
            if (ticketRx == true) {
              OrderController.printRx(newOrder.id, false)
              OrderController.fieldRX(newOrder.id)
            }
            reviewForTransfers(newOrder.id)
            sb.doOutside {
              try{
                OrderController.runScriptBckpOrder( newOrder )
              } catch ( Exception e ){
                println e
              }
            }
            // Flujo despues de imprimir nota de venta}
            Order otherOrder = CustomerController.requestOrderByCustomer(this, customer)
            if( otherOrder != null && otherOrder?.id != null ){
              isPaying = true
            }
        } else {
            sb.optionPane(
                    message: 'Ocurrio un error al registrar la venta, intentar nuevamente',
                    messageType: JOptionPane.ERROR_MESSAGE
            ).createDialog(this, 'No se puede registrar la venta')
                    .show()
        }
    }

    private boolean isValidOrder() {
        if (itemsModel.size() == 0) {
            sb.optionPane(
                    message: 'Se debe agregar al menos un art\u00edculo a la venta',
                    messageType: JOptionPane.ERROR_MESSAGE
            ).createDialog(this, 'No se puede registrar la venta')
                    .show()
            return false
        }
        return true
    }




    protected void onMouseClickedAtPromotions(MouseEvent pEvent) {
      OperationType operationType1 = operationType.selectedItem as OperationType
      if (SwingUtilities.isRightMouseButton(pEvent) && (pEvent.getID() == MouseEvent.MOUSE_CLICKED) &&
              !operationType1.equals(OperationType.PAYING )) {
        if (discountMenu == null) {
          discountMenu = new DiscountContextMenu(this.promotionDriver)
        }
        discountMenu.activate(pEvent, this)
      }
    }

    protected void onTogglePromotion(IPromotionAvailable pPromotion, Boolean pNewValue) {
      OperationType operationType1 = operationType.selectedItem as OperationType
      if( !operationType1.equals(OperationType.PAYING) ){
          Boolean valid = true
          Boolean notMinAmount = false
          BigDecimal minimum = BigDecimal.ZERO
          BigDecimal totalOrder = BigDecimal.ZERO
          for(int i=0;i<promotionList.size();i++){
              if( promotionList.get(i) instanceof PromotionDiscount){
                  valid = false
              }
          }
          if( valid ){
              if( pPromotion instanceof PromotionAvailable){
                  if( pPromotion.promotion instanceof PromotionCombo){
                      minimum = pPromotion.promotion.base.entity.montoMinimo
                  } else if( pPromotion.promotion instanceof PromotionSingle){
                      minimum = pPromotion.promotion.entity.montoMinimo
                  }
              }
              for(OrderItem det : order.items){
                  if( !StringUtils.trimToEmpty(det.item.type).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                      totalOrder = totalOrder.add( det.item.listPrice.multiply(new BigDecimal(det.quantity)) )
                  }
              }
              if( minimum.compareTo(totalOrder) > 0 ){
                  notMinAmount = true
              }
          }
          if (pNewValue && valid) {
              if( !notMinAmount ){
                  this.promotionDriver.requestApplyPromotion(pPromotion)
              } else {
                  NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US)
                  sb.optionPane(message: "Monto Mínimo de Compra ${nf.format(minimum)}", optionType: JOptionPane.DEFAULT_OPTION)
                          .createDialog(new JTextField(), "Error")
                          .show()
              }
          } else {
              this.promotionDriver.requestCancelPromotion(pPromotion)

              Payment payment = null
              for(Payment pay : order.payments){
                  if( StringUtils.trimToEmpty(pay.paymentTypeId).equalsIgnoreCase(TAG_PAYMENT_TYPE_TRANSF) ){
                      payment = pay
                  }
              }
              if( payment != null ){
                  List<CuponMv> lstCupons = OrderController.obtenerCuponMvByTargetOrder( StringUtils.trimToEmpty(order.id) )
                  if( lstCupons.size() > 0 ){
                      OrderController.existDiscountKey( StringUtils.trimToEmpty(lstCupons.first().claveDescuento),
                              StringUtils.trimToEmpty(payment.paymentReference) )
                  }
              }

              OrderController.deleteCuponMv( order.id )
          }
          for(Payment payment : order.payments){
              OrderController.removePaymentFromOrder( order.id, payment )
          }
          OrderController.saveOrder( order )
          updateOrder(order?.id)
      }
    }

    Order getOrder() {
        return order
    }

    public List<IPromotionAvailable> getPromotionList() {
        return this.promotionList
    }

    DefaultTableModel getPromotionModel() {
        return this.promotionModel
    }

    void refreshData() {
        if( promotionListTmp.size() > 0 ){
          promotionList.addAll( promotionListTmp )
        }
        if(order.deals.size() > 0 ){
          if(order.deals.first() instanceof IPromotion){
              OrderLinePromotion orderDiscount = order.deals.first()
              println promotionList .size()
              for(PromotionAvailable promotionAvailable : promotionList){
                  if(orderDiscount.promotion.idPromocion == promotionAvailable.promotion.entity.idPromocion
                          && flag <= 0 ){
                      this.promotionDriver.getApplyPromotion(promotionAvailable)
                      flag = flag+1
                  }
              }
          }
        }
        this.promotionDriver.enableItemsTableEvents(false)
        this.getPromotionModel().fireTableDataChanged()
        updateOrder(order?.id)
        this.promotionDriver.enableItemsTableEvents(true)
    }

    public void focusGained(FocusEvent e) {

    }

    public void focusLost(FocusEvent e) {
        if (itemSearch.text.length() > 0) {
            doItemSearch( false )
            itemSearch.requestFocus()
        }
    }

    private void fireRequestQuote() {
      if( OrderController.validOrderNotCancelled( StringUtils.trimToEmpty(order?.id) ) ){
        dioptra = OrderController.generaDioptra(OrderController.preDioptra(order?.dioptra))
        String dio = OrderController.codigoDioptra(dioptra)
        Item i = OrderController.findArt(dio.trim())
          if (itemsModel.size() > 0) {
              if (paymentsModel.size() == 0) {
                OrderController.requestSaveAsQuote(order, customer)
                CustomerController.deletedClienteProceso( customer.id )
                OrderController.deleteCuponMv( StringUtils.trimToEmpty(order.id) )
                this.reset()
              } else {
                sb.doLater {
                  OrderController.notifyAlert(TXT_REQUEST_QUOTE, TXT_PAYMENTS_PRESENT)
                }
              }
          } else {
            sb.doLater {
              OrderController.notifyAlert(TXT_REQUEST_QUOTE, TXT_NO_ORDER_PRESENT)
            }
          }
      } else {
          JOptionPane.showMessageDialog( null, "La venta ya ha sido anulada",
                  "Venta Anulada", JOptionPane.ERROR_MESSAGE )
      }
    }

    private void setCustomerInOrder() {
        if ((order?.id != null) && (customer != null)) {
            if (!order.customer.equals(customer)) {
                order.customer = customer
                OrderController.saveCustomerForOrder(order.id, customer.id)
            }
        }
    }

    void setCustomerInOrderFromMenu( Customer customer ) {
        if ((order?.id != null) && (customer != null)) {
            if (!order.customer.equals(customer)) {
                order.customer = customer
                OrderController.saveCustomerForOrder(order.id, customer.id)
            }
        }
        this.customer = customer
        if(!operationType.selectedItem.equals(OperationType.DOMESTIC) ){
          operationType.addItem( OperationType.DOMESTIC )
        }
        if(this.customer != null){
          if( CustomerController.findProccesClient(this.customer.id) != null ){
            activeDialogProccesCustomer = false
            operationType.setSelectedItem( OperationType.PENDING )
          } else {
            activeDialogProccesCustomer = false
            operationType.setSelectedItem( OperationType.PENDING )
          }
        } else {
          operationType.removeItem( OperationType.DOMESTIC )
        }
        doBindings()
    }

    void reset() {
        order = new Order()
        customer = CustomerController.findDefaultCustomer()
        this.getPromotionDriver().init(this)
        dioptra = new Dioptra()
        antDioptra = new Dioptra()
        order?.dioptra = null
        doBindings()
        operationType.setSelectedItem(OperationType.DEFAULT)
    }


    void resetJava() {
      order = new Order()
      customer = CustomerController.findDefaultCustomerJava()
      this.getPromotionDriver().init(this)
      dioptra = new Dioptra()
      antDioptra = new Dioptra()
      order?.dioptra = null
      doBindings()
      operationType.setSelectedItem(OperationType.DEFAULT)
    }


    void setCustomer(Customer pCustomer) {
        this.logger.debug(String.format('Assign Customer: %s', pCustomer.toString()))

        customer = pCustomer
        doBindings()
    }

    void setOrder(Order pOrder) {
        this.logger.debug(String.format('Assign Order: %s', pOrder.toString()))
        this.updateOrder(pOrder.id)
    }

    void setPromotion( Order pOrder ){
      this.promotionDriver.updatePromotionClient( pOrder )
    }

    void setOperationTypeSelected(OperationType pOperation) {
        operationType.setSelectedItem(pOperation)
    }

    void disableUI() {
        uiEnabled = false
    }

    void enableUI() {
        this.doBindings()
        uiEnabled = true
    }

    private void fireRequestContinue(DefaultTableModel itemsModel) {
      if( OrderController.validOrderNotCancelled( StringUtils.trimToEmpty(order?.id) ) ){
        int artCount = 0
        Boolean hasLc = false
        dioptra = new Dioptra()
        Boolean hasDioptra = false
        Boolean hasOnlyEnsure = false
        for(OrderItem it : order.items){
            Item result = ItemController.findItemsById(it.item.id)
            if( result != null ){
                controlItem( result, true )
                if( result.indexDiotra != null && result.indexDiotra.trim().length() > 0 ){
                    hasDioptra = true
                }
            }
          if( StringUtils.trimToEmpty(result.type).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
            hasOnlyEnsure = true
          }
          if( StringUtils.trimToEmpty(result.type).equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO) ){
            hasLc = true
          }
        }
        if( !hasDioptra ){
            order.dioptra = null
        }
        dioptra = OrderController.generaDioptra(OrderController.preDioptra(order?.dioptra))
        String dio = OrderController.codigoDioptra(dioptra)
        List<Item> itemLc = OrderController.existeLenteContacto(order)
        if( itemLc.size() > 0 ) {
          for(Item it : itemLc){
            validaLC( it, true )
          }
        }
      Boolean warranty = true
      if( hasOnlyEnsure ){
        if( order.items.size() > 1 ){
          hasOnlyEnsure = false
        }
      }
      if( !hasOnlyEnsure ){
        if( true ){
          NotaVentaJava notaWarranty = OrderController.ensureOrder( StringUtils.trimToEmpty(order.id) )
          warranty = OrderController.validWarranty( OrderController.findOrderJavaByidOrder(StringUtils.trimToEmpty(order.id)), true, null, notaWarranty.idFactura, false )
        } else {
          warranty = true
        }
      }
      if( warranty ){
        if( validLensesPack() ){
          rec = OrderController.findRx(order, customer)
          Boolean continueSave = true
          if( hasLc && (rec == null || rec.idReceta == null) ){
            Branch branch = Session.get(SessionItem.BRANCH) as Branch
            EditRxDialog editRx = new EditRxDialog(this, new Rx(), customer?.id, branch?.id, 'Nueva Receta', "MONOFOCAL", false, true)
            editRx.show()
            try {
              if( rec != null ){
                OrderController.saveRxOrder(order?.id, this.rec.idReceta)
              } else {
                continueSave = false
              }
            } catch ( Exception e ){ println e }
          }
          if( continueSave ){
          if (!dioptra.getLente().equals(null)) {
            Item i = OrderController.findArt(dio.trim())
            if (i?.id != null || dio.trim().equals('nullnullnullnullnullnull')) {
              String tipoArt = null
              for (int row = 0; row <= itemsModel.rowCount; row++) {
                          String artString = itemsModel.getValueAt(row, 0).toString()
                          if (artString.trim().equals('SV')) {
                              artCount = artCount + 1
                              tipoArt = 'MONOFOCAL'
                          } else if (artString.trim().equals('B')) {
                              artCount = artCount + 1
                              tipoArt = 'BIFOCAL'
                          } else if (artString.trim().equals('P')) {
                              artCount = artCount + 1
                              tipoArt = 'PROGRESIVO'
                          }
                      }
                      armazonString = OrderController.armazonString(order?.id)

                      if (artCount == 0) {
                          ticketRx = false
                          flujoContinuar()
                      } else {
                          rec = OrderController.findRx(order, customer)
                          Order armOrder = OrderController.getOrder(order?.id)
                          if (rec.idReceta == null) {   //Receta Nueva
                              Branch branch = Session.get(SessionItem.BRANCH) as Branch
                              EditRxDialog editRx = new EditRxDialog(this, new Rx(), customer?.id, branch?.id, 'Nueva Receta', tipoArt, false, false)
                              editRx.show()
                              try {
                                  OrderController.saveRxOrder(order?.id, rec.idReceta)
                                  ticketRx = true
                                  if (armOrder?.udf2.equals('')) {
                                      ArmRxDialog armazon = new ArmRxDialog(this, order, armazonString)
                                      armazon.show()
                                      order = armazon.order
                                  }
                                  flujoContinuar()
                              } catch ( Exception e) { println e }
                          } else {
                              ticketRx = true
                              if (armOrder?.udf2.equals('')) {
                                  ArmRxDialog armazon = new ArmRxDialog(this, order, armazonString)
                                  armazon.show()
                                  order = armazon.order
                              }
                              flujoContinuar()
                          }
                      }
                  } else {
                      sb.optionPane(message: "Codigo Dioptra Incorrecto", optionType: JOptionPane.DEFAULT_OPTION)
                              .createDialog(new JTextField(), "Error")
                              .show()
                  }
              } else {
                  ticketRx = false
                  flujoContinuar()
              }
            }
        } else {
          sb.optionPane(message: "Favor de capturar paquete.", optionType: JOptionPane.DEFAULT_OPTION)
             .createDialog(new JTextField(), "Error")
             .show()
        }
      } else {
          lstWarranty.clear()
          if( !canceledWarranty ){
              TXT_ERROR_WARRANTY = "No se puede registrar la venta"
              if( MSJ_ERROR_WARRANTY.length() <= 0 ){
                  MSJ_ERROR_WARRANTY = "Error al asignar el seguro, Verifiquelo e intente nuevamente."
              }
              JOptionPane.showMessageDialog( null, MSJ_ERROR_WARRANTY,
                      TXT_ERROR_WARRANTY, JOptionPane.ERROR_MESSAGE )
          }
      }
    } else {
        JOptionPane.showMessageDialog( null, "La venta ya ha sido anulada",
              "Venta Anulada", JOptionPane.ERROR_MESSAGE )
      }
    }

    private void flujoContinuar() {
      if (isPaymentListEmpty()) {
        sb.doLater {
          if( !validLenses() ){
            order.dioptra = null
          }
          Order newOrder = OrderController.saveOrderJava(order)
          CustomerController.updateCustomerInSite(this.customer.id)
          this.promotionDriver.requestPromotionJavaSave(newOrder?.id, false)
          for(IPromotionAvailable promo : promotionList){
            if( promo instanceof PromotionDiscount ){
              OrderController.updateCuponMvByClave(order.id, StringUtils.trimToEmpty(promo.discountType.description))
            }
          }
          this.reset()
        }
      } else {
        sb.doLater {
          OrderController.notifyAlert(TXT_REQUEST_CONTINUE, TXT_PAYMENTS_PRESENT)
        }
      }
    }


    private void fireRequestCancelOrder( ) {
      List<Order> lstOrders = CancellationController.findOrderToResetValues(order.id)
      for (Order order : lstOrders) {
        CancellationController.resetValuesofCancellation(order.id)
      }
      OrderController.deleteOrder( StringUtils.trimToEmpty(order.id) )
      User u = Session.get(SessionItem.USER) as User
      OrderController.addLogOrderCancelled( StringUtils.trimToEmpty(order.id), StringUtils.trimToEmpty(u.username) )
      this.reset()
    }



    private void fireRequestNewOrder(DefaultTableModel itemsModel) {
      if( OrderController.validOrderNotCancelled( StringUtils.trimToEmpty(order?.id) ) ){
        int artCount = 0
        dioptra = new Dioptra()
        Boolean hasDioptra = false
        Boolean hasOnlyEnsure = false
        Boolean hasLc = false
        for(OrderItem it : order.items){
            Item result = ItemController.findItemsById(it.item.id)
            if( result != null ){
                controlItem( result, true )
                if( result.indexDiotra != null && result.indexDiotra.trim().length() > 0 ){
                    hasDioptra = true
                }
            }
          if( StringUtils.trimToEmpty(result.type).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
            hasOnlyEnsure = true
          }
          if( StringUtils.trimToEmpty(result.type).equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO) ){
            hasLc = true
          }
        }
        if( !hasDioptra ){
            order.dioptra = null
        }
        dioptra = OrderController.generaDioptra(OrderController.preDioptra(order?.dioptra))
        String dio = OrderController.codigoDioptra(dioptra)
        List<Item> itemLc = OrderController.existeLenteContacto(order)
        if( itemLc.size() > 0 ) {
            for(Item it : itemLc){
                validaLC( it, true )
            }
        }
      Boolean warranty = true
      if( hasOnlyEnsure ){
        if( order.items.size() > 1 ){
          hasOnlyEnsure = false
        }
      }
      if( !hasOnlyEnsure ){
        if( true ){
          NotaVentaJava notaWarranty = OrderController.ensureOrder( StringUtils.trimToEmpty(order.id) )
          warranty = OrderController.validWarranty( OrderController.findOrderByidOrder(StringUtils.trimToEmpty(order.id)), true, null, notaWarranty.idFactura, false )
        } else {
          warranty = true
        }
      }
      if( warranty ){
        if( validLensesPack() ){
          Boolean continueSave = true
          rec = OrderController.findRx(order, customer)
          if( hasLc && (rec == null || rec.idReceta == null) ){
            Branch branch = Session.get(SessionItem.BRANCH) as Branch
            EditRxDialog editRx = new EditRxDialog(this, new Rx(), customer?.id, branch?.id, 'Nueva Receta', "MONOFOCAL", false, true)
            editRx.show()
            try {
              if( rec != null ){
                OrderController.saveRxOrder(order?.id, this.rec.idReceta)
              } else {
                continueSave = false
              }
            } catch ( Exception e ){ println e }
          }
          if( continueSave ){
              if (!dioptra.getLente().equals(null)) {
                  Item i = OrderController.findArt(dio.trim())
                  if (i?.id != null || dio.trim().equals('nullnullnullnullnullnull')) {
                      String tipoArt = null
                      for (int row = 0; row <= itemsModel.rowCount; row++) {
                          String artString = itemsModel.getValueAt(row, 0).toString()
                          if (artString.trim().equals('SV')) {
                              artCount = artCount + 1
                              tipoArt = 'MONOFOCAL'
                          } else if (artString.trim().equals('B')) {
                              artCount = artCount + 1
                              tipoArt = 'BIFOCAL'
                          } else if (artString.trim().equals('P')) {
                              artCount = artCount + 1
                              tipoArt = 'PROGRESIVO'
                          }
                      }
                      armazonString = OrderController.armazonString(order?.id)

                      if (artCount == 0) {
                          ticketRx = false
                          flujoOtraOrden()
                      } else {
                          rec = OrderController.findRx(order, customer)
                          Order armOrder = OrderController.getOrder(order?.id)
                          if (rec.idReceta == null) {   //Receta Nueva
                              Branch branch = Session.get(SessionItem.BRANCH) as Branch
                              EditRxDialog editRx = new EditRxDialog(this, new Rx(), customer?.id, branch?.id, 'Nueva Receta', tipoArt, false, false)
                              editRx.show()
                              try {
                                  OrderController.saveRxOrder(order?.id, rec.idReceta)
                                  ticketRx = true
                                  if (armOrder?.udf2.equals('')) {
                                      ArmRxDialog armazon = new ArmRxDialog(this, order, armazonString)
                                      armazon.show()
                                      order = armazon.order
                                  }
                                  flujoOtraOrden()
                              } catch ( Exception e) { println e }
                          } else {
                              ticketRx = true
                              if (armOrder?.udf2.equals('')) {
                                  ArmRxDialog armazon = new ArmRxDialog(this, order, armazonString)
                                  armazon.show()
                                  order = armazon.order
                              }
                              flujoOtraOrden()
                          }
                      }
                  } else {
                      sb.optionPane(message: "Codigo Dioptra Incorrecto", optionType: JOptionPane.DEFAULT_OPTION)
                              .createDialog(new JTextField(), "Error")
                              .show()
                  }
              } else {
                  ticketRx = false
                  flujoOtraOrden()
              }
            }
        } else {
              sb.optionPane(message: "Favor de capturar paquete.", optionType: JOptionPane.DEFAULT_OPTION)
                      .createDialog(new JTextField(), "Error")
                      .show()
        }
      } else {
          lstWarranty.clear()
          if( !canceledWarranty ){
              TXT_ERROR_WARRANTY = "No se puede registrar la venta"
              if( MSJ_ERROR_WARRANTY.length() <= 0 ){
                  MSJ_ERROR_WARRANTY = "Error al asignar el seguro, Verifiquelo e intente nuevamente."
              }
              JOptionPane.showMessageDialog( null, MSJ_ERROR_WARRANTY,
                      TXT_ERROR_WARRANTY, JOptionPane.ERROR_MESSAGE )
          }
      }
    } else {
          JOptionPane.showMessageDialog( null, "La venta ya ha sido anulada",
                  "Venta Anulada", JOptionPane.ERROR_MESSAGE )
      }
    }


    private void flujoOtraOrden(){
        //if (itemsModel.size() > 0) {
            if (paymentsModel.size() == 0) {
                if( !validLenses() ){
                    order.dioptra = null
                }
                for(IPromotionAvailable promo : promotionList){
                    if( promo instanceof PromotionDiscount ){
                        OrderController.updateCuponMvByClave(order.id, StringUtils.trimToEmpty(promo.discountType.description))
                    }
                }
                Customer c = this.customer
                Order newOrder = OrderController.saveOrder(order)
                CustomerController.updateCustomerInSite(c.id)
                this.promotionDriver.requestPromotionSave(newOrder?.id, false)
                this.reset()
                this.disableUI()
                this.operationTypeSelected = OperationType.PENDING
                this.setCustomer(c)
                this.enableUI()
            } else {
                sb.doLater {
                    OrderController.notifyAlert(TXT_REQUEST_NEW_ORDER, TXT_PAYMENTS_PRESENT)
                }
            }
        /*} else {
            sb.doLater {
                OrderController.notifyAlert(TXT_REQUEST_NEW_ORDER, TXT_PAYMENTS_PRESENT)
            }
        }*/
    }

    private Boolean isPaymentListEmpty() {
        return (order.payments.size() == 0)
    }



    private Boolean validLenses( ){
      Boolean hasLenses = OrderController.validLenses( order )
      return hasLenses
    }

    public void cleanAll( ){
      sb = null
      order = null
      dioptra = null
      customerTypes = null
      customer = null
      promotionList = null
      ticketRx = null
      lstCustomers = null
      discountMenu = null
      currentOperationType = null
      dioptra = null
      antDioptra = null
      promotionListTmp = null
    }


    private void cleanOrder(){
        order = new Order()
        dioptra = new Dioptra()
        advanceOnlyInventariable = false
        String clientesActivos = OrderController.obtieneTiposClientesActivos()
        for(OperationType customer : lstCustomers){
            if(clientesActivos.contains(customer.value)){
                customerTypes.add(customer)
            }
        }
        customer = CustomerController.findDefaultCustomer()
        promotionList = new ArrayList<PromotionAvailable>()
        promotionListTmp = new ArrayList<PromotionAvailable>()
        this.promotionDriver.init(this)
        ticketRx = false
        doBindings()
        itemsModel.addTableModelListener(this.promotionDriver)
        uiEnabled = true
        OperationType
    }



    private void validaLC(Item item, Boolean fillOblig ) {
      if(ItemController.esLenteContacto(item.id)){
        ModelLc modeloLc = ItemController.findLenteContacto( item.id )
        if( modeloLc != null ){
          LentesContactoDialog dialog = new LentesContactoDialog( order.id, modeloLc.model, item.id, modeloLc.curve,
              modeloLc.diameter, modeloLc.sphere, modeloLc.cylinder, modeloLc.axis, modeloLc.color, customer.id, fillOblig, null )
          dialog.show()
          if( dialog.cancelled ){
            OrderItem oi = null
            for(OrderItem det : order.items){
              if( det.item.id.equals(item.id) ){
                oi = det
              }
            }
            OrderController.removeOrderItemFromOrder( order.id, oi )
            OrderController.removePedidoLc( order.id, item.id )
          }
          updateOrder( order.id )
          //doBindings()
        } else if( ItemController.findLenteContactoStock( item.id ) ){
          BatchDialog dialog = new BatchDialog( order, item, fillOblig )
          dialog.show()
        }
      }
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


  private void validTransferCuponMv( ){
    Payment payment = null
    for(Payment pay : order.payments){
      if( StringUtils.trimToEmpty(pay.paymentTypeId).equalsIgnoreCase(TAG_PAYMENT_TYPE_TRANSF) ){
        NotaVenta notaVenta = OrderController.findOrderByidOrder( StringUtils.trimToEmpty(pay.paymentReference) )
        List<CuponMv> lstCupon = OrderController.obtenerCuponMvByTargetOrder( StringUtils.trimToEmpty(notaVenta.factura) )
        if( lstCupon.size() > 0 ){
          promotionDriver.addCouponDiscountTransf( order, lstCupon.first().montoCupon, lstCupon.first().claveDescuento, lstCupon.first().montoCupon )
        }
      }
    }
    //if( payment != null ){
    //}
  }


  private void refreshDioptra(){
    updateOrder( StringUtils.trimToEmpty(order.id) )
    for(OrderItem it : order.items){
      Item result = ItemController.findItemsById(it.item.id)
      if( result != null ){
        controlItem( result, true )
      }
    }
  }


  private void generatedCoupons( Boolean validClave, Order newOrder){
    if(validClave){
      Boolean hasGenericH = false
      for( OrderItem oi : newOrder.items ){
        if( StringUtils.trimToEmpty(oi.item.type).equalsIgnoreCase(TAG_GENERICO_LENTE_CONTACTO) ){
          hasGenericH = true
        }
      }
      if( hasGenericH ){
        Integer var = 1
        if( Registry.tirdthPairValid() ){
          var = 2
        }
        for(int i=0;i<var;i++){
          BigDecimal montoCupon = i == 0 ? OrderController.getCuponAmount(newOrder.id) : OrderController.getCuponAmountThirdPair( newOrder.id )
          if(montoCupon.compareTo(BigDecimal.ZERO) > 0){
            String titulo = i == 0 ? "CUPON SEGUNDO PAR LC" : "CUPON TERCER PAR LC"
            Integer numCupon = i == 0 ? 2 : 3
            CuponMv cuponMv = new CuponMv()
            cuponMv.facturaDestino = ""
            cuponMv.facturaOrigen = order.id
            cuponMv.fechaAplicacion = null
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, Registry.diasVigenciaCupon)
            cuponMv.fechaVigencia = calendar.getTime()
            cuponMv = OrderController.updateCuponMv( newOrder.id, "", montoCupon, numCupon, false )
            OrderController.printCuponTicket( cuponMv, titulo, montoCupon )
          }
        }
      } else {
        Integer var = 1
        if( Registry.tirdthPairValid() ){
          var = 2
        }
        for(int i=0;i<var;i++){
              BigDecimal montoCupon = i == 0 ? OrderController.getCuponAmount(newOrder.id) : OrderController.getCuponAmountThirdPair( newOrder.id )
              if(montoCupon.compareTo(BigDecimal.ZERO) > 0){
                  String titulo = i == 0 ? "CUPON SEGUNDO PAR" : "CUPON TERCER PAR"
                  Integer numCupon = i == 0 ? 2 : 3
                  CuponMv cuponMv = new CuponMv()
                  //cuponMv.claveDescuento = promotionDriver.claveAleatoria(StringUtils.trimToEmpty(newOrder.bill))
                  cuponMv.facturaDestino = ""
                  cuponMv.facturaOrigen = order.id
                  cuponMv.fechaAplicacion = null
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTime(new Date());
                  calendar.add(Calendar.DAY_OF_YEAR, Registry.diasVigenciaCupon)
                  cuponMv.fechaVigencia = calendar.getTime()
                  cuponMv = OrderController.updateCuponMv( newOrder.id, "", montoCupon, numCupon, false )
                  OrderController.printCuponTicket( cuponMv, titulo, montoCupon )
              }
        }
      }
    }
  }

  private Boolean validLensesPack( ){
    Boolean valid = true
    Boolean hasLenses = false
    Boolean hasCorrectPack = false
    updateOrder( order.id )
    for(OrderItem orderItem : order.items){
      if( StringUtils.trimToEmpty(orderItem.item.name).equalsIgnoreCase(TAG_ARTICULO_P)
              || StringUtils.trimToEmpty(orderItem.item.name).equalsIgnoreCase(TAG_ARTICULO_B) ){
        hasLenses = true
      }
    }
    if( hasLenses ){
      for(OrderItem orderItem : order.items){
        if( StringUtils.trimToEmpty(orderItem.item.type).equalsIgnoreCase(TAG_PAQUETE) ){
          hasCorrectPack = true
        }
      }
      if( !hasCorrectPack ){
        valid = false
      }
    }
    return valid
  }


  private Boolean appliedEnsure( Articulo articulo ){
    Boolean valid = false
    for(int i=0;i<promotionList.size();i++){
      if(promotionList.get(i) instanceof PromotionDiscount){
        if(StringUtils.trimToEmpty(promotionList.get(i).discountType.text).equalsIgnoreCase("Redencion de Seguro") &&
                StringUtils.trimToEmpty(articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
          valid = true
        }
      }
    }
    return valid
  }



  private String ipCurrentMachine( ){
    String line = ""
    String ip = ""
    try{
          line = System.getenv("SSH_CLIENT");
    } catch ( Exception e ) { println e }

    if( StringUtils.trimToEmpty(line).length() > 0 ){
          String[] data = StringUtils.trimToEmpty(line).split(" ")
          if( data.length > 1 ){
              ip = data[0]
              println "Ip Maquina: "+ip
         }
    }

    if(StringUtils.trimToEmpty(ip).length() <= 0){
          Enumeration en = NetworkInterface.getNetworkInterfaces();
          while(en.hasMoreElements()){
              NetworkInterface ni=(NetworkInterface) en.nextElement();
              Enumeration ee = ni.getInetAddresses();
              while(ee.hasMoreElements()) {
                  InetAddress ia= (InetAddress) ee.nextElement();
                  if(StringUtils.trimToEmpty(ia.canonicalHostName).contains(InetAddress.getLocalHost().getHostName())){
                      ip = ia.getHostAddress()
                      println("Ip Maquina: "+ip)
                  }
              }
          }
    }
    return ip
  }


}