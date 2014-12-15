package mx.lux.pos.service

import mx.lux.pos.model.*

interface InventarioService {

  // TipoTransInv Entity
  List<TipoTransInv> listarTiposTransaccion( )

  Integer obtenerSiguienteFolio( String pIdTipoTransInv )

  TipoTransInv obtenerTipoTransaccion( String pIdTipoTransInv )

  // TransInv Entity
  Date obtenerUltimaFechaTransaccion( )

  Integer registrarTransaccion( TransInv pTransInv )

  Integer solicitarTransaccion( InvTrRequest pRequest )

  Integer solicitarTransaccionLc( LcRequest pRequest )

  Boolean solicitarTransaccionVenta( NotaVenta pNotaVenta )

  Boolean solicitarTransaccionDevolucion( NotaVenta pNotaVenta )

  TransInv obtenerTransaccion( String pIdTipoTrans, Integer pFolio )

  Boolean isReceiptDuplicate( )

  // TransInvDetalle Entity

  Integer obtenerExistenciaPorArticulo( Integer id )

  Collection<Articulo> listarArticulosConExistencia( )

  Collection<Articulo> listarArticulosLcConExistencia( )

  // Services added for Inventory View
  TipoTransInv obtenerTipoTransaccionAjuste( )

  TipoTransInv obtenerTipoTransaccionDevolucion( )

  TipoTransInv obtenerTipoTransaccionDevolucionExtraordinaria( )

  TipoTransInv obtenerTipoTransaccionEntrada( )

  TipoTransInv obtenerTipoTransaccionSalida( )

  TipoTransInv obtenerTipoTransaccionOtraSalida( )

  TipoTransInv obtenerTipoTransaccionOtraEntrada( )

  TipoTransInv obtenerTipoTransaccionVenta( )

  TipoTransInv obtenerTipoTransaccionSalidaAlmacen();

  TipoTransInv obtenerTipoTransaccionEntradaAlmacen();

  List<Sucursal> listarSucursales( )

  Sucursal sucursalActual( )

  Empleado obtenerEmpleado( String pEmpId )

  Sucursal obtenerSucursal( Integer pSite )

  List<TransInv> listarTransaccionesPorRangoFecha( Date pRangeStart, Date pRangeEnd )

  List<TransInv> listarTransaccionesPorTipo( String pIdTipoTrans )

  List<TransInv> listarTransaccionesPorSucursalDestino( Integer pSiteTo )

  List<TransInv> listarTransaccionesPorSku( Integer pSku )

  List<TransInv> listarTransaccionesPorArticulo( String pPartCodeSeed )

  List<TransInv> listarTransaccionesPorReferencia( String pReference )

  List<TransInv> listarTransaccionesPorTipoAndReferencia( String pTrType, String pReference )

  InvAdjustSheet leerArchivoAjuste( String pFilename )

  Shipment leerArchivoRemesa( String pFilename )

  Shipment obtieneArticuloEntrada(String clave, Integer sucursal, String pIdTipoTrans)

  List<Sucursal> listarAlmacenes( )

  List<Sucursal> listarSoloSucursales( )

  List<Sucursal> listarSoloAlmacenPorAclarar( Integer id )

  Boolean transaccionCargada( String clave )

  InvAdjustSheet obtenerArmazones(  )

  InvAdjustSheet obtenerAccesorios(  )
}
