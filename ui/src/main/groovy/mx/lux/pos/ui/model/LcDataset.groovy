package mx.lux.pos.ui.model

import mx.lux.pos.model.PedidoLc
import mx.lux.pos.service.PedidoService
import mx.lux.pos.ui.model.adapter.LcFilter
import mx.lux.pos.ui.resources.ServiceManager

class LcDataset extends Dataset<PedidoLc> {

  LcDataset() {
    filter = new LcFilter()
  }

  LcFilter getFilter() {
    return (LcFilter) super.filter
  }

  void requestTransactions(  Boolean queryTrans ) {
    List<PedidoLc> rawList = new ArrayList<PedidoLc>()
    PedidoService service = ServiceManager.requestService
    if ( filter.isSiteToActive( ) ) {
      rawList = service.listarTransaccionesPorSucursalDestino( filter.siteTo.toString() )
    } else if ( filter.isTrTypeActive( ) ) {
      rawList = service.listarTransaccionesPorIdPedido( filter.trType )
    } else if ( filter.isSkuActive( ) ) {
      rawList = service.listarTransaccionesPorCliente( filter.sku )
    } else if ( filter.isPartCodeActive( ) ) {
      rawList = service.listarTransaccionesPorArticulo( filter.partCode )
    } else if ( filter.isReferenceActive() ) {
      rawList = service.listarTransaccionesPorFolio( filter.reference.trim() )
    } else if ( queryTrans ) {
        rawList = service.listarUltimasTransacciones( )
        filter.dateFrom = null
        filter.dateTo = null
    } else if ( filter.isDateRangeActive( ) && !filter.isReferenceActive() ) {
      rawList = service.listarTransaccionesPorRangoFecha( filter.dateFrom, filter.dateTo )
    }
      setItems( rawList )
  }

  public void setItems( List<PedidoLc> pRawList ) {
    super.setItems( pRawList )
    Collections.sort( dataset, new Comparator<PedidoLc>() {
        @Override
        int compare(PedidoLc o1, PedidoLc o2) {
            return o2.fechaAlta.compareTo(o1.fechaAlta)
        }
    } )
  }
  
}
