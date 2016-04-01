package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.java.querys.AcusesQuery
import mx.lux.pos.java.querys.AcusesTipoQuery
import mx.lux.pos.java.querys.DescuentosClaveQuery
import mx.lux.pos.java.querys.DescuentosQuery
import mx.lux.pos.java.querys.FormaContactoQuery
import mx.lux.pos.java.querys.JbQuery
import mx.lux.pos.java.querys.JbTrackQuery
import mx.lux.pos.java.querys.ParametrosQuery
import mx.lux.pos.java.querys.PedidoLcQuery
import mx.lux.pos.java.querys.PreciosQuery
import mx.lux.pos.java.querys.PromocionQuery
import mx.lux.pos.java.repository.AcusesJava
import mx.lux.pos.java.repository.AcusesTipoJava
import mx.lux.pos.java.repository.ArticulosJava
import mx.lux.pos.java.repository.AutorizaMovJava
import mx.lux.pos.java.repository.BancoEmisorJava
import mx.lux.pos.java.repository.CuponMvJava
import mx.lux.pos.java.repository.DescuentosClaveJava
import mx.lux.pos.java.repository.DescuentosJava
import mx.lux.pos.java.repository.DetalleNotaVentaJava
import mx.lux.pos.java.repository.EmpleadoJava
import mx.lux.pos.java.repository.ExamenJava
import mx.lux.pos.java.repository.FormaContactoJava
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.JbLlamadaJava
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.java.repository.PagoJava
import mx.lux.pos.java.repository.Parametros
import mx.lux.pos.java.repository.PedidoLcJava
import mx.lux.pos.java.repository.PreciosJava
import mx.lux.pos.java.repository.PromocionJava
import mx.lux.pos.java.repository.RecetaJava
import mx.lux.pos.java.repository.TmpServiciosJava
import mx.lux.pos.java.service.ArticulosServiceJava
import mx.lux.pos.java.service.CancelacionServiceJava
import mx.lux.pos.java.service.ClienteServiceJava
import mx.lux.pos.java.service.CotizaServiceJava
import mx.lux.pos.java.service.DetalleNotaVentaServiceJava
import mx.lux.pos.java.service.ExamenServiceJava
import mx.lux.pos.java.service.InventarioServiceJava
import mx.lux.pos.java.service.NotaVentaServiceJava
import mx.lux.pos.java.service.RecetaServiceJava
import mx.lux.pos.java.service.TicketServiceJava
import mx.lux.pos.model.*
import mx.lux.pos.java.querys.ArticulosQuery
import mx.lux.pos.java.querys.AutorizaMovQuery
import mx.lux.pos.java.querys.BancoEmisorQuery
import mx.lux.pos.java.querys.DetalleNotaVentaQuery
import mx.lux.pos.java.querys.NotaVentaQuery
import mx.lux.pos.java.querys.PagoQuery
import mx.lux.pos.java.querys.RecetaQuery
import mx.lux.pos.java.querys.TmpServiciosQuery
import mx.lux.pos.repository.*
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.*
import mx.lux.pos.service.business.Registry
import mx.lux.pos.service.impl.FormaContactoService
import mx.lux.pos.ui.MainWindow
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.view.dialog.AseguraNotaDialog
import mx.lux.pos.ui.view.dialog.ContactClientDialog
import mx.lux.pos.ui.view.dialog.ContactDialog
import mx.lux.pos.ui.view.dialog.ManualPriceDialog
import mx.lux.pos.ui.view.dialog.WarrantySelectionDialog
import mx.lux.pos.ui.view.panel.OrderPanel
import org.apache.commons.lang.NumberUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.swing.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@Slf4j
@Component
class OrderController {



    private static final Double ZERO_TOLERANCE = 0.0005
    private static final String TAG_GENERICO_A = 'A'
    private static final String TAG_GENERICO_B = 'B'
    private static final String TAG_GENERICO_C = 'C'
    private static final String TAG_CUPON = 'C'
    private static final String TAG_CUPON_SEGURO = 'C1'
    private static final String TAG_CANCELADA = 'T'
    private static final String TAG_GENERICOS_INVENTARIABLES = 'A,E,H'
    private static final String TAG_ARTICULO_NO_VIGENTE = 'C'
    private static final String DATE_FORMAT = 'dd-MM-yyyy'
    private static final String TAG_REUSO = 'R'
    private static final String TAG_MSJ_CUPON = 'DESCUENTO CUPON'
    private static final String TAG_MSJ_CUPON_LC = 'DESCUENTO CUPON LC'
    private static final String TAG_TIPO_DESCUENTO = 'M'
    private static final String TAG_GENERICO_SEG = 'J'
    private static final String TAG_ID_GARANTIA = "GR"
    private static final String TAG_GENERICO_SEGUROS = 'J'
    private static final String TAG_GENERICO_ARMAZON = 'A'
    private static final String TAG_GENERICO_LENTE = 'B'
    private static final String TAG_SEGUROS_ARMAZON = 'SS'
    private static final String TAG_SEGUROS_OFTALMICO = 'SEG'
    private static final String TAG_TIPO_OFTALMICO = 'O'
    private static final String TAG_TIPO_SOLAR = 'G'
    private static final String TAG_SUBTIPO_NINO = 'N'
    private static final String TAG_MONTAJE = 'MONTAJE'
    private static String MSJ_ERROR_WARRANTY = ""
    private static String TXT_ERROR_WARRANTY = ""
    private static final String TAG_CLAVE_DESCUENTO_EDAD = "PREDAD"
    private static final String TAG_TRANSACCION_VENTA = "VENTA"
    private static final String TAG_TRANSACCION_SALIDA = "SALIDA"
    private static final String TAG_TRANSACCION_CANCELACION = "DEVOLUCION"
    private static final String TAG_TRANSACCION_REM_SP = "ENTRADA_SP"
    private static final String TAG_TRANSACCION_S = "S"
    private static final String TAG_TRANSACCION_ENTRADA = "E"
    private static final String TAG_FORMA_CARGO_EMP = 'FE'
    private static final String TAG_FORMA_CARGO_MVIS = 'FM'

    private static Boolean insertSegKig

    private static List<Warranty> lstWarranty = new ArrayList<>()
    private static String idOrderEnsured

    private static NotaVentaService notaVentaService
    private static RecetaServiceJava recetaServiceJava
    private static NotaVentaServiceJava notaVentaServiceJava
    private static DetalleNotaVentaService detalleNotaVentaService
    private static DetalleNotaVentaServiceJava detalleNotaVentaServiceJava
    private static PagoService pagoService
    private static TicketService ticketService
    private static TicketServiceJava ticketServiceJava
    private static BancoService bancoService
    private static InventarioService inventarioService
    private static InventarioServiceJava inventarioServiceJava
    private static MonedaExtranjeraService fxService
    private static Boolean displayUsd
    private static PromotionService promotionService
    private static CancelacionService cancelacionService
    private static CancelacionServiceJava cancelacionServiceJava
    private static RecetaService recetaService
    private static ExamenService examenService
    private static ExamenServiceJava examenServiceJava
    private static ArticuloService articuloService
    private static ArticulosServiceJava articulosServiceJava
    private static CotizacionService cotizacionService
    private static CotizaServiceJava cotizacionServiceJava
    private static JbService jbService
    private static FormaContactoService formaContactoService
    private static JbRepository jbRepository
    private static JbTrackService jbTrackService
    private static JbLlamadaRepository jbLlamadaRepository
    private static ParametroRepository parametroRepository
    private static TmpServiciosRepository tmpServiciosRepository
    private static DescuentoClaveRepository descuentoClaveRepository
    private static GenericoRepository genericoRepository
    private static PrecioRepository precioRepository
    private static AcusesTipoRepository acusesTipoRepository
    private static AcuseRepository acuseRepository
    private static JbServiciosRepository jbServiciosRepository
    private static JbNotasRepository jbNotasRepository
    private static DescuentoRepository descuentoRepository
    private static CuponMvRepository cuponMvRepository
    private static NotaVentaRepository notaVentaRepository
    private static BancoDevRepository bancoDevRepository
    private static ClienteServiceJava clienteServiceJava
    private static TransInvRepository transInvRepository
    private static TransInvDetalleRepository transInvDetalleRepository
    private static final String TAG_USD = "USD"
    private static Integer numberQuote = 0

    @Autowired
    public OrderController(
            NotaVentaService notaVentaService,
            DetalleNotaVentaService detalleNotaVentaService,
            PagoService pagoService,
            TicketService ticketService,
            BancoService bancoService,
            InventarioService inventarioService,
            MonedaExtranjeraService monedaExtranjeraService,
            PromotionService promotionService,
            CancelacionService cancelacionService,
            RecetaService recetaService,
            ExamenService examenService,
            ArticuloService articuloService,
            JbRepository jbRepository,
            JbTrackService jbTrackService,
            JbService jbService,
            JbLlamadaRepository jbLlamadaRepository,
            ParametroRepository parametroRepository,
            TmpServiciosRepository tmpServiciosRepository,
            DescuentoClaveRepository descuentoClaveRepository,
            GenericoRepository genericoRepository,
            PrecioRepository precioRepository,
            AcusesTipoRepository acusesTipoRepository,
            AcuseRepository acuseRepository,
            JbServiciosRepository jbServiciosRepository,
            JbNotasRepository jbNotasRepository,
            DescuentoRepository descuentoRepository,
            CotizacionService cotizacionService,
            FormaContactoService formaContactoService,
            CuponMvRepository cuponMvRepository,
            NotaVentaRepository notaVentaRepository,
            BancoDevRepository bancoDevRepository,
            TransInvRepository transInvRepository,
            TransInvDetalleRepository transInvDetalleRepository

    ) {
        this.notaVentaService = notaVentaService
        this.detalleNotaVentaService = detalleNotaVentaService
        this.pagoService = pagoService
        this.ticketService = ticketService
        this.bancoService = bancoService
        this.inventarioService = inventarioService
        fxService = monedaExtranjeraService
        this.promotionService = promotionService
        this.cancelacionService = cancelacionService
        this.recetaService = recetaService
        this.articuloService = articuloService
        this.jbRepository = jbRepository
        this.jbTrackService = jbTrackService
        this.jbLlamadaRepository = jbLlamadaRepository
        this.parametroRepository = parametroRepository
        this.tmpServiciosRepository = tmpServiciosRepository
        this.descuentoClaveRepository = descuentoClaveRepository
        this.genericoRepository = genericoRepository
        this.precioRepository = precioRepository
        this.acusesTipoRepository = acusesTipoRepository
        this.acuseRepository = acuseRepository
        this.jbServiciosRepository = jbServiciosRepository
        this.jbNotasRepository = jbNotasRepository
        this.descuentoRepository = descuentoRepository
        this.examenService = examenService
        this.cotizacionService = cotizacionService
        this.jbService = jbService
        this.formaContactoService = formaContactoService
        this.cuponMvRepository = cuponMvRepository
        this.notaVentaRepository = notaVentaRepository
        this.bancoDevRepository = bancoDevRepository
        recetaServiceJava = new RecetaServiceJava()
        notaVentaServiceJava = new NotaVentaServiceJava()
        articulosServiceJava = new ArticulosServiceJava()
        inventarioServiceJava = new InventarioServiceJava()
        examenServiceJava = new ExamenServiceJava()
        cotizacionServiceJava = new CotizaServiceJava()
        ticketServiceJava = new TicketServiceJava()
        cancelacionServiceJava = new CancelacionServiceJava()
        detalleNotaVentaServiceJava = new DetalleNotaVentaServiceJava()
        clienteServiceJava = new ClienteServiceJava()
        this.transInvRepository = transInvRepository
        this.transInvDetalleRepository = transInvDetalleRepository
    }

    private static Boolean canceledWarranty
    private static String postEnsure

    static Order getOrder(String orderId) {
      log.info("obteniendo orden id: ${orderId}")
      //NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(orderId)
      NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById( orderId )
      Order order = Order.toOrder(notaVenta)
      if (StringUtils.isNotBlank(order?.id)) {
        order.items?.clear()
        //List<DetalleNotaVenta> detalles = detalleNotaVentaService.listarDetallesNotaVentaPorIdFactura(orderId)
        List<DetalleNotaVentaJava> detalles = notaVenta.detalles.size() > 0 ? notaVenta.detalles : DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFactura(orderId)
        detalles?.each { DetalleNotaVentaJava tmp ->
          order.items?.add(OrderItem.toOrderItem(tmp))
          order.due
        }
        order.payments?.clear()
        //List<Pago> pagos = pagoService.listarPagosPorIdFactura(orderId)
        List<PagoJava> pagos = notaVenta.pagos.size() > 0 ? notaVenta.pagos : PagoQuery.busquedaPagosPorIdFactura(orderId)
        pagos?.each { PagoJava tmp ->
          Payment paymentTmp = Payment.toPaymment(tmp)
          if (tmp?.idBancoEmi?.integer) {
            //BancoEmisor banco = bancoService.obtenerBancoEmisor(tmp?.idBancoEmi?.toInteger())
            BancoEmisorJava banco = BancoEmisorQuery.BuscaBancoEmisorPorId(tmp?.idBancoEmi?.toInteger())
            paymentTmp.issuerBank = banco?.descripcion
          }
          order.payments?.add(paymentTmp)
        }
        return order
      } else {
        log.warn('no se obtiene orden, notaVenta no existe')
      }
      return null
    }

    static Order openOrder(String clienteID, String empID) {
      log.info('abriendo nueva orden')
      NotaVentaJava notaVenta = notaVentaServiceJava.abrirNotaVenta(clienteID, empID)
      return Order.toOrder(notaVenta)
    }

    static Item findArt(String dioptra) {

        Articulo art = articuloService.findbyName(dioptra)

        return Item.toItem(art)
    }

    static RecetaJava findRx(Order order, Customer customer) {
        //NotaVenta rxNotaVenta = notaVentaService.obtenerNotaVenta(order?.id)
        NotaVentaJava rxNotaVenta = NotaVentaQuery.busquedaNotaById( order.id )
        List<Rx> recetas = CustomerController.findAllPrescriptions(customer?.id)
        RecetaJava receta = new RecetaJava()
        Iterator iterator = recetas.iterator();
        while (iterator.hasNext()) {
            Rx rx = iterator.next()
            if (rxNotaVenta.receta == rx?.id) {
                rxNotaVenta.receta
                //receta = recetaService.findbyId(rxNotaVenta.receta)
                receta = RecetaQuery.buscaRecetaPorIdReceta( rxNotaVenta.receta )
            }
        }
        return receta
    }

    static void savePago(Pago pago) {
        pagoService.actualizarPago(pago)
    }

    static Integer reciboSeq() {
        return pagoService.reciboSeq().toInteger()
    }

    static List<Pago> findPagos(String IdFactura) {

        List<Pago> pagos = pagoService.listarPagosPorIdFactura(IdFactura)

        return pagos
    }

    static void savePromisedDate(String idNotaVenta, Date fechaPrometida) {
      NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(idNotaVenta)
      notaVentaServiceJava.saveProDate(notaVenta, fechaPrometida)
    }

    static void saveRxOrder(String idNotaVenta, Integer receta) {
      log.debug( "guardando receta ${receta}" )
      //NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(idNotaVenta)
      NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById( idNotaVenta )
      //notaVentaService.saveRx(notaVenta, receta)
      if( receta != null ){
        recetaServiceJava.saveRx( notaVenta, receta )
      }
    }

  static Order saveFrame(String idNotaVenta, String opciones, String forma) {
    NotaVentaJava notaVenta = notaVentaServiceJava.saveFrame(idNotaVenta, opciones, forma)
    return Order.toOrder(notaVenta)
  }

    static Dioptra addDioptra(Order order, String dioptra) {
      log.debug( "addDioptra( )" )
      //NotaVenta nota = notaVentaService.obtenerNotaVenta(order.id)
      NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(order.id)
      nota.setCodigoLente(dioptra)
      //nota = notaVentaService.registrarNotaVenta(nota)
      nota = notaVentaServiceJava.registrarNotaVenta( nota )
      Dioptra diop = generaDioptra(preDioptra(nota.codigoLente))
      println('Codigo Lente: ' + nota.codigoLente)
      return diop
    }

    static String preDioptra(String dioString) {
        String preDioptra
        //try{
        if (!dioString.equals(null) && dioString.length() >= 7) {
            preDioptra = dioString.substring(0, 1) + ',' +
                    dioString.substring(1, 2) + ',' +
                    dioString.substring(2, 3) + ',' +
                    dioString.substring(3, 5) + ',' +
                    dioString.substring(5, 6) + ',' +
                    dioString.substring(6, 7)
        } else {
            preDioptra = dioString
        }
        //}catch(e){}
        return preDioptra
    }

    static Dioptra generaDioptra(String dioString) {
        Dioptra nuevoDioptra = new Dioptra()
        if (StringUtils.trimToEmpty(dioString).length() <= 0) {
        } else {
            ArrayList<String> caract = new ArrayList<String>()
            String s = dioString
            StringTokenizer st = new StringTokenizer(s.trim(), ",")
            Iterator its = st.iterator()
            while (its.hasNext()) {
                caract.add(its.next().toString())
            }
            nuevoDioptra = new Dioptra(caract.get(0).toString(), caract.get(1).toString(), caract.get(2).toString(), caract.get(3).toString(), caract.get(4).toString(), caract.get(5).toString())
        }
        return nuevoDioptra
    }

    static Order addItemToOrder(Order order, Item item, String surte) {
      String orderId = order?.id
      String clienteID = order.customer?.id
      String empleadoID = order?.employee
      log.info("agregando articulo id: ${item?.id} a orden id: ${orderId}")
      if (item?.id) {
        orderId = (NotaVentaQuery.busquedaNotaById(orderId) ? orderId : openOrder(clienteID, empleadoID)?.id)
        NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(orderId)
        DetalleNotaVentaJava detalle = null
        if (item.isManualPriceItem()) {
          String rmks = nota.observacionesNv+nota.observacionesNv.trim().length() <= 0 ? order.comments : ''
          ManualPriceDialog dlg = ManualPriceDialog.instance
          dlg.item = item
          dlg.remarks = rmks
          dlg.activate()
          if (dlg.itemAccepted) {
            item.listPrice = item.price
            detalle = new DetalleNotaVentaJava(
                    idArticulo: item.id,
                    cantidadFac: 1,
                    precioUnitLista: item.listPrice,
                    precioUnitFinal: item.price,
                    precioCalcLista: item.listPrice,
                    precioFactura: item.price,
                    precioCalcOferta: 0,
                    precioConv: 0,
                    idTipoDetalle: 'N',
                    surte: StringUtils.trimToEmpty(item?.type).equalsIgnoreCase('B') ? 'P' : surte,
            )
            nota.observacionesNv = dlg.remarks
            notaVentaServiceJava.registrarNotaVenta(nota)
          }
        } else {
          if(!TAG_GENERICOS_INVENTARIABLES.contains(item.type)){
            surte = ' '
          }
          detalle = new DetalleNotaVentaJava(
                  idArticulo: item.id,
                  cantidadFac: 1,
                  precioUnitLista: item.listPrice,
                  precioUnitFinal: item.price,
                  precioCalcLista: item.listPrice,
                  precioFactura: item.price,
                  precioCalcOferta: 0,
                  precioConv: 0,
                  idTipoDetalle: 'N',
                  surte: surte
           )
        }
        if (detalle != null) {
          nota = notaVentaServiceJava.registrarDetalleNotaVentaEnNotaVenta(orderId, detalle)
        }
        if (nota != null ) {
          notaVentaServiceJava.registraImpuestoPorFactura( nota )
        }
        nota.observacionesNv = nota.observacionesNv.trim().length() <= 0 ? order.comments : ''
        return Order.toOrder(nota)
      } else {
        log.warn("no se agrega articulo, parametros invalidos")
      }
      return null
    }

    static Order addOrderItemToOrder(String orderId, OrderItem orderItem, String surte, String batch) {
        log.info("actualizando orderItem id: ${orderItem?.item?.id} en orden id: ${orderId}")
        if (StringUtils.isNotBlank(orderId) && orderItem?.item?.id) {
            DetalleNotaVenta detalle = new DetalleNotaVenta(
                    idArticulo: orderItem.item.id,
                    cantidadFac: orderItem.quantity ?: 1,
                    precioUnitLista: orderItem.item.listPrice,
                    precioUnitFinal: orderItem.item.price,
                    precioCalcLista: orderItem.item.listPrice,
                    precioFactura: orderItem.item.price,
                    precioCalcOferta: 0,
                    precioConv: 0,
                    idTipoDetalle: 'N',
                    surte: surte,
                    idRepVenta: batch
            )
            NotaVenta notaVenta = notaVentaService.registrarDetalleNotaVentaEnNotaVenta(orderId, detalle)
            return Order.toOrder(notaVenta)
        } else {
            log.warn("no se actualiza articulo, parametros invalidos")
        }
        return null
    }


