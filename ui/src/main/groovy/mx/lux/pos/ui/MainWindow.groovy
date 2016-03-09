package mx.lux.pos.ui

import groovy.swing.SwingBuilder
import mx.lux.pos.service.PromotionService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.*
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.view.action.ExitAction
import mx.lux.pos.ui.view.dialog.AdjustSaleDialog
import mx.lux.pos.ui.view.dialog.AssignSubmanagerDialog
import mx.lux.pos.ui.view.dialog.AuthorizationDialog
import mx.lux.pos.ui.view.dialog.AuthorizationIpDialog
import mx.lux.pos.ui.view.dialog.ChangeIpBoxDialog
import mx.lux.pos.ui.view.dialog.ChangePasswordDialog
import mx.lux.pos.ui.view.dialog.CustomerSearchDialog
import mx.lux.pos.ui.view.dialog.EntregaTrabajoDialog
import mx.lux.pos.ui.view.dialog.FreedomCouponDialog
import mx.lux.pos.ui.view.dialog.ImportEmployeeDialog
import mx.lux.pos.ui.view.dialog.RecalculateDialog
import mx.lux.pos.ui.view.dialog.ReprintEnsureDialog
import mx.lux.pos.ui.view.panel.*
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import javax.swing.*
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.text.SimpleDateFormat

class MainWindow extends JFrame implements KeyListener {

    static final String MSG_LOAD_PARTS = "No se encuentra el archivo: %s"
    static final String TEXT_LOAD_PARTS_TITLE = "Importar Catálogo de Artículos"
    static final String TEXT_LOAD_PART_CLASS_TITLE = "Importar Clasificación de Artículos"
    static SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy")

    static String version

    static MainWindow instance
    private Logger log = LoggerFactory.getLogger( this.getClass() )
    private SwingBuilder sb
    private JPanel mainPanel
    private JPanel logInPanel
    private JPanel orderPanel
    private JPanel showOrderPanel
    private InvTrView invTrView
    private LcView lcView
    private CustomerSearchDialog customerDialog
    private JPanel dailyClosePanel
    private JPanel consultaPanel
    private JPanel priceListPanel
    private JPanel invoicePanel
    private JToolBar infoBar
    Boolean openSoi
    private JLabel userLabel
    private JLabel branchLabel
    private JLabel versionLabel
    private JMenu toolsMenu
    private JMenu ordersMenu
    private JMenu clientsMenu
    private JMenu lcMenu
    private JMenu inventoryMenu
    private JMenu reportsMenu
    private JMenu controlTrabajosMenu
    private JMenu supportMenu
    private JMenuItem orderMenuItem
    private JMenuItem orderSearchMenuItem
    private JMenuItem dailyCloseMenuItem
    private JMenuItem priceListMenuItem
    private JMenuItem invoiceMenuItem
    private JMenuItem sessionMenuItem
    private JMenuItem cancellationReportMenuItem
    private JMenuItem dailyCloseReportMenuItem
    private JMenuItem assignSubManagerMenuItem
    private JMenuItem incomePerBranchReportMenuItem
    private JMenuItem sellerRevenueReportMenuItem
    private JMenuItem undeliveredJobsReportMenuItem
    private JMenuItem undeliveredJobsAuditReportMenuItem
    private JMenuItem salesReportMenuItem
    private JMenuItem salesByLineReportMenuItem
    private JMenuItem salesByBrandReportMenuItem
    private JMenuItem salesBySellerReportMenuItem
    private JMenuItem cuponMvReportMenuItem
    private JMenuItem inventoryTransactionMenuItem
    private JMenuItem inventoryOhQueryMenuItem
    private JMenuItem inventoryLcOhQueryMenuItem
    private JMenuItem recalculateMenuItem
    private JMenuItem salesBySellerByBrandMenuItem
    private JMenuItem stockbyBrandMenuItem
    private JMenuItem stockbyBrandColorMenuItem
    private JMenuItem taxBillsMenuItem
    private JMenuItem discountsMenuItem
    private JMenuItem promotionsMenuItem
    private JMenuItem promotionsListMenuItem
    private JMenuItem paymentsMenuItem
    private JMenuItem quoteMenuItem
    private JMenuItem importEmployeeMenuItem
    private JMenuItem loadPartsMenuItem
    private JMenuItem loadPartClassMenuItem
    private JMenuItem generateInventoryFile
    private JMenuItem newSalesDayMenuItem
    private JMenuItem adjustSaleMenuItem
    private JMenuItem reprintEnsureMenuItem
    private JMenuItem ipBoxMenuItem
    private JMenuItem freedomCouponMenuItem
    private JMenuItem cotizacionMenuItem
    private JMenuItem kardexMenuItem
    private JMenuItem kardexBySkuMenuItem
    private JMenuItem salesTodayMenuItem
    private JMenuItem salesByPeriodMenuItem
    private JMenuItem entregaMenuItem
    private JMenuItem changePasswordMenuItem
    private JMenuItem nationalClientMenuItem
    private JMenuItem contactLensesMenuItem
    private JMenuItem jobControlMenuItem
    private JMenuItem workSubmittedMenuItem
    private JMenuItem optometristSalesMenuItem
    //private JMenuItem examsMenuItem
    private JMenuItem examsByOptoMenuItem
    private JMenuItem couponMenuItem
    private JMenuItem multipaymentMenuItem
    private JMenuItem disactivateSPItem
    private JMenuItem cellarReportMenuItem
    private JMenuItem consultaMenuItem
    private PromotionService promotionService


