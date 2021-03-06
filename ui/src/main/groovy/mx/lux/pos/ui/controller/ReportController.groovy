package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.model.Articulo
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.ReportService
import mx.lux.pos.ui.view.dialog.*
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.text.NumberFormat

@Slf4j
@Component
class ReportController {

  private static ReportService reportService
  private static DateSelectionDialog dateDialog
  private static TwoDatesSelectionDialog twoDateDialog
  private static CuponMvReportSelectionDialog cuponMvReportSelectionDialog
  private static TwoDatesSelectionAndKeyDialog twoDateKeyDialog
  private static TwoDatesSelectionFilterDialog twoDateFilterDialog
  private static TwoDatesSelectionFilterLineDialog twoDatesSelectionFilterLineDialog
  private static TwoDatesSelectionFilterBrandDialog twoDatesSelectionFilterBrandDialog
  private static TwoDatesSelectionFilterBrandArticleDialog twoDatesSelectionFilterBrandArticleDialog
  private static ArticleSelectionFilterDialog articleSelectionFilterDialog
  private static ArticleAndColorSelectionFilterDialog articleAndColorSelectionFilterDialog
  private static FilterDialog filterDialog
  private static TwoDatesSelectionFilterbyEmployeeDialog twoDatesSelectionFilterbyEmployeeDialog
  private static TwoDatesSelectionFilterPaymentsDialog twoDatesSelectionFilterPaymentsDialog
  private static TwoDatesSelectionRadioFilterDialog twoDatesSelectionRadioFilterDialog
  private static KardexReportDialog kardexReportDialog
  private static DateSelectionCheckBoxDialog salesForDayDialog
  private static TwoDatesSelectionFilterbyClientDialog twoDatesSelectionFilterbyClientDialog

  static enum Report {
    DailyClose, IncomePerBranch, SellerRevenue, Sales,
    SalesbySeller, UndeliveredJobs, Cancellations,
    SalesbyLine, SalesbyBrand, SalesbySellerbyBrand,
    StockbyBrand, StockbyBrandColor, JobControl,
    WorkSubmitted, TaxBills, Discounts, PromotionsinSales,
    Payments, Quote, Exams, OptometristSales,
    Promotions, Kardex, SalesToday, PaymentsbyPeriod,
    Coupon, UndeliveredJobsAudit, ExamsByOpto,
    Cellar, CouponMv, Multipayment, KardexBySku,
    Submanager, Check
  }

  @Autowired
  ReportController( ReportService reportService ) {
    this.reportService = reportService
  }

  // Internal Methods
  static void fireDailyCloseReport( ) {
    if ( dateDialog == null ) {
      dateDialog = new DateSelectionDialog()
    }
    dateDialog.setTitle( "Reporte de Cierre Diario" )
    dateDialog.activate()
    Date reportForDate = dateDialog.getSelectedDate()
    if ( reportForDate != null && dateDialog.button ) {
      log.debug( "Imprime el reporte de Cierre Diario" )
      reportService.obtenerReporteCierreDiario( reportForDate )
      dateDialog = null
    } else {
      log.debug( "Cancelar y continuar" )
    }
  }

