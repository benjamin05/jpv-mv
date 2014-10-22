package mx.lux.pos.model

import mx.lux.pos.service.impl.ServiceFactory

class LcType {

  static LcType ADJUST = new LcType( ServiceFactory.inventory.obtenerTipoTransaccionAjuste() )
  static LcType ISSUE = new LcType( ServiceFactory.inventory.obtenerTipoTransaccionSalida() )
  static LcType RECEIPT = new LcType( ServiceFactory.inventory.obtenerTipoTransaccionEntrada() )
  static LcType SALES = new LcType( ServiceFactory.inventory.obtenerTipoTransaccionVenta() )
  static LcType RETURN = new LcType( ServiceFactory.inventory.obtenerTipoTransaccionDevolucion() )
  static LcType RETURN_XO = new LcType( ServiceFactory.inventory.obtenerTipoTransaccionDevolucionExtraordinaria() )
  static LcType OUTBOUND = new LcType( ServiceFactory.inventory.obtenerTipoTransaccionSalidaAlmacen() )
  static LcType INBOUND = new LcType( ServiceFactory.inventory.obtenerTipoTransaccionEntradaAlmacen() )

  private TipoTransInv trType

  private LcType( TipoTransInv pTrType ) {
    this.trType = pTrType
  }

  // Public methods
  boolean equals( Object pObj ) {
    boolean result = false
    if ( pObj instanceof LcType ) {
      result = this.trType.idTipoTrans.equals( ( pObj as LcType ).trType.idTipoTrans )
    } else if ( pObj instanceof TransInv ) {
      result = this.trType.idTipoTrans.equalsIgnoreCase( ( pObj as TransInv ).idTipoTrans )
    } else if ( pObj instanceof String ) {
      result = this.trType.idTipoTrans.equalsIgnoreCase( ( pObj as String ).trim() )
    }
    return result
  }

  String getTrType( ) {
    return trType.idTipoTrans
  }

}
