package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.model.*
import mx.lux.pos.service.CancelacionService
import mx.lux.pos.service.IOService
import mx.lux.pos.service.InventarioService
import mx.lux.pos.service.JbService
import mx.lux.pos.service.JbTrackService
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.service.PagoService
import mx.lux.pos.service.TicketService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.resources.ServiceManager
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.swing.JOptionPane

@Slf4j
@Component
class CancellationController {

  private static CancelacionService cancelacionService
  private static TicketService ticketService
  private static NotaVentaService notaVentaService
  private static IOService ioService
  private static InventarioService inventarioService
  private static JbService jbService
  private static JbTrackService jbTrackService
  private static PagoService pagoService

  private static final String TAG_REUSO = 'R'
  private static final String TAG_CUPON = 'C'
  private static final String TAG_DESC_CANCELACION_1_PAR = "CANCELACION PRIMER PAR"
  private static final String TAG_RAZON_CAMBIO_FORMA_PAGO = 'CAMBIO DE FORMA DE PAGO'

  @Autowired CancellationController( CancelacionService cancelacionService, TicketService ticketService, NotaVentaService notaVentaService,
                                     IOService ioService, InventarioService inventarioService, JbService jbService, JbTrackService jbTrackService,
                                     PagoService pagoService ) {
    this.cancelacionService = cancelacionService
    this.ticketService = ticketService
    this.notaVentaService = notaVentaService
    this.ioService = ioService
    this.inventarioService = inventarioService
    this.jbService = jbService
    this.jbTrackService = jbTrackService
    this.pagoService = pagoService
  }

  static List<String> findAllCancellationReasons( ) {
    log.info( 'obteniendo lista de causas de cancelacion' )
    List<CausaCancelacion> lstResults = cancelacionService.listarCausasCancelacion()
    List<CausaCancelacion> results = new ArrayList<>()
    results.add( new CausaCancelacion() )
    for(CausaCancelacion causaCancelacion : lstResults){
      if( !causaCancelacion.descripcion.equalsIgnoreCase(TAG_DESC_CANCELACION_1_PAR) ){
        results.add( causaCancelacion )
      }
    }
    return results?.collect { CausaCancelacion causa ->
      causa.descripcion
    }
  }

  static boolean allowLateCancellation( String orderId ) {
    log.info( 'solicitando autorizacion para cancelacion posterior a compra' )
    if ( StringUtils.isNotBlank( orderId ) ) {
      return cancelacionService.permitirCancelacionExtemporanea( orderId )
    }
    log.warn( 'no se obtiene autorizacion, parametro nulo o vacio' )
    return false
  }

  static boolean cancelOrder( String orderId, String reason, String comments, Boolean transCupones ) {
    log.info( "solicitando cancelacion de orden id: ${orderId}, causa: ${reason}" )
    if ( StringUtils.isNotBlank( orderId ) && StringUtils.isNotBlank( reason ) ) {
      User user = Session.get( SessionItem.USER ) as User
      Modificacion modificacion = new Modificacion(
          idEmpleado: user?.username,
          causa: reason,
          observaciones: comments
      )
      modificacion = cancelacionService.registrarCancelacionDeNotaVenta( orderId, modificacion, user.username, transCupones )
      if ( modificacion?.id ) {
        if( cancelacionService.cancMismoDia( StringUtils.trimToEmpty(orderId) ) ){
          //cancelacionService.actualizaJbCancelado( StringUtils.trimToEmpty(modificacion.idFactura), StringUtils.trimToEmpty(user.username) )
          updateJb( StringUtils.trimToEmpty(orderId) )
        }
        cancelacionService.cancelarCupones( orderId )
        log.debug( "modificacion de cancelacion registrada id: ${modificacion.id}" )
        //ServiceManager.ioServices.logAdjustmentNotification( modificacion.id )
        return true
      } else {
        log.warn( 'error, no se registra cancelacion' )
      }
    } else {
      log.warn( 'no se registra cancelacion, parametros invalidos' )
    }
    return false
  }

