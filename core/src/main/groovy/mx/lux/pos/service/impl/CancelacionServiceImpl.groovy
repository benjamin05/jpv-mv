package mx.lux.pos.service.impl

import com.mysema.query.BooleanBuilder
import com.mysema.query.types.OrderSpecifier
import com.mysema.query.types.Predicate
import groovy.util.logging.Slf4j
import mx.lux.pos.model.*
import mx.lux.pos.model.JbTrack
import mx.lux.pos.repository.*
import mx.lux.pos.service.CancelacionService
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.service.InventarioService
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.TicketService
import mx.lux.pos.service.IOService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.util.CustomDateUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
import javax.swing.JOptionPane
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@Slf4j
@Service('cancelacionService')
@Transactional(readOnly = true)
class CancelacionServiceImpl implements CancelacionService {

    @Resource
    private CausaCancelacionRepository causaCancelacionRepository

    @Resource
    private NotaVentaRepository notaVentaRepository

    @Resource
    private ModeloLcRepository modeloLcRepository

    @Resource
    private NotaVentaService notaVentaService

    @Resource
    private InventarioService inventarioService

    @Resource
    private TicketService ticketService

    @Resource
    private ArticuloService articuloService

    @Resource
    private AutorizaMovRepository autorizaMovRepository

    @Resource
    private IOService ioService

    @Resource
    private PedidoLcRepository pedidoLcRepository

    @Resource
    private FormaContactoRepository formaContactoRepository

    @Resource
    private ParametroRepository parametroRepository

    @Resource
    private JbRepository jbRepository

    @Resource
    private AcuseRepository acuseRepository

    @Resource
    private JbTrackRepository jbTrackRepository

    @Resource
    private CuponMvRepository cuponMvRepository

    @Resource
    private ModificacionRepository modificacionRepository

    @Resource
    private ModificacionCanRepository modificacionCanRepository

    @Resource
    private PagoRepository pagoRepository

    @Resource
    private EmpleadoRepository empleadoRepository

    @Resource
    private PedidoLcDetRepository pedidoLcDetRepository

    @Resource
    private NotaFacturaRepository notaFacturaRepository

    @Resource
    private JbLlamadaRepository jbLlamadaRepository

    @Resource
    private DevolucionRepository devolucionRepository


    private static final TAG_JB_CANCELADA = 'CN'
    private static final TAG_NOTA_FACTURA_CANCELACION = 'cancelacion'
    private static final TAG_DETALLES_CANCELACION = 'CAN_APART'
    private static final TAG_VER_SP = 'ver_sp'
    private static final TAG_GENERICO_ARMAZON = 'A'
    private static final TAG_SURTE_PINO = 'P'
    private static final TAG_EFECTIVO = 'EF'
    private static final TAG_GENERICO_LC = 'H'
    private static final TAG_TIPO_PEDIDO = 'NC'
    private static final String TAG_GENERICOS_INVENTARIABLES = 'A,E,H'
    private static final TAG_CANCELADA = 'T'

    @Override
    List<CausaCancelacion> listarCausasCancelacion() {
        log.info("listando causas de cancelacion")
        List<CausaCancelacion> causas = causaCancelacionRepository.findByDescripcionNotNullOrderByDescripcionAsc()
        Collections.sort( causas, new Comparator<CausaCancelacion>() {
            @Override
            int compare(CausaCancelacion o1, CausaCancelacion o2) {
                return o1.id.compareTo(o2.id)
            }
        })
        log.debug("obtiene causas: ${causas*.id}")
        return causas?.any() ? causas : []
    }

    @Override
    boolean permitirCancelacionExtemporanea(String idNotaVenta) {
        log.info("determinando autorizacion para cancelacion extemporanea de notaVenta id: ${idNotaVenta}")
        if (Registry.isCancellationLimitedToSameDay()) {
            log.info('requiere cancelar dia de venta')
            NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(idNotaVenta)
            if (notaVenta?.fechaHoraFactura && DateUtils.isSameDay(new Date(), notaVenta.fechaHoraFactura)) {
                return true
            } else {
                log.debug('fechaHoraFactura es distinta al dia actual')
            }
            return false
        } else {
            log.debug('parametro cancelacion mismo dia inactivo')
        }
        return true
    }

    @Override
    @Transactional
    Modificacion registrarCancelacionDeNotaVenta(String idNotaVenta, Modificacion modificacion, String idUser, Boolean transCupones) {
        log.info("registrando cancelacion para notaVenta id: ${idNotaVenta}")
        if (StringUtils.isNotBlank(idNotaVenta)) {
            NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(idNotaVenta)
            log.debug("status: ${notaVenta?.sFactura}")
            boolean esActiva = !'T'.equalsIgnoreCase(notaVenta?.sFactura)
            if (StringUtils.isNotBlank(notaVenta?.id) && esActiva) {
                List<Pago> pagos = pagoRepository.findByIdFactura(notaVenta.id)
                pagos?.each { Pago pago ->
                    log.debug("pago id: ${pago?.id}, monto: ${pago?.monto}, por devolver: ${pago?.monto}")
                    pago?.porDevolver = pago?.monto
                }
                modificacion.idFactura = idNotaVenta
                modificacion.tipo = 'can'
                modificacion = modificacionRepository.save(modificacion)
                ModificacionCan modificacionCan = new ModificacionCan(
                        id: modificacion?.id,
                        estadoAnterior: notaVenta.sFactura
                )
                notaVenta.sFactura = 'T'
                modificacionCanRepository.save(modificacionCan)
                notaVentaRepository.save(notaVenta)
                pagoRepository.save(pagos)
                List<PedidoLc> pedidosLc = pedidoLcRepository.findById( StringUtils.trimToEmpty(notaVenta.factura) )
                if( pedidosLc.size() > 0 ){
                  InvTrRequest request = getRequest(notaVenta.detalles as List<DetalleNotaVenta>, idUser)
                  if (ServiceFactory.inventory.solicitarTransaccion( request ) == null) {
                    log.warn("no se registra el movimiento, error al registrar devolucion")
                  }
                } else if( transCupones ){
                  if (ServiceFactory.inventory.solicitarTransaccionDevolucion(notaVenta)) {
                    log.warn("Se registro el movimiento de devolucion correctamente")

                    if( seValidaSurtePino( notaVenta ) ){
                      //acuseVerSPAcuseVerSPAcuseVerSP( notaVenta )
                      /*if( !validandoEnvioPino( notaVenta.id ) ){
                        ServiceFactory.inventory.solicitarTransaccionDevolucionSP(notaVenta)
                      }*/
                    }
                  }
                } else {
                  Boolean transCanSameDay = Registry.transCanSameDay()
                  if( transCanSameDay ){
                    if (ServiceFactory.inventory.solicitarTransaccionDevolucion(notaVenta)) {
                      log.warn("Se registro el movimiento de devolucion correctamente")
                      if( seValidaSurtePino( notaVenta ) ){
                        //acuseVerSPAcuseVerSPAcuseVerSP( notaVenta )
                        /*if( !validandoEnvioPino( notaVenta.id ) ){
                          ServiceFactory.inventory.solicitarTransaccionDevolucionSP(notaVenta)
                        }*/
                      }
                    }
                  } else {
                    String orderDate = notaVenta.fechaHoraFactura.format('dd-MM-yyyy')
                    String currentDate = new Date().format('dd-MM-yyyy')
                    if(currentDate.trim().equalsIgnoreCase(orderDate.trim())){
                      if (ServiceFactory.inventory.solicitarTransaccionDevolucion(notaVenta)) {
                        log.warn("Se registro el movimiento de devolucion correctamente")
                        if( seValidaSurtePino( notaVenta ) ){
                          //acuseVerSPAcuseVerSPAcuseVerSP( notaVenta )
                          /*if( !validandoEnvioPino( notaVenta.id ) ){
                            ServiceFactory.inventory.solicitarTransaccionDevolucionSP(notaVenta)
                          }*/
                        }
                      }
                    }
                  }
                }
                return modificacion
            } else {
                log.warn('no se registra cancelacion, notaVenta invalida o no existe')
            }
        } else {
            log.warn('no se registra cancelacion, parametros invalidos')
        }
        return null
    }

