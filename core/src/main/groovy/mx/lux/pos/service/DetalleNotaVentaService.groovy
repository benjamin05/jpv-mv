package mx.lux.pos.service

import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.model.LogSP

interface DetalleNotaVentaService {

  DetalleNotaVenta obtenerDetalleNotaVenta( String idFactura, Integer idArticulo )

  List<DetalleNotaVenta> listarDetallesNotaVentaPorIdFactura( String idFactura )

  Boolean verificaValidacionSP( Integer idArticulo, String idFactura, String respuesta )

  LogSP saveLogSP( Integer idArticulo, String idFactura, String respuesta )

}