  static void fireIncomePerBranchReport( ) {
    if ( twoDateDialog == null ) {
      twoDateDialog = new TwoDatesSelectionDialog()
    }
    twoDateDialog.setTitle( "Reporte de Ingresos por Sucursal" )
    twoDateDialog.activate()
    Date reportForDateStart = twoDateDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateDialog.getSelectedDateEnd()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateDialog.button ) {
      log.debug( "Imprime el reporte de Ingresos por Sucursal" )
      reportService.obtenerReporteIngresosXSucursal( reportForDateStart, reportForDateEnd )
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireSellerRevenueReport( ) {
    if ( twoDateFilterDialog == null ) {
      twoDateFilterDialog = new TwoDatesSelectionFilterDialog()
    }
    twoDateFilterDialog.setTitle( "Reporte de Ingresos por Vendedor" )
    twoDateFilterDialog.activate()
    Date reportForDateStart = twoDateFilterDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateFilterDialog.getSelectedDateEnd()
    boolean resumen = twoDateFilterDialog.getCbResume()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateFilterDialog.button ) {
      if ( resumen == true ) {
        log.debug( "Imprime el reporte de Ingresos por Vendedor Resumido" )
        reportService.obtenerReporteIngresosXVendedorResumido( reportForDateStart, reportForDateEnd )
      }
      if ( resumen == false ) {
        reportService.obtenerReporteIngresosXVendedorCompleto( reportForDateStart, reportForDateEnd )
        log.debug( "Imprime el reporte de Ingresos por Vendedor Completo" )
      }
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireSalesReport( ) {
    if ( twoDateDialog == null ) {
      twoDateDialog = new TwoDatesSelectionDialog()
    }
    twoDateDialog.setTitle( "Reporte de Ventas" )
    twoDateDialog.activate()
    Date reportForDateStart = twoDateDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateDialog.getSelectedDateEnd()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateDialog.button ) {
      log.debug( "Imprime el reporte de Ventas Completo" )
      //reportService.obtenerReporteVentasCompleto( reportForDateStart, reportForDateEnd )
        reportService.obtenerReporteVentasMasVision( reportForDateStart, reportForDateEnd )
        twoDateDialog = null
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireSalesbySellerReport( ) {
    if ( twoDateFilterDialog == null ) {
      twoDateFilterDialog = new TwoDatesSelectionFilterDialog()
    }
    twoDateFilterDialog.setTitle( "Reporte de Ventas por Vendedor" )
    twoDateFilterDialog.activate()
    Date reportForDateStart = twoDateFilterDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateFilterDialog.getSelectedDateEnd()
    boolean resumen = twoDateFilterDialog.getCbResume()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateFilterDialog.button ) {
      if ( resumen == false ) {
        reportService.obtenerReporteVentasporVendedorCompleto( reportForDateStart, reportForDateEnd )
        log.debug( "Imprime el reporte de Ventas por Vendedor Completo" )
        twoDateFilterDialog = null
      }
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireUndeliveredJobsReport( ) {
    log.debug( "Imprime el reporte de Trabajos sin Entregar" )
    reportService.obtenerReporteTrabajosSinEntregar()
  }

  static void fireCellarReport( ) {
    if ( dateDialog == null ) {
      dateDialog = new DateSelectionDialog()
    }
    dateDialog.setTitle( "Reporte de Bodegas" )
    Calendar cal=Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH,Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
    cal.add(Calendar.MONTH, 1)
    dateDialog.setDate( cal.getTime() );
    dateDialog.activate()
    Date reportForDate = dateDialog.getSelectedDate()
    if ( reportForDate != null && dateDialog.button ) {
    log.debug( "Imprime el reporte de Bodegas" )
    reportService.obtenerReporteBodegas( reportForDate )
    } else {
        log.debug( "Cancelar_continuar" )
    }
  }

  static void fireUndeliveredJobsAuditReport( ) {
    log.debug( "Imprime el reporte de Trabajos sin Entregar" )
    reportService.obtenerReporteTrabajosSinEntregarAuditoria()
  }

  static void fireCancellationsReport( ) {
    if ( twoDateFilterDialog == null ) {
      twoDateFilterDialog = new TwoDatesSelectionFilterDialog()
    }
    twoDateFilterDialog.setTitle( "Reporte de Cancelaciones" )
    twoDateFilterDialog.activate()

    Date reportForDateStart = twoDateFilterDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateFilterDialog.getSelectedDateEnd()
    boolean resumen = twoDateFilterDialog.getCbResume()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateFilterDialog.button ) {
      if ( resumen ) {
        log.debug( "Imprime el reporte de Cancelaciones Resumido" )
        reportService.obtenerReporteCancelacionesResumido( reportForDateStart, reportForDateEnd )
        twoDateFilterDialog = null
      } else {
        reportService.obtenerReporteCancelacionesCompleto( reportForDateStart, reportForDateEnd )
        log.debug( "Imprime el reporte de Cancelaciones Completo" )
        twoDateFilterDialog = null
      }
    } else {
      log.debug( "Cancelar_continuar" )
    }

  }

  static void fireSalesbyLineReport( ) {
    if ( twoDatesSelectionFilterLineDialog == null ) {
      twoDatesSelectionFilterLineDialog = new TwoDatesSelectionFilterLineDialog()
    }
    twoDatesSelectionFilterLineDialog.setTitle( "Reporte de Ventas por Linea" )
    twoDatesSelectionFilterLineDialog.activate()
    String articuloDesc = twoDatesSelectionFilterLineDialog.getselectedArticle()
    Date reportForDateStart = twoDatesSelectionFilterLineDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDatesSelectionFilterLineDialog.getSelectedDateEnd()
    boolean articulo = twoDatesSelectionFilterLineDialog.getCbArticulo()
    boolean Factura = twoDatesSelectionFilterLineDialog.getCbFactura()
    boolean gogle = twoDatesSelectionFilterLineDialog.getCbGogle()
    boolean oftalmico = twoDatesSelectionFilterLineDialog.getCbOftalmico()
    boolean todo = twoDatesSelectionFilterLineDialog.getCbTodo()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDatesSelectionFilterLineDialog.button ) {
      if ( articulo == true ) {
        log.debug( "Imprime el reporte de Ventas por Linea por Articulo" )
        reportService.obtenerReporteVentasporLineaArticulo( reportForDateStart, reportForDateEnd, articuloDesc, gogle, oftalmico, todo )
      } else {
        log.debug( "Imprime el reporte de Ventas por Linea por Factura" )
        reportService.obtenerReporteVentasporLineaFactura( reportForDateStart, reportForDateEnd, articuloDesc, gogle, oftalmico, todo )
      }

    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireSalesbyBrandReport( ) {
    if ( twoDatesSelectionFilterBrandArticleDialog == null ) {
      twoDatesSelectionFilterBrandArticleDialog = new TwoDatesSelectionFilterBrandArticleDialog( true )
    }
    twoDatesSelectionFilterBrandArticleDialog.setTitle( "Reporte de Ventas por Marca" )
    twoDatesSelectionFilterBrandArticleDialog.activate()
    String articuloDesc = twoDatesSelectionFilterBrandArticleDialog.getselectedArticle()
    Date reportForDateStart = twoDatesSelectionFilterBrandArticleDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDatesSelectionFilterBrandArticleDialog.getSelectedDateEnd()
    boolean articulos = twoDatesSelectionFilterBrandArticleDialog.getCbArticulos()
    boolean solar = false//twoDatesSelectionFilterBrandDialog.getCbGogle()
    boolean oftalmico = false//twoDatesSelectionFilterBrandDialog.getCbOftalmico()
    boolean todo = true//twoDatesSelectionFilterBrandDialog.getCbTodo()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDatesSelectionFilterBrandArticleDialog.button ) {
      log.debug( "Imprime el reporte de Ventas por Marca" )
      reportService.obtenerReporteVentasMarca( reportForDateStart, reportForDateEnd, articuloDesc, articulos, solar, oftalmico, todo )
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireSalesbySellerbyBrandReport( ) {
    if ( twoDatesSelectionFilterBrandDialog == null ) {
        twoDatesSelectionFilterBrandDialog = new TwoDatesSelectionFilterBrandDialog( )
    }
      twoDatesSelectionFilterBrandDialog.setTitle( "Reporte de Ventas por Vendedor por Marca" )
      twoDatesSelectionFilterBrandDialog.activate()
    String articuloDesc = twoDatesSelectionFilterBrandDialog.getselectedArticle()
    Date reportForDateStart = twoDatesSelectionFilterBrandDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDatesSelectionFilterBrandDialog.getSelectedDateEnd()
    boolean solar = false//twoDatesSelectionFilterBrandArticleDialog.getCbGogle()
    boolean oftalmico = false//twoDatesSelectionFilterBrandArticleDialog.getCbOftalmico()
    boolean todo = true//twoDatesSelectionFilterBrandArticleDialog.getCbTodo()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDatesSelectionFilterBrandDialog.button ) {
      log.debug( "Imprime el reporte de Ventas por Vendedor por Marca" )
      reportService.obtenerReporteVentasVendedorporMarca( reportForDateStart, reportForDateEnd, articuloDesc, false, solar, oftalmico, todo )
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireStockbyBrandReport( ) {
    if ( articleSelectionFilterDialog == null ) {
      articleSelectionFilterDialog = new ArticleSelectionFilterDialog()
    }
    articleSelectionFilterDialog.setTitle( "Reporte de Existencias por Marca" )
    articleSelectionFilterDialog.activate()
    String articuloDesc = articleSelectionFilterDialog.getselectedArticle()
    boolean resumen = articleSelectionFilterDialog.getCbResume()
    boolean gogle = false//articleSelectionFilterDialog.getCbGogle()
    boolean oftalmico = false//articleSelectionFilterDialog.getCbOftalmico()
    boolean todo = true//articleSelectionFilterDialog.getCbTodo()
    if ( articleSelectionFilterDialog.button ) {
      if ( resumen == true ) {
        log.debug( "Imprime el reporte de Existencias por Marca Resumido" )
        reportService.obtenerReporteExistenciasporMarcaResumido( articuloDesc, gogle, oftalmico, todo )
        articleSelectionFilterDialog = null
      } else {
        log.debug( "Imprime el reporte de Existencias por Marca" )
        reportService.obtenerReporteExistenciasporMarca( articuloDesc, gogle, oftalmico, todo )
        articleSelectionFilterDialog = null
      }
    }
  }

  static void fireStockbyBrandColorReport( ) {
    if ( articleAndColorSelectionFilterDialog == null ) {
      articleAndColorSelectionFilterDialog = new ArticleAndColorSelectionFilterDialog()
    }
    articleAndColorSelectionFilterDialog.setTitle( "Reporte de Existencias por Articulo" )
    articleAndColorSelectionFilterDialog.activate()
    String articuloDesc = articleAndColorSelectionFilterDialog.getselectedArticle()
    String descripcion = articleAndColorSelectionFilterDialog.getselectedDescription()
    String colorDesc = articleAndColorSelectionFilterDialog.getselectedColor()
    if ( articleAndColorSelectionFilterDialog.button ) {
      log.debug( "Imprime el reporte de Existencias por Articulo" )
      reportService.obtenerReporteExistenciasporArticulo( articuloDesc, descripcion, colorDesc )
      articleAndColorSelectionFilterDialog = null
    }
  }

  static void fireJobControlReport( ) {
    if ( filterDialog == null ) {
      filterDialog = new FilterDialog()
    }
    filterDialog.setTitle( "Reporte de Control de Trabajos" )
    filterDialog.activate()
    boolean retenido = filterDialog.getRetenido()
    boolean porEnviar = filterDialog.getPorEnviar()
    boolean pino = filterDialog.getPino()
    boolean sucursal = filterDialog.getSucursal()
    boolean todos = filterDialog.getTodo()
    boolean factura = filterDialog.getFactura()
    boolean fecha = filterDialog.getFecha()
    if ( filterDialog.button ) {
      log.debug( "Imprime el reporte de Control de Trabajos" )
      reportService.obtenerReporteControldeTrabajos( retenido, porEnviar, pino, sucursal, todos, factura, fecha );
      filterDialog = null
    }
  }

  static void fireWorkSubmittenReport( ) {
    if ( twoDatesSelectionFilterbyEmployeeDialog == null ) {
      twoDatesSelectionFilterbyEmployeeDialog = new TwoDatesSelectionFilterbyEmployeeDialog()
    }
    twoDatesSelectionFilterbyEmployeeDialog.setTitle( "Reporte de Trabajos Entregados" )
    twoDatesSelectionFilterbyEmployeeDialog.activate()
    Date reportForDateStart = twoDatesSelectionFilterbyEmployeeDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDatesSelectionFilterbyEmployeeDialog.getSelectedDateEnd()
    boolean resumen = twoDatesSelectionFilterbyEmployeeDialog.getCbResume()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDatesSelectionFilterbyEmployeeDialog.button ) {
      if ( resumen == true ) {
        log.debug( "Imprime el reporte de Trabajos Entregados por Empleado" )
        reportService.obtenerReporteTrabajosEntregadosporEmpleado( reportForDateStart, reportForDateEnd )
        twoDatesSelectionFilterbyEmployeeDialog = null
      }
      if ( resumen == false ) {
        log.debug( "Imprime el reporte de Trabajos Entregados sin Corte por Empleado" )
        reportService.obtenerReporteTrabajosEntregados( reportForDateStart, reportForDateEnd )
        twoDatesSelectionFilterbyEmployeeDialog = null
      }
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireTaxBillsReport( ) {
    if ( twoDateDialog == null ) {
      twoDateDialog = new TwoDatesSelectionDialog()
    }
    twoDateDialog.setTitle( "Facturas Fiscales" )
    twoDateDialog.activate()
    Date reportForDateStart = twoDateDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateDialog.getSelectedDateEnd()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateDialog.button ) {
      log.debug( "Imprime el reporte de Facturas Fiscales" )
      reportService.obtenerReporteFacturasFiscales( reportForDateStart, reportForDateEnd )
      twoDateDialog = null
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireDiscountsReport( ) {
    if ( twoDateKeyDialog == null ) {
        twoDateKeyDialog = new TwoDatesSelectionAndKeyDialog()
    }
      twoDateKeyDialog.setTitle( "Descuentos" )
      twoDateKeyDialog.activate()
    Date reportForDateStart = twoDateKeyDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateKeyDialog.getSelectedDateEnd()
    String key = twoDateKeyDialog.getDiscountKey().trim()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateKeyDialog.button ) {
      log.debug( "Imprime el reporte de Descuentos" )
      reportService.obtenerReporteDescuentosMasVision( reportForDateStart, reportForDateEnd, key )
      twoDateKeyDialog = null
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void firePromotionsReport( ) {
    if ( twoDateDialog == null ) {
      twoDateDialog = new TwoDatesSelectionDialog()
    }
    twoDateDialog.setTitle( "Promociones en Ventas" )
    twoDateDialog.activate()
    Date reportForDateStart = twoDateDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateDialog.getSelectedDateEnd()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateDialog.button ) {
      log.debug( "Imprime el reporte de Promociones" )
      reportService.obtenerReportePromocionesAplicadas( reportForDateStart, reportForDateEnd )
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void firePaymentsReport( ) {
    if ( twoDatesSelectionFilterPaymentsDialog == null ) {
      twoDatesSelectionFilterPaymentsDialog = new TwoDatesSelectionFilterPaymentsDialog()
    }
    twoDatesSelectionFilterPaymentsDialog.setTitle( "Pagos" )
    twoDatesSelectionFilterPaymentsDialog.activate()
    Date reportForDateStart = twoDatesSelectionFilterPaymentsDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDatesSelectionFilterPaymentsDialog.getSelectedDateEnd()
    String payment = twoDatesSelectionFilterPaymentsDialog.getSelectedPayment()
    String bill = twoDatesSelectionFilterPaymentsDialog.getSelectedBill()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDatesSelectionFilterPaymentsDialog.button ) {
      log.debug( "Imprime el reporte de Pagos" )
      reportService.obtenerReportePagos( reportForDateStart, reportForDateEnd, payment, bill )
      twoDatesSelectionFilterPaymentsDialog = null
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireQuoteReport( ) {
    if ( twoDateDialog == null ) {
      twoDateDialog = new TwoDatesSelectionDialog()
    }
    twoDateDialog.setTitle( "Cotizaciones" )
    twoDateDialog.activate()
    Date reportForDateStart = twoDateDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateDialog.getSelectedDateEnd()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateDialog.button ) {
      log.debug( "Imprime el reporte de Cotizaciones" )
      reportService.obtenerReporteCotizaciones( reportForDateStart, reportForDateEnd )
      twoDateDialog = null
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void fireExamsReport( ) {
    if ( twoDateFilterDialog == null ) {
      twoDateFilterDialog = new TwoDatesSelectionFilterDialog()
    }
    twoDateFilterDialog.setTitle( "Reporte de Examenes" )
    twoDateFilterDialog.activate()
    Date reportForDateStart = twoDateFilterDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateFilterDialog.getSelectedDateEnd()
    boolean resumen = twoDateFilterDialog.getCbResume()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateFilterDialog.button ) {
      if ( resumen == true ) {
        log.debug( "Imprime el reporte de Examenes Resumido" )
        reportService.obtenerReporteExamenesResumido( reportForDateStart, reportForDateEnd )
        twoDateFilterDialog = null
      }
      if ( resumen == false ) {
        reportService.obtenerReporteExamenesCompleto( reportForDateStart, reportForDateEnd )
        twoDateFilterDialog = null
        log.debug( "Imprime el reporte de Examenes Completo" )
      }
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }


    static void fireExamsByOptoReport( ) {
        if ( twoDatesSelectionFilterbyClientDialog == null ) {
            twoDatesSelectionFilterbyClientDialog = new TwoDatesSelectionFilterbyClientDialog()
        }
        twoDatesSelectionFilterbyClientDialog.setTitle( "Reporte de Examenes por Optometrista" )
        twoDatesSelectionFilterbyClientDialog.activate()
        Date reportForDateStart = twoDatesSelectionFilterbyClientDialog.getSelectedDateStart()
        Date reportForDateEnd = twoDatesSelectionFilterbyClientDialog.getSelectedDateEnd()
        boolean resumen = twoDatesSelectionFilterbyClientDialog.getCbResume()
        if ( reportForDateStart != null && reportForDateEnd != null && twoDatesSelectionFilterbyClientDialog.button ) {
            if( resumen ){
                reportService.obtenerReporteExamenesPorOptoCompleto( reportForDateStart, reportForDateEnd )
                twoDatesSelectionFilterbyClientDialog = null
            } else {
              reportService.obtenerReporteExamenesPorOpto( reportForDateStart, reportForDateEnd )
              twoDatesSelectionFilterbyClientDialog = null
            }
        } else {
            log.debug( "Cancelar_continuar" )
        }
    }


  static void fireOptometristSalesReport( ) {
    if ( twoDateDialog == null ) {
        twoDateDialog = new TwoDatesSelectionDialog()
    }
      twoDateDialog.setTitle( "Reporte de Ventas por Optometrista" )
      twoDateDialog.activate()
    Date reportForDateStart = twoDateDialog.getSelectedDateStart()
    Date reportForDateEnd = twoDateDialog.getSelectedDateEnd()
    if ( reportForDateStart != null && reportForDateEnd != null && twoDateDialog.button ) {
        log.debug( "Imprime el reporte de Ventas por Optometrista" )
        reportService.obtenerReporteVentasporOptometrista( reportForDateStart, reportForDateEnd )
        twoDateDialog = null
    } else {
      log.debug( "Cancelar_continuar" )
    }
  }

  static void firePromotionsListReport(){
    log.debug( "Imprime el reporte de promociones que se pueden aplicar" )

    Date fechaImpresion = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH )
    reportService.obtenerReportePromociones( fechaImpresion )

  }

  static void kardexByDateAndSkuReport() {
    log.debug( 'Imprime el reporte de kardex por sku y fecha' )
    if ( kardexReportDialog == null ) {
      kardexReportDialog = new KardexReportDialog("Seleccionar Articulo y fechas")
    }
    kardexReportDialog.setTitle( "Kardex Por Articulo" )
    kardexReportDialog.activate()
    String articulo =kardexReportDialog.getSku()
    Date reportForDateStart = kardexReportDialog.getSelectedDateStart()
    Date reportForDateEnd = kardexReportDialog.getSelectedDateEnd()
    if( StringUtils.trimToEmpty(articulo) != '' && reportForDateStart != null && reportForDateEnd != null ){
      //Integer sku = NumberFormat.getInstance().parse( strSku )
      reportService.obtenerReporteDeKardex( articulo, reportForDateStart, reportForDateEnd, null )
    }
    kardexReportDialog = null
  }


  static void kardexBySkuReport() {
    log.debug( 'Imprime el reporte de kardex por sku' )
    if ( kardexReportDialog == null ) {
      kardexReportDialog = new KardexReportDialog("Seleccionar Sku y fechas")
    }
    kardexReportDialog.setTitle( "Kardex Por Sku" )
    kardexReportDialog.activate()
    Integer sku = 0
    try{
      sku = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(kardexReportDialog.getSku()))
    } catch ( NumberFormatException e ){
      println e.message
    }
    Articulo articulo = RepositoryFactory.partMaster.findOne(sku)
    Date reportForDateStart = kardexReportDialog.getSelectedDateStart()
    Date reportForDateEnd = kardexReportDialog.getSelectedDateEnd()
    if( articulo != null && reportForDateStart != null && reportForDateEnd != null ){
      //Integer sku = NumberFormat.getInstance().parse( strSku )
      reportService.obtenerReporteDeKardex( StringUtils.trimToEmpty(articulo.articulo)+","+StringUtils.trimToEmpty(articulo.codigoColor), reportForDateStart, reportForDateEnd, articulo.id )
    }
    kardexReportDialog = null
  }


  static void todaySales(){
    log.debug( 'Imprime el reporte de ventas del dia' )
    if( salesForDayDialog == null ){
      salesForDayDialog = new DateSelectionCheckBoxDialog()
    }
    salesForDayDialog.setTitle( "Ventas Por Dia" )
    salesForDayDialog.activate()
    Date salesDate = salesForDayDialog.getSelectedDate()
    Boolean artPrecioMayorCero = salesForDayDialog.getCbArtCero()
    if( salesDate != null ){
      reportService.obtenerReporteDeVentasDelDiaActual( salesDate, artPrecioMayorCero )
    }
  }


  static void paymentsByPeriod(){
    log.debug( 'imprime el reporte de Ingresos por Preiodo' )
    if( twoDateDialog == null ){
      twoDateDialog = new TwoDatesSelectionDialog()
    }
    twoDateDialog.setTitle( 'Ingresos por Periodo' )
    twoDateDialog.activate()
    Date dateStart = twoDateDialog.getSelectedDateStart()
    Date dateEnd = twoDateDialog.getSelectedDateEnd()
    if( dateStart != null && dateEnd != null ){
      reportService.obtenerReporteDeIngresosPorPeriodo( dateStart, dateEnd )
    }
  }


  static void coupons(){
      log.debug( 'imprime el reporte de Cupones' )
      if( twoDateDialog == null ){
          twoDateDialog = new TwoDatesSelectionDialog()
      }
      twoDateDialog.setTitle( 'Cupones' )
      twoDateDialog.activate()
      Date dateStart = twoDateDialog.getSelectedDateStart()
      Date dateEnd = twoDateDialog.getSelectedDateEnd()
      if( dateStart != null && dateEnd != null ){
          reportService.obtenerReporteDeCupones( dateStart, dateEnd )
          twoDateDialog = null
      }
  }


  static void couponsMv(){
    log.debug( 'imprime el reporte de CuponesMv' )
    if( cuponMvReportSelectionDialog == null ){
      cuponMvReportSelectionDialog = new CuponMvReportSelectionDialog()
    }
    cuponMvReportSelectionDialog.setTitle( 'Cupones' )
    cuponMvReportSelectionDialog.activate()
    Boolean todo = cuponMvReportSelectionDialog.todo
    Boolean NoAplicados = cuponMvReportSelectionDialog.noAplicados
    Boolean extemporaneos = cuponMvReportSelectionDialog.extemporaneos
    Date dateStart = cuponMvReportSelectionDialog.getSelectedDateStart()
    Date dateEnd = cuponMvReportSelectionDialog.getSelectedDateEnd()
    if( todo != null && NoAplicados != null && extemporaneos != null &&
            dateStart != null && dateEnd != null && cuponMvReportSelectionDialog.button ){
      reportService.obtenerReporteDeCuponesMv( dateStart, dateEnd, todo, NoAplicados, extemporaneos )
      cuponMvReportSelectionDialog = null
    }
  }

  static void fireMultipaymentReport(){
    log.debug( "Imprime el reporte de ventas por multipago" )
    if( twoDateDialog == null ){
      twoDateDialog = new TwoDatesSelectionDialog()
    }
    twoDateDialog.setTitle( 'Multipago' )
    twoDateDialog.activate()
    Date dateStart = twoDateDialog.selectedDateStart
    Date dateEnd = twoDateDialog.selectedDateEnd
    if( dateStart != null && dateEnd != null && twoDateDialog.button ){
      reportService.obtenerReporteVentasMultipago( dateStart, dateEnd )
      twoDateDialog = null
    }
  }


  static void fireSubmanagerReport(){
    log.debug( "Imprime el reporte de subgerentes asignados" )
    if( twoDateDialog == null ){
      twoDateDialog = new TwoDatesSelectionDialog()
    }
    twoDateDialog.setTitle( 'Subgerentes Asignados' )
    twoDateDialog.activate()
    Date dateStart = twoDateDialog.selectedDateStart
    Date dateEnd = twoDateDialog.selectedDateEnd
    if( dateStart != null && dateEnd != null && twoDateDialog.button ){
      reportService.obtenerReporteSubgerentesAsignados( dateStart, dateEnd )
      twoDateDialog = null
    }
  }


  static void fireCheckReport(){
    log.debug( "Imprime el reporte de checadas" )
    if( twoDateDialog == null ){
      twoDateDialog = new TwoDatesSelectionDialog()
    }
    twoDateDialog.setTitle( 'Checadas Regional' )
    twoDateDialog.activate()
    Date dateStart = twoDateDialog.selectedDateStart
    Date dateEnd = twoDateDialog.selectedDateEnd
    if( dateStart != null && dateEnd != null && twoDateDialog.button ){
      reportService.obtenerReporteChecadasPorFecha( dateStart, dateEnd )
      twoDateDialog = null
    }
  }



  // Public Methods
  static void fireReport( Report pReport ) {
    switch ( pReport ) {
      case Report.DailyClose: fireDailyCloseReport(); break;
      case Report.IncomePerBranch: fireIncomePerBranchReport(); break;
      case Report.SellerRevenue: fireSellerRevenueReport(); break;
      case Report.Sales: fireSalesReport(); break;
      case Report.SalesbySeller: fireSalesbySellerReport(); break;
      case Report.UndeliveredJobs: fireUndeliveredJobsReport(); break;
      case Report.UndeliveredJobsAudit: fireUndeliveredJobsAuditReport(); break;
      case Report.Cancellations: fireCancellationsReport(); break;
      case Report.SalesbyLine: fireSalesbyLineReport(); break;
      case Report.SalesbyBrand: fireSalesbyBrandReport(); break;
      case Report.SalesbySellerbyBrand: fireSalesbySellerbyBrandReport(); break;
      case Report.StockbyBrand: fireStockbyBrandReport(); break;
      case Report.StockbyBrandColor: fireStockbyBrandColorReport(); break;
      case Report.JobControl: fireJobControlReport(); break;
      case Report.WorkSubmitted: fireWorkSubmittenReport(); break;
      case Report.TaxBills: fireTaxBillsReport(); break;
      case Report.Discounts: fireDiscountsReport(); break;
      case Report.PromotionsinSales: firePromotionsReport(); break;
      case Report.Payments: firePaymentsReport(); break;
      case Report.Quote: fireQuoteReport(); break;
      case Report.Exams: fireExamsReport(); break;
      case Report.OptometristSales: fireOptometristSalesReport(); break;
      case Report.Promotions: firePromotionsListReport(); break;
      case Report.Kardex: kardexByDateAndSkuReport(); break;
      case Report.KardexBySku: kardexBySkuReport(); break;
      case Report.SalesToday: todaySales(); break;
      case Report.PaymentsbyPeriod: paymentsByPeriod(); break;
      case Report.Coupon: coupons(); break;
      case Report.CouponMv: couponsMv(); break;
      case Report.ExamsByOpto: fireExamsByOptoReport(); break;
      case Report.Cellar: fireCellarReport(); break;
      case Report.Multipayment: fireMultipaymentReport(); break;
      case Report.Submanager: fireSubmanagerReport(); break;
      case Report.Check: fireCheckReport(); break;
    }
  }
}
