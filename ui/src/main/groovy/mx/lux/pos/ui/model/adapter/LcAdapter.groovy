package mx.lux.pos.ui.model.adapter

import mx.lux.pos.model.Articulo
import mx.lux.pos.model.Cliente
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.model.PedidoLcDet
import mx.lux.pos.model.Sucursal
import mx.lux.pos.model.TransInv
import mx.lux.pos.model.TransInvDetalle
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.Lc
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.resources.ServiceManager
import org.apache.commons.lang3.StringUtils

import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class LcAdapter extends StringAdapter {

  static final String FLD_TODAY = "Today"
  private static final String FLD_TR_EFF_DATE = "fechaAlta"
  private static final String FLD_ID = "id"
  private static final String FLD_TR_PART_LIST = "Lc.[Part.PartCode]"
  private static final String FLD_TR_NBR = "folio"

  DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )

  String getText( Object pObject ) {
    String text = super.getText( pObject )
    if ( pObject instanceof Date ) {
      text = df.format( ( Date ) pObject )
    } else if ( pObject instanceof User ) {
      text = getUserToString( pObject as User )
    }
    return text
  }

  String getText( Object pObject, String pField ) {
    String text = super.getText( pObject, pField )
    if ( pObject instanceof Lc ) {
      Lc data = ( Lc ) pObject
      if ( pField.equals( FLD_TODAY ) ) {
        text = df.format( data.today )
      }
    } else if ( pObject instanceof PedidoLc ) {
      PedidoLc tr = ( PedidoLc ) pObject
      if ( pField.equals( FLD_TR_EFF_DATE ) ) {
        text = df.format( tr.fechaAlta )
      } else if ( pField.equals( FLD_TR_NBR ) ) {
        text = String.format( "%s", tr.folio )
      } else if ( pField.equals( FLD_TR_PART_LIST ) ) {
        text = getPartList( tr )
      } else if ( pField.equals( FLD_ID ) ) {
          Sucursal sucursal = ServiceManager.siteService.obtenerSucursal(Registry.currentSite)
          NotaVenta nota = ServiceManager.orderService.obtenerNotaVentaPorTicket( sucursal.centroCostos+"-"+tr?.id )
          if( nota == null ){
              nota = ServiceManager.orderService.obtenerNotaVenta( tr?.id )
          }
          if(nota != null && StringUtils.trimToEmpty(nota.factura).length() > 0){
            text = String.format( "%s%s", sucursal != null ? sucursal.centroCostos+"-" : "",
                  nota.factura )
          } else {
            text = String.format( "%s", tr.id )
          }
      }
    }
    return text
  }

  String getClient( String idCliente ) {
    Integer id = 0
    try {
      id = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(idCliente))
    } catch (NumberFormatException e ){println e}
    Cliente cliente = ServiceManager.customerService.obtenerCliente( id )
    return cliente != null ? cliente.nombreCompleto : ''
  }

  String getModelList( String idPedido ) {
    String listModels = ""
    PedidoLc pedidoLc = ServiceManager.partService.buscaPedidoLc( idPedido )
    if( pedidoLc != null ){
      for(PedidoLcDet det : pedidoLc.pedidoLcDets){
          listModels = listModels+","+det.modelo
      }
      listModels = listModels.replaceFirst(",","")
    }
    return listModels
  }

  private String getPartList( PedidoLc pPedidoLc ) {
    String partCodes = ""
    List<String> skuList = new ArrayList<String>( pPedidoLc.pedidoLcDets.size() )
    for ( PedidoLcDet det in pPedidoLc.pedidoLcDets ) {
      if ( !skuList.contains( det.modelo ) ) {
        skuList.add( det.modelo )
      }
    }
    if ( skuList.size() > 0 ) {
      List<Articulo> partList = new ArrayList<>()
        for(String art : skuList){
        partList.add(ServiceManager.partService.obtenerArticuloPorArticulo( art, false ))
      }
      for ( Articulo part in partList ) {
        partCodes += ( partCodes.length() == 0 ? "" : ", " ) + part.articulo
      }
    }
    return partCodes
  }

  String getUserToString( User pUser ) {
    String str = String.format( "[%s] %s", StringUtils.trimToEmpty( pUser.username ), pUser.fullName )
    return str
  }
}