    @Override
    List<Devolucion> listarDevolucionesDeNotaVenta(String idFactura) {
        log.info("listando devoluciones por idFactura ${idFactura}")
        if (StringUtils.isNotBlank(idFactura)) {
            List<Modificacion> modificaciones = modificacionRepository.findByIdFacturaOrderByFechaAsc(idFactura)
            log.debug("obtiene modificaciones: ${modificaciones*.id}")
            if (modificaciones?.any()) {
                List<Devolucion> devoluciones = devolucionRepository.findByIdModInOrderByFechaAsc(modificaciones*.id)
                log.debug("obtiene devoluciones: ${devoluciones*.id}")
                return devoluciones?.any() ? devoluciones : []
            } else {
                log.warn('no se listan devoluciones, no existen modificaciones')
            }
        } else {
            log.warn('no se listan devoluciones, parametros invalidos')
        }
        return []
    }

    @Override
    @Transactional
    List<Devolucion> registrarDevolucionesDeNotaVenta(String idNotaVenta, Map<Integer, String> devolucionesPagos, String dataDev) {
        log.info("registrando devoluciones: ${devolucionesPagos} de notaVenta id: ${idNotaVenta}")
        boolean tieneElementos = devolucionesPagos?.any() && devolucionesPagos?.keySet()?.any()
        if (StringUtils.isNotBlank(idNotaVenta) && tieneElementos) {
            List<Modificacion> mods = modificacionRepository.findByIdFacturaAndTipo(idNotaVenta, 'can')
            log.debug("modificaciones: ${mods*.id}")
            Modificacion modificacion = mods?.any() ? mods.first() : null
            log.debug("obtiene modificacion: ${modificacion?.id}")
            if (modificacion?.id) {
                List<Pago> pagos = []
                List<Devolucion> devoluciones = []
                try {
                    devolucionesPagos.each { Integer pagoId, String valor ->
                        Pago pago = pagoRepository.findOne(pagoId)
                        log.debug("obtiene pago: ${pago?.id}")
                        if (pago?.id) {
                          if( pago?.porDevolver?.doubleValue() > 0.00 ){
                            Boolean putDataDev = false
                            String formaPago = TAG_EFECTIVO
                            if ('ORIGINAL'.equalsIgnoreCase(valor)) {
                                formaPago = 'TR'.equalsIgnoreCase(pago.idFPago) ? pago.clave : pago.idFPago
                            } else if ('CHEQUE'.equalsIgnoreCase(valor)){
                              formaPago = "CH"
                              putDataDev = true
                            } else if ('TRANSFERENCIA BANCARIA'.equalsIgnoreCase(valor)){
                                formaPago = "TB"
                                putDataDev = true
                            }
                            Devolucion devolucion = new Devolucion(
                                    idMod: modificacion.id,
                                    idPago: pagoId,
                                    idFormaPago: formaPago,
                                    idBanco: pago.idBancoEmisor?.isInteger() ? pago.idBancoEmisor.toInteger() : null,
                                    monto: pago.porDevolver,
                                    tipo: 'd',
                                    devEfectivo: putDataDev ? dataDev : ""
                            )
                            log.debug("genera devolucion: ${devolucion.dump()}")
                            pago.porDevolver = 0
                            pagos.add(pago)
                            devoluciones.add(devolucion)
                          }
                        } else {
                            throw new Exception("no se encuentra el pago con id: ${pagoId}")
                        }
                    }
                    pagoRepository.save(pagos)
                    devoluciones = devolucionRepository.save(devoluciones)
                    log.debug("devoluciones registradas: ${devoluciones*.id}")
                    return devoluciones
                } catch (ex) {
                    log.warn("no se registran devoluciones, ${ex.message}")
                }
            } else {
                log.warn('no se registran devoluciones, notaVenta sin cancelacion')
            }
        } else {
          NotaVenta notaVenta = notaVentaRepository.findOne( idNotaVenta )
          if( notaVenta != null && notaVenta.ventaNeta.compareTo(BigDecimal.ZERO) <= 0 ){
            List<Devolucion> lstDev = new ArrayList<>()
            Devolucion dev = new Devolucion()
            dev.id = 0
            lstDev.add( dev )
            return lstDev
          } else {
            log.warn('no se registran devoluciones, parametros invalidos')
          }
        }
        return []
    }

