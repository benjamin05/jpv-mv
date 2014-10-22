package mx.lux.pos.ui.model.adapter

import mx.lux.pos.model.InvTrDetRequest
import mx.lux.pos.model.LcRequest
import mx.lux.pos.ui.model.Lc
import mx.lux.pos.ui.model.InvTrSku
import mx.lux.pos.ui.model.LcSku

class LcRequestAdapter {

  static LcRequest getRequest( Lc pBuffer ) {
    LcRequest request = new LcRequest()
    request.effDate = new Date()
    request.idUser = pBuffer.currentUser.username
    request.remarks = pBuffer.postRemarks
      if("ENTRADA_TIENDA".equalsIgnoreCase(pBuffer.postTrType.idTipoTrans)){
        request.siteFrom = ( pBuffer.receiptDocument.siteFrom != null ? pBuffer.receiptDocument.siteFrom : null )
        request.reference = pBuffer.claveCodificada
      } else if("OTRAS_ENTRADAS".equalsIgnoreCase(pBuffer.postTrType.idTipoTrans)) {
        request.reference = pBuffer.claveCodificada
      } else {
        request.reference = pBuffer.postReference
      }
    request.siteTo = ( pBuffer.postSiteTo != null ? pBuffer.postSiteTo.id : null )
    request.trType = pBuffer.postTrType.idTipoTrans
    for (LcSku inSku in pBuffer.skuList) {
      request.skuList.add( new InvTrDetRequest( inSku.sku, inSku.qty ) )
    }
    return request
  }
  
}