    MainWindow( ) {
        instance = this
        this.addKeyListener( this )
        openSoi = true
        sb = new SwingBuilder()
        buildUI()
    }

    private void buildUI( ) {
        logInPanel = new LogInPanel( doForwardToDefaultPanel, version )
        sb.build {
            lookAndFeel( 'system' )
            frame( this,
                    title: 'Punto de Venta',
                    focusable: true,
                    layout: new MigLayout( 'fill,insets 1,center,wrap', '[fill]', '[top]' ),
                    minimumSize: [ 850, 620 ] as Dimension,
                    location: [ 70, 35 ] as Point,
                    pack: true,
                    resizable: false,
                    defaultCloseOperation: EXIT_ON_CLOSE
            ) {
                menuBar {
                    ordersMenu = menu( text: 'Ventas', mnemonic: 'V',
                            menuSelected: {
                                boolean userLoggedIn = Session.contains( SessionItem.USER )
                                Boolean isManager = false
                                if( userLoggedIn ){
                                  User user = Session.get(SessionItem.USER) as User
                                  isManager = IOController.getInstance().isManager(user.username)
                                }
                                orderMenuItem.visible = userLoggedIn
                                orderSearchMenuItem.visible = userLoggedIn
                                dailyCloseMenuItem.visible = userLoggedIn
                                priceListMenuItem.visible = isManager
                                invoiceMenuItem.visible = userLoggedIn
                                nationalClientMenuItem.visible = userLoggedIn
                                // TODO: Benja enable feature cotizacionMenuItem.visible = userLoggedIn
                            }
                    ) {
                        orderMenuItem = menuItem( text: 'Venta',
                                visible: false,
                                actionPerformed: {
                                    mainPanel.remove( orderPanel )
                                    orderPanel = null
                                    orderPanel = new OrderPanel()
                                    clean( orderPanel )
                                    mainPanel.add( 'orderPanel', orderPanel )
                                    mainPanel.layout.show( mainPanel, 'orderPanel' )
                                }
                        )
                        orderSearchMenuItem = menuItem( text: 'Consulta',
                                visible: false,
                                actionPerformed: {
                                    clean( showOrderPanel )
                                    mainPanel.remove( showOrderPanel )
                                    showOrderPanel = new ShowOrderPanel()
                                    mainPanel.add( 'showOrderPanel', showOrderPanel )
                                    mainPanel.layout.show( mainPanel, 'showOrderPanel' )
                                }
                        )
                        /*cotizacionMenuItem = menuItem( text: 'Cotizaciones',
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    QuoteController.instance.requestQuote() }
                        )*/
                        dailyCloseMenuItem = menuItem( text: 'Cierre diario',
                                visible: false,
                                actionPerformed: {
                                    clean( dailyClosePanel )
                                    dailyClosePanel = new DailyClosePanel()
                                    mainPanel.add( 'dailyClosePanel', dailyClosePanel )
                                    mainPanel.layout.show( mainPanel, 'dailyClosePanel' )
                                }
                        )
                        priceListMenuItem = menuItem( text: 'Lista de Precios',
                                visible: false,
                                actionPerformed: {
                                    clean( priceListPanel )
                                    priceListPanel = new PriceListPanel().panel
                                    mainPanel.add( 'priceListPanel', priceListPanel )
                                    mainPanel.layout.show( mainPanel, 'priceListPanel' )
                                }
                        )
                        invoiceMenuItem = menuItem( text: 'Facturaci\u00f3n',
                                visible: false,
                                actionPerformed: {
                                    clean( invoicePanel )
                                    invoicePanel = new InvoicePanel()
                                    mainPanel.add( 'invoicePanel', invoicePanel )
                                    mainPanel.layout.show( mainPanel, 'invoicePanel' )
                                }
                        )
                    }
                    clientsMenu = menu( text: 'Clientes', mnemonic: 'C',
                            menuSelected: {
                                Runtime garbage = Runtime.getRuntime();
                                garbage.gc();
                                boolean userLoggedIn = Session.contains( SessionItem.USER )
                                nationalClientMenuItem.visible = userLoggedIn
                            }
                    ){
                      nationalClientMenuItem = menuItem( text: "Cliente Nacional", visible: true,
                              actionPerformed: {
                                  Runtime garbage = Runtime.getRuntime();
                                  garbage.gc();
                                  if ( customerDialog == null ) {
                                  customerDialog = new CustomerSearchDialog( this, new Order() )
                              }
                                  customerDialog.show()
                                  if (!customerDialog.canceled) {
                                    if( orderPanel.order.id == null ){
                                      mainPanel.remove( orderPanel )
                                      orderPanel = new OrderPanel()
                                      mainPanel.add( 'orderPanel', orderPanel )
                                      mainPanel.layout.show( mainPanel, 'orderPanel' )
                                    }
                                    orderPanel.setCustomerInOrderFromMenu( customerDialog.customer )
                                    customerDialog = new CustomerSearchDialog( this, new Order() )
                                  }
                                  customerDialog = new CustomerSearchDialog( this, new Order() )
                              }
                      )
                    }
                    inventoryMenu = menu( text: 'Inventario', mnemonic: 'I',
                            menuSelected: {
                                boolean userLoggedIn = Session.contains( SessionItem.USER )
                                inventoryTransactionMenuItem.visible = userLoggedIn
                                inventoryOhQueryMenuItem.visible = userLoggedIn
                                inventoryLcOhQueryMenuItem.visible = userLoggedIn
                                User u = Session.get(SessionItem.USER) as User
                                recalculateMenuItem.visible = userLoggedIn ? AccessController.validPassAudit(StringUtils.trimToEmpty(u.username), StringUtils.trimToEmpty(u.password)) : false
                                adjustSaleMenuItem.visible = userLoggedIn ? AccessController.validPassAudit(StringUtils.trimToEmpty(u.username), StringUtils.trimToEmpty(u.password)) : false
                                //generateInventoryFile.visible = userLoggedIn
                                //loadPartsMenuItem.visible = userLoggedIn
                                //loadPartClassMenuItem.visible = userLoggedIn
                            }
                    ) {
                        inventoryTransactionMenuItem = menuItem( text: 'Transacciones',
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    /*if ( invTrView == null ) {
                                        invTrView = new InvTrView()
                                    }*/
                                    if ( invTrView == null ) {
                                        invTrView = new InvTrView()
                                    } else {
                                      invTrView = null
                                      invTrView = new InvTrView()
                                    }
                                    mainPanel.add( 'invTrPanel', invTrView.panel )
                                    invTrView.activate()
                                    mainPanel.layout.show( mainPanel, 'invTrPanel' )
                                }
                        )
                        inventoryOhQueryMenuItem = menuItem( text: "Ticket Existencias", visible: true,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    InvQryController.instance.requestInvOhTicket() }
                        )
                        inventoryLcOhQueryMenuItem = menuItem( text: "Ticket Existencias LC", visible: true,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    InvQryController.instance.requestInvLcTicket() }
                        )
                        recalculateMenuItem = menuItem( text: 'Recalcular',
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    RecalculateDialog dialog = new RecalculateDialog()
                                    dialog.show()
                                }
                        )
                        adjustSaleMenuItem = menuItem( text: 'Reclasificar Venta',
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    AdjustSaleDialog dialog = new AdjustSaleDialog()
                                    dialog.show()
                                }
                        )
                        /*loadPartsMenuItem = menuItem( text: TEXT_LOAD_PARTS_TITLE,
                                visible: true,
                                actionPerformed: {
                                    requestImportPartMaster()
                                }
                        )
                        loadPartClassMenuItem = menuItem( text: TEXT_LOAD_PART_CLASS_TITLE,
                                visible: true,
                                actionPerformed: {
                                    requestImportPartClass()
                                }
                        )
                        generateInventoryFile = menuItem( text: 'Archivo Inventario',
                                visible: false,
                                actionPerformed: {
                                    generateInventoryFile()
                                }
                        )*/
                    }
                    lcMenu = menu( text: 'LC', mnemonic: 'L',
                            menuSelected: {
                                Runtime garbage = Runtime.getRuntime();
                                garbage.gc();
                                boolean userLoggedIn = Session.contains( SessionItem.USER )
                                contactLensesMenuItem.visible = userLoggedIn
                            }
                    ){
                        contactLensesMenuItem = menuItem( text: "Lentes de Contacto", visible: true,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    if ( lcView == null ) {
                                        lcView = new LcView()
                                    } else {
                                        clean(lcView.panel)
                                        lcView = new LcView()
                                    }
                                    mainPanel.add( 'lcPanel', lcView.panel )
                                    lcView.activate()
                                    mainPanel.layout.show( mainPanel, 'lcPanel' )
                                }
                        )
                    }
                    reportsMenu = menu( text: "Reportes", mnemonic: "R",
                            menuSelected: {
                                boolean userLoggedIn = Session.contains( SessionItem.USER )
                                Boolean isManager = false
                                if( userLoggedIn ){
                                  User user = Session.get(SessionItem.USER) as User
                                  isManager = IOController.getInstance().isManager(user.username)
                                }
                                cancellationReportMenuItem.visible = isManager
                                dailyCloseReportMenuItem.visible = userLoggedIn
                                //incomePerBranchReportMenuItem.visible = userLoggedIn
                                //sellerRevenueReportMenuItem.visible = userLoggedIn
                                undeliveredJobsReportMenuItem.visible = userLoggedIn
                                undeliveredJobsAuditReportMenuItem.visible = isManager
                                salesReportMenuItem.visible = userLoggedIn
                                //salesByLineReportMenuItem.visible = userLoggedIn
                                salesBySellerReportMenuItem.visible = userLoggedIn
                                //salesByBrandReportMenuItem.visible = userLoggedIn
                                //salesBySellerByBrandMenuItem.visible = userLoggedIn
                                stockbyBrandMenuItem.visible = isManager
                                stockbyBrandColorMenuItem.visible = isManager
                                jobControlMenuItem.visible = isManager
                                workSubmittedMenuItem.visible = isManager
                                taxBillsMenuItem.visible = isManager
                                discountsMenuItem.visible = userLoggedIn
                                //promotionsMenuItem.visible = userLoggedIn
                                //promotionsListMenuItem.visible = userLoggedIn
                                paymentsMenuItem.visible = isManager
                                quoteMenuItem.visible = userLoggedIn
                                kardexMenuItem.visible = isManager
                                kardexBySkuMenuItem.visible = isManager
                                //salesTodayMenuItem.visible = userLoggedIn
                                //salesByPeriodMenuItem.visible = userLoggedIn
                                undeliveredJobsReportMenuItem.visible = isManager
                                discountsMenuItem.visible = isManager
                                optometristSalesMenuItem.visible = userLoggedIn
                                //examsMenuItem.visible = userLoggedIn
                                examsByOptoMenuItem.visible = isManager
                                couponMenuItem.visible = isManager
                                cellarReportMenuItem.visible = isManager
                                cuponMvReportMenuItem.visible = userLoggedIn
                                multipaymentMenuItem.visible = false
                            }
                    ) {
                        salesReportMenuItem = menuItem( text: "Ventas",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Sales )
                                }
                        )
                        optometristSalesMenuItem = menuItem( text: "Ventas por Optometrista",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.OptometristSales )
                                }
                        )
                        salesBySellerReportMenuItem = menuItem( text: "Ventas por Vendedor",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.SalesbySeller )
                                }
                        )
                        cancellationReportMenuItem = menuItem( text: "Cancelaciones",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Cancellations )
                                }
                        )
                        dailyCloseReportMenuItem = menuItem( text: "Cierre Diario",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.DailyClose )
                                }
                        )
                        jobControlMenuItem = menuItem( text: "Control de Trabajos",
                            visible: false,
                            actionPerformed: {
                                Runtime garbage = Runtime.getRuntime();
                                garbage.gc();
                              ReportController.fireReport( ReportController.Report.JobControl )
                            }
                        )
                        quoteMenuItem = menuItem( text: "Cotizaciones",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Quote )
                                }
                        )
                        cuponMvReportMenuItem = menuItem( text: "Cupones(Descuentos)",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.CouponMv )
                                }
                        )
                        couponMenuItem = menuItem( text: "Cupones(Forma de Pago)",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Coupon )
                                }
                        )
                        discountsMenuItem = menuItem( text: "Descuentos",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Discounts )
                                }
                        )
                        /*examsMenuItem = menuItem( text: "Examenes",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Exams )
                                }
                        )*/
                        examsByOptoMenuItem = menuItem( text: "Examenes por Optometrista",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.ExamsByOpto )
                                }
                        )
                        stockbyBrandColorMenuItem = menuItem( text: "Existencias por Art\u00edculo",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.StockbyBrandColor )
                                }
                        )
                        stockbyBrandMenuItem = menuItem( text: "Existencias por Marca",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.StockbyBrand )
                                }
                        )
                        taxBillsMenuItem = menuItem( text: "Facturas Fiscales",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.TaxBills )
                                }
                        )
                        /*salesByPeriodMenuItem = menuItem( text: "Ingresos por Periodo",
                                visible: false,
                                actionPerformed: {
                                    ReportController.fireReport( ReportController.Report.PaymentsbyPeriod )
                                }
                        )
                        incomePerBranchReportMenuItem = menuItem( text: "Ingresos por Sucursal",
                                visible: false,
                                actionPerformed: {
                                    ReportController.fireReport( ReportController.Report.IncomePerBranch )
                                }
                        )
                        sellerRevenueReportMenuItem = menuItem( text: "Ingresos por Vendedor",
                                visible: false,
                                actionPerformed: {
                                    ReportController.fireReport( ReportController.Report.SellerRevenue )
                                }
                        )*/
                        kardexMenuItem = menuItem( text: "Kardex por Articulo",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Kardex )
                                }
                        )
                        kardexBySkuMenuItem = menuItem( text: "Kardex por Sku",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.KardexBySku )
                                }
                        )
                        multipaymentMenuItem = menuItem(text: "Multipago",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Multipayment )
                                }
                        )
                        paymentsMenuItem = menuItem( text: "Pagos",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Payments )
                                }
                        )
                        /*promotionsMenuItem = menuItem( text: "Promociones en Ventas",
                                visible: false,
                                actionPerformed: {
                                    ReportController.fireReport( ReportController.Report.PromotionsinSales )
                                }
                        )
                        promotionsListMenuItem = menuItem( text: "Promociones",
                                visible: false,
                                actionPerformed: {
                                    ReportController.fireReport( ReportController.Report.Promotions )
                                }
                        )*/
                        cellarReportMenuItem = menuItem( text: "Trabajos de Bodega",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.Cellar )
                                }
                        )
                        workSubmittedMenuItem = menuItem( text: "Trabajos Entregados",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.WorkSubmitted )
                                }
                        )
                        undeliveredJobsReportMenuItem = menuItem( text: "Trabajos sin Entregar",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.UndeliveredJobs )
                                }
                        )
                        undeliveredJobsAuditReportMenuItem = menuItem( text: "Trabajos sin Entregar Auditoria",
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ReportController.fireReport( ReportController.Report.UndeliveredJobsAudit )
                                }
                        )
                        /*salesTodayMenuItem = menuItem( text: "Ventas del D\u00eda",
                                visible: false,
                                actionPerformed: {
                                    ReportController.fireReport( ReportController.Report.SalesToday )
                                }
                        )
                        salesByLineReportMenuItem = menuItem( text: "Ventas por L\u00ednea",
                                visible: false,
                                actionPerformed: {
                                    ReportController.fireReport( ReportController.Report.SalesbyLine )
                                }
                        )
                        salesByBrandReportMenuItem = menuItem( text: "Ventas por Marca",
                                visible: false,
                                actionPerformed: {
                                    ReportController.fireReport( ReportController.Report.SalesbyBrand )
                                }
                        )*/
                        /*salesBySellerByBrandMenuItem = menuItem( text: "Ventas por Vendedor por Marca",
                                visible: false,
                                actionPerformed: {
                                    ReportController.fireReport( ReportController.Report.SalesbySellerbyBrand )
                                }
                        )*/
                    }
                    /*controlTrabajosMenu = menu( text: 'Control de Trabajos', mnemonic: 'C',
                            menuSelected: {
                                boolean userLoggedIn = Session.contains( SessionItem.USER )
                                consultaMenuItem.visible = userLoggedIn
                            }
                    ) {
                        consultaMenuItem = menuItem( text: 'Consulta Trabajos',
                                visible: true,
                                actionPerformed: {
                                    actionPerformed: {
                                        clean( consultaPanel )
                                        if( consultaPanel == null ){
                                            consultaPanel = new ConsultaPanel();
                                            mainPanel.add( 'consultaPanel', consultaPanel )
                                        }
                                        consultaPanel.limpiaPantalla()
                                        mainPanel.layout.show( mainPanel, 'consultaPanel' )
                                    }
                                }
                        )
                    }*/
                    toolsMenu = menu( text: 'Herramientas', mnemonic: 'H',
                            menuSelected: {
                                boolean userLoggedIn = Session.contains( SessionItem.USER )
                                Boolean isManager = false
                                if(userLoggedIn){
                                  User user = Session.get( SessionItem.USER ) as User
                                  isManager = IOController.getInstance().isManager(user.username)//StringUtils.trimToEmpty(Registry.idManager)
                                }
                                sessionMenuItem.visible = userLoggedIn
                                newSalesDayMenuItem.visible = isManager
                                entregaMenuItem.visible = isManager
                                changePasswordMenuItem.visible = isManager
                                disactivateSPItem.visible = userLoggedIn
                                importEmployeeMenuItem.visible = isManager
                                reprintEnsureMenuItem.visible = isManager
                                ipBoxMenuItem.visible = userLoggedIn
                                freedomCouponMenuItem.visible = userLoggedIn
                                supportMenu.visible = isManager
                                assignSubManagerMenuItem.visible = isManager
                            }
                    ) {
                        assignSubManagerMenuItem = menuItem( text: 'Asigna Subgerente',
                                visible: true,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    assignSubManager()
                                }
                        )
                        entregaMenuItem = menuItem(text: 'Entrega',
                                visible: true,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    entrega()
                                }
                        )
                        changePasswordMenuItem = menuItem( text: 'Cambio de Password',
                                visible: true,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    ChangePasswordDialog dialog = new ChangePasswordDialog()
                                    dialog.show()
                                }
                        )
                        importEmployeeMenuItem = menuItem( text: 'Importa Empleado',
                                visible: true,
                                actionPerformed: {
                                  Runtime garbage = Runtime.getRuntime();
                                  garbage.gc();
                                  AuthorizationDialog authDialog = new AuthorizationDialog(this, "Esta operacion requiere autorizaci\u00f3n")
                                  authDialog.show()
                                  if (authDialog.authorized) {
                                    ImportEmployeeDialog dialog = new ImportEmployeeDialog()
                                    dialog.show()
                                  } else {
                                    OrderController.notifyAlert('Se requiere autorizacion para esta operacion', 'Se requiere autorizacion para esta operacion')
                                  }
                                }
                        )
                        reprintEnsureMenuItem = menuItem( text: 'Reimprimir Seguro',
                                visible: true,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    AuthorizationDialog authDialog = new AuthorizationDialog(this, "Esta operacion requiere autorizaci\u00f3n")
                                    authDialog.show()
                                    if (authDialog.authorized) {
                                      reprintEnsure()
                                    } else {
                                      OrderController.notifyAlert('Se requiere autorizacion para esta operacion', 'Se requiere autorizacion para esta operacion')
                                    }
                                }
                        )
                        newSalesDayMenuItem = menuItem( text: 'Registrar Efectivo Caja',
                                visible: true,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    requestNewSalesDay()
                                }
                        )
                        supportMenu = menu(
                                visible: false,
                                text: 'Soporte'
                        ){
                          disactivateSPItem = menuItem( text: 'Activa/Desactiva Surte Pino',
                                    visible: true,
                                    actionPerformed: {
                                        Runtime garbage = Runtime.getRuntime();
                                        garbage.gc();
                                        disactivateSP()
                                    }
                          )
                          ipBoxMenuItem = menuItem( text: 'Configurar Caja',
                                    visible: true,
                                    actionPerformed: {
                                        Runtime garbage = Runtime.getRuntime();
                                        garbage.gc();
                                        /*AuthorizationIpDialog authDialog = new AuthorizationIpDialog(this, "Esta operacion requiere autorizaci\u00f3n")
                                        authDialog.show()
                                        if (authDialog.authorized) {*/
                                            ChangeIpBoxDialog dialog = new ChangeIpBoxDialog()
                                            dialog.show()
                                        /*} else {
                                            OrderController.notifyAlert('Se requiere autorizacion para esta operacion', 'Se requiere autorizacion para esta operacion')
                                        }*/
                                    }
                          )
                          freedomCouponMenuItem = menuItem( text: 'Liberar Facturas',
                                  visible: true,
                                  actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    /*AuthorizationIpDialog authDialog = new AuthorizationIpDialog(this, "Esta operacion requiere autorizaci\u00f3n")
                                    authDialog.show()
                                    if (authDialog.authorized) {*/
                                      FreedomCouponDialog dialog = new FreedomCouponDialog( this )
                                      dialog.show()
                                    /*} else {
                                      OrderController.notifyAlert('Se requiere autorizacion para esta operacion', 'Se requiere autorizacion para esta operacion')
                                    }*/
                                  }
                          )
                        }
                        sessionMenuItem = menuItem( text: 'Cerrar Sesi\u00f3n',
                                visible: false,
                                actionPerformed: {
                                    Runtime garbage = Runtime.getRuntime();
                                    garbage.gc();
                                    requestLogout()
                                }
                        )
                        menuItem( menuItem( text: 'Salir',
                                visible: true,
                                actionPerformed: { requestExit() } )
                        )
                    }
                }
                infoBar = toolBar(
                        visible: false,
                        floatable: false,
                        background: Color.WHITE,
                        constraints: 'hidemode 3'
                ) {
                    borderLayout()
                    userLabel = label( constraints: BorderLayout.LINE_START )
                    branchLabel = label( constraints: BorderLayout.CENTER, horizontalAlignment: JLabel.CENTER_ALIGNMENT )
                    versionLabel = label( constraints: BorderLayout.LINE_END )
                }

                mainPanel = panel( layout: new CardLayout() )
                mainPanel.add( 'logInPanel', logInPanel )

            }
        }
    }

    public void keyReleased( KeyEvent e ) {
    }

    public void keyTyped( KeyEvent e ) {
    }

    public void keyPressed( KeyEvent e ) {
        if ( e.isControlDown() && e.getKeyChar() != 'k' && e.getKeyCode() == 75 ) {
            // TODO: Benjamin enable through feature
            // log.debug( "Abriendo dialogo de cotizaciones" )
            // QuoteController.instance.requestQuote()
        }
    }

    private def doForwardToDefaultPanel = {
        User user = Session.get( SessionItem.USER ) as User
        Branch branch = Session.get( SessionItem.BRANCH ) as Branch
        orderPanel = new OrderPanel()
        mainPanel.add( 'orderPanel', orderPanel )
        mainPanel.layout.show( mainPanel, 'orderPanel' )

        userLabel.text = "[${user?.username ?: ''}] ${user?.fullName ?: ''}"
        branchLabel.text = "[${branch?.id ?: ''}] ${branch?.name ?: ''}"
        versionLabel.text = version
        infoBar.visible = true

    }

    private void initialize( ) {
        String fechaParametro = Registry.fechaPrimerArranque
        String fechaActual = fecha.format(new Date())
        if(fechaParametro != ''){
          if(!fechaActual.trim().equalsIgnoreCase(fechaParametro)){
            IOController.getInstance().deletCustomerProcess()
            IOController.getInstance().updateInitialDate(fechaActual)
            DailyCloseController.validPendingClosedDays( )
          }
        } else {
            IOController.getInstance().updateInitialDate(fechaActual)
        }
        sb.doOutside {
            PriceListController.loadExpiredPriceList()
            IOController.getInstance().cargaFoliosPendientesPedidosLc()
            IOController.getInstance().loadAcusePedidoLc()
            IOController.getInstance().autoUpdateFxRates()
            DailyCloseController.openDay()
            IOController.getInstance().loadMessageTicketFile()
            DailyCloseController.RegistrarPromociones()
            DailyCloseController.RegistrarClavesDescuento()
            IOController.getInstance().autoUpdateEmployeeFile()
            InvTrController controllerInv = InvTrController.instance
            controllerInv.readAdjutFile()
            controllerInv.readAutIssueFile()
            //IOController.getInstance().startAsyncNotifyDispatcher()
        }
    }

    void requestImportPartMaster( ) {
        IOController.getInstance().requestImportPartMaster()
    }

    void requestImportPartClass( ) {
        IOController.getInstance().requestImportClasificationArtMaster()
    }

    void generateInventoryFile( ){
        ItemController.generateInventoryFile()
    }

    JPanel getMainPanel( ) {
        return mainPanel
    }

    void entrega(){
        EntregaTrabajoDialog trabajo = new EntregaTrabajoDialog(this)
        trabajo.show()
    }

    void requestNewSalesDay( ) {
        OpenSalesController.instance.requestNewDay()
    }

    void reprintEnsure( ) {
      ReprintEnsureDialog dialog = new ReprintEnsureDialog()
      dialog.show()
    }

    void assignSubManager( ) {
      AssignSubmanagerDialog dialog = new AssignSubmanagerDialog(this, "")
      dialog.show()
    }

    void disactivateSP( ) {
        OpenSalesController.instance.disactivateSP()
    }

    void requestLogout( ) {
        if(orderPanel != null && orderPanel?.order?.id != null){
            OrderController.deleteCuponMv( StringUtils.trimToEmpty(orderPanel.order.id) )
            orderPanel.promotionDriver.requestPromotionSave(orderPanel.order?.id, false)
        }
        openSoi = false
        AccessController.logOut()
        infoBar.visible = false
        mainPanel.remove( orderPanel )
        mainPanel.remove( showOrderPanel )
        mainPanel.remove( dailyClosePanel )
        mainPanel.remove( priceListPanel )
        mainPanel.remove( invoicePanel )
        orderPanel = null
        showOrderPanel = null
        dailyClosePanel = null
        priceListPanel = null
        invoicePanel = null
        logInPanel = new LogInPanel( doForwardToDefaultPanel, version )
        mainPanel.layout.show( mainPanel, 'logInPanel' )
        Runtime garbage = Runtime.getRuntime();
        garbage.gc();
    }


    void requestExit(){
      if(orderPanel != null && orderPanel?.order?.id != null){
        OrderController.deleteCuponMv( StringUtils.trimToEmpty(orderPanel.order.id) )
        orderPanel.promotionDriver.requestPromotionSave(orderPanel.order?.id, false)
      }
      //new ExitAction().action
      System.exit( 0 )
    }


    void clean( JPanel panelSelected ){
      if( !panelSelected.equals(orderPanel) ){
          if( orderPanel != null ){
            orderPanel.cleanAll( )
            mainPanel.remove( orderPanel )
            orderPanel.finalize()
            orderPanel = null
          }
      }
      if( !panelSelected.equals(showOrderPanel) ){
          if( showOrderPanel != null ){
            showOrderPanel.cleanAll( )
            mainPanel.remove( showOrderPanel )
            orderPanel.finalize()
            showOrderPanel = null
          }
      }
      if( !panelSelected.equals(dailyClosePanel) ){
          mainPanel.remove( dailyClosePanel )
          orderPanel.finalize()
          dailyClosePanel = null
      }
      if( !panelSelected.equals(priceListPanel) ){
          mainPanel.remove( priceListPanel )
          orderPanel.finalize()
          priceListPanel = null
      }
      if( !panelSelected.equals(invoicePanel) ){
          mainPanel.remove( invoicePanel )
          orderPanel.finalize()
          invoicePanel = null
      }
      if( lcView != null && !panelSelected.equals(lcView.panel) ){
        mainPanel.remove( lcView.panel )
        orderPanel.finalize()
        lcView.panel = null
      }
      if( invTrView != null && !panelSelected.equals(invTrView.panel) ){
        mainPanel.remove( invTrView.panel )
        orderPanel.finalize()
        invTrView.panel = null
      }
      Runtime garbage = Runtime.getRuntime();
      garbage.gc();
    }

    static void main( args ) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext( "classpath:spring-config.xml" )
        ctx.registerShutdownHook()
        SwingUtilities.invokeLater(
                new Runnable() {
                    void run( ) {
                        version = Session.getVersion()
                        MainWindow window = new MainWindow()
                        window.initialize()
                        window.show()
                    }
                }
        )
    }
}