    static String codigoDioptra(Dioptra codDioptra) {
        String codigo
        if (!codDioptra.equals(null)) {
            codigo = codDioptra.material + codDioptra.lente + codDioptra.tipo + codDioptra.especial + codDioptra.tratamiento + codDioptra.color
        } else {
            codigo = null
        }
        return codigo
    }

    static Order removeOrderItemFromOrder(String orderId, OrderItem orderItem) {
      log.info("eliminando orderItem, articulo id: ${orderItem?.item?.id} de orden id: ${orderId}")
      if (StringUtils.isNotBlank(orderId) && orderItem?.item?.id) {
        NotaVentaJava notaVenta = notaVentaServiceJava.eliminarDetalleNotaVentaEnNotaVenta(orderId, orderItem.item.id)
        if (notaVenta?.idFactura) {
          NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(orderId)
          Order o = new Order()
          ArticulosJava i = ArticulosQuery.busquedaArticuloPorId(orderItem?.item?.id?.toInteger())
          if (!i?.indiceDioptra?.equals(null)) {
            if(StringUtils.trimToEmpty(i?.idGenerico).equalsIgnoreCase(TAG_GENERICO_B)){
              nota.receta = null
              NotaVentaQuery.updateNotaVenta( nota )
            }
            if( StringUtils.trimToEmpty(i.indiceDioptra).length() > 0 ){
              Dioptra actDioptra = validaDioptra(generaDioptra(preDioptra(nota.codigoLente)), generaDioptra(i.indiceDioptra))
              o = Order.toOrder(notaVenta)
              actDioptra = addDioptra(o, codigoDioptra(actDioptra))
            } else {
              o = Order.toOrder(notaVenta)
            }
          }
          return o
        } else {
          log.warn("no se elimina orderItem, notaVenta no existe")
        }
      } else {
        log.warn("no se elimina orderItem, parametros invalidos")
      }
      return null
    }


    static Dioptra validaDioptra(Dioptra dioptra, Dioptra nuevoDioptra) {

        if (dioptra.getMaterial().toString().equals('@') || dioptra?.material == null || (dioptra.getMaterial().toString().equals('C') && !nuevoDioptra.getMaterial().toString().equals('@')) /* || (!dioptra.getMaterial().toString().trim().equals('C') && !nuevoDioptra.getMaterial().toString().trim().equals('@')) */ ) {
            if (dioptra.getMaterial().toString().trim().equals(nuevoDioptra.getMaterial().toString().trim())) {
                dioptra.setMaterial('C')
            } else {
                dioptra.setMaterial(nuevoDioptra.getMaterial())
            }
        }
        if (dioptra.getLente().toString().equals('@') || dioptra?.lente == null || !nuevoDioptra?.getLente().toString().equals('@')) {
            if (dioptra.getLente().toString().trim().equals(nuevoDioptra.getLente().toString().trim())) {
                dioptra.setLente('@')
            } else {
                dioptra.setLente(nuevoDioptra.getLente())
            }
        }
        if (dioptra.getTipo().toString().equals('@') || dioptra?.tipo == null || (dioptra.getTipo().toString().equals('N') && !nuevoDioptra.getTipo().toString().equals('@'))/* || (!dioptra.getTipo().toString().trim().equals('N') && !nuevoDioptra.getTipo().toString().trim().equals('@'))*/) {
            if (dioptra.getTipo().toString().trim().equals(nuevoDioptra.getTipo().toString().trim())) {
                dioptra.setTipo('N')
            } else {
                dioptra.setTipo(nuevoDioptra.getTipo())
            }
        }
        if (dioptra.getEspecial().toString().equals('@@') || dioptra?.especial == null || (dioptra.getEspecial().toString().equals('BL') && !nuevoDioptra.getEspecial().toString().equals('@@')) /* || (!dioptra.getEspecial().toString().trim().equals('BL') && !nuevoDioptra.getEspecial().toString().trim().equals('@@'))*/) {
            if (dioptra.getEspecial().toString().trim().equals(nuevoDioptra.getEspecial().toString().trim())) {
                dioptra.setEspecial('BL')
            } else {
                dioptra.setEspecial(nuevoDioptra.getEspecial())
            }
        }
        if (dioptra.getTratamiento().toString().equals('@') || dioptra?.tratamiento == null || (dioptra.getTratamiento().toString().equals('B') && !nuevoDioptra.getTratamiento().toString().equals('@')) /* || (!dioptra.getTratamiento().toString().trim().equals('B') && !nuevoDioptra.getTratamiento().toString().trim().equals('@'))*/) {
            if (dioptra.getTratamiento().toString().trim().equals(nuevoDioptra.getTratamiento().toString().trim())) {
                dioptra.setTratamiento('B')
            } else {
                dioptra.setTratamiento(nuevoDioptra.getTratamiento())
            }
        }
        if (dioptra.getColor().toString().trim().equals('@') || dioptra?.color == null || (dioptra.getColor().toString().trim().equals('B') && !nuevoDioptra.getColor().toString().trim().equals('@')) /* || (!dioptra.getColor().toString().trim().equals('B') && !nuevoDioptra.getColor().toString().trim().equals('@'))*/) {

            if (dioptra.getColor().toString().trim().equals(nuevoDioptra.getColor().toString().trim())) {
                dioptra.setColor('B')
            } else {
                dioptra.setColor(nuevoDioptra.getColor())
            }
        }
        return dioptra
    }


    static Pago addPaymentToOrder(String orderId, Payment payment) {
        log.info("agregando pago monto: ${payment?.amount}, tipo: ${payment?.paymentTypeId} a orden id: ${orderId}")
        if (StringUtils.isNotBlank(orderId) && StringUtils.isNotBlank(payment?.paymentTypeId) && payment?.amount) {

            User user = Session.get(SessionItem.USER) as User

            Pago pago = new Pago(
                    idFormaPago: payment.paymentTypeId,
                    referenciaPago: payment.paymentReference,
                    monto: payment.amount,
                    idEmpleado: user?.username,
                    idFPago: payment.paymentTypeId,
                    clave: payment.paymentReference,
                    referenciaClave: payment.codeReference,
                    idBancoEmisor: payment.issuerBankId,
                    idTerminal: payment.terminalId,
                    idPlan: payment.planId
            )
            Pago newPago = notaVentaService.registrarPagoEnNotaVenta(orderId, pago)
            return newPago
        } else {
            log.warn("no se agrega pago, parametros invalidos")
        }
        return null
    }

    static Order removePaymentFromOrder(String orderId, Payment payment) {
        log.info("eliminando pago id: ${payment?.id}, monto: ${payment?.amount}, tipo: ${payment?.paymentTypeId}")
        log.info("de orden id: ${orderId}")
        if (StringUtils.isNotBlank(orderId) && payment?.id) {
            cancelacionService.restablecerMontoAlBorrarPago(payment.id)
            NotaVenta notaVenta = notaVentaService.eliminarPagoEnNotaVenta(orderId, payment.id)
            if (notaVenta?.id) {
                return Order.toOrder(notaVenta)
            } else {
                log.warn("no se elimina pago, notaVenta no existe")
            }
        } else {
            log.warn("no se elimina pago, parametros invalidos")
        }
        return null
    }



    static void entregaInstante(Order order) {
        log.info("registrando orden id: ${order?.id}, cliente: ${order?.customer?.id}")
        if (StringUtils.isNotBlank(order?.id) && order?.customer?.id) {
            NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(order.id)
            if (StringUtils.isNotBlank(notaVenta?.id)) {
                User user = Session.get(SessionItem.USER) as User
                notaVenta?.empEntrego = user?.username
                notaVenta?.fechaEntrega = new Date()
                notaVenta?.horaEntrega = new Date()

                notaVentaService.saveOrder(notaVenta)
            }
        }
    }

    static Order placeOrder(Order order, String idEmpleado, Boolean isMultypayment) {
      log.info("registrando orden id: ${order?.id}, cliente: ${order?.customer?.id}")
      if (StringUtils.isNotBlank(order?.id) && order?.customer?.id) {
        NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(order.id)
        if (StringUtils.isNotBlank(notaVenta?.idFactura)) {
          if (StringUtils.trimToEmpty(notaVenta.idEmpleado).length() <= 0) {
            notaVenta.idEmpleado = idEmpleado
          }
          if (notaVenta.idCliente != null) {
            notaVenta.idCliente = order.customer.id
          }
          if( StringUtils.trimToNull(order.dioptra) == null ){
            notaVenta.codigoLente = null
          }
          notaVenta.observacionesNv = order.comments
          notaVenta.udf4 = isMultypayment ? "M" : ""
          notaVenta = notaVentaServiceJava.cerrarNotaVenta(notaVenta)
          if (inventarioServiceJava.solicitarTransaccionVenta(notaVenta)) {
            log.debug("transaccion de inventario correcta")
            if( inventarioServiceJava.solicitarTransaccionEntradaSP(notaVenta) ){
              log.debug("transaccion entrada SP correcta")
              inventarioServiceJava.insertarRegistroRemesa( notaVenta )
            }
          } else {
                    log.warn("no se pudo procesar la transaccion de inventario")
          }
          ServiceManager.ioServices.logSalesNotification(notaVenta.idFactura)
          return Order.toOrder(notaVenta)
        } else {
          log.warn("no se registra orden, notaVenta no existe")
        }
      } else {
        log.warn("no se registra orden, parametros invalidos")
      }
        return null
    }

    static void fieldRX(String orderId) {
        if (StringUtils.isNotBlank(orderId)) {
            recetaService.generaAcuse(orderId)
        } else {
            log.warn("no se imprime receta, parametros invalidos")
        }

    }

    static void printPaid(String orderId, Integer pagoId) {
        if (StringUtils.isNotBlank(orderId)) {
            ticketService.imprimePago(orderId, pagoId)
        } else {
            log.warn("no se imprime pago, parametros invalidos")
        }
    }

    static Order notaVentaxRx(Integer rx){
        return Order.toOrder(notaVentaService.notaVentaxRx(rx))
    }

    static void printRx(String orderId, Boolean reimpresion) {
        log.info("imprimiendo receta id: ")
        if (StringUtils.isNotBlank(orderId)) {
            ticketService.imprimeRx(orderId, reimpresion)
        } else {
            log.warn("no se imprime receta, parametros invalidos")

        }
    }


    static Jb entraJb(String rx) {
        return jbRepository.findOne(rx)
    }

  static void insertaEntrega(Order order, Boolean entregaInstante) {
    println('Order ID: ' + order?.id)
    Boolean alreadyDelivered = false
    NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(order?.id)
    User user = Session.get(SessionItem.USER) as User
    notaVenta.setEmpEntrego(user?.username)
    notaVenta.setHoraEntrega(new Date())
    if (notaVenta?.fechaEntrega == null) {
      notaVenta.setFechaEntrega(new Date())
    } else {
      alreadyDelivered = true
    }
    println('Factura: ' + notaVenta?.getFactura())
    String idFactura = notaVenta.getFactura()
    notaVentaServiceJava.saveOrder(notaVenta)
    Boolean hasRedEnsure = false
    for(PagoJava pago : notaVenta.pagos){
      if( StringUtils.trimToEmpty(pago.eTipoPago.idPago).equalsIgnoreCase(TAG_CUPON_SEGURO) ){
        hasRedEnsure = true
      }
    }
    Boolean validPayments = true
    for( NotaVentaJava nv : notaVenta ){
      for(PagoJava pay : nv.pagos){
        if(TAG_FORMA_CARGO_EMP.equalsIgnoreCase(StringUtils.trimToEmpty(pay.idFPago)) ||
                TAG_FORMA_CARGO_MVIS.equalsIgnoreCase(StringUtils.trimToEmpty(pay.idFPago))){
          validPayments = false
        }
      }
    }
    notaVentaServiceJava.saveOrder(notaVenta)
    if( notaVenta.fechaEntrega != null ){
          if( Registry.isCouponFFActivated() && !alreadyDelivered ){
            if( !Registry.couponFFOtherDiscount() ){
              Boolean hasNotDiscount = true
              if( notaVenta.descuentosJava != null ){
                if( !StringUtils.trimToEmpty(notaVenta.descuentosJava.clave).equalsIgnoreCase("PREDAD") ){
                  hasNotDiscount = false
                }
              }
              if( notaVenta.ordenPromDet.size() <= 0 && hasNotDiscount && !hasRedEnsure && validPayments){
                generateCouponFAndF( StringUtils.trimToEmpty( order.id ) )
              }
            } else if(!hasRedEnsure && validPayments){
              generateCouponFAndF( StringUtils.trimToEmpty( order.id ) )
            }
          }
        }
          Boolean orderToday = StringUtils.trimToEmpty(notaVenta.fechaHoraFactura.format("dd/MM/yyyy")).equalsIgnoreCase(StringUtils.trimToEmpty(new Date().format("dd/MM/yyyy")))
          Boolean validDateEnsure = orderToday ? true : validEnsureDateAplication(notaVenta)
        if( !alreadyDelivered ){
          if( validDateEnsure ){
            if( validWarranty( notaVenta, false, null, "", false ) ){
              Boolean doubleEnsure = lstWarranty.size() > 1 ? true : false
              for(Warranty warranty : lstWarranty){
                String idFac = StringUtils.trimToEmpty(idOrderEnsured).length() > 0 ? StringUtils.trimToEmpty(idOrderEnsured) : notaVenta.idFactura
                ItemController.printWarranty( warranty.amount, warranty.idItem, warranty.typeEnsure, idFac, doubleEnsure )
              }
              idOrderEnsured = ""
              lstWarranty.clear()
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
            postEnsure = ""
        } else {
            lstWarranty.clear()
            Boolean validWarranty = false
            Warranty warranty = new Warranty()
            warranty.idItem = ""
            warranty.amount = BigDecimal.ZERO
            for(DetalleNotaVentaJava det : notaVenta.detalles){
              if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                validWarranty = true
                if( StringUtils.trimToEmpty(det.articulo.articulo).startsWith(TAG_SEGUROS_OFTALMICO) ){
                  warranty.typeEnsure = "L"
                } else if( StringUtils.trimToEmpty(det.articulo.articulo).startsWith(TAG_SEGUROS_ARMAZON) ){
                  warranty.typeEnsure = "S"
                } else if( StringUtils.trimToEmpty(det.articulo.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) ){
                  warranty.typeEnsure = "N"
                }
              } else {
                warranty.amount = warranty.amount.add(det.precioUnitFinal)
                warranty.idItem = warranty.idItem+","+StringUtils.trimToEmpty(det.articulo.articulo)
                warranty.idOrder = ""
              }
            }
            warranty.idItem = warranty.idItem.replaceFirst(",","")
            if( warranty.amount.compareTo(BigDecimal.ZERO) > 0 && StringUtils.trimToEmpty(warranty.typeEnsure).length() > 0 ){
              lstWarranty.add( warranty )
            }
            if( lstWarranty.size() > 0 ){
              Boolean doubleEnsure = lstWarranty.size() > 1 ? true : false
              for(Warranty warranty1 : lstWarranty){
                ItemController.printWarranty( warranty1.amount, warranty1.idItem, warranty1.typeEnsure, StringUtils.trimToEmpty(notaVenta.idFactura), doubleEnsure )
              }
              lstWarranty.clear()
            }
          }

        }
    if (!entregaInstante) {
      Jb trabajo = JbQuery.getJbRxSimple(idFactura)
      if( trabajo == null ){
        idFactura = idFactura.replaceFirst("^0*", "")
        trabajo = JbQuery.getJbRxSimple(idFactura)
      }
      if( trabajo != null && !trabajo.estado.equalsIgnoreCase('TE')){
        trabajo.setEstado('TE')
        JbQuery.updateEstadoJbRx(idFactura, trabajo.estado)
      }
      mx.lux.pos.java.repository.JbTrack jbTrack = new mx.lux.pos.java.repository.JbTrack()
      String bill = order?.bill.replaceFirst("^0*", "")
      jbTrack?.rx = bill
      jbTrack?.estado = 'TE'
      jbTrack?.emp = user?.username
      jbTrack?.fecha = new Date()
      jbTrack?.idMod = '0'
      jbTrack?.idViaje = null
      jbTrack?.obs = user?.username

      JbTrackQuery.insertJbTrack(jbTrack)
      JbTrackQuery.insertJbTrack(jbTrack)
      JbQuery.eliminaJbLLamada(order?.bill)
      cancelacionServiceJava.actualizaGrupo( notaVenta.idFactura, 'E' )
    }
  }

    static void printOrder(String orderId) {
        printOrder(orderId, true)
    }

    static void printOrder(String orderId, boolean pNewOrder) {
        log.info("imprimiendo orden id: ${orderId}")
        if (StringUtils.isNotBlank(orderId)) {
            ticketService.imprimeVenta(orderId, pNewOrder)
        } else {
            log.warn("no se imprime orden, parametros invalidos")
        }
    }

    static List<Order> findLastOrders() {
        log.info("obteniendo ultimas ordenes")
        List<NotaVenta> results = notaVentaService.listarUltimasNotasVenta()
        return results?.collect { NotaVenta tmp ->
            Order.toOrder(tmp)
        }
    }

    static List<Order> findOrdersByParameters(Map<String, Object> params) {
        log.info("buscando ordenes por parametros: ${params}")
        List<NotaVenta> results = notaVentaService.listarNotasVentaPorParametros(params)
        log.debug("ordenes obtenidas: ${results*.id}")
        return results.collect { NotaVenta tmp ->
            Order.toOrder(tmp)
        }
    }

    static Order findOrderByTicket(String ticket) {
        log.info("buscando orden por ticket: ${ticket}")
        String[] ticketTmp = ticket.split('-')
        NotaVenta result = notaVentaService.obtenerNotaVentaPorTicket(ticket)
        if( result == null ){
            ticket = ticketTmp[0]+"-"+String.format("%06d",Integer.parseInt(ticketTmp[1]))
            result = notaVentaService.obtenerNotaVentaPorTicket(ticket)
        }
        return Order.toOrder(result)
    }

    static Order findOrderByTicketJava(String factura) {
      log.info("buscando orden por ticket: ${factura}")
      NotaVentaJava result = NotaVentaQuery.busquedaNotaByFactura(factura)
      if( result == null ){
        factura = String.format("%06d",Integer.parseInt(factura))
        result = NotaVentaQuery.busquedaNotaByFactura(factura)
      }
      return Order.toOrder(result)
    }

    static Order findOrderByIdOrder(String idOrder) {
        log.info("buscando orden por ticket: ${idOrder}")
        NotaVenta result = notaVentaService.obtenerNotaVenta(idOrder)
        return Order.toOrder(result)
    }

    static Double requestUsdRate() {
        /*log.info("Request USD rate")
        Double rate = 1.0
        MonedaDetalle fxrate = fxService.findActiveRate(TAG_USD)
        if (fxrate != null) {
            rate = fxrate.tipoCambio.doubleValue()
        }*/
        return 0.0//rate
    }

    static Boolean requestUsdDisplayed() {
        /*if (displayUsd == null) {
            log.info("Request USD rate")
            displayUsd = fxService.requestUsdDisplayed()
        }*/
        return false//displayUsd
    }

  static SalesWithNoInventory requestConfigSalesWithNoInventory() {
    return notaVentaServiceJava.obtenerConfigParaVentasSinInventario()
  }

    static DetalleNotaVenta getDetalleNotaVenta(String idFactura, Integer idArticulo) {
        log.debug("getDetalleNotaVenta( String idFactura, Integer idArticulo )")

        DetalleNotaVenta venta = detalleNotaVentaService.obtenerDetalleNotaVenta(idFactura, idArticulo)

        return venta
    }

    static Promocion getPromocion(Integer idPromocion) {
        log.debug("getPromocion( Integer idPromocion )")
        Promocion promocion = promotionService.obtenerPromocion(idPromocion)
        return promocion
    }

    static void requestSaveAsQuote(Order pOrder, Customer pCustomer) {
        Integer pQuoteId = ServiceManager.quote.copyFromOrder(pOrder.id, pCustomer.id,
                ((User) Session.get(SessionItem.USER)).username)
        if (pQuoteId != null) {
            ticketService.imprimeCotizacion(pQuoteId, pOrder.id )
            notaVentaService.eliminarNotaVenta(pOrder.id)
            String msg = String.format('La cotización fue registrada como: %d    ', pQuoteId)
            JOptionPane.showMessageDialog(MainWindow.instance, msg, 'Cotización', JOptionPane.INFORMATION_MESSAGE)
        }
    }

