package mx.lux.pos.service

import mx.lux.pos.model.TipoPago

interface TipoPagoService {

  TipoPago obtenerTipoPagoPorDefecto( )

  List<TipoPago> listarTiposPago( )

  List<TipoPago> listarTiposPagoActivos( )

  TipoPago obtenerTipoPagosPorId( String idFormaPago )

  List<TipoPago> listarTiposPagoActivosMultipago( )
}
