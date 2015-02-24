package mx.lux.pos.service

import mx.lux.pos.model.*

interface CancelacionService {

  List<CausaCancelacion> listarCausasCancelacion( )

  boolean permitirCancelacionExtemporanea( String idNotaVenta )

  Modificacion registrarCancelacionDeNotaVenta( String idNotaVenta, Modificacion modificacion, String idUser, Boolean transCupones  )

  List<Devolucion> listarDevolucionesDeNotaVenta( String idNotaVenta )

  List<Devolucion> registrarDevolucionesDeNotaVenta( String idNotaVenta, Map<Integer, String> devolucionesPagos, String dataDev )

  List<Pago> registrarTransferenciasParaNotaVenta( String idOrigen, String idDestino, Map<Integer, BigDecimal> transferenciasPagos )

  List<NotaVenta> listarNotasVentaOrigenDeNotaVenta( String idNotaVenta )

  BigDecimal obtenerCreditoDeNotaVenta( String idNotaVenta )

  void restablecerValoresDeCancelacion( String idNotaVenta )

  Boolean validandoTransferencia( String idNotaVenta )

  void restablecerMontoAlBorrarPago( Integer idPago )

  Boolean validandoEnvioPino( String idOrder )

  CausaCancelacion causaCancelacion( Integer id )

  Jb actualizaJb( String idFactura )

  JbTrack insertaJbTrack( String idFactura )

  void eliminaJbLlamada( String idFactura )

  void generaAcuses( String idFactura )

  void actualizaGrupo( String idFactura, String trans )

  void actualizaJbCancelado( String idFactura, String idEmpleado )

  Boolean cancMismoDia( String idOrder )

  void cancelarCupones( String idOrder )

  void enviaCancelaccionPedidoLc( String factura )

  Boolean enviaTransferenciaPedidoLc( String factura, String idFactura )

  CuponMv liberaCupon( String idFactura )

  List<String> reasignarCupones( String idFactura, List<Jb> lstJbs, List<JbTrack> lstJbTracks )

  List<NotaVenta> tieneCuponesAplicados( String idFactura )

  Integer salidaLentesContacto(String idFactura, String idUser)

  void imprimeTransaccionOtrasSalidas( Integer idTrans )

  void registraLogAutorizacion( String idFactura, String idEmp, Integer idTipoTrans, Pago pago )

  Modificacion obtenerModificacion( String idNotaVenta )
}