    @Override
    @Transactional
    List<Pago> registrarTransferenciasParaNotaVenta(String idOrigen, String idDestino, Map<Integer, BigDecimal> transferenciasPagos) {
        log.info("registrando transferencias: ${transferenciasPagos} de notaVenta origen: ${idOrigen}, destino: ${idDestino}")
        boolean tieneElementos = transferenciasPagos?.any() && transferenciasPagos?.keySet()?.any()
        if (StringUtils.isNotBlank(idOrigen) && StringUtils.isNotBlank(idDestino) && tieneElementos) {
            NotaVenta destino = notaVentaService.obtenerNotaVenta(idDestino)
            List<Modificacion> mods = modificacionRepository.findByIdFacturaAndTipo(idOrigen, 'can')
            log.debug("modificaciones: ${mods*.id}")
            Modificacion modificacion = mods?.any() ? mods.first() : null
            log.debug("obtiene modificacion: ${modificacion?.id}")
            if (StringUtils.isNotBlank(destino?.id) && modificacion?.id) {
                List<Pago> pagos = []
                List<Pago> transferencias = []
                List<Devolucion> devoluciones = []
                Date fechaActual = new Date()
                try {
                    transferenciasPagos.each { Integer pagoId, BigDecimal valor ->
                        Pago pago = pagoRepository.findOne(pagoId)
                        log.debug("obtiene pago: ${pago?.id}")
                        if (pago?.id && valor) {
                            pago.porDevolver -= valor
                            Pago transferencia = new Pago(
                                    idFactura: idDestino,
                                    idFormaPago: pago.idFPago,
                                    referenciaPago: idOrigen,
                                    monto: valor,
                                    idEmpleado: destino.idEmpleado,
                                    idSucursal: pago.idSucursal,
                                    idFPago: 'TR',
                                    clave: pago.idFPago,
                                    referenciaClave: "${idOrigen}:${pagoId}",
                                    idSync: '2',
                                    tipoPago: DateUtils.isSameDay(destino.fechaHoraFactura ?: fechaActual, fechaActual) ? 'a' : 'l',
                                    idBancoEmisor: pago.idBancoEmisor,
                                    idTerminal: pago.idTerminal,
                                    idPlan: pago.idPlan
                            )
                            log.debug("genera transferencia: ${transferencia.dump()}")
                            Devolucion devolucion = new Devolucion(
                                    idMod: modificacion.id,
                                    idPago: pagoId,
                                    idFormaPago: pago.idFPago,
                                    idBanco: pago.idBancoEmisor?.isInteger() ? pago.idBancoEmisor.toInteger() : null,
                                    monto: valor,
                                    tipo: 't',
                                    transf: idDestino
                            )
                            log.debug("genera devolucion: ${devolucion.dump()}")
                            pagos.add(pago)
                            transferencias.add(transferencia)
                            devoluciones.add(devolucion)
                        } else {
                            throw new Exception("no se encuentra el pago con id: ${pagoId} o el monto es invalido: ${valor}")
                        }
                    }
                    pagoRepository.save(pagos)
                    transferencias = pagoRepository.save(transferencias)
                    log.debug("transferencias de pago registradas: ${transferencias*.id}")
                    devoluciones.each { Devolucion dev ->
                        Pago pago = transferencias.find { Pago tmp ->
                            tmp?.referenciaClave?.equalsIgnoreCase("${idOrigen}:${dev?.idPago}")
                        }
                        if (pago?.id) {
                            dev.referencia = pago.id
                        } else {
                            throw new Exception("no se encuentra transferencia con pago origen id: ${dev.idPago}")
                        }
                      if( dev.devEfectivo == null ){
                        dev.devEfectivo = ''
                      }
                    }
                    devoluciones = devolucionRepository.save(devoluciones)
                    log.debug("devoluciones registradas: ${devoluciones*.id}")
                    notaVentaService.registrarNotaVenta(destino)
                    return transferencias
                } catch (ex) {
                    log.warn("no se registran transferencias, ${ex.message}")
                }
            } else {
                log.warn('no se registran transferencias, notaVenta origen y/o destino invalidas, y/o sin cancelacion')
            }
        } else {
            log.warn('no se registran transferencias, parametros invalidos')
        }
        return []
    }

    @Override
    List<NotaVenta> listarNotasVentaOrigenDeNotaVenta(String idNotaVenta) {
        log.info("obteniendo notaVenta origen de notaVenta id: ${idNotaVenta}")
        if (StringUtils.isNotBlank(idNotaVenta)) {
            BooleanBuilder builder = new BooleanBuilder(QPago.pago.idFPago.eq('TR'))
            builder.and(QPago.pago.idFactura.eq(idNotaVenta))
            List<Pago> transferencias = pagoRepository.findAll(builder) as List<Pago>
            log.debug("obtiene pagos tipo transferencia: ${transferencias*.id}")
            if (transferencias?.any()) {
                Predicate predicate = QNotaVenta.notaVenta.id.in(transferencias*.referenciaPago)
                OrderSpecifier order = QNotaVenta.notaVenta.fechaHoraFactura.asc()
                List<NotaVenta> notas = notaVentaRepository.findAll(predicate, order) as List<NotaVenta>
                log.debug("obtiene notas origen: ${notas*.id}")
                return notas?.any() ? notas : []
            } else {
                log.warn('no se obtiene notasVenta origen, notaVenta no ha recibido transferencias')
            }
        } else {
            log.warn('no se obtienen notasVenta origen, parametros invalidos')
        }
        return []
    }

    @Override
    BigDecimal obtenerCreditoDeNotaVenta(String idNotaVenta) {
        log.info("obteniendo credito de notaVenta id: ${idNotaVenta}")
        if (StringUtils.isNotBlank(idNotaVenta)) {
            List<Modificacion> mods = modificacionRepository.findByIdFacturaAndTipo(idNotaVenta, 'can')
            log.debug("modificaciones: ${mods*.id}")
            Modificacion modificacion = mods?.any() ? mods.first() : null
            log.debug("obtiene modificacion: ${modificacion?.id}")
            if (modificacion?.id) {
                BigDecimal porDevolver = BigDecimal.ZERO
                List<Pago> pagos = pagoRepository.findByIdFactura(idNotaVenta) ?: []
                pagos?.each { Pago pmt ->
                    porDevolver += pmt?.porDevolver ?: BigDecimal.ZERO
                }
                log.debug("obtiene credito: ${porDevolver}")
                return porDevolver
            } else {
                log.warn('no se obtiene credito de notaVenta, notaVenta sin cancelacion')
            }
        } else {
            log.warn('no se obtiene credito de notaVenta, parametros invalidos')
        }
        return null
    }


    @Override
    Boolean validandoTransferencia(String idNotaVenta) {
        Boolean transfer = true
        List<Pago> lstPagos = pagoRepository.findByReferenciaPago(idNotaVenta)
        List<Pago> lstPagosTransf = pagoRepository.findByIdFactura(idNotaVenta)
        BigDecimal sumaPagos = BigDecimal.ZERO
        BigDecimal sumaPagosTransf = BigDecimal.ZERO
        for (Pago pagoTransf : lstPagosTransf) {
            sumaPagosTransf = sumaPagosTransf.add(pagoTransf.monto)
        }
        for (Pago pago : lstPagos) {
            sumaPagos = sumaPagos.add(pago.monto)
        }
        if ( ( sumaPagos < sumaPagosTransf ) ) {
            transfer = false
        }
        return transfer
    }

    @Override
    @Transactional
    void restablecerValoresDeCancelacion(String idNotaVenta) {
        log.info("restableciendo valores de cancelacion")
      QPago payment = QPago.pago
        List<Pago> lstPagos = pagoRepository.findByReferenciaPago(idNotaVenta)
        List<Pago> lstPagosTransf = pagoRepository.findByIdFactura(idNotaVenta)
        BigDecimal sumaPagos = BigDecimal.ZERO
        BigDecimal sumaPagosTransf = BigDecimal.ZERO
        for (Pago pagoTransf : lstPagosTransf) {
            sumaPagosTransf = sumaPagosTransf.add(pagoTransf.monto)
        }
        for (Pago pago : lstPagos) {
            sumaPagos = sumaPagos.add(pago.monto)
        }
        for( Pago pagoTransf : lstPagos ){
          if( StringUtils.trimToEmpty( pagoTransf.notaVenta?.factura ).isEmpty() && !StringUtils.trimToEmpty(pagoTransf.referenciaClave).isEmpty() ){
            String[] idPagoTransf = pagoTransf.referenciaClave.split( ':' )
            Pago pagoFuente = pagoRepository.findOne( Integer.parseInt( idPagoTransf[1].trim() ) )
            QDevolucion dev = QDevolucion.devolucion
            Devolucion devolucion = devolucionRepository.findOne( dev.idPago.eq(pagoFuente.getId()).and(dev.monto.eq(pagoTransf.monto)).
                and(dev.transf.eq(pagoTransf.idFactura)))
            if( devolucion != null ){
              BigDecimal montoTotal = pagoTransf.monto.add( pagoFuente.porDevolver )
              devolucionRepository.delete( devolucion.id )
              pagoFuente.setPorDevolver( montoTotal )
              pagoRepository.save( pagoFuente )
              pagoRepository.flush()
            }
          }
        }
    }


