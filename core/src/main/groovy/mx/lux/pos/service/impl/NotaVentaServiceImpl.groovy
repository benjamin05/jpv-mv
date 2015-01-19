package mx.lux.pos.service.impl

import com.mysema.query.BooleanBuilder
import com.mysema.query.types.OrderSpecifier
import com.mysema.query.types.Predicate
import groovy.util.logging.Slf4j
import mx.lux.pos.model.*
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.repository.*
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.service.business.EliminarNotaVentaTask
import mx.lux.pos.service.business.Registry
import mx.lux.pos.service.io.PromotionsAdapter
import mx.lux.pos.util.StringList
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
import java.sql.Timestamp
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Slf4j
@Service( 'notaVentaService' )
@Transactional( readOnly = true )
class NotaVentaServiceImpl implements NotaVentaService {

  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm:ss'
  private static final String DATE_FORMAT = 'dd-MM-yyyy'
  private static final String TAG_SURTE_SUCURSAL = 'S'
  private static final String TAG_PAGO_CUPON = 'C'
  private static final String TAG_REUSO = 'R'
  private static final String TAG_GENERICOS_INVENTARIABLES = 'A,E,H'
  private static final String TAG_TIPO_NOTA_VENTA = 'F'
  private static final String TAG_NOTA_CANCELADA = 'T'
  private static final String TAG_TRANSFERENCIA = 'TR'
  private static final String TAG_GENERICOS_B = 'B'
  private static final String TAG_GENERICOS_PAQUETE = 'Q'
  private static final String TAG_GENERICOS_ARMAZON = 'A'
  private static final String TAG_GENERICOS_TIPO = 'G'
  private static final String TAG_GENERICOS_LENTECONTACTO1 = 'C'
  private static final String TAG_GENERICOS_LENTECONTACTO2 = 'H'
  private static final String TAG_GEN_TIPO_C = 'C'
  private static final String TAG_GEN_TIPO_NC = 'NC'
  private static final String TAG_CAUSA_CAN_PAGOS = 'CAMBIO DE FORMA DE PAGO'
  private static final String TAG_ARTICULO_COLOR = 'COG'

  @Resource
  private NotaVentaRepository notaVentaRepository

  @Resource
  private DetalleNotaVentaRepository detalleNotaVentaRepository

  @Resource
  private PagoRepository pagoRepository

  @Resource
  private SucursalRepository sucursalRepository

  @Resource
  private PromocionRepository promocionRepository

  @Resource
  private ArticuloRepository articuloRepository

  @Resource
  private DescuentoRepository descuentoRepository

  @Resource
  private DescuentoClaveRepository descuentoClaveRepository

  @Resource
  private OrdenPromDetRepository ordenPromDetRepository

  @Resource
  private CuponMvRepository cuponMvRepository

  @Resource
  private CierreDiarioRepository cierreDiarioRepository

  @Resource
  private ModeloLcRepository modeloLcRepository

  @Resource
  private PrecioRepository precioRepository

  @Resource
  private ParametroRepository parametroRepository

  @Resource
  private ModificacionRepository modificacionRepository

  @Resource
  private RecetaRepository recetaRepository

  @Resource
  private FacturasImpuestosRepository facturasImpuestosRepository

  @Resource
  private JbRepository jbRepository

  @Resource
  private JbTrackRepository jbTrackRepository

  @Resource
  private PedidoLcRepository pedidoLcRepository

  @Resource
  private PedidoLcDetRepository pedidoLcDetRepository

  @Resource
  private MontoCuponRepository montoCuponRepository

  @Resource
  private ArticuloService articuloService

  @Override
  NotaVenta obtenerNotaVenta( String idNotaVenta ) {
    log.info( "obteniendo notaVenta: ${idNotaVenta}" )
    if ( StringUtils.isNotBlank( idNotaVenta ) ) {
      NotaVenta notaVenta = notaVentaRepository.findOne( idNotaVenta )
      log.debug( "obtiene notaVenta id: ${notaVenta?.id}," )
      log.debug( "fechaHoraFactura: ${notaVenta?.fechaHoraFactura?.format( DATE_TIME_FORMAT )}" )
      return notaVenta
    } else {
      log.warn( 'no se obtiene notaVenta, parametros invalidos' )
    }
    return null
  }
    @Override
    @Transactional
    NotaVenta notaVentaxRx(Integer rx){
     return  notaVentaRepository.notaVentaxRx(rx)
    }


    @Override
  @Transactional
  NotaVenta abrirNotaVenta(String clienteID, String empleadoID ) {
    log.info( 'abriendo nueva notaVenta' )
    Parametro parametro = new Parametro()
      parametro.setValor(clienteID)
      //Cambiar el parametro por clienteID

    NotaVenta notaVenta = new NotaVenta(
        id: notaVentaRepository.getNotaVentaSequence(),
        idSucursal: sucursalRepository.getCurrentSucursalId(),
        idCliente: parametro?.valor?.isInteger() ? parametro.valor.toInteger() : null
    )
      notaVenta.setIdEmpleado(empleadoID)
    try {
      notaVenta = notaVentaRepository.save( notaVenta )
      log.info( "notaVenta registrada id: ${notaVenta?.id}" )
      return notaVenta
    } catch ( ex ) {
      log.error( "problema al registrar notaVenta: ${notaVenta?.dump()}", ex )
    }
    return null
  }

  @Override
  @Transactional
  NotaVenta registrarNotaVenta( NotaVenta notaVenta ) {
    log.info( "registrando notaVenta id: ${notaVenta?.id}," )
    log.info( "fechaHoraFactura: ${notaVenta?.fechaHoraFactura?.format( DATE_TIME_FORMAT )}" )
    if ( StringUtils.isNotBlank( notaVenta?.id ) ) {
      String idNotaVenta = notaVenta.id
      if ( notaVentaRepository.exists( idNotaVenta ) ) {
        notaVenta.idSucursal = sucursalRepository.getCurrentSucursalId()
        BigDecimal total = BigDecimal.ZERO
        List<DetalleNotaVenta> detalles = detalleNotaVentaRepository.findByIdFacturaOrderByCantidadFacAsc( idNotaVenta )
        detalles?.each { DetalleNotaVenta detalleNotaVenta ->
            BigDecimal precio = detalleNotaVenta?.precioUnitFinal ?: 0
            Integer cantidad = detalleNotaVenta?.cantidadFac ?: 0
            BigDecimal subtotal = precio.multiply( cantidad )
            total = total.add( subtotal )
        }

        BigDecimal pagado = BigDecimal.ZERO
        List<Pago> pagos = pagoRepository.findByIdFactura( idNotaVenta )
        pagos?.each { Pago pago ->
          BigDecimal monto = pago?.monto ?: 0
          pagado = pagado.add( monto )
        }
        log.debug( "ventaNeta: ${notaVenta.ventaNeta} -> ${total}" )
        log.debug( "ventaTotal: ${notaVenta.ventaTotal} -> ${total}" )
        log.debug( "sumaPagos: ${notaVenta.sumaPagos} -> ${pagado}" )
        BigDecimal diferencia = notaVenta?.ventaNeta?.subtract(total)
        if( //notaVenta?.montoDescuento?.compareTo(BigDecimal.ZERO) > 0 &&
                ((notaVenta?.ventaNeta?.subtract(total) < new BigDecimal(0.05)) && (notaVenta?.ventaNeta?.subtract(total) > new BigDecimal(-0.05))) ){
          log.debug( "redondeo monto total" )
          if( detalles.size() > 0 ){
            DetalleNotaVenta det =  detalles.first()
            BigDecimal monto = det.precioUnitFinal.add(diferencia)
            if( diferencia.compareTo(BigDecimal.ZERO) > 0 || diferencia.compareTo(BigDecimal.ZERO) < 0 ){
              det.setPrecioUnitFinal( monto )
              det.setPrecioFactura( monto )
              detalleNotaVentaRepository.save( det )
              detalleNotaVentaRepository.flush()
            }
          }
        } else {
          notaVenta.ventaNeta = total
          notaVenta.ventaTotal = total
        }

        notaVenta.sumaPagos = pagado
        notaVenta.tipoNotaVenta = TAG_TIPO_NOTA_VENTA
        try {
          notaVenta = notaVentaRepository.save( notaVenta )
          notaVentaRepository.flush()
          log.info( "notaVenta registrada id: ${notaVenta?.id}" )

        } catch ( ex ) {
          log.error( "problema al registrar notaVenta: ${notaVenta?.dump()}", ex )
        }
      } else {
        log.warn( "no se registra notaVenta, id no existe" )
      }
    } else {
      log.warn( "no se registra notaVenta, parametros invalidos" )
    }
    return notaVenta
  }

  private DetalleNotaVenta establecerPrecios( DetalleNotaVenta detalle ) {
    log.debug( "estableciendo precios para detalleNotaVenta articulo: ${detalle?.idArticulo}" )
    if ( detalle?.idArticulo ) {
      Articulo articulo = articuloRepository.findOne( detalle.idArticulo )
      log.debug( "obtiene articulo id: ${articulo?.id}, codigo: ${articulo?.articulo}, color: ${articulo?.codigoColor}" )
      if ( articulo?.id ) {
        List<Precio> precios = precioRepository.findByArticulo( articulo.articulo )
        log.debug( "obtiene lista de precios ${precios*.lista}" )
        if ( precios?.any() ) {
          Precio precioLista = precios.find { Precio tmp ->
            'L'.equalsIgnoreCase( tmp?.lista )
          }
          log.debug( "precio lista: ${precioLista?.dump()}" )
          BigDecimal lista = precioLista?.precio ?: BigDecimal.ZERO
          Precio precioOferta = precios.find { Precio tmp ->
            'O'.equalsIgnoreCase( tmp?.lista )
          }
          log.debug( "precio oferta: ${precioOferta?.dump()}" )
          BigDecimal oferta = precioOferta?.precio ?: BigDecimal.ZERO
          BigDecimal unitario = oferta && ( oferta < lista ) ? oferta : lista
          detalle.precioCalcLista = lista
          detalle.precioCalcOferta = oferta
          detalle.precioUnitLista = unitario
          detalle.precioUnitFinal = unitario
          detalle.precioFactura = unitario
          detalle.precioConv = BigDecimal.ZERO
          log.debug( "detalleNotaVenta actualizado: ${detalle.dump()}" )
        } else {
          log.warn( 'no se establecen precios, lista de precios vacia' )
        }
      } else {
        log.warn( 'no se establecen precios, articulo invalido' )
      }
    } else {
      log.warn( 'no se establecen precios, parametros invalidos' )
    }
    return detalle
  }