  static List<Refund> findRefundsByOrderId( String orderId ) {
    log.info( "obteniendo devoluciones por orden id: ${orderId}" )
    List<Devolucion> results = cancelacionService.listarDevolucionesDeNotaVenta( orderId )
    if ( results?.any() ) {
      return results.collect { Devolucion devolucion ->
        Refund.toRefund( devolucion )
      }
    }
    return [ ]
  }

  static boolean refundPaymentsCreditFromOrder( String orderId, Map<Integer, String> creditRefunds, String dataDev ) {
    log.info( "solicitando registrar devoluciones: ${creditRefunds} de orden id: ${orderId}" )
    if ( StringUtils.isNotBlank( orderId ) ) {
      creditRefunds?.each { Integer pagoId, String valor ->
        if ( StringUtils.isBlank( valor ) ) {
          creditRefunds.remove( pagoId )
        }
      }
      List<Devolucion> results = cancelacionService.registrarDevolucionesDeNotaVenta( orderId, creditRefunds, dataDev )
      if ( results?.any() ) {
        log.debug( "devoluciones registradas obtenidas: ${results*.id}" )
        return true
      }
    }
    return false
  }

  static boolean transferPaymentsCreditToOrder( String fromOrderId, String toOrderId, Map<Integer, BigDecimal> creditTransfers ) {
    log.info( "solicitando registrar transferencias: ${creditTransfers} de orden id: ${fromOrderId} a orden id: ${toOrderId}" )
    if ( StringUtils.isNotBlank( fromOrderId ) && StringUtils.isNotBlank( toOrderId ) ) {
        Map<Integer, BigDecimal> creditTransfers2 = new HashMap<Integer, BigDecimal>()
        creditTransfers2.putAll( creditTransfers )
        creditTransfers2?.each { Integer pagoId, BigDecimal valor ->
        if ( !valor ) {
          creditTransfers.remove( pagoId )
        }
      }
      List<Pago> results = cancelacionService.registrarTransferenciasParaNotaVenta( fromOrderId, toOrderId, creditTransfers )
      if ( results?.any() ) {
        log.debug( "transferencias registradas obtenidas: ${results*.id}" )
        return true
      }
    }
    return false
  }

  static boolean orderHasTransfers( String orderId ) {
    log.info( "verificando transferencias para la orden id: ${orderId}" )
    if ( StringUtils.isNotBlank( orderId ) ) {
      List<NotaVenta> results = cancelacionService.listarNotasVentaOrigenDeNotaVenta( orderId )
      return results?.any()
    } else {
      log.warn( 'no se verifican transferencias para la orden, parametros invalidos' )
    }
    return false
  }

  static List<String> findSourceOrdersWithCredit( String orderId ) {
    log.info( "obteniendo ordenes origen con credito a partir de la orden id: ${orderId}" )
    if ( StringUtils.isNotBlank( orderId ) ) {
      List<NotaVenta> results = cancelacionService.listarNotasVentaOrigenDeNotaVenta( orderId )
      List<String> sources = [ ]
      results?.each { NotaVenta tmp ->
        BigDecimal credit = cancelacionService.obtenerCreditoDeNotaVenta( tmp?.id )
        if ( credit != null && credit >= 1 ) {
          sources.add( tmp?.id )
        }
      }
      return sources.any() ? sources : [ ]
    } else {
      log.warn( 'no se obtenienen ordenes origen con credito a partir de la orden, parametros invalidos' )
    }
    return [ ]
  }

  static void printCancellationPlan( String orderId ) {
    log.info( "imprimiendo plan de cancelacion de orden id: ${orderId}" )
    if ( StringUtils.isNotBlank( orderId ) ) {
      ticketService.imprimePlanCancelacion( orderId )
    } else {
      log.warn( 'no se imprime plan de cancelacion de orden, parametros invalidos' )
    }
  }

  static void printOrderCancellation( String orderId ) {
    log.info( "imprimiendo cancelacion de orden id: ${orderId}" )
    if ( StringUtils.isNotBlank( orderId ) ) {
      ticketService.imprimeCancelacion( orderId )
    } else {
      log.warn( 'no se imprime cancelacion de orden, parametros invalidos' )
    }
  }

