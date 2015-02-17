package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.model.*
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

    private static Boolean insertSegKig

    private static List<Warranty> lstWarranty = new ArrayList<>()
    private static String idOrderEnsured

    private static NotaVentaService notaVentaService
    private static DetalleNotaVentaService detalleNotaVentaService
    private static PagoService pagoService
    private static TicketService ticketService
    private static BancoService bancoService
    private static InventarioService inventarioService
    private static MonedaExtranjeraService fxService
    private static Boolean displayUsd
    private static PromotionService promotionService
    private static CancelacionService cancelacionService
    private static RecetaService recetaService
    private static ExamenService examenService
    private static ArticuloService articuloService
    private static CotizacionService cotizacionService
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
            NotaVentaRepository notaVentaRepository

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
    }

    private static Boolean canceledWarranty
    private static String postEnsure

    static Order getOrder(String orderId) {
        log.info("obteniendo orden id: ${orderId}")
        NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(orderId)
        Order order = Order.toOrder(notaVenta)
        if (StringUtils.isNotBlank(order?.id)) {
            order.items?.clear()
            List<DetalleNotaVenta> detalles = detalleNotaVentaService.listarDetallesNotaVentaPorIdFactura(orderId)
            detalles?.each { DetalleNotaVenta tmp ->
                order.items?.add(OrderItem.toOrderItem(tmp))
                order.due
            }
            order.payments?.clear()
            List<Pago> pagos = pagoService.listarPagosPorIdFactura(orderId)
            pagos?.each { Pago tmp ->
                Payment paymentTmp = Payment.toPaymment(tmp)
                if (tmp?.idBancoEmisor?.integer) {
                    BancoEmisor banco = bancoService.obtenerBancoEmisor(tmp?.idBancoEmisor?.toInteger())
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
        NotaVenta notaVenta = notaVentaService.abrirNotaVenta(clienteID, empID)
        return Order.toOrder(notaVenta)
    }

    static Item findArt(String dioptra) {

        Articulo art = articuloService.findbyName(dioptra)

        return Item.toItem(art)
    }

    static Receta findRx(Order order, Customer customer) {
        NotaVenta rxNotaVenta = notaVentaService.obtenerNotaVenta(order?.id)
        List<Rx> recetas = CustomerController.findAllPrescriptions(customer?.id)
        Receta receta = new Receta()
        Iterator iterator = recetas.iterator();
        while (iterator.hasNext()) {
            Rx rx = iterator.next()
            if (rxNotaVenta.receta == rx?.id) {
                rxNotaVenta.receta
                receta = recetaService.findbyId(rxNotaVenta.receta)
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
        NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(idNotaVenta)
        notaVentaService.saveProDate(notaVenta, fechaPrometida)

    }

    static void saveRxOrder(String idNotaVenta, Integer receta) {
        log.debug( "guardando receta ${receta}" )
        println 'receta con error'+receta
        NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(idNotaVenta)
        notaVentaService.saveRx(notaVenta, receta)
    }

    static Order saveFrame(String idNotaVenta, String opciones, String forma) {

        NotaVenta notaVenta = notaVentaService.saveFrame(idNotaVenta, opciones, forma)

        return Order.toOrder(notaVenta)
    }

    static Dioptra addDioptra(Order order, String dioptra) {
        log.debug( "addDioptra( )" )
        NotaVenta nota = notaVentaService.obtenerNotaVenta(order.id)
        nota.setCodigo_lente(dioptra)
        nota = notaVentaService.registrarNotaVenta(nota)
        Dioptra diop = generaDioptra(preDioptra(nota.codigo_lente))
        println('Codigo Lente: ' + nota.codigo_lente)
        return diop
    }

    static String preDioptra(String dioString) {
        String preDioptra
        //try{
        if (!dioString.equals(null)) {
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
        if (dioString == null) {
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
            orderId = (notaVentaService.obtenerNotaVenta(orderId) ? orderId : openOrder(clienteID, empleadoID)?.id)
            NotaVenta nota = notaVentaService.obtenerNotaVenta(orderId)
            DetalleNotaVenta detalle = null
            if (item.isManualPriceItem()) {
                String rmks = nota.observacionesNv+nota.observacionesNv.trim().length() <= 0 ? order.comments : ''
                ManualPriceDialog dlg = ManualPriceDialog.instance
                dlg.item = item
                dlg.remarks = rmks
                dlg.activate()
                if (dlg.itemAccepted) {
                    item.listPrice = item.price
                    detalle = new DetalleNotaVenta(
                            idArticulo: item.id,
                            cantidadFac: 1,
                            precioUnitLista: item.listPrice,
                            precioUnitFinal: item.price,
                            precioCalcLista: item.listPrice,
                            precioFactura: item.price,
                            precioCalcOferta: 0,
                            precioConv: 0,
                            idTipoDetalle: 'N',
                            surte: item?.type.trim().equalsIgnoreCase('B') ? 'P' : surte,
                    )
                    nota.observacionesNv = dlg.remarks

                    notaVentaService.registrarNotaVenta(nota)
                }
            } else {
                if(!TAG_GENERICOS_INVENTARIABLES.contains(item.type)){
                    surte = ' '
                }
                detalle = new DetalleNotaVenta(
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
                nota = notaVentaService.registrarDetalleNotaVentaEnNotaVenta(orderId, detalle)
            }
            if (nota != null ) {
                notaVentaService.registraImpuestoPorFactura( nota )
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
            NotaVenta notaVenta = notaVentaService.eliminarDetalleNotaVentaEnNotaVenta(orderId, orderItem.item.id)
            if (notaVenta?.id) {
                NotaVenta nota = notaVentaService.obtenerNotaVenta(orderId)
                Order o = new Order()
                Articulo i = articuloService.obtenerArticulo(orderItem?.item?.id.toInteger())

                if (!i?.indice_dioptra.equals(null)) {
                    if(StringUtils.trimToEmpty(i?.idGenerico).equalsIgnoreCase(TAG_GENERICO_B)){
                      nota.receta = null
                      notaVentaRepository.save( nota )
                    }
                    Dioptra actDioptra = validaDioptra(generaDioptra(preDioptra(nota.codigo_lente)), generaDioptra(i.indice_dioptra))
                    o = Order.toOrder(notaVenta)

                    actDioptra = addDioptra(o, codigoDioptra(actDioptra))

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
            NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(order.id)
            if (StringUtils.isNotBlank(notaVenta?.id)) {

              if (StringUtils.trimToEmpty(notaVenta.idEmpleado).length() <= 0) {
                notaVenta.idEmpleado = idEmpleado
              }
                if (notaVenta.idCliente != null) {
                    notaVenta.idCliente = order.customer.id
                }
                if( StringUtils.trimToNull(order.dioptra) == null ){
                    notaVenta.codigo_lente = null
                }
                notaVenta.observacionesNv = order.comments
                //notaVenta.empEntrego = user?.username
                notaVenta.udf4 = isMultypayment ? "M" : ""
                notaVenta = notaVentaService.cerrarNotaVenta(notaVenta)
                if (inventarioService.solicitarTransaccionVenta(notaVenta)) {
                    log.debug("transaccion de inventario correcta")
                } else {
                    log.warn("no se pudo procesar la transaccion de inventario")
                }
                ServiceManager.ioServices.logSalesNotification(notaVenta.id)
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
        NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(order?.id)
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
        notaVentaService.saveOrder(notaVenta)
        if( notaVenta.fechaEntrega != null ){
          if( Registry.isCouponFFActivated() && !alreadyDelivered ){
            if( !Registry.couponFFOtherDiscount() ){
              if( notaVenta.ordenPromDet.size() <= 0 && notaVenta.desc == null ){
                generateCouponFAndF( StringUtils.trimToEmpty( order.id ) )
              }
            } else {
              generateCouponFAndF( StringUtils.trimToEmpty( order.id ) )
            }
          }
          Boolean orderToday = StringUtils.trimToEmpty(notaVenta.fechaHoraFactura.format("dd/MM/yyyy")).equalsIgnoreCase(StringUtils.trimToEmpty(new Date().format("dd/MM/yyyy")))
          Boolean validDateEnsure = orderToday ? true : validEnsureDateAplication(notaVenta)
        if( !alreadyDelivered ){
          if( validDateEnsure ){
            if( validWarranty( notaVenta, false, null, "", false ) ){
              Boolean doubleEnsure = lstWarranty.size() > 1 ? true : false
              for(Warranty warranty : lstWarranty){
                String idFac = StringUtils.trimToEmpty(idOrderEnsured).length() > 0 ? StringUtils.trimToEmpty(idOrderEnsured) : notaVenta.id
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
            for(DetalleNotaVenta det : notaVenta.detalles){
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
                ItemController.printWarranty( warranty1.amount, warranty1.idItem, warranty1.typeEnsure, StringUtils.trimToEmpty(notaVenta.id), doubleEnsure )
              }
              lstWarranty.clear()
            }
          }
        }
        }

        if (entregaInstante == false) {
            Jb trabajo = jbRepository.findOne(idFactura)
            if( trabajo == null ){
              idFactura = idFactura.replaceFirst("^0*", "")
              trabajo = jbRepository.findOne( idFactura)
            }
            if( trabajo != null && !trabajo.estado.equalsIgnoreCase('TE')){
              trabajo.setEstado('TE')
              trabajo = jbRepository.saveAndFlush(trabajo)
            }

            JbTrack jbTrack = new JbTrack()
            String bill = order?.bill.replaceFirst("^0*", "")
            jbTrack?.rx = bill
            jbTrack?.estado = 'TE'
            jbTrack?.emp = user?.username
            jbTrack?.fecha = new Date()
            jbTrack?.id_mod = '0'
            jbTrack?.id_viaje = null
            jbTrack?.obs = user?.username

            jbTrackService.saveJbTrack(jbTrack)
            jbLlamadaRepository.deleteByJbLlamada(order?.bill)
            cancelacionService.actualizaGrupo( notaVenta.id, 'E' )
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
        return notaVentaService.obtenerConfigParaVentasSinInventario()
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
            Empleado employee = notaVentaService.obtenerEmpleadoDeNotaVenta(pOrderId)
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
        if (entregaInstante) {
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
                      Parametro diaIntervalo = Registry.find(TipoParametro.DIA_PRO)
                      Date diaPrometido = new Date() + diaIntervalo?.valor?.toInteger()
                      savePromisedDate(notaVenta?.id, diaPrometido)
                    }
                }
                if (StringUtils.trimToEmpty(detalle?.surte).equals('P')) {
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
        if(entregaInstante){
            if (surte || temp || !entregaBo) {
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
    }else{
            registro = false
        }
        return registro
    }

    static Boolean creaJb(String idFactura, Boolean cSaldo) {
        NotaVenta notaVenta = notaVentaService.obtenerNotaVentaPorTicket(idFactura)
        List<DetalleNotaVenta> detalleVenta = detalleNotaVentaService.listarDetallesNotaVentaPorIdFactura(notaVenta?.id)
        Boolean creaJB = false
        String articulos = ''
        String surte = ''
        String tipoJb = ''
        Boolean genericoD = false
        Iterator iterator = detalleVenta.iterator();
        while (iterator.hasNext()) {
            DetalleNotaVenta detalle = iterator.next()
            Articulo articulo = articuloService.obtenerArticulo(detalle?.idArticulo)
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

        TmpServicios tmpServicios = tmpServiciosRepository.findbyIdFactura(notaVenta?.id)
        if (tmpServicios?.id_serv != null) {
            creaJB = true
        }

        if ( creaJB ) {
            Jb jb = jbRepository.findOne(notaVenta?.factura)
            println('JB: ' + jb?.rx)

            Jb nuevoJb = new Jb()
            JbTrack nuevojbTrack = new JbTrack()

            if (jb == null) {
                String factura = notaVenta.factura.replaceFirst("^0*", "")
                nuevoJb?.rx = factura
                nuevoJb?.estado = 'PE'
                nuevoJb?.id_cliente = notaVenta?.idCliente
                nuevoJb?.emp_atendio = notaVenta?.empleado?.id
                nuevoJb?.fecha_promesa = notaVenta?.fechaPrometida
                nuevoJb?.num_llamada = 0
                nuevoJb?.material = articulos
                nuevoJb?.surte = surte
                nuevoJb?.saldo = notaVenta.ventaNeta - notaVenta?.sumaPagos
                nuevoJb?.jb_tipo = tipoJb
                nuevoJb?.cliente = notaVenta?.cliente?.nombreCompleto
                nuevoJb?.fecha_venta = notaVenta?.fechaHoraFactura

                nuevojbTrack?.rx = factura
                nuevojbTrack?.estado = 'PE'
                nuevojbTrack?.emp = notaVenta?.empleado?.id
                nuevojbTrack?.obs = articulos

                println('jbTipo: ' + nuevoJb?.jb_tipo)
                println('LC: ' + StringUtils.trimToEmpty(nuevoJb?.jb_tipo).equals('LC'))
                if (StringUtils.trimToEmpty(nuevoJb?.jb_tipo).equals('LC')) {
                    nuevoJb?.estado = 'EP'
                    nuevoJb?.id_viaje = '8'

                    JbTrack nuevoJbTrack2 = new JbTrack()
                    nuevoJbTrack2?.rx = factura
                    nuevoJbTrack2?.estado = 'EP'
                    nuevoJbTrack2?.obs = '8'
                    nuevoJbTrack2?.id_viaje = '8'
                    nuevoJbTrack2?.emp = notaVenta?.empleado?.id
                    nuevoJbTrack2?.fecha = new Date()
                    nuevoJbTrack2?.id_mod = '0'
                    println('LC: ' + nuevoJbTrack2?.id_viaje)
                    nuevoJbTrack2 = jbTrackService.saveJbTrack(nuevoJbTrack2)

                }

                Parametro convenioNomina = parametroRepository.findOne(TipoParametro.CONV_NOMINA.value)


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
                        nuevoJb?.jb_tipo = 'EMA'
                    } else {
                        nuevoJb?.jb_tipo = 'EMP'
                    }

                }


            }

            if (cSaldo) {
                nuevoJb?.estado = 'RTN'
                nuevojbTrack?.estado = 'RTN'
                nuevojbTrack?.obs = 'Factura con Saldo'
            }

            nuevoJb?.fecha_mod = new Date()
            nuevoJb?.id_mod = '0'
            nuevojbTrack?.fecha = new Date()
            nuevojbTrack?.id_mod = '0'
            if( nuevoJb.rx != null ){
              nuevoJb = jbRepository.save(nuevoJb)
              jbRepository.flush()
            }
            if( nuevojbTrack.rx != null ){
              nuevojbTrack = jbTrackService.saveJbTrack(nuevojbTrack)
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
        for(int i=0;i<its.length;i++){
          if( !surte.equalsIgnoreCase(its[i]) && !its[i].equalsIgnoreCase("P") ){
            surteOption.add(its[i])
          }
        }
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
        Precio precio = precioRepository.findbyArt(item?.name.trim())

        if( (item.subtype.startsWith('S') || item.typ.equalsIgnoreCase('O')) ||
                (item?.type?.trim().equals('A') && precio?.surte?.trim().equals('P')) ){
            AcusesTipo acusesTipo = acusesTipoRepository.findOne('AUT')
            String url = acusesTipo?.pagina + '?id_suc=' + branch?.id.toString().trim() + '&id_col=' + item?.color?.trim() + '&id_art=' + item?.name.toString().trim()
            String resultado = ''
            if(  detalleNotaVentaService.verificaValidacionSP(item?.id, order.id, '') ){
              resultado = callWS(url, item?.id, order.id)
            } else {
              resultado = 'No|'+item?.name?.toString().trim()+'|noValidaSP'
            }
            println(resultado)
            int index
            try {
                index = 1
            } catch (ex) {
                index = 1
            }
            String[] result = resultado.split(/\|/)
            String condicion = result[0]

            if (condicion.trim().equals('Si')) {
                String contenido = resultado + '|' + item?.id + '|' + item?.color + '|' + 'facturacion'
                Date date = new Date()
                SimpleDateFormat formateador = new SimpleDateFormat("hhmmss")
                String nombre = formateador.format(date)
                generaAcuse(contenido, nombre)

                surteSwitch.surte = 'P'
            } else if (condicion.trim().equals('No') && result.size() == 2) {
                Integer question = JOptionPane.showConfirmDialog(new JDialog(), '¿Desea Continuar con la venta?', 'Almacen Central no Responde o sin Existencias',
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

        surteSwitch.setAgregaArticulo(agregaArticulo)
        surteSwitch.setSurteSucursal(surteSucursal)

        return surteSwitch
    }

    static void insertaAcuseAPAR(Order order, Branch branch) {

        List<DetalleNotaVenta> listarDetallesNotaVentaPorIdFactura = detalleNotaVentaService.listarDetallesNotaVentaPorIdFactura(order?.id)
        String parte = ''
        int rx = 0
        Item item =  new Item()
        Boolean insertarAcuse = false
        Iterator iterator = listarDetallesNotaVentaPorIdFactura.iterator();
        while (iterator.hasNext()) {
            DetalleNotaVenta detalleNotaVenta = new DetalleNotaVenta()
            detalleNotaVenta = iterator.next()
            if (detalleNotaVenta?.articulo?.idGenerico?.trim().equals('B')) {
                rx = 1
            }
            if (detalleNotaVenta?.idTipoDetalle?.trim().equals('VD') ||
                    detalleNotaVenta?.idTipoDetalle?.trim().equals('VI.') ||
                    detalleNotaVenta?.idTipoDetalle?.trim().equals('FT') ||
                    detalleNotaVenta?.idTipoDetalle?.trim().equals('LD') ||
                    detalleNotaVenta?.idTipoDetalle?.trim().equals('LI') ||
                    detalleNotaVenta?.idTipoDetalle?.trim().equals('CI') ||
                    detalleNotaVenta?.idTipoDetalle?.trim().equals('CD') ||
                    detalleNotaVenta?.idTipoDetalle?.trim().equals('REM')
            ) {
                parte = parte + detalleNotaVenta?.idTipoDetalle?.trim() + ','
            }

            if (detalleNotaVenta?.surte?.trim().equals('P') && detalleNotaVenta?.articulo?.idGenerico?.trim().equals('A')) {
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

            Acuse acuseAPAR = new Acuse()
            acuseAPAR?.contenido = contenidoAPAR
            acuseAPAR?.idTipo = 'APAR'
            acuseAPAR?.intentos = 0

            acuseRepository.saveAndFlush(acuseAPAR)
            insertarAcuse = false
        }
    }

    static void generaAcuse(String contenido, String nombre) {
        try {
            Parametro ruta = parametroRepository.findOne(TipoParametro.ARCHIVO_CONSULTA_WEB.value)
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
            detalleNotaVentaService.saveLogSP( idArticulo, idFactura, respuesta )
        } catch (Exception e) {
            future.cancel(true)
            respuesta = 'No|'+idArticulo
            this.log.warn("encountered problem while doing some work", e)
        }
        return respuesta
    }


    static String armazonString(String idNotaVenta) {
        String armazonString = ''
        List<DetalleNotaVenta> detalleVenta = detalleNotaVentaService.listarDetallesNotaVentaPorIdFactura(idNotaVenta)
        Iterator iterator = detalleVenta.iterator();
        while (iterator.hasNext()) {
            DetalleNotaVenta detalle = iterator.next()

            if (detalle?.articulo?.idGenerico.trim().equals('A')) {
                armazonString = detalle?.articulo?.articulo.trim()
            }

        }
        return armazonString
    }

    static void validaSurtePorGenerico( Order order ){
        NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(order.id)
        notaVentaService.validaSurtePorGenericoInventariable( notaVenta )
    }


    static String obtieneTiposClientesActivos( ){
        return Registry.activeCustomers
    }


    static void saveSuyo(Order order, User user, String dejo, String instrucciones, String condiciones, String serv) {
        TmpServicios servicios = new TmpServicios()
        servicios?.id_factura = order?.id
        servicios?.fecha_prom = new Date()
        servicios?.emp = user?.username
        servicios?.id_cliente = order?.customer?.id
        servicios?.cliente = order?.customer?.name + ' ' + order?.customer?.fathersName + ' ' + order?.customer?.mothersName
        servicios?.condicion = condiciones
        servicios?.dejo = dejo
        servicios?.instruccion = instrucciones
        servicios?.servicio = serv
        tmpServiciosRepository.saveAndFlush(servicios)
        NotaVenta notaVenta = notaVentaService.obtenerNotaVenta(order?.id)
        notaVenta?.observacionesNv = dejo
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
        Integer idTmpServicio = tmpServiciosRepository.tmpExiste(idNotaVenta)
        if (idTmpServicio != null) {
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
      Boolean unPaquete = articuloService.validaUnSoloPaquete( lstIds, idItem )
      return unPaquete
    }


    static Boolean validOnlyOneLens( List<OrderItem> lstItems, Integer idItem ){
        List<Integer> lstIds = new ArrayList<Integer>()
        for(OrderItem item : lstItems){
            lstIds.add( item.item.id )
        }
        Boolean unLente = articuloService.validaUnSoloLente( lstIds, idItem )
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
      return notaVentaService.validaSoloInventariables( order.id )
    }


  static void creaJbAnticipoInventariables( String idFactura ) {
    log.debug( "creaJbAnticipoInventariables( )" )
    notaVentaService.insertaJbAnticipoInventariables( idFactura )
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
    Parametro p = parametroRepository.findOne( TipoParametro.ANTICIPO_MENOR_REQUIERE_AUTORIZACIN.value )
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
      Parametro p = parametroRepository.findOne( TipoParametro.VALIDA_EMPLEADO.value )
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
      Examen examen = examenService.obtenerExamenPorIdCliente( order.customer.id )
      if( examen != null && (examen.factura = null || examen.factura.trim().length() <= 0) ){
        examen.factura = order.bill
        examenService.guardarExamen( examen )
      }
    }


    static void updateQuote( Order order, Integer numQuote ){
       cotizacionService.updateQuote( order.id, numQuote )
    }



    static Boolean validaEntregaSegundaVenta(Order order) {
      Boolean valid = true
      Boolean isGoogle = false
      Boolean hasCupon = false
      Boolean hasGenericB = false
      Boolean hasGenericC = false
      NotaVenta notaAnterior = notaVentaService.buscarNotaInicial( order.customer.id, order.id )
      for(OrderItem item : order.items){
        Articulo articulo = articuloService.obtenerArticulo( item.item.id )
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
      /*} else {
        valid = true
      }*/
      return valid
    }


    static void retenerEntrega(String orderId){
      NotaVenta nota = notaVentaService.obtenerNotaVenta( orderId )
      NotaVenta notaAnterior = notaVentaService.buscarNotaInicial( nota.idCliente, nota.id )
      BigDecimal saldo = BigDecimal.ZERO
      if( nota != null ){
        saldo = nota.ventaNeta.subtract(nota.sumaPagos)
      }
      List<DetalleNotaVenta> lstDetalles = new ArrayList<>(nota.detalles)
      String articulos = ''
      for(DetalleNotaVenta det : nota.detalles){
        articulos = articulos+","+det.articulo.articulo.trim()
      }
      articulos = articulos.replaceFirst( ",", "" )
      JbTrack nuevoJbTrack = new JbTrack()
      nuevoJbTrack?.rx = nota.factura
      nuevoJbTrack?.estado = 'PE'
      nuevoJbTrack?.emp = nota.idEmpleado
      nuevoJbTrack?.obs = articulos
      nuevoJbTrack?.fecha = new Date()
      nuevoJbTrack?.id_mod = '0'
      jbTrackService.saveJbTrack( nuevoJbTrack )

      Jb jbRtn = new Jb()
      jbRtn.rx = nota.factura
      jbRtn.estado = 'RTN'
      jbRtn.id_cliente = nota.idCliente
      jbRtn.emp_atendio = nota.idEmpleado
      jbRtn.num_llamada = 0
      jbRtn.saldo = saldo
      jbRtn.jb_tipo = 'REF'
      jbRtn.cliente = nota.cliente.nombreCompleto
      jbRtn.id_mod = '0'
      jbRtn.fecha_mod = new Date()
      jbRtn.fecha_venta = nota.fechaHoraFactura
      jbRtn.material = articulos
      jbRtn = jbService.saveJb( jbRtn )

      JbTrack jbTrack = new JbTrack()
      jbTrack.rx = jbRtn.rx
      jbTrack.estado = "RTN"
      jbTrack.obs = "PAGO CON CUPON"
      jbTrack.emp = jbRtn.emp_atendio
      jbTrack.fecha = new Date()
      jbTrack.id_mod = '0'
      jbTrackService.saveJbTrack( jbTrack )
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
      return notaVentaService.obtenerMontoCupon( idOrder )
    }

    static BigDecimal getCuponAmountThirdPair(String idOrder) {
        log.debug( 'getCuponAmountThirdPair( )' )
        return notaVentaService.obtenerMontoCuponTercerPar( idOrder )
    }

    static void creaJbAnticipoInventariablesMultypayment( String idFactura ) {
        log.debug( "creaJbAnticipoInventariables( )" )
        NotaVenta nota = notaVentaService.obtenerNotaVenta( idFactura )
        if( nota != null && (nota.ventaNeta.doubleValue()-nota.sumaPagos.doubleValue() > 0) ){
          notaVentaService.insertaJbAnticipoInventariables( idFactura )
        }
    }


    static Boolean validLenses( Order order ){
        return notaVentaService.validaLentes( order.id )
    }


    static List<Item> existeLenteContacto(Order order){
      List<Item> lstItems = new ArrayList<>()
      List<Articulo> articulo = notaVentaService.validaLentesContacto( order.id )
      for(Articulo art : articulo){
        lstItems.add( Item.toItem(art) )
      }
      return lstItems
    }



    static Boolean validHasLcDet( String orderId, Integer idArticulo ){
      return notaVentaService.validaTieneDetalles( orderId, idArticulo )
    }



    static void removePedidoLc( String orderId, Integer idArticulo ){
      log.debug( "Remover pedido de lentes de contacto" )
      notaVentaService.removePedidoLc( orderId, idArticulo )
    }



    static void creaJbFam( String idOrder1, String idOrder2 ){
      log.debug( "creaJbFam( )" )
      NotaVenta nota1 = notaVentaService.obtenerNotaVenta( idOrder1 )
      NotaVenta nota2 = notaVentaService.obtenerNotaVenta( idOrder2 )
      Jb jbFam = jbService.saveJbFamilia( nota1, nota2 )
      formaContactoService.saveFCFam( nota1.factura, jbFam, nota2.factura )
    }


    static void updateOrderLc( Order order ){
      NotaVenta nota = notaVentaService.obtenerNotaVenta( order.id )
      BigDecimal total = BigDecimal.ZERO
      for(DetalleNotaVenta det : nota.detalles){
        total = total+(det.precioUnitFinal.multiply(det.cantidadFac))
      }
      nota.ventaNeta = total
      nota.ventaTotal = total
      notaVentaService.registrarNotaVenta( nota )
    }

    static void deliverOrderLc( String idPedido ){
      notaVentaService.entregaPedidoLc( StringUtils.trimToEmpty(idPedido) )
    }

    static Boolean validGenericNoDelivered( String idOrder ){
        NotaVenta nota = notaVentaService.obtenerNotaVenta( idOrder )
        List<DetalleNotaVenta> detalleVenta = new ArrayList<>()
        for(DetalleNotaVenta det : nota.detalles){
          detalleVenta.add( det )
        }
        Boolean entregaBo = false
        Boolean surte = false
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
        Parametro genericoNoEntrega = parametroRepository.findOne(TipoParametro.GENERICOS_NO_ETREGABLES.value)
        ArrayList<String> genericosNoEntregables = new ArrayList<String>()
        String s = genericoNoEntrega?.valor
        StringTokenizer st = new StringTokenizer(s.trim(), ",")
        Iterator its = st.iterator()
        while (its.hasNext()) {
            genericosNoEntregables.add(its.next().toString())
        }
        Articulo articulo = articuloService.obtenerArticulo(idItem)
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
                    entregaBo = true
                }
            }
        return entregaBo
    }



    static void saveBatch( String idFactura, Integer idArticulo, String lote ) {
      notaVentaService.saveBatch( idFactura, idArticulo, lote )
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
      return notaVentaService.existePromoEnOrden( idOrder, idPromo )
    }


    static Boolean hasOrderLc( String bill ){
      Boolean hasOrderLc = false
      PedidoLc pedidoLc = articuloService.buscaPedidoLc( StringUtils.trimToEmpty(bill) )
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

    static NotaVenta findOrderByidOrder(String idOrder) {
      NotaVenta result = notaVentaService.obtenerNotaVenta( idOrder )
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

    static CuponMvView cuponValid( Integer idCustomer ){
      CuponMvView cuponMvView = new CuponMvView()
      cuponMvView.amount = notaVentaService.cuponValid( idCustomer )
      cuponMvView.idOrderSource = notaVentaService.orderSource( idCustomer )
      return cuponMvView
    }


    static void deleteCuponMv( String idOrder ){
      notaVentaService.eliminarCUponMv( idOrder )
    }

    static CuponMv obtenerCuponMv( String idFacturaOrigen, String idFacturaDestino, BigDecimal montoCupon, Integer numeroCupon ){
        return notaVentaService.actualizarCuponMv( StringUtils.trimToEmpty(idFacturaOrigen),
                StringUtils.trimToEmpty(idFacturaDestino), montoCupon, numeroCupon, false )
    }

    static CuponMv obtenerCuponMvByClave( String clave ){
        return notaVentaService.obtenerCuponMvClave( clave )
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
          descuentoClave.descripcion_descuento = TAG_MSJ_CUPON
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
      return notaVentaService.cuponGeneraCupon(claveDescuento)
    }


    static String descuentoClavePoridFactura( String idFactura ){
      return notaVentaService.claveDescuentoNota( StringUtils.trimToEmpty(idFactura) )
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
        isOpenDay = notaVentaService.diaActualEstaAbierto()
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


    static void printResumeCancCoupon( String idOrder, String devAmount ){
      ticketService.imprimeResumenCuponCan( idOrder, devAmount )
    }


    static Boolean validDioptra( String idOrder ){
      Boolean valid = false
      NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( idOrder )
      if( notaVenta != null ){
        valid = articuloService.validaCodigoDioptra( StringUtils.trimToEmpty(notaVenta.codigo_lente) )
      }
      return valid
    }


    static void generateCouponFAndF( String idOrder ){
      BigDecimal amountSale = BigDecimal.ZERO
      Boolean hasNoCouponApply = true
      Boolean hasLenses = false
      Date fechaInicio = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH );
      Date fechaFin = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 );
      QDescuento qDescuento = QDescuento.descuento
      NotaVenta nota = notaVentaService.obtenerNotaVenta( idOrder )
      if( nota!= null && nota.fechaEntrega != null ){
        List<NotaVenta> lstNotasCliente = notaVentaService.obtenerNotaVentaPorClienteFF( nota.idCliente )
        for(NotaVenta notaVenta : lstNotasCliente){
          List<CuponMv> cuponMv = notaVentaService.obtenerCuponMvFacturaOriFF( StringUtils.trimToEmpty(notaVenta.factura) )
          if( cuponMv.size() > 0 && StringUtils.trimToEmpty(cuponMv.first().claveDescuento).startsWith("F") ){
            hasNoCouponApply = false
          }
        }
        if( hasNoCouponApply ){
          Integer appliedCoup = 0
          List<CuponMv> lstCupones = notaVentaService.obtenerCuponMvFacturaDest( StringUtils.trimToEmpty(nota.factura) )
          if( lstCupones.size() <= 0 ){
            lstCupones = notaVentaService.obtenerCuponMvFacturaDest( StringUtils.trimToEmpty(nota.id) )
          }
          for(CuponMv c : lstCupones){
            if( StringUtils.trimToEmpty(c.fechaAplicacion.format("dd-MM-yyyy")).equalsIgnoreCase(StringUtils.trimToEmpty(new Date().format("dd-MM-yyyy"))) ){
              appliedCoup = appliedCoup+1
            }
          }
          List<Descuento> lstDesc = descuentoRepository.findAll(qDescuento.clave.eq("AF200").and(qDescuento.idFactura.eq(nota.id))) as List<Descuento>
          Integer descuentoAF = lstDesc.size()
          if( appliedCoup > 0 || descuentoAF > 0 ){
            hasNoCouponApply = false
          }
        }
        for(DetalleNotaVenta det : nota.detalles){
          if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_B) ){
            hasLenses = true
          }
          if( !StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEG) ){
            amountSale = amountSale.add(det.precioUnitFinal)
          }
        }
        if( amountSale.doubleValue() > Registry.amountToGenerateFFCoupon && hasNoCouponApply && hasLenses ){
          List<CuponMv> cuponMvTmp = notaVentaService.obtenerCuponMvFacturaDest( StringUtils.trimToEmpty(nota.factura) )
          if( cuponMvTmp.size() <= 0 || !StringUtils.trimToEmpty(cuponMvTmp.first().claveDescuento).startsWith("F") ){
            String titulo = "FRIENDS AND FAMILY"
            Integer numCupon = 0
            CuponMv cuponMv = new CuponMv()
            cuponMv.facturaDestino = ""
            cuponMv.facturaOrigen = nota.factura
            cuponMv.fechaAplicacion = null
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, Registry.diasVigenciaCuponFF)
            cuponMv.fechaVigencia = calendar.getTime()
            println cuponMv.fechaVigencia.format("dd-MM-yyyy")
            cuponMv = updateCuponMv( nota.id, "", Registry.amountFFCoupon, numCupon, true )
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
          valid = false
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
          if( StringUtils.trimToEmpty(articulo.subtipo).startsWith(TAG_SUBTIPO_NINO) ){
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
        if( valid ){
          if( lstIdGar.size() == 1 ){
            BigDecimal amount = BigDecimal.ZERO
            Articulo warnt = articuloService.obtenerArticulo( lstIdGar.first() )
            for(DetalleNotaVenta orderItem : nota.detalles){
              if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS)
                      && !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE) ){
                if( StringUtils.trimToEmpty(warnt.articulo).startsWith(TAG_SEGUROS_ARMAZON) ){
                  if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                    amount = amount.add(orderItem.precioUnitLista)
                  }
                  typeEnsure = "S"
                } else {
                  if( StringUtils.trimToEmpty(warnt.articulo).equalsIgnoreCase(TAG_SEGUROS_OFTALMICO) ){
                    if( StringUtils.trimToEmpty(orderItem.articulo.subtipo).startsWith(TAG_SUBTIPO_NINO) ||
                            !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON)){
                      amount = amount.add(orderItem.precioUnitLista)
                    }
                  } else {
                    if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ||
                            (StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) &&
                          StringUtils.trimToEmpty(orderItem.articulo.tipo).equalsIgnoreCase(TAG_TIPO_OFTALMICO)) ){
                      amount = amount.add(orderItem.precioUnitLista)
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
              String items = ""
              for(DetalleNotaVenta orderItem : nota.detalles){
                if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS)
                       && !StringUtils.trimToEmpty(orderItem.articulo.articulo).equalsIgnoreCase(TAG_MONTAJE)){
                  if( StringUtils.trimToEmpty(warnt.articulo).startsWith(TAG_SEGUROS_ARMAZON) ){
                    if( StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                      items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                    }
                  } else {
                    //if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
                      items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                    //}
                  }
                }
              }
              Warranty warranty = new Warranty()
              warranty.amount = amount
              warranty.idItem = items.replaceFirst(",","")
              warranty.typeEnsure = typeEnsure
              warranty.idOrder = addIdOrder ? nota.id : ""
              idOrderEnsured = StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ? StringUtils.trimToEmpty(idOrderPostEnsure) : idOrderEnsured
              println idOrderEnsured
              lstWarranty.add( warranty )
              lstIdGar.clear()
            } else {
              MSJ_ERROR_WARRANTY = "Seguro Invalido."
              valid = false
            }
          } else if( lstIdGar.size() == 2 && frame && lens ) {
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
              for(DetalleNotaVenta orderItem : lstLens){
                if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                  items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                }
              }
              Warranty warranty = new Warranty()
              warranty.amount = amountSegL
              warranty.idItem = items.replaceFirst(",","")
              warranty.typeEnsure = typeEnsureO
              warranty.idOrder = addIdOrder ? nota.id : ""
              idOrderEnsured = StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ? StringUtils.trimToEmpty(idOrderPostEnsure) : idOrderEnsured
              println idOrderEnsured
              lstWarranty.add( warranty )
              lstIdGar.clear()
            } else {
              MSJ_ERROR_WARRANTY = "Seguro Invalido."
              valid = false
            }

            if( warrantyAmountFrame.compareTo(BigDecimal.ZERO) > 0 && segValid(segFrame.id, lstIdFrames) ){
              String items = ""
              for(DetalleNotaVenta orderItem : lstFrames){
                if( !StringUtils.trimToEmpty(orderItem.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
                  items = items+","+StringUtils.trimToEmpty(orderItem.articulo.articulo)
                }
              }
              Warranty warranty = new Warranty()
              warranty.amount = amountSegF
              warranty.idItem = items.replaceFirst(",","")
              warranty.typeEnsure = typeEnsureF
              warranty.idOrder = addIdOrder ? nota.id : ""
              idOrderEnsured = StringUtils.trimToEmpty(idOrderPostEnsure).length() > 0 ? StringUtils.trimToEmpty(idOrderPostEnsure) : idOrderEnsured
              println idOrderEnsured
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



  private static Boolean segValid(Integer itemWarr, List<Integer> items ){
    Boolean valid = true
    Boolean frame = false
    Boolean sunglass = false
    Boolean ophtalmic = false
    Boolean lens = false
    Boolean lensKid = false
    if( itemWarr != null ){
      Articulo itemWarranty = ItemController.findArticle( itemWarr )
      for(Integer id : items){
        Articulo item = ItemController.findArticle( id )
        if( StringUtils.trimToEmpty(item.idGenerico).equalsIgnoreCase(TAG_GENERICO_ARMAZON) ){
          frame = true
        }
        if( StringUtils.trimToEmpty(item.subtipo).startsWith(TAG_SUBTIPO_NINO) ){
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



  static NotaVenta ensureOrder( String idOrder ){
    NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( idOrder )
    if( notaVenta != null ){
      Boolean warranty = true
      for( DetalleNotaVenta det : notaVenta.detalles ){
        if( !StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_SEGUROS) ){
          warranty = false
        }
      }
      if( warranty ){
        AseguraNotaDialog dialog = new AseguraNotaDialog()
        dialog.show()
        if( dialog.notaVenta != null ){
          notaVenta = dialog.notaVenta
        }
      } else {
        notaVenta = new NotaVenta()
      }
    }
    return  notaVenta
  }



  static Boolean validaAplicaGarantia(String idFactura) {
    Boolean valid = true
    NotaVenta notaVenta = notaVentaService.obtenerNotaVentaPorTicket( StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+idFactura )
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
        for(DetalleNotaVenta det : notaVenta.detalles){
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


  static void reprintEnsure( NotaVenta notaVenta ){
    if( notaVenta.fechaEntrega != null ) {
      if(validEnsureDateAplication(notaVenta)){
        if( validWarranty( notaVenta, false, null, "", false ) ){
          Boolean doubleEnsure = lstWarranty.size() > 1
          for(Warranty warranty : lstWarranty){
            ItemController.printWarranty( warranty.amount, warranty.idItem, warranty.typeEnsure, StringUtils.trimToEmpty(notaVenta.id), doubleEnsure )
          }
          lstWarranty.clear()
        }
      }
    } else {
      JOptionPane.showMessageDialog(null, "La nota no ha sido entregada. No se puede reimprimir el seguro.")
    }
  }


  static Boolean validEnsureDateAplication( NotaVenta notaVenta ){
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
}