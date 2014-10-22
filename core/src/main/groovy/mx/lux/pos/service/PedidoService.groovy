package mx.lux.pos.service

import mx.lux.pos.model.*

interface PedidoService {

  // TipoTransInv Entity
  List<TipoTransInv> listarTiposTransaccion( )

  Integer obtenerSiguienteFolio( String pIdTipoTransInv )

  TipoTransInv obtenerTipoTransaccion( String pIdTipoTransInv )

  // TransInv Entity
  Date obtenerUltimaFechaTransaccion( )

  Integer registrarTransaccion( TransInv pTransInv )

  Integer solicitarTransaccion( InvTrRequest pRequest )

  Boolean solicitarTransaccionVenta( NotaVenta pNotaVenta )

  Boolean solicitarTransaccionDevolucion( NotaVenta pNotaVenta )

  TransInv obtenerTransaccion( String pIdTipoTrans, Integer pFolio )

  Boolean isReceiptDuplicate( )

  // TransInvDetalle Entity

  Integer obtenerExistenciaPorArticulo( Integer id )

  Collection<Articulo> listarArticulosConExistencia( )

  // Services added for Inventory View
  TipoTransInv obtenerTipoTransaccionAjuste( )

  TipoTransInv obtenerTipoTransaccionDevolucion( )

  TipoTransInv obtenerTipoTransaccionDevolucionExtraordinaria( )

  TipoTransInv obtenerTipoTransaccionEntrada( )

  TipoTransInv obtenerTipoTransaccionSalida( )

  TipoTransInv obtenerTipoTransaccionVenta( )

  TipoTransInv obtenerTipoTransaccionSalidaAlmacen();

  TipoTransInv obtenerTipoTransaccionEntradaAlmacen();

  List<Sucursal> listarSucursales( )

  Sucursal sucursalActual( )

  Empleado obtenerEmpleado( String pEmpId )

  Sucursal obtenerSucursal( Integer pSite )

  List<PedidoLc> listarTransaccionesPorRangoFecha( Date pRangeStart, Date pRangeEnd )

  List<PedidoLc> listarUltimasTransacciones( )

  PedidoLc obtenerPedidoPoridPedido( String idPedido )

    List<PedidoLc> listarTransaccionesPorIdPedido( String pIdPedido )

  List<PedidoLc> listarTransaccionesPorSucursalDestino( String pSiteTo )

  List<PedidoLc> listarTransaccionesPorCliente( String cliente )

  List<PedidoLc> listarTransaccionesPorArticulo( String pPartCodeSeed )

  List<PedidoLc> listarTransaccionesPorFolio( String pReference )

  PedidoLcDet obtenerPedidoDetPorNumReg( Integer numReg )

  List<TransInv> listarTransaccionesPorTipoAndReferencia( String pTrType, String pReference )

  InvAdjustSheet leerArchivoAjuste( String pFilename )

  Shipment leerArchivoRemesa( String pFilename )

  Shipment obtieneArticuloEntrada(String clave, Integer sucursal, String pIdTipoTrans)

  List<Sucursal> listarAlmacenes( )

  List<Sucursal> listarSoloSucursales( )

  Boolean transaccionCargada( String clave )
}