    @Override
    @Transactional
    void restablecerMontoAlBorrarPago( Integer idPago ) {
        Pago pago = pagoRepository.findOne( idPago )
        if( pago != null && !StringUtils.trimToEmpty(pago.referenciaClave).isEmpty() ){
            String[] idPagoTransf = pago.referenciaClave.split( ':' )
          if( idPagoTransf.length > 1 ){
            Pago pagoDeTransf = pagoRepository.findOne( Integer.parseInt( idPagoTransf[1].trim() ) )
            BigDecimal porDevolver = pago.monto.add(pagoDeTransf.porDevolver)
            pagoDeTransf.setPorDevolver( porDevolver )
            QDevolucion dev = QDevolucion.devolucion
            Devolucion devolucion = devolucionRepository.findOne( dev.idPago.eq(pagoDeTransf.getId()).and(dev.monto.eq(pago.monto)).
                and(dev.transf.eq(pago.idFactura)))
            devolucionRepository.delete( devolucion.id )
            pagoRepository.save( pagoDeTransf )
            pagoRepository.flush()
          }
        }
    }



    @Override
    Boolean validandoEnvioPino( String idOrder ){
      Boolean surtioPino = false
      NotaVenta nota = notaVentaRepository.findOne( idOrder.trim() )
      String urlValida = Registry.URLValidSP
      String contenido = "id_suc=${nota.idSucursal}&factura=${nota.factura}&id_acuse=${nota.factura}"

      ExecutorService executor = Executors.newFixedThreadPool(1)
      String respuesta = ""
      int timeoutSecs = 20
      final Future<?> future = executor.submit(new Runnable() {
          public void run() {
              try{
                  URL url = "${urlValida}?${contenido}".toURL()
                  println url.text
                  respuesta = url.text?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
                  println "Respuesta pino surtido: "+respuesta
                  String[] valores = respuesta.split(/\|/)
                  if(valores.length >=2){
                      if(!valores[1].toString().trim().contains('0')){
                          surtioPino = true
                      }
                  }
              } catch (Exception e){
                  println( e )
              }
            }
        })
        try {
            future.get(timeoutSecs, TimeUnit.SECONDS)
        } catch (Exception e) {
            future.cancel(true)
            respuesta = ''
            log.warn("encountered problem while doing some work", e)
        }
      if( StringUtils.trimToEmpty(respuesta).length() <= 0 ){
        surtioPino = true
      }
      return surtioPino
    }


    @Override
    @Transactional
    Jb actualizaJb( String idFactura ){
      log.debug( 'actualizaJb( )' )
      NotaVenta nota = notaVentaRepository.findOne( idFactura )
      Jb jb = jbRepository.findOne( nota != null ? nota.factura.trim(): '' )
      if( jb != null ){
        jb.estado = TAG_JB_CANCELADA
        jbRepository.save( jb )
        jbRepository.flush()
      }
      return jb
    }


  @Override
  CausaCancelacion causaCancelacion( Integer id ) {
   log.info("cancelacion con id: ${id}")
    CausaCancelacion causa = causaCancelacionRepository.findOne( id )
    log.debug("obtiene causa: ${causa?.descripcion}")
    return causa != null ? causa : null
  }



    @Override
    @Transactional
    JbTrack insertaJbTrack( String idFactura ){
      log.debug( 'actualizaJbTrack( )' )
      NotaVenta nota = notaVentaRepository.findOne( idFactura.trim() )
      List<Modificacion> lstModificaciones = new ArrayList<Modificacion>()
      if( nota != null ){
        lstModificaciones = modificacionRepository.findByIdFactura( nota.id )
      }

      JbTrack jbTrack = new JbTrack()
      String factura = nota.factura.replaceFirst("^0*", "")
      jbTrack.rx = factura.trim()
      jbTrack.estado = TAG_JB_CANCELADA
      jbTrack.emp = lstModificaciones.size() > 0 ? lstModificaciones.first().idEmpleado : ''
      jbTrack.obs = lstModificaciones.first().causa.trim()
      jbTrack.id_mod = '0'
      jbTrack.id_viaje = null
      jbTrack.fecha = new Date()

      jbTrackRepository.save( jbTrack )
      jbTrackRepository.flush()
      return jbTrack
    }


    @Override
    @Transactional
    void eliminaJbLlamada( String idFactura ){
      log.debug( 'eliminaJbLlamada( )' )
      NotaVenta nota = notaVentaRepository.findOne( idFactura )
      QJbLlamada llamada = QJbLlamada.jbLlamada
      JbLlamada jbLlamada = jbLlamadaRepository.findOne( llamada.rx.eq(nota != null ? nota.factura : '') )
      if(jbLlamada != null ){
        jbLlamadaRepository.delete( jbLlamada.rx )
      }
    }


  @Override
  void generaAcuses( String idFactura ){
    log.debug( "generaAcuses()" )
    Parametro idSuc = parametroRepository.findOne( TipoParametro.ID_SUCURSAL.value )
    NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
        Acuse acuse = new Acuse()
        acuse.idTipo = TAG_NOTA_FACTURA_CANCELACION
        try {
            acuse = acuseRepository.saveAndFlush( acuse )
            log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
        } catch ( Exception e ) {
            log.error( e.getMessage() )
        }
        acuse.contenido = String.format( 'fechaVal=%s|', CustomDateUtils.format(new Date(), 'ddMMyyyy') )
        acuse.contenido += String.format( 'id_acuseVal=%s|', String.format( '%d', acuse.id ) )
        acuse.contenido += String.format( 'id_facturaVal=%s|', notaVenta.factura.trim() )
        acuse.contenido += String.format( 'id_sucVal=%s|', String.format( '%d', notaVenta.idSucursal ) )
        acuse.contenido += String.format( 'no_soiVal=%s|', notaVenta.id.trim() )
        acuse.fechaCarga = new Date()
        try {
            acuse = acuseRepository.saveAndFlush( acuse )
            log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
        } catch ( Exception e ) {
            log.error( e.getMessage() )
        }

    Boolean acuseCanApart = false
    /*for(DetalleNotaVenta det : notaVenta.detalles){
      if( TAG_GENERICO_ARMAZON.equalsIgnoreCase(det.articulo.idGenerico.trim()) ){
        if( TAG_SURTE_PINO.equalsIgnoreCase(det.surte.trim()) ){
          if( det.notaVenta.fechaEntrega == null ){
              Acuse acuse1 = new Acuse()
              acuse1.idTipo = TAG_DETALLES_CANCELACION
              try {
                  acuse1 = acuseRepository.saveAndFlush( acuse1 )
                  log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse1.id, acuse1.idTipo, acuse1.contenido ) )
              } catch ( Exception e ) {
                  log.error( e.getMessage() )
              }
              acuse1.contenido = String.format( 'id_sucVal=%s|', String.format( '%d', notaVenta.idSucursal ) )
              acuse1.contenido += String.format( 'facturaVal=%s|', notaVenta.factura.trim() )
              acuse1.contenido += String.format( 'id_acuseVal=%s|', String.format( '%d', acuse1.id ) )
              acuse1.fechaCarga = new Date()
              try {
                  acuse1 = acuseRepository.saveAndFlush( acuse1 )
                  log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse1.id, acuse1.idTipo, acuse1.contenido ) )
              } catch ( Exception e ) {
                  log.error( e.getMessage() )
              }
          }
        }
      }
    }*/

  }