    static String requestOrderFromQuote(JPanel pComponent) {
        String orderNbr = null
        String confirm = JOptionPane.showInputDialog(MainWindow.instance, OrderPanel.MSG_INPUT_QUOTE_ID,
                OrderPanel.TXT_QUOTE_TITLE, JOptionPane.QUESTION_MESSAGE)
        if (StringUtils.trimToNull(confirm) != null) {
            Integer quoteNbr = NumberUtils.createInteger(StringUtils.trimToEmpty(confirm))
            numberQuote = quoteNbr
            if (quoteNbr != null) {
                Map<String, Object> result = ServiceManager.quote.toOrder(quoteNbr)
                if (result != null) {
                    orderNbr = StringUtils.trimToNull((String) result.get('orderNbr'))
                    Cotizacion cotizacion = cotizacionService.obtenerCotizacion( quoteNbr )
                    Boolean invalidItem = false
                    String invalidArticle = ''
                    String nvItems = ''
                    if( cotizacion != null ){
                      NotaVenta nota = notaVentaService.obtenerNotaVenta( orderNbr )
                      if(nota != null){
                        if( cotizacion.cotizaDet.size() > nota.detalles.size() ){
                          invalidItem = true
                          for(DetalleNotaVenta det : nota.detalles){
                            nvItems = nvItems+','+det.articulo.articulo
                          }
                          for(CotizaDet cot : cotizacion.cotizaDet){
                            if(!nvItems.contains( cot.articulo )){
                              invalidArticle = invalidArticle+","+cot.articulo
                            }
                          }
                        }
                      }
                    }
                    if(invalidItem){
                        invalidArticle = invalidArticle.replaceFirst( ",","" )
                        JOptionPane.showMessageDialog(MainWindow.instance, "Articulo(s) '${invalidArticle}' no vigente(s)",
                                "Articulo(s) Invalido(s)", JOptionPane.DEFAULT_OPTION)
                    }
                }
                if (orderNbr == null) {
                    JOptionPane.showMessageDialog(MainWindow.instance, (String) result.get('statusMessage'),
                            OrderPanel.TXT_QUOTE_TITLE, JOptionPane.ERROR_MESSAGE)
                }
            }
        }
        return orderNbr
    }

    static Integer getNumberQuote(){
        return numberQuote
    }

    static String requestEmployee(String pOrderId) {
      String empName = ''
      if (StringUtils.trimToNull(StringUtils.trimToEmpty(pOrderId)) != null) {
        //Empleado employee = notaVentaService.obtenerEmpleadoDeNotaVenta(pOrderId)
        EmpleadoJava employee = notaVentaServiceJava.obtenerEmpleadoDeNotaVenta(pOrderId)
        if (employee != null) {
          if (((User) Session.get(SessionItem.USER)).equals(employee)) {
            empName = ((User) Session.get(SessionItem.USER)).toString()
          } else {
            empName = User.toUser(employee).toString()
          }
        }
      }
      return empName
    }

    static void saveCustomerForOrder(String pOrderNbr, Integer pCustomerId) {
        if (StringUtils.isNotBlank(pOrderNbr)) {
            NotaVenta order = notaVentaService.obtenerNotaVenta(pOrderNbr)
            if (order != null) {
                order.idCliente = pCustomerId
                notaVentaService.saveOrder(order)
            }
        }
    }

    static Customer getCustomerFromOrder(String pOrderNbr) {
        Customer cust = null
        if (StringUtils.trimToNull(pOrderNbr) != null) {
            NotaVenta order = notaVentaService.obtenerNotaVenta(pOrderNbr)
            if (order != null) {
                cust = Customer.toCustomer(order.cliente)
            }
        }
        return cust
    }

    static Order saveOrder(Order order) {
        log.info("registrando orden id: ${order?.id}, cliente: ${order?.customer?.id}")
        if (StringUtils.isNotBlank(order?.id) && order?.customer?.id) {
            NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(order.id)
            if (StringUtils.isNotBlank(notaVenta?.id)) {
                User user = Session.get(SessionItem.USER) as User
                if (StringUtils.isBlank(notaVenta.idEmpleado)) {
                    notaVenta.idEmpleado = user?.username
                }
                if (notaVenta.idCliente != null) {
                    notaVenta.idCliente = order.customer.id
                }
                notaVenta.codigo_lente = order?.dioptra
                notaVenta.observacionesNv = order.comments
                notaVenta = notaVentaService.registrarNotaVenta(notaVenta)
                return Order.toOrder(notaVenta)
            } else {
                log.warn("no se registra orden, notaVenta no existe")
            }
        } else {
            log.warn("no se registra orden, parametros invalidos")
        }
        return null
    }



    static Order saveOrderJava(Order order) {
      log.info("registrando orden id: ${order?.id}, cliente: ${order?.customer?.id}")
      if (StringUtils.isNotBlank(order?.id) && order?.customer?.id) {
        NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(order.id)
        if (StringUtils.isNotBlank(notaVenta?.idFactura)) {
          User user = Session.get(SessionItem.USER) as User
          if (StringUtils.isBlank(notaVenta.idEmpleado)) {
            notaVenta.idEmpleado = user?.username
          }
          if (notaVenta.idCliente != null) {
            notaVenta.idCliente = order.customer.id
          }
          notaVenta.codigoLente = order?.dioptra
          notaVenta.observacionesNv = order.comments
          notaVenta = notaVentaServiceJava.registrarNotaVenta(notaVenta)
          return Order.toOrder(notaVenta)
        } else {
          log.warn("no se registra orden, notaVenta no existe")
        }
      } else {
        log.warn("no se registra orden, parametros invalidos")
      }
      return null
    }



    static void notifyAlert(String pTitle, String pMessage) {
        JOptionPane.showMessageDialog(MainWindow.instance, pMessage, pTitle, JOptionPane.ERROR_MESSAGE)
    }

    static void notify(String pTitle, String pMessage) {
        JOptionPane.showMessageDialog(MainWindow.instance, pMessage, pTitle, JOptionPane.INFORMATION_MESSAGE)
    }

    static Boolean isPaymentPolicyFulfilled(Order pOrder) {


        Boolean result = true
        if (pOrder.due < 0) {
            this.notifyAlert(OrderPanel.TXT_INVALID_PAYMENT_TITLE, 'Los pagos no deben ser mayores al total de la venta.')
            result = false
        } else if (pOrder.containsOphtalmic()) {

            /*
 if ( pOrder.advancePct < ( SettingsController.instance.advancePct - ZERO_TOLERANCE ) ) {
   this.notifyAlert( OrderPanel.TXT_INVALID_PAYMENT_TITLE, 'Pago menor al %Anticipo establecido.' )
   result = false
 }
          */
        } else if (pOrder.due > 0) {
            this.notifyAlert(OrderPanel.TXT_INVALID_PAYMENT_TITLE, 'Se debe cubrir el total del saldo.')
            result = false
        }
        return result
    }

    static void requestNextOrderFromCustomer(Customer pCustomer, CustomerListener pListener) {
        NotaVenta dbOrder = notaVentaService.obtenerSiguienteNotaVentaDeCliente(pCustomer.id)
        if (dbOrder != null) {
            Order o = Order.toOrder(dbOrder)
            pListener.disableUI()
            pListener.operationTypeSelected = OperationType.PAYING
            pListener.setCustomer(pCustomer)
            pListener.setOrder(o)
            pListener.enableUI()
        }
    }


    static Boolean validaEntrega(String idFactura, String idSucursal, Boolean entregaInstante) {
      String ticket = idSucursal + '-' + idFactura
      Boolean registro = true
      NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaByFactura(idFactura)
      if(notaVenta == null){
        if(idFactura.length()< 6){
          idFactura = String.format( "%06d", Integer.parseInt(idFactura) )
          ticket = idSucursal + '-' + idFactura
          notaVenta = NotaVentaQuery.busquedaNotaByFactura(idFactura)
        }
      }
      if(notaVenta != null){
        Order order = Order.toOrder(notaVenta)
        List<DetalleNotaVentaJava> detalleVenta = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFactura(notaVenta?.idFactura)
        Boolean entregaBo = true
        Boolean surte = false
        if (entregaInstante) {
          Parametros genericoNoEntrega = ParametrosQuery.BuscaParametroPorId(TipoParametro.GENERICOS_NO_ETREGABLES.value)
          ArrayList<String> genericosNoEntregables = new ArrayList<String>()
          String s = genericoNoEntrega?.valor
          StringTokenizer st = new StringTokenizer(s.trim(), ",")
          Iterator its = st.iterator()
          while (its.hasNext()) {
            genericosNoEntregables.add(its.next().toString())
          }
          Iterator iterator = detalleVenta.iterator();
          while (iterator.hasNext()) {
            DetalleNotaVentaJava detalle = iterator.next()
            ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(detalle?.idArticulo)
            for (int a = 0; a < genericosNoEntregables.size(); a++) {
              String[] values = genericosNoEntregables.get(a).trim().split(":")
              String generico = StringUtils.trimToEmpty(values[0])
              String tipo = values.length > 1 ? StringUtils.trimToEmpty(values[1]) : ''
              String subtipo = values.length > 2 ? StringUtils.trimToEmpty(values[2]) : ''
              String marca = values.length > 3 ? StringUtils.trimToEmpty(values[3]) : ''
              Boolean genericoValid = false
              Boolean tipoValid = false
              Boolean subtipoValid = false
              Boolean marcaValid = false
              if (StringUtils.trimToEmpty(articulo?.idGenerico).equalsIgnoreCase(generico.trim())) {
                genericoValid = true
              }
              if( tipo.length() > 0 ){
                if (StringUtils.trimToEmpty(articulo?.tipo).equalsIgnoreCase(tipo.trim())) {
                  tipoValid = true
                }
              } else {
                tipoValid = true
              }
              if( subtipo.length() > 0 ){
                if (StringUtils.trimToEmpty(articulo?.subtipo).equalsIgnoreCase(subtipo.trim())) {
                  subtipoValid = true
                }
              } else {
                subtipoValid = true
              }
              if( marca.length() > 0 ){
                if (StringUtils.trimToEmpty(articulo?.marca).equalsIgnoreCase(marca.trim())) {
                  marcaValid = true
                }
              } else {
                marcaValid = true
              }
              if( genericoValid && tipoValid && subtipoValid && marcaValid ){
                entregaBo = false
                Parametros diaIntervalo = Registry.find(mx.lux.pos.java.TipoParametro.DIA_PRO)
                Date diaPrometido = new Date() + diaIntervalo?.valor?.toInteger()
                savePromisedDate(notaVenta?.idFactura, diaPrometido)
              }
            }
            if (StringUtils.trimToEmpty(detalle?.surte).equals('P') && !detalle?.articulo?.generico?.inventariable) {
              surte = true
            }
          }
        }
        TmpServiciosJava tmpServicios = TmpServiciosQuery.buscaTmpServiciosPorIdFactura(notaVenta?.idFactura)
        Boolean temp = false
        if (tmpServicios?.idServ != null) {
          temp = true
        }
        if(entregaInstante){
          if (surte || temp || !entregaBo) {
            List<FormaContactoJava> result = ContactController.findByIdCliente(notaVenta?.idCliente)
              if (result.size() == 0) {
                ContactDialog contacto = new ContactDialog(notaVenta)
                contacto.activate()
              } else {
                ContactClientDialog contactoCliente = new ContactClientDialog(notaVenta)
                contactoCliente.activate()
                if (contactoCliente.formaContactoSeleted != null) {
                  FormaContactoJava formaContacto = contactoCliente.formaContactoSeleted
                  formaContacto?.rx = notaVenta?.factura
                  formaContacto?.fechaMod = new Date()
                  formaContacto?.idCliente = notaVenta?.idCliente
                  formaContacto?.idSucursal = notaVenta?.idSucursal
                  formaContacto?.observaciones =  contactoCliente.formaContactoSeleted?.observaciones != '' ? contactoCliente.formaContactoSeleted?.observaciones : ' '
                  formaContacto?.idTipoContacto = contactoCliente.formaContactoSeleted?.tipoContacto?.idTipoContacto
                  ContactController.saveFormaContacto(formaContacto)
                }
              }
          }
        }
        //*Contacto
        if ((order?.total - order?.paid) == 0 && entregaBo) {
          Boolean fechaC = true
          if (!entregaInstante) {
            SimpleDateFormat fecha = new SimpleDateFormat("dd/MMMM/yyyy")
            String fechaVenta = fecha.format(notaVenta?.fechaHoraFactura).toString()
            String ahora = fecha.format(new Date())
            if (fechaVenta.equals(ahora)) {
              fechaC = false
            }
          }
          if (fechaC) {
            if( validaEntregaSegundaVenta( order ) ){
              insertaEntrega(order, entregaInstante)
              deliverOrderLc( notaVenta.factura )
            } else {
              retenerEntrega( order.id )
            }
            try{
              runScriptBckpOrder( order )
            } catch( Exception e){
              println e
            }
          } else {
            JOptionPane.showMessageDialog(null, "No se puede entregar trabajo hoy mismo")
          }
        } else {
          if (!entregaInstante) {
            JOptionPane.showMessageDialog(null, "La nota tiene saldo pendiente por cubrir. No se puede entregar trabajo")
          }
        }
      } else {
        registro = false
      }
      return registro
    }

    static Boolean creaJb(String idFactura, Boolean cSaldo) {
      NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaByFactura(idFactura)
      List<DetalleNotaVentaJava> detalleVenta = detalleNotaVentaServiceJava.listarDetallesNotaVentaPorIdFactura(notaVenta?.idFactura)
      Boolean creaJB = false
      String articulos = ''
      String surte = ''
      String tipoJb = ''
      Boolean genericoD = false
      Iterator iterator = detalleVenta.iterator();
      while (iterator.hasNext()) {
        DetalleNotaVentaJava detalle = iterator.next()
        ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(detalle?.idArticulo)
        articulos = articulos + articulo?.articulo + ', '
        if (StringUtils.trimToEmpty(articulo?.idGenerico).equals('A') || StringUtils.trimToEmpty(articulo?.idGenerico).equals('E')) {
          surte = detalle?.surte
        }
        if (StringUtils.trimToEmpty(articulo?.idGenerico).equals('D')) {
          genericoD = true
        }
        if( notaVenta.fechaEntrega == null ){
          //if (articulo?.idGenerico.trim().equals('B') || articulo?.idGenerico.trim().equals('C') || articulo?.idGenerico.trim().equals('H')) {
          creaJB = true
          if (StringUtils.trimToEmpty(articulo?.idGenerico).equals('C') || StringUtils.trimToEmpty(articulo?.idGenerico).equals('H')) {
            tipoJb = 'LC'
          } else if (StringUtils.trimToEmpty(articulo?.idGenerico).equals('B')) {
            tipoJb = 'LAB'
          }
        }
        String surt = StringUtils.trimToEmpty(detalle?.surte) != '' ? StringUtils.trimToEmpty(detalle?.surte) : ''
        if (surt.equals('P')) {
          creaJB = true
        }
      }
      TmpServiciosJava tmpServicios = TmpServiciosQuery.buscaTmpServiciosPorIdFactura(notaVenta?.idFactura)
      if (tmpServicios?.idServ != null) {
        creaJB = true
      }
      if ( creaJB ) {
        JbJava jb = JbQuery.buscarPorRx(notaVenta?.factura)
        println('JB: ' + jb?.rx)
        JbJava nuevoJb = new JbJava()
        mx.lux.pos.java.repository.JbTrack nuevojbTrack = new mx.lux.pos.java.repository.JbTrack()
        if (jb == null) {
          String factura = notaVenta.factura.replaceFirst("^0*", "")
          nuevoJb?.rx = factura
          nuevoJb?.estado = 'PE'
          nuevoJb?.idCliente = notaVenta?.idCliente
          nuevoJb?.empAtendio = notaVenta?.empleado?.idEmpleado
          nuevoJb?.fechaPromesa = notaVenta?.fechaPrometida
          nuevoJb?.numLlamada = 0
          nuevoJb?.material = articulos
          nuevoJb?.surte = surte
          nuevoJb?.saldo = notaVenta.ventaNeta - notaVenta?.sumaPagos
          nuevoJb?.jbTipo = tipoJb
          nuevoJb?.cliente = notaVenta?.cliente?.nombreCompleto
          nuevoJb?.fechaVenta = notaVenta?.fechaHoraFactura

          nuevojbTrack?.rx = factura
          nuevojbTrack?.estado = 'PE'
          nuevojbTrack?.emp = notaVenta?.empleado?.idEmpleado
          nuevojbTrack?.obs = articulos
          println('jbTipo: ' + nuevoJb?.jbTipo)
          println('LC: ' + StringUtils.trimToEmpty(nuevoJb?.jbTipo).equals('LC'))
          if (StringUtils.trimToEmpty(nuevoJb?.jbTipo).equals('LC')) {
            nuevoJb?.estado = 'EP'
            nuevoJb?.idViaje = '8'
            mx.lux.pos.java.repository.JbTrack nuevoJbTrack2 = new mx.lux.pos.java.repository.JbTrack()
            nuevoJbTrack2?.rx = factura
            nuevoJbTrack2?.estado = 'EP'
            nuevoJbTrack2?.obs = '8'
            nuevoJbTrack2?.idViaje = '8'
            nuevoJbTrack2?.emp = notaVenta?.empleado?.idEmpleado
            nuevoJbTrack2?.fecha = new Date()
            nuevoJbTrack2?.idMod = '0'
            println('LC: ' + nuevoJbTrack2?.idViaje)
            JbTrackQuery.insertJbTrack(nuevoJbTrack2)
          }
          Parametros convenioNomina = ParametrosQuery.BuscaParametroPorId(TipoParametro.CONV_NOMINA.value)
          String s = convenioNomina?.valor
          StringTokenizer st = new StringTokenizer(s.trim(), ",")
          Iterator its = st.iterator()
          Boolean convenio = false
          while (its.hasNext()) {
            if (its.next().toString().equals(notaVenta?.idConvenio)) {
              convenio = true
            }
          }
          if (convenio) {
            nuevoJb?.estado = 'RTN'
            if (genericoD) {
              nuevoJb?.jbTipo = 'EMA'
            } else {
              nuevoJb?.jbTipo = 'EMP'
            }

          }
        }
        if (cSaldo) {
          nuevoJb?.estado = 'RTN'
          nuevojbTrack?.estado = 'RTN'
          nuevojbTrack?.obs = 'Factura con Saldo'
        }
        nuevoJb?.fechaMod = new Date()
        nuevoJb?.idMod = '0'
        nuevojbTrack?.fecha = new Date()
        nuevojbTrack?.idMod = '0'
        if( nuevoJb.rx != null ){
          nuevoJb = JbQuery.saveJb(nuevoJb)
        }
        if( nuevojbTrack.rx != null ){
          JbTrackQuery.insertJbTrack(nuevojbTrack)
        }
      }
      return creaJB
    }

    static DescuentoClave descuentoClavexId(String idDescuentoClave) {
        DescuentoClave descuentoClave = descuentoClaveRepository.findOne(idDescuentoClave)
        return descuentoClave
    }

    static Boolean surteEnabled(String idGenerico) {
        Generico generico = genericoRepository.findOne(idGenerico)
        return generico?.inventariable
    }


    static List<String> surteOption(String idGenerico, String surte) {
        Generico generico = genericoRepository.findOne(idGenerico)

        List<String> surteOption = new ArrayList<String>()
        surteOption.add(surte)
        String s = generico?.surte
        StringTokenizer st = new StringTokenizer(StringUtils.trimToEmpty(s), ",")
        //Iterator its = st.iterator()
        String[] its = StringUtils.trimToEmpty(s).trim().split(',')
        /*for(int i=0;i<its.length;i++){
          if( !surte.equalsIgnoreCase(its[i]) && !its[i].equalsIgnoreCase("P") ){
            surteOption.add(its[i])
          }
        }*/
        /*while (its.hasNext()) {
            if (!its.next().toString().trim().equals(surte)) {
                surteOption.add(its.next().toString())
            }
        }*/

        return surteOption
    }


