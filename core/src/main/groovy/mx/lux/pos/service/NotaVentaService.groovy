package mx.lux.pos.service

import mx.lux.pos.model.*

interface NotaVentaService {

  NotaVenta obtenerNotaVenta( String idNotaVenta )

  NotaVenta abrirNotaVenta(String clienteID,String empleadoID )

  NotaVenta notaVentaxRx(Integer rx)

  NotaVenta registrarNotaVenta( NotaVenta notaVenta )

  NotaVenta registrarDetalleNotaVentaEnNotaVenta( String idNotaVenta, DetalleNotaVenta detalleNotaVenta )

  NotaVenta eliminarDetalleNotaVentaEnNotaVenta( String idNotaVenta, Integer idArticulo )

    Pago registrarPagoEnNotaVenta( String idNotaVenta, Pago pago )

  NotaVenta eliminarPagoEnNotaVenta( String idNotaVenta, Integer idPago )

  void eliminarNotaVenta( String idNotaVenta )

  NotaVenta cerrarNotaVenta( NotaVenta notaVenta )

  List<NotaVenta> listarUltimasNotasVenta( )

  List<NotaVenta> listarNotasVentaPorParametros( Map<String, Object> parametros )

  NotaVenta obtenerNotaVentaPorTicket( String ticket )

  SalesWithNoInventory obtenerConfigParaVentasSinInventario( )

  Empleado obtenerEmpleadoDeNotaVenta( pOrderId )

  void saveOrder( NotaVenta pNotaVenta )

  NotaVenta obtenerSiguienteNotaVenta( Integer pIdCustomer )

    void saveRx(NotaVenta rNotaVenta, Integer receta)

    void saveProDate(NotaVenta rNotaVenta, Date fechaPrometida)

    NotaVenta saveFrame(String idNotaVenta, String opciones, String forma)

  void validaSurtePorGenericoInventariable( NotaVenta notaVenta )

  void registraImpuestoPorFactura( NotaVenta notaVenta )

  Boolean ticketReusoValido( String ticket, Integer idArticulo )

  Boolean montoValidoFacturacion( String ticket )

  List<NotaVenta> obtenerDevolucionesPendientes( Date fecha )

  NotaVenta buscarNotasReuso( String idFactura )

  NotaVenta obtenerNotaVentaOrigen( String idNotaVenta )

  Boolean validaSoloInventariables( String idFactura )

  void insertaJbAnticipoInventariables( String idFactura )

  void correScriptRespaldoNotas( String idFactura )

  NotaVenta buscarNotaInicial( Integer idCliente, String idFactura )

  List<NotaVenta> obtenerNotaVentaPorCliente( Integer idCliente )

  BigDecimal obtenerMontoCupon( String idNotaVenta )

  BigDecimal obtenerMontoCuponTercerPar( String idNotaVenta )

  Boolean validaLentes( String idFactura )

  List<Articulo> validaLentesContacto( String idFactura )

  Boolean validaTieneDetalles( String orderId, Integer idArticulo )

  void removePedidoLc( String orderId, Integer idArticulo )

  void insertaJbLc( String idFactura )

  Boolean validaSoloInventariablesMultipago( String idFactura )

  void saveBatch( String idFactura, Integer idArticulo, String lote )

  Boolean validaLote( String idFactura, Integer idArticulo, String lote )

  Boolean existePromoEnOrden( String idFactura, Integer idPromo )

  void creaAcusePedidoLc( String idFactura )

  PedidoLc actualizaFechaRecepcionPedidoLc( String idFactura )

  List<PedidoLc> obtienePedidosLcPorEnviar( )

  void cargaFoliosPendientesPedidosLc( )

  String[] montoDescuentoNota( String idFactura )

  void cargaAcusesPedidosLc( )

  void entregaPedidoLc( String idPedido )

  void guardarCuponMv( CuponMv cuponMv )

  CuponMv actualizarCuponMv( String idFacturaOrigen, String idFacturaDestino, BigDecimal montoCupon, Integer numeroCupon, Boolean ffCupon )

  void actualizarCuponMvPorClave( String idFacturaDestino, String clave )

  CuponMv obtenerCuponMv( String factura )

  Boolean cuponMvEsApplicable( Integer idCliente, String factura )

  CuponMv obtenerCuponMvFuente( String factura )

  CuponMv obtenerCuponMvClave( String clave )

  List<CuponMv> obtenerCuponMvFacturaOri( String factura )

  List<CuponMv> obtenerCuponMvFacturaDest( String factura )

  BigDecimal cuponValid( Integer idCliente )

  String orderSource( Integer idCliente )

  void eliminarCUponMv( String idFactura )

  CuponMv obtenerCuponMv( String idFacturaOrigen, String idFacturaDestino )

  void eliminarCuponMultipago( String idFactura )

  void eliminaPromocion( String idFactura )

  Boolean cuponGeneraCupon( String claveCupon )

  String claveDescuentoNota( String idFactura )

  String esReusoPedidoLc( String idFactura )

  void existeDescuentoClave( String clave, String antiguaFactura )

  Boolean diaActualEstaAbierto()

  NotaVenta registrarDetalleNotaVentaEnNotaVentaReasignCupon( String idNotaVenta, DetalleNotaVenta detalleNotaVenta )

  List<CuponMv> obtenerCuponMvFacturaOriApplied( String factura )

  Pago actualizarPagoEnNotaVenta( String idOrder, Pago pago )

  PedidoLc obtienePedidoLc( String idFactura )

  NotaVenta obtenerUltimaNotaVentaPorCliente ( Integer id)

  List<NotaVenta> obtenerNotaVentaPorClienteFF( Integer idCliente )

  List<CuponMv> obtenerCuponMvFacturaOriFF( String factura )
}