  @Override
  @Transactional
  void actualizaGrupo( String idFactura, String trans ){
      NotaVenta nota = notaVentaRepository.findOne( idFactura )
      Jb trabajo = jbRepository.findOne(nota.factura)
      if( trabajo?.id_grupo?.length() > 0 ){
          QJb jbq = QJb.jb
          List<Jb> grupo = jbRepository.findAll( jbq.id_grupo.eq(trabajo.id_grupo) )
          if(grupo.size() > 1){
              Integer noEntCant = 0
              for(Jb jbTmp : grupo){
                  if(!jbTmp.estado.trim().equalsIgnoreCase('TE') && !jbTmp.estado.trim().equalsIgnoreCase('CN')){
                      noEntCant = noEntCant+1
                  }
              }
              if(noEntCant == 0){
                  Jb jbGrupo = jbRepository.findOne( trabajo?.id_grupo )
                  if(jbGrupo != null){
                      jbGrupo.estado = trans.equalsIgnoreCase('E') ? 'TE' : 'CN'
                      jbRepository.saveAndFlush( jbGrupo )
                  }
                  JbLlamada llamada = jbLlamadaRepository.findOne( trabajo?.id_grupo )
                  if(llamada != null){
                      jbLlamadaRepository.delete(llamada.rx)
                  }
              }
          }
       }
  }


  @Override
  @Transactional
  void actualizaJbCancelado( String idFactura, String idEmpleado ){
    NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
    if( notaVenta != null ){
      Jb jb = jbRepository.findOne( StringUtils.trimToEmpty(notaVenta.factura) )
      if( jb != null ){
        jb.estado = 'CN'
        jb.emp_atendio = idEmpleado
        jb = jbRepository.save( jb )
        jbRepository.flush()
        JbTrack jbTrack = new JbTrack()
        jbTrack.rx = jb.rx
        jbTrack.estado = 'CN'
        jbTrack.emp = idEmpleado
        jbTrack.fecha = new Date()
        jbTrack.id_mod = '0'
        jbTrackRepository.save( jbTrack )
        jbTrackRepository.flush()
      }
    }
  }



  @Override
  Boolean cancMismoDia( String idOrder ){
    Boolean canMismoDia = false
    SimpleDateFormat df = new SimpleDateFormat( "dd-MM-yyyy" )
    NotaVenta notaVenta = notaVentaRepository.findOne( idOrder )
    if( notaVenta != null &&
            StringUtils.trimToEmpty(df.format(notaVenta.fechaHoraFactura)).equalsIgnoreCase(StringUtils.trimToEmpty(df.format(new Date()))) ){
      canMismoDia = true
    }
    return canMismoDia
  }


  @Override
  void cancelarCupones( String idOrder ){
    NotaVenta notaVenta = notaVentaRepository.findOne( StringUtils.trimToEmpty(idOrder) )
    if( notaVenta != null ){
      QCuponMv qCuponMv = QCuponMv.cuponMv
      List<CuponMv> lstCupones = cuponMvRepository.findAll( qCuponMv.facturaOrigen.eq(StringUtils.trimToEmpty(notaVenta.factura)) )
      if( lstCupones.size() > 0 ){
        Boolean cuponesIntegros = true
        for(CuponMv cuponMv : lstCupones){
          if(cuponMv.fechaAplicacion != null){
            cuponesIntegros = false
          }
        }
        if( cuponesIntegros ){
          for(CuponMv cuponMv : lstCupones){
            cuponMv.fechaAplicacion = new Date()
            cuponMvRepository.save( cuponMv )
          }
          cuponMvRepository.flush()
        }
      }
    }
  }


  @Override
  void enviaCancelaccionPedidoLc( String factura ){
    log.debug( "enviaCancelaccionPedidoLc( )" )
    PedidoLc pedidoLc = pedidoLcRepository.findOne( factura )
    String url = Registry.urlCancelationOrderLc
    if ( org.apache.commons.lang.StringUtils.trimToNull( url ) != null && pedidoLc != null
            && StringUtils.trimToEmpty(pedidoLc.folio).length() > 0 ) {
      SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy")
      url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s|%s', pedidoLc.folio, df.format(new Date()) ), 'UTF-8' ) )
      println "Url para cancelacion de pedido LC: "+url
      try {
        String response = url.toURL().text
        response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
        println "Respuesta llamada web cancelacion de pedido LC: "+response
      } catch ( Exception e ) { println( e ) }
    }
  }


