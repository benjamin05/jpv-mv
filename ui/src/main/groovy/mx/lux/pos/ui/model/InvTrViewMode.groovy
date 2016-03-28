package mx.lux.pos.ui.model

import mx.lux.pos.model.TipoTransInv
import mx.lux.pos.service.InventarioService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.IOController
import mx.lux.pos.ui.resources.ServiceManager

class InvTrViewMode {
  def static InvTrViewMode ISSUE
  def static InvTrViewMode ISSUE_FRAMES
  def static InvTrViewMode ISSUE_ACCESORIES
  def static InvTrViewMode OTHER_ISSUE
  def static InvTrViewMode QUERY
  def static InvTrViewMode RECEIPT
  def static InvTrViewMode OTHER_RECEIPT
  def static InvTrViewMode RETURN
  def static InvTrViewMode ADJUST
  def static InvTrViewMode FILE_ADJUST

  def static InvTrViewMode OUTBOUND
  def static InvTrViewMode INBOUND

  private static List<InvTrViewMode> list
  private TipoTransInv trType
  private String text
  

  // Private Constructors - initialization occurs in listViewModes
  private InvTrViewMode( TipoTransInv pTrType ) {
    trType = pTrType
    text = String.format( "[%s] %s", trType.idTipoTrans, trType.descripcion )
  }
  
  private InvTrViewMode( String pText ) {
    trType = null
    text = String.format("<%s>", pText)
  }
   
  // Public Methods
  static List<InvTrViewMode> listViewModes() {
    //if (list == null) {
      User user = Session.get( SessionItem.USER ) as User
      list = new ArrayList<InvTrViewMode>()
      InventarioService inventory = ServiceManager.getInventoryService()
      ISSUE = new InvTrViewMode( inventory.obtenerTipoTransaccionSalida() )
      RECEIPT = new InvTrViewMode( inventory.obtenerTipoTransaccionEntrada() )
      QUERY = new InvTrViewMode( "Consulta" )
      ADJUST = new InvTrViewMode( inventory.obtenerTipoTransaccionAjuste() )
      RETURN = new InvTrViewMode( inventory.obtenerTipoTransaccionDevolucionExtraordinaria() )
      //FILE_ADJUST = new InvTrViewMode( 'Ajuste archivo' )
      OUTBOUND = new InvTrViewMode( inventory.obtenerTipoTransaccionSalidaAlmacen() )
      OUTBOUND.text = '[SALIDA_FACTURAR]SALIDA POR FACTURAR'
      INBOUND = new InvTrViewMode( inventory.obtenerTipoTransaccionEntradaAlmacen() )
      //FILE_ADJUST.trType = inventory.obtenerTipoTransaccionAjuste()
      OTHER_RECEIPT = new InvTrViewMode( inventory.obtenerTipoTransaccionOtraEntrada() )
      OTHER_RECEIPT.text = "[OTRAS_ENTRADAS]LENTES DE CONTACTO"
      OTHER_ISSUE = new InvTrViewMode( inventory.obtenerTipoTransaccionOtraSalida() )
      OTHER_ISSUE.text = "[OTRAS_SALIDAS]LENTES DE CONTACTO"
      ISSUE_FRAMES = new InvTrViewMode( inventory.obtenerTipoTransaccionSalida() )
      ISSUE_FRAMES.text = "[SALIDA_ARMAZONES] SALIDA TOTAL ARMAZONES"
      ISSUE_ACCESORIES = new InvTrViewMode( inventory.obtenerTipoTransaccionSalida() )
      ISSUE_ACCESORIES.text = "[SALIDA_ACCESORIOS] SALIDA TOTAL ACCESORIOS"
      //list.addAll( [QUERY, ISSUE, RECEIPT, ADJUST, RETURN, OUTBOUND,INBOUND, FILE_ADJUST] )
      if( IOController.getInstance().isManager(user.username) ){
        list.addAll( [QUERY, RECEIPT, ISSUE, ADJUST, INBOUND, OUTBOUND, OTHER_RECEIPT, OTHER_ISSUE] )
      } else {
        list.addAll( [QUERY] )
      }
      if( Registry.totalOutputEnabled() ){
        list.addAll( [ISSUE_FRAMES, ISSUE_ACCESORIES] )
      }
    //}
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