    static SurteSwitch surteCallWS(Branch branch, Item item, String surte, Order order) {
      Boolean agregaArticulo = true
      Boolean surteSucursal = true
      SurteSwitch surteSwitch = new SurteSwitch()
      surteSwitch?.surte = surte
      List<PreciosJava> lstPrecios = PreciosQuery.buscaPreciosPorArticulo(StringUtils.trimToEmpty(item?.name));
      PreciosJava precio = lstPrecios.size() > 0 ? lstPrecios.get(0) : new PreciosJava()
      if( Registry?.genericCustomer?.id != order.customer.id ){
      if( ((StringUtils.trimToEmpty(item.subtype).startsWith('S') && !StringUtils.trimToEmpty(item.subtype).equalsIgnoreCase("SV"))
              || item.typ.equalsIgnoreCase('O')) ||
                (StringUtils.trimToEmpty(item?.type).equals('A') && StringUtils.trimToEmpty(precio?.surte).equals('P')) ){
        AcusesTipoJava acusesTipo = AcusesTipoQuery.buscaAcuseTipoPorIdTipo('AUT')
        String url = acusesTipo?.pagina + '?id_suc=' + StringUtils.trimToEmpty(branch?.id.toString()) + '&id_col=' + item?.color?.trim() + '&id_art=' + StringUtils.trimToEmpty(item?.name.toString())
        String resultado = ''
        if(  detalleNotaVentaServiceJava.verificaValidacionSP(item?.id, order.id, '') ){
          resultado = callWS(url, item?.id, order.id)
        } else {
          resultado = 'No|'+StringUtils.trimToEmpty(item?.name?.toString())+'|noValidaSP'
        }
            println(resultado)
            int index
            try {
                index = 1
            } catch (ex) {
                index = 1
            }
            String[] result = StringUtils.trimToEmpty(resultado).split(/\|/)
            String condicion = result[0]

            if (condicion.trim().equals('Si')) {
                String contenido = resultado + '|' + item?.id + '|' + item?.color + '|' + 'facturacion'
                Date date = new Date()
                SimpleDateFormat formateador = new SimpleDateFormat("hhmmss")
                String nombre = formateador.format(date)
                generaAcuse(contenido, nombre)

                surteSwitch.surte = 'P'
            } else if (condicion.trim().equals('No') && result.size() == 2) {
                Integer question = JOptionPane.showConfirmDialog(new JDialog(), '<html>Almacen Central no Responde o sin Existencias<br> <br><center>¿Desea Continuar con la venta?<center><html>', '¡Atencion!',
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)
                if (question == 0) {
                    surteSucursal = false
                } else {
                    agregaArticulo = false
                }
            } else if( result.size() >= 3 && result[2].equalsIgnoreCase('noValidaSP') ){
                //notifyAlert('Almacen Central no Responde', 'Contacte a Soporte Tecnico')
                surteSucursal = false
            }
        }
      }
      surteSwitch.setAgregaArticulo(agregaArticulo)
      surteSwitch.setSurteSucursal(surteSucursal)
      return surteSwitch
    }

    static void insertaAcuseAPAR(Order order, Branch branch) {
      List<DetalleNotaVentaJava> listarDetallesNotaVentaPorIdFactura = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFactura(order?.id)
      String parte = ''
      int rx = 0
      Item item =  new Item()
      Boolean insertarAcuse = false
      Iterator iterator = listarDetallesNotaVentaPorIdFactura.iterator();
      while (iterator.hasNext()) {
        DetalleNotaVentaJava detalleNotaVenta = new DetalleNotaVentaJava()
        detalleNotaVenta = iterator.next()
        if (StringUtils.trimToEmpty(detalleNotaVenta?.articulo?.idGenerico).equalsIgnoreCase('B')) {
          rx = 1
        }
        if (StringUtils.trimToEmpty(detalleNotaVenta?.idTipoDetalle).equalsIgnoreCase('VD') ||
                StringUtils.trimToEmpty(detalleNotaVenta?.idTipoDetalle).equalsIgnoreCase('VI.') ||
                StringUtils.trimToEmpty(detalleNotaVenta?.idTipoDetalle).equalsIgnoreCase('FT') ||
                StringUtils.trimToEmpty(detalleNotaVenta?.idTipoDetalle).equalsIgnoreCase('LD') ||
                StringUtils.trimToEmpty(detalleNotaVenta?.idTipoDetalle).equalsIgnoreCase('LI') ||
                StringUtils.trimToEmpty(detalleNotaVenta?.idTipoDetalle).equalsIgnoreCase('CI') ||
                StringUtils.trimToEmpty(detalleNotaVenta?.idTipoDetalle).equalsIgnoreCase('CD') ||
                StringUtils.trimToEmpty(detalleNotaVenta?.idTipoDetalle).equalsIgnoreCase('REM')
        ) {
          parte = parte + detalleNotaVenta?.idTipoDetalle?.trim() + ','
        }
        if(StringUtils.trimToEmpty(detalleNotaVenta?.surte).equalsIgnoreCase('P') && StringUtils.trimToEmpty(detalleNotaVenta?.articulo?.idGenerico).equals('A')) {
          insertarAcuse = true
          item = Item.toItem(detalleNotaVenta?.articulo)
        }
      }
      if (insertarAcuse) {
        String contenidoAPAR = "parteVal=" + parte
        contenidoAPAR = contenidoAPAR + "|facturaVal=" + order?.bill
        contenidoAPAR = contenidoAPAR + "|rxVal=" + rx
        contenidoAPAR = contenidoAPAR + "|id_colVal=" + item?.color
        contenidoAPAR = contenidoAPAR + "|id_sucVal=" + branch?.id
        contenidoAPAR = contenidoAPAR + "|id_artVal=" + item?.name
        contenidoAPAR = contenidoAPAR + "|id_acuseVal=" + (acuseRepository?.nextIdAcuse() +1).toString() + '|'
        AcusesJava acuseAPAR = new AcusesJava()
        acuseAPAR?.contenido = contenidoAPAR
        acuseAPAR?.idTipo = 'APAR'
        acuseAPAR?.intentos = 0
        acuseAPAR?.fechaCarga = new Date()
        AcusesQuery.saveAcuses(acuseAPAR)
        insertarAcuse = false
      }
    }

    static void generaAcuse(String contenido, String nombre) {
      try {
        Parametros ruta = ParametrosQuery.BuscaParametroPorId(TipoParametro.ARCHIVO_CONSULTA_WEB.value)
        File archivo = new File(ruta?.valor, nombre.toString())
        BufferedWriter out = new BufferedWriter(new FileWriter(archivo))
        out.write(contenido)
        out.close()
      } catch (Exception e) {
        e.printStackTrace()
      }
    }

    static String callUrlMethod(String url) {
        String resultado = new String()
        def urlTexto = url
        def resp = urlTexto?.toURL()
        resp = resp.text

        List<String> htmlList = new ArrayList<String>()

        String s = resp?.replaceAll("[\n\r\t]", "")

        println(s)
        StringTokenizer st = new StringTokenizer(s.trim(), ">")
        Iterator its = st.iterator()
        int ini = 0
        Boolean xx = false

        while (its.hasNext()) {
            htmlList.add(its.next().toString() + ">")
            if (xx == true) {

                int index = htmlList.get(ini).indexOf('<')

                resultado = htmlList.get(ini).substring(0, index)

                xx = false

            }
            if (htmlList.get(ini).trim().equals('<XX>')) {
                xx = true
            }
            ini = ini + 1
        }
        return resultado
    }




  static  String callWS(String url, Integer idArticulo, String idFactura) {
    ExecutorService executor = Executors.newFixedThreadPool(1)
    println url
    LogSP log = new LogSP()
    String respuesta = ''
    int timeoutSecs = 20
    final Future<?> future = executor.submit(new Runnable() {
      public void run() {
        try {
          URL urlResp = url.toURL()
          println urlResp.text
          respuesta = urlResp.text?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
          println "Respuesta Surte Pino: ${respuesta}"
        } catch (Exception e) {
          throw new RuntimeException(e)
        }
      }
    })
    try {
      future.get(timeoutSecs, TimeUnit.SECONDS)
      detalleNotaVentaServiceJava.saveLogSP( idArticulo, idFactura, respuesta )
    } catch (Exception e) {
      future.cancel(true)
      respuesta = 'No|'+idArticulo
      this.log.warn("encountered problem while doing some work", e)
    }
    return respuesta
  }


    static String armazonString(String idNotaVenta) {
      String armazonString = ''
      //List<DetalleNotaVenta> detalleVenta = detalleNotaVentaService.listarDetallesNotaVentaPorIdFactura(idNotaVenta)
      List<DetalleNotaVentaJava> detalleVenta = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFactura(idNotaVenta)
      Iterator iterator = detalleVenta.iterator();
      while (iterator.hasNext()) {
        DetalleNotaVentaJava detalle = iterator.next()
        if (StringUtils.trimToEmpty(detalle?.articulo?.idGenerico).equals('A')) {
          armazonString = StringUtils.trimToEmpty(detalle?.articulo?.articulo)
        }
      }
      return armazonString
    }

    static void validaSurtePorGenerico( Order order ){
      NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(order.id)
      notaVentaServiceJava.validaSurtePorGenericoInventariable( notaVenta )
    }


    static String obtieneTiposClientesActivos( ){
        return Registry.activeCustomers
    }


    static void saveSuyo(Order order, User user, String dejo, String instrucciones, String condiciones, String serv) {
        //TmpServicios servicios = new TmpServicios()
        TmpServiciosJava servicios = new TmpServiciosJava()
        servicios.idFactura = order?.id
        servicios.fechaProm = new Date()
        servicios.emp = user?.username
        servicios.idCliente = order?.customer?.id
        servicios.cliente = order?.customer?.name + ' ' + order?.customer?.fathersName + ' ' + order?.customer?.mothersName
        servicios.condicion = condiciones
        servicios.dejo = dejo
        servicios.instruccion = instrucciones
        servicios.servicio = serv
        //tmpServiciosRepository.saveAndFlush(servicios)
        TmpServiciosQuery.saveTmpServicio(servicios)
        //NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(order?.id)
        NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById( order.id )
        notaVenta?.observacionesNv = dejo
        //notaVentaService.saveOrder(notaVenta)
        NotaVentaQuery.updateNotaVenta( notaVenta )
    }


    static void deleteSuyo( Order order ){
      QTmpServicios qTmpServicios = QTmpServicios.tmpServicios
      List<TmpServicios> lstTmpServicio = tmpServiciosRepository.findAll( qTmpServicios.id_factura.eq(order.id) )
      for(TmpServicios tmpServicios : lstTmpServicio){
        tmpServiciosRepository.delete( tmpServicios.id_serv )
        tmpServiciosRepository.flush()
      }
      NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(order?.id)
      notaVenta?.observacionesNv = ""
      notaVentaService.saveOrder(notaVenta)
    }


    static void printSuyo(Order order, User user) {
        TmpServicios servicios = tmpServiciosRepository.findOne( tmpServiciosRepository.tmpExiste(order?.id))
        JbNotas jbNotas = new JbNotas()
        jbNotas?.id_nota = order?.bill.toInteger()
        jbNotas?.id_cliente = order?.customer?.id
        jbNotas?.cliente = order?.customer?.name + ' ' + order?.customer?.fathersName + ' ' + order?.customer?.mothersName
        jbNotas?.dejo = servicios?.dejo
        jbNotas?.instruccion =  servicios?.instruccion
        jbNotas?.emp = user?.username
        jbNotas?.servicio = servicios?.servicio
        jbNotas?.condicion = servicios?.condicion
        jbNotas?.fecha_prom = servicios?.fecha_prom
        jbNotas?.fecha_orden = order?.date
        jbNotas?.fecha_mod = new Date()
        jbNotas?.tipo_serv = 'RECEPCION'
        jbNotas?.id_mod = '0'

        jbNotas = jbNotasRepository.saveAndFlush(jbNotas)


        ticketService.imprimeSuyo(order?.id, jbNotas)
    }



    static Boolean revisaTmpservicios(String idNotaVenta) {
      Boolean existe = false
      TmpServiciosJava tmpServicio = TmpServiciosQuery.buscaTmpServiciosPorIdFactura(idNotaVenta)
      if (tmpServicio != null) {
        existe = true
      }
      return existe
    }

    static ArrayList<String> findAllServices() {
        ArrayList<String> list = new ArrayList<>()
        List<JbServicios> jbServiciosList = jbServiciosRepository.findAll()
        Iterator iterator = jbServiciosList.iterator()
        while (iterator.hasNext()) {

            list.add(iterator.next().servicio)
        }
        return list
    }



  static Boolean validOnlyOnePackage( List<OrderItem> lstItems, Integer idItem ){
    List<Integer> lstIds = new ArrayList<Integer>()
    for(OrderItem item : lstItems){
      lstIds.add( item.item.id )
    }
    Boolean unPaquete = articulosServiceJava.validaUnSoloPaquete( lstIds, idItem )
    return unPaquete
  }


  static Boolean validOnlyOneLens( List<OrderItem> lstItems, Integer idItem ){
    List<Integer> lstIds = new ArrayList<Integer>()
    for(OrderItem item : lstItems){
      lstIds.add( item.item.id )
    }
    Boolean unLente = articulosServiceJava.validaUnSoloLente( lstIds, idItem )
    return unLente
  }


    static Boolean validReusoTicket( String ticket, Integer idArticulo ){
      Boolean ticketValido = notaVentaService.ticketReusoValido( ticket, idArticulo )
      return ticketValido
    }


    static Boolean amountvalid( String factura ){
      log.debug( "amountvalid( )" )
      Boolean montoValido = notaVentaService.montoValidoFacturacion( factura )
      return montoValido
    }


  static Boolean validOnlyInventariable( Order order ){
    return notaVentaServiceJava.validaSoloInventariables( order.id )
  }


  static void creaJbAnticipoInventariables( String idFactura ) {
    log.debug( "creaJbAnticipoInventariables( )" )
    notaVentaServiceJava.insertaJbAnticipoInventariables( idFactura )
  }


  static void runScriptBckpOrder( Order order ){
    log.debug( "runScriptBckpOrder( )" )
    if( order != null && order.id != null ){
      notaVentaService.correScriptRespaldoNotas( order.id )
    }
  }


  static void printPaidOrder(String orderId) {
      if (StringUtils.isNotBlank(orderId)) {
        NotaVenta nota = notaVentaService.obtenerNotaVenta( orderId )
        if( nota != null ){
          for(Pago pago : nota.pagos){
            if( !StringUtils.trimToEmpty(pago.idRecibo).isEmpty() ){
              ticketService.imprimePago(orderId, pago.id)
            }
          }
        }
      } else {
          log.warn("no se imprime pago, parametros invalidos")
      }}


  static Boolean requiereAuth( Order order ){
    Boolean autorizacion = false
    Parametros p = ParametrosQuery.BuscaParametroPorId( mx.lux.pos.java.TipoParametro.ANTICIPO_MENOR_REQUIERE_AUTORIZACIN.valor )
    if( p != null ){
      final String[] TRUE_VALUES = [ "si", "s", "yes", "y", "true", "t", "on" ]
      String value = StringUtils.trimToEmpty( p.valor ).toLowerCase()
      if ( value.length() > 0 ) {
        for ( String trueValue : TRUE_VALUES ) {
          autorizacion = autorizacion || trueValue.equals( value )
          if ( autorizacion )
            break
        }
      }
    }
    return autorizacion
  }

  static Boolean showValidEmployee( ){
    Boolean autorizacion = false
    Parametros p = ParametrosQuery.BuscaParametroPorId( mx.lux.pos.java.TipoParametro.VALIDA_EMPLEADO.valor )
    if( p != null ){
      final String[] TRUE_VALUES = [ "si", "s", "yes", "y", "true", "t", "on" ]
      String value = StringUtils.trimToEmpty( p.valor ).toLowerCase()
      if ( value.length() > 0 ) {
        for ( String trueValue : TRUE_VALUES ) {
          autorizacion = autorizacion || trueValue.equals( value )
          if ( autorizacion )
            break
        }
      }
    }
    return autorizacion
  }


    static Descuento findDiscount( Order order ) {
      List<Descuento> desc = descuentoRepository.findByIdFactura( order.id )
      if( desc.size() > 0 ){
        return  desc.first()
      }
      return  null
    }


    static void updateExam( Order order ){
      ExamenJava examen = examenServiceJava.obtenerExamenPorIdCliente( order.customer.id )
      if( examen != null && (examen.factura = null || StringUtils.trimToEmpty(examen.factura).length() <= 0) ){
        examen.factura = order.bill
        examenServiceJava.guardarExamen( examen )
      }
    }


  static void updateRx( Order order ){
    RecetaJava receta = RecetaQuery.buscaRecetaPorIdReceta( order.rx )
    if( receta != null && StringUtils ){
      receta.tipoOpt = "${StringUtils.trimToEmpty(Registry.currentSite.toString())}:${StringUtils.trimToEmpty(order.bill)}"
      RecetaQuery.saveOrUpdateRx( receta )
    }
  }


  static void updateQuote( Order order, Integer numQuote ){
    if( numQuote != null ){
      cotizacionServiceJava.updateidFacturaQuote( order.id, numQuote )
    } else {
      cotizacionServiceJava.updateQuote( order.id, numQuote )
    }
  }



  static Boolean validaEntregaSegundaVenta(Order order) {
    Boolean valid = true
    Boolean isGoogle = false
    Boolean hasCupon = false
    Boolean hasGenericB = false
    Boolean hasGenericC = false
    NotaVentaJava notaAnterior = notaVentaServiceJava.buscarNotaInicial( order.customer.id, order.id )
    for(OrderItem item : order.items){
      ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId( item.item.id )
      if( articulo.idGenerico.equalsIgnoreCase(TAG_GENERICO_B) ){
        hasGenericB = true
      }
    }
    if( !hasGenericB ){
      for(Payment pago : order.payments){
        if(pago.paymentTypeId.startsWith(TAG_CUPON) && !pago.paymentTypeId.equalsIgnoreCase(TAG_CUPON_SEGURO)){
          hasCupon = true
        }
      }
      if( hasCupon ){
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MMMM/yyyy")
        String fechaVenta = fecha.format(order?.date).toString()
        String ahora = fecha.format(new Date())
        if (fechaVenta.equals(ahora)) {
          valid = false
        } else {
          valid = true
        }
      } else {
        valid = true
      }
    } else {
          valid = true
    }
    return valid
  }


    static void retenerEntrega(String orderId){
      NotaVentaJava nota = NotaVentaQuery.busquedaNotaById( orderId )
      NotaVentaJava notaAnterior = notaVentaServiceJava.buscarNotaInicial( nota.idCliente, nota.id )
      BigDecimal saldo = BigDecimal.ZERO
      if( nota != null ){
        saldo = nota.ventaNeta.subtract(nota.sumaPagos)
      }
      List<DetalleNotaVentaJava> lstDetalles = new ArrayList<>(nota.detalles)
      String articulos = ''
      for(DetalleNotaVentaJava det : nota.detalles){
        articulos = articulos+","+det.articulo.articulo.trim()
      }
      articulos = articulos.replaceFirst( ",", "" )
      mx.lux.pos.java.repository.JbTrack nuevoJbTrack = new mx.lux.pos.java.repository.JbTrack()
      nuevoJbTrack?.rx = nota.factura
      nuevoJbTrack?.estado = 'PE'
      nuevoJbTrack?.emp = nota.idEmpleado
      nuevoJbTrack?.obs = articulos
      nuevoJbTrack?.fecha = new Date()
      nuevoJbTrack?.idMod = '0'
      JbQuery.saveJbTrack( nuevoJbTrack )

      JbJava jbRtn = new JbJava()
      jbRtn.rx = nota.factura
      jbRtn.estado = 'RTN'
      jbRtn.idCliente = nota.idCliente
      jbRtn.empAtendio = nota.idEmpleado
      jbRtn.numLlamada = 0
      jbRtn.saldo = saldo
      jbRtn.jbTipo = 'REF'
      jbRtn.cliente = nota.cliente.nombreCompleto
      jbRtn.idMod = '0'
      jbRtn.fechaMod = new Date()
      jbRtn.fechaVenta = nota.fechaHoraFactura
      jbRtn.material = articulos
      jbRtn = JbQuery.updateEstadoJbRx( jbRtn.rx, jbRtn.estado )

      mx.lux.pos.java.repository.JbTrack jbTrack = new mx.lux.pos.java.repository.JbTrack()
      jbTrack.rx = jbRtn.rx
      jbTrack.estado = "RTN"
      jbTrack.obs = "PAGO CON CUPON"
      jbTrack.emp = jbRtn.empAtendio
      jbTrack.fecha = new Date()
      jbTrack.idMod = '0'
      JbQuery.saveJbTrack( jbTrack )
    }


    static List<NotaVenta> findOrderByClient(Integer idCliente) {
        log.info("buscando orden por cliente: ${idCliente}")
        List<NotaVenta> results = notaVentaService.obtenerNotaVentaPorCliente( idCliente )
        return results
    }