  @Override
  Boolean enviaTransferenciaPedidoLc( String factura, String idFactura ){
    log.debug( "enviaTransferenciaPedidoLc( )" )
    Boolean succes = true
    PedidoLc pedidoLc = pedidoLcRepository.findOne( factura )
    NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
    if( pedidoLc != null && notaVenta != null ){
      PedidoLc newPedidoLc = pedidoLcRepository.findOne( StringUtils.trimToEmpty(notaVenta.factura) )
      if( newPedidoLc == null ){
        newPedidoLc = new PedidoLc()
      }
      newPedidoLc.id = StringUtils.trimToEmpty(notaVenta.factura)
      newPedidoLc.folio = pedidoLc.folio
      newPedidoLc.cliente = pedidoLc.cliente
      newPedidoLc.sucursal = pedidoLc.sucursal
      newPedidoLc.fechaAlta = pedidoLc.fechaAlta
      newPedidoLc.fechaRecepcion = pedidoLc.fechaRecepcion
      newPedidoLc.fechaAcuse = pedidoLc.fechaAcuse
      newPedidoLc.fechaEnvio = pedidoLc.fechaEnvio
      newPedidoLc.fechaEntrega = pedidoLc.fechaEntrega
      pedidoLcRepository.save( newPedidoLc )
      pedidoLcRepository.flush()
      pedidoLcRepository.delete(pedidoLc.id)
      pedidoLcRepository.flush()
      QPedidoLcDet qPedidoLcDet = QPedidoLcDet.pedidoLcDet
      List<PedidoLcDet> lstDet = pedidoLcDetRepository.findAll( qPedidoLcDet.id.eq(StringUtils.trimToEmpty(notaVenta.factura)) )
      for(PedidoLcDet pedidoLcDet1 : lstDet){
        pedidoLcDetRepository.delete( pedidoLcDet1.numReg )
        pedidoLcDetRepository.flush()
      }
      for(PedidoLcDet pedidoLcDet : pedidoLc.pedidoLcDets){
        pedidoLcDet.id = StringUtils.trimToEmpty(notaVenta.factura)
        pedidoLcDetRepository.save(pedidoLcDet)
        pedidoLcDetRepository.flush()
      }
      String url = Registry.urlReuseOrderLc
      if ( StringUtils.trimToNull( url ) != null && pedidoLc != null ) {
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy")
        url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s|%s', pedidoLc.folio, StringUtils.trimToEmpty(notaVenta.factura) ), 'UTF-8' ) )
        println "Url generada: "+url
        try {
          String response = url.toURL().text
          response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
          println "respuesta reuso pedido LC: "+response
        } catch ( Exception e ) {
          println( e )
        }
      }
    } else {
      succes = false
    }
    return succes
  }


  @Override
  @Transactional
  CuponMv liberaCupon( String idFactura ){
    CuponMv cuponMv = null
    NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
    if( notaVenta != null ){
      QCuponMv qCuponMv = QCuponMv.cuponMv
      cuponMv = cuponMvRepository.findOne( qCuponMv.facturaDestino.eq(notaVenta.factura) )
      if( cuponMv != null ){
        cuponMv.facturaDestino = ""
        cuponMv.fechaAplicacion = null
        cuponMv.fechaVigencia = new Date()
        cuponMvRepository.save( cuponMv )
        cuponMvRepository.flush()
      }
    }
    return cuponMv
  }



    static InvTrRequest getRequest( List<DetalleNotaVenta> detalleNotaVenta, String idUser ) {
        InvTrRequest request = new InvTrRequest()
        request.effDate = new Date()
        request.idUser = idUser
        request.remarks = ""
        request.reference = StringUtils.trimToEmpty( detalleNotaVenta.first().idFactura )
        request.siteTo = null
        request.trType = "DEVOLUCION"
        for (DetalleNotaVenta det : detalleNotaVenta) {
          /*if( TAG_GENERICO_LC.equalsIgnoreCase(StringUtils.trimToEmpty(det.articulo.idGenerico))
                  && TAG_TIPO_PEDIDO.equalsIgnoreCase(StringUtils.trimToEmpty(det.articulo.tipo)) ){*/
            request.skuList.add( new InvTrDetRequest( det.idArticulo, det.cantidadFac.intValue() ) )
          //}
        }
        return request
    }

  @Override
  @Transactional
  List<String> reasignarCupones( String idFactura, List<Jb> lstJbs, List<JbTrack> lstJbTracks ){
    List<String> lstNuevasNotas = new ArrayList<>()
    NotaVenta notaVentaOri = notaVentaRepository.findOne( idFactura )
    List<String> lstFacturasDest = new ArrayList<>()
    if( notaVentaOri != null && StringUtils.trimToEmpty(notaVentaOri.factura).length() > 0 ){
      QCuponMv qCuponMv = QCuponMv.cuponMv
      List<CuponMv> cuponesMv = cuponMvRepository.findAll( qCuponMv.facturaOrigen.eq(notaVentaOri.factura.trim()).
              and(qCuponMv.facturaDestino.isNotNull().and(qCuponMv.facturaDestino.isNotEmpty().
                      and(qCuponMv.fechaAplicacion.isNotNull()))) ) as List<CuponMv>
      BigDecimal montoTotalPagosOri = BigDecimal.ZERO
      BigDecimal montoTransf = BigDecimal.ZERO
      for(Pago pagoOri : notaVentaOri.pagos){
        montoTotalPagosOri = montoTotalPagosOri.add(pagoOri.monto)
      }
      for(CuponMv cuponMv : cuponesMv){
        List<NotaVenta> antiguaNotaVenta = notaVentaRepository.findByFactura(StringUtils.trimToEmpty(cuponMv.facturaDestino))
        if( antiguaNotaVenta != null ){
          NotaVenta nuevaNotaVenta = notaVentaService.abrirNotaVenta(
                  StringUtils.trimToEmpty(antiguaNotaVenta.first().idCliente.toString()),
                  StringUtils.trimToEmpty(antiguaNotaVenta.first().idEmpleado))
          for(DetalleNotaVenta det : antiguaNotaVenta.first().detalles){
            agregaArticuloReasigCupon( nuevaNotaVenta, det.articulo, det.surte, antiguaNotaVenta.first(), det )
          }
          List<Pago> lstPagos = new ArrayList<>()
          lstPagos.addAll( antiguaNotaVenta.first().pagos as List<Pago> )
          insertaTransfReasignCupon(antiguaNotaVenta.first().id, nuevaNotaVenta.id, lstPagos, BigDecimal.ZERO)
          Pago pagoCupon = new Pago()
          Pago pagoCupon1 = new Pago()
          List<Pago> lstPagosTmp = new ArrayList<>()
          lstPagosTmp.addAll(notaVentaOri.pagos as List<Pago>)
          Collections.sort(lstPagosTmp, new Comparator<Pago>() {
              @Override
              int compare(Pago o1, Pago o2) {
                  return o2.monto.compareTo(o1.monto)
              }
          })
          for(Pago pagoTmp : lstPagosTmp){
            if(pagoTmp.porDevolver.compareTo(BigDecimal.ZERO) > 0){
              if( pagoCupon.id == null ){
                pagoCupon = pagoTmp
              } else {
                pagoCupon1 = pagoTmp
              }
            }
          }
          montoTransf = cuponMv.montoCupon
          montoTotalPagosOri = cuponMv.montoCupon.compareTo(montoTotalPagosOri) <= 0 ? montoTotalPagosOri.subtract(cuponMv.montoCupon) : BigDecimal.ZERO
          if( pagoCupon.porDevolver != null && pagoCupon.porDevolver.compareTo(BigDecimal.ZERO) > 0 ){
            if( montoTransf.compareTo(pagoCupon.porDevolver) <= 0 ){
              lstPagos = new ArrayList<>()
              lstPagos.add(pagoCupon)
              if( montoTransf.doubleValue() > (nuevaNotaVenta.ventaTotal.doubleValue()-nuevaNotaVenta.sumaPagos) ){
                montoTransf = nuevaNotaVenta.ventaTotal.doubleValue()-nuevaNotaVenta.sumaPagos
              }
              insertaTransfReasignCupon(notaVentaOri.id, nuevaNotaVenta.id, lstPagos, montoTransf)
            } else {
              BigDecimal diferencia = montoTransf.subtract(pagoCupon.porDevolver)
              lstPagos = new ArrayList<>()
              lstPagos.add(pagoCupon)
              insertaTransfReasignCupon(notaVentaOri.id, nuevaNotaVenta.id, lstPagos, pagoCupon.porDevolver)
              lstPagos = new ArrayList<>()
              lstPagos.add(pagoCupon1)
              insertaTransfReasignCupon(notaVentaOri.id, nuevaNotaVenta.id, lstPagos, diferencia)
            }
          }
          nuevaNotaVenta.codigo_lente = antiguaNotaVenta.first().codigo_lente
          nuevaNotaVenta.receta = antiguaNotaVenta.first().receta
          nuevaNotaVenta.udf2 = antiguaNotaVenta.first().udf2
          nuevaNotaVenta.udf4 = antiguaNotaVenta.first().udf4
          nuevaNotaVenta.observacionesNv = antiguaNotaVenta.first().observacionesNv
          nuevaNotaVenta = notaVentaService.cerrarNotaVenta(nuevaNotaVenta)

          QFormaContacto qFormaContacto = QFormaContacto.formaContacto
          List<FormaContacto> lstFormasContacto = formaContactoRepository.findAll( qFormaContacto.rx.eq(StringUtils.trimToEmpty(antiguaNotaVenta.first().factura)) ) as List<FormaContacto>
          for(FormaContacto formaContacto : lstFormasContacto){
            FormaContacto newFormaContacto = new FormaContacto()
            newFormaContacto.rx = StringUtils.trimToEmpty(nuevaNotaVenta.factura)
            newFormaContacto.id_cliente = formaContacto.id_cliente
            newFormaContacto.id_tipo_contacto = formaContacto.id_tipo_contacto
            newFormaContacto.contacto = formaContacto.contacto
            newFormaContacto.observaciones = formaContacto.observaciones
            newFormaContacto.id_sucursal = formaContacto.id_sucursal
            formaContactoRepository.save( newFormaContacto )
            formaContactoRepository.flush()
          }
          for( Jb jb : lstJbs ){
            if( StringUtils.trimToEmpty(jb.rx).equalsIgnoreCase(StringUtils.trimToEmpty(antiguaNotaVenta.first().factura)) ){
              Jb nuevoJb = jb
              nuevoJb.rx = StringUtils.trimToEmpty(nuevaNotaVenta.factura)
              nuevoJb.fecha_venta = nuevaNotaVenta.fechaHoraFactura
              jbRepository.save( nuevoJb )
              jbRepository.flush()
            }
          }
          for(JbTrack jbTrack : lstJbTracks){
            if( jbTrack != null &&
                    StringUtils.trimToEmpty(jbTrack.rx).equalsIgnoreCase(StringUtils.trimToEmpty(antiguaNotaVenta.first().factura)) ){
              JbTrack nuevoJbTrack = jbTrack
              nuevoJbTrack.rx = StringUtils.trimToEmpty(nuevaNotaVenta.factura)
              nuevoJbTrack.estado = jbTrack.estado
              nuevoJbTrack.obs = jbTrack.obs
              nuevoJbTrack.emp = jbTrack.emp
              nuevoJbTrack.id_viaje = jbTrack.id_viaje != null ? jbTrack.id_viaje : ""
              nuevoJbTrack.fecha = jbTrack.fecha
              nuevoJbTrack.id_mod = jbTrack.id_mod
              nuevoJbTrack.id_jbtrack = null
              jbRepository.save( nuevoJbTrack )
              jbRepository.flush()
            }
          }
          lstNuevasNotas.add( StringUtils.trimToEmpty(nuevaNotaVenta.id) )
        }
      }
    } else {
      println "No existe notaVenta origen con id ${idFactura}"
    }
    return lstNuevasNotas
  }