  @Override
  @Transactional
  NotaVenta registrarDetalleNotaVentaEnNotaVenta( String idNotaVenta, DetalleNotaVenta detalleNotaVenta ) {
    log.info( "registrando detalleNotaVenta id: ${detalleNotaVenta?.id} idArticulo: ${detalleNotaVenta?.idArticulo}" )
    log.info( "en notaVenta id: ${idNotaVenta}" )
    NotaVenta notaVenta = obtenerNotaVenta( idNotaVenta )
    if ( StringUtils.isNotBlank( notaVenta?.id ) && detalleNotaVenta?.idArticulo ) {
      detalleNotaVenta.idFactura = idNotaVenta
      detalleNotaVenta.idSucursal = sucursalRepository.getCurrentSucursalId()
      DetalleNotaVenta tmp = detalleNotaVentaRepository.findByIdFacturaAndIdArticulo( idNotaVenta, detalleNotaVenta.idArticulo )
      log.debug( "obtiene detalleNotaVenta existente: ${tmp?.dump()}" )
      if ( tmp?.id ) {
        log.debug( "actualizando detalleNotaVenta con id: ${tmp.id} cantidadFac: ${tmp.cantidadFac}" )
        detalleNotaVenta.id = tmp.id
        detalleNotaVenta.cantidadFac += tmp.cantidadFac
        detalleNotaVenta.idRepVenta = tmp.idRepVenta
        log.debug( "actualizados cantidadFac: ${detalleNotaVenta.cantidadFac}" )
      } else {
        log.debug( "registrando nuevo detalleNotaVenta" )
      }
      detalleNotaVenta = establecerPrecios( detalleNotaVenta )
      try {
        detalleNotaVenta = detalleNotaVentaRepository.save( detalleNotaVenta )
        log.debug( "detalleNotaVenta registrado id: ${detalleNotaVenta.id}" )
        return registrarNotaVenta( notaVenta )
      } catch ( ex ) {
        log.error( "problema al registrar detalleNotaVenta: ${detalleNotaVenta?.dump()}", ex )
      }
    } else {
      log.warn( "no se registra detalleNotaVenta, parametros invalidos" )
    }
    return null
  }

  @Override
  @Transactional
  NotaVenta eliminarDetalleNotaVentaEnNotaVenta( String idNotaVenta, Integer idArticulo ) {
    log.info( "eliminando detalleNotaVenta idArticulo: ${idArticulo} de notaVenta id: ${idNotaVenta}" )
    if ( idArticulo && StringUtils.isNotBlank( idNotaVenta ) ) {
      DetalleNotaVenta detalle = detalleNotaVentaRepository.findByIdFacturaAndIdArticulo( idNotaVenta, idArticulo )
      if ( detalle?.id ) {
        log.debug( "obtiene detalleNotaVenta id: ${detalle.id}" )
        NotaVenta notaVenta = obtenerNotaVenta( idNotaVenta )
        if ( StringUtils.isNotBlank( notaVenta?.id ) ) {
          detalleNotaVentaRepository.delete( detalle.id )
          log.debug( "detalleNotaVenta eliminado" )
          return registrarNotaVenta( notaVenta )
        } else {
          log.warn( "no se elimina detalleNotaVenta, no existe notaVenta id: ${idNotaVenta}" )
        }
      } else {
        log.warn( "no se elimina detalleNotaVenta, no existe con idNotaVenta: ${idNotaVenta} idArticulo: ${idArticulo}" )
      }
    } else {
      log.warn( "no se elimina detalleNotaVenta, parametros invalidos" )
    }
    return null
  }

  @Override
  @Transactional
  Pago registrarPagoEnNotaVenta( String idNotaVenta, Pago pago ) {
    log.info( "registrando pago id: ${pago?.id} idFormaPago: ${pago?.idFormaPago} monto: ${pago?.monto}" )
    log.info( "en notaVenta id: ${idNotaVenta}" )
    NotaVenta notaVenta = obtenerNotaVenta( idNotaVenta )
    if ( StringUtils.isNotBlank( notaVenta?.id ) && StringUtils.isNotBlank( pago?.idFormaPago ) && pago?.monto ) {
      String formaPago = pago.idFormaPago
      if ( 'ES'.equalsIgnoreCase( formaPago ) ) {
        formaPago = 'EFM'
      } else if ( 'TS'.equalsIgnoreCase( formaPago ) ) {
        formaPago = 'TCM'
      }
      log.debug( "forma pago definida: ${formaPago}" )
      Date fechaActual = new Date()
      pago.idFormaPago = formaPago
      pago.idFactura = idNotaVenta
      pago.idSucursal = sucursalRepository.getCurrentSucursalId()
      pago.tipoPago = DateUtils.isSameDay( notaVenta.fechaHoraFactura ?: fechaActual, fechaActual ) ? 'a' : 'l'
      log.debug( "obteniendo existencia de pago con id: ${pago.id}" )
      Pago tmp = pagoRepository.findOne( pago.id ?: 0 )
      if ( tmp?.id ) {
        log.debug( "pago ya registrado, no se puede modificar" )
      } else {
        log.debug( "registrando pago con monto: ${pago.monto}" )
        try {
          pago = pagoRepository.save( pago )
          log.debug( "pago registrado id: ${pago.id}" )
            registrarNotaVenta( notaVenta )
            return  pago
        } catch ( ex ) {
          log.error( "problema al registrar pago: ${pago?.dump()}", ex )
        }
      }
    } else {
      log.warn( "no se registra pago, parametros invalidos" )
    }
    return null
  }

  @Override
  @Transactional
  NotaVenta eliminarPagoEnNotaVenta( String idNotaVenta, Integer idPago ) {
    log.info( "eliminando pago id: ${idPago} idFactura: ${idNotaVenta}" )
    if ( idPago && StringUtils.isNotBlank( idNotaVenta ) ) {
      Pago pago = pagoRepository.findOne( idPago )
      if ( pago?.id ) {
        log.debug( "obtiene pago id: ${pago.id} idFormaPago: ${pago.idFormaPago} monto: ${pago.idFormaPago}" )
        NotaVenta notaVenta = obtenerNotaVenta( idNotaVenta )
        if ( StringUtils.isNotBlank( notaVenta?.id ) ) {
          pagoRepository.delete( pago.id )
          pagoRepository.flush()
          log.debug( "pago eliminado" )
          return registrarNotaVenta( notaVenta )
        } else {
          log.warn( "no se elimina pago, no existe notaVenta id: ${idNotaVenta}" )
        }
      } else {
        log.warn( "no se elimina pago, no existe con id: ${idPago}" )
      }
    } else {
      log.warn( "no se elimina pago, parametros invalidos" )
    }
    return null
  }
    @Transactional
    NotaVenta saveFrame(String idNotaVenta, String opciones, String forma) {

        NotaVenta rNotaVenta = obtenerNotaVenta(idNotaVenta)

                println('Material: ' + opciones)
                println('Acabado: '+ forma)
                rNotaVenta?.setUdf2(opciones)
                rNotaVenta?.setUdf3(forma)
        try{
          println rNotaVenta.dump()
          rNotaVenta =  notaVentaRepository.save( rNotaVenta )
          notaVentaRepository.flush()
        } catch ( Exception e ){
            println e
        }
        return rNotaVenta
    }


    @Transactional
    void saveProDate(NotaVenta rNotaVenta, Date fechaPrometida){
        if ( StringUtils.isNotBlank( rNotaVenta.id) ) {
            if ( notaVentaRepository.exists( rNotaVenta.id ) ) {
                rNotaVenta.setFechaPrometida(fechaPrometida)
                registrarNotaVenta( rNotaVenta )
            } else {
                log.warn( "id no existe" )
            }
        } else {
            log.warn( "No hay receta" )
        }
    }

    @Transactional
    void saveRx(NotaVenta rNotaVenta, Integer receta){
        if ( StringUtils.isNotBlank( rNotaVenta.id) && receta != null && recetaRepository.exists(receta) ) {
            //if ( notaVentaRepository.exists( rNotaVenta.id ) && rNotaVenta.receta == null ) {
                rNotaVenta.setReceta(receta)
             registrarNotaVenta( rNotaVenta )
            /*} else {
                log.warn( "id no existe" )
            }*/
        } else {
            log.warn( "No hay receta" )
        }
    }

  @Override
  @Transactional
  NotaVenta cerrarNotaVenta( NotaVenta notaVenta ) {
    log.info( "cerrando notaVenta id: ${notaVenta?.id}" )
    if ( StringUtils.isNotBlank( notaVenta?.id ) ) {
      String idNotaVenta = notaVenta.id
      if ( notaVentaRepository.exists( idNotaVenta ) ) {
        /*Boolean subtypeS = false
        Boolean typeG = false
        for(DetalleNotaVenta det : notaVenta.detalles){
          if(det.articulo.subtipo.startsWith('S')){
            subtypeS = true
          }
          if(det.articulo.tipo.startsWith('G')){
            typeG = true
          }
        }
        if( (subtypeS || typeG) && notaVenta.codigo_lente != null && notaVenta.codigo_lente.trim().length() > 0 ){*/
        Boolean agregarColor = false
        for(DetalleNotaVenta det : notaVenta.detalles){
          if( StringUtils.trimToEmpty(det.articulo.articulo).equalsIgnoreCase(TAG_ARTICULO_COLOR) ){
            agregarColor = true
          }
        }
        if( agregarColor && notaVenta.codigo_lente != null && notaVenta.codigo_lente.trim().length() > 0 ){
          String dioptra = notaVenta.codigo_lente
          String dioptraTmp = dioptra.substring( 0, dioptra.length()-1 )
          dioptra = dioptraTmp+'T'
          if( articuloService.validaCodigoDioptra( StringUtils.trimToEmpty(dioptra) ) ){
            notaVenta.codigo_lente = dioptra
          }
        }
        Date fecha = new Date()
        String factura = String.format( "%06d", notaVentaRepository.getFacturaSequence() )
        notaVenta.factura = factura
        notaVenta.tipoNotaVenta = 'F'
        notaVenta.tipoDescuento = 'N'
        notaVenta.tipoEntrega = 'S'
        notaVenta.setfExpideFactura( true )
        //notaVenta.fechaEntrega = notaVenta.fechaEntrega ?: fecha
        //notaVenta.horaEntrega = notaVenta.horaEntrega ?: fecha
        notaVenta.fechaPrometida = notaVenta.fechaPrometida ?: fecha
        return registrarNotaVenta( notaVenta )
      } else {
        log.warn( "no se cierra notaVenta, id no existe" )
      }
    } else {
      log.warn( "no se cierra notaVenta, parametros invalidos" )
    }
    return null
  }

  @Override
  List<NotaVenta> listarUltimasNotasVenta( ) {
    log.info( "listando ultimas notasVenta" )
    List<NotaVenta> results = notaVentaRepository.findByFacturaNotEmptyLimitingLatestResults( 10 )
    return results?.any() ? results : [ ]
  }