    static List<Order> castListNotaVentaToOrder( List<NotaVenta> lstNotas ){
      List<Order> lstOrders = new ArrayList<>()
      Collections.sort( lstNotas, new Comparator<NotaVenta>() {
          @Override
          int compare(NotaVenta o1, NotaVenta o2) {
              return o1.getFechaHoraFactura().compareTo(o2.getFechaHoraFactura())
          }
      })
      for( NotaVenta nv : lstNotas ){
        lstOrders.add( Order.toOrder(nv) )
      }
      return  lstOrders
    }


    static BigDecimal getMinimumPayment(Order firstOrder, Order secondOrder, BigDecimal amountCupon) {
      BigDecimal minimumAmount = BigDecimal.ZERO
      BigDecimal amountSecondPercentaje = BigDecimal.ZERO
      BigDecimal amountSecond = BigDecimal.ZERO
      BigDecimal porcentaje = Registry.advancePct
      minimumAmount = firstOrder.total.multiply(porcentaje)
      //amountSecondPercentaje = (secondOrder.total.subtract(amountCupon)).compareTo(BigDecimal.ZERO) > 0 ? secondOrder.total.subtract(amountCupon) : BigDecimal.ZERO
      amountSecondPercentaje = secondOrder.total
      amountSecond = amountSecondPercentaje.multiply(porcentaje)
      /*if( amountCupon.compareTo(amountSecondPercentaje) < 0 ){
        amountSecond = amountSecondPercentaje.subtract(amountCupon)
      }*/
      minimumAmount = minimumAmount.add(amountSecond)

      return minimumAmount
    }



    static List<Payment> listPayments( Order first, Order second ){
      List<Payment> lstPayments = new ArrayList<Payment>()
      lstPayments.addAll( first.payments )
      lstPayments.addAll( second.payments )
      return lstPayments
    }


    static Order updateFirstOrder( String idOrder ){
      NotaVenta nota = notaVentaService.obtenerNotaVenta( idOrder )
      return Order.toOrder( nota )
    }

    static Order updateSecondOrder( String idOrder ){
        NotaVenta nota = notaVentaService.obtenerNotaVenta( idOrder )
        return Order.toOrder( nota )
    }



    static List<Payment> listAllPayments( Order first, Order second ){
      List<Payment> lstPayments = new ArrayList<Payment>()
      List<Pago> lstPagosUno = pagoService.listarPagosPorIdFactura( first.id )
      List<Pago> lstPagosDos = pagoService.listarPagosPorIdFactura( second.id )
      for(Pago pago : lstPagosUno){
        lstPayments.add( Payment.toPaymment(pago) )
      }
      for(Pago pago : lstPagosDos){
        lstPayments.add( Payment.toPaymment(pago) )
      }
      return lstPayments
    }


  static BigDecimal amountPayments( String idFirst, String idSecond ){
    log.debug( "amountPayments( )" )
    BigDecimal amount = BigDecimal.ZERO
    NotaVenta first = notaVentaService.obtenerNotaVenta( idFirst )
    NotaVenta second = notaVentaService.obtenerNotaVenta( idSecond )
    if( first != null && second != null ){
      amount = first.sumaPagos.add(second.sumaPagos)
    }
    return amount
  }




    static Boolean validaEntregaMultipago(String idFactura, String idSucursal, Boolean entregaInstante, Boolean askContact) {
        String ticket = idSucursal + '-' + idFactura
        Boolean registro = true
        NotaVenta notaVenta = notaVentaService.obtenerNotaVentaPorTicket(ticket)
        if(notaVenta == null){
            if(idFactura.length()< 6){
                idFactura = String.format( "%06d", Integer.parseInt(idFactura) )
                ticket = idSucursal + '-' + idFactura
                notaVenta = notaVentaService.obtenerNotaVentaPorTicket(ticket)
            }
        }
        if(notaVenta != null){
            Order order = Order.toOrder(notaVenta)
            List<DetalleNotaVenta> detalleVenta = detalleNotaVentaService.listarDetallesNotaVentaPorIdFactura(notaVenta?.id)
            Boolean entregaBo = true
            Boolean surte = false
            if (entregaInstante == true) {
                Parametro genericoNoEntrega = parametroRepository.findOne(TipoParametro.GENERICOS_NO_ETREGABLES.value)
                ArrayList<String> genericosNoEntregables = new ArrayList<String>()
                String s = genericoNoEntrega?.valor
                StringTokenizer st = new StringTokenizer(s.trim(), ",")
                Iterator its = st.iterator()
                while (its.hasNext()) {
                    genericosNoEntregables.add(its.next().toString())
                }
                Iterator iterator = detalleVenta.iterator();
                while (iterator.hasNext()) {
                    DetalleNotaVenta detalle = iterator.next()

                    Articulo articulo = articuloService.obtenerArticulo(detalle?.idArticulo)
                    for (int a = 0; a < genericosNoEntregables.size(); a++) {
                        String[] values = genericosNoEntregables.get(a).trim().split(":")
                        String generico = StringUtils.trimToEmpty(values[0])
                        String tipo = values.length > 1 ? StringUtils.trimToEmpty(values[1]) : ''
                        String subtipo = values.length > 2 ? StringUtils.trimToEmpty(values[2]) : ''
                        String marca = values.length > 3 ? StringUtils.trimToEmpty(values[3]) : ''
                        Boolean genericoValid = false
                        Boolean tipoValid = false
                        Boolean subtipoValid = false
                        Boolean marcaValid = false
                        if (articulo?.idGenerico.trim().equalsIgnoreCase(generico.trim())) {
                            genericoValid = true
                        }
                        if( tipo.length() > 0 ){
                            if (articulo?.tipo.trim().equalsIgnoreCase(tipo.trim())) {
                                tipoValid = true
                            }
                        } else {
                            tipoValid = true
                        }
                        if( subtipo.length() > 0 ){
                            if (articulo?.subtipo.trim().equalsIgnoreCase(subtipo.trim())) {
                                subtipoValid = true
                            }
                        } else {
                            subtipoValid = true
                        }
                        if( marca.length() > 0 ){
                            if (articulo?.marca.trim().equalsIgnoreCase(marca.trim())) {
                                marcaValid = true
                            }
                        } else {
                            marcaValid = true
                        }
                        if( genericoValid && tipoValid && subtipoValid && marcaValid ){
                            entregaBo = false
                            Parametro diaIntervalo = Registry.find(TipoParametro.DIA_PRO)
                            Date diaPrometido = new Date() + diaIntervalo?.valor.toInteger()
                            savePromisedDate(notaVenta?.id, diaPrometido)
                        }
                    }
                    if (detalle?.surte.equals('P')) {
                        surte = true
                    }
                }
            }

            TmpServicios tmpServicios = tmpServiciosRepository.findbyIdFactura(notaVenta?.id)
            Boolean temp = false
            if (tmpServicios?.id_serv != null) {
                temp = true
            }
            /*println(surte == true)
            println(temp == true)
            println(entregaBo == true)*/
            //*Contacto
            if(entregaInstante && askContact){
                if (surte == true || temp == true || entregaBo == false) {
                    List<FormaContacto> result = ContactController.findByIdCliente(notaVenta?.idCliente.toInteger())
                    if (result.size() == 0) {
                        ContactDialog contacto = new ContactDialog(notaVenta)
                        contacto.activate()
                    } else {
                        ContactClientDialog contactoCliente = new ContactClientDialog(notaVenta)
                        contactoCliente.activate()
                        if (contactoCliente.formaContactoSeleted != null) {
                            FormaContacto formaContacto = contactoCliente.formaContactoSeleted
                            formaContacto?.rx = notaVenta?.factura
                            formaContacto?.fecha_mod = new Date()
                            formaContacto?.id_cliente = notaVenta?.idCliente
                            formaContacto?.id_sucursal = notaVenta?.idSucursal
                            formaContacto?.observaciones =  contactoCliente.formaContactoSeleted?.observaciones != '' ? contactoCliente.formaContactoSeleted?.observaciones : ' '
                            formaContacto?.id_tipo_contacto = contactoCliente.formaContactoSeleted?.tipoContacto?.id_tipo_contacto
                            ContactController.saveFormaContacto(formaContacto)
                        }
                    }
                }
            }
            //*Contacto
            if ((order?.total - order?.paid) == 0 && entregaBo == true) {
                Boolean fechaC = true
                if (entregaInstante == false) {
                    SimpleDateFormat fecha = new SimpleDateFormat("dd/MMMM/yyyy")
                    String fechaVenta = fecha.format(notaVenta?.fechaHoraFactura).toString()
                    String ahora = fecha.format(new Date())
                    if (fechaVenta.equals(ahora)) {
                        fechaC = false
                    }
                }
                if (fechaC == true) {
                    if( validaEntregaSegundaVenta( order ) ){
                        insertaEntrega(order, entregaInstante)
                        deliverOrderLc( notaVenta.factura )
                    } else {
                        retenerEntrega( order.id )
                    }
                    try{
                        runScriptBckpOrder( order )
                    } catch( Exception e){
                        println e
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No se puede entregar trabajo hoy mismo")
                }
            } else {
                if (entregaInstante == false) {
                    JOptionPane.showMessageDialog(null, "La nota tiene saldo pendiente por cubrir. No se puede entregar trabajo")
                }
            }
        }else{
            registro = false
        }
        return registro
    }


    static BigDecimal getCuponAmount(String idOrder) {
      log.debug( 'getCuponAmount( )' )
      return notaVentaServiceJava.obtenerMontoCupon( idOrder )
    }

    static BigDecimal getCuponAmountThirdPair(String idOrder) {
        log.debug( 'getCuponAmountThirdPair( )' )
        return notaVentaServiceJava.obtenerMontoCuponTercerPar( idOrder )
    }

    static void creaJbAnticipoInventariablesMultypayment( String idFactura ) {
        log.debug( "creaJbAnticipoInventariables( )" )
        NotaVenta nota = notaVentaService.obtenerNotaVenta( idFactura )
        if( nota != null && (nota.ventaNeta.doubleValue()-nota.sumaPagos.doubleValue() > 0) ){
          notaVentaService.insertaJbAnticipoInventariables( idFactura )
        }
    }


    static Boolean validLenses( Order order ){
        //return notaVentaService.validaLentes( order.id )
      return notaVentaServiceJava.validaLentes( order.id )
    }


    static List<Item> existeLenteContacto(Order order){
      List<Item> lstItems = new ArrayList<>()
      //List<Articulo> articulo = notaVentaService.validaLentesContacto( order.id )
      List<ArticulosJava> articulo = notaVentaServiceJava.validaLentesContacto( StringUtils.trimToEmpty(order.id) )
      for(ArticulosJava art : articulo){
        lstItems.add( Item.toItem(art) )
      }
      return lstItems
    }



    static Boolean validHasLcDet( String orderId, Integer idArticulo ){
      return notaVentaService.validaTieneDetalles( orderId, idArticulo )
    }



    static void removePedidoLc( String orderId, Integer idArticulo ){
      log.debug( "Remover pedido de lentes de contacto" )
      //notaVentaService.removePedidoLc( orderId, idArticulo )
      notaVentaServiceJava.removePedidoLc( orderId, idArticulo )
    }



    static void creaJbFam( String idOrder1, String idOrder2 ){
      log.debug( "creaJbFam( )" )
      NotaVenta nota1 = notaVentaService.obtenerNotaVenta( idOrder1 )
      NotaVenta nota2 = notaVentaService.obtenerNotaVenta( idOrder2 )
      Jb jbFam = jbService.saveJbFamilia( nota1, nota2 )
      formaContactoService.saveFCFam( nota1.factura, jbFam, nota2.factura )
    }


    static void updateOrderLc( Order order ){
      NotaVentaJava nota = NotaVentaQuery.busquedaNotaById( order.id )
      BigDecimal total = BigDecimal.ZERO
      for(DetalleNotaVentaJava det : nota.detalles){
        total = total+(det.precioUnitFinal.multiply(det.cantidadFac))
      }
      nota.ventaNeta = total
      nota.ventaTotal = total
      notaVentaServiceJava.registrarNotaVenta( nota )
    }

    static void deliverOrderLc( String idPedido ){
      notaVentaServiceJava.entregaPedidoLc( StringUtils.trimToEmpty(idPedido) )
    }

  static Boolean validGenericNoDelivered( String idOrder ){
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById( idOrder )
    List<DetalleNotaVentaJava> detalleVenta = new ArrayList<>()
    for(DetalleNotaVentaJava det : nota.detalles){
      detalleVenta.add( det )
    }
    Boolean entregaBo = false
    Boolean surte = false
    Parametros genericoNoEntrega = ParametrosQuery.BuscaParametroPorId(mx.lux.pos.java.TipoParametro.GENERICOS_NO_ETREGABLES.valor)
    ArrayList<String> genericosNoEntregables = new ArrayList<String>()
    String s = genericoNoEntrega?.valor
    StringTokenizer st = new StringTokenizer(s.trim(), ",")
    Iterator its = st.iterator()
    while (its.hasNext()) {
      genericosNoEntregables.add(its.next().toString())
    }
    Iterator iterator = detalleVenta.iterator();
    while (iterator.hasNext()) {
      DetalleNotaVentaJava detalle = iterator.next()
      ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(detalle?.idArticulo)
      for (int a = 0; a < genericosNoEntregables.size(); a++) {
        String[] values = genericosNoEntregables.get(a).trim().split(":")
        String generico = StringUtils.trimToEmpty(values[0])
        String tipo = values.length > 1 ? StringUtils.trimToEmpty(values[1]) : ''
        String subtipo = values.length > 2 ? StringUtils.trimToEmpty(values[2]) : ''
        String marca = values.length > 3 ? StringUtils.trimToEmpty(values[3]) : ''
        Boolean genericoValid = false
        Boolean tipoValid = false
        Boolean subtipoValid = false
        Boolean marcaValid = false
        if (StringUtils.trimToEmpty(articulo?.idGenerico).equalsIgnoreCase(generico.trim())) {
          genericoValid = true
        }
        if( tipo.length() > 0 ){
          if (StringUtils.trimToEmpty(articulo?.tipo).equalsIgnoreCase(tipo.trim())) {
            tipoValid = true
          }
        } else {
          tipoValid = true
        }
        if( subtipo.length() > 0 ){
          if (StringUtils.trimToEmpty(articulo?.subtipo).equalsIgnoreCase(subtipo.trim())) {
            subtipoValid = true
          }
        } else {
          subtipoValid = true
        }
        if( marca.length() > 0 ){
          if (StringUtils.trimToEmpty(articulo?.marca).equalsIgnoreCase(marca.trim())) {
            marcaValid = true
          }
        } else {
          marcaValid = true
        }
        if( genericoValid && tipoValid && subtipoValid && marcaValid ){
          entregaBo = true
        }
      }
    }
    return entregaBo
  }


    static void creaJbLc( String idFactura ) {
        log.debug( "creaJbLc( )" )
        notaVentaService.insertaJbLc( idFactura )
    }




  static Boolean validArticleGenericNoDelivered( Integer idItem ){
    Boolean entregaBo = false
    Parametros genericoNoEntrega = ParametrosQuery.BuscaParametroPorId(TipoParametro.GENERICOS_NO_ETREGABLES.value)
    ArrayList<String> genericosNoEntregables = new ArrayList<String>()
    String s = genericoNoEntrega?.valor
    StringTokenizer st = new StringTokenizer(s.trim(), ",")
    Iterator its = st.iterator()
    while (its.hasNext()) {
      genericosNoEntregables.add(its.next().toString())
    }
    ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(idItem)
    for (int a = 0; a < genericosNoEntregables.size(); a++) {
      String[] values = genericosNoEntregables.get(a).trim().split(":")
      String generico = StringUtils.trimToEmpty(values[0])
      String tipo = values.length > 1 ? StringUtils.trimToEmpty(values[1]) : ''
      String subtipo = values.length > 2 ? StringUtils.trimToEmpty(values[2]) : ''
      String marca = values.length > 3 ? StringUtils.trimToEmpty(values[3]) : ''
      Boolean genericoValid = false
      Boolean tipoValid = false
      Boolean subtipoValid = false
      Boolean marcaValid = false
      if (StringUtils.trimToEmpty(articulo?.idGenerico).equalsIgnoreCase(StringUtils.trimToEmpty(generico))) {
        genericoValid = true
      }
      if( tipo.length() > 0 ){
        if (StringUtils.trimToEmpty(articulo?.tipo).equalsIgnoreCase(StringUtils.trimToEmpty(tipo))) {
          tipoValid = true
        }
      } else {
        tipoValid = true
      }
      if( subtipo.length() > 0 ){
        if (StringUtils.trimToEmpty(articulo?.subtipo).equalsIgnoreCase(StringUtils.trimToEmpty(subtipo))) {
          subtipoValid = true
        }
      } else {
        subtipoValid = true
      }
      if( marca.length() > 0 ){
        if (StringUtils.trimToEmpty(articulo?.marca).equalsIgnoreCase(StringUtils.trimToEmpty(marca))) {
          marcaValid = true
        }
      } else {
        marcaValid = true
      }
      if( genericoValid && tipoValid && subtipoValid && marcaValid ){
        entregaBo = true
      }
    }
    return entregaBo
  }



    static void saveBatch( String idFactura, Integer idArticulo, String lote ) {
      notaVentaServiceJava.saveBatch( idFactura, idArticulo, lote )
    }



    static String findBatchByIdAndArticle(String idOrder, Integer idArticle) {
      String batch = ""
      DetalleNotaVenta det = detalleNotaVentaService.obtenerDetalleNotaVenta( idOrder, idArticle )
      if( det != null ){
        batch = StringUtils.trimToEmpty(det.idRepVenta)
      }
      return batch
    }



    static Boolean validalote(String idOrder, Integer idArticulo, String lote){
      return notaVentaService.validaLote( idOrder, idArticulo, lote )
    }


    static Boolean ticketToday(String ticket) {
      Boolean isToday = false
      NotaVenta nota = notaVentaService.obtenerNotaVentaPorTicket( Registry.currentSite.toString()+"-"+ticket )
      if( nota != null && nota.fechaHoraFactura.format(DATE_FORMAT).equalsIgnoreCase(new Date().format(DATE_FORMAT)) ){
        isToday = true
      }
      return isToday
    }



    static Boolean esPromocionValida(String idOrder, Integer idPromo){
      //return notaVentaService.existePromoEnOrden( idOrder, idPromo )
      return notaVentaServiceJava.existePromoEnOrden( idOrder, idPromo )
    }


  static Boolean hasOrderLc( String bill ){
    Boolean hasOrderLc = false
    PedidoLcJava pedidoLc = PedidoLcQuery.buscaPedidoLcPorId( StringUtils.trimToEmpty(bill) )
    if( pedidoLc != null ){
      hasOrderLc = true
    }
    return hasOrderLc
  }


    static void createAcuse( String idOrder ){
      notaVentaService.creaAcusePedidoLc( idOrder )
    }

    static void printTicketEnvioLc( String idPedido ){
        ticketService.imprimeTicketEnvioLc( idPedido )
    }


    static Boolean estaCancelada( String factura ){
      Boolean estaCancelada = false
      //String ticket = StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+StringUtils.trimToEmpty(factura)
      NotaVenta nota = notaVentaService.obtenerNotaVentaPorTicket( factura )
      if( nota != null && TAG_CANCELADA.equalsIgnoreCase(nota.sFactura) ){
        estaCancelada = true
      }
      return estaCancelada
    }


    static String[] montoDescuento( String idFactura ){
      return notaVentaService.montoDescuentoNota( idFactura )
    }


    static void printReuse( String idOrder ){
      Boolean hasReuse = false
      NotaVenta nv = notaVentaService.obtenerNotaVenta( idOrder )
      for(DetalleNotaVenta det : nv.detalles){
        if( TAG_REUSO.equalsIgnoreCase(StringUtils.trimToEmpty(det?.surte)) ){
          hasReuse = true
        }
      }
      if( hasReuse ){
        ticketService.imprimeTicketReuso( idOrder )
      }
    }



    static saveCuponMv( CuponMv cuponMv ){
      notaVentaService.guardarCuponMv( cuponMv )
    }

    static CuponMv updateCuponMv( String idFacturaOrigen, String idFacturaDestino, BigDecimal montoCupon, Integer numeroCupon, Boolean ffCoupon ){
      return notaVentaService.actualizarCuponMv( StringUtils.trimToEmpty(idFacturaOrigen),
              StringUtils.trimToEmpty(idFacturaDestino), montoCupon, numeroCupon, ffCoupon )
    }

    static CuponMvJava updateCuponMvJava( String idFacturaOrigen, String idFacturaDestino, BigDecimal montoCupon, Integer numeroCupon, Boolean ffCoupon ){
      return notaVentaServiceJava.actualizarCuponMv( StringUtils.trimToEmpty(idFacturaOrigen),
              StringUtils.trimToEmpty(idFacturaDestino), montoCupon, numeroCupon, ffCoupon )
    }

    static NotaVenta findOrderByidOrder(String idOrder) {
      NotaVenta result = notaVentaService.obtenerNotaVenta( idOrder )
      return result
    }

    static NotaVentaJava findOrderJavaByidOrder(String idOrder) {
      NotaVentaJava result = NotaVentaQuery.busquedaNotaById( idOrder )
      return result
    }

    static Boolean hasCuponMv(String idOrder) {
        CuponMv cuponMv1 = notaVentaService.obtenerCuponMv( StringUtils.trimToEmpty(idOrder) )
      println (cuponMv1 != null && !StringUtils.trimToEmpty(cuponMv1.claveDescuento).startsWith("F"))
        return (cuponMv1 != null && !StringUtils.trimToEmpty(cuponMv1.claveDescuento).startsWith("F"))
    }

    static void printCuponTicket( CuponMv cuponMv, String titulo, BigDecimal monto ){
      ticketService.imprimeCupon( cuponMv,titulo, monto )
    }

    static void printCuponTicket( CuponMvJava cuponMv, String titulo, BigDecimal monto ){
      ticketService.imprimeCupon( cuponMv,titulo, monto )
    }

    static CuponMvView cuponValid( Integer idCustomer ){
      CuponMvView cuponMvView = new CuponMvView()
      cuponMvView.amount = notaVentaService.cuponValid( idCustomer )
      cuponMvView.idOrderSource = notaVentaService.orderSource( idCustomer )
      return cuponMvView
    }


    static void deleteCuponMv( String idOrder ){
      notaVentaServiceJava.eliminarCUponMv( idOrder )
    }

    static CuponMv obtenerCuponMv( String idFacturaOrigen, String idFacturaDestino, BigDecimal montoCupon, Integer numeroCupon ){
        return notaVentaService.actualizarCuponMv( StringUtils.trimToEmpty(idFacturaOrigen),
                StringUtils.trimToEmpty(idFacturaDestino), montoCupon, numeroCupon, false )
    }

    static CuponMv obtenerCuponMvByClave( String clave ){
        return notaVentaService.obtenerCuponMvClave( clave )
    }

    static CuponMvJava obtenerCuponMvJavaByClave( String clave ){
      return notaVentaServiceJava.obtenerCuponMvClave( clave )
    }

    static List<CuponMv> obtenerCuponMvBySourceOrder( String order ){
        return notaVentaService.obtenerCuponMvFacturaOri( order )
    }

    static List<CuponMv> obtenerCuponMvByTargetOrder( String order ){
        return notaVentaService.obtenerCuponMvFacturaDest( order )
    }

    static CuponMv updateCuponMvByClave( String idFacturaDest, String clave ){
        return notaVentaService.actualizarCuponMvPorClave( idFacturaDest, clave )
    }

    static CuponMvJava updateCuponMvJavaByClave( String idFacturaDest, String clave ){
      return notaVentaServiceJava.actualizarCuponMvPorClave( idFacturaDest, clave )
    }


    static DescuentoClave descuentoClaveCupon(String idDescuentoClave) {
        DescuentoClave descuentoClave = null
        QCuponMv qCuponMv = QCuponMv.cuponMv
        CuponMv cuponMv1 = cuponMvRepository.findOne( qCuponMv.claveDescuento.eq(idDescuentoClave).
                and(qCuponMv.facturaDestino.isNull().or(qCuponMv.facturaDestino.isEmpty())).
                and(qCuponMv.fechaAplicacion.isNull()).and(qCuponMv.fechaVigencia.after(new Date()).
                or(qCuponMv.fechaVigencia.eq(new Date()))) )
        if( cuponMv1 != null ){
          descuentoClave = new DescuentoClave()
          descuentoClave.clave_descuento = cuponMv1.claveDescuento
          descuentoClave.porcenaje_descuento = cuponMv1.montoCupon.doubleValue()
          descuentoClave.descripcion_descuento = StringUtils.trimToEmpty(cuponMv1.claveDescuento).startsWith("H") ? TAG_MSJ_CUPON_LC : TAG_MSJ_CUPON
          descuentoClave.tipo = TAG_TIPO_DESCUENTO
          descuentoClave.vigente = true
        }

        return descuentoClave
    }

    static void deleteCuponMultypayment( String idOrder ){
      notaVentaService.eliminarCuponMultipago( idOrder )
    }


    static void deletePromotion( String idOrder ){
        notaVentaService.eliminaPromocion( idOrder )
    }

    static Boolean generatesCoupon(String claveDescuento){
      //return notaVentaServiceJava.cuponGeneraCupon(claveDescuento)
      return notaVentaService.cuponGeneraCupon(claveDescuento)
    }


    static String descuentoClavePoridFactura( String idFactura ){
      return notaVentaService.claveDescuentoNota( StringUtils.trimToEmpty(idFactura) )
    }

    static String descuentoClaveJavaPoridFactura( String idFactura ){
      return notaVentaServiceJava.claveDescuentoNota( StringUtils.trimToEmpty(idFactura) )
    }

    static String isReuseOrderLc(String idOrder) {
      return notaVentaService.esReusoPedidoLc( idOrder )
    }


    static void existDiscountKey( String key, String oldOrder ){
      notaVentaService.existeDescuentoClave( key, oldOrder )
    }


  static Boolean dayIsOpen(){
    Boolean isOpenDay = true
    if( Registry.validDayCloseToSell() ){
      isOpenDay = notaVentaServiceJava.diaActualEstaAbierto()
    }
    return isOpenDay
  }



    static List<Coupons> appliedCouponsByOrderSource( String bill ){
      List<CuponMv> lstCupones = new ArrayList<>()
      List<CuponMv> cupones = notaVentaService.obtenerCuponMvFacturaOriApplied( bill )
      for(CuponMv cuponMv : cupones){
        if( StringUtils.trimToEmpty(cuponMv.facturaDestino).length() > 0 &&
        cuponMv.fechaAplicacion != null ){
          String ticket = StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+StringUtils.trimToEmpty(cuponMv.facturaDestino)
          NotaVenta facturaDestino = notaVentaService.obtenerNotaVentaPorTicket( ticket )
          if( !facturaDestino.sFactura.equalsIgnoreCase(TAG_CANCELADA) ){
            if( facturaDestino.montoDescuento.doubleValue() <= cuponMv.montoCupon.doubleValue() ){
              cuponMv.montoCupon = facturaDestino.montoDescuento
            }
            lstCupones.add( cuponMv )
          }
        }
      }
      return lstCupones?.collect { CuponMv tmp ->
          Coupons.toCoupon(tmp)
      }
    }


    static Pago updatePaymentToOrder(String orderId, Payment payment) {
        log.info("actualizando pago monto: ${payment?.amount}, tipo: ${payment?.paymentTypeId} a orden id: ${orderId}")
        if (StringUtils.isNotBlank(orderId) && StringUtils.isNotBlank(payment?.paymentTypeId) && payment?.amount) {
          User user = Session.get(SessionItem.USER) as User
          Pago pago = pagoService.obtenerPago( payment.id )
          if( pago != null ){
            pago.idFormaPago = payment.paymentTypeId
            pago.referenciaPago = payment.paymentReference
            pago.monto = payment.amount
            pago.idEmpleado = user?.username
            pago.idFPago = payment.paymentTypeId
            pago.clave = payment.paymentReference
            pago.referenciaClave = payment.codeReference
            pago.idBancoEmisor = payment.issuerBankId
            pago.idTerminal = payment.terminalId
            pago.idPlan = payment.planId
            Pago newPago = notaVentaService.actualizarPagoEnNotaVenta(orderId, pago)
            return newPago
          } else {
            log.warn("no se encontro el pago para actualizar")
          }
        } else {
            log.warn("no se agrega pago, parametros invalidos")
        }
        return null
    }


    static void printResumeCancCoupon( String idOrder, List<String> devAmount ){
      ticketService.imprimeResumenCuponCan( idOrder, devAmount )
    }


    static Boolean validDioptra( String idOrder ){
      Boolean valid = false
      NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById( idOrder )
      if( notaVenta != null ){
        valid = articulosServiceJava.validaCodigoDioptra( StringUtils.trimToEmpty(notaVenta.codigoLente) )
      }
      return valid
    }


    static void generateCouponFAndF( String idOrder ){
      BigDecimal amountSale = BigDecimal.ZERO
      Boolean hasNoCouponApply = true
      Boolean hasLenses = false
      Date fechaInicio = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH );
      Date fechaFin = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 );
      NotaVentaJava nota = NotaVentaQuery.busquedaNotaById( idOrder )
      if( nota!= null && nota.fechaEntrega != null ){
        List<NotaVentaJava> lstNotasCliente = notaVentaServiceJava.obtenerNotaVentaPorClienteFF( nota.idCliente )
        for(NotaVentaJava notaVenta : lstNotasCliente){
          List<CuponMvJava> cuponMv = notaVentaServiceJava.obtenerCuponMvFacturaOriFF( StringUtils.trimToEmpty(notaVenta.factura) )
          if( cuponMv.size() > 0 && StringUtils.trimToEmpty(cuponMv.first().claveDescuento).startsWith("F") ){
            hasNoCouponApply = false
          }
        }
        if( hasNoCouponApply ){
          Integer appliedCoup = 0
          List<CuponMvJava> lstCupones = notaVentaServiceJava.obtenerCuponMvFacturaDest( StringUtils.trimToEmpty(nota.factura) )
          if( lstCupones.size() <= 0 ){
            lstCupones = notaVentaServiceJava.obtenerCuponMvFacturaDest( StringUtils.trimToEmpty(nota.idFactura) )
          }
          for(CuponMvJava c : lstCupones){
            if( StringUtils.trimToEmpty(c.fechaAplicacion.format("dd-MM-yyyy")).equalsIgnoreCase(StringUtils.trimToEmpty(new Date().format("dd-MM-yyyy"))) ){
              appliedCoup = appliedCoup+1
            }
          }
          List<DescuentosJava> lstDesc = DescuentosQuery.buscaDescuentosPorClaveAndIdFactura("AF200", nota.idFactura)
          Integer descuentoAF = lstDesc.size()
          if( appliedCoup > 0 || descuentoAF > 0 ){
            hasNoCouponApply = false
          }
        }
        for(DetalleNotaVentaJava det : nota.detalles){
          if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_B) ){
            hasLenses = true
          }
          if( !StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEG) ){
            amountSale = amountSale.add(det.precioUnitFinal)
          }
        }
        if( amountSale.doubleValue() >= Registry.amountToGenerateFFCoupon && hasNoCouponApply && hasLenses ){
          List<CuponMvJava> cuponMvTmp = notaVentaServiceJava.obtenerCuponMvFacturaDest( StringUtils.trimToEmpty(nota.factura) )
          if( cuponMvTmp.size() <= 0 || !StringUtils.trimToEmpty(cuponMvTmp.first().claveDescuento).startsWith("F") ){
            String titulo = "FRIENDS AND FAMILY"
            Integer numCupon = 0
            CuponMvJava cuponMv = new CuponMvJava()
            cuponMv.facturaDestino = ""
            cuponMv.facturaOrigen = nota.factura
            cuponMv.fechaAplicacion = null
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, Registry.diasVigenciaCuponFF)
            cuponMv.fechaVigencia = calendar.getTime()
            println cuponMv.fechaVigencia.format("dd-MM-yyyy")
            cuponMv = updateCuponMvJava( nota.idFactura, "", Registry.amountFFCoupon, numCupon, true )
            printCuponTicket( cuponMv, titulo, Registry.amountFFCoupon )
          }
        }
      }
    }