  @Override
  List<NotaVenta> tieneCuponesAplicados( String idFactura ){
    List<NotaVenta> notasdeCupones = new ArrayList<>()
    NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
    if( notaVenta != null && StringUtils.trimToEmpty(notaVenta.factura).length() > 0 ){
      QCuponMv qCuponMv = QCuponMv.cuponMv
      List<CuponMv> cuponesMv = cuponMvRepository.findAll( qCuponMv.facturaOrigen.eq(notaVenta.factura.trim()).
              and(qCuponMv.facturaDestino.isNotNull().and(qCuponMv.facturaDestino.isNotEmpty().
                      and(qCuponMv.fechaAplicacion.isNotNull()))) ) as List<CuponMv>
      for(CuponMv cupones : cuponesMv){
        String ticket = StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+StringUtils.trimToEmpty(cupones.facturaDestino)
        NotaVenta facturaDestino = notaVentaService.obtenerNotaVentaPorTicket( ticket )
        if( !facturaDestino.sFactura.equalsIgnoreCase(TAG_CANCELADA) ){
        notasdeCupones.add( notaVentaService.obtenerNotaVentaPorTicket(StringUtils.trimToEmpty(Registry.currentSite+"-"+cupones.facturaDestino)) )
        }
      }
    }
    return notasdeCupones
  }



  @Override
  @Transactional
  void agregaArticuloReasigCupon(NotaVenta nota, Articulo item, String surte, NotaVenta notaOrig, DetalleNotaVenta detalleNotaVenta){
      if (item?.id) {
        DetalleNotaVenta detalle = null
        if(!TAG_GENERICOS_INVENTARIABLES.contains(item.idGenerico)){
          surte = ' '
        }
        BigDecimal precioUnitLista = BigDecimal.ZERO
        for(DetalleNotaVenta det : notaOrig.detalles){
          if( item.id == det.idArticulo ){
            precioUnitLista = det.precioUnitLista
          }
        }
        List<ModeloLc> lstModelos = modeloLcRepository.findAll()
        for( ModeloLc modeloLc : lstModelos ){
          if( StringUtils.trimToEmpty(modeloLc.id).equalsIgnoreCase(StringUtils.trimToEmpty(item.articulo)) ){
            surte = 'S'
          }
        }
        detalle = new DetalleNotaVenta(
            idArticulo: item.id,
            cantidadFac: detalleNotaVenta.cantidadFac,
            precioUnitLista: precioUnitLista,
            precioUnitFinal: precioUnitLista,
            precioCalcLista: precioUnitLista,
            precioFactura: precioUnitLista,
            precioCalcOferta: detalleNotaVenta.precioCalcOferta,
            precioConv: detalleNotaVenta.precioConv,
            idTipoDetalle: StringUtils.trimToEmpty(detalleNotaVenta.idTipoDetalle),
            surte: StringUtils.trimToEmpty(surte).equalsIgnoreCase("P") ? "S" : surte
        )
          if (detalle != null) {
              nota = notaVentaService.registrarDetalleNotaVentaEnNotaVentaReasignCupon(nota.id, detalle)
          }
          if (nota != null ) {
              notaVentaService.registraImpuestoPorFactura( nota )
          }
          nota.observacionesNv = StringUtils.trimToEmpty(nota.observacionesNv)
      } else {
          log.warn("no se agrega articulo, parametros invalidos")
      }
  }


