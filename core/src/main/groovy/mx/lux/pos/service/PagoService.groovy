package mx.lux.pos.service

import mx.lux.pos.model.Pago
import mx.lux.pos.model.TipoPago

interface PagoService {

  Pago obtenerPago( Integer id )

  List<Pago> listarPagosPorIdFactura( String idFactura )

  Pago actualizarPago( Pago pago )

  Boolean obtenerTipoPagosDolares( String formaPago )

    BigInteger reciboSeq()

}