  static Boolean printCancellationsFromOrder( String orderId ) {
    Boolean reuso = false
    log.info( "imprimiendo cancelaciones a partir de orden id: ${orderId}" )
    if ( StringUtils.isNotBlank( orderId ) ) {
      List<NotaVenta> results = cancelacionService.listarNotasVentaOrigenDeNotaVenta( orderId )
      results?.each { NotaVenta tmp ->
        ticketService.imprimeCancelacion( tmp?.id )
      }
      Boolean isReuso = false
      NotaVenta nv = notaVentaService.obtenerNotaVenta( orderId )
      if(nv != null){
        for(DetalleNotaVenta det : nv.detalles){
          if( TAG_REUSO.equalsIgnoreCase(det?.surte?.trim()) ){
            isReuso = true
          }
        }
      }
      if(results.size() > 0 && isReuso){
        reuso = true
        ticketService.imprimeRegresoMaterial( results.first().id )
        ticketService.imprimeRecepcionMaterial( results.first().id )
        ticketService.imprimeTicketReuso( results.first().id )
      }
    } else {
      log.warn( 'no se imprimen cancelaciones a partir de orden, parametros invalidos' )
    }
    return reuso
  }

  static void refreshOrder( Order order ) {
    Order newOrder = OrderController.getOrder( order.id )
    order.id = newOrder.id
    order.bill = newOrder.bill
    order.comments = newOrder.comments
    order.status = newOrder.status
    order.date = newOrder.date
    order.branch = newOrder.branch
    order.customer = newOrder.customer
    order.items = newOrder.items
    order.payments = newOrder.payments
    order.deals = newOrder.deals
    order.total = newOrder.total
    order.paid = newOrder.paid
    order.due = newOrder.due
  }


    static boolean validateTransfer( String idOrder ) {
        log.info( "Validadndo transferencia" )
        boolean transfValida = cancelacionService.validandoTransferencia( idOrder )
        return transfValida
    }

    static void resetValuesofCancellation( String idOrder ) {
        log.info( "restableciendo valores de cancelacion con id: ${idOrder}" )
            cancelacionService.restablecerValoresDeCancelacion( idOrder )
    }

  static List<Order> findOrderToResetValues( String idOrder ){
    log.info( "obteniendo notaventa para restablecer los montos de transferencia" )
    List<NotaVenta> lstNotasVentas = cancelacionService.listarNotasVentaOrigenDeNotaVenta( idOrder )
    return lstNotasVentas?.collect { NotaVenta tmp ->
      Order.toOrder( tmp )
    }
  }


  static String findCancellationReasonById( Integer id ) {
    log.info( 'obteniendo lista de causas de cancelacion' )
    CausaCancelacion result = cancelacionService.causaCancelacion( id )
    return result.descripcion
  }


  static void printMaterialReturn( String orderId ) {
      log.info( "imprimiendo regreso de material de orden id: ${orderId}" )
      if ( StringUtils.isNotBlank( orderId ) ) {
          ticketService.imprimeRegresoMaterial( orderId )
      } else {
          log.warn( 'no se imprime regreso de material, parametros invalidos' )
      }
  }


  static void printMaterialReception( String orderId ) {
      log.info( "imprimiendo recepcion de material de orden id: ${orderId}" )
      if ( StringUtils.isNotBlank( orderId ) ) {
          ticketService.imprimeRecepcionMaterial( orderId )
      } else {
          log.warn( 'no se imprime recepcion de material, parametros invalidos' )
      }
  }

  static Boolean verificaPino( String idOrder ){
      Boolean isPino =  cancelacionService.validandoEnvioPino( idOrder )
      return isPino
  }


  static void printPinoNotStocked( String orderId ) {
      log.info( "imprimiendo pino no surtido de orden id: ${orderId}" )
      if ( StringUtils.isNotBlank( orderId ) ) {
          ticketService.imprimePinoNoSurtido( orderId )
      } else {
          log.warn( 'no se imprime plan de cancelacion de orden, parametros invalidos' )
      }
  }


