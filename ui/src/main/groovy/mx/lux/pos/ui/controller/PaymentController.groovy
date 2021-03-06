package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.model.BancoEmisor
import mx.lux.pos.model.MensajesPorParametro
import mx.lux.pos.model.Pago
import mx.lux.pos.model.Plan as CorePlan
import mx.lux.pos.model.Terminal as CoreTerminal
import mx.lux.pos.model.TipoPago
import mx.lux.pos.model.CuponMv
import mx.lux.pos.service.*
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.*
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component
class PaymentController {

  private static PagoService pagoService
  private static TipoPagoService tipoPagoService
  private static BancoService bancoService
  private static TerminalService terminalService
  private static PlanService planService
  private static MensajeService mensajeService
  private static NotaVentaService notaVentaService
  private static String TAG_CUPON = "CUPON"
  private static String TAG_FORMA_PAGO_TRANSF = "TR"
  private static String TAG_FORMA_PAGO_TD = "TD"
  private static String TAG_FORMA_PAGO_EF = "EF"

  @Autowired
  PaymentController(
      PagoService pagoService,
      TipoPagoService tipoPagoService,
      BancoService bancoService,
      TerminalService terminalService,
      PlanService planService,
      MensajeService mensajeService,
      NotaVentaService notaVentaService
  ) {
    this.pagoService = pagoService
    this.tipoPagoService = tipoPagoService
    this.bancoService = bancoService
    this.terminalService = terminalService
    this.planService = planService
    this.mensajeService = mensajeService
    this.notaVentaService = notaVentaService
  }

  static PaymentType findDefaultPaymentType( ) {
    log.info( "obteniendo tipo de pago por defecto" )
    TipoPago result = tipoPagoService.obtenerTipoPagoPorDefecto()
    PaymentType.toPaymentType( result )
  }

  static List<PaymentType> findActivePaymentTypes( BigDecimal montoCupon, String idOrder, Integer idCliente ) {
    log.info( "obteniendo tipos de pago activos" )
    List<TipoPago> results = tipoPagoService.listarTiposPagoActivos()
    results?.collect { TipoPago tipoPago ->
      PaymentType.toPaymentType( tipoPago )
    }
  }

  static List<Bank> findIssuingBanks( ) {
    log.info( "obteniendo bancos emisores" )
    List<BancoEmisor> results = bancoService.listarBancosEmisores()
    results?.collect { BancoEmisor banco ->
      Bank.toBank( banco )
    }
  }

  static List<Terminal> findTerminals( ) {
    log.info( "obteniendo terminales" )
    List<CoreTerminal> results = terminalService.listarTerminales()
    results?.collect { CoreTerminal terminal ->
      Terminal.toTerminal( terminal )
    }
  }

  static List<Plan> findPlansByTerminal( String terminalId ) {
    log.info( "obteniendo planes por terminal: ${terminalId}" )
    List<CorePlan> results = planService.listarPlanesPorTerminal( terminalId )
    results?.collect { CorePlan plan ->
      Plan.toPlan( plan )
    }
  }

  static List<Payment> findPaymentsByOrderId( String orderId ) {
    log.info( "obteniendo pagos por orden id: ${orderId}" )
    List<Pago> results = pagoService.listarPagosPorIdFactura( orderId )
    if ( results?.any() ) {
      return results.collect { Pago pago ->
        Payment.toPaymment( pago )
      }
    }
    return [ ]
  }



  static List<Payment> findPaymentsToCancellByOrderId( String orderId ) {
    log.info( "obteniendo pagos por orden id: ${orderId}" )
    List<Pago> resultsTmp = pagoService.listarPagosPorIdFactura( orderId )
    List<Pago> results = new ArrayList<>()
    for(Pago pay : resultsTmp){
      if( !StringUtils.trimToEmpty(pay.idFPago).equalsIgnoreCase("C1") ){
        results.add(pay)
      }
    }
    if ( results?.any() ) {
      return results.collect { Pago pago ->
        Payment.toPaymment( pago )
      }
    }
    return [ ]
  }



  static String obtenerMensaje( String clave ){
    log.debug( 'obtenerMensaje( clave )' )
    return mensajeService.obtenerMensajePorClave( clave )
  }

  static String obtenerMensaje( MensajesPorParametro mensaje ){
    log.debug( 'obtenerMensaje( mensaje )' )
    String clave = mensaje.clave
    return mensajeService.obtenerMensajePorClave( clave )
  }


  static Boolean findTypePaymentsDollar( String formaPago ){
    log.debug( 'findTypePaymentsDollar( )' )
    return pagoService.obtenerTipoPagosDolares( formaPago )
  }


  static String findTypePaymentByIdPago( String idFormaPago ){
    log.debug( 'findTypePaymentsDollar( )' )
    TipoPago tipo = tipoPagoService.obtenerTipoPagosPorId( idFormaPago )

    return tipo.descripcion
  }


  static List<PaymentType> findActivePaymentTypesToMultypayment( ) {
    log.info( "obteniendo tipos de pago activos" )
    List<TipoPago> results = tipoPagoService.listarTiposPagoActivosMultipago()
    results?.collect { TipoPago tipoPago ->
        PaymentType.toPaymentType( tipoPago )
    }
  }


  static String findReturnTypeDev( Integer idPayment, String dataDev ){
    String type = 'EFECTIVO'
    Pago pago = pagoService.obtenerPago( idPayment )
    String typePaymentDevOri = Registry.typePaymentDev
    Boolean isTB = true
    String[] data = dataDev.split(",")
    for(String d : data){
      if( StringUtils.trimToEmpty(d).length() <= 0 ){
        isTB = false
      }
    }
    if(pago != null){
      if(TAG_FORMA_PAGO_TRANSF.equalsIgnoreCase(StringUtils.trimToEmpty(pago.idFPago)) &&
              typePaymentDevOri.contains(pago.idFormaPago)){
        type = 'ORIGINAL'
      } else if(typePaymentDevOri.contains(pago.idFPago)){
        type = 'ORIGINAL'
      } else if(TAG_FORMA_PAGO_TD.equalsIgnoreCase(StringUtils.trimToEmpty(pago.idFormaPago)) ||
              TAG_FORMA_PAGO_EF.equalsIgnoreCase(StringUtils.trimToEmpty(pago.idFormaPago))){
        if( isTB ){
          type = 'TRANSFERENCIA BANCARIA'
        } else {
          type = 'CHEQUE'
        }
      }
    }
    return type
  }


}