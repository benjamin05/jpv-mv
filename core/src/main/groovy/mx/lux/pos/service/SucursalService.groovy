package mx.lux.pos.service

import mx.lux.pos.model.Sucursal

interface SucursalService {

  Sucursal obtenSucursalActual( )

  Sucursal obtenerSucursal( Integer pSucursal )

  List<Sucursal> listarSucursales( )

  List<Sucursal> listarAlmacenes( )

  List<Sucursal> listarSoloSucursales( )

  List<Sucursal> listarSoloAlmacenPorAclarar( Integer id )

  Boolean validarSucursal( Integer pSucursal )

}