  static void updateJb( String orderId ){
    log.info( "updateJb( )" )
    cancelacionService.actualizaJb( orderId )
    cancelacionService.insertaJbTrack( orderId )
    cancelacionService.eliminaJbLlamada( orderId )
    cancelacionService.actualizaGrupo( orderId, 'C' )
  }

  static void generatedAcuses( orderId ){
    log.info( 'generatedAcuses( )' )
    cancelacionService.generaAcuses( orderId )
  }

  static Boolean printReUse( String orderId ){
    Boolean reuse = false
    if ( StringUtils.isNotBlank( orderId ) ) {
      List<NotaVenta> results = cancelacionService.listarNotasVentaOrigenDeNotaVenta( orderId )
      Boolean isReuso = false
      NotaVenta nv = notaVentaService.obtenerNotaVenta( orderId )
      if(nv != null){
          for(DetalleNotaVenta det : nv.detalles){
              if( TAG_REUSO.equalsIgnoreCase(det?.surte?.trim()) ){
                  isReuso = true
              }
          }
      }
      if( results.size() > 0 && isReuso ){
        reuse = true
        ticketService.imprimeRegresoMaterial( results.first().id )
        ticketService.imprimeRecepcionMaterial( results.first().id )
        ticketService.imprimeTicketReuso( results.first().id )
      }
      return reuse
    }
  }


  static String findSourceOrder( String orderId ) {
    log.debug( "findSourceOrder( )" )
    NotaVenta notaOrig = notaVentaService.obtenerNotaVentaOrigen( orderId )
    return notaOrig != null ? notaOrig.id : ''
  }


   static void refoundCoupons( String idNotaVenta ){
     String paymentsNoRefound = Registry.paymentsNoRefound
     NotaVenta nota = notaVentaService.obtenerNotaVenta( idNotaVenta )
     Boolean cupon = false
     BigDecimal montoPago = BigDecimal.ZERO
     BigDecimal montoPagoCup = BigDecimal.ZERO
     for(Pago payment : nota.pagos){
       montoPago = montoPago.add(payment.monto)
       if(paymentsNoRefound.contains(payment.idFPago.trim())){
         cupon = true
       }
     }
     if( cupon ){
       Map<Integer, String> creditRefunds = [ : ]
       List<Pago> payments = new ArrayList<Pago>()

       creditRefunds = new HashMap<>()
       for(Pago payment : nota.pagos){
         if(paymentsNoRefound.contains(payment.idFPago.trim())){
           montoPagoCup = payment.monto
           payments.add( payment )
         }
       }
       payments.each { Pago pmt ->
         creditRefunds.put( pmt?.id, 'ORIGINAL' )
       }

       if( refundPaymentsCreditFromOrder( nota.id, creditRefunds, "" ) &&
               (montoPago.compareTo(montoPagoCup) <= 0) ){
           printOrderCancellation( nota.id )
           NotaVenta notasReuso = notaVentaService.buscarNotasReuso( nota.id )
           if(notasReuso != null){
               printReUse( notasReuso.id )
           } else {
               //printMaterialReturn( nota.id )
               //printMaterialReception( nota.id )
           }
       }
       payments = new ArrayList<Pago>()
     }
   }


  static void sendCancellationOrderLc( String order ){
    cancelacionService.enviaCancelaccionPedidoLc( order )
  }


  static Boolean sendTransferOrderLc( String order, String idOrder ){
    return cancelacionService.enviaTransferenciaPedidoLc( order, idOrder )
  }

