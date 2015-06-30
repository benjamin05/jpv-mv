package mx.lux.pos.service.impl

import com.ibm.icu.text.RuleBasedNumberFormat
import com.mysema.query.BooleanBuilder
import groovy.util.logging.Slf4j
import mx.lux.pos.model.*
import mx.lux.pos.repository.*
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.*
import mx.lux.pos.service.business.InventorySearch
import mx.lux.pos.service.business.Registry
import mx.lux.pos.util.CustomDateUtils
import mx.lux.pos.util.CustomDateUtils as MyDateUtils
import mx.lux.pos.util.MoneyUtils
import mx.lux.pos.util.SubtypeCouponsUtils
import org.apache.commons.lang.WordUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.commons.lang3.time.DateUtils
import org.apache.velocity.app.VelocityEngine
import org.springframework.format.number.CurrencyFormatter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.velocity.VelocityEngineUtils

import javax.annotation.Resource
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

@Slf4j
@Service( 'ticketService' )
@Transactional( readOnly = true )
class TicketServiceImpl implements TicketService {

  private static final String DATE_FORMAT = 'dd-MM-yyyy'
  private static final String TIME_FORMAT = 'HH:mm:ss'
  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm:ss'
  private static final String TAG_CANCELADO = 'C'
  private static final String TAG_FACTURA_CANCELADA = 'T'
  private static final String TAG_DEVUELTO = 'D'
  private static final Integer LONGITUD_MAXIMA = 70
  private static final String TAG_EFD = 'EFD'
  private static final String TAG_EFECTIVO = 'EF'
  private static final String TAG_TARJETA_DEBITO = 'TD'
  private static final String TAG_TARJETA_CREDITO = 'TC'
  private static final String TAG_TRANSFER = 'TR'
  private static final String TAG_DEPOSITO_MN = 'EFECTIVO'
  private static final String TAG_DEPOSITO_US = 'DOLARES'
  private static final String TAG_GENERICO_ARMAZON = 'A'
  private static final String TAG_GENERICO_INV = 'A,H'
  private static final String TAG_GENERICO_H = 'H'
  private static final String TAG_DF = '09'

  private static final BigDecimal CERO_BIGDECIMAL = 0.005

  @Resource
  private ArticuloRepository articuloRepository

  @Resource
  private MonedaExtranjeraService monedaExtranjeraService

  @Resource
  private PedidoLcRepository pedidoLcRepository

  @Resource
  private CuponMvRepository cuponMvRepository

  @Resource
  private RepRepository repRepository

  @Resource
  private CiudadesRepository ciudadesRepository

  @Resource
  private MunicipioRepository municipioRepository

  @Resource
  private MensajeTicketRepository mensajeTicketRepository

  @Resource
  private PrecioRepository precioRepository

  @Resource
  private ParametroRepository parametroRepository

  @Resource
  private NotaVentaRepository notaVentaRepository

  @Resource
  private OrdenPromDetRepository ordenPromDetRepository

  @Resource
  private NotaVentaService notaVentaService

  @Resource
  private DetalleNotaVentaRepository detalleNotaVentaRepository

  @Resource
  private TransInvRepository transInvRepository

  @Resource
  private TransInvDetalleRepository transInvDetalleRepository

  @Resource
  private DevolucionRepository devolucionRepository

  @Resource
  private CierreDiarioRepository cierreDiarioRepository

  @Resource
  private GenericoRepository genericoRepository

  @Resource
  private ResumenDiarioRepository resumenDiarioRepository

  @Resource
  private CotizaDetRepository cotizaDetRepository

  @Resource
  private AperturaRepository aperturaRepository

  @Resource
  private MonedaDetalleRepository monedaDetalleRepository

  @Resource
  private DescuentoRepository descuentoRepository

  @Resource
  private ExamenRepository examenRepository

  @Resource
  private PromocionRepository promocionRepository

  @Resource
  private PagoRepository pagoRepository


  @Resource
  private TipoPagoRepository tipoPagoRepository
  @Resource
  private PagoExternoRepository pagoExternoRepository

  @Resource
  private DepositoRepository depositoRepository

  @Resource
  private ResumenTerminalRepository resumenTerminalRepository

  @Resource
  private EmpleadoRepository empleadoRepository

  @Resource
  private EntregadoExternoRepository entregadoExternoRepository

  @Resource
  private ModificacionRepository modificacionRepository

  @Resource
  private SucursalRepository sucursalRepository

  @Resource
  private ClienteRepository clienteRepository

  @Resource
  private ComprobanteService comprobanteService

  @Resource
  private ContribuyenteService contribuyenteService

  @Resource
  private EstadoService estadoService

  @Resource
  private VelocityEngine velocityEngine

  @Resource
  private ReimpresionRepository reimpresionRepository

  @Resource
  private RecetaRepository  recetaRepository

  private File generaTicket( String template, Map<String, Object> items ) {
    log.info( "generando archivo de ticket con plantilla: ${template}" )
    if ( StringUtils.isNotBlank( template ) && items?.any() ) {
      try {
        String fileName = items.nombre_ticket ?: 'ticket'
        def file = File.createTempFile( fileName, null )
        file.withWriter { BufferedWriter writer ->
          items.writer = writer
          VelocityEngineUtils.mergeTemplate( velocityEngine, template, "ASCII", items, writer )
          true
        }
        log.debug( "archivo generado en: ${file.path}" )
        return file
      } catch ( ex ) {
        log.error( "error al generar archivo de ticket: ${ex.message}", ex )
      }
    } else {
      log.warn( "parametros no validos ${template}/${items}" )
    }
    return null
  }

  private void imprimeTicket( String template, Map<String, Object> items ) {
    File ticket = generaTicket( template, items )

      if ( ticket?.exists() ) {
      try {
        def parametro = parametroRepository.findOne( TipoParametro.IMPRESORA_TICKET.value )
        def cmd = "${parametro?.valor} " + "${ticket.path}"
        log.info( "ejecuta: ${cmd}" )


                 //Evita pasmarse cuando no hay impresora conectada
          try
          {
              def proc = cmd.execute()
              int exitVal = proc.exitValue();
              println("Process exitValue: " + exitVal);
              proc.waitFor()

          } catch (Throwable t)
          {

          }

                //Evita pasmarse cuando no hay impresora conectada


      } catch ( ex ) {
        log.error( "error durante la ejecucion del comando de impresion: ${ex.message}", ex )
      }
    } else {
      log.warn( "archivo de ticket no generado, no se puede imprimir" )
    }
  }

  @Override
  void imprimePago(String orderId, Integer pagoId){
      NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( orderId )
      Pago pagoN = pagoRepository.findOne(pagoId)

      if ( StringUtils.isNotBlank( notaVenta?.id ) ) {

         TipoPago tpago = tipoPagoRepository.findOne(pagoN?.idFPago)
          BigDecimal ant  = 0
          List<Pago> listaPagos = pagoRepository.findByIdFactura(orderId)
          Iterator iterator = listaPagos.iterator();
          while (iterator.hasNext()) {
              Pago pago = iterator.next()
              if(pago.idRecibo.trim() != pagoN?.idRecibo.trim()) {
                 ant = ant + pago.monto
              }

          }

          String nSaldo = '$' + (notaVenta?.ventaTotal-(ant  + pagoN?.monto)).toString()

      def pago = [
              recibo: pagoN?.idRecibo,
              tipoPago: tpago?.descripcion,
              factura: notaVenta?.factura,
              monto: '$'+pagoN?.monto,
              anterior: '$' + ant,
              parcialidad:'$'+pagoN?.monto,
              noParcialidad: (pagoN?.parcialidad.trim().toInteger() - 1).toString(),
              nuevoSaldo:nSaldo
      ]
      def sucu = [
           nombre: notaVenta?.sucursal?.nombre ,
           direccion: notaVenta?.sucursal?.direccion,
           colonia:notaVenta?.sucursal?.colonia ,
           telefono: notaVenta?.sucursal?.telefonos,
           ciudad:notaVenta?.sucursal?.ciudad
      ]
          Date fechaE = new Date()
         SimpleDateFormat fecha = new SimpleDateFormat("dd-MM-yy")
          String fechaExp = fecha.format(fechaE)
      def exp =[
           fecha:fechaExp,
           atendio: notaVenta?.empleado?.nombreCompleto
      ]
          def cli = [
                  nombre: notaVenta?.cliente?.nombreCompleto,
                  domicilio: notaVenta?.cliente?.direccion,
                  telefono: notaVenta?.cliente.telefonoCasa
          ]

          AddressAdapter companyAddress = Registry.companyAddress

      def items = [
         pagoN: pago,
         sucursal:sucu,
         expedicion: exp,
         cliente: cli,
         montoTotal:'$'+notaVenta?.ventaTotal,
         compania: companyAddress

      ]
      imprimeTicket( 'template/ticket-pago.vm', items )
      }
  }

  @Override
 void imprimeRx(String orderId, Boolean reimp){
      NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( orderId )
      if ( StringUtils.isNotBlank( notaVenta?.id ) ) {
          String numero = ''
          BigInteger primerTicket = 0
          if(reimp == true){
                Reimpresion reimpresion  =  new Reimpresion('Rx',notaVenta?.id, new Date(),notaVenta?.empleado?.id,notaVenta?.factura )
                reimpresionRepository.saveAndFlush(reimpresion)
                 primerTicket = reimpresionRepository.noReimpresiones(notaVenta?.factura).toInteger()
          if(primerTicket.toInteger() >= 1){
                numero = 'COPIA ' + (primerTicket.toInteger()).toString()

          }
          }else{
              numero = ''
          }
            String pTicket = ''
             if( primerTicket != 0){
                 pTicket = primerTicket.toString()
              }

        def idTicket = [
                sucursal: notaVenta?.sucursal?.id.toString(),
                factura: notaVenta?.factura,
                id : notaVenta?.sucursal?.id.toString() + notaVenta?.factura + pTicket+'RX',
                noCopia: numero
        ]
          Date fechaA = new Date()
          SimpleDateFormat fecha = new SimpleDateFormat("dd/MMMM/yyyy")
          String fechaImpresion = fecha.format(fechaA)

          Receta rx = recetaRepository.findById(notaVenta?.receta)
          Date fechaS = rx?.fechaReceta != null ? rx?.fechaReceta : new Date()
          fecha = new SimpleDateFormat("dd-MM-yy")
          String fechaSolicitada = fecha.format(fechaS)

          Date horaA = new Date()
          SimpleDateFormat hora = new SimpleDateFormat("H:mm:ss")
          String horaImpresion = hora.format(horaA)

          Date fechaP = notaVenta?.fechaPrometida
          String fechaPrometida = fechaP != null ? fecha.format(fechaP) : ''

          Empleado opto = empleadoRepository.findById(rx?.idOptometrista)
          String optometrista = ''
          if( opto != null ){
            optometrista = opto != null ? opto?.nombreCompleto : '' + ' [' + opto != null ? opto?.id.trim() : '' + ']'
          }

          def infoGeneral = [
                sucursal: notaVenta?.sucursal?.nombre + ' ['+ notaVenta?.sucursal?.id +']',
                fechaActual: fechaImpresion,
                fechaSolicitud: fechaSolicitada,
                horaActual: horaImpresion,
                fechaPrometida: fechaPrometida,
                soi: notaVenta?.id,
                receto: optometrista,
                atendio: notaVenta?.empleado?.nombreCompleto

        ]
          String trat = ''


          DetalleNotaVenta artArmazon = new DetalleNotaVenta()
          List<DetalleNotaVenta> articulos = detalleNotaVentaRepository.findByIdFactura(notaVenta?.id)
          String articulo = ''
          Iterator iterator = articulos.iterator();
          while (iterator.hasNext()) {

              DetalleNotaVenta detalle = iterator.next()
              articulo = articulo + ' ' + detalle?.articulo?.articulo + ','

              if(detalle?.articulo?.idGenerico.trim().equals('A')){
                  artArmazon = detalle
              }
              if(detalle?.articulo?.idGenerico.trim().equals('G')){
                  trat =  detalle?.articulo?.descripcion
              }

          }

          def infoCliente = [
                  nombre: notaVenta?.cliente?.nombreCompleto,
                  telCasa: notaVenta?.cliente?.telefonoCasa,
                  telTrab: notaVenta?.cliente?.telefonoTrabajo,
                  extTrab: notaVenta?.cliente?.extTrabajo,
                  telAd: notaVenta?.cliente?.telefonoAdicional,
                  extAd: notaVenta?.cliente?.extAdicional,
                  saldo: notaVenta?.ventaNeta - notaVenta?.sumaPagos,
                  lente: notaVenta?.codigo_lente,
                  articulos: articulo
          ]
              println(artArmazon?.articulo?.articulo +' '+ artArmazon?.articulo?.codigoColor + ' [' + artArmazon?.surte + ']' + '     Armazon')

          String armazonCli = ''
                 if(notaVenta?.fArmazonCli == true){
                     armazonCli = 'ARMAZON DEL CLIENTE'
                 }else{
                     if(artArmazon?.articulo != null){
                    armazonCli = artArmazon?.articulo?.articulo +' '+ artArmazon?.articulo?.codigoColor + ' [' + artArmazon?.surte + ']'
                     }
                 }

                  String usoLente = rx != null ? rx?.sUsoAnteojos.trim() : ''
                  switch (usoLente) {
                    case 'i': usoLente = 'INTERMEDIO'
                        break
                    case 'c': usoLente = 'CERCA'
                        break
                     case 'l': usoLente = 'LEJOS'
                         break
                    case 'b': usoLente = 'BIFOCAL'
                        break
                    case 'p': usoLente = 'PROGRESIVO'
                        break
                    case 't': usoLente = 'BIFOCAL INTERMEDIO'
                        break
                    }

          def detalleLente = [
                  ODEsfer:rx?.odEsfR,
                  ODCil:rx?.odCilR,
                  ODEje:rx?.odEjeR,
                  ODAdd:rx?.odAdcR,
                  ODPris:rx?.odPrismaH + rx?.odPrismaV,

                  OIEsfer:rx?.oiEsfR,
                  OICil:rx?.oiCilR,
                  OIEje:rx?.oiEjeR,
                  OIAdd:rx?.oiAdcR,
                  OIPris:rx?.oiPrismaH + rx?.oiPrismaV,

                  distIntLejos:rx?.diLejosR,
                  distIntCercas:rx?.diCercaR,
                  distMonoD:rx?.diOd,
                  distMonoI:rx?.diOi,
                  alturaSeg:rx?.altOblR,

                  armazon: armazonCli,
                  uso: usoLente,
                  tratamiento: trat,
                  material: notaVenta?.udf2,
                  formaLente: notaVenta?.udf3,
                  surte: artArmazon?.surte

          ]
          Modificacion mod = new Modificacion()
          mod.idFactura = ''
          mod.causa = ''
          for(Pago payment : notaVenta.pagos){
            if(payment.referenciaPago.trim().length() > 0){
              NotaVenta nvOrigen = notaVentaRepository.findOne( payment.referenciaPago.trim() )
              if(nvOrigen != null){
                List<Modificacion> modificaciones = modificacionRepository.findByIdFactura( nvOrigen.id.trim() )
                if(modificaciones.size() > 0){
                  mod = modificaciones.first()
                } else {
                  mod = null
                }
              }
            }
          }
          String factCanc = mod != null && mod.id != null ? mod.notaVenta.factura : ""
          String causa = mod != null && mod.id != null ? mod.causa.trim() : ""
          def coment = [
                 cometRx:factCanc+" "+causa+" "+rx?.observacionesR,
                 cometFactura: notaVenta?.observacionesNv,
                 conSaldo:'',
                 regresoClases:'',
                 ventaPino:''
          ]

          println('IdTicket'+idTicket?.id)

        def items = [
              nombre_ticket: 'ticket-rx',
              codigoBarrasAnchas: idTicket,
              infoTicket: infoGeneral,
              cliente: infoCliente,
              lente: detalleLente,
              comentarios: coment,
              externo: false
            ] as Map<String, Object>

        imprimeTicket( 'template/ticket-rx.vm', items )
      }else{
          log.warn( 'no se imprime ticket rx, parametros invalidos' )
      }
  }

