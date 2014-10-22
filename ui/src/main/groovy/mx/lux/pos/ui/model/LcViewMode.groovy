package mx.lux.pos.ui.model

import mx.lux.pos.model.TipoTransInv
import mx.lux.pos.service.InventarioService
import mx.lux.pos.ui.resources.ServiceManager

class LcViewMode {
  def static LcViewMode ISSUE
  def static LcViewMode QUERY
  def static LcViewMode RECEIPT
  def static LcViewMode RETURN
  def static LcViewMode ADJUST
  def static LcViewMode FILE_ADJUST
  def static LcViewMode SEND_ORDER

  def static LcViewMode OUTBOUND
  def static LcViewMode INBOUND

  private static List<LcViewMode> list
  private TipoTransInv trType
  private String text


  // Private Constructors - initialization occurs in listViewModes
  private LcViewMode( TipoTransInv pTrType ) {
    trType = pTrType
    text = String.format( "[%s] %s", trType.idTipoTrans, trType.descripcion )
  }

  private LcViewMode( String pText ) {
    trType = null
    text = String.format("<%s>", pText)
  }
   
  // Public Methods
  static List<LcViewMode> listViewModes() {
    if (list == null) {
      list = new ArrayList<LcViewMode>()
      InventarioService inventory = ServiceManager.getInventoryService()
      ISSUE = new LcViewMode( "Otras Salidas" )
      RECEIPT = new LcViewMode( inventory.obtenerTipoTransaccionOtraEntrada() )
      RECEIPT.text = "<Recepcion de Pedido>"
      QUERY = new LcViewMode( "Consulta" )
      ADJUST = new LcViewMode( inventory.obtenerTipoTransaccionAjuste() )
      RETURN = new LcViewMode( "Otras Entradas" )
      FILE_ADJUST = new LcViewMode( 'Ajuste archivo' )
      SEND_ORDER = new LcViewMode( "Pedidos por Enviar" )
      OUTBOUND = new LcViewMode( inventory.obtenerTipoTransaccionSalidaAlmacen() )
      INBOUND = new LcViewMode( inventory.obtenerTipoTransaccionEntradaAlmacen() )
      FILE_ADJUST.trType = inventory.obtenerTipoTransaccionAjuste()
      //list.addAll( [QUERY, ISSUE, RECEIPT, ADJUST, RETURN, OUTBOUND,INBOUND, FILE_ADJUST] )
      list.addAll( [QUERY, SEND_ORDER, RECEIPT] )
    }
    return list
  }
  
  String getText() {
    return text
  }
  
  TipoTransInv getTrType() {
    return trType
  }
  
  String toString() {
    return text
  }

}