  static void freeCoupon( String idOrder ){
    CuponMv cuponMv = cancelacionService.liberaCupon( StringUtils.trimToEmpty(idOrder) )
    if(cuponMv != null){
      String titulo = ""
      if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("8") ){
        titulo = "SEGUNDO PAR"
      } else if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("7") ){
          titulo = "TERCER PAR"
      } else if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("F") ){
          titulo = "FRIENDS AND FAMILY"
      }
      ticketService.imprimeCupon(cuponMv, titulo, cuponMv.montoCupon)
    }
  }



  static void reassignCoupons( String idOrder ){
    List<NotaVenta> lstNotasConCupon = cancelacionService.tieneCuponesAplicados( idOrder )
    List<Jb> lstJbs = new ArrayList<>()
    List<JbTrack> lstJbTracks = new ArrayList<>()
    if( lstNotasConCupon.size() > 0 ){
      for(NotaVenta nota : lstNotasConCupon){
        List<CausaCancelacion> lstCausas = cancelacionService.listarCausasCancelacion()
        String causaStr = ""
        for(CausaCancelacion causa : lstCausas){
          if( StringUtils.trimToEmpty(causa.descripcion).equalsIgnoreCase(TAG_DESC_CANCELACION_1_PAR) ){
            causaStr = causa.descripcion
          }
        }
        Jb jb = jbService.findJBbyRx( StringUtils.trimToEmpty(nota.factura) )
        if( jb != null ){
          lstJbs.add( jb )
          List<JbTrack> jbTracks = jbTrackService.findByRx( StringUtils.trimToEmpty(nota.factura) )
          for( JbTrack jbTrack : jbTracks ){
            lstJbTracks.add( jbTrack )
          }
        }
        if ( cancelOrder( nota.id, causaStr, "", true ) ) {
            if( !StringUtils.trimToEmpty(causaStr).equalsIgnoreCase(TAG_RAZON_CAMBIO_FORMA_PAGO) ){
              //outputContactLens( nota.id )
            }
            updateJb( nota.id )
            generatedAcuses( nota.id )
            //printCancellationPlan( nota.id )
            try{
                OrderController.runScriptBckpOrder( Order.toOrder(nota) )
            } catch ( Exception e ){
                println e
            }
        } else {
            println( "Ocurrio un error al cancelar" )
        }
      }
      List<String> lstIdsNuevasNotas = cancelacionService.reasignarCupones( idOrder, lstJbs, lstJbTracks )
      for(String id : lstIdsNuevasNotas){
        ticketService.imprimeVenta( id )
        NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( id )
        if( notaVenta != null ){
          if (inventarioService.solicitarTransaccionVenta(notaVenta)) {
            log.debug("transaccion de inventario correcta")
          } else {
            log.warn("no se pudo procesar la transaccion de inventario")
          }
          ioService.logSalesNotification(notaVenta.id)

            List<Pago> lstPagos = new ArrayList<>()
            lstPagos.addAll( notaVenta.pagos )
            Collections.sort(lstPagos, new Comparator<Pago>() {
                @Override
                int compare(Pago o1, Pago o2) {
                    return o1.id.compareTo(o2.id)
                }
            })
            NotaVenta notaOri = null
            for(Pago pago : lstPagos){
                if(StringUtils.trimToEmpty(pago.referenciaPago).startsWith("A")){
                    NotaVenta nota = notaVentaService.obtenerNotaVenta( StringUtils.trimToEmpty(pago.referenciaPago) )
                    if(nota != null && nota.sFactura.equalsIgnoreCase("T")){
                        PedidoLc pedidoLc = notaVentaService.obtienePedidoLc(StringUtils.trimToEmpty(nota.factura))
                        if( pedidoLc != null ){
                            notaOri = nota
                        }
                    }
                }
            }
            if( notaOri != null ){
                cancelacionService.enviaTransferenciaPedidoLc( StringUtils.trimToEmpty(notaOri.factura), StringUtils.trimToEmpty(notaVenta.id) )
            }
        }
      }
    }
  }



  static void outputContactLens(String idOrder){
    User user = Session.get( SessionItem.USER ) as User
    Integer idTrans = cancelacionService.salidaLentesContacto(idOrder, user.username)
    cancelacionService.imprimeTransaccionOtrasSalidas( idTrans )
  }


  static void registerLogAuth( String idOrer, Integer idTipoTrans, Integer idPago ){
    User user = Session.get( SessionItem.USER ) as User
    Pago pago = pagoService.obtenerPago( idPago )
    cancelacionService.registraLogAutorizacion( idOrer, user.username, idTipoTrans, pago )
  }


}