    @Override
    void imprimeSuyo(String idNotaVenta, JbNotas jbNotas){


        NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( idNotaVenta )


        if ( StringUtils.isNotBlank( notaVenta?.id )  &&  StringUtils.isNotBlank( jbNotas?.id_nota.toString() )) {

            Sucursal sucursal = sucursalRepository.findOne(notaVenta?.idSucursal)
            Empleado empleado = empleadoRepository.findOne(notaVenta?.idEmpleado)
            Cliente cliente = clienteRepository.findOne(notaVenta?.idCliente)
            Date fechaA = new Date()
            SimpleDateFormat fecha = new SimpleDateFormat("dd-MM-yyyy")
            String fechaImpresion = fecha.format(fechaA)


            def tienda = [
                    sucursal: sucursal?.nombre + ' [' + sucursal?.id + ']',
                    telefono: sucursal?.telefonos,
                    empleado:  empleado?.nombre,
                    fecha: fechaImpresion
            ]
            def customer = [
                    nombre: cliente?.nombre + ' ' + cliente?.apellidoPaterno + ' ' + cliente?.apellidoMaterno,
                    domicilio: cliente?.direccion,
                    colonia: cliente?.colonia,
                    cp: cliente?.codigo,
                    telCasa: cliente?.telefonoCasa,
                    telTrab: cliente?.telefonoTrabajo,
                    telAd: cliente?.telefonoAdicional,
                    extTrab: cliente?.extTrabajo,
                    extAd: cliente?.extAdicional
            ]

            def dejo = [
                    factura: jbNotas?.id_nota,
                    dejo: jbNotas?.dejo,
                    fechaEntrega: jbNotas?.fecha_prom,
                    servicio: jbNotas?.servicio,
                    instruccion: jbNotas?.instruccion,
                    condiciones: jbNotas?.condicion
            ]

        def items = [
                nombre_ticket: 'ticket-suyo',
                id_nota: jbNotas.id_nota.toString(),
                infoTienda: tienda,
                infoCliente: customer,
                infoDejo: dejo,
                firmaGerente: 'Vo. Bo. Gerente'

        ] as Map<String, Object>

        imprimeTicket( 'template/ticket-suyo.vm', items )
        imprimeTicket( 'template/ticket-suyo.vm', items )
        } else {
            log.warn( 'no se imprime ticket venta, parametros invalidos' )
        }
    }



  @Override
  void imprimeVenta( String idNotaVenta ) {
    imprimeVenta( idNotaVenta, false )
  }