static Boolean validWarranty( Descuento promotionApplied, Item item ){
      Boolean valid = true
      Boolean hasWarrantyApplied = false
      if( promotionApplied != null ){
        if( StringUtils.trimToEmpty(promotionApplied.idTipoD).equalsIgnoreCase(TAG_ID_GARANTIA) ){
          hasWarrantyApplied = true
        }
      }
      if( hasWarrantyApplied && StringUtils.trimToEmpty(item.type).equalsIgnoreCase(TAG_GENERICO_SEG) ){
        valid = false
      }
      return valid
    }



    static Boolean validWarranty( NotaVenta nota, Boolean cleanWaranties, OrderPanel panel, String idOrderPostEnsure, Boolean addIdOrder ){
      canceledWarranty = false
      if( StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ){
        postEnsure = StringUtils.trimToEmpty(idOrderPostEnsure)
      }
      NotaVenta oldNota = null
      if( StringUtils.trimToEmpty(postEnsure).length() > 0 ){
        oldNota = notaVentaService.obtenerNotaVenta( postEnsure )
        if( oldNota != null ){
          nota.detalles.addAll( oldNota.detalles )
        }
      }
      Boolean valid = true
      Boolean applyValid = false
      List<Integer> lstIdGar = new ArrayList<>()
      List<Integer> lstIdArm = new ArrayList<>()
      for(DetalleNotaVenta orderItem : nota.detalles){
        if( !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE) ){
          if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
            for(int i=0;i<orderItem.cantidadFac;i++){
              lstIdGar.add(orderItem.idArticulo)
            }
          } else {
            for(int i=0;i<orderItem.cantidadFac;i++){
              lstIdArm.add(orderItem.idArticulo)
            }
          }
        }
      }
      Boolean hasC1 = false
      for(Pago pago : nota.pagos){
        if(TAG_CUPON_SEGURO.equalsIgnoreCase(StringUtils.trimToEmpty(pago.idFPago))){
          hasC1 = true
        }
      }
      if( oldNota != null && StringUtils.trimToEmpty(oldNota.udf5).length() > 0 ){
        valid = false
      }
      Boolean sunglass = false
      Boolean lens = false
      Boolean ophtglass = false
      Boolean lensKid = false
      Boolean frame = false
      String typeEnsure = ""
      for(Integer idArt : lstIdArm ){
        Articulo articulo = articuloService.obtenerArticulo( idArt )
        if( articulo != null ){
          String type = StringUtils.trimToEmpty(articulo.subtipo).length() > 0 ? StringUtils.trimToEmpty(articulo.subtipo) : StringUtils.trimToEmpty(articulo.idGenSubtipo)
          if( StringUtils.trimToEmpty(type).startsWith(TAG_SUBTIPO_NINO) ){
            lensKid = true
          }
          if( StringUtils.trimToEmpty(articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
            frame = true
          }
          if( StringUtils.trimToEmpty(articulo.tipo).equalsIgnoreCase(TAG_TIPO_OFTALMICO) ){
            ophtglass = true
          } else if( StringUtils.trimToEmpty(articulo.tipo).equalsIgnoreCase(TAG_TIPO_SOLAR) ){
              sunglass = true
          } else if( StringUtils.trimToEmpty(articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_LENTE) ){
            lens = true
          }
        }
      }

      /*if( ophtglass && !lens ){
        valid = false
      } else if( lens && !ophtglass && !sunglass){
        valid = false
      }*/

      if( lstIdGar.size() > 0 ){
        if( valid && !hasC1 ){
          if( lstIdGar.size() == 1 ){
            List<DetalleNotaVenta> lstDets = new ArrayList<>()
            BigDecimal amount = BigDecimal.ZERO
            Articulo warnt = articuloService.obtenerArticulo( lstIdGar.first() )
            String items = ""
            for(DetalleNotaVenta orderItem : nota.detalles){
              if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS)
                      && !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE) ){
                if( StringUtils.trimToEmpty(warnt.articulo).startsWith(TAG_SEGUROS_ARMAZON) ){
                  if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                    amount = amount.add(orderItem.precioUnitLista)
                    items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                    lstDets.add( orderItem )
                  }
                  typeEnsure = "S"
                } else {
                  if( StringUtils.trimToEmpty(warnt.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) ){
                    String type = StringUtils.trimToEmpty(orderItem.articulo.subtipo).length() > 0 ? StringUtils.trimToEmpty(orderItem.articulo.subtipo) : StringUtils.trimToEmpty(orderItem.articulo.idGenSubtipo)
                    if( StringUtils.trimToEmpty(type).startsWith(TAG_SUBTIPO_NINO) ||
                            !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
                      amount = amount.add(orderItem.precioUnitLista)
                      items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                      lstDets.add( orderItem )
                    }
                  } else {
                    if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ||
                            (StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) &&
                          StringUtils.trimToEmpty(orderItem.articulo.tipo).equalsIgnoreCase(TAG_TIPO_OFTALMICO)) ){
                      amount = amount.add(orderItem.precioUnitLista)
                      items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                      lstDets.add( orderItem )
                    }
                  }
                  if( StringUtils.trimToEmpty(warnt.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) ){
                    typeEnsure = "N"
                  } else if( !StringUtils.trimToEmpty(warnt.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) &&
                          StringUtils.trimToEmpty(warnt.articulo).startsWith(TAG_SEGUROS_OFTALMICO) ){
                    typeEnsure = "L"
                  }
                }
              }
            }
            BigDecimal warrantyAmount = ItemController.warrantyValid( amount, lstIdGar.first() )
            if( warrantyAmount.compareTo(BigDecimal.ZERO) > 0 && segValid(lstIdGar.first(), lstIdArm) ){
              amount = BigDecimal.ZERO
              for(DetalleNotaVenta orderItem : lstDets){
                if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS)
                       && !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE)){
                  if( StringUtils.trimToEmpty(warnt.articulo).startsWith(TAG_SEGUROS_ARMAZON) ){
                    if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                      //items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                      amount = amount.add( orderItem.precioUnitFinal )
                    }
                  } else {
                    //if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                      //items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                    //}
                    amount = amount.add( orderItem.precioUnitFinal )
                  }
                }
              }
              Warranty warranty = new Warranty()
              warranty.amount = amount
              warranty.idItem = items.replaceFirst(",","")
              warranty.typeEnsure = typeEnsure
              warranty.idOrder = addIdOrder ? nota.id : ""
              idOrderEnsured = StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ? StringUtils.trimToEmpty(idOrderPostEnsure) : idOrderEnsured
              //println idOrderEnsured
              lstWarranty.add( warranty )
              lstIdGar.clear()
            } else {
              MSJ_ERROR_WARRANTY = "Seguro Invalido."
              valid = false
            }
          } else if( lstIdGar.size() == 2 && frame && lens ) {
            List<DetalleNotaVenta> lstDetsL = new ArrayList<>()
            List<DetalleNotaVenta> lstDetsF = new ArrayList<>()
            BigDecimal amountSegL = BigDecimal.ZERO
            BigDecimal amountSegF = BigDecimal.ZERO
            List<DetalleNotaVenta> lstLens = new ArrayList<>()
            List<DetalleNotaVenta> lstFrames = new ArrayList<>()
            Articulo segFrame = new Articulo()
            Articulo segLens = new Articulo()
            String typeEnsureO = ""
            String typeEnsureF = ""
            for(DetalleNotaVenta orderItem : nota.detalles){
              if( !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE) ){
                if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                  if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                    if( StringUtils.trimToEmpty(orderItem.articulo.tipo).equalsIgnoreCase(TAG_TIPO_OFTALMICO) ){
                      amountSegL = amountSegL.add(orderItem.precioUnitLista)
                      lstLens.add( orderItem )
                    } else {
                      amountSegF = amountSegF.add(orderItem.precioUnitLista)
                      lstFrames.add( orderItem )
                    }
                  } else {
                    amountSegL = amountSegL.add(orderItem.precioUnitLista)
                    lstLens.add( orderItem )
                  }
                } else {
                  if( StringUtils.trimToEmpty(orderItem.articulo.articulo).startsWith(TAG_SEGUROS_ARMAZON) ){
                    segFrame = orderItem.articulo
                    typeEnsureF = "S"
                  } else if( !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) &&
                          StringUtils.trimToEmpty(orderItem.articulo.articulo).startsWith(TAG_SEGUROS_OFTALMICO) ){
                    segLens = orderItem.articulo
                    typeEnsureO = "L"
                    if( StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) ){
                      typeEnsureO = "N"
                    }
                  }
                }
              }
            }

            BigDecimal warrantyAmountLens = ItemController.warrantyValid( amountSegL, segLens.id )
            BigDecimal warrantyAmountFrame = ItemController.warrantyValid( amountSegF, segFrame.id )
            List<Integer> lstIdFrames = new ArrayList<>()
            List<Integer> lstIdLens = new ArrayList<>()
            for(DetalleNotaVenta detFrames : lstFrames){
              lstIdFrames.add(detFrames.idArticulo)
            }
            for(DetalleNotaVenta detLens : lstLens){
              lstIdLens.add(detLens.idArticulo)
            }
            if( warrantyAmountLens.compareTo(BigDecimal.ZERO) > 0 && segValid(segLens.id, lstIdLens) ){
              String items = ""
              amountSegL = BigDecimal.ZERO
              for(DetalleNotaVenta orderItem : lstLens){
                if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                  items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                  amountSegL = amountSegL.add( orderItem.precioUnitFinal )
                }
              }
              Warranty warranty = new Warranty()
              warranty.amount = amountSegL
              warranty.idItem = items.replaceFirst(",","")
              warranty.typeEnsure = typeEnsureO
              warranty.idOrder = addIdOrder ? nota.id : ""
              idOrderEnsured = StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ? StringUtils.trimToEmpty(idOrderPostEnsure) : idOrderEnsured
              //println idOrderEnsured
              lstWarranty.add( warranty )
              lstIdGar.clear()
            } else {
              MSJ_ERROR_WARRANTY = "Seguro Invalido."
              valid = false
            }

            if( warrantyAmountFrame.compareTo(BigDecimal.ZERO) > 0 && segValid(segFrame.id, lstIdFrames) ){
              String items = ""
              amountSegF = BigDecimal.ZERO
              for(DetalleNotaVenta orderItem : lstFrames){
                if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                  items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                  amountSegF = amountSegF.add( orderItem.precioUnitFinal )
                }
              }
              Warranty warranty = new Warranty()
              warranty.amount = amountSegF
              warranty.idItem = items.replaceFirst(",","")
              warranty.typeEnsure = typeEnsureF
              warranty.idOrder = addIdOrder ? nota.id : ""
              idOrderEnsured = StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ? StringUtils.trimToEmpty(idOrderPostEnsure) : idOrderEnsured
              //println idOrderEnsured
              lstWarranty.add( warranty )
              lstIdGar.clear()
            } else {
              MSJ_ERROR_WARRANTY = "Seguro Invalido."
              valid = false
            }
          } else {
            MSJ_ERROR_WARRANTY = "Seleccione solo un seguro."
            valid = false
          }
        } else if( hasC1 ) {
          MSJ_ERROR_WARRANTY = "No se puede asignar seguro a una redención."
          valid = false
        }
      } else if( cleanWaranties && lensKid ){
        insertSegKig = true
        /*panel.itemSearch.text = "SEG"
        panel.doItemSearch()*/
      } else {
        valid = true
      }
      if( cleanWaranties ){
        lstWarranty.clear()
      }
      return valid
    }



  static Boolean validWarranty( NotaVentaJava nota, Boolean cleanWaranties, OrderPanel panel, String idOrderPostEnsure, Boolean addIdOrder ){
    canceledWarranty = false
    if( StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ){
      postEnsure = StringUtils.trimToEmpty(idOrderPostEnsure)
    }
    NotaVentaJava oldNota = null
    if( StringUtils.trimToEmpty(postEnsure).length() > 0 ){
      oldNota = NotaVentaQuery.busquedaNotaById( postEnsure )
      if( oldNota != null ){
        nota.detalles.addAll( oldNota.detalles )
      }
    }
    Boolean valid = true
    Boolean applyValid = false
    List<Integer> lstIdGar = new ArrayList<>()
    List<Integer> lstIdArm = new ArrayList<>()
    BigDecimal totalAmount = BigDecimal.ZERO
    for(DetalleNotaVentaJava orderItem : nota.detalles){
      if( !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE) ){
        if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
          for(int i=0;i<orderItem.cantidadFac;i++){
            lstIdGar.add(orderItem.idArticulo)
          }
        } else {
          for(int i=0;i<orderItem.cantidadFac;i++){
            lstIdArm.add(orderItem.idArticulo)
            totalAmount = totalAmount.add(orderItem.precioUnitFinal)
          }
        }
      }
    }
    Boolean hasC1 = false
    for(PagoJava pago : nota.pagos){
      if(TAG_CUPON_SEGURO.equalsIgnoreCase(StringUtils.trimToEmpty(pago.idFPago))){
        hasC1 = true
      }
    }
    if( oldNota != null && StringUtils.trimToEmpty(oldNota.udf5).length() > 0 ){
      valid = false
    }
    Boolean sunglass = false
    Boolean lens = false
    Boolean ophtglass = false
    Boolean lensKid = false
    Boolean frame = false
    String typeEnsure = ""
    for(Integer idArt : lstIdArm ){
      ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId( idArt )
      if( articulo != null ){
        String type = StringUtils.trimToEmpty(articulo.subtipo).length() > 0 ? StringUtils.trimToEmpty(articulo.subtipo) : StringUtils.trimToEmpty(articulo.idGenSubtipo)
        if( StringUtils.trimToEmpty(type).startsWith(TAG_SUBTIPO_NINO) ){
          lensKid = true
        }
        if( StringUtils.trimToEmpty(articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
          frame = true
        }
        if( StringUtils.trimToEmpty(articulo.tipo).equalsIgnoreCase(TAG_TIPO_OFTALMICO) ){
          ophtglass = true
        } else if( StringUtils.trimToEmpty(articulo.tipo).equalsIgnoreCase(TAG_TIPO_SOLAR) ){
          sunglass = true
        } else if( StringUtils.trimToEmpty(articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_LENTE) ){
          lens = true
        }
      }
    }

    if( lstIdGar.size() > 0 ){
      if( valid && !hasC1 && totalAmount.compareTo(BigDecimal.ZERO) > 0 ){
        if( lstIdGar.size() == 1 ){
          List<DetalleNotaVentaJava> lstDets = new ArrayList<>()
          BigDecimal amount = BigDecimal.ZERO
          ArticulosJava warnt = articulosServiceJava.obtenerArticulo( lstIdGar.first() )
          String items = ""
          for(DetalleNotaVentaJava orderItem : nota.detalles){
            if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS)
                    && !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE) ){
              if( StringUtils.trimToEmpty(warnt.articulo).startsWith(TAG_SEGUROS_ARMAZON) ){
                if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                  amount = amount.add(orderItem.precioUnitLista)
                  items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                  lstDets.add( orderItem )
                }
                typeEnsure = "S"
              } else {
                if( StringUtils.trimToEmpty(warnt.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) ){
                  String type = StringUtils.trimToEmpty(orderItem.articulo.subtipo).length() > 0 ? StringUtils.trimToEmpty(orderItem.articulo.subtipo) : StringUtils.trimToEmpty(orderItem.articulo.idGenSubtipo)
                  if( StringUtils.trimToEmpty(type).startsWith(TAG_SUBTIPO_NINO) ||
                          !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
                    amount = amount.add(orderItem.precioUnitLista)
                    items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                    lstDets.add( orderItem )
                  }
                } else {
                  if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ||
                          (StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) &&
                                  StringUtils.trimToEmpty(orderItem.articulo.tipo).equalsIgnoreCase(TAG_TIPO_OFTALMICO)) ){
                    amount = amount.add(orderItem.precioUnitLista)
                    items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                    lstDets.add( orderItem )
                  }
                }
                if( StringUtils.trimToEmpty(warnt.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) ){
                  typeEnsure = "N"
                } else if( !StringUtils.trimToEmpty(warnt.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) &&
                        StringUtils.trimToEmpty(warnt.articulo).startsWith(TAG_SEGUROS_OFTALMICO) ){
                  typeEnsure = "L"
                }
              }
            }
          }
          BigDecimal warrantyAmount = ItemController.warrantyValidJava( amount, lstIdGar.first() )
          if( warrantyAmount.compareTo(BigDecimal.ZERO) > 0 && segValid(lstIdGar.first(), lstIdArm) ){
            amount = BigDecimal.ZERO
            for(DetalleNotaVentaJava orderItem : lstDets){
              if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS)
                      && !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE)){
                if( StringUtils.trimToEmpty(warnt.articulo).startsWith(TAG_SEGUROS_ARMAZON) ){
                  if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                    amount = amount.add( orderItem.precioUnitFinal )
                  }
                } else {
                  amount = amount.add( orderItem.precioUnitFinal )
                }
              }
            }
            Warranty warranty = new Warranty()
            warranty.amount = amount
            warranty.idItem = items.replaceFirst(",","")
            warranty.typeEnsure = typeEnsure
            warranty.idOrder = addIdOrder ? nota.idFactura : ""
            idOrderEnsured = StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ? StringUtils.trimToEmpty(idOrderPostEnsure) : idOrderEnsured
            //println idOrderEnsured
            lstWarranty.add( warranty )
            lstIdGar.clear()
          } else {
            MSJ_ERROR_WARRANTY = "Seguro Invalido."
            valid = false
          }
        } else if( lstIdGar.size() == 2 && frame && lens ) {
          List<DetalleNotaVentaJava> lstDetsL = new ArrayList<>()
          List<DetalleNotaVentaJava> lstDetsF = new ArrayList<>()
          BigDecimal amountSegL = BigDecimal.ZERO
          BigDecimal amountSegF = BigDecimal.ZERO
          List<DetalleNotaVentaJava> lstLens = new ArrayList<>()
          List<DetalleNotaVentaJava> lstFrames = new ArrayList<>()
          ArticulosJava segFrame = new ArticulosJava()
          ArticulosJava segLens = new ArticulosJava()
          String typeEnsureO = ""
          String typeEnsureF = ""
          for(DetalleNotaVentaJava orderItem : nota.detalles){
            if( !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE) ){
              if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                  if( StringUtils.trimToEmpty(orderItem.articulo.tipo).equalsIgnoreCase(TAG_TIPO_OFTALMICO) ){
                    amountSegL = amountSegL.add(orderItem.precioUnitLista)
                    lstLens.add( orderItem )
                  } else {
                    amountSegF = amountSegF.add(orderItem.precioUnitLista)
                    lstFrames.add( orderItem )
                  }
                } else {
                  amountSegL = amountSegL.add(orderItem.precioUnitLista)
                  lstLens.add( orderItem )
                }
              } else {
                if( StringUtils.trimToEmpty(orderItem.articulo.articulo).startsWith(TAG_SEGUROS_ARMAZON) ){
                                    segFrame = orderItem.articulo
                                    typeEnsureF = "S"
                } else if( !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) &&
                        StringUtils.trimToEmpty(orderItem.articulo.articulo).startsWith(TAG_SEGUROS_OFTALMICO) ){
                  segLens = orderItem.articulo
                  typeEnsureO = "L"
                  if( StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) ){
                    typeEnsureO = "N"
                  }
                }
              }
            }
          }

          BigDecimal warrantyAmountLens = ItemController.warrantyValidJava( amountSegL, segLens.idArticulo )
          BigDecimal warrantyAmountFrame = ItemController.warrantyValidJava( amountSegF, segFrame.idArticulo )
          List<Integer> lstIdFrames = new ArrayList<>()
          List<Integer> lstIdLens = new ArrayList<>()
          for(DetalleNotaVentaJava detFrames : lstFrames){
            lstIdFrames.add(detFrames.idArticulo)
          }
          for(DetalleNotaVentaJava detLens : lstLens){
            lstIdLens.add(detLens.idArticulo)
          }
          if( warrantyAmountLens.compareTo(BigDecimal.ZERO) > 0 && segValid(segLens.idArticulo, lstIdLens) ){
            String items = ""
            amountSegL = BigDecimal.ZERO
            for(DetalleNotaVentaJava orderItem : lstLens){
              if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                amountSegL = amountSegL.add( orderItem.precioUnitFinal )
              }
            }
            Warranty warranty = new Warranty()
            warranty.amount = amountSegL
            warranty.idItem = items.replaceFirst(",","")
            warranty.typeEnsure = typeEnsureO
            warranty.idOrder = addIdOrder ? nota.idFactura : ""
            idOrderEnsured = StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ? StringUtils.trimToEmpty(idOrderPostEnsure) : idOrderEnsured
            //println idOrderEnsured
            lstWarranty.add( warranty )
            lstIdGar.clear()
          } else {
            MSJ_ERROR_WARRANTY = "Seguro Invalido."
            valid = false
          }

          if( warrantyAmountFrame.compareTo(BigDecimal.ZERO) > 0 && segValid(segFrame.idArticulo, lstIdFrames) ){
            String items = ""
            amountSegF = BigDecimal.ZERO
            for(DetalleNotaVentaJava orderItem : lstFrames){
              if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                amountSegF = amountSegF.add( orderItem.precioUnitFinal )
              }
            }
            Warranty warranty = new Warranty()
            warranty.amount = amountSegF
            warranty.idItem = items.replaceFirst(",","")
            warranty.typeEnsure = typeEnsureF
            warranty.idOrder = addIdOrder ? nota.idFactura : ""
            idOrderEnsured = StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ? StringUtils.trimToEmpty(idOrderPostEnsure) : idOrderEnsured
            //println idOrderEnsured
            lstWarranty.add( warranty )
            lstIdGar.clear()
          } else {
            MSJ_ERROR_WARRANTY = "Seguro Invalido."
            valid = false
          }
        } else {
          MSJ_ERROR_WARRANTY = "Seleccione solo un seguro."
          valid = false
        }
      } else if( hasC1 ) {
        MSJ_ERROR_WARRANTY = "No se puede asignar seguro a una redención."
        valid = false
      } else if( totalAmount.compareTo(BigDecimal.ZERO) <= 0 ) {
        MSJ_ERROR_WARRANTY = "No se puede asignar seguro a una nota con monto \$0.00"
        valid = false
      }
    } else if( cleanWaranties && lensKid ){
      insertSegKig = true
    } else {
            valid = true
    }
    if( cleanWaranties ){
            lstWarranty.clear()
    }
    return valid
    }



  private static Boolean segValid(Integer itemWarr, List<Integer> items ){
    Boolean valid = true
    Boolean frame = false
    Boolean sunglass = false
    Boolean ophtalmic = false
    Boolean lens = false
    Boolean lensKid = false
    if( itemWarr != null ){
      ArticulosJava itemWarranty = ArticulosQuery.busquedaArticuloPorId( itemWarr )
      for(Integer id : items){
        ArticulosJava item = ArticulosQuery.busquedaArticuloPorId( id )
        if( StringUtils.trimToEmpty(item.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
          frame = true
        }
        String type = StringUtils.trimToEmpty(item.subtipo).length() > 0 ? StringUtils.trimToEmpty(item.subtipo) : StringUtils.trimToEmpty(item.idGenSubtipo)
        if( StringUtils.trimToEmpty(type).startsWith(TAG_SUBTIPO_NINO) ){
          lensKid = true
        } else if( StringUtils.trimToEmpty(item.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
          if( StringUtils.trimToEmpty(item.tipo).equalsIgnoreCase(TAG_TIPO_SOLAR) ){
            sunglass = true
          } else if( StringUtils.trimToEmpty(item.tipo).equalsIgnoreCase(TAG_TIPO_OFTALMICO) ){
            ophtalmic = true
          }
        } else if( StringUtils.trimToEmpty(item.idGenerico).equalsIgnoreCase(TAG_GENERICO_LENTE) ){
          lens = true
        }
      }
      if( StringUtils.trimToEmpty(itemWarranty.articulo).startsWith(TAG_SEGUROS_ARMAZON) && !sunglass ){
        valid = false
      } else if( StringUtils.trimToEmpty(itemWarranty.articulo).startsWith(TAG_SEGUROS_OFTALMICO) && (!lens && !ophtalmic) ){
        valid = false
      }
      if( StringUtils.trimToEmpty(itemWarranty.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) && lensKid ){
        valid = true
      } else if(StringUtils.trimToEmpty(itemWarranty.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) && !lensKid) {
        valid = false
      }
    } else {
      valid = false
    }
    return valid
  }


  static Boolean keyFree( String key ){
    return RepositoryFactory.discounts.findByClave( key ).size() <= 0
  }


  static List<Order> findOrdersByDate( Date date ){
      List<Order> lstOrders = new ArrayList<>()
      List<NotaVenta> lstNotas = notaVentaService.obtenerNotaVentaPorFecha( date )
      for( NotaVenta nv : lstNotas ){
        if( nv.codigo_lente != null && nv.codigo_lente.contains("@") ){
          lstOrders.add( Order.toOrder(nv) )
        }
      }
      return  lstOrders
  }



  static NotaVentaJava ensureOrder( String idOrder ){
    //NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( idOrder )
    NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById( idOrder )
    if( notaVenta != null ){
      Boolean warranty = true
      for( DetalleNotaVentaJava det : notaVenta.detalles ){
        if( !StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
          warranty = false
        }
      }
      if( warranty && notaVenta?.detalles?.size() > 0 ){
        AseguraNotaDialog dialog = new AseguraNotaDialog()
        dialog.show()
        if( dialog.notaVenta != null ){
          notaVenta = dialog.notaVenta
        }
      } else {
        notaVenta = new NotaVentaJava()
      }
    }
    return  notaVenta
  }



  static Boolean validaAplicaGarantia(String idFactura) {
    Boolean valid = true
    //NotaVenta notaVenta = notaVentaService.obtenerNotaVentaPorTicket( StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+idFactura )
    NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaByFactura( StringUtils.trimToEmpty(idFactura) )
    Boolean noDelivered = true
    Boolean noCancelled = true
    Boolean noEnsured = true
    if( notaVenta != null ){
      if( notaVenta.fechaEntrega != null ){
        if( !notaVenta.fechaEntrega.format("dd/MM/yyyy").equalsIgnoreCase(new Date().format("dd/MM/yyyy")) ){
          noDelivered = false
        }
        if( StringUtils.trimToEmpty(notaVenta.sFactura).equalsIgnoreCase("T") ){
          noCancelled = false
        }
        for(DetalleNotaVentaJava det : notaVenta.detalles){
          if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase("J") ){
            noEnsured = false
          }
        }
      }
    }
    if( !noDelivered || !noCancelled || !noEnsured ){
      valid = false
    }
    return valid
  }


  static void reprintEnsure( NotaVentaJava notaVenta ){
    if( notaVenta.fechaEntrega != null ) {
      if(validEnsureDateAplication(notaVenta)){
        if( validWarranty( notaVenta, false, null, "", false ) ){
          Boolean doubleEnsure = lstWarranty.size() > 1
          for(Warranty warranty : lstWarranty){
            ItemController.printWarranty( warranty.amount, warranty.idItem, warranty.typeEnsure, StringUtils.trimToEmpty(notaVenta.idFactura), doubleEnsure )
          }
          lstWarranty.clear()
        }
      }
    } else {
      JOptionPane.showMessageDialog(null, "La nota no ha sido entregada. No se puede reimprimir el seguro.")
    }
  }


  static void genreatedEntranceSP ( String idOrder ){
    NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( idOrder )
    if( notaVenta != null ){

    }
  }


  static List<DevBank> findDevBanks( ){
    List<DevBank> lstBanks = new ArrayList<>()
    List<BancoDev> lstBancos = bancoDevRepository.findAll( )
    Collections.sort(lstBancos, new Comparator<BancoDev>() {
        @Override
        int compare(BancoDev o1, BancoDev o2) {
            return o1.nombre.compareTo(o2.nombre)
        }
    })
    lstBanks.add( new DevBank() )
    lstBancos.each { BancoDev tmp -> lstBanks.add( DevBank.toDevBank(tmp) ) }
    return lstBanks
  }



  static Boolean validEnsureDateAplication( NotaVentaJava notaVenta ){
    Boolean valid = false
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy")
    Date limitDate = new Date()
    try{
      limitDate = df.parse( Registry.validEnsureDate )
    } catch ( Exception e ){ println e }
    if( notaVenta.fechaHoraFactura.compareTo(limitDate) >= 0 ){//&& new Date().compareTo(limitDate) >= 0){
      valid = true
    }
    return valid
  }


  static Descuento findClaveApplied( String clave ) {
    List<Descuento> desc = descuentoRepository.findByClave( StringUtils.trimToEmpty( clave ) )
    if( desc.size() > 0 ){
      return  desc.first()
    }
    return  null
  }



  static String validCrmClaveWeb( String clave ){
    log.debug( "validCrmClaveWeb( )" )
    String claveFree = notaVentaService.validaClaveCrmWeb( clave )
    return claveFree
  }


  static void saveAcuseCrmClave( String idOrder ){
    log.debug( "saveAcuseCrmClave( )" )
    NotaVentaServiceJava.guardaAcuseClaveCrm( idOrder )
  }

  static Boolean changeIpBox( String ip ){
    return notaVentaService.cambiaIpCaja( ip )
  }


  static void deleteOrder( String idOrder ){
    notaVentaService.borrarNotaVenta( idOrder )
  }

  static void addLogOrderCancelled( String idOrder, String idEmployee ){
    notaVentaService.agregarLogNotaAnulada( idOrder, idEmployee )
  }

  static Boolean validOrderNotCancelled( String idOrder ){
    //return notaVentaService.validaNotaNoAnulada( idOrder )
    AutorizaMovJava autorizaMov = AutorizaMovQuery.buscaAutorizaMovPorFactura( idOrder )
    if( autorizaMov != null ){
      return false
    } else{
      return true
    }
  }


  static List<OrderToCancell> findOrdersToCancell(){
    List<OrderToCancell> lstOrders = new ArrayList<>()
    List<NotaVenta> lstNotas = notaVentaService.obtenerNotasPorCancelar()
    for( NotaVenta nota : lstNotas ){
      OrderToCancell orderToCancell = new OrderToCancell()
      orderToCancell.idOrder = StringUtils.trimToEmpty(nota.id)
      orderToCancell.client = StringUtils.trimToEmpty(nota.cliente.nombreCompleto)
      orderToCancell.discount = nota.desc != null ? StringUtils.trimToEmpty(nota.desc.clave) : ""
      lstOrders.add( orderToCancell )
    }
    return lstOrders
  }


  static BigDecimal amountPromoAge( String idOrder ){
    BigDecimal monto = BigDecimal.ZERO
    NotaVenta nota = notaVentaService.obtenerNotaVenta( idOrder )
    if( nota != null ){
      BigDecimal montoParcial = BigDecimal.ZERO
      Boolean montoValido = false
      Boolean hasSV = false
      Boolean hasMF = false
      Boolean hasFrame = false
      for(DetalleNotaVenta det : nota.detalles){
        if( StringUtils.trimToEmpty(det?.articulo?.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
          hasFrame = true
        }
        if( !StringUtils.trimToEmpty(det?.articulo?.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEG) ){
          montoParcial = montoParcial.add(det.precioUnitFinal.multiply(det.cantidadFac))
        }
        if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_LENTE) ){
          if( StringUtils.trimToEmpty(det.articulo.articulo).equalsIgnoreCase("SV") ){
            hasSV = true
          } else if( StringUtils.trimToEmpty(det.articulo.articulo).equalsIgnoreCase("B") ||
                  StringUtils.trimToEmpty(det.articulo.articulo).equalsIgnoreCase("P") ){
            hasMF = true
          }
        }
      }
      if( montoParcial.compareTo(Registry.validAmountPromoAge) >= 0 ){
        montoValido = true
      }
      if( nota?.idCliente != Registry.genericCustomer && montoValido && hasFrame && (hasSV || hasMF) ){
        if( nota.cliente != null && nota?.cliente?.fechaNacimiento != null ){
          Date fechaActual = new Date();
          SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
          String hoy = formato.format(fechaActual);
          String[] dat1 = formato.format(nota?.cliente?.fechaNacimiento).split("/");
          String[] dat2 = hoy.split("/");
          Integer anos = Integer.parseInt(dat2[2]) - Integer.parseInt(dat1[2]);
          Integer mes = Integer.parseInt(dat2[1]) - Integer.parseInt(dat1[1]);
          if (mes < 0) {
              anos = anos - 1;
          } else if (mes == 0) {
            int dia = Integer.parseInt(dat2[0]) - Integer.parseInt(dat1[0]);
            if (dia < 0) {
              anos = anos - 1;
            }
          }
          if( hasSV ){
            monto = new BigDecimal( anos*Registry.amountPromoAgeMonofocal )
          } else if( hasMF ){
            monto = new BigDecimal( anos*Registry.amountPromoAgeMultifocal )
          }
        }
      }
    }
    return monto
  }


  static Boolean canApplyDiscountAge( Order order ) {
    Boolean smthApply = false
    if( StringUtils.trimToEmpty(order.id).length() > 0 ){
      Calendar cal = Calendar.getInstance();
      cal.set(cal.get(Calendar.YEAR),
      cal.getMinimum(Calendar.MONTH),
      cal.getMinimum(Calendar.DAY_OF_YEAR),
      cal.getMinimum(Calendar.HOUR_OF_DAY),
      cal.getMinimum(Calendar.MINUTE),
      cal.getMinimum(Calendar.SECOND));
      Date fechaStart = cal.getTime()
      Date fechaEnd = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 )
      List<NotaVentaJava> lstNotasClient = NotaVentaQuery.busquedaNotaByIdClienteAndDate(order.customer.id, fechaStart, fechaEnd)
      for(NotaVentaJava nv : lstNotasClient){
        List<DescuentosJava> descuento = DescuentosQuery.buscaDescuentosPorIdFactura(nv.idFactura)
        for(DescuentosJava desc : descuento){
          if(StringUtils.trimToEmpty(desc.clave).equalsIgnoreCase(TAG_CLAVE_DESCUENTO_EDAD)){
            smthApply = true
          }
        }
      }
    }
    return smthApply
  }


  static String couponKeyValid( String couponKey, Order order ) {
    log.debug("couponKeyValid ( "+ couponKey+" )")
    String montoMinimo = ""
    DescuentoClave descuentoClave = descuentoClaveRepository.descuentoClave( StringUtils.trimToEmpty(couponKey) )
    BigDecimal minimumAmount = BigDecimal.ZERO
    if( descuentoClave != null ){
      if( descuentoClave.getMontoMinimo() != null && descuentoClave.getMontoMinimo().compareTo(BigDecimal.ZERO) > 0 ){
        minimumAmount = descuentoClave.getMontoMinimo()
      } else {
        minimumAmount = Registry.minimunAmountAgreement
      }
    }
      BigDecimal totalOrder = BigDecimal.ZERO
      NotaVentaJava nota = NotaVentaQuery.busquedaNotaById( order.id )
      if( nota != null ){
          for(DetalleNotaVentaJava det : nota.detalles){
            if( !StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEG) ){
              totalOrder = totalOrder.add(det.precioUnitLista.multiply(new BigDecimal(det.cantidadFac)))
            }
          }
          //println totalOrder
          if( totalOrder.compareTo(minimumAmount) < 0 ){
              montoMinimo = NumberFormat.getCurrencyInstance(Locale.US).format(minimumAmount)
          }
      }
    return montoMinimo
  }


  static Boolean validMinimumAmountCrmByParameter( BigDecimal couponAmount, NotaVenta notaVenta ){
    Boolean valid = true
    BigDecimal totalAmount = BigDecimal.ZERO
    String amounts = StringUtils.trimToEmpty(Registry.minimunAmountCrm)
    if( amounts.length() > 0 ){
      for(DetalleNotaVenta detalleNotaVenta : notaVenta.detalles){
        if( !detalleNotaVenta.articulo.idGenerico.equalsIgnoreCase(TAG_GENERICO_SEG) ){
          totalAmount = totalAmount.add(detalleNotaVenta.precioUnitFinal)
        }
      }
      String[] ranges = amounts.split(",")
      for(String range : ranges){
        String[] data = StringUtils.trimToEmpty(range).split(":")
        if( data.length > 1 && data[0].toString().isNumber() && data[1].toString().isNumber() ){
          Double discount = 0.00
          Double minimumAmount = 0.00
          try{
            discount = NumberFormat.getInstance().parse(data[0]).doubleValue()
            minimumAmount = NumberFormat.getInstance().parse(data[1]).doubleValue()
          } catch ( NumberFormatException e ){
            println e
          }
          if( discount == couponAmount ){
            if( totalAmount.doubleValue() < minimumAmount ){
              valid = false
            }
          }
        }
      }
    }
    return  valid
  }


  static Boolean validMinimumAmountCrm( BigDecimal minimunAmount, NotaVenta notaVenta ){
    Boolean valid = true
    BigDecimal totalAmount = BigDecimal.ZERO
    for(DetalleNotaVenta detalleNotaVenta : notaVenta.detalles){
      if( !detalleNotaVenta.articulo.idGenerico.equalsIgnoreCase(TAG_GENERICO_SEG) ){
        totalAmount = totalAmount.add(detalleNotaVenta.precioUnitFinal)
      }
    }
    if( totalAmount.doubleValue() < minimunAmount ){
      valid = false
    }
    return  valid
  }


  static List<PromocionJava> findCrmPromotions( ){
    List<PromocionJava> lstPromotions = new ArrayList<>();
    List<PromocionJava> lstPromotionsCrm = PromocionQuery.buscaPromocionesCrm()
    for(PromocionJava promocionJava : lstPromotionsCrm){
      /*String[] data = StringUtils.trimToEmpty(promocionJava.descripcion).split(":")
      if( data.length > 1 ){
        String clave = StringUtils.trimToEmpty(data[1])
        if(DescuentosQuery.buscaDescuentoPorClave(StringUtils.trimToEmpty(clave)) == null){*/
          lstPromotions.add(promocionJava)
        //}
      //}
    }
    return lstPromotions
  }


  static PromocionJava findCrmPromotionByKey( String key ){
    PromocionJava promotion = new PromocionJava()
    List<PromocionJava> lstPromotionsCrm = PromocionQuery.buscaPromocionesCrm()
    for(PromocionJava promocionJava : lstPromotionsCrm){
      String[] data = StringUtils.trimToEmpty(promocionJava.descripcion).split(":")
      if( data.length > 1 ){
        String keyP = StringUtils.trimToEmpty(data[1].toString()).substring(0,4)
        String keyF = StringUtils.trimToEmpty(key).substring(0,4)
        if( StringUtils.trimToEmpty(keyF).equalsIgnoreCase(keyP) ){
          promotion = promocionJava
        }
      }
    }
    return promotion
  }


  static Boolean validRxData( String idOrder, String dioptra ) {
    Boolean valid = true
    NotaVentaJava notaVentaJava = NotaVentaQuery.busquedaNotaById( idOrder )
    RecetaJava rx = null
    if( notaVentaJava.receta != null ){
      rx = RecetaQuery.buscaRecetaPorIdReceta( notaVentaJava.receta )
    }
    if( rx != null && rx.idReceta != null ){
      Double esfDer = 0.00
      Double cilDer = 0.00
      Double esfIz = 0.00
      Double cilIz = 0.00
      try{
        String esfDerStr = rx.odEsfR.replace("+","")
        //esfDerStr = esfDerStr.replace("-","")
        String cilDerStr = rx.odCilR.replace("+","")
        //cilDerStr = cilDerStr.replace("-","")
        String esfIzStr = rx.oiEsfR.replace("+","")
        //esfIzStr = esfIzStr.replace("-","")
        String cilIzStr = rx.oiCilR.replace("+","")
        //cilIzStr = cilIzStr.replace("-","")
        esfDer = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(esfDerStr).length() > 0 ? StringUtils.trimToEmpty(esfDerStr) : "0").doubleValue()
        cilDer = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(cilDerStr).length() > 0 ? StringUtils.trimToEmpty(cilDerStr) : "0").doubleValue()
        esfIz = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(esfIzStr).length() > 0 ? StringUtils.trimToEmpty(esfIzStr) : "0").doubleValue()
        cilIz = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(cilIzStr).length() > 0 ? StringUtils.trimToEmpty(cilIzStr) : "0").doubleValue()
      } catch ( NumberFormatException e ) { println e }

      String dataLimits = Registry.limitGraduation
      String[] data = StringUtils.trimToEmpty(dataLimits).split(",")
      for(String d : data){
        String[] dataTmp = StringUtils.trimToEmpty(d).split(":")
        if( dataTmp.length >= 3 ){
          Double firstLimit = 0.00
          Double secondLimit = 0.00
          try{
            firstLimit = NumberFormat.getInstance().parse(dataTmp[1])
            secondLimit = NumberFormat.getInstance().parse(dataTmp[2])
          } catch ( NumberFormatException e ) { println e }

          if( StringUtils.trimToEmpty(dioptra).startsWith(dataTmp[0]) ){
            Boolean esferaVaild = true
            if( esfDer < 0 ){
              if( esfDer < secondLimit.doubleValue()*-1 ){
                esferaVaild = false
              }
            } else {
              if( esfDer > secondLimit ){
                esferaVaild = false
              }
            }
            if( esfIz < 0 ){
              if( esfIz < secondLimit.doubleValue()*-1 ){
                esferaVaild = false
              }
            } else {
              if( esfIz > secondLimit ){
                esferaVaild = false
              }
            }
            if( esfDer > firstLimit || esfIz > firstLimit || esfDer.abs()+cilDer.abs() > secondLimit || esfIz.abs()+cilIz.abs() > secondLimit ){
              valid = false
            }
          }
        }
      }
      /*if( StringUtils.trimToEmpty(dioptra).startsWith("C") ){
        if( esfDer > 6 || esfIz > 6 || esfDer+cilDer > 6 || esfIz+cilIz > 6 ){
          valid = false
        }
      } else if( StringUtils.trimToEmpty(dioptra).startsWith("P") ){
        if( esfDer > 8 || esfIz > 8 || esfDer+cilDer > 12 || esfIz+cilIz > 12 ){
          valid = false
        }
      } else if( StringUtils.trimToEmpty(dioptra).startsWith("H") ){
        if( esfDer > 10 || esfIz > 10 || esfDer+cilDer > 16 || esfIz+cilIz > 16 ){
          valid = false
        }
      }*/
    }
    return valid
  }



  static Boolean reclassifyFrame(Order order, Item oldItem, Item newItem) {
    Boolean reclassified = true
    try{
      Integer cantidad = 0
      NotaVentaJava notaVentaJava = NotaVentaQuery.busquedaNotaById( StringUtils.trimToEmpty(order.id) )
      if( notaVentaJava != null ){
            for( DetalleNotaVentaJava det : notaVentaJava.detalles){
                if( det.idArticulo == oldItem.id){
                    det.idArticulo = newItem.id
                    DetalleNotaVentaQuery.updateArtiuloDetalleNotaVenta( det, oldItem.id )
                }
            }
      }
      List<TransInv> transInv = transInvRepository.findByReferencia( StringUtils.trimToEmpty(notaVentaJava.idFactura) )
      for(TransInv tr : transInv){
            QTransInvDetalle qTransInvDetalle = QTransInvDetalle.transInvDetalle
            List<TransInvDetalle> transInvDet = transInvDetalleRepository.findAll( qTransInvDetalle.idTipoTrans.eq(StringUtils.trimToEmpty(tr.idTipoTrans)).
                    and(qTransInvDetalle.folio.eq(tr.folio))) as List<TransInvDetalle>
            for(TransInvDetalle trDet : transInvDet){
                /*if( trDet.idTipoTrans.equalsIgnoreCase(TAG_TRANSACCION_VENTA) || trDet.idTipoTrans.equalsIgnoreCase( TAG_TRANSACCION_CANCELACION) ||
                        trDet.idTipoTrans.equalsIgnoreCase( TAG_TRANSACCION_REM_SP) || trDet.idTipoTrans.equalsIgnoreCase( TAG_TRANSACCION_SALIDA)){*/
                    if( trDet.sku == oldItem.id ){
                        ArticulosJava articuloViejo = ArticulosQuery.busquedaArticuloPorId( oldItem.id )
                        ArticulosJava articuloNuevo = ArticulosQuery.busquedaArticuloPorId( newItem.id )
                        if( trDet.tipoMov.equalsIgnoreCase(TAG_TRANSACCION_S) ){
                            if( articuloViejo != null && articuloNuevo != null ){
                                articuloViejo.existencia = articuloViejo.existencia+trDet.cantidad
                                articuloNuevo.existencia = articuloNuevo.existencia-trDet.cantidad
                            }
                        } else if( trDet.tipoMov.equalsIgnoreCase(TAG_TRANSACCION_ENTRADA) ){
                            articuloViejo.existencia = articuloViejo.existencia-trDet.cantidad
                            articuloNuevo.existencia = articuloNuevo.existencia+trDet.cantidad
                        }
                        ArticulosQuery.saveOrUpdateArticulos( articuloViejo )
                        ArticulosQuery.saveOrUpdateArticulos( articuloNuevo )
                        trDet.sku = newItem.id
                        transInvDetalleRepository.save( trDet )
                        transInvDetalleRepository.flush()
                    }
                //}
            }
      }
    } catch ( Exception e ){
      reclassified = false
      print e.message
    }
    return reclassified
  }



  static String obtieneDioptra(String idOrder) {
    String dioptra = ""
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById( StringUtils.trimToEmpty(idOrder) )
    if( nota != null && StringUtils.trimToEmpty(nota.codigoLente).length() > 0 ){
      dioptra = StringUtils.trimToEmpty(nota.codigoLente)
    }
    return dioptra
  }


  static Boolean validRxDataByParam( Rx rx, String dioptra ) {
    Boolean valid = true
    if( rx != null ){
            Double esfDer = 0.00
            Double cilDer = 0.00
            Double esfIz = 0.00
            Double cilIz = 0.00
            try{
                String esfDerStr = rx.odEsfR.replace("+","")
                //esfDerStr = esfDerStr.replace("-","")
                String cilDerStr = rx.odCilR.replace("+","")
                //cilDerStr = cilDerStr.replace("-","")
                String esfIzStr = rx.oiEsfR.replace("+","")
                //esfIzStr = esfIzStr.replace("-","")
                String cilIzStr = rx.oiCilR.replace("+","")
                //cilIzStr = cilIzStr.replace("-","")
                esfDer = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(esfDerStr).length() > 0 ? StringUtils.trimToEmpty(esfDerStr) : "0").doubleValue()
                cilDer = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(cilDerStr).length() > 0 ? StringUtils.trimToEmpty(cilDerStr) : "0").doubleValue()
                esfIz = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(esfIzStr).length() > 0 ? StringUtils.trimToEmpty(esfIzStr) : "0").doubleValue()
                cilIz = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(cilIzStr).length() > 0 ? StringUtils.trimToEmpty(cilIzStr) : "0").doubleValue()
            } catch ( NumberFormatException e ) { println e }

            String dataLimits = Registry.limitGraduation
            String[] data = StringUtils.trimToEmpty(dataLimits).split(",")
            for(String d : data){
                String[] dataTmp = StringUtils.trimToEmpty(d).split(":")
                if( dataTmp.length >= 3 ){
                    Double firstLimit = 0.00
                    Double secondLimit = 0.00
                    try{
                        firstLimit = NumberFormat.getInstance().parse(dataTmp[1])
                        secondLimit = NumberFormat.getInstance().parse(dataTmp[2])
                    } catch ( NumberFormatException e ) { println e }

                    if( StringUtils.trimToEmpty(dioptra).startsWith(dataTmp[0]) ){
                        Boolean esferaVaild = true
                        if( esfDer < 0 ){
                            if( esfDer < secondLimit.doubleValue()*-1 ){
                                esferaVaild = false
                            }
                        } else {
                            if( esfDer > secondLimit ){
                                esferaVaild = false
                            }
                        }
                        if( esfIz < 0 ){
                            if( esfIz < secondLimit.doubleValue()*-1 ){
                                esferaVaild = false
                            }
                        } else {
                            if( esfIz > secondLimit ){
                                esferaVaild = false
                            }
                        }
                        if( esfDer > firstLimit || esfIz > firstLimit || esfDer.abs()+cilDer.abs() > secondLimit || esfIz.abs()+cilIz.abs() > secondLimit ){
                            valid = false
                        }
                    }
                }
            }
    }
    return valid
  }


  static List<JbJava> jbBySend( ) {
    List<JbJava> lstJb = JbQuery.buscarJbPorEstado( "PE" )
    return lstJb
  }


  static Rx findRxByBill(String bill) {
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaByFactura( StringUtils.trimToEmpty(bill) )
    RecetaJava receta = null
    if( nota != null && nota.receta != null ){
      receta = RecetaQuery.buscaRecetaPorIdReceta( nota.receta )
    }
    return Rx.toRx(receta)
  }


  static void validSPWithoutLens( Order order ){
    NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(order.id)
    detalleNotaVentaServiceJava.validaSPSinLente( notaVenta )
  }



  static void reschedule( String rx, Date date ){
    JbJava jb = JbQuery.buscarPorRx( rx )
    if( jb != null ){
      jb.volverLlamar = date
      JbQuery.updateJb( jb )
    }
  }


  static void stopContact( String rx, String observations ){
    User user = Session.get( SessionItem.USER ) as User
    JbJava jb = JbQuery.buscarPorRx( rx )
    if( jb != null ){
      mx.lux.pos.java.repository.JbTrack jbTrack = new mx.lux.pos.java.repository.JbTrack()
      jbTrack.rx = rx
      jbTrack.estado = "NT"
      jbTrack.obs = observations
      jbTrack.emp = user.username
      jbTrack.idViaje = ''
      jbTrack.fecha = new Date()
      jbTrack.idMod = '0'
      JbQuery.saveJbTrack( jbTrack )

      JbQuery.eliminaJbLLamada( rx )

      jb.noLlamar = true
      JbQuery.updateJb(jb)
    }
  }
}