  @Override
  @Transactional
  void insertaTransfReasignCupon( String fromOrderId, String toOrderId, List<Pago> lstPagos, BigDecimal monto ){
      Map<Integer, BigDecimal> creditTransfers = [ : ]
      lstPagos.each { Pago pmt ->
          creditTransfers.put( pmt?.id, monto.compareTo(BigDecimal.ZERO) > 0 ? monto : pmt?.monto ?: BigDecimal.ZERO )
      }
      if ( StringUtils.isNotBlank( fromOrderId ) && StringUtils.isNotBlank( toOrderId ) ) {
        Map<Integer, BigDecimal> creditTransfers2 = new HashMap<Integer, BigDecimal>()
        creditTransfers2.putAll( creditTransfers )
        creditTransfers2?.each { Integer pagoId, BigDecimal valor ->
          if ( !valor ) {
            creditTransfers.remove( pagoId )
          }
        }
        List<Pago> results = registrarTransferenciasParaNotaVenta( fromOrderId, toOrderId, creditTransfers )
      }
  }




  @Override
  @Transactional
  Integer salidaLentesContacto(String idFactura, String idUser){
    Integer idTrans = null
    NotaVenta notaVenta = notaVentaRepository.findOne( StringUtils.trimToEmpty(idFactura) )
      List<PedidoLc> pedidosLc = pedidoLcRepository.findById( StringUtils.trimToEmpty(notaVenta.factura) )
      if( pedidosLc.size() > 0 ){
          InvTrRequest request = getRequestSalida(notaVenta.detalles as List<DetalleNotaVenta>, idUser)
          idTrans = inventarioService.solicitarTransaccion( request )
          if ( idTrans == null) {
              log.warn("no se registra el movimiento, error al registrar devolucion")
          }
      }
    return idTrans
  }



    static InvTrRequest getRequestSalida( List<DetalleNotaVenta> detalleNotaVenta, String idUser ) {
        InvTrRequest request = new InvTrRequest()
        request.effDate = new Date()
        request.idUser = idUser
        request.remarks = ""
        request.reference = ""
        request.siteTo = null
        request.trType = "OTRAS_SALIDAS"
        for (DetalleNotaVenta det : detalleNotaVenta) {
            if( TAG_GENERICO_LC.equalsIgnoreCase(StringUtils.trimToEmpty(det.articulo.idGenerico))
                    && TAG_TIPO_PEDIDO.equalsIgnoreCase(StringUtils.trimToEmpty(det.articulo.tipo)) ){
                request.skuList.add( new InvTrDetRequest( det.idArticulo, det.cantidadFac.intValue() ) )
            }
        }
        return request
    }


  @Override
  void imprimeTransaccionOtrasSalidas( Integer idTrans ){
    TransInv tr = inventarioService.obtenerTransaccion( "OTRAS_SALIDAS", idTrans )
    if ( tr != null ) {
      ticketService.imprimeTransInv( tr )
    }
  }


  @Override
  @Transactional
  void registraLogAutorizacion( String idFactura, String idEmp, Integer idTipoTrans, Pago pago ){
    NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
    AutorizaMov autorizaMov = new AutorizaMov()
    autorizaMov.idEmpleado = idEmp
    autorizaMov.fecha = new Date()
    autorizaMov.hora = new Date()
    autorizaMov.tipoTransaccion = idTipoTrans
    autorizaMov.factura = notaVenta != null ? StringUtils.trimToEmpty(notaVenta.factura) : ""
    if(pago != null){
      String datosPago = StringUtils.trimToEmpty(pago.idFPago)
      if(StringUtils.trimToEmpty(pago.eTipoPago.f1).length() > 0){
        datosPago = datosPago+","+StringUtils.trimToEmpty(pago.clave)
      } else {
        datosPago = datosPago+","
      }
      if(StringUtils.trimToEmpty(pago.eTipoPago.f2).length() > 0){
        datosPago = datosPago+","+StringUtils.trimToEmpty(pago.referenciaClave)
      } else {
        datosPago = datosPago+","
      }
      if(StringUtils.trimToEmpty(pago.eTipoPago.f3).length() > 0){
        datosPago = datosPago+","+StringUtils.trimToEmpty(pago.idBancoEmisor)
      } else {
        datosPago = datosPago+","
      }
      if(StringUtils.trimToEmpty(pago.eTipoPago.f4).length() > 0){
        datosPago = datosPago+","+StringUtils.trimToEmpty(pago.idTerminal)
      } else {
        datosPago = datosPago+","
      }
      if(StringUtils.trimToEmpty(pago.eTipoPago.f5).length() > 0){
        datosPago = datosPago+","+StringUtils.trimToEmpty(pago.idPlan)
      } else {
        datosPago = datosPago+","
      }
      datosPago = datosPago+","+StringUtils.trimToEmpty(pago.monto.toString())
      autorizaMov.notas = datosPago
    }
    autorizaMovRepository.save( autorizaMov )
    autorizaMovRepository.flush()
  }



  Boolean seValidaSurtePino( NotaVenta notaVenta ){
    Boolean valid = false
    Boolean hasSP = false
    Boolean noEnSucursal = false
    Jb jb = jbRepository.findOne( StringUtils.trimToEmpty(notaVenta.factura) )
    if( jb != null ){
      if( !StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase("RS") ){
        noEnSucursal = true
      }
    } else {
      noEnSucursal = true
    }
    for( DetalleNotaVenta det : notaVenta.detalles ){
      if( StringUtils.trimToEmpty(det.surte).equalsIgnoreCase("P") ){
              hasSP = true
      }
    }
    if( hasSP && notaVenta.fechaEntrega == null && noEnSucursal ){
      valid = true
    }
    return valid
  }

  @Override
  void acuseVerSPAcuseVerSPAcuseVerSP( NotaVenta notaVenta ){
    Acuse acuse = new Acuse()
    acuse.idTipo = TAG_VER_SP
    try {
      acuse = acuseRepository.saveAndFlush( acuse )
      log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
    } catch ( Exception e ) {
      log.error( e.getMessage() )
    }
    acuse.contenido = String.format( 'id_sucVal=%s|', String.format( '%d', notaVenta.idSucursal ) )
    acuse.contenido += String.format( 'facturaVal=%s|', notaVenta.factura.trim() )
    acuse.contenido += String.format( 'id_acuseVal=%s|', String.format( '%d', acuse.id ) )
    acuse.fechaCarga = new Date()
    try {
      acuse = acuseRepository.saveAndFlush( acuse )
      log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
    } catch ( Exception e ) {
      log.error( e.getMessage() )
    }
  }

  @Override
  Modificacion obtenerModificacion( String idNotaVenta ){
    QModificacion qModificacion = QModificacion.modificacion
    return modificacionRepository.findOne( qModificacion.idFactura.eq(idNotaVenta) )
  }


}
