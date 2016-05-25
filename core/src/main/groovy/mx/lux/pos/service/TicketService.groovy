package mx.lux.pos.service

import mx.lux.pos.java.repository.CuponMvJava
import mx.lux.pos.java.repository.JbSobres
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.java.repository.Repo
import mx.lux.pos.model.*

interface TicketService {

  void imprimeVenta( String idNotaVenta )

    void imprimePago(String orderId, Integer pagoId)

    void imprimeRx(String orderId, Boolean reimp)

    void imprimeSuyo(String idNotaVenta,JbNotas jbNotas)

  void imprimeVenta( String idNotaVenta, Boolean pNewOrder )

  boolean imprimeCierreTerminales( Date fechaCierre, List<ResumenDiario> resumenesDiario, Empleado empleado, String terminal )

  void imprimeResumenDiario( Date fechaCierre, Empleado empleado )

  void imprimeDepositosResumenDiario( Date fechaCierre )

  void imprimeUbicacionListaPrecios( ListaPrecios listaPrecios, List<Articulo> articulos )

  void imprimeCargaListaPrecios( ListaPrecios listaPrecios )

  void imprimeTransInv( TransInv pTrans )

  void imprimeTransInv( TransInv pTrans, Boolean pNewTransaction )

  void imprimeCancelacion( String idNotaVenta )

  void imprimePlanCancelacion( String idNotaVenta )

  void imprimeResumenExistencias( InvOhSummary pSummary )

  void imprimeResumenExistenciasLc( InvOhSummary pSummary )

  void imprimeReferenciaFiscal( String idFiscal )

  void imprimeComprobanteFiscal( String idFiscal )

  void imprimeCotizacion(  Cotizacion cotizacion, CotizaDet cotizaDet,  boolean totalizar, boolean convenio, String convenioDesc )

  void imprimeAperturaCaja( Date fechaApertura )

  void imprimeCotizacion( Integer pQuoteNbr, String idFactura )

  void imprimeRegresoMaterial( String idNotaVenta )

  void imprimeRecepcionMaterial( String idNotaVenta )

  void imprimePinoNoSurtido( String idNotaVenta )

  void imprimeTicketReuso( String idNotaVenta )

  void imprimeTicketEnvioLc( String idPedido )

  void imprimeTicketPedidosLcPendientes( Date fechaCierre )

  void imprimeCupon( CuponMvJava cuponMv, String titulo, BigDecimal monto )

  void imprimeCupon( CuponMv cuponMv, String titulo, BigDecimal monto )

  void imprimeResumenCuponCan( String idFactura, List<String> porDev )

  NotaVentaJava imprimeGarantia( BigDecimal montoGarantia, String idArticulo, String tipoSeguro, String idFactura, Boolean doubleEnsure )

  void imprimePackingPrevio( String idEmp, String estatus )

  void reimprimePacking( String viaje, Date fecha )

  void imprimeSobre( JbSobres jbSobre )

  void imprimeReposicion( Repo repo )


}