  void imprimeVenta( String idNotaVenta, Boolean pNewOrder ) {
    log.info( "imprimiendo ticket venta de notaVenta id: ${idNotaVenta}" )
    NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( idNotaVenta )
    if ( StringUtils.isNotBlank( notaVenta?.id ) ) {
      NumberFormat formatter = NumberFormat.getCurrencyInstance( Locale.US )
      List<String> lstComentario = new ArrayList<String>()
      String marcasFactura = ''
      String articulosFactura = ''
      String dateTextFormat = "dd 'de' MMMM 'de' yyyy"
      Locale locale = new Locale( 'es' )
      def detalles = [ ]
      List<DetalleNotaVenta> detallesLst = detalleNotaVentaRepository.findByIdFacturaOrderByIdArticuloAsc( idNotaVenta )
        Boolean cupon2Par= false
        BigDecimal monto2Par = BigDecimal.ZERO
        Boolean cupon3Par= false
        BigDecimal monto3Par = BigDecimal.ZERO
        String leyendaCupon = ""
        String cuponLc = ""
        QCuponMv qCuponMv = QCuponMv.cuponMv
        /*List<CuponMv> cuponMv = cuponMvRepository.findAll( qCuponMv.facturaOrigen.eq(notaVenta.factura).
                and(qCuponMv.facturaDestino.isEmpty().or(qCuponMv.facturaDestino.isNull())) ) as List<CuponMv>*/
        //if( cuponMv.size() <= 0 ){
        List<CuponMv> cuponMv = cuponMvRepository.findAll( qCuponMv.facturaOrigen.eq(notaVenta.factura) ) as List<CuponMv>
        //}
      Boolean sameSubtipo = true
      if( cuponMv.size() == 1 && StringUtils.trimToEmpty(cuponMv.first().claveDescuento).startsWith(TAG_GENERICO_H) ){
        if( notaVenta != null ){
          List<DetalleNotaVenta> lstDet = new ArrayList<>(notaVenta.detalles)
          Collections.sort( lstDet, new Comparator<DetalleNotaVenta>() {
              @Override
              int compare(DetalleNotaVenta o1, DetalleNotaVenta o2) {
                  return o1.cantidadFac.compareTo(o2.cantidadFac)
              }
          } )
          String subtipo = ""
          for(DetalleNotaVenta det : lstDet){
            if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase("H") ){
              cuponLc = SubtypeCouponsUtils.getTitle2( det.articulo.subtipo )
              if( subtipo.length() > 0 ){
                if( !StringUtils.trimToEmpty(subtipo).equalsIgnoreCase(StringUtils.trimToEmpty(det.articulo.subtipo) ) ){
                  sameSubtipo = false
                }
              } else {
                subtipo = StringUtils.trimToEmpty(det.articulo.subtipo)
                cuponLc = SubtypeCouponsUtils.getTitle2( det.articulo.subtipo )
              }
            }
          }
        }
      }
      BigDecimal subtotal = BigDecimal.ZERO
      BigDecimal totalArticulos = BigDecimal.ZERO
      Integer contadorLc = 0
      Collections.sort( detallesLst, new Comparator<DetalleNotaVenta>() {
        @Override
        int compare(DetalleNotaVenta o1, DetalleNotaVenta o2) {
          return o1.cantidadFac.compareTo(o2.cantidadFac)
        }
      } )
      detallesLst?.each { DetalleNotaVenta tmp ->
        // TODO: rld review for SOI lux
        // BigDecimal precio = tmp?.precioUnitFinal?.multiply( tmp?.cantidadFac ) ?: 0
        BigDecimal precio = tmp?.precioUnitLista?.multiply( tmp?.cantidadFac ) ?: 0
        subtotal = subtotal.add( precio )
        Boolean cupon = sameSubtipo
        if( StringUtils.trimToEmpty(tmp?.articulo?.idGenerico).equalsIgnoreCase(TAG_GENERICO_H) ){
          contadorLc = contadorLc+tmp?.cantidadFac?.intValue()
          if( contadorLc > 1 ){
            cupon = true
          }
        }
        String descripcion = "[${tmp?.articulo?.articulo}] ${tmp?.surte != null ? '['+tmp?.surte.trim()+']' : ''} ${cupon && cuponLc.length() > 0 ? '['+cuponLc.trim()+']' : ''} ${tmp?.articulo?.descripcion}"
        String descripcion1
        String descripcion2 = ""
        if ( descripcion.length() > 36 ) {
          descripcion1 = descripcion.substring( 0, 36 )
          if ( descripcion.length() > 72 ) {
            descripcion2 = descripcion.substring( 37, 72 )
          } else {
            descripcion2 = descripcion.substring( 37 )
          }
        } else {
          descripcion1 = descripcion
        }

        totalArticulos = totalArticulos.add( tmp.cantidadFac )
        marcasFactura = marcasFactura+","+StringUtils.trimToEmpty(tmp.articulo.marca)
        articulosFactura = articulosFactura+","+StringUtils.trimToEmpty(tmp.articulo.id.toString())
        def detalle = [
            cantidad: tmp?.cantidadFac?.toInteger() ?: '',
            codigo: "${tmp?.articulo?.articulo ?: ''} ${tmp?.articulo?.codigoColor ?: ''}",
            descripcion1: descripcion1,
            descripcion2: descripcion2,
            precio: formatter.format( precio )
        ]
        detalles.add( detalle )
      }
      def pagos = [ ]
      List<Pago> pagosLst = pagoRepository.findByIdFacturaOrderByFechaAsc( idNotaVenta )
      pagosLst?.each { Pago pmt ->
        BigDecimal monto = pmt?.monto ?: 0
        String ref = pmt?.referenciaPago ?: ''
        Integer pos = ( ref.size() >= 4 ) ? ( ref.size() - 4 ) : 0
        String tipoPago
        boolean creditoEmp = pmt?.eTipoPago?.equals( Registry.getTipoPagoCreditoEmpleado() )
        if ( creditoEmp ) {
          tipoPago = pmt?.eTipoPago?.descripcion
        } else {
          tipoPago = "${pmt?.eTipoPago?.descripcion} ${ref.substring( pos )}"
        }
        def pago = [
            tipo_pago: tipoPago,
            monto: formatter.format( monto )
        ]
        pagos.add( pago )

        if ( creditoEmp ) {
          lstComentario.add( String.format( "Empleado: %s", pmt?.clave ) )
          lstComentario.add( String.format( "   # Emp: %s", pmt?.idBancoEmisor ) )
        }
      }
      BigDecimal ventaNeta = notaVenta.ventaNeta ?: 0
      String empleado = String.format( "%s [%s]", notaVenta.empleado.nombreCompleto, StringUtils.trimToEmpty( notaVenta.empleado.id ) )
      RuleBasedNumberFormat textFormatter = new RuleBasedNumberFormat( locale, RuleBasedNumberFormat.SPELLOUT )

      String textoVentaNeta = ( "${textFormatter.format( ventaNeta.intValue() )} PESOS "+ "${ventaNeta.remainder( 1 ).unscaledValue()}/100 M.N." )

      AddressAdapter companyAddress = Registry.companyAddress
      BigDecimal saldo = notaVenta.ventaNeta.subtract(notaVenta.sumaPagos)

      List<String> promociones = new ArrayList<>()
      List<OrdenPromDet> lstPromociones = ordenPromDetRepository.findByIdFactura( notaVenta.id )
      List<String> msjPromo = new ArrayList<>()
        QMensajeTicket mensaje = QMensajeTicket.mensajeTicket
        List<MensajeTicket> lstMensajesTickets = (List<MensajeTicket>)mensajeTicketRepository.findAll( mensaje.fechaFinal.after( new Date() ) )
        for(MensajeTicket msj : lstMensajesTickets){
            Boolean alreadyAdd = false
            Boolean hasBrand = false
            Boolean hasArticle = false
            if( (!StringUtils.trimToEmpty(msj.idLinea).equalsIgnoreCase('') && !StringUtils.trimToEmpty(msj.idLinea).equalsIgnoreCase('*'))||
                    (!StringUtils.trimToEmpty(msj.listaArticulo).equalsIgnoreCase('') && !StringUtils.trimToEmpty(msj.listaArticulo).equalsIgnoreCase('*')) ){
                if( StringUtils.trimToEmpty(msj.idLinea) != '' ){
                    hasBrand = true
                }
                if( StringUtils.trimToEmpty(msj.listaArticulo) != '' ){
                    hasArticle = true
                }
                if( hasBrand ){
                    Boolean validBrand = false
                    String[] marcas = marcasFactura.split(',')
                    for(String marca : marcas){
                        if(StringUtils.trimToEmpty(marca) != '' && msj.idLinea.contains(marca)){
                            validBrand = true
                        }
                    }
                    if( validBrand ){
                        if( hasArticle ){
                            String[] articulos = articulosFactura.split(',')
                            for(String art : articulos){
                                if(!alreadyAdd && StringUtils.trimToEmpty(art) != '' && msj.listaArticulo.contains(art)){
                                    alreadyAdd = true
                                    msjPromo.add(msj.mensaje)
                                }
                            }
                        } else {
                            if(!alreadyAdd){
                                alreadyAdd = true
                                msjPromo.add(msj.mensaje)
                            }
                        }
                    }
                } else if( hasArticle ){
                    String[] articulos = articulosFactura.split(',')
                    for(String art : articulos){
                        if(!alreadyAdd && StringUtils.trimToEmpty(art) != '' && msj.listaArticulo.contains(art)){
                            alreadyAdd = true
                            msjPromo.add(msj.mensaje)
                        }
                    }
                }
            } else if( (StringUtils.trimToEmpty(msj.idLinea).equalsIgnoreCase('') || StringUtils.trimToEmpty(msj.idLinea).equalsIgnoreCase('*'))&&
                    (StringUtils.trimToEmpty(msj.listaArticulo).equalsIgnoreCase('') || StringUtils.trimToEmpty(msj.listaArticulo).equalsIgnoreCase('*')) ){
                msjPromo.add(msj.mensaje)
            }
        }

      for(OrdenPromDet promo : lstPromociones){
          Promocion promocion = promocionRepository.findOne( promo.idPromocion )
          if(promocion != null){
            String data = '['+promocion.idPromocion.toString()+']'+', '+promocion.descripcion
            promociones.add( data )
          }
      }

      if(cuponMv.size() == 1 && StringUtils.trimToEmpty(cuponMv.first().claveDescuento).startsWith(TAG_GENERICO_H) ){
        leyendaCupon = "SOLICITA TU TICKET."
        cupon2Par = true
        monto2Par = cuponMv.get(0).montoCupon
      } else if(cuponMv.size() == 1 && Registry.tirdthPairValid()){
        leyendaCupon = "SOLICITA TU TICKET."
        cupon3Par = true
        monto3Par = cuponMv.get(0).montoCupon
      } else if(cuponMv.size() == 2 && Registry.tirdthPairValid()){
        leyendaCupon = "SOLICITA TUS TICKETS."
        //cupon2Par = true
        monto2Par = Math.max(cuponMv.get(0).montoCupon.doubleValue(),cuponMv.get(1).montoCupon.doubleValue())
        //cupon3Par = true
        monto3Par = Math.min(cuponMv.get(0).montoCupon.doubleValue(),cuponMv.get(1).montoCupon.doubleValue())
        if( monto2Par.compareTo(BigDecimal.ZERO) > 0 && monto3Par.compareTo(BigDecimal.ZERO) > 0 ){
          cupon2Par = true
          cupon3Par = true
        }
      } else {
        if(cuponMv.size() > 0){
          leyendaCupon = "SOLICITA TU TICKET."
          //cupon2Par = true
          monto2Par = cuponMv.size() > 1 ? Math.max(cuponMv.get(0).montoCupon.doubleValue(),cuponMv.get(1).montoCupon.doubleValue()) : cuponMv.get(0).montoCupon
          if( monto2Par.compareTo(BigDecimal.ZERO) > 0 ){
            cupon2Par = true
          }
        }
      }
      String estado = ""
      if( notaVenta.sucursal != null ){
        Rep rep = repRepository.findOne( StringUtils.trimToEmpty(notaVenta.sucursal.idEstado) )
        if( StringUtils.trimToEmpty(notaVenta?.sucursal?.idEstado).equalsIgnoreCase(TAG_DF) ){
          estado = rep.nombre
        } else {
          QMunicipio qMunicipio = QMunicipio.municipio
          List<Municipio> municipios = municipioRepository.findAll( qMunicipio.idEstado.eq(StringUtils.trimToEmpty(notaVenta?.sucursal?.idEstado)).
                  and( qMunicipio.idLocalidad.eq(StringUtils.trimToEmpty(notaVenta?.sucursal?.idLocalidad)) ) )
          String ciudad = ""
          if( municipios.size() > 0 ){
            ciudad = municipios.first().nombre
            if( municipios.first().nombre.contains( "(" ) ){
              String[] data = municipios.first().nombre.split("\\(")
              if( data.length > 1 ){
                        ciudad = data[0]
              }
            }
          }
          estado = StringUtils.trimToEmpty(ciudad)+", "+StringUtils.trimToEmpty( rep.nombre )
        }
      }
      def items = [
          nombre_ticket: 'ticket-venta',
          nota_venta: notaVenta,
          compania: companyAddress,
          despliega_atencion_a_clientes: true,//companyAddress.hasCustomerService(),
          venta_neta: formatter.format( ventaNeta ),
          subtotal: formatter.format( subtotal ),
          descuento: formatter.format( subtotal.subtract( ventaNeta ) ),
          detalles: detalles,
          pagos: pagos,
          articulos: totalArticulos,
          saldo: String.format( '%.2f', saldo.compareTo(BigDecimal.ZERO) > 0 ? saldo : BigDecimal.ZERO ),
          cliente: notaVenta.cliente,
          empleado: empleado,
          sucursal: notaVenta.sucursal,
          observaciones: StringUtils.trimToEmpty(notaVenta.observacionesNv) != '' ? notaVenta.observacionesNv : '',
          fecha: DateFormatUtils.format( notaVenta.fechaHoraFactura, dateTextFormat, locale ),
          hora: new Date().format( TIME_FORMAT ),
          texto_venta_neta: textoVentaNeta.toUpperCase(),
          fecha_entrega: notaVenta?.fechaPrometida ? DateFormatUtils.format( notaVenta.fechaPrometida, dateTextFormat, locale ) : '',
          comentarios: lstComentario,
          mensajesPromo: msjPromo,
          cupon2: cupon2Par,
          cupon3: cupon3Par,
          montoCupon2: formatter.format(monto2Par),
          montoCupon3: formatter.format(monto3Par),
          leyendaCupon: leyendaCupon,
          municipio: StringUtils.trimToEmpty(notaVenta?.sucursal?.municipio?.nombre),
          estado: StringUtils.trimToEmpty(estado)
      ] as Map<String, Object>

      imprimeTicket( 'template/ticket-venta-si.vm', items )
      if ( Registry.isReceiptDuplicate() && pNewOrder ) {
        imprimeTicket( 'template/ticket-venta-si.vm', items )
      }

    } else {
      log.warn( 'no se imprime ticket venta, parametros invalidos' )
    }
  }

  @Override
  boolean imprimeCierreTerminales( Date fechaCierre, List<ResumenDiario> resumenesDiario, Empleado empleado, String terminal ) {
    boolean terminalEmpty = false
    final String CREDIT = 'N'
    final List<String> FX_CARD = ['TCD', 'TDD']
    final List<String> RETURN_LIST = [ 'C', 'D' ]
    final String CREDIT_TAG = 'Cred'
    final String DEBIT_TAG = 'Debito'
    final String FX_TAG = 'USD'
    final String TITLE_USD_TAG = 'Cantidad'
    final String TITLE_MN_TAG = 'Plan'
    NumberFormat formatterMoney = new DecimalFormat( '#,##0.00' )
    List<CierreTerminales> resumenTerminales = new ArrayList<CierreTerminales>()
    if ( terminal.equalsIgnoreCase( 'TODAS' ) ) {
      String tituloPlan
      if ( resumenesDiario.size() > 0 ) {
        for ( ResumenDiario resumen : resumenesDiario ) {
          CierreTerminales terminales = findorCreate( resumenTerminales, resumen.idTerminal )
          terminales.AcumulaTerminales( resumen )
        }
        for ( CierreTerminales cierre : resumenTerminales ) {
            /*Date fechaStart = DateUtils.truncate( fechaCierre, Calendar.DAY_OF_MONTH )
            Date fechaEnd = new Date( DateUtils.ceiling( fechaCierre, Calendar.DAY_OF_MONTH ).getTime() - 1 )
            List<Modificacion> lstModificaciones = modificacionRepository.findByFechaBetween(fechaStart,fechaEnd)
            List<NotaVenta> lstNotas = new ArrayList<>()
            for(Modificacion mod : lstModificaciones){
                NotaVenta nota = notaVentaRepository.findOne( mod.idFactura )
                if( nota != null && nota.sFactura.trim().equalsIgnoreCase(TAG_FACTURA_CANCELADA)){
                    lstNotas.add( nota )
                }
            }
            List<Pago> lstPagos = new ArrayList<>()
            for(NotaVenta nv : lstNotas){
                for(Pago pay : nv.pagos){
                    if( pay.idFPago.equalsIgnoreCase(TAG_TARJETA_DEBITO) || pay.idFPago.equalsIgnoreCase(TAG_TARJETA_CREDITO) ){
                      if( cierre.idTerminal.trim().equalsIgnoreCase( pay.terminal.descripcion.trim() ) ){
                        lstPagos.add( pay )
                      }
                    }
                }
            }
            BigDecimal montoDev = BigDecimal.ZERO
            for(Pago pago : lstPagos){
                List<Devolucion> lstDevoluciones = devolucionRepository.findByIdPago( pago.id )
                for(Devolucion dev : lstDevoluciones){
                    if( dev.tipo.trim().equalsIgnoreCase('d') && dev.idFormaPago.trim().equalsIgnoreCase(TAG_EFECTIVO)){
                      montoDev = montoDev.add( dev.monto )
                    }
                }
            }*/
          BigDecimal total = 0
          BigDecimal totalDolares = BigDecimal.ZERO
          cierre.detTerminales.each { resumenDiario ->
            if ( resumenDiario.plan?.equals( 'C' ) || resumenDiario.plan?.equals( 'D' ) ) {
              total = total - resumenDiario.importe
            } else {
              total = total + resumenDiario.importe
            }
            //total = total.add(montoDev)
          }
          def subtotales = [ ]
          for ( ResumenDiario rd : cierre.detTerminales ) {
            String tipo = StringUtils.trimToEmpty(rd.tipo).toUpperCase()
            String rdPlan = StringUtils.trimToEmpty(rd.plan).toUpperCase()
            String plan
            if( tipo.length() > 0 && Registry.isCardPaymentInDollars(tipo)){
              plan = String.format( '%s %s', rdPlan, FX_TAG )
              tituloPlan = TITLE_USD_TAG
              totalDolares = totalDolares.add( NumberFormat.getInstance().parse(rd.plan) )
            } else {
              plan = rdPlan
              tituloPlan = TITLE_MN_TAG
            }
            if( plan.equalsIgnoreCase('')){
              plan = DEBIT_TAG
            }
            String monto = String.format( '%,.2f', rd.importe )
            CurrencyFormatter formatter = new CurrencyFormatter()
            def sub = [
                term: cierre.idTerminal,
                plan: plan,
                tknum: String.format( '%3s', String.format( '%d', rd.facturas) ),
                rctnum: String.format( '%5s', String.format( '%d', rd.vouchers) ),
                monto: String.format( '%10s', monto )
            ]
            subtotales.add( sub )
          }
          Boolean dolaresValidos = false
          if(totalDolares.compareTo(BigDecimal.ZERO) < 0 || totalDolares.compareTo(BigDecimal.ZERO) > 0 ){
            dolaresValidos = true
          }
          CurrencyFormatter formatter = new CurrencyFormatter()
          def datos = [ nombre_ticket: 'ticket-cierre-terminal',
              fechaCierre: CustomDateUtils.format( fechaCierre, 'dd-MM-yyyy' ),
              terminal: cierre.idTerminal,
              detalle: subtotales,
              titulo: tituloPlan,
              totalDolares: dolaresValidos ? formatterMoney.format( totalDolares ) : '',
              total: formatter.print( total, Locale.getDefault() ),
              thisSite: String.format( '%s [%d]', empleado.sucursal.nombre, empleado.sucursal.id ),
              empleado: empleado.nombreCompleto() ]
          imprimeTicket( 'template/ticket-cierre-terminal.vm', datos )
        }
      } else {
        terminalEmpty = true
      }
    } else {
      String tituloPlan
      BigDecimal totalDolares = BigDecimal.ZERO
      BigDecimal total = 0
      resumenesDiario.each { resumenDiario ->
        if ( resumenDiario.plan?.equals( 'C' ) || resumenDiario.plan?.equals( 'D' ) ) {
          total = total - resumenDiario.importe
        } else {
          total = total + resumenDiario.importe
        }
      }
      def subtotales = [ ]
      for ( ResumenDiario rd : resumenesDiario ) {
        String tipo = StringUtils.trimToEmpty(rd.tipo).toUpperCase()
        String rdPlan = StringUtils.trimToEmpty(rd.plan).toUpperCase()
        String plan
        if( tipo.length() > 0 && Registry.isCardPaymentInDollars(tipo)){
          plan = String.format( '%s %s', rdPlan, FX_TAG )
          tituloPlan = TITLE_USD_TAG
          totalDolares = totalDolares.add( NumberFormat.getInstance().parse(rd.plan) )
        } else {
          plan = rdPlan
          tituloPlan = TITLE_MN_TAG
        }
        if( plan.equalsIgnoreCase('')){
          plan = DEBIT_TAG
        }
        String monto = String.format( '%,.2f', rd.importe )
        def sub = [
            term: terminal,
            plan: plan,
            tknum: String.format( '%3s', String.format( '%d', rd.facturas) ),
            rctnum: String.format( '%5s', String.format( '%d', rd.vouchers) ),
            monto: String.format( '%10s', monto )
        ]
        subtotales.add( sub )
      }
      Boolean dolaresValidos = false
      if(totalDolares.compareTo(BigDecimal.ZERO) < 0 || totalDolares.compareTo(BigDecimal.ZERO) > 0 ){
        dolaresValidos = true
      }
      CurrencyFormatter formatter = new CurrencyFormatter()
      def datos = [ nombre_ticket: 'ticket-cierre-terminal',
          fechaCierre: CustomDateUtils.format( fechaCierre, 'dd-MM-yyyy' ),
          terminal: terminal,
          detalle: subtotales,
          titulo: tituloPlan,
          totalDolares: dolaresValidos ? formatterMoney.format( totalDolares ) : '',
          total: formatter.print( total, Locale.getDefault() ),
          thisSite: String.format( '%s [%d]', empleado.sucursal.nombre, empleado.sucursal.id ),
          empleado: empleado.nombreCompleto() ]
      imprimeTicket( 'template/ticket-cierre-terminal.vm', datos )
    }
    return terminalEmpty
  }


  private CierreTerminales findorCreate( List<CierreTerminales> lstTerminales, String idTerminales ) {
    CierreTerminales found = null
    for ( CierreTerminales res : lstTerminales ) {
      if ( res.idTerminal.equals( idTerminales ) ) {
        found = res
        break
      }
    }
    if ( found == null ) {
      found = new CierreTerminales( idTerminales )
      lstTerminales.add( found )
    }
    return found
  }

  @Override
  void imprimeResumenDiario( Date fechaCierre, Empleado empleado ) {

    NumberFormat formatter = new DecimalFormat( '#,##0.00' )
    Date fechaInicio = DateUtils.addDays( fechaCierre, -1 )
    Date fechaFin = DateUtils.addDays( fechaCierre, 1 )
    Date fechaStart = DateUtils.truncate( fechaCierre, Calendar.DAY_OF_MONTH )
    Date fechaEnd = new Date( DateUtils.ceiling( fechaCierre, Calendar.DAY_OF_MONTH ).getTime() - 1 )
    CierreDiario cierreDiario = cierreDiarioRepository.findOne( fechaCierre )

    if ( cierreDiario != null ) {
      QNotaVenta nv = QNotaVenta.notaVenta
      List<NotaVenta> notasVenta = notaVentaRepository.findAll(nv.fechaHoraFactura.between(fechaStart, fechaEnd).
          and(nv.factura.isNotEmpty()).and(nv.factura.isNotNull())) as List<NotaVenta>
      Parametro parametro = parametroRepository.findOne( TipoParametro.CONV_NOMINA.value )
      String[] valores = parametro?.valor?.split( ',' )
      notasVenta = notasVenta.findAll { notaVenta -> !valores.contains( notaVenta.idConvenio ) }

      QPago payment = QPago.pago
      List<Pago> pagos = pagoRepository.findAll(payment.fecha.between(fechaStart,fechaEnd).
          and(payment.notaVenta.factura.isNotEmpty()).and(payment.notaVenta.factura.isNotNull()))
      List<Pago> pagosDolares = new ArrayList<Pago>()
      for (Pago p : pagos) {
        if ( TAG_EFD.equalsIgnoreCase(p.idFormaPago) && !TAG_TRANSFER.equalsIgnoreCase(p.idFPago)) {
          pagosDolares.add( p )
        }
      }

      BigDecimal dolaresPesos = BigDecimal.ZERO
      pagosDolares.each { pago -> dolaresPesos = dolaresPesos + MoneyUtils.parseNumber( pago.idPlan ) }

      List<Deposito> depositos = depositoRepository.findBy_Fecha( fechaCierre )
      BigDecimal totalDepositosMN = BigDecimal.ZERO
      BigDecimal totalDepositosUS = BigDecimal.ZERO
      depositos.each { deposito ->
        if( TAG_DEPOSITO_MN.equalsIgnoreCase(deposito.tipoDeposito) ){
          totalDepositosMN = totalDepositosMN + deposito.monto
        } else if( TAG_DEPOSITO_US.equalsIgnoreCase(deposito.tipoDeposito) ){
          totalDepositosUS = totalDepositosUS + deposito.monto
        }
        deposito.empleado = new Empleado()
        deposito.empleado.nombre = String.format('%10s', formatter.format( deposito.monto ) )
      }
      BigDecimal efectivoNetoMN = cierreDiario.efectivoRecibido + cierreDiario.efectivoExternos - cierreDiario.efectivoDevoluciones
      BigDecimal efectivoNetoUS = dolaresPesos
      BigDecimal diferenciaEfectivoMN = totalDepositosMN - efectivoNetoMN
      BigDecimal diferenciaEfectivoUS = totalDepositosUS - efectivoNetoUS

      List<ResumenDiario> resumenesDiario = resumenDiarioRepository.findByFechaCierre( fechaCierre )

      List<ResumenDiario> resumenTerminales = new ArrayList<ResumenDiario>()
      String terminal

      if ( resumenesDiario.size() == 1 ) {
        ResumenDiario resumen = new ResumenDiario()
        resumen = resumenesDiario.first()
        BigDecimal montoDolares = BigDecimal.ZERO
        resumen.plan = '0'
        if( Registry.isCardPaymentInDollars(resumen.tipo) && resumen.plan.isNumber() ){
          montoDolares = montoDolares.add( NumberFormat.getInstance().parse( resumen.plan ) )
          if( montoDolares.compareTo(BigDecimal.ZERO) == 1 || montoDolares.compareTo(BigDecimal.ZERO) == -1 ){
            resumen.plan = formatter.format( montoDolares.doubleValue() )
          } else {
            resumen.plan = '0'
          }
        }
        resumen.formaPago = new FormaPago()
        resumen.formaPago.descripcion = String.format('%10s', formatter.format( resumen.importe ) )
        resumenTerminales.add( resumen )
      } else {
        Collections.sort( resumenesDiario )
        ResumenDiario current = null
        BigDecimal montoDolares
        for ( ResumenDiario resumen : resumenesDiario ) {
          if ( ( current == null ) || ( !current.idTerminal.equalsIgnoreCase( resumen.idTerminal ) ) ) {
            current = new ResumenDiario()
            current.idTerminal = resumen.idTerminal.toUpperCase()
            current.importe = BigDecimal.ZERO
            resumenTerminales.add( current )
            montoDolares = BigDecimal.ZERO
          }
          if ( resumen.plan?.equals( TAG_CANCELADO ) || resumen.plan?.equals( TAG_DEVUELTO ) ) {
            current.importe = current.importe.subtract( resumen.importe )
            current.plan = '0'
            if( Registry.isCardPaymentInDollars(resumen.tipo) && resumen.plan.isNumber() ){
              montoDolares = montoDolares.subtract( NumberFormat.getInstance().parse( resumen.plan ) )
              if( montoDolares.compareTo(BigDecimal.ZERO) == 1 || montoDolares.compareTo(BigDecimal.ZERO) == -1 ){
                current.plan = formatter.format( montoDolares.doubleValue() )
              } else {
                current.plan = '0'
              }
            }
          } else {
              current.importe = current.importe.add( resumen.importe )
              current.plan = '0'
            if( Registry.isCardPaymentInDollars(resumen.tipo) && resumen.plan.isNumber() ){
                montoDolares = montoDolares.add( NumberFormat.getInstance().parse( resumen.plan ) )
                if( montoDolares.compareTo(BigDecimal.ZERO) == 1 || montoDolares.compareTo(BigDecimal.ZERO) == -1 ){
                  current.plan = formatter.format( montoDolares.doubleValue() )
                } else {
                  current.plan = '0'
                }
              }
          }
          current.formaPago = new FormaPago()
          current.formaPago.descripcion = String.format('%10s', formatter.format( current.importe ) )
        }
      }

      List<Pago> vales = pagoRepository.findBy_Fecha( fechaCierre )
      for( Pago pago : vales ){
        pago.referenciaPago = formatter.format( pago.monto )
      }
      vales = vales.findAll {
        NotaVenta tmp = notasVenta.find { notaVenta ->
          it.idFactura = notaVenta.id
        }
        tmp != null && it.idSync != '2' && it.idFormaPago == 'VA'
      }
      BigDecimal montoVales = BigDecimal.ZERO
      vales.each { vale -> montoVales = montoVales + vale.monto }

      QNotaVenta notaVenta = QNotaVenta.notaVenta
      List<NotaVenta> comprobantes = notaVentaRepository.findAll( notaVenta.fechaHoraFactura.between(fechaCierre,fechaFin).and(notaVenta.factura.isNotEmpty()).
          and(notaVenta.factura.isNotNull()) )

      List<PagoExterno> pagosExternos = pagoExternoRepository.findByFechaGreaterThanAndFechaLessThanAndFormaPago_aceptaEnPagos( fechaCierre, fechaFin, true )
      for( PagoExterno pagoExterno : pagosExternos ){
        pagoExterno.referencia = formatter.format( pagoExterno.monto )
      }
      BigDecimal totalPagosExternos = BigDecimal.ZERO
      pagosExternos.each { pagoExterno -> totalPagosExternos = totalPagosExternos + pagoExterno.monto }

      Parametro parametroGerente = parametroRepository.findOne( TipoParametro.ID_GERENTE.value )
      Empleado gerente = empleadoRepository.findById( parametroGerente.valor )

      List<EntregadoExterno> entregadosExternos = entregadoExternoRepository.findByFechaGreaterThanAndFechaLessThan( fechaInicio, fechaFin )
      log.debug( "Entregados externos ${ entregadosExternos.size() }" )
      Map<EntregadoExterno> entregadosExternosTmp = new HashMap<EntregadoExterno>()
      entregadosExternos.each { entregadoExterno ->
        if ( entregadosExternosTmp.containsKey( entregadoExterno.idFactura ) ) {
          EntregadoExterno tmp = entregadosExternosTmp.get( entregadoExterno.idFactura ) as EntregadoExterno
          tmp.idFactura = formatter.format( tmp.pago + entregadoExterno.pago )
          entregadosExternosTmp.put( entregadoExterno.idFactura, tmp )
        } else {
          entregadosExternosTmp.put( entregadoExterno.idFactura, entregadoExterno )
        }
      }
      Parametro parametroIva = parametroRepository.findOne( TipoParametro.IVA_VIGENTE.value )
      Integer ivaPorcentaje = NumberFormat.getInstance().parse( parametroIva.valor ).intValue()
      Double ivaVigente = 1+(ivaPorcentaje/100)

      Double ventaBruta = cierreDiario.ventaBruta.doubleValue()/ivaVigente
      Double cancelaciones = cierreDiario.cancelaciones.doubleValue()/ivaVigente
      Double ventaNeta = cierreDiario.ventaNeta
      Double ventaNetaSinIva = cierreDiario.ventaNeta.doubleValue()/ivaVigente
      Double montoTotalIva = cierreDiario.ventaNeta.subtract( ventaNetaSinIva )

      List<Articulo> lstArticulos = articuloRepository.findByCantExistenciaLessThan( 0 )
      for( Articulo articulo : lstArticulos ) {
        articulo.descripcion = String.format('%10s', formatter.format( articulo.precio ) )
      }


      BigDecimal descuento = cierreDiario.modificaciones.doubleValue()/ivaVigente

      MonedaDetalle tipoCambioUsd = monedaExtranjeraService.findActiveRate( 'USD' )
      MonedaDetalle tipoCambioEur = monedaExtranjeraService.findActiveRate( 'EUR' )
      Integer cantidadVentaNeta = cierreDiario.cantidadVentas - cierreDiario.cantidadCancelaciones

      BigDecimal desc = BigDecimal.ZERO
      Integer cantDesc = 0
      QNotaVenta notaV = QNotaVenta.notaVenta
      List<NotaVenta> lstNotasActivas = notaVentaRepository.findAll(notaV.fechaHoraFactura.between(fechaStart,fechaEnd).
          and(notaV.factura.isNotEmpty()).and(notaV.factura.isNotNull()).and(notaV.sFactura.ne('T')))

      for( NotaVenta notas : lstNotasActivas ){
        List<OrdenPromDet> lstOrdenPromDet = ordenPromDetRepository.findByIdFactura( notas.id )
        for( OrdenPromDet promo : lstOrdenPromDet ){
          desc = desc.add( promo.descuentoMonto )
          cantDesc = cantDesc+1
        }
        if( notas.montoDescuento.abs().compareTo(CERO_BIGDECIMAL) > 0 ){
          desc = desc.add( notas.montoDescuento )
          cantDesc = cantDesc+1
        }
      }
      desc = desc.div(ivaVigente)

      def retornos = []
      QTransInv trans = QTransInv.transInv
      List<TransInv> lstRetornos = transInvRepository.findAll(trans.fecha.between(fechaStart,fechaEnd).and(trans.idTipoTrans.eq('RETORNO')))
      for(TransInv transaccion : lstRetornos){
        QTransInvDetalle transDet = QTransInvDetalle.transInvDetalle
        List<TransInvDetalle> lstTransDet = transInvDetalleRepository.findAll( transDet.idTipoTrans.eq(transaccion.idTipoTrans).
            and(transDet.folio.eq(transaccion.folio) ))
        String [] referencia = transaccion.referencia.split(/\|/)
        def transTmp = [
            folio: transaccion.folio,
            importe: referencia[1].trim(),
            ticket: referencia[0],
            detalles: lstTransDet
        ]
        retornos.add( transTmp );
      }

      QPago pay = QPago.pago
      def notasCredito = []
      List<Pago> lstNotasCredito = pagoRepository.findAll( pay.fecha.between(fechaStart,fechaEnd).and(pay.idFPago.eq('NOT')).
          and(pay.notaVenta.factura.isNotEmpty()).and(pay.notaVenta.factura.isNotNull()))
      for(Pago pago: lstNotasCredito){
        String monto = formatter.format( pago.monto )
        def notaCreditoTmp = [
            factura: pago.notaVenta.factura,
            clave: pago.clave,
            monto: monto
        ]
        notasCredito.add( notaCreditoTmp )
      }

      def ventasEmpleado = []
      List<Pago> lstVentasEmpleado = pagoRepository.findAll( pay.fecha.between(fechaStart,fechaEnd).and(pay.idFPago.eq('CRE')).
          and(pay.notaVenta.factura.isNotEmpty()).and(pay.notaVenta.factura.isNotNull())) as List<Pago>
      for(Pago pago: lstVentasEmpleado){
        String monto = formatter.format( pago.monto )
        def ventaEmpleadoTmp = [
            factura: pago.notaVenta.factura,
            idBancoEmisor: pago.idBancoEmisor,
            monto: monto
        ]
        ventasEmpleado.add( ventaEmpleadoTmp )
      }


      BigDecimal faltanteEmp = BigDecimal.ZERO
      BigDecimal faltanteMv = BigDecimal.ZERO
      List<Pago> lstPagosFaltante = pagoRepository.findAll( pay.fecha.between(fechaStart,fechaEnd).and(pay.idFPago.eq('FE').or(pay.idFPago.eq('FM'))).
                and(pay.notaVenta.factura.isNotEmpty()).and(pay.notaVenta.factura.isNotNull())) as List<Pago>
      for(Pago pagoFaltante : lstPagosFaltante){
        if( StringUtils.trimToEmpty(pagoFaltante.idFPago).equalsIgnoreCase("FE") ){
          faltanteEmp = faltanteEmp.add(pagoFaltante.monto)
        } else if( StringUtils.trimToEmpty(pagoFaltante.idFPago).equalsIgnoreCase("FM") ){
            faltanteMv = faltanteMv.add(pagoFaltante.monto)
        }
      }


      QExamen ex = QExamen.examen
      List<Examen> lstExamenes = examenRepository.findAll( ex.idAtendio.eq('9999').and(ex.observacionesEx.eq('SE')).
              and(ex.fechaAlta.between(fechaStart,fechaEnd)) )

      def datos = [ nombre_ticket: 'ticket-resumen-diario',
          fecha_cierre: MyDateUtils.format( fechaCierre, 'dd-MM-yyyy' ),
          hora_cierre: cierreDiario.horaCierre != null ? String.format('%s %s', MyDateUtils.format( cierreDiario.fechaCierre, 'dd-MM-yyyy' ), MyDateUtils.format( cierreDiario.horaCierre, 'HH:mm:ss' ) ): '',
          empleado: empleado.nombreCompleto(),
          id_sucursal: empleado.sucursal.id,
          nombre_sucursal: empleado.sucursal.nombre,
          estado_cierre_diario: cierreDiario.estado,
          cantidad_ventas_brutas: cierreDiario.cantidadVentas == 0 ? '-' : cierreDiario.cantidadVentas,
          importe_ventas_brutas: cierreDiario.ventaBruta.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.ventaBruta ) ),
          cantidad_modificaciones: cierreDiario.cantidadModificaciones == 0 ? '-' : cierreDiario.cantidadModificaciones,
          importe_modificaciones: cierreDiario.modificaciones.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.modificaciones ) ),
          cantidad_modificaciones_netas: cantDesc == 0 ? '-' : cantDesc,
          importe_modificaciones_netas: desc.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( desc ) ),
          cantidad_cancelaciones: cierreDiario.cantidadCancelaciones == 0 ? '-' : cierreDiario.cantidadCancelaciones,
          cantidad_venta_neta: cantidadVentaNeta == 0 ? '-' : cantidadVentaNeta,
          importe_cancelaciones: cierreDiario.cancelaciones.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.cancelaciones ) ),
          importe_venta_neta: cierreDiario.ventaNeta.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.ventaNeta ) ),
          importe_ingresos_brutos: cierreDiario.ingresoBruto.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.ingresoBruto) ),
          importe_devoluciones: cierreDiario.devoluciones.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.devoluciones ) ),
          importe_ingresos_netos: cierreDiario.ingresoNeto.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.ingresoNeto ) ),
          importe_efectivo_recibido: cierreDiario.efectivoRecibido.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.efectivoRecibido ) ),
          importe_efectivo_externos: cierreDiario.efectivoExternos.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.efectivoExternos ) ),
          importe_efectivo_devoluciones: cierreDiario.efectivoDevoluciones.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.efectivoDevoluciones ) ),
          importe_efectivo_neto: cierreDiario.efectivoNeto.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.efectivoNeto ) ),
          importe_dolares_recibido: dolaresPesos.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( dolaresPesos.multiply( new BigDecimal( 1.00 ) ) ) ),
          importe_dolares_devoluciones: cierreDiario.dolaresDevoluciones.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : formatter.format( cierreDiario.dolaresDevoluciones ),
          importe_dolares_pesos: cierreDiario.dolaresRecibidos.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( cierreDiario.dolaresRecibidos ) ),
          //iva_vigente: montoTotalIva.compareTo(BigDecimal.ZERO) == 0 ? String.format('%10s', '-') : String.format('%10s', formatter.format( montoTotalIva ) ),
          artSinExis: null,
          today: MyDateUtils.format( new Date(), 'dd-MM-yyyy' ),
          tipo_cambio_USD: String.format( '%.2f', tipoCambioUsd?.tipoCambio != null ? tipoCambioUsd?.tipoCambio : BigDecimal.ZERO ),
          tipo_cambio_EUR: String.format( '%.2f', tipoCambioEur?.tipoCambio != null ? tipoCambioEur?.tipoCambio : BigDecimal.ZERO ),
          depositos: depositos.size() > 0 ? depositos : null,
          faltanteMN: diferenciaEfectivoMN < 0 ? String.format('%10s', formatter.format( diferenciaEfectivoMN ) ) : null,
          sobranteMN: diferenciaEfectivoMN > 0 ? String.format('%10s', formatter.format( diferenciaEfectivoMN ) ) : null,
          faltanteUS: diferenciaEfectivoUS < 0 ? String.format('%10s', formatter.format( diferenciaEfectivoUS ) ) : null,
          sobranteUS: diferenciaEfectivoUS > 0 ? String.format('%10s', formatter.format( diferenciaEfectivoUS ) ) : null,
          resumen_terminales: resumenTerminales.size() > 0 ? resumenTerminales : null,
          numero_vales: vales.size(),
          monto_vales: montoVales.compareTo(BigDecimal.ZERO) == 0 ? '-' : String.format('%10s', formatter.format( montoVales ) ),
          vales: vales.size() > 0 ? vales : null,
          totalComprobantes: comprobantes.size(),
          comprobantesInicial: cierreDiario.facturaInicial != null ? cierreDiario.facturaInicial : '',
          comprobantesFinal: cierreDiario.facturaFinal != null ? cierreDiario.facturaFinal : '',
          pagosExternos: pagosExternos.isEmpty() ? null : pagosExternos,
          totalPagosExternos: totalPagosExternos.compareTo(BigDecimal.ZERO) == 0 ? '-' : String.format('%10s', formatter.format( totalPagosExternos ) ),
          gerente: gerente?.nombreCompleto(),
          retornos: retornos.size() > 0 ? retornos : null,
          notas_credito: notasCredito.size() > 0 ? notasCredito : null,
          ventas_empleado: ventasEmpleado.size() > 0 ? ventasEmpleado : null,
          observaciones: StringUtils.isNotBlank( cierreDiario.observaciones ) ? StringUtils.replace( cierreDiario.observaciones, '~', '\n' ) : '',
          entregadosExternos: entregadosExternosTmp.isEmpty() ? null : new ArrayList<EntregadoExterno>( entregadosExternosTmp.values() ),
          total_clientes_sinExamen: lstExamenes.size(),
          clientes_sinExamen: lstExamenes,
          montoFaltanteEmp: faltanteEmp.compareTo(BigDecimal.ZERO) > 0 ? formatter.format(faltanteEmp): "-",
          montoFaltanteMv: faltanteMv.compareTo(BigDecimal.ZERO) > 0 ? formatter.format(faltanteMv): "-"]

      imprimeTicket( 'template/ticket-resumen-diario.vm', datos )
    } else {
      log.error( "Se ha producido un error al imprimir el Resumen Diario. No hay datos sobre el día ${ MyDateUtils.format( fechaCierre, 'dd/MM/yyyy' ) }" )
      imprimeTicket( 'template/error.vm', [ nombre_ticket: 'ticket-cierre-terminal', mensaje: "Se ha producido un error al imprimir el Resumen Diario. No hay datos sobre el día ${ MyDateUtils.format( fechaCierre, 'dd/MM/yyyy' ) }" ] )
    }
  }

  @Override
  void imprimeUbicacionListaPrecios( ListaPrecios listaPrecios, List<Articulo> articulos ) {
    def idSucursal = parametroRepository.findOne( TipoParametro.ID_SUCURSAL.value )?.valor
    def sucursal = sucursalRepository.findOne( idSucursal?.toInteger() )
    for(Articulo articulo : articulos){
      Articulo tmp = articuloRepository.findOne( articulo.id )
      if( tmp != null ){
        articulo.cantExistencia = tmp.cantExistencia
      }
    }
    def lstArticulos = [ ]
    String descripcion1 = ''
    String descripcion2 = ''
    String descripcion3 = ''
    String descripcion4 = ''
    String descripcion5 = ''
    for(Articulo art : articulos){
      if( art.descripcion.length() > 22 ){
        descripcion1 = art.descripcion.substring(0,22)
        if( art.descripcion.length() > 44 ){
          descripcion2 = art.descripcion.substring(22,44)
        }
        if( art.descripcion.length() > 66 ){
          descripcion3 = art.descripcion.substring(44,66)
        }
        if( art.descripcion.length() > 88 && art.descripcion.length() > 100 ){
          descripcion4 = art.descripcion.substring(88,100)
        } else if( art.descripcion.length() > 88 ) {
          descripcion4 = art.descripcion.substring(88)
        }
        if( art.descripcion.length() > 100 && art.descripcion.length() > 122 ){
          descripcion5 = art.descripcion.substring(100,122)
        } else if( art.descripcion.length() > 100 ) {
          descripcion5 = art.descripcion.substring(100)
        }
      } else {
        descripcion1 = art.descripcion
      }
      def tmpArticulo = [
        id: art.id,
        articulo: art.articulo,
        color: art.codigoColor != null && StringUtils.trimToEmpty(art.codigoColor) != '' ? art.codigoColor : art.idCb != null ? art.idCb : '',
        descripcion1: descripcion1,
        descripcion2: descripcion2,
        descripcion3: descripcion3,
        descripcion4: descripcion4,
        descripcion5: descripcion5,
        cantidad: art.cantExistencia
      ]
      if( art.cantExistencia > 0 ){
          lstArticulos.add( tmpArticulo )
      }
    }
    def items = [
        nombre_ticket: 'ticket-ubicacion-lista-precios',
        sucursal: sucursal,
        id_lista: listaPrecios?.id,
        fecha: new Date().format( 'dd-MM-yyyy' ),
        articulos: articulos,
        lstArticulos: lstArticulos
    ]
    //if ( Registry.isSunglass() ) {
      imprimeTicket( 'template/ticket-ubicacion-lista-precios-si.vm', items )
    /*} else {
      imprimeTicket( 'template/ticket-ubicacion-lista-precios.vm', items )
    }*/
  }

  @Override
  void imprimeCargaListaPrecios( ListaPrecios listaPrecios ) {
    def idSucursal = parametroRepository.findOne( TipoParametro.ID_SUCURSAL.value )?.valor
    def sucursal = sucursalRepository.findOne( idSucursal?.toInteger() )
    def items = [
        nombre_ticket: 'ticket-carga-lista-precios',
        sucursal: sucursal,
        id_lista: listaPrecios?.id,
        tipo_carga: listaPrecios?.tipoCarga,
        fecha: new Date().format( 'dd-MM-yyyy' )
    ]
    imprimeTicket( 'template/ticket-carga-lista-precios.vm', items )
  }

  void imprimeTransInv( TransInv pTrans ) {
    this.imprimeTransInv( pTrans, true )
  }

    protected static String replaceCharAt(String s, int pos, char c) {
        StringBuffer buf = new StringBuffer( s );
        buf.setCharAt( pos, c );
        return buf.toString( );
    }

    protected  String claveAleatoria(Integer sucursal, Integer folio) {
        String folioAux = "" + folio.intValue();
        String sucursalAux = "" + sucursal.intValue()
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        if (folioAux.size() < 4) {
            folioAux = folioAux?.padLeft( 4, '0' )
        }
        else {
            folioAux = folioAux.substring(0,4);
        }
        String resultado = sucursalAux?.padLeft( 3, '0' ) + folioAux


        for (int i = 0; i < resultado.size(); i++) {
            int numAleatorio = (int) (Math.random() * abc.size());
            if (resultado.charAt(i) == '0') {
                resultado = replaceCharAt(resultado, i, abc.charAt(numAleatorio))
            }
            else {
                int numero = Integer.parseInt ("" + resultado.charAt(i));
                numero = 10 - numero
                char diff = Character.forDigit(numero, 10);
                resultado = replaceCharAt(resultado, i, diff)
            }


        }
        return resultado;
    }

  void imprimeTransInv( TransInv pTrans, Boolean pNewTransaction ) {
    TransInvAdapter adapter = TransInvAdapter.instance
    def parts = [ ]
    Integer cantidad = 0
    String referencia = ''
    for ( TransInvDetalle trDet in pTrans.trDet ) {
      Articulo part = ServiceFactory.partMaster.obtenerArticulo( trDet.sku, true)
      def tkPart = [
          sku: adapter.getText( trDet, adapter.FLD_TRD_SKU ),
          partNbr: adapter.getText( part, adapter.FLD_PART_CODE ),
          color: adapter.getText( part, adapter.FLD_PART_COLOR_CODE ),
          partColor: adapter.getText( part, adapter.FLD_PART_CODE_PLUS_COLOR ) ,
          desc: adapter.getText( part, adapter.FLD_PART_DESC ),
          price: String.format( '%12s', adapter.getText( part, adapter.FLD_PART_PRICE ) ),
          qty: String.format( '%5s', adapter.getText( trDet, adapter.FLD_TRD_QTY ) )
      ]
        cantidad = cantidad+trDet.cantidad
      parts.add( tkPart )
    }
    AddressAdapter companyAddress = Registry.companyAddress
    Sucursal site = sucursalRepository.findOne( pTrans.sucursal )
    Sucursal siteTo = null
    if ( pTrans.sucursalDestino != null ) {
      siteTo = sucursalRepository.findOne( pTrans.sucursalDestino )
    }
    Empleado emp = empleadoRepository.findOne( pTrans.idEmpleado )
    List<String> remarks = adapter.split( StringUtils.trimToEmpty( pTrans.observaciones ), 36 )
    Parametro parametroGerente = parametroRepository.findOne( TipoParametro.ID_GERENTE.value )
    Empleado mgr = empleadoRepository.findById( parametroGerente.valor )
    /*if ( site.idGerente != null ) {
      mgr = empleadoRepository.findById( site.idGerente )
    }*/
    if ( InventorySearch.esTipoTransaccionSalida( pTrans.idTipoTrans ) ) {
      def tkInvTr = [
          nombre_ticket: "ticket-salida-inventario",
          effDate: adapter.getText( pTrans, adapter.FLD_TR_EFF_DATE ),
          thisSite: adapter.getText( site ),
          user: adapter.getText( emp ),
          mgr: adapter.getText( mgr ),
          trNbr: adapter.getText( pTrans, adapter.FLD_TR_NBR ),
          siteTo: adapter.getText( siteTo ),
          remarks_1: ( remarks.size() > 0 ? remarks.get( 0 ) : "" ),
          remarks_2: ( remarks.size() > 1 ? remarks.get( 1 ) : "" ),
          quantity: cantidad,
          parts: parts
      ]
      imprimeTicket( "template/ticket-salida-inventario.vm", tkInvTr )
    } else if ( InventorySearch.esTipoTransaccionAjuste( pTrans.idTipoTrans ) ) {
      def tkInvTr = [
          nombre_ticket: "ticket-ajuste-inventario",
          effDate: adapter.getText( pTrans, adapter.FLD_TR_EFF_DATE ),
          thisSite: adapter.getText( site ),
          user: adapter.getText( emp ),
          mgr: adapter.getText( mgr ),
          trNbr: adapter.getText( pTrans, adapter.FLD_TR_NBR ),
          siteTo: adapter.getText( siteTo ),
          remarks_1: ( remarks.size() > 0 ? remarks.get( 0 ) : "" ),
          remarks_2: ( remarks.size() > 1 ? remarks.get( 1 ) : "" ),
          parts: parts
      ]
      imprimeTicket( "template/ticket-ajuste-inventario.vm", tkInvTr )
    } else if ( InventorySearch.esTipoTransaccionDevolucion( pTrans.idTipoTrans ) ) {
      def tkInvTr = [
          nombre_ticket: "ticket-devolucion",
          effDate: adapter.getText( pTrans, adapter.FLD_TR_EFF_DATE ),
          compania: companyAddress,
          sucursal: site,
          user: adapter.getText( emp ),
          mgr: adapter.getText( mgr ),
          ticket: adapter.getText( pTrans, adapter.FLD_SRC_TICKET ),
          empName: adapter.getText( pTrans, adapter.FLD_SALES_PERSON ),
          returnAmount: adapter.getText( pTrans, adapter.FLD_RETURN_AMOUNT ),
          trNbr: adapter.getText( pTrans, adapter.FLD_TR_NBR ),
          remarks_1: ( remarks.size() > 0 ? remarks.get( 0 ) : "" ),
          remarks_2: ( remarks.size() > 1 ? remarks.get( 1 ) : "" ),
          quantity: cantidad,
          parts: parts
      ]
      imprimeTicket( "template/ticket-devolucion.vm", tkInvTr )
      if ( Registry.isReceiptDuplicate() && pNewTransaction ) {
        imprimeTicket( 'template/ticket-devolucion.vm', tkInvTr )
      }

    } else if ( InventorySearch.esTipoTransaccionSalidaSucursal( pTrans.idTipoTrans ) ) {
        def tkInvTr = [
                nombre_ticket: "ticket-salida-sucursal",
                effDate: adapter.getText( pTrans, adapter.FLD_TR_EFF_DATE ),
                thisSite: adapter.getText( site ),
                user: adapter.getText( emp ),
                codaleatorio: pTrans.referencia,
                mgr: adapter.getText( mgr ),
                trNbr: adapter.getText( pTrans, adapter.FLD_TR_NBR ),
                siteTo: adapter.getText( siteTo ),
                remarks_1: ( remarks.size() > 0 ? remarks.get( 0 ) : "" ),
                remarks_2: ( remarks.size() > 1 ? remarks.get( 1 ) : "" ),
                quantity: cantidad,
                parts: parts
        ]
        imprimeTicket( "template/ticket-salida-sucursal.vm", tkInvTr )
    } else if ( InventorySearch.esTipoTransaccionEntrada( pTrans.idTipoTrans ) ) {
        def tkInvTr = [
                nombre_ticket: "ticket-entrada-sucursal",
                effDate: adapter.getText( pTrans, adapter.FLD_TR_EFF_DATE ),
                thisSite: adapter.getText( site ),
                user: adapter.getText( emp ),
                codaleatorio: pTrans.referencia,
                mgr: adapter.getText( mgr ),
                trNbr: adapter.getText( pTrans, adapter.FLD_TR_NBR ),
                siteTo: adapter.getText( siteTo ),
                remarks_1: ( remarks.size() > 0 ? remarks.get( 0 ) : "" ),
                remarks_2: ( remarks.size() > 1 ? remarks.get( 1 ) : "" ),
                quantity: cantidad,
                parts: parts
        ]
        imprimeTicket( "template/ticket-entrada-inventario.vm", tkInvTr )
    } else if ( InventorySearch.esTipoTransaccionOtraSalida( pTrans.idTipoTrans ) ) {
          def tkInvTr = [
                  nombre_ticket: "ticket-salida-inventario",
                  effDate: adapter.getText( pTrans, adapter.FLD_TR_EFF_DATE ),
                  thisSite: adapter.getText( site ),
                  user: adapter.getText( emp ),
                  mgr: adapter.getText( mgr ),
                  trNbr: adapter.getText( pTrans, adapter.FLD_TR_NBR ),
                  siteTo: adapter.getText( siteTo ),
                  remarks_1: ( remarks.size() > 0 ? remarks.get( 0 ) : "" ),
                  remarks_2: ( remarks.size() > 1 ? remarks.get( 1 ) : "" ),
                  quantity: cantidad,
                  parts: parts
          ]
          imprimeTicket( "template/ticket-salida-inventario.vm", tkInvTr )
      }
  }

  @Override
  void imprimeCancelacion( String idNotaVenta ) {
    log.info( "imprimiendo ticket cancelacion de notaVenta id: ${idNotaVenta}" )
    NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( idNotaVenta )
    List<Modificacion> mods = modificacionRepository.findByIdFacturaAndTipo( idNotaVenta ?: '', 'can' )
    log.debug( "modificaciones: ${mods*.id}" )
    Modificacion modificacion = mods?.any() ? mods.first() : null
    log.debug( "obtiene modificacion: ${modificacion?.id}" )
    if ( StringUtils.isNotBlank( notaVenta?.id ) && modificacion?.id ) {
      List<Pago> pagosLst = pagoRepository.findByIdFacturaOrderByFechaAsc( idNotaVenta )
      BigDecimal totalPorDevolver = 0
      pagosLst.each { Pago pmt ->
        totalPorDevolver += pmt.porDevolver ?: 0
      }
      if ( totalPorDevolver == 0 ) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance( Locale.US )
        Integer sucursalId = sucursalRepository.getCurrentSucursalId()
        Sucursal sucursal = sucursalRepository.findOne( sucursalId )
        Empleado empleado = empleadoRepository.findOne( modificacion.idEmpleado )
        def detalles = [ ]
        List<DetalleNotaVenta> detallesLst = detalleNotaVentaRepository.findByIdFacturaOrderByIdArticuloAsc( idNotaVenta )
        detallesLst.each { DetalleNotaVenta det ->
          BigDecimal precio = det?.precioUnitFinal?.multiply( det?.cantidadFac )
          def item = [
              cantidad: "${det?.cantidadFac?.toInteger() ?: 0}",
              codigo: "${det?.articulo?.articulo ?: ''} ${det?.articulo?.codigoColor ?: ''}",
              surte: "${det?.surte ?: ''}",
              precio: formatter.format( precio ?: 0 )
          ]
          log.debug( "genera detalle: ${item}" )
          detalles.add( item )
        }
        def pagos = [ ]
        BigDecimal totalPagos = 0
        pagosLst.each { Pago pmt ->
          BigDecimal monto = pmt?.monto ?: 0
          String referenciaPago
          NotaVenta nota = notaVentaRepository.findOne(pmt?.referenciaPago)
          if(nota != null){
            referenciaPago = nota.factura
          } else {
            referenciaPago = pmt?.referenciaPago
          }
          totalPagos += monto
          def item = [
              descripcion: "${pmt?.eTipoPago?.descripcion ?: ''} ${referenciaPago ?: ''}",
              monto: formatter.format( monto )
          ]
          log.debug( "genera pago: ${item}" )
          pagos.add( item )
        }
        def transferencias = [ ]
        def devoluciones = [ ]
        BigDecimal totalTransferencias = 0
        BigDecimal totalDevoluciones = 0
        List<Devolucion> devolucionesLst = devolucionRepository.findByIdModOrderByFechaAsc( modificacion.id )
        String devEfectivo = ""
        String devEfectivo1 = ""
        String nombre = ""
        String cuenta = ""
        String clabe = ""
        String correo = ""
        BigDecimal devEfec = BigDecimal.ZERO
        devolucionesLst.each { Devolucion dev ->
          BigDecimal monto = dev?.monto ?: 0
          if ( 'd'.equalsIgnoreCase( dev?.tipo ) ) {
            totalDevoluciones += monto
            def item = [
                original: "${dev?.pago?.eTipoPago?.descripcion ?: ''}",
                devolucion: "${dev?.formaPago?.descripcion ?: ''}",
                importe: formatter.format( monto )
            ]
            if( StringUtils.trimToEmpty(dev?.formaPago?.id).equalsIgnoreCase("TB") ){
              devEfectivo = "EL PLAZO DE LA DEVOLUCION"
              devEfectivo1 = "ES DE 1 DIA HABIL."
            } else if( StringUtils.trimToEmpty(dev?.formaPago?.id).equalsIgnoreCase("CH") ){
              devEfectivo = "EL PLAZO DE LA DEVOLUCION"
              devEfectivo1 = "ES DE 3 A 4 DIAS HABILES."
            }
            String[] data = StringUtils.trimToEmpty(dev.devEfectivo).split(",")
            if( data.length >= 5 ){
              nombre = data[0]
              cuenta = data[2]
              clabe = "/ "+data[3]
              correo = data[4]
              devEfec = devEfec.add( dev.monto )
            }
            log.debug( "genera devolucion: ${item}" )
            devoluciones.add( item )
          } else {
            totalTransferencias += monto
            String referenciaPago
            NotaVenta nv = notaVentaRepository.findOne( dev?.transf )
            if(nv != null){
              referenciaPago = nv.factura
            } else {
              referenciaPago = dev?.transf
            }
            def item = [
                descripcion: "Factura ${referenciaPago} (${dev?.formaPago?.descripcion ?: ''})",
                monto: formatter.format( monto )
            ]
            log.debug( "genera transferencia: ${item}" )
            transferencias.add( item )
          }
        }
        Parametro parametroGerente = parametroRepository.findOne( TipoParametro.ID_GERENTE.value )
        Empleado gerente = empleadoRepository.findById( parametroGerente.valor )
        Map<String, Object> items = [
            nombre_ticket: 'ticket-cancelacion',
            sucursal: sucursal,
            fecha: modificacion.fecha != null ? modificacion.fecha.format( DATE_FORMAT ) : new Date().format( DATE_FORMAT ),
            fecha_venta: notaVenta.fechaHoraFactura?.format( DATE_TIME_FORMAT ) ?: '',
            empleado: empleado,
            nota_venta: notaVenta,
            cliente: notaVenta.cliente?.nombreCompleto( false ),
            gerente: gerente?.nombreCompleto(),
            modificacion: modificacion,
            detalles: detalles,
            venta_neta: formatter.format( notaVenta.ventaNeta ),
            total_saldo: formatter.format( notaVenta.ventaNeta.subtract( notaVenta.sumaPagos ) ),
            pagos: pagos,
            total_pagos: formatter.format( totalPagos ),
            devoluciones: devoluciones,
            total_devoluciones: formatter.format( totalDevoluciones ),
            transferencias: transferencias,
            total_transferencias: formatter.format( totalTransferencias ),
            total_movimientos: formatter.format( totalDevoluciones.add( totalTransferencias ) ),
            message: devEfectivo,
            message1: devEfectivo1,
            nombre: nombre,
            cuenta: cuenta,
            clabe: clabe,
            correo: correo,
            devEfec: formatter.format(devEfec)
        ]
        imprimeTicket( 'template/ticket-cancelacion.vm', items )
      } else {
        log.warn( 'no se imprime ticket cancelacion, aun tiene monto por devolver' )
      }
    } else {
      log.warn( 'no se imprime ticket cancelacion, parametros invalidos' )
    }
  }

  @Override
  void imprimePlanCancelacion( String idNotaVenta ) {
    log.info( "imprimiendo ticket plan cancelacion de notaVenta id: ${idNotaVenta}" )
    NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( idNotaVenta )
    List<Modificacion> mods = modificacionRepository.findByIdFacturaAndTipo( idNotaVenta ?: '', 'can' )
    log.debug( "modificaciones: ${mods*.id}" )
    Modificacion modificacion = mods?.any() ? mods.first() : null
    log.debug( "obtiene modificacion: ${modificacion?.id}" )
    if ( StringUtils.isNotBlank( notaVenta?.id ) && modificacion?.id ) {
      NumberFormat formatter = NumberFormat.getCurrencyInstance( Locale.US )
      Integer sucursalId = sucursalRepository.getCurrentSucursalId()
      Sucursal sucursal = sucursalRepository.findOne( sucursalId )
      List<DetalleNotaVenta> detallesLst = detalleNotaVentaRepository.findByIdFacturaOrderByIdArticuloAsc( idNotaVenta )
      List<Pago> pagosLst = pagoRepository.findByIdFacturaOrderByFechaAsc( idNotaVenta )
      def pagos = [ ]
      pagosLst.each { Pago pmt ->
        BigDecimal monto = pmt?.monto ?: 0
        String referenciaPago
        NotaVenta nota = notaVentaRepository.findOne( pmt?.referenciaPago )
        if(nota != null){
          referenciaPago = nota?.factura
        } else {
          referenciaPago = pmt?.referenciaPago
        }
        def item = [
            descripcion: "${pmt?.eTipoPago?.descripcion} ${referenciaPago}",
            monto: formatter.format( monto )
        ]
        log.debug( "genera pago: ${item}" )
        pagos.add( item )
      }
      Map<String, Object> items = [
          nombre_ticket: 'ticket-plan-cancelacion',
          ticket_id: "${sucursal.centroCostos}-${notaVenta.factura}",
          nota_venta: notaVenta,
          poliza: "",
          poliza_vigente: "",
          factura: "",
          detalles: detallesLst*.articulo*.articulo,
          pagos: pagos
      ]
      imprimeTicket( 'template/ticket-plan-cancelacion.vm', items )
    } else {
      log.warn( 'no se imprime ticket cancelacion, parametros invalidos' )
    }
  }

  void imprimeResumenExistencias( InvOhSummary pSummary ) {
    log.debug( String.format( "Imprime Resume de Existencias\n%s", pSummary.toString() ) )
    DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm" )
    Sucursal site = ServiceFactory.sites.obtenSucursalActual()
      Collections.sort( pSummary.lines, new Comparator<InvOhDet>() {
          @Override
          int compare(InvOhDet o1, InvOhDet o2) {
              return o1.id.compareTo(o2.id)
          }
      })
    def tkQtyOH = [
        nombre_ticket: "ticket-resumen-inventario",
        effDate: df.format( new Date() ),
        thisSite: TransInvAdapter.instance.getText( site ),
        genre: StringUtils.trimToNull( pSummary.genre ),
        brand: StringUtils.trimToNull( pSummary.brand ),
        lineas: pSummary.lines,
        qtyTotal: pSummary.qtyTotal
    ]
    imprimeTicket( "template/ticket-resumen-inventario.vm", tkQtyOH )
  }


    void imprimeResumenExistenciasLc( InvOhSummary pSummary ) {
        log.debug( String.format( "Imprime Resume de Existencias\n%s", pSummary.toString() ) )
        DateFormat df = new SimpleDateFormat( "dd/MM/yyyy HH:mm" )
        Sucursal site = ServiceFactory.sites.obtenSucursalActual()
        /*Collections.sort( pSummary.lines, new Comparator<InvOhDet>() {
            @Override
            int compare(InvOhDet o1, InvOhDet o2) {
                return o1.id.compareTo(o2.id)
            }
        })*/
        String marca = pSummary.lines.get(0).brand
        Integer cantidad = 0
        Integer rowTmp = 0
        Integer row = 0
        for(int i = 0; i < pSummary.lines.size(); i++){
          if( StringUtils.trimToEmpty(marca).equalsIgnoreCase(pSummary.lines.get(i).brand) ||
                  pSummary.lines.get(i).brand == null ){
              cantidad = cantidad+pSummary.lines.get(i).qty
          } else {
              pSummary.lines.get(rowTmp).setQtyByBrand( cantidad )
              cantidad = 0
              cantidad = cantidad+pSummary.lines.get(i).qty
          }
          if( i == pSummary.lines.size()-1 ){
              pSummary.lines.get(rowTmp).setQtyByBrand( cantidad )
              cantidad = 0
              cantidad = cantidad+pSummary.lines.get(i).qty
          }
          if( pSummary.lines.get(i).brand != null ){
            rowTmp = row
            marca = pSummary.lines.get(i).brand
            if( pSummary.lines.size() == i+1 ){
              pSummary.lines.get(rowTmp).setQtyByBrand( cantidad )
            }
          }
          row = row+1
        }

        List<InvOhDet> lineas = new ArrayList<InvOhDet>()
        def lines = []
        if( pSummary.resume ){
          for(InvOhDet invDet : pSummary.lines){
            if( invDet.brand != null ){
              lineas.add( invDet )
            }
          }
        } else {
          for(InvOhDet invDet : pSummary.lines){
            if( invDet.desc.length() > 29 ){
              def line = [
                id: invDet.id,
                desc: invDet.desc.substring(0,29),
                desc1: invDet.desc.substring(29),
                qty: invDet.qty
              ]
              lineas.add( line )
            } else {
              lineas.add( invDet )
            }
          }
        }

        def tkQtyOH = [
                nombre_ticket: "ticket-resumen-inventario-lc",
                effDate: df.format( new Date() ),
                thisSite: TransInvAdapter.instance.getText( site ),
                genre: StringUtils.trimToNull( pSummary.genre ),
                brand: StringUtils.trimToNull( pSummary.brand ),
                lineas: lineas.size() <= 0 ? pSummary.lines : lineas,
                all: !pSummary.resume,
                qtyTotal: pSummary.qtyTotal
        ]
        imprimeTicket( "template/ticket-resumen-inventario-lc.vm", tkQtyOH )
    }

  @Override
  void imprimeReferenciaFiscal( String idFiscal ) {
    log.info( "imprimiendo ticket referencia fiscal con idFiscal: ${idFiscal}" )
    Comprobante comprobante = comprobanteService.obtenerComprobante( idFiscal )
    if ( comprobante?.id ) {
      Integer idCliente = comprobante.idCliente?.isInteger() ? comprobante.idCliente.toInteger() : 0
      Cliente cliente = clienteRepository.findOne( idCliente )
      AddressAdapter companyAddress = Registry.companyAddress
      Contribuyente contribuyente = Registry.company
      Map<String, Object> items = [
          nombre_ticket: 'ticket-referencia-fiscal',
          comprobante: comprobante,
          email: StringUtils.trimToEmpty( comprobante.email ),
          cliente: cliente,
          empresa: companyAddress.shortName,
          email_contacto: contribuyente.email.trim(),
          telefono_contacto: ''
      ]
      if ( Registry.isSunglass() ) {
        imprimeTicket( 'template/ticket-referencia-fiscal-si.vm', items )
      } else {
        imprimeTicket( 'template/ticket-referencia-fiscal.vm', items )
      }
    } else {
      log.warn( 'no se imprime ticket referencia fiscal, no existe comprobante' )
    }
  }

  @Override
  void imprimeComprobanteFiscal( String idFiscal ) {
    log.info( "imprimiendo ticket comprobante fiscal con idFiscal: ${idFiscal}" )
    Comprobante comprobante = comprobanteService.obtenerComprobante( idFiscal )
    if ( comprobante?.id ) {
      List<File> archivos = comprobanteService.descargarArchivosComprobante( idFiscal )
      if ( archivos?.any() ) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance( Locale.US )
        Integer idCliente = comprobante.idCliente?.isInteger() ? comprobante.idCliente.toInteger() : 0
        Cliente cliente = clienteRepository.findOne( idCliente )
        Integer idSucursal = sucursalRepository.getCurrentSucursalId()
        Sucursal sucursal = sucursalRepository.findOne( idSucursal )
        File xml = archivos.first()
        def cfd = new XmlSlurper().parseText( xml?.text )
        String rfcEmisor = cfd.Emisor.@rfc
        Contribuyente contribuyenteEmisor = contribuyenteService.obtenerContribuyentePorRfc( rfcEmisor )
        AddressAdapter companyAddress = Registry.companyAddress
        Estado estadoEmisor = estadoService.obtenerEstado( contribuyenteEmisor?.idEstado )
        def emisor
        if ( contribuyenteEmisor != null ) {
          emisor = [
              rfc: contribuyenteEmisor?.rfc,
              nombre: contribuyenteEmisor?.nombre,
              calle: contribuyenteEmisor?.domicilio,
              colonia: contribuyenteEmisor?.colonia,
              municipio: contribuyenteEmisor?.ciudad,
              estado: estadoEmisor?.nombre,
              codigo_postal: contribuyenteEmisor?.codigoPostal,
              regimen_fiscal: cfd.Emisor.RegimenFiscal.@Regimen
          ]
        } else {
          emisor = [
              rfc: companyAddress?.taxId,
              nombre: companyAddress?.name,
              calle: companyAddress?.address_1,
              colonia: companyAddress?.address_2,
              municipio: companyAddress?.city,
              estado: estadoEmisor?.nombre,
              codigo_postal: companyAddress?.CP,
              regimen_fiscal: cfd.Emisor.RegimenFiscal.@Regimen
          ]
        }
        def receptor = [
            rfc: cfd.Receptor.@rfc,
            nombre: cfd.Receptor.@nombre,
            calle: cfd.Receptor.Domicilio.@calle,
            colonia: cfd.Receptor.Domicilio.@colonia,
            municipio: cfd.Receptor.Domicilio.@municipio,
            estado: cfd.Receptor.Domicilio.@estado,
            codigo_postal: cfd.Receptor.Domicilio.@codigoPostal
        ]
        def conceptos = [ ]
        cfd.Conceptos.Concepto.each {
          String precioTxt = it.@valorUnitario ?: ''
          Double precio = precioTxt.isNumber() ? precioTxt.toDouble() : 0
          String importeTxt = it.@importe ?: ''
          Double importe = importeTxt.isNumber() ? importeTxt.toDouble() : 0
          String descripcionTmp = it.@descripcion
          String descripcion = WordUtils.wrap( descripcionTmp, 24 )
          List<String> descripciones = descripcion.tokenize( '\n' ) ?: [ ]
          def concepto = [
              cantidad: it.@cantidad,
              unidad: it.@unidad,
              descripcion: descripciones.any() ? descripciones.first() : '',
              valor_unitario: formatter.format( precio ),
              importe: formatter.format( importe )
          ]
          conceptos.add( concepto )
          descripciones.eachWithIndex { String val, Integer idx ->
            if ( idx ) {
              def tmp = [
                  cantidad: '',
                  unidad: '',
                  descripcion: val,
                  valor_unitario: '',
                  importe: ''
              ]
              conceptos.add( tmp )
            }
          }
        }
        def impuestos = [ ]
        cfd.Impuestos.Traslados.Traslado.each {
          String importeTxt = it.@importe ?: ''
          Double importe = importeTxt.isNumber() ? importeTxt.toDouble() : 0
          def impuesto = [
              impuesto: it.@impuesto,
              tasa: it.@tasa,
              importe: formatter.format( importe )
          ]
          impuestos.add( impuesto )
        }
        String subtotalTxt = cfd.@subTotal ?: ''
        Double subtotal = subtotalTxt.isNumber() ? subtotalTxt.toDouble() : 0
        String totalImpuestosTxt = cfd.Impuestos.@totalImpuestosTrasladados ?: ''
        Double totalImpuestos = totalImpuestosTxt.isNumber() ? totalImpuestosTxt.toDouble() : 0
        String totalTxt = cfd.@total ?: ''
        BigDecimal total = totalTxt.isNumber() ? totalTxt.toBigDecimal() : 0
        RuleBasedNumberFormat textFormatter = new RuleBasedNumberFormat( new Locale( 'es' ), RuleBasedNumberFormat.SPELLOUT )
        String textoTotal = "${textFormatter.format( total.intValue() )} ${total.remainder( 1 ).unscaledValue()}/100 M.N."

        Map<String, Object> items = [
            nombre_ticket: 'ticket-comprobante-fiscal',
            folio: "${cfd.@serie}-${cfd.@folio}",
            fecha: cfd.@fecha,
            sello: cfd.@sello,
            cadena_original: null,
            sello_cfdi: null,
            cadena_original_cfdi: null,
            num_aprobacion: cfd.@noAprobacion,
            anio_aprobacion: cfd.@anoAprobacion,
            lugar_expedicion: cfd.@LugarExpedicion,
            forma_pago: cfd.@formaDePago,
            num_certificado: cfd.@noCertificado,
            subtotal: formatter.format( subtotal ),
            total_impuestos: formatter.format( totalImpuestos ),
            total: formatter.format( total ),
            texto_total: textoTotal.toUpperCase(),
            metodo_pago: cfd.@metodoDePago,
            comprobante: comprobante,
            emisor: emisor,
            receptor: receptor,
            cliente: cliente,
            sucursal: sucursal,
            conceptos: conceptos,
            impuestos: impuestos,
            leyenda: "Este documento es una representacion impresa de un CFD",
            empresa: companyAddress.shortName
        ]

        imprimeTicket( 'template/ticket-comprobante-fiscal.vm', items )
      } else {
        log.warn( 'no se imprime ticket comprobante fiscal, no se obtienen archivos' )
      }
    } else {
      log.warn( 'no se imprime ticket comprobante fiscal, no existe comprobante' )
    }
  }

  void imprimeCotizacion( Cotizacion cotizacion, CotizaDet cotizaDet, boolean totalizar, boolean convenio, String convenioDesc ) {
    log.debug( "imprimeCotizacion" )
    DateFormat df = new SimpleDateFormat( "dd-MM-yyyy" )
    SimpleDateFormat nextDate = new SimpleDateFormat( "dd MMMM yyyy" )
    BigDecimal totalMonto = BigDecimal.ZERO
    String total = " "
    String convenioNota
    String convenioAst
    String tipoArticulo = ' '
    Sucursal site = ServiceFactory.sites.obtenSucursalActual()

    if ( convenio && convenioDesc != null ) {
      String nombre = convenioDesc.trim()
      convenioNota = "* Precios especiales para convenio $nombre, para hacerlos validos es necesario presentar la documentacion pactada con la empresa"
      convenioAst = "*"
    } else {
      convenioNota = ''
      convenioAst = ''
    }

    def articulos = [ ]
    QCotizaDet cotizadet = QCotizaDet.cotizaDet
    Iterable<CotizaDet> lstArticulos = cotizaDetRepository.findAll( cotizadet.id_cotiza.eq( cotizacion.id ) )

    Calendar fechaExp = Calendar.getInstance();
    fechaExp.add( Calendar.MONTH, 1 );

    NumberFormat formatter = NumberFormat.getCurrencyInstance( Locale.US )
    String letrero = " "
    log.debug( "Totalizar::", totalizar )
    BooleanBuilder qColor = new BooleanBuilder()

    Iterable<Articulo> lstArt = null
    if ( totalizar ) {
      for ( CotizaDet cotizaciones : lstArticulos ) {
        QArticulo art = QArticulo.articulo1
        if ( cotizaciones.color != null && cotizaciones.color.length() > 0 ) {
          qColor.and( art.codigoColor.eq( cotizaciones.color ) )
        } else {
          qColor.and( art.codigoColor.isNull() )
        }

        lstArt = articuloRepository.findAll( art.articulo.eq( cotizaciones.articulo ).
            and( qColor ) )
        totalMonto = totalMonto.add( cotizaciones.precioUnit )
        total = formatter.format( totalMonto ).toString()
        if ( lstArt.iterator().hasNext() ) {
          Articulo articulo = lstArt.iterator().next()
          tipoArticulo = genericoRepository.findOne( articulo.generico.id ).descripcion
        }
      }
      log.debug( "Total::", total )
      letrero = "TOTAL:"
    } else {
      for ( CotizaDet cotizaciones : lstArticulos ) {
        if ( lstArt.iterator().hasNext() ) {
          Articulo articulo = lstArt.iterator().next()
          tipoArticulo = genericoRepository.findOne( articulo.generico.id ).descripcion
        }
      }
    }

    lstArticulos?.each { CotizaDet tmp ->
      String articuloDesc = StringUtils.trimToEmpty( tmp?.articulos?.descripcion )
      String color = tmp?.color
      BigInteger precio = tmp?.precioUnit
      tipoArticulo = genericoRepository.findOne( tmp?.articulos?.idGenerico ).descripcion
      String descripcion
      if ( articuloDesc.length() > LONGITUD_MAXIMA ) {
        descripcion = articuloDesc.substring( 0, LONGITUD_MAXIMA )
      } else {
        descripcion = articuloDesc
      }

      def articulo = [
          articulo: "[${tmp?.articulos?.id}] ${tipoArticulo} ${tmp?.articulos?.marca}",
          precio: formatter.format( precio ),
      ]
      articulos.add( articulo )
    }

    AddressAdapter companyAddress = Registry.companyAddress
    def data = [
        date: df.format( new Date() ),
        thisSite: site,
        compania: companyAddress,
        nombre: cotizacion.nombre,
        observaciones: cotizacion.observaciones,
        cotizacionId: cotizacion.id,
        empleado: cotizacion.emp,
        articulos: articulos,
        total: total,
        letrero: letrero,
        nextDate: nextDate.format( fechaExp.getTime() ).toString().toUpperCase(),
        notaConvenio: convenioNota,
        asteriscoConvenio: convenioAst
    ]
    imprimeTicket( "template/ticket-cotizacion.vm", data )
  }

  void imprimeAperturaCaja( Date fechaApertura ) {
    log.debug( "Imprime Apertura Caja" )
    DateFormat df = new SimpleDateFormat( "dd-MM-yyyy" )
    DateFormat dft = new SimpleDateFormat( "dd-MM-yyyy HH:mm a" )
    NumberFormat formatter = NumberFormat.getCurrencyInstance( Locale.US )

    Parametro parametroGerente = parametroRepository.findOne( TipoParametro.ID_GERENTE.value )
    Empleado gerente = empleadoRepository.findById( parametroGerente.valor )
    log.debug( "Gerente", gerente.nombreCompleto )
    Sucursal site = ServiceFactory.sites.obtenSucursalActual()
    Apertura apertura = aperturaRepository.findOne( fechaApertura )
    MonedaDetalle monedaDetEur = monedaExtranjeraService.findActiveRate( "EUR", apertura.fechaApertura )
    MonedaDetalle monedaDetUsd = monedaExtranjeraService.findActiveRate( "USD", apertura.fechaApertura )

    def data = [
        sucursal: site.id,
        fecha: df.format( apertura.fechaApertura ),
        mnx: formatter.format( apertura.efvoPesos ),
        usd: formatter.format( apertura.efvoDolares ),
        observaciones: apertura.observaciones,
        monedasDetUsd: formatter.format( monedaDetUsd != null ? monedaDetUsd?.tipoCambio : BigDecimal.ZERO ),
        monedasDetEur: formatter.format( monedaDetEur != null ? monedaDetEur?.tipoCambio : BigDecimal.ZERO ),
        gerente: gerente.nombreCompleto,
        fechaImpresion: dft.format( new Date() )
    ]
    imprimeTicket( "template/ticket-aperturaCaja.vm", data )
  }

  void imprimeDevolucion( TransInv pTrans ) {
    TransInvAdapter adapter = TransInvAdapter.instance
    def parts = [ ]
    for ( TransInvDetalle trDet in pTrans.trDet ) {
      Articulo part = articuloRepository.findOne( trDet.sku )
      def tkPart = [
          sku: adapter.getText( trDet, adapter.FLD_TRD_SKU ),
          partNbr: adapter.getText( part, adapter.FLD_PART_CODE ),
          desc: adapter.getText( part, adapter.FLD_PART_DESC ),
          qty: adapter.getText( trDet, adapter.FLD_TRD_QTY )
      ]
      parts.add( tkPart )
    }
    AddressAdapter companyAddress = Registry.companyAddress
    Sucursal site = sucursalRepository.findOne( pTrans.sucursal )
    Empleado emp = empleadoRepository.findOne( pTrans.idEmpleado )
    List<String> remarks = adapter.split( StringUtils.trimToEmpty( pTrans.observaciones ), 40 )
    Parametro parametroGerente = parametroRepository.findOne( TipoParametro.ID_GERENTE.value )
    Empleado mgr = empleadoRepository.findById( parametroGerente.valor )
    /*if ( site.idGerente != null ) {
      mgr = empleadoRepository.findById( site.idGerente )
    }*/
    if ( InventorySearch.esTipoTransaccionSalida( pTrans.idTipoTrans ) ) {
      def tkInvTr = [
          nombre_ticket: "ticket-devolucion",
          effDate: adapter.getText( pTrans, adapter.FLD_TR_EFF_DATE ),
          compania: companyAddress,
          sucursal: site,
          user: adapter.getText( emp ),
          mgr: adapter.getText( mgr ),
          ticket: adapter.getText( pTrans, adapter.FLD_SRC_TICKET ),
          empName: adapter.getText( pTrans, adapter.FLD_SALES_PERSON ),
          returnAmount: adapter.getText( pTrans, adapter.FLD_RETURN_AMOUNT ),
          trNbr: adapter.getText( pTrans, adapter.FLD_TR_NBR ),
          remarks_1: ( remarks.size() > 0 ? remarks.get( 0 ) : "" ),
          remarks_2: ( remarks.size() > 1 ? remarks.get( 1 ) : "" ),
          parts: parts
      ]
      imprimeTicket( "template/ticket-salida-inventario.vm", tkInvTr )
    } else if ( InventorySearch.esTipoTransaccionAjuste( pTrans.idTipoTrans ) ) {
      def tkInvTr = [
          nombre_ticket: "ticket-ajuste-inventario",
          effDate: adapter.getText( pTrans, adapter.FLD_TR_EFF_DATE ),
          thisSite: adapter.getText( site ),
          user: adapter.getText( emp ),
          mgr: adapter.getText( mgr ),
          trNbr: adapter.getText( pTrans, adapter.FLD_TR_NBR ),
          siteTo: adapter.getText( siteTo ),
          remarks_1: ( remarks.size() > 0 ? remarks.get( 0 ) : "" ),
          remarks_2: ( remarks.size() > 1 ? remarks.get( 1 ) : "" ),
          parts: parts
      ]
      imprimeTicket( "template/ticket-ajuste-inventario.vm", tkInvTr )
    }
  }

  void imprimeCotizacion( Integer pQuoteId, String idFactura ) {
    Cotizacion quote = RepositoryFactory.quotes.findOne( pQuoteId )
    if ( quote != null ) {
      log.debug( String.format( 'Print Cotizacion:%d', pQuoteId ) )
      Sucursal site = sucursalRepository.findOne( Registry.currentSite )
      Empleado salesmen = empleadoRepository.findOne( quote.idEmpleado )
      Cliente customer = clienteRepository.findOne( quote.idCliente )
      double totalAmt = 0
      def tkParts = [ ]
      for ( CotizaDet det : RepositoryFactory.quoteDetail.findByIdCotiza( quote.idCotiza ) ) {
        Articulo part = articuloRepository.findOne( det.sku )
        List<Precio> precios = precioRepository.findByArticulo(part.articulo.trim())
        Precio precio = new Precio()
        Boolean oferta = false
        for(Precio price : precios){
          if( StringUtils.trimToEmpty(price.lista).equalsIgnoreCase("O") && price.precio.compareTo(BigDecimal.ZERO) > 0 ){
            precio = price
            oferta = true
          }
        }
        if(precios.size() > 0 && !oferta){
          precio = precios.first()
        }
        String cantidad = ( det.cantidad != 1 ? String.format( '(%d@%,.2f)', det.cantidad, part.precio ) : '' )
        String price = String.format( '$%,.2f', det.cantidad * precio?.precio )
        totalAmt += ( det.cantidad * precio.precio )
        def tkPart = [
            desc: String.format( '[%d] %s %s  %s', part.id, part.generico?.descripcion, part.marca, cantidad ),
            price: price
        ]
        tkParts.add( tkPart )
      }

      Boolean leyendaCupon = false
      Boolean leyendaCupon3 = false
      BigDecimal monto2Par = notaVentaService.obtenerMontoCupon( StringUtils.trimToEmpty(idFactura) )
      BigDecimal monto3Par = notaVentaService.obtenerMontoCuponTercerPar( StringUtils.trimToEmpty(idFactura) )
      if( monto2Par.compareTo(BigDecimal.ZERO) > 0 && monto3Par.compareTo(BigDecimal.ZERO) ){
        leyendaCupon = true
      }
      if( Registry.tirdthPairValid() ){
        leyendaCupon3 = true
      }

      def tkCotiza = [
          company: Registry.companyShortName,
          quoteNbr: StringUtils.right( String.format( "      %d", quote.idCotiza ), 6 ),
          quoteDate: CustomDateUtils.format( quote.fechaMod, 'dd-MM-yyyy' ),
          site: ( site != null ? site.nombre : '' ),
          phone: ( site != null ? site.telefonos : '' ),
          empName: String.format( '(%s) %s', quote.idEmpleado, ( salesmen != null ? salesmen.nombreCompleto : '' ) ),
          custName: customer.nombreCompleto,
          remarks: quote.observaciones,
          parts: tkParts,
          totalPrice: String.format( '$%,.2f', totalAmt ),
          quoteExpires: CustomDateUtils.format( DateUtils.addDays( quote.fechaMod, 30 ), 'dd MMMM yyyy' ).toUpperCase(),
          monto2Par: String.format('$%.02f', monto2Par),
          monto3Par: String.format('$%.02f', monto3Par ),
          leyendaCupon: leyendaCupon,
          leyendaCupon3: leyendaCupon3
      ]
      this.imprimeTicket( 'template/ticket-cotizacion-simple.vm', tkCotiza )
    } else {
      log.debug( String.format( 'Cotizacion (%d) not found.', pQuoteId ) )
    }
  }


  @Override
  void imprimeRegresoMaterial( String idNotaVenta ){
     log.debug('imprimeRegresoMaterial( )')
      NotaVenta nota = notaVentaRepository.findOne(idNotaVenta)
      List<Modificacion> modificaciones = modificacionRepository.findByIdFactura(idNotaVenta)
      Integer idSuc = Registry.currentSite
      Sucursal sucursal = sucursalRepository.findOne(idSuc)
      Articulo articulo = new Articulo()
      for(DetalleNotaVenta detalle : nota.detalles){
        if(detalle.articulo.idGenerico.trim().equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
          articulo = detalle.articulo
        }
      }
      Parametro parametroGerente = parametroRepository.findOne( TipoParametro.ID_GERENTE.value )
      Empleado gerente = empleadoRepository.findById( parametroGerente.valor )
      if(nota != null && modificaciones.size() > 0){
         Modificacion mod = modificaciones.first()
      def datos = [ nombre_ticket: "ticket-regreso-material",
          idMod: mod.id,
          sucursal: sucursal.nombre+' ['+sucursal.id+']',
          fecha: mod.fecha.format('dd/MM/yyyy'),
          hora: mod.fecha.format('HH:mm:ss'),
          idFactura: nota.id,
          factura: nota.factura,
          idSucursal: idSuc,
          gerente: gerente?.nombreCompleto(),
          armazon: articulo.id != null ? (StringUtils.trimToEmpty(articulo.idCb) != '' ? articulo.articulo+'$'+articulo.idCb : articulo.articulo)  : ''
        ]
        this.imprimeTicket( 'template/ticket-regreso-material.vm', datos )
      } else {
          log.debug( String.format( 'Nota (%s) not found.', idNotaVenta ) )
      }
  }


  void imprimeRecepcionMaterial( String idNotaVenta ){
      log.debug('imprimeRecepcionMaterial( )')
      NotaVenta nota = notaVentaRepository.findOne(idNotaVenta)
      List<Modificacion> modificaciones = modificacionRepository.findByIdFactura(idNotaVenta)
      Integer idSuc = Registry.currentSite
      Sucursal sucursal = sucursalRepository.findOne(idSuc)
      List<Articulo> articulos = new ArrayList<>()
      for(DetalleNotaVenta detalle : nota.detalles){
          if(detalle.articulo.idGenerico.trim().equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
              articulos.add(detalle.articulo)
          }
      }
      Parametro parametroGerente = parametroRepository.findOne( TipoParametro.ID_GERENTE.value )
      Empleado gerente = empleadoRepository.findById( parametroGerente.valor )
      if(nota != null && modificaciones.size() > 0){
          Modificacion mod = modificaciones.first()
          def datos = [ nombre_ticket: "ticket-recepcion-material",
                  idMod: mod.id,
                  sucursal: sucursal.nombre+' ['+sucursal.id+']',
                  fecha: mod.fecha.format('dd/MM/yyyy'),
                  hora: mod.fecha.format('HH:mm:ss'),
                  idFactura: nota.id,
                  factura: nota.factura,
                  idSucursal: idSuc,
                  gerente: gerente?.nombreCompleto(),
                  armazones: articulos
          ]
          this.imprimeTicket( 'template/ticket-recepcion-material.vm', datos )
      } else {
          log.debug( String.format( 'Nota (%s) not found.', idNotaVenta ) )
      }
  }


  @Override
  void imprimePinoNoSurtido( String idNotaVenta ){
      log.debug('imprimePinoNoSurtido( )')
      NotaVenta nota = notaVentaRepository.findOne(idNotaVenta)
      Integer idSuc = Registry.currentSite
      Sucursal sucursal = sucursalRepository.findOne(idSuc)
      List<Articulo> articulos = new ArrayList<>()
      for(DetalleNotaVenta detalle : nota.detalles){
          if(detalle.articulo.idGenerico.trim().equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
              articulos.add(detalle.articulo)
          }
      }
      Parametro parametroGerente = parametroRepository.findOne( TipoParametro.ID_GERENTE.value )
      Empleado gerente = empleadoRepository.findById( parametroGerente.valor )
      if(nota != null){
          def datos = [ nombre_ticket: "ticket-pino-no-surtido",
                  fecha: new Date().format('dd/MM/yyyy'),
                  sucursal: sucursal.nombre+' ['+sucursal.id+']',
                  factura: nota.factura,
                  idSucursal: idSuc,
                  gerente: gerente?.nombreCompleto(),
                  armazones: articulos.first()
          ]
          this.imprimeTicket( 'template/ticket-pino-no-surtido.vm', datos )
      } else {
          log.debug( String.format( 'Nota (%s) not found.', idNotaVenta ) )
      }
  }





    @Override
    void imprimeTicketReuso( String idNotaVenta ){
        log.debug('imprimeTicketReuso( )')
        NotaVenta nota = new NotaVenta()
        NotaVenta notaNueva = notaVentaRepository.findOne(idNotaVenta)
        Integer idSuc = Registry.currentSite
        Sucursal sucursal = sucursalRepository.findOne(idSuc)
        String factOri = ''
        def lstArticulos = []
        List<Articulo> articulos = new ArrayList<>()
        QPago pay = QPago.pago
        List<Pago> pagosTransf = pagoRepository.findAll( pay.idFactura.eq(notaNueva.id).and(pay.referenciaPago.isNotNull()).and(pay.referenciaPago.isNotEmpty()) )
        if( pagosTransf.size() > 0 ){
          nota = notaVentaRepository.findOne(StringUtils.trimToEmpty(pagosTransf.first().referenciaPago))
        }
        for(DetalleNotaVenta detalle : notaNueva.detalles){
            if(TAG_GENERICO_INV.contains(StringUtils.trimToEmpty(detalle.articulo.idGenerico))){
                articulos.add(detalle.articulo)
                def artTmp = [
                        articulo: detalle.articulo.articulo,
                        cantidad: detalle.cantidadFac,
                        tipo: StringUtils.trimToEmpty(detalle.idTipoDetalle)
                ]
                lstArticulos.add( artTmp )
            }
        }
        Parametro parametroGerente = parametroRepository.findOne( TipoParametro.ID_GERENTE.value )
        Empleado gerente = empleadoRepository.findById( parametroGerente.valor )
        if(nota != null){
            def datos = [ nombre_ticket: "ticket-reuso",
                    fecha: new Date().format('dd/MM/yyyy'),
                    hora: new Date().format('HH:mm:ss'),
                    sucursal: sucursal.nombre+' ['+sucursal.id+']',
                    facturaOriginal: StringUtils.trimToEmpty(nota.factura).length() > 0 ? StringUtils.trimToEmpty(nota.factura) : "",
                    factura: notaNueva.factura,
                    idSoi: notaNueva.id,
                    idSucursal: idSuc,
                    gerente: gerente?.nombreCompleto(),
                    armazones: articulos.first(),
                    articulos: lstArticulos
            ]
            this.imprimeTicket( 'template/ticket-reuso.vm', datos )
        } else {
            log.debug( String.format( 'Nota (%s) not found.', idNotaVenta ) )
        }
    }


    @Override
    void imprimeTicketEnvioLc( String idPedido ){
      log.debug( "imprimeTicketEnvioLc( )" )
      PedidoLc pedidoLc = pedidoLcRepository.findOne( idPedido )
      SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy")
      if( pedidoLc != null ){
        def datos = [
            ticket: StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+StringUtils.trimToEmpty(pedidoLc.id),
            pedidos_det: pedidoLc.pedidoLcDets,
            fecha: df.format( new Date() )
        ]
        this.imprimeTicket( 'template/ticket-envio-pedido-lc.vm', datos )
      } else {
          log.debug( String.format( 'Pedido (%s) not found.', idPedido ) )
      }
    }


    @Override
    void imprimeTicketPedidosLcPendientes( Date fechaCierre){
        log.debug( "imprimeTicketEnvioLc( )" )
        QPedidoLc qPedidoLc = QPedidoLc.pedidoLc
        def pedidos = []
        List<PedidoLc> lstPedidoLc = new ArrayList<>()
        List<PedidoLc> pedidosTmp = pedidoLcRepository.findAll( qPedidoLc.fechaEnvio.isNull(), qPedidoLc.id.asc() )
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy")
        Sucursal sucursal = sucursalRepository.findOne( Registry.currentSite )
        for(PedidoLc pedidoLc1 : pedidosTmp){
          List<NotaVenta> lstNotas = notaVentaRepository.findByFactura( pedidoLc1.id )
          if( lstNotas.size() > 0 ){
              if( !StringUtils.trimToEmpty(lstNotas.get(0).sFactura).equalsIgnoreCase(TAG_FACTURA_CANCELADA)
                      && lstNotas.get(0).fechaEntrega == null ){
                lstPedidoLc.add( pedidoLc1 )
              }
          }
        }
        for( PedidoLc pedidoLc : lstPedidoLc ){
          List<NotaVenta> notaVenta = notaVentaRepository.findByFactura( StringUtils.trimToEmpty(pedidoLc.id) )
          //if( !notaVenta.get(0).sFactura.trim().equalsIgnoreCase(TAG_FACTURA_CANCELADA) ){
              String modelos = ""
              Integer cantidad = 0
              for(PedidoLcDet pedidoLcDet : pedidoLc.pedidoLcDets){
                  cantidad = cantidad+pedidoLcDet.cantidad
                  if(!modelos.contains(StringUtils.trimToEmpty(pedidoLcDet.modelo))){
                      modelos = modelos+","+StringUtils.trimToEmpty(pedidoLcDet.modelo)
                  }
              }
              if(modelos.startsWith(",")){
                  modelos = modelos.replaceFirst(",","")
              }
              def pedido = [
                      ticket: pedidoLc.id,
                      productos: modelos,
                      cantidad: cantidad
              ]
              pedidos.add( pedido )
          //}
        }
        if( lstPedidoLc.size() > 0 ){
            def datos = [
                    sucursal: sucursal.nombre+"("+sucursal.centroCostos+")",
                    fecha: df.format( fechaCierre ),
                    cant_pedido_pendiente: lstPedidoLc.size(),
                    pedidos: pedidos
            ]
            this.imprimeTicket( 'template/ticket-pendientes-envio-pedido-lc.vm', datos )
        } else {
            log.debug( 'No hay pedidos pendientes por enviar' )
        }
    }


  @Override
  void imprimeCupon( CuponMv cuponMv, String titulo, BigDecimal monto ){
    log.debug( "imprimeCupon( )" )
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy")
    String restrictions = ""
    String restrictions1 = ""
    String titulo2 = ""
    if( cuponMv != null ){
      if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("F") ){
        restrictions = 'APLICA EN LA COMPRA MINIMA DE $1000.00'
        restrictions1 = 'CONSULTA CONDICIONES EN TIENDA.'
      } else if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("H") ){
        restrictions = 'APLICAN RESTRICCIONES'
      }
    }
    if( StringUtils.trimToEmpty(cuponMv.claveDescuento).startsWith("H") ){
      NotaVenta notaVenta = notaVentaService.obtenerNotaVentaPorTicket( "${StringUtils.trimToEmpty(Registry.currentSite.toString())}-${StringUtils.trimToEmpty(cuponMv.facturaOrigen)}" );
      if( notaVenta != null ){
        Integer contador = 0
        for(DetalleNotaVenta det : notaVenta.detalles){
          if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase("H") ){
            titulo2 = SubtypeCouponsUtils.getTitle2( det.articulo.subtipo )
          }
        }
      }
    }
    if( cuponMv != null ){
      def datos = [
        titulo: titulo,
        titulo2: titulo2,
        monto: String.format('$%s', monto),
        clave: cuponMv.claveDescuento,
        fecha_vigencia: df.format(cuponMv.fechaVigencia),
        restrictions: restrictions,
        restrictions1: restrictions1
      ]
      this.imprimeTicket( 'template/ticket-cupon.vm', datos )
    } else {
          log.debug( String.format( 'Cupon (%s) not found.', cuponMv.claveDescuento ) )
    }
  }


    void imprimeResumenCuponCan( String idFactura, List<String> porDev ){
      NumberFormat formatterMoney = new DecimalFormat( '$#,##0.00' )
      log.debug( "imprimeResumenCuponCan( )" )
      NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
      if( notaVenta != null && StringUtils.trimToEmpty(notaVenta.factura).length() > 0 ){
        List<CuponMv> lstCuponesTmp = notaVentaService.obtenerCuponMvFacturaOriApplied( StringUtils.trimToEmpty(notaVenta.factura) )
        List<CuponMv> lstCupones = new ArrayList<>()
        for(CuponMv cuponMv2 : lstCuponesTmp){
          String ticket = StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+StringUtils.trimToEmpty(cuponMv2.facturaDestino)
          NotaVenta facturaDestino = notaVentaService.obtenerNotaVentaPorTicket( ticket )
          if( !facturaDestino.sFactura.equalsIgnoreCase(TAG_FACTURA_CANCELADA) ){
            lstCupones.add( cuponMv2 )
          }
        }
        def cupones = []
        BigDecimal sumaPagos = BigDecimal.ZERO
        BigDecimal sumaCupones = BigDecimal.ZERO
        String porDevolver = BigDecimal.ZERO
        String articulos = ""
        BigDecimal cuponesTotal = BigDecimal.ZERO
        for(DetalleNotaVenta det : notaVenta.detalles){
          articulos = articulos+","+StringUtils.trimToEmpty(det.articulo.articulo)
        }
        if( articulos.startsWith(",") ){
          articulos = articulos.replaceFirst(",","")
        }
        for(CuponMv cuponMv1 : lstCupones){
          String cliente = ""
          String articulosDest = ""
          String saldo = ""
          String fechaEntrega = ""
          List<NotaVenta> notaDestino = notaVentaRepository.findByFactura( StringUtils.trimToEmpty(cuponMv1.facturaDestino) )
          if( notaDestino.size() > 0 ){
            cliente = notaDestino.first().cliente.nombreCompleto
            saldo = formatterMoney.format(notaDestino.first().ventaNeta.subtract(notaDestino.first().sumaPagos))
            fechaEntrega = notaDestino.first().fechaEntrega != null ? notaDestino.first().fechaEntrega.format("dd-MM-yyyy") : ""
            for(DetalleNotaVenta detalleNotaVenta : notaDestino.first().detalles){
              articulosDest = articulosDest+","+StringUtils.trimToEmpty(detalleNotaVenta.articulo.articulo)
            }
            if( articulosDest.startsWith(",") ){
              articulosDest = articulosDest.replaceFirst(",","")
            }
          }
          BigDecimal montoC = BigDecimal.ZERO
          if( notaDestino.first().montoDescuento.doubleValue() <= cuponMv1.montoCupon ){
            cuponesTotal = cuponesTotal.add(notaDestino.first().montoDescuento)
            montoC = notaDestino.first().montoDescuento
          } else {
            cuponesTotal = cuponesTotal.add(cuponMv1.montoCupon)
            montoC = cuponMv1.montoCupon
          }
          //cuponesTotal = cuponesTotal.add(cuponMv1.montoCupon)
          def facturaApl = [
              cliente: cliente,
              factura: cuponMv1.facturaDestino,
              articulos: articulosDest,
              fecha: cuponMv1.fechaAplicacion.format("dd/MM/yyyy"),
              monto: formatterMoney.format(montoC),
              saldo: saldo,
              fechaEntrega: fechaEntrega
          ]
          cupones.add( facturaApl )
          if( notaDestino.first().montoDescuento.doubleValue() <= cuponMv1.montoCupon ){
            sumaCupones = sumaCupones.add(notaDestino.first().montoDescuento)
          } else {
            sumaCupones = sumaCupones.add(cuponMv1.montoCupon)
          }
          //sumaCupones = sumaCupones.add(cuponMv1.montoCupon)
        }
        Boolean tieneTarjeta = false
        for( Pago pago : notaVenta.pagos ){
          if(StringUtils.trimToEmpty(pago.idFPago).equalsIgnoreCase(TAG_TARJETA_CREDITO)){
            tieneTarjeta = true
          }
          sumaPagos = sumaPagos.add(pago.monto)
        }
          String amount = ""
          Collections.sort(notaVenta.pagos as List<Pago>, new Comparator<Pago>() {
              @Override
              int compare(Pago o1, Pago o2) {
                  return o1.idFPago.compareTo(o2.idFPago)
              }
          })
          for(Pago payment : notaVenta.pagos){
              if( payment.monto.doubleValue() >= sumaCupones ){
                  String tipoPago = "EF"
                  if( org.apache.commons.lang.StringUtils.trimToEmpty(payment.idFPago).equalsIgnoreCase(TAG_TARJETA_CREDITO) ){
                      tipoPago = 'TC'
                  }
                  amount = amount+", "+String.format( '$%.2f-%s',payment.monto.subtract(sumaCupones),tipoPago)
              }
              sumaCupones = sumaCupones.doubleValue()-payment.monto.doubleValue() < 0.00 ? BigDecimal.ZERO : sumaCupones.doubleValue()-payment.monto.doubleValue()
          }
          if( amount.startsWith(", ") ){
              amount = amount.replaceFirst(",","")
          } else if( org.apache.commons.lang.StringUtils.trimToEmpty(amount).length() <= 0 ){
              amount = '$0.00'
          }
        porDevolver = amount//String.format('$%.02f', sumaPagos.subtract(sumaCupones) )
        BigDecimal montoPagos = BigDecimal.ZERO
        for(Pago pago : notaVenta.pagos){
          montoPagos = montoPagos.add(pago.monto)
          pago.setIdMod( formatterMoney.format(pago.monto) )
        }
        if( lstCupones.size() > 0 ){
            def datos = [
              titulo: "RESUMEN CANCELACION",
              factura: StringUtils.trimToEmpty(notaVenta.factura),
              articulos: articulos,
              pagos: notaVenta.pagos,
              cupones: cupones,
              importeDevolver: porDev,
              tieneTarjeta: tieneTarjeta,
              pagoTotal: formatterMoney.format(montoPagos),
              cuponesTotal: cuponesTotal
            ]
            this.imprimeTicket( 'template/ticket-resumen-can-cupon.vm', datos )
        } else {
            log.debug( String.format( 'Cupones not found.' ) )
        }
      }
    }


    void imprimeDepositosResumenDiario( Date fechaCierre ){
      log.debug( "imprimeDepositosResumenDiario( )" )
      NumberFormat formatterMoney = new DecimalFormat( '$#,##0.00' )
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy")
      QDeposito qDeposito = QDeposito.deposito
      List<Deposito> depositos = depositoRepository.findAll( qDeposito.fechaCierre.eq(fechaCierre) ) as List<Deposito>
      Sucursal sucursal = sucursalRepository.findOne( Registry.currentSite )
      for(Deposito deposito : depositos){
        def datos = [
             nombre_ticket: 'ticket-deposito',
             sucursal: String.format("%s %s",sucursal.centroCostos,sucursal.nombre),
             importe: formatterMoney.format(deposito.monto),
             fechaIngreso: df.format(fechaCierre)
        ]
        this.imprimeTicket( 'template/ticket-deposito.vm', datos )
      }
    }


    NotaVenta imprimeGarantia( BigDecimal montoGarantia, String idArticulo, String tipoSeguro, String idFactura, Boolean doubleEnsure ){
      log.debug( "imprimeGarantia( )" )
      DateFormat df = new SimpleDateFormat( "dd-MM-yy" )
      NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
      Boolean validPayments = true
      for(Pago pago : notaVenta.pagos){
        if( StringUtils.trimToEmpty(pago.idFPago).equalsIgnoreCase("FE") || StringUtils.trimToEmpty(pago.idFPago).equalsIgnoreCase("FM") ){
          validPayments = false
        }
      }
      if( validPayments ){
          Sucursal site = ServiceFactory.sites.obtenSucursalActual()
          AddressAdapter companyAddress = Registry.companyAddress
          Integer validity = 0
          Calendar calendar = Calendar.getInstance();
          if( notaVenta != null ){
              if( tipoSeguro.equalsIgnoreCase("N") ){
                  validity = Registry.validityEnsureKid
              } else if( tipoSeguro.equalsIgnoreCase("L") ){
                  validity = Registry.validityEnsureOpht
              } else if( tipoSeguro.equalsIgnoreCase("S") ){
                  validity = Registry.validityEnsureFrame
              }
              Integer porcGar = Registry.percentageWarranty
              BigDecimal porcentaje = montoGarantia.multiply(porcGar/100)
              Integer monto = porcentaje.intValue()
              String clave = ""
              if( StringUtils.trimToEmpty(notaVenta.udf5).length() > 0 ){
                  if( doubleEnsure && !notaVenta.udf5.contains(",") ){
                      calendar.add(Calendar.YEAR, validity);
                      String date = df.format(calendar.getTime())
                      Integer fecha = 0
                      try{
                          fecha = NumberFormat.getInstance().parse(date.replace("-",""))
                      } catch ( NumberFormatException e ){ println e }
                      clave = claveAleatoria( fecha, monto )
                      clave = tipoSeguro+clave
                  } else {
                      if( notaVenta.udf5.contains(",") ){
                          String[] claves = notaVenta.udf5.split(",")
                          for(String cl : claves){
                              if( cl.charAt(0).equals(tipoSeguro.charAt(0)) ){
                                  clave = cl
                              }
                          }
                      } else {
                          clave = StringUtils.trimToEmpty(notaVenta.udf5)
                      }
                      calendar.setTime(notaVenta.fechaEntrega);
                      calendar.add(Calendar.YEAR, validity);
                  }
              } else {
                  calendar.add(Calendar.YEAR, validity);
                  String date = df.format(calendar.getTime())
                  Integer fecha = 0
                  try{
                      fecha = NumberFormat.getInstance().parse(date.replace("-",""))
                  } catch ( NumberFormatException e ){ println e }
                  clave = claveAleatoria( fecha, monto )
                  clave = tipoSeguro+clave
              }

              if( StringUtils.trimToEmpty(notaVenta.udf5).length() <= 0 ){
                  notaVenta.udf5 = clave
                  //notaVentaService.saveOrder( notaVenta )
              } else if( doubleEnsure && !notaVenta.udf5.contains(",") ) {
                  notaVenta.udf5 = notaVenta.udf5+","+clave
                  //notaVentaService.saveOrder( notaVenta )
              }

              def data = [
                      date: df.format( calendar.getTime() ),
                      thisSite: site,
                      compania: companyAddress,
                      codaleatorio: clave,
                      articulo: idArticulo
              ]
              imprimeTicket( "template/ticket-garantia.vm", data )
          }
          return notaVenta
      } else {
        return null
      }
    }


}