  private Predicate generarPredicadoTicket( String ticket ) {
    log.info( "generando predicado para busqueda de notaVenta con ticket: ${ticket}" )
    List<String> tokens = StringUtils.splitPreserveAllTokens( ticket, '-' )
    if ( StringUtils.isNotBlank( ticket ) && tokens?.size() >= 2 ) {
      String centroCostos = StringUtils.trimToEmpty( tokens.get( 0 ) )
      String factura = StringUtils.trimToEmpty( tokens.get( 1 ) )
      log.debug( "ticket con centro de costos: ${centroCostos} y factura: ${factura}" )
      if ( factura.length() > 0 && centroCostos.length() > 0 ) {
        QNotaVenta qNotaVenta = QNotaVenta.notaVenta
        BooleanBuilder builder = new BooleanBuilder( qNotaVenta.factura.eq( factura ) )
        builder.and( qNotaVenta.sucursal.centroCostos.eq( centroCostos ) )
        return builder
      } else {
        log.warn( 'no se genera predicado, factura y/o centro de costos invalidos' )
      }
    } else {
      log.warn( 'no se genera predicado, parametros invalidos' )
    }
    return null
  }

  @Override
  List<NotaVenta> listarNotasVentaPorParametros( Map<String, Object> parametros ) {
    log.info( "listando notasVenta por parametros: ${parametros}" )
    if ( parametros?.any() ) {

        println(parametros.dateFrom as Date)
        println(parametros.dateTo as Date)
        println(parametros.folio)
        println(parametros.ticket)
        println(parametros.employee)
      Date dateFrom = parametros.dateFrom as Date
      Date dateTo = parametros.dateTo as Date
      String folio = parametros.folio
      String ticket = parametros.ticket
      String employee = parametros.employee
      String factura = ''
      QNotaVenta qNotaVenta = QNotaVenta.notaVenta
      BooleanBuilder builder = new BooleanBuilder()
      if(ticket.trim() != ''){
        String[] ticketValid = ticket.split('-')
        if(ticketValid.length > 1){
          dateFrom = null
          dateTo = null
          factura = ticketValid[1]
        }
      }
      if ( dateFrom && dateTo ) {
        dateTo = new Date( dateTo.next().time - 1 )
        log.debug( "fecha inicio: ${dateFrom?.format( DATE_TIME_FORMAT )}" )
        log.debug( "fecha fin: ${dateTo?.format( DATE_TIME_FORMAT )}" )
          if(!StringUtils.isNotBlank( folio )){
          builder.and( qNotaVenta.fechaHoraFactura.between( dateFrom, dateTo ) )
      }
      }
      if ( StringUtils.isNotBlank( folio ) ) {
        log.debug( "folio: ${folio}" )
        builder.and( qNotaVenta.id.eq( folio ) )
      }
      Predicate predicate = generarPredicadoTicket( ticket )
      if ( predicate ) {
        builder.and( predicate )
      }
      if ( StringUtils.isNotBlank( employee ) ) {
        log.debug( "empleado: ${employee}" )
        builder.and( qNotaVenta.idEmpleado.eq( employee ) )
      }
      if ( builder.args?.any() ) {
        builder.and( qNotaVenta.factura.isNotEmpty() )
        List<NotaVenta> results = notaVentaRepository.findAll( builder, qNotaVenta.fechaHoraFactura.desc() ) as List<NotaVenta>
        if( results.size() <= 0 && factura.length() > 0){
          try{
            results = notaVentaRepository.findByFactura( String.format("%06d", NumberFormat.getInstance().parse(factura.trim())) )
          } catch (Exception e){
            println e
          }
        }
        for(NotaVenta nota : results){
          List<OrdenPromDet> lstPromos = ordenPromDetRepository.findByIdFactura( nota.id )
          if(lstPromos.size() > 0){
            nota.ordenPromDet.clear()
            nota.ordenPromDet.addAll( lstPromos )
          }
        }
        return results?.any() ? results : [ ]
      }
    } else {
      log.warn( "no se realiza busqueda, parametros invalidos" )
    }
    return [ ]
  }

  @Override
  NotaVenta obtenerNotaVentaPorTicket( String ticket ) {
    log.info( "obteniendo notaVenta con ticket: ${ticket}" )
    Predicate predicate = generarPredicadoTicket( ticket )
    if ( predicate ) {
      OrderSpecifier orderSpecifier = QNotaVenta.notaVenta.fechaHoraFactura.desc()
      List<NotaVenta> resultados = notaVentaRepository.findAll( predicate, orderSpecifier ) as List<NotaVenta>
      NotaVenta notaVenta = resultados?.any() ? resultados.first() : null
      log.debug( "obtiene notaVenta id: ${notaVenta?.id}" )
      return notaVenta
    } else {
      log.warn( 'no se obtiene notaVenta, parametros invalidos' )
    }
    return null
  }

  void eliminarNotaVenta( String pOrderNbr ) {
    log.debug( String.format( "Eliminar Nota Venta: %s", pOrderNbr ) )
    EliminarNotaVentaTask task = new EliminarNotaVentaTask()
    NotaVenta order = notaVentaRepository.findOne( pOrderNbr )
    if ( order != null ) {
      task.addNotaVenta( order.id )
      log.debug( task.toString() )
      task.run()
    } else {
      log.debug( String.format( 'No existe Nota Venta: %s', pOrderNbr ) )
    }
  }

  SalesWithNoInventory obtenerConfigParaVentasSinInventario( ) {
    SalesWithNoInventory autorizacion = Registry.configForSalesWithNoInventory
    return autorizacion
  }

  Empleado obtenerEmpleadoDeNotaVenta( pOrderId ) {
    Empleado employee = null
    if ( StringUtils.trimToNull( pOrderId ) != null ) {
      NotaVenta order = notaVentaRepository.findOne( StringUtils.trimToEmpty( pOrderId ) )
      if ( ( order != null ) && ( StringUtils.trimToNull( order.idEmpleado ) != null ) ) {
        employee = RepositoryFactory.employeeCatalog.findOne( StringUtils.trimToEmpty( order.idEmpleado ) )
      }
    }
    return employee
  }

  @Transactional
  void saveOrder( NotaVenta pNotaVenta ) {
    if ( pNotaVenta != null ) {

      notaVentaRepository.saveAndFlush( pNotaVenta )
    }
  }

    @Override
    @Transactional
  NotaVenta obtenerSiguienteNotaVenta( Integer pIdCustomer ) {
    Date fechaStart = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH )
    Date fechaEnd = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 )
    QNotaVenta nota = QNotaVenta.notaVenta
    //List<NotaVenta> orders = notaVentaRepository.findByIdCliente( pIdCustomer )
    List<NotaVenta> orders = notaVentaRepository.findAll(nota.idCliente.eq(pIdCustomer).
            and(nota.fechaHoraFactura.between(fechaStart,fechaEnd)))
    NotaVenta order = null
    for (NotaVenta o : orders) {
      if ( o.detalles.size() > 0 && StringUtils.isBlank( o.factura )) {
        order = o
        break
      }
    }
    if (order == null) {
      ServiceFactory.customers.eliminarClienteProceso( pIdCustomer )
    }
    return order
  }


  @Override
  @Transactional
  void validaSurtePorGenericoInventariable( NotaVenta notaVenta ){
    List<DetalleNotaVenta> detalles = detalleNotaVentaRepository.findByIdFactura( notaVenta.id )
    for(DetalleNotaVenta det : detalles){
        if(!TAG_GENERICOS_INVENTARIABLES.contains(det.articulo.idGenerico)){
            det.surte = ' '
            detalleNotaVentaRepository.save( det )
            detalleNotaVentaRepository.flush()
        }
    }
  }


  @Override
  @Transactional
  void registraImpuestoPorFactura( NotaVenta notaVenta ){
    Parametro parametro = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.value )
    FacturasImpuestos impuesto = new FacturasImpuestos()
    impuesto.idFactura = notaVenta?.id
    impuesto.idImpuesto = parametro.valor
    impuesto.idSucursal = notaVenta.idSucursal
    impuesto.fecha = new Date()

    impuesto = facturasImpuestosRepository.save( impuesto )
    log.debug( "guardando idImpuesto ${impuesto.idImpuesto} a factura: ${impuesto.idFactura}" )
    facturasImpuestosRepository.flush()
  }



  @Override
  Boolean ticketReusoValido( String ticket, Integer idArticulo ){
    QNotaVenta nv = QNotaVenta.notaVenta
    NotaVenta nota = notaVentaRepository.findOne( nv.factura.eq(ticket.trim()).and(nv.sFactura.eq(TAG_NOTA_CANCELADA)) )
    Articulo articulo = articuloRepository.findOne( idArticulo )
    Boolean validTicket = false
    Boolean valid
    if( nota != null && articulo != null ){
      if( !nota.fechaHoraFactura.format(DATE_FORMAT).equalsIgnoreCase(new Date().format(DATE_FORMAT)) ){
          List<Modificacion> lstMod = modificacionRepository.findByIdFacturaOrderByFechaAsc( StringUtils.trimToEmpty(nota.id) )
          Modificacion mod = new Modificacion()
          for(Modificacion modificacion : lstMod){
            if( StringUtils.trimToEmpty(modificacion.tipo).equalsIgnoreCase("can") ){
              mod = modificacion
            }
          }
          if( mod.id != null && mod.fecha.format(DATE_FORMAT).equalsIgnoreCase(new Date().format(DATE_FORMAT)) ){
            for(DetalleNotaVenta det : nota.detalles){
              if(det.idArticulo == articulo.id){
                validTicket = true
              }
            }
          }
      }
    }
    if( validTicket ){
      valid = true
    }
    return valid
  }



  @Override
  Boolean montoValidoFacturacion( String ticketComp ){
    log.debug( "montoValidoFacturacion( )" )
    Boolean esValido = true
    BigDecimal montoTotal = BigDecimal.ZERO
    BigDecimal montoCupones = BigDecimal.ZERO
    String[] ticketTmp = ticketComp.split("-")
    String ticket = ''
    if(ticketTmp.length >= 2){
      ticket = ticketTmp[1]
    }
    QNotaVenta nv = QNotaVenta.notaVenta
    NotaVenta nota = notaVentaRepository.findOne( nv.factura.eq(ticket.trim()) )
    if(nota != null){
      for(Pago pago : nota.pagos){
        montoTotal = montoTotal.add(pago.monto)
        if(pago.idFPago.trim().startsWith(TAG_PAGO_CUPON)){
          montoCupones = montoCupones.add(pago.monto)
        }
      }
      if(montoTotal.subtract(montoCupones) == 0){
        esValido = false
      }
    }
    return esValido
  }


  @Override
  List<NotaVenta> obtenerDevolucionesPendientes( Date fecha ) {
      log.info( "obteniendo pagos del dia: ${fecha}" )
      Date fechaInicio = DateUtils.truncate( fecha, Calendar.DAY_OF_MONTH );
      Date fechaFin = new Date( DateUtils.ceiling( fecha, Calendar.DAY_OF_MONTH ).getTime() - 1 );
      List<NotaVenta> lstNotasVentas = new ArrayList<NotaVenta>()
      QModificacion mod = QModificacion.modificacion
      List<Modificacion> lstModificaciones = modificacionRepository.findAll( mod.fecha.between(fechaInicio, fechaFin).
              and(mod.tipo.equalsIgnoreCase('can')))
      for(Modificacion modificacion : lstModificaciones){
          NotaVenta notaVenta = notaVentaRepository.findOne( modificacion.idFactura )
          if(notaVenta != null){
              Boolean pendiente = false
              for(Pago pago : notaVenta.pagos){
                  if( pago.porDevolver.compareTo(BigDecimal.ZERO) > 0){
                      pendiente = true
                  }
              }
              if(pendiente){
                  lstNotasVentas.add(notaVenta)
              }
          }
      }
      return lstNotasVentas
  }


  @Override
  NotaVenta buscarNotasReuso( String idFactura ) {
    log.debug( "buscarNotasReuso( )" )
    NotaVenta nota = new NotaVenta()
    NotaVenta notas = notaVentaRepository.findOne( idFactura )
    QPago pay = QPago.pago
    List<Pago> lstPagos = pagoRepository.findAll( pay.referenciaPago.eq(notas.id.trim()) )
    if( lstPagos.size() > 0 ){
      NotaVenta notaTmp = lstPagos.first().notaVenta
      for(DetalleNotaVenta det : notaTmp.detalles){
        if(TAG_REUSO.equalsIgnoreCase(det.surte.trim())){
          nota = notaTmp
        }
      }
    }
    return nota != null && nota.id != null ? nota : null
  }


  @Override
  NotaVenta obtenerNotaVentaOrigen( String idNotaVenta ){
    NotaVenta nota = notaVentaRepository.findOne( idNotaVenta )
    NotaVenta notaOrigen = null
    String idNotaOrigen = ''
    for(Pago payment : nota.pagos){
      if(TAG_TRANSFERENCIA.equalsIgnoreCase(payment.idFPago) && payment?.referenciaPago?.trim().length() > 0){
        idNotaOrigen = payment?.referenciaPago?.trim()
        notaOrigen = notaVentaRepository.findOne( idNotaOrigen )
      }
    }
    return notaOrigen
  }


  @Override
  Boolean validaSoloInventariables( String idFactura ) {
    log.debug( "validaSoloInventariables( )" )
    NotaVenta nota = notaVentaRepository.findOne( idFactura )
    Boolean esInventariable = true
    for(DetalleNotaVenta det : nota.detalles){
      if( TAG_GENERICOS_B.contains(det?.articulo?.idGenerico?.trim()) ){
        esInventariable = false
      }
    }
    return esInventariable
  }


  @Override
  void insertaJbAnticipoInventariables( String idFactura ){
    log.debug( "insertaJbAnticipoInventariables( )" )
    NotaVenta nota = notaVentaRepository.findOne( idFactura )
    String articulos = ''
      for(DetalleNotaVenta det : nota.detalles){
          articulos = articulos+","+det.articulo.articulo.trim()
      }
      articulos = articulos.replaceFirst( ",", "" )
    if( nota != null ){
      Jb jb = new Jb()
      String factura = nota.factura.replaceFirst("^0*", "")
      jb.rx = factura
      jb.estado = 'RTN'
      jb.id_cliente = nota.idCliente.toString().trim()
      jb.emp_atendio = nota.idEmpleado
      jb.num_llamada = 0
      jb.saldo = nota.ventaNeta.subtract( nota.sumaPagos )
      jb.material = articulos
      jb.fecha_promesa = nota.fechaPrometida
      jb.jb_tipo = 'REF'
      jb.id_mod = '0'
      jb.fecha_mod = new Date()
      jb.cliente = nota?.cliente?.nombreCompleto
      jb.fecha_venta = nota?.fechaHoraFactura
      jb = jbRepository.saveAndFlush( jb )

      JbTrack jbTrack = new JbTrack()
      jbTrack.rx = jb.rx
      jbTrack.estado = 'RTN'
      jbTrack.obs = 'TRABAJO CON SALDO'
      jbTrack.emp = jb.emp_atendio
      jbTrack.fecha = new Date()
      jbTrack.id_mod = '0'
      jbTrackRepository.saveAndFlush( jbTrack )
    }
  }

  @Override
  void correScriptRespaldoNotas( String idFactura ){
    log.debug( "correScriptRespaldoNotas( )" )
    NotaVenta nota = notaVentaRepository.findOne( idFactura )
    if( nota != null ){
      try{
      String cmd = String.format( "%s %s", Registry.commandBakpOrder, nota.id);
      Process p = Runtime.getRuntime().exec(cmd);
      log.debug( "comando a ejecutar <${cmd}>" )
      } catch (Exception e){
        println e
      }
    }
  }


  @Override
  NotaVenta buscarNotaInicial( Integer idCliente, String idFactura ){
      List<NotaVenta> nota = null
      Date fechaStart = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH )
      Date fechaEnd = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 )
      QNotaVenta nv = QNotaVenta.notaVenta
      nota = notaVentaRepository.findAll(nv.idCliente.eq(idCliente).and(nv.fechaHoraFactura.between(fechaStart, fechaEnd)).
              and(nv.id.ne(idFactura)). and(nv.sFactura.ne("T")).
              and(nv.factura.isNotNull()).and(nv.factura.isNotEmpty()),
              nv.fechaHoraFactura.asc()) as List<NotaVenta>
      if( nota.size() > 0 ){
        return  nota.last()
      } else {
        return null
      }
  }


  @Override
  List<NotaVenta> obtenerNotaVentaPorCliente( Integer idCliente ){
    log.debug( "obtenerNotaVentaPorCliente(  )" )
    Date fechaStart = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH )
    Date fechaEnd = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 )
    List<NotaVenta> lstNotas = new ArrayList<>()
    List<NotaVenta> notas = new ArrayList<>()
    QNotaVenta nv = QNotaVenta.notaVenta
    List<NotaVenta> notasTmp = notaVentaRepository.findAll( nv.idCliente.eq(idCliente).
            and(nv.fechaHoraFactura.between(fechaStart,fechaEnd)).and(nv.sFactura.ne('T')).
            and(nv.fechaPrometida.isNull()), nv.fechaHoraFactura.asc(), nv.ventaTotal.asc() )
    for(NotaVenta notv : notasTmp){
      if(notv.detalles.size() > 0){
        notas.add(notv)
      }
    }
    if( notas.size() > 1 ){
      lstNotas.add( notas.get(0) )
      lstNotas.add( notas.get(1) )
    }
    return lstNotas
  }




  @Override
  BigDecimal obtenerMontoCupon( String idNotaVenta ){
    log.debug( 'obtenerMontoCupon( )' )
    BigDecimal montoCupon = BigDecimal.ZERO
    Articulo articulo = new Articulo()
    List<MontoCupon> lstMontosCupon = new ArrayList<>()
    NotaVenta nota = notaVentaRepository.findOne( idNotaVenta )
    if( nota != null){
      for( DetalleNotaVenta det : nota.detalles ){
        List<Precio> lstPrecios = precioRepository.findByArticulo( det.articulo.articulo )
        BigDecimal precio = det.articulo.precio.multiply(det.cantidadFac)
        if( lstPrecios.size() > 0 ){
          precio = lstPrecios.get(0).precio.multiply(det.cantidadFac)
        }
        QMontoCupon mc = QMontoCupon.montoCupon
        MontoCupon montosCup = montoCuponRepository.findOne( mc.generico.eq(det.articulo.idGenerico).
                and(mc.tipo.eq(det.articulo.tipo)).
                and(mc.montoMinimo.eq(precio).or(mc.montoMaximo.eq(precio))))
        if( montosCup == null ){
            montosCup = montoCuponRepository.findOne(mc.generico.eq(det.articulo.idGenerico).
                  and(mc.tipo.eq(det.articulo.tipo)).
                  and(mc.montoMinimo.loe(precio)).and(mc.montoMaximo.goe(precio)))
        }
        if( montosCup == null ){
            montosCup = montoCuponRepository.findOne( mc.generico.eq(det.articulo.idGenerico).
                    and(mc.tipo.eq(det.articulo.tipo)).
                    and(mc.montoMinimo.loe(precio)).and(mc.montoMaximo.goe(precio)))
        }
        if( montosCup == null ){
          montosCup = montoCuponRepository.findOne( mc.generico.eq(det.articulo.idGenerico).
              and(mc.montoMinimo.loe(precio)).and(mc.montoMaximo.goe(precio)))
          if( montosCup != null && StringUtils.trimToEmpty(montosCup.tipo).length() > 0 ){
            montosCup = null
          }
        }
        if( montosCup != null ){
          lstMontosCupon.add( montosCup )
        }
      }
      montoCupon = montoCuponCalculo( lstMontosCupon )
    }
    return montoCupon
  }


  BigDecimal obtenerMontoCuponTercerPar( String idNotaVenta ){
    log.debug( 'obtenerMontoCuponTercerPar( )' )
    BigDecimal montoCupon = BigDecimal.ZERO
    Articulo articulo = new Articulo()
    List<MontoCupon> lstMontosCupon = new ArrayList<>()
    NotaVenta nota = notaVentaRepository.findOne( idNotaVenta )
    if( nota != null){
          for( DetalleNotaVenta det : nota.detalles ){
              List<Precio> lstPrecios = precioRepository.findByArticulo( det.articulo.articulo )
              BigDecimal precio = det.articulo.precio.multiply(det.cantidadFac)
              if( lstPrecios.size() > 0 ){
                  precio = lstPrecios.get(0).precio.multiply(det.cantidadFac)
              }
              QMontoCupon mc = QMontoCupon.montoCupon
              MontoCupon montosCup = montoCuponRepository.findOne( mc.generico.eq(det.articulo.idGenerico).
                      and(mc.tipo.eq(det.articulo.tipo)).
                      and(mc.montoMinimo.eq(precio).or(mc.montoMaximo.eq(precio))))
              if( montosCup == null ){
                  montosCup = montoCuponRepository.findOne(mc.generico.eq(det.articulo.idGenerico).
                          and(mc.tipo.eq(det.articulo.tipo)).
                          and(mc.montoMinimo.loe(precio)).and(mc.montoMaximo.goe(precio)))
              }
              if( montosCup == null ){
                  montosCup = montoCuponRepository.findOne( mc.generico.eq(det.articulo.idGenerico).
                          and(mc.tipo.eq(det.articulo.tipo)).
                          and(mc.montoMinimo.loe(precio)).and(mc.montoMaximo.goe(precio)))
              }
              if( montosCup == null ){
                  montosCup = montoCuponRepository.findOne( mc.generico.eq(det.articulo.idGenerico).
                          and(mc.montoMinimo.loe(precio)).and(mc.montoMaximo.goe(precio)))
                  if( montosCup != null && StringUtils.trimToEmpty(montosCup.tipo).length() > 0 ){
                      montosCup = null
                  }
              }
              if( montosCup != null ){
                  lstMontosCupon.add( montosCup )
              }
          }
          montoCupon = montoCuponCalculoTercerPar( lstMontosCupon )
      }
      return montoCupon
  }


  static BigDecimal montoCuponCalculo( List<MontoCupon> lstMontos ){
    BigDecimal montoCupon = BigDecimal.ZERO
    if( lstMontos.size() > 1 ){
      montoCupon = lstMontos.get(0).monto.max(lstMontos.get(1).monto)
    } else if(lstMontos.size() > 0){
      montoCupon = lstMontos.get(0).monto
    }
    return montoCupon
  }


  static BigDecimal montoCuponCalculoTercerPar( List<MontoCupon> lstMontos ){
        BigDecimal montoCupon = BigDecimal.ZERO
        if( lstMontos.size() > 1 ){
            montoCupon = lstMontos.get(0).montoTercerPar.max(lstMontos.get(1).montoTercerPar)
        } else if(lstMontos.size() > 0){
            montoCupon = lstMontos.get(0).montoTercerPar
        }
        return montoCupon
  }


    @Override
    Boolean validaLentes( String idFactura ){
      Boolean hasLente = false
        NotaVenta nota = notaVentaRepository.findOne( idFactura )
        for(DetalleNotaVenta det : nota.detalles){
          if( det.articulo.indice_dioptra != null && StringUtils.trimToEmpty(det.articulo.indice_dioptra) != '' ){
            hasLente = true
          }
        }
      return hasLente
    }



    @Override
    List<Articulo> validaLentesContacto( String idFactura ){
        List<Articulo> articulo = new ArrayList<>()
        NotaVenta nota = notaVentaRepository.findOne( idFactura )
        List<ModeloLc> modelosLc = modeloLcRepository.findAll()
        for(DetalleNotaVenta det : nota.detalles){
          if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICOS_LENTECONTACTO2) ){
            if( StringUtils.trimToEmpty(det.articulo.idGenTipo).equalsIgnoreCase(TAG_GEN_TIPO_C) ){
              if(StringUtils.trimToEmpty(det.idRepVenta).length() <= 0 ){
                articulo.add(det.articulo)
              }
              String[] lotes = StringUtils.trimToEmpty(det.idRepVenta).split(",")
              if( lotes.size() < det.cantidadFac ){
                Integer faltantes = det.cantidadFac-lotes.size()
                for(int i=0; i < faltantes; i++){
                  articulo.add(det.articulo)
                }
              }
            } else if( StringUtils.trimToEmpty(det.articulo.idGenTipo).equalsIgnoreCase(TAG_GEN_TIPO_NC) ){
                for(ModeloLc mod : modelosLc){
                    if( StringUtils.trimToEmpty(det.articulo.articulo).equalsIgnoreCase(StringUtils.trimToEmpty(mod.id)) ){
                        PedidoLc pedidoLc = pedidoLcRepository.findOne( idFactura )
                        QPedidoLcDet pedidoLcDet = QPedidoLcDet.pedidoLcDet
                        List<PedidoLcDet> pedidoDet = pedidoLcDetRepository.findAll( pedidoLcDet.id.eq(det.idFactura).
                                and(pedidoLcDet.modelo.eq(det.articulo.articulo)))
                        if( pedidoLc != null ){
                            if(pedidoDet.size() <= 0){
                                articulo.add(det.articulo)
                            }
                        } else {
                            articulo.add(det.articulo)
                        }
                    }
                }
            }
          }
        }
        return articulo
    }



  @Override
  Boolean validaTieneDetalles( String orderId, Integer idArticulo ){
    Boolean hasDet = false
    QPedidoLcDet pedidoLcDet = QPedidoLcDet.pedidoLcDet
    List<PedidoLcDet> pedidos = pedidoLcDetRepository.findAll( pedidoLcDet.id.eq(orderId) )
    Articulo articulo = articuloRepository.findOne( idArticulo )
    for(PedidoLcDet pedid : pedidos){
      if( StringUtils.trimToEmpty(pedid.modelo).equalsIgnoreCase(StringUtils.trimToEmpty(articulo.articulo)) ){
        hasDet = true
      }
    }
    return hasDet
  }



  @Override
  @Transactional
  void removePedidoLc( String orderId, Integer idArticulo ){
    Articulo articulo = articuloRepository.findOne(idArticulo)
    QPedidoLcDet ped = QPedidoLcDet.pedidoLcDet
    List<PedidoLcDet> pedidoLcDet = pedidoLcDetRepository.findAll(ped.id.eq(orderId).and(ped.modelo.eq(articulo.articulo)))
    for(PedidoLcDet det : pedidoLcDet){
        pedidoLcDetRepository.delete( det.numReg )
        pedidoLcDetRepository.flush()
    }
    PedidoLc pedidoLc = pedidoLcRepository.findOne( orderId )
    if(pedidoLc != null && pedidoLc.pedidoLcDets.size() <= 0){
      pedidoLcRepository.delete(pedidoLc.id)
    }
  }



    @Override
    void insertaJbLc( String idFactura ){
        log.debug( "insertaJbLc( )" )
        NotaVenta nota = notaVentaRepository.findOne( idFactura )
        String articulos = ''
        for(DetalleNotaVenta det : nota.detalles){
            articulos = articulos+","+det.articulo.articulo.trim()
        }
        articulos = articulos.replaceFirst( ",", "" )
        if( nota != null ){
            Jb jb = new Jb()
            String factura = nota.factura.replaceFirst("^0*", "")
            jb.rx = factura
            jb.estado = 'RTN'
            jb.id_cliente = nota.idCliente.toString().trim()
            jb.emp_atendio = nota.idEmpleado
            jb.num_llamada = 0
            jb.saldo = nota.ventaNeta.subtract( nota.sumaPagos )
            jb.material = articulos
            jb.fecha_promesa = nota.fechaPrometida
            jb.jb_tipo = 'REF'
            jb.id_mod = '0'
            jb.fecha_mod = new Date()
            jb.cliente = nota?.cliente?.nombreCompleto
            jb.fecha_venta = nota?.fechaHoraFactura
            jb = jbRepository.saveAndFlush( jb )

            JbTrack jbTrack = new JbTrack()
            jbTrack.rx = jb.rx
            jbTrack.estado = 'RTN'
            jbTrack.obs = articulos
            jbTrack.emp = jb.emp_atendio
            jbTrack.fecha = new Date()
            jbTrack.id_mod = '0'
            jbTrackRepository.saveAndFlush( jbTrack )
        }
    }



    Boolean validaSoloInventariablesMultipago( String idFactura ) {
        log.debug( "validaSoloInventariables( )" )
        NotaVenta nota = notaVentaRepository.findOne( idFactura )
        Boolean esInventariable = true
        for(DetalleNotaVenta det : nota.detalles){
            if( !det?.articulo?.generico.inventariable ){
                esInventariable = false
            }
        }
        return esInventariable
    }



  @Transactional
  @Override
  void saveBatch( String idFactura, Integer idArticulo, String lote ){
    DetalleNotaVenta detalleNota = detalleNotaVentaRepository.findByIdFacturaAndIdArticulo( idFactura, idArticulo )
    if( detalleNota != null ){
      detalleNota.idRepVenta = StringUtils.trimToEmpty(detalleNota.idRepVenta)+","+StringUtils.trimToEmpty(lote)
      if( detalleNota.idRepVenta.startsWith(",") ){
          detalleNota.idRepVenta = detalleNota.idRepVenta.replaceFirst( ",","" )
      }
      detalleNotaVentaRepository.save( detalleNota )
      detalleNotaVentaRepository.flush()
    }
  }



  @Override
  Boolean validaLote( String idFactura, Integer idArticulo, String lote ){
    Boolean valido = true
    DetalleNotaVenta det = detalleNotaVentaRepository.findByIdFacturaAndIdArticulo( idFactura, idArticulo )
    if( det != null ){
      String[] lotes = StringUtils.trimToEmpty(det.idRepVenta).split(",")
      for(String l : lotes){
        if( l.equalsIgnoreCase(lote) ){
          valido = false
        }
      }
    } else {
      valido = false
    }
    return valido
  }




  @Override
  Boolean existePromoEnOrden( String idFactura, Integer idPromo ){
    Boolean existPromo = false
    List<OrdenPromDet> lstOrdenPromDet = ordenPromDetRepository.findByIdFactura( idFactura )
    if( lstOrdenPromDet.size() > 0 ){
      if( idPromo == lstOrdenPromDet.first().idPromocion ){
          existPromo = true
        }
    }
    return existPromo
  }

  @Override
  void creaAcusePedidoLc( String idFactura ){
    NotaVenta nota = notaVentaRepository.findByFactura( idFactura ).get(0)
    if( nota != null ){
      PedidoLc pedidoLc = pedidoLcRepository.findOne( nota.factura )
      if( pedidoLc != null ){
          Integer sucursal = Registry.currentSite
          String url = Registry.getURLPedidoLc()
          String argumentos = "${sucursal}-${pedidoLc.id}|${pedidoLc.pedidoLcDets.size()}|"
          Integer contador1 = 0
          for(PedidoLcDet det : pedidoLc.pedidoLcDets){
              contador1 = contador1+1
              argumentos = argumentos+"${contador1}|${det.modelo}|${det.curvaBase}|${det.diametro}|${det.esfera}|${det.cilindro}|${det.eje}|${det.color}|${det.cantidad}|"
          }
          url += String.format( '?arg=%s', argumentos )
          log.debug( "Url generada para el pedido: ${ url }" )
          String response = ""

          ExecutorService executor = Executors.newFixedThreadPool(1)
          int timeoutSecs = 15
          final Future<?> future = executor.submit(new Runnable() {
              public void run() {
                  try {
                      response = url.toURL().text
                      response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
                      log.debug( "Respuesta de la llamada Web del pedido: ${ response }" )
                  } catch (Exception e) {
                      throw new RuntimeException(e)
                  }
              }
          })
          try {
              future.get(timeoutSecs, TimeUnit.SECONDS)
          } catch (Exception e) {println e}

          if(StringUtils.trimToEmpty(response).isNumber()){
            pedidoLc.folio = response
          }
          pedidoLc.fechaEnvio = new Date()
          pedidoLcRepository.save( pedidoLc )
          pedidoLcRepository.flush()

          String fichero = "${Registry.archivePath}/${Registry.currentSite}-${pedidoLc.id}.LC"
          log.debug( "Generando Fichero: ${ fichero }" )
          File file = new File( fichero )
          if ( file.exists() ) { file.delete() }
          log.debug( 'Creando archivo de Pedido de LC' )
          Integer contador = 0
          PrintStream strOut = new PrintStream( file )
          StringBuffer sb = new StringBuffer()
          sb.append("${sucursal}-${pedidoLc.id}|${pedidoLc.pedidoLcDets.size()}|")
          for(PedidoLcDet det : pedidoLc.pedidoLcDets){
            contador = contador+1
            sb.append("\n${contador}|${det.modelo}|${StringUtils.trimToEmpty(det.curvaBase)}|${det.diametro}|${det.esfera}|${det.cilindro}|${det.eje}|${det.color}|${det.cantidad}|")
          }
          strOut.println sb.toString()
          strOut.close()
      }
    }
  }


  @Override
  @Transactional
  PedidoLc actualizaFechaRecepcionPedidoLc( String idFactura ){
    PedidoLc pedidoLc = pedidoLcRepository.findOne( StringUtils.trimToEmpty(idFactura) )
    if( pedidoLc != null ){
      pedidoLc.fechaRecepcion = new Date()
      pedidoLc = pedidoLcRepository.save( pedidoLc )
      pedidoLcRepository.flush()
    }
    return pedidoLc
  }


  @Override
  List<PedidoLc> obtienePedidosLcPorEnviar( ){
    List<PedidoLc> lstPedidosEnviar = new ArrayList<>()
    QPedidoLc plc = QPedidoLc.pedidoLc
    List<PedidoLc> lstPedidos = pedidoLcRepository.findAll( plc.fechaEnvio.isNull() ) as List<PedidoLc>
    for(PedidoLc pedidoLc : lstPedidos){
      List<NotaVenta> nota = notaVentaRepository.findByFactura( StringUtils.trimToEmpty(pedidoLc.id) ) as List<NotaVenta>
      if( nota.size() > 0 && nota.get(0).fechaEntrega == null ){
        lstPedidosEnviar.add( pedidoLc )
      }
    }
    return lstPedidosEnviar
  }


  @Override
  @Transactional
  void cargaFoliosPendientesPedidosLc( ){
      Parametro ubicacion = Registry.find( TipoParametro.RUTA_POR_RECIBIR )
      Parametro parametro = parametroRepository.findOne( TipoParametro.RUTA_RECIBIDOS.value )
      log.debug( "Ubicacion Archivo: %s", ubicacion.valor )
      log.debug( "Ubicacion Destino: %s", parametro.valor )

      File source = new File( StringUtils.trimToEmpty(ubicacion.valor) )
      File destination = new File( StringUtils.trimToEmpty(parametro.valor) )
      String idSucursal = StringUtils.trimToEmpty(Registry.currentSite.toString())
      if ( source.exists() && destination.exists() ) {
          source.eachFile() { file ->
              String fileName = file.getName()
              String[] datosFileName = fileName.split("-")
              if ( fileName.endsWith( "LCF" ) &&
                      StringUtils.trimToEmpty(datosFileName[0]).equalsIgnoreCase(idSucursal)) {
                  try {
                      file.eachLine { String pLine ->
                          String[] datos = pLine.split(/\|/)
                          if( datos.length > 1 ){
                            String[] orden = datos[0].split("-")
                            if(orden.length > 1){
                              PedidoLc pedidoLc = pedidoLcRepository.findOne( orden[1] )
                              if( pedidoLc != null &&
                                      (StringUtils.trimToEmpty(pedidoLc.folio).length() <= 0 ||
                                              StringUtils.trimToEmpty(pedidoLc.folio).equalsIgnoreCase("0"))){
                                pedidoLc.folio = datos[1]
                                pedidoLcRepository.save( pedidoLc )
                                pedidoLcRepository.flush()
                              }
                            }
                          }
                      }
                  } catch ( Exception ex ) { System.out.println( ex ) }

                  def newFile = new File( destination, file.name )
                  def moved = file.renameTo( newFile )
              } else {
                  println datosFileName[0]
              }
          }
      }
  }


  @Override
  String[] montoDescuentoNota( String idFactura ){
    String[] descuento = new String[2]
    NotaVenta nota = notaVentaRepository.findOne( idFactura )
    if( nota != null ){
      if( nota.por100Descuento > 0 ){
        List<Descuento> descuentos = descuentoRepository.findByIdFactura( nota.id )
        if( descuentos.size() > 0 ){
          descuento[0] = descuentos.get(0).tipoClave
          descuento[1] = StringUtils.trimToEmpty(nota.montoDescuento.toString())
        }
      } else {
        List<OrdenPromDet> ordenPromDet = ordenPromDetRepository.findByIdFactura( idFactura )
        if( ordenPromDet.size() > 0 ){
          BigDecimal monto = BigDecimal.ZERO
          descuento[0] = ""
          for(OrdenPromDet promo : ordenPromDet){
            Promocion promocion = promocionRepository.findOne( promo.idPromocion )
            if( promocion != null ){
              descuento[0] = descuento[0]+","+ promocion.descripcion
            }
            monto = monto.add(promo.descuentoMonto)
          }
          if( descuento[0].startsWith(",") ){
            descuento[0] = descuento[0].replaceFirst(",","")
          }
          descuento[1] = StringUtils.trimToEmpty(monto.toString())
        } else {
          descuento[0] = ""
          descuento[1] = ""
        }
      }
    }
    return descuento
  }


    @Override
    @Transactional
    void cargaAcusesPedidosLc( ){
        Parametro ubicacion = Registry.find( TipoParametro.RUTA_POR_RECIBIR )
        Parametro parametro = parametroRepository.findOne( TipoParametro.RUTA_RECIBIDOS.value )
        log.debug( "Ubicacion Archivo: %s", ubicacion.valor )
        log.debug( "Ubicacion Destino: %s", parametro.valor )

        File source = new File( StringUtils.trimToEmpty(ubicacion.valor) )
        File destination = new File( StringUtils.trimToEmpty(parametro.valor) )
        String idSucursal = StringUtils.trimToEmpty(Registry.currentSite.toString())
        if ( source.exists() && destination.exists() ) {
            source.eachFile() { file ->
                String fileName = file.getName()
                String[] datosFileName = fileName.split("-")
                if ( fileName.endsWith( "LCA" ) &&
                        StringUtils.trimToEmpty(datosFileName[0]).equalsIgnoreCase(idSucursal)) {
                    try {
                        file.eachLine { String pLine ->
                            String[] datos = pLine.split(/\|/)
                            String[] factura = datos[0].split("-")
                            if( factura.length > 1 ){
                                PedidoLc pedidoLc = pedidoLcRepository.findOne(StringUtils.trimToEmpty(factura[1]))
                                if( pedidoLc != null && pedidoLc.fechaAcuse == null ){
                                    SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
                                    Date fechaAcuse = null;
                                    try {
                                        fechaAcuse = formatoDelTexto.parse(datos[1]);
                                    } catch (ParseException ex) {
                                        println ex.printStackTrace();
                                    }
                                        pedidoLc.fechaAcuse = fechaAcuse
                                        pedidoLcRepository.save( pedidoLc )
                                        pedidoLcRepository.flush()
                                }

                            }
                        }
                    } catch ( Exception ex ) { System.out.println( ex ) }
                    def newFile = new File( destination, file.name )
                    def moved = file.renameTo( newFile )
                } else {
                    println datosFileName[0]
                }
            }
        }
    }



    @Override
    @Transactional
    void entregaPedidoLc( String idPedido ){
      PedidoLc pedidoLc = pedidoLcRepository.findOne( idPedido )
      if( pedidoLc != null ){
        pedidoLc.fechaEntrega = new Date()
        pedidoLcRepository.save( pedidoLc )
        pedidoLcRepository.flush()
      }
    }



  @Override
  @Transactional
  void guardarCuponMv( CuponMv cuponMv ){
    cuponMvRepository.save( cuponMv )
    cuponMvRepository.flush()
  }

  @Override
  @Transactional
  CuponMv actualizarCuponMv( String idFacturaOrigen, String idFacturaDestino, BigDecimal montoCupon, Integer numeroCupon, Boolean ffCupon ){
    QCuponMv qCuponMv = QCuponMv.cuponMv
    CuponMv cuponMv = cuponMvRepository.findOne( qCuponMv.facturaOrigen.eq(idFacturaOrigen).
            and(qCuponMv.facturaDestino.eq(idFacturaDestino)) )
    if( cuponMv != null && StringUtils.trimToEmpty(idFacturaDestino).length() > 0 ){
      cuponMvRepository.delete( cuponMv.claveDescuento )
      cuponMvRepository.flush()
      NotaVenta notaOrigen = notaVentaRepository.findOne( idFacturaOrigen )
      if( notaOrigen == null ){
        List<NotaVenta> lstNotas = notaVentaRepository.findByFactura( idFacturaOrigen )
        if( lstNotas.size() > 0 ){
          notaOrigen = lstNotas.get(0)
        }
      }
      NotaVenta notaDestino = notaVentaRepository.findOne( idFacturaDestino )
      Integer factura = 0
      try{
        factura = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(notaOrigen.factura))
      } catch ( ParseException e ){ println e }
      String clave = StringUtils.trimToEmpty(cuponMv.claveDescuento).length() > 0 ?
          StringUtils.trimToEmpty(cuponMv.claveDescuento) :
          claveAleatoria( StringUtils.trimToEmpty(factura.toString()), StringUtils.trimToEmpty(numeroCupon.toString()) )
      String facturaDestino = StringUtils.trimToEmpty(notaDestino != null ? notaDestino.factura : "")
      Date fechaAplicacion = cuponMv.fechaAplicacion != null ? cuponMv.fechaAplicacion : new Date()
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      calendar.add(Calendar.DAY_OF_YEAR, Registry.diasVigenciaCupon)
      Date fechaVigencia = cuponMv.fechaVigencia != null ? cuponMv.fechaVigencia : calendar.getTime()
      cuponMvRepository.insertClave( clave, notaOrigen.factura, facturaDestino, fechaAplicacion, fechaVigencia )
      cuponMv = cuponMvRepository.findOne(clave)
      cuponMv.montoCupon = montoCupon
      cuponMvRepository.save(cuponMv)
      cuponMvRepository.flush()
      QDescuento qDescuento = QDescuento.descuento
      Descuento descuento = descuentoRepository.findOne( qDescuento.idFactura.eq(notaDestino?.id).
              and(qDescuento.clave.isNull().or(qDescuento.clave.isEmpty())) )
      if( descuento != null ){
        descuento.clave = cuponMv.claveDescuento
        descuento.tipoClave = cuponMv.claveDescuento
        descuentoRepository.save( descuento )
        descuentoRepository.flush()
      }
    } else {
      NotaVenta notaOrigen = notaVentaRepository.findOne( idFacturaOrigen )
      if( notaOrigen == null ){
        List<NotaVenta> lstNotas = notaVentaRepository.findByFactura( idFacturaOrigen )
        if( lstNotas.size() > 0 ){
          notaOrigen = lstNotas.get(0)
        }
      }
      Integer factura = 0
      try{
        factura = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(notaOrigen.factura))
      } catch ( ParseException e ){ println e }
      String clave = claveAleatoria( StringUtils.trimToEmpty(factura.toString()), StringUtils.trimToEmpty(numeroCupon.toString()) )
      if( ffCupon ){
        clave = clave.replaceFirst(clave.charAt(0).toString(),"F")
      }
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      calendar.add(Calendar.DAY_OF_YEAR, ffCupon ? Registry.diasVigenciaCuponFF : Registry.diasVigenciaCupon)
      Date fechaVigencia = calendar.getTime()
      cuponMv = new CuponMv()
      cuponMv.claveDescuento = clave
      cuponMv.facturaOrigen = StringUtils.trimToEmpty(notaOrigen.factura)
      cuponMv.facturaDestino = ''
      cuponMv.fechaAplicacion = null
      cuponMv.fechaVigencia = fechaVigencia
      cuponMv.montoCupon = montoCupon
      cuponMvRepository.save( cuponMv )
      cuponMvRepository.flush()
    }
    return cuponMv
  }


  @Override
  CuponMv obtenerCuponMv( String factura ){
    QCuponMv qCuponMv = QCuponMv.cuponMv
    return cuponMvRepository.findOne( qCuponMv.facturaDestino.eq(factura) )
  }


  @Override
  CuponMv obtenerCuponMvFuente( String factura ){
    QCuponMv qCuponMv = QCuponMv.cuponMv
    List<CuponMv> cuponMv = cuponMvRepository.findAll( qCuponMv.facturaOrigen.eq(factura).
            and(qCuponMv.fechaAplicacion.isNull()).and(qCuponMv.facturaDestino.isEmpty().
            or(qCuponMv.facturaDestino.isNull())) )
    return cuponMv.size() > 0 ? cuponMv.get(0) : null
  }

  @Override
  CuponMv obtenerCuponMvClave( String clave ){
    return cuponMvRepository.findOne( clave )
  }

  @Override
  List<CuponMv> obtenerCuponMvFacturaOri( String factura ){
    QCuponMv qCuponMv = QCuponMv.cuponMv
    return cuponMvRepository.findAll( qCuponMv.facturaOrigen.eq(factura).and(qCuponMv.fechaAplicacion.isNull()).
            and(qCuponMv.facturaDestino.isNull().or(qCuponMv.facturaDestino.isEmpty())) ) as List<CuponMv>
  }

  @Override
  List<CuponMv> obtenerCuponMvFacturaDest( String factura ){
    QCuponMv qCuponMv = QCuponMv.cuponMv
    return cuponMvRepository.findAll( qCuponMv.facturaDestino.eq(factura) ) as List<CuponMv>
  }


  @Override
  @Transactional
  void actualizarCuponMvPorClave( String idFacturaDestino, String clave ){
    CuponMv cuponMv1 = cuponMvRepository.findOne( clave )
    if( cuponMv1 != null ){
      NotaVenta notaVenta1 = notaVentaRepository.findOne( idFacturaDestino )
      if( notaVenta1 != null ){
        String factura = StringUtils.trimToEmpty(notaVenta1.factura).length() > 0 ? StringUtils.trimToEmpty(notaVenta1.factura) : notaVenta1.id
        cuponMv1.facturaDestino = StringUtils.trimToEmpty( factura )
        cuponMv1.fechaAplicacion = new Date()
        cuponMvRepository.save( cuponMv1 )
        cuponMvRepository.flush()
      }
    }
  }


  @Override
  BigDecimal cuponValid( Integer idCliente ){
    BigDecimal montoCupon = BigDecimal.ZERO
    List<NotaVenta> lstNotasCliente = new ArrayList<>()
    if( Registry.genericCustomer.id != idCliente ){
        List<NotaVenta> notaVenta = notaVentaRepository.findByIdCliente( idCliente )
        Collections.sort(notaVenta, new Comparator<NotaVenta>() {
            @Override
            int compare(NotaVenta o1, NotaVenta o2) {
                return o1.fechaHoraFactura.compareTo(o2.fechaHoraFactura)
            }
        })
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy")
        for(NotaVenta nota : notaVenta){
            if( !nota.sFactura.equalsIgnoreCase(TAG_NOTA_CANCELADA) &&
                    StringUtils.trimToEmpty(nota.factura).length() > 0 &&
                    df.format(new Date()).equalsIgnoreCase(df.format(nota.fechaHoraFactura)) ){
                lstNotasCliente.add(nota)
            }
        }
        if(lstNotasCliente.size() == 1){
            montoCupon = obtenerMontoCupon( lstNotasCliente.get(0).id )
        } else if(lstNotasCliente.size() == 2) {
            montoCupon = obtenerMontoCuponTercerPar( lstNotasCliente.get(0).id )
        }
    }
    return montoCupon
  }


  @Override
  String orderSource( Integer idCliente ){
      String orderSource = ""
      List<NotaVenta> lstNotasCliente = new ArrayList<>()
      if( Registry.genericCustomer.id != idCliente ){
          List<NotaVenta> notaVenta = notaVentaRepository.findByIdCliente( idCliente )
          Collections.sort(notaVenta, new Comparator<NotaVenta>() {
              @Override
              int compare(NotaVenta o1, NotaVenta o2) {
                  return o1.fechaHoraFactura.compareTo(o2.fechaHoraFactura)
              }
          })
          SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy")
          for(NotaVenta nota : notaVenta){
              if( !nota.sFactura.equalsIgnoreCase(TAG_NOTA_CANCELADA) &&
                      StringUtils.trimToEmpty(nota.factura).length() > 0 &&
                      df.format(new Date()).equalsIgnoreCase(df.format(nota.fechaHoraFactura)) ){
                  lstNotasCliente.add(nota)
              }
          }
          if(lstNotasCliente.size() == 1 || lstNotasCliente.size() == 2){
              orderSource = StringUtils.trimToEmpty(lstNotasCliente.get(0).factura)
          } /*else if(lstNotasCliente.size() == 2){
              println "Cupon tercer par"
          }*/
      }
      return orderSource
  }



  @Override
  @Transactional
  void eliminarCUponMv( String idFactura ){
    QCuponMv qCuponMv = QCuponMv.cuponMv
    List<CuponMv> cuponesMv = cuponMvRepository.findAll( qCuponMv.facturaDestino.eq(idFactura) )
    for( CuponMv cuponMv1 : cuponesMv ){
      cuponMv1.facturaDestino = ""
      cuponMv1.fechaAplicacion = null
      cuponMvRepository.save( cuponMv1 )
      cuponMvRepository.flush()
    }
    QDescuento qDescuento = QDescuento.descuento
    List<Descuento> lstDescuentos = descuentoRepository.findAll( qDescuento.idFactura.eq(idFactura) )
    for(Descuento descuento : lstDescuentos){
      descuentoRepository.delete(descuento.id)
      descuentoRepository.flush()
    }
  }



  @Override
  Boolean cuponMvEsApplicable( Integer idCliente, String factura ){
    Boolean valid = false
    CuponMv cuponMv1 = obtenerCuponMv( factura )
    if( cuponMv1 == null && Registry.genericCustomer != idCliente){
      List<NotaVenta> lstNotas = notaVentaRepository.findByIdCliente( idCliente )
      Collections.sort(lstNotas, new Comparator<NotaVenta>() {
          @Override
          int compare(NotaVenta o1, NotaVenta o2) {
              return o1.fechaHoraFactura.compareTo(o2.fechaHoraFactura)
          }
      })
      for(NotaVenta nota : lstNotas){
        if(!TAG_NOTA_CANCELADA.equalsIgnoreCase(nota.sFactura) && StringUtils.trimToEmpty(nota.factura).length() > 0){
          CuponMv cuponMv = obtenerCuponMvFuente( StringUtils.trimToEmpty(nota.factura) )
          if( cuponMv != null && StringUtils.trimToEmpty(cuponMv.facturaDestino).length() <= 0
                  && new Date().before(cuponMv.fechaVigencia) ){
            valid = true
            break
          }
        }
      }
    }
    return valid
  }



  static String claveAleatoria(String factura, String numeroPar) {
      String digitos = "" + numeroPar+"0"
      String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
      String resultado = digitos?.padLeft( 1, '0' ) + factura
      for (int i = 0; i < resultado.size(); i++) {
        int numAleatorio = (int) (Math.random() * abc.size());
        if (resultado.charAt(i) == '0') {
          resultado = replaceCharAt(resultado, i, abc.charAt(numAleatorio))
        } else {
          int numero = Integer.parseInt ("" + resultado.charAt(i));
          numero = 10 - numero
          char diff = Character.forDigit(numero, 10);
          resultado = replaceCharAt(resultado, i, diff)
        }
      }
      return resultado;
    }

    static String replaceCharAt(String s, int pos, char c) {
        StringBuffer buf = new StringBuffer( s );
        buf.setCharAt( pos, c );
        return buf.toString( );
    }

  @Override
  CuponMv obtenerCuponMv( String idFacturaOrigen, String idFacturaDestino ){
    QCuponMv qCuponMv = QCuponMv.cuponMv
    CuponMv cuponMv1 = cuponMvRepository.findOne( qCuponMv.facturaOrigen.eq(idFacturaOrigen).
            and(qCuponMv.facturaDestino.isNull().or(qCuponMv.facturaDestino.isEmpty())) )
    return cuponMv1
  }


  @Override
  @Transactional
  void eliminarCuponMultipago( String idFactura ){
    NotaVenta notaVenta1 = notaVentaRepository.findOne( idFactura )
    QCuponMv qCuponMv = QCuponMv.cuponMv
    QDescuento qDescuento = QDescuento.descuento
    CuponMv cuponMv1 = cuponMvRepository.findOne( qCuponMv.facturaDestino.eq(idFactura) )
    Descuento descuento = descuentoRepository.findOne( qDescuento.idFactura.eq(idFactura).
            and(qDescuento.clave.isNull().or(qDescuento.clave.isEmpty())) )
    if( notaVenta1 != null && cuponMv1 != null && descuento != null ){
      for(DetalleNotaVenta det : notaVenta1.detalles){
        det.precioUnitFinal = det.precioUnitLista
        det.precioFactura = det.precioCalcLista
        detalleNotaVentaRepository.save( det )
        detalleNotaVentaRepository.flush()
      }
      notaVenta1.por100Descuento = 0
      notaVenta1.ventaNeta = notaVenta1.ventaNeta.add(notaVenta1.montoDescuento)
      notaVenta1.ventaTotal = notaVenta1.ventaTotal.add(notaVenta1.montoDescuento)
      notaVenta1.montoDescuento = BigDecimal.ZERO
      notaVenta1.tipoDescuento = ""
      notaVentaRepository.save( notaVenta1 )
      notaVentaRepository.flush()
      cuponMvRepository.delete( cuponMv1.claveDescuento )
      cuponMvRepository.flush()
      descuentoRepository.delete( descuento.id )
      descuentoRepository.flush()
    }
  }


  @Override
  @Transactional
  void eliminaPromocion( String idFactura ){
    NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
    if( notaVenta != null ){
      List<Descuento> descuentos = descuentoRepository.findByIdFactura( StringUtils.trimToEmpty(notaVenta.id) )
      if( descuentos.size() > 0 ){
        for(Descuento descuento : descuentos){
          descuentoRepository.delete( descuento.id )
          descuentoRepository.flush()
        }
        BigDecimal monto = BigDecimal.ZERO
        for(DetalleNotaVenta det : notaVenta.detalles){
          monto = monto.add(det.precioUnitLista)
          det.precioUnitFinal = det.precioUnitLista
          det.precioFactura = det.precioCalcLista
          detalleNotaVentaRepository.save( det )
          detalleNotaVentaRepository.flush()
        }
        notaVenta.ventaTotal = monto
        notaVenta.ventaNeta = monto
        notaVenta.por100Descuento = 0
        notaVenta.montoDescuento = BigDecimal.ZERO
        notaVentaRepository.save( notaVenta )
        notaVentaRepository.flush()
      }
      List<OrdenPromDet> promociones = ordenPromDetRepository.findByIdFactura( idFactura )
      if(promociones.size() > 0){
        for(OrdenPromDet promo : promociones){
          ordenPromDetRepository.delete( promo.idOrdenPromDet )
          ordenPromDetRepository.flush()
        }
        BigDecimal monto = BigDecimal.ZERO
        for(DetalleNotaVenta det : notaVenta.detalles){
          monto = monto.add( det.precioUnitLista )
          det.precioUnitFinal = det.precioUnitLista
          det.precioFactura = det.precioCalcLista
          detalleNotaVentaRepository.save( det )
          detalleNotaVentaRepository.flush()
        }
        notaVenta.ventaTotal = monto
        notaVenta.ventaNeta = monto
        notaVenta.por100Descuento = 0
        notaVenta.montoDescuento = BigDecimal.ZERO
        notaVentaRepository.save( notaVenta )
        notaVentaRepository.flush()
      }
    }
  }


  @Override
  Boolean cuponGeneraCupon( String claveCupon ){
    Boolean generaCupon = true
    DescuentoClave descuentoClave = descuentoClaveRepository.findOne( claveCupon )
    if( descuentoClave != null ){
      generaCupon = descuentoClave.cupon
    }
    return generaCupon
  }


  @Override
  String claveDescuentoNota( String idFactura ){
    String clave = ""
    QDescuento qDescuento = QDescuento.descuento
    Descuento descuento = descuentoRepository.findOne( qDescuento.idFactura.eq(idFactura) )
    if( descuento != null ){
      clave = StringUtils.trimToEmpty(descuento.clave)
    }
    return clave
  }


  @Override
  String esReusoPedidoLc( String idFactura ){
    Boolean esPedidoValido = false
    PedidoLc pedidoLc = pedidoLcRepository.findOne( idFactura )
    if( pedidoLc != null ){
      List<Modificacion> modificacion = modificacionRepository.findByIdFactura( idFactura )
      Boolean isCancelled = false
      for(Modificacion modificacion1 : modificacion){
        if( StringUtils.trimToEmpty(modificacion1.tipo).equalsIgnoreCase("can") &&
                StringUtils.trimToEmpty(modificacion1.causa).equalsIgnoreCase(TAG_CAUSA_CAN_PAGOS) ){
          esPedidoValido = true
        }
      }
    }
    return esPedidoValido
  }


  @Override
  @Transactional
  void existeDescuentoClave( String clave, String antiguaFactura ){
    Boolean exist = false
    List<Descuento> lstDescuento = descuentoRepository.findByClave( clave )
    for(Descuento descuento : lstDescuento){
      if( StringUtils.trimToEmpty(descuento.idFactura).equalsIgnoreCase(antiguaFactura) ){
        exist = true
      }
    }
    if( exist ){
      NotaVenta notaVenta1 = notaVentaRepository.findOne( antiguaFactura )
      CuponMv cuponMv1 = obtenerCuponMvClave( clave )
      if( cuponMv1 != null ){
          cuponMv1.facturaDestino = StringUtils.trimToEmpty(notaVenta1.factura)
          cuponMv1.fechaAplicacion = notaVenta1.fechaHoraFactura
          cuponMvRepository.save( cuponMv1 )
      }
    }
  }


  @Override
  Boolean diaActualEstaAbierto(){
    Boolean isOpen = true
    CierreDiario cierreDiario = cierreDiarioRepository.findOne( new Date() )
    if( cierreDiario != null ){
      if( StringUtils.trimToEmpty(cierreDiario.estado).equalsIgnoreCase("c") ){
        isOpen = false
      }
    }
    return isOpen
  }



  @Override
  @Transactional
  NotaVenta registrarDetalleNotaVentaEnNotaVentaReasignCupon( String idNotaVenta, DetalleNotaVenta detalleNotaVenta ) {
        log.info( "registrando detalleNotaVenta id: ${detalleNotaVenta?.id} idArticulo: ${detalleNotaVenta?.idArticulo}" )
        log.info( "en notaVenta id: ${idNotaVenta}" )
        NotaVenta notaVenta = obtenerNotaVenta( idNotaVenta )
        if ( StringUtils.isNotBlank( notaVenta?.id ) && detalleNotaVenta?.idArticulo ) {
            detalleNotaVenta.idFactura = idNotaVenta
            detalleNotaVenta.idSucursal = sucursalRepository.getCurrentSucursalId()
            DetalleNotaVenta tmp = detalleNotaVentaRepository.findByIdFacturaAndIdArticulo( idNotaVenta, detalleNotaVenta.idArticulo )
            log.debug( "obtiene detalleNotaVenta existente: ${tmp?.dump()}" )
            if ( tmp?.id ) {
                log.debug( "actualizando detalleNotaVenta con id: ${tmp.id} cantidadFac: ${tmp.cantidadFac}" )
                detalleNotaVenta.id = tmp.id
                detalleNotaVenta.cantidadFac += tmp.cantidadFac
                detalleNotaVenta.idRepVenta = tmp.idRepVenta
                log.debug( "actualizados cantidadFac: ${detalleNotaVenta.cantidadFac}" )
            } else {
                log.debug( "registrando nuevo detalleNotaVenta" )
            }
            //detalleNotaVenta = establecerPrecios( detalleNotaVenta )
            try {
                detalleNotaVenta = detalleNotaVentaRepository.save( detalleNotaVenta )
                log.debug( "detalleNotaVenta registrado id: ${detalleNotaVenta.id}" )
                return registrarNotaVenta( notaVenta )
            } catch ( ex ) {
                log.error( "problema al registrar detalleNotaVenta: ${detalleNotaVenta?.dump()}", ex )
            }
        } else {
            log.warn( "no se registra detalleNotaVenta, parametros invalidos" )
        }
        return null
  }


    @Override
    List<CuponMv> obtenerCuponMvFacturaOriApplied( String factura ){
        QCuponMv qCuponMv = QCuponMv.cuponMv
        return cuponMvRepository.findAll( qCuponMv.facturaOrigen.eq(factura).and(qCuponMv.fechaAplicacion.isNotNull()).
                and(qCuponMv.facturaDestino.isNotNull().or(qCuponMv.facturaDestino.isNotEmpty())) ) as List<CuponMv>
    }

  @Override
  @Transactional
  Pago actualizarPagoEnNotaVenta( String idNotaVenta, Pago pago ){
      log.info( "registrando pago id: ${pago?.id} idFormaPago: ${pago?.idFormaPago} monto: ${pago?.monto}" )
      log.info( "en notaVenta id: ${idNotaVenta}" )
      NotaVenta notaVenta = obtenerNotaVenta( idNotaVenta )
      if ( StringUtils.isNotBlank( notaVenta?.id ) && StringUtils.isNotBlank( pago?.idFormaPago ) && pago?.monto ) {
          String formaPago = pago.idFormaPago
          if ( 'ES'.equalsIgnoreCase( formaPago ) ) {
              formaPago = 'EFM'
          } else if ( 'TS'.equalsIgnoreCase( formaPago ) ) {
              formaPago = 'TCM'
          }
          log.debug( "forma pago definida: ${formaPago}" )
          Date fechaActual = new Date()
          pago.idFormaPago = formaPago
          pago.idFactura = idNotaVenta
          pago.idSucursal = sucursalRepository.getCurrentSucursalId()
          //pago.tipoPago = DateUtils.isSameDay( notaVenta.fechaHoraFactura ?: fechaActual, fechaActual ) ? 'a' : 'l'
          log.debug( "obteniendo existencia de pago con id: ${pago.id}" )
          Pago tmp = pagoRepository.findOne( pago.id ?: 0 )
          if ( tmp?.id ) {
            log.debug( "registrando pago con monto: ${pago.monto}" )
            try {
              pago = pagoRepository.save( pago )
              log.debug( "pago registrado id: ${pago.id}" )
              registrarNotaVenta( notaVenta )
              return  pago
            } catch ( ex ) {
              log.error( "problema al registrar pago: ${pago?.dump()}", ex )
            }
          } else {
            log.debug( "Error al actualizar pago" )
          }
      } else {
          log.warn( "no se registra pago, parametros invalidos" )
      }
      return null
  }


  @Override
  PedidoLc obtienePedidoLc( String idFactura ){
    return pedidoLcRepository.findOne( idFactura )
  }

    @Override
    NotaVenta obtenerUltimaNotaVentaPorCliente( Integer id ){
        log.debug( "obtenerUltimaNotaVentaPorCliente( $id )" )

        if ( id == null ) {
            return null
        }

        QNotaVenta nv = QNotaVenta.notaVenta
        List<NotaVenta> notasTmp = notaVentaRepository.findAll( nv.idCliente.eq(id), nv.fechaHoraFactura.desc() )

        NotaVenta nota = null;


        for(NotaVenta notv : notasTmp){
            nota = notv
            break
        }

        return nota
    }


    @Override
    List<NotaVenta> obtenerNotaVentaPorClienteFF( Integer idCliente ){
        log.debug( "obtenerNotaVentaPorCliente(  )" )
        Date fechaStart = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH )
        Date fechaEnd = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 )
        List<NotaVenta> lstNotas = new ArrayList<>()
        List<NotaVenta> notas = new ArrayList<>()
        QNotaVenta nv = QNotaVenta.notaVenta
        List<NotaVenta> notasTmp = notaVentaRepository.findAll( nv.idCliente.eq(idCliente).
                and(nv.fechaEntrega.between(fechaStart,fechaEnd)).and(nv.sFactura.ne('T')).
                and(nv.factura.isNotEmpty()).and(nv.factura.isNotNull()), nv.fechaHoraFactura.asc(), nv.ventaTotal.asc() )
        return notasTmp
    }

    @Override
    List<CuponMv> obtenerCuponMvFacturaOriFF( String factura ){
        QCuponMv qCuponMv = QCuponMv.cuponMv
        return cuponMvRepository.findAll( qCuponMv.facturaOrigen.eq(factura), qCuponMv.fechaVigencia.desc() ) as List<CuponMv>
    }


  @Override
  List<NotaVenta> obtenerNotaVentaPorFecha( Date fecha ){
    Date fechaInicio = DateUtils.truncate( fecha, Calendar.DAY_OF_MONTH );
    Date fechaFin = new Date( DateUtils.ceiling( fecha, Calendar.DAY_OF_MONTH ).getTime() - 1 );
    QNotaVenta qNotaVenta = QNotaVenta.notaVenta
    return notaVentaRepository.findAll( qNotaVenta.factura.isNotNull().and(qNotaVenta.factura.isNotEmpty()).
            and(qNotaVenta.fechaHoraFactura.between(fechaInicio, fechaFin)) )
  }


}