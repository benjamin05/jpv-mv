package mx.lux.pos.ui.view.driver

import groovy.model.DefaultTableColumn
import groovy.model.PropertyModel
import mx.lux.pos.model.Articulo
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.Cliente
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.model.PedidoLcDet
import mx.lux.pos.model.ShipmentLine
import mx.lux.pos.model.Sucursal
import mx.lux.pos.model.TransInv
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.ClienteService
import mx.lux.pos.service.InventarioService
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.service.SucursalService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.CuponMvView
import mx.lux.pos.ui.model.InvTrSku
import mx.lux.pos.ui.model.LcSku
import mx.lux.pos.ui.model.LcViewMode
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.adapter.InvTrAdapter
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.dialog.EditPaymentDialog
import mx.lux.pos.ui.view.dialog.EnvioPedidoLCDialog
import mx.lux.pos.ui.view.dialog.ReuseOrderLcDialog
import mx.lux.pos.ui.view.panel.LcView
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import org.apache.commons.lang.StringUtils

import javax.swing.DefaultCellEditor
import javax.swing.JOptionPane
import javax.swing.table.TableColumn
import java.awt.event.MouseEvent
import java.text.NumberFormat
import java.text.SimpleDateFormat

class LcSendOrderDriver extends LcDriver {

  public static final String TAG_CNCELADA = "T"

  // Public methods
  Boolean assign( LcView pView ) {
    Boolean validated
    // Validate
    validated = ( pView.data.getSkuList().size() > 0 )

    // Assign
    if ( validated ) {
        pView.data.postRemarks = pView.panel.txtRemarks.getText( )
      // No assign, no modifications allowed, 
      // As the remission was processed a transaction is generated
    }

    return validated
  }

  void enableUI( LcView pView ) {
    super.enableUI( pView )
    UI_Standards.setLocked( pView.panel.txtRemarks, true )
    UI_Standards.setLocked( pView.panel.txtPartSeed, true )
    pView.panel.tBrowser.columnModel.getColumn(0).setHeaderValue(new String("Linea"))
    pView.panel.tBrowser.columnModel.getColumn(0).setMaxWidth(50)
    pView.panel.tBrowser.columnModel.getColumn(1).setHeaderValue(new String("Fecha"))
    pView.panel.tBrowser.columnModel.getColumn(1).setMaxWidth(100)
    pView.panel.tBrowser.columnModel.getColumn(2).setHeaderValue(new String("Factura"))
    pView.panel.tBrowser.columnModel.getColumn(2).setMinWidth(90)
    pView.panel.tBrowser.columnModel.getColumn(2).setMaxWidth(90)
    pView.panel.tBrowser.columnModel.getColumn(3).setHeaderValue(new String("Cantidad"))
    pView.panel.tBrowser.columnModel.getColumn(3).setMaxWidth(80)
    pView.panel.tBrowser.columnModel.getColumn(4).setMinWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(4).setMaxWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(5).setMinWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(5).setMaxWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(6).setMinWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(6).setMaxWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(7).setMinWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(7).setMaxWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(8).setHeaderValue(new String("Descripcion"))
    pView.panel.tBrowser.columnModel.getColumn(8).setMinWidth(510)
    pView.panel.tBrowser.columnModel.getColumn(8).setMaxWidth(510)
    obtenerFacturasPorEnviar( pView )
    pView.panel.btnPrint.setEnabled( false )
    pView.panel.selector.setVisible( false )
  }

  void flagRemission( LcView pView ) {
    pView.panel.lblStatus.setText( pView.data.documentWarning )
  }

  public void processRemission( LcView pView ) {
    Boolean unknownArticle = false
    ArticuloService partMaster = ServiceManager.partService
    pView.data.postReference = pView.data.receiptDocument.fullRef
    for ( ShipmentLine det in pView.data.receiptDocument.lines ) {
      Articulo part = partMaster.obtenerArticulo( det.sku, false )
      if ( part != null ) {
        pView.data.addPart( part, det.qty )
      } else {
          unknownArticle = true
          pView.data.claveCodificada = det.sku.toString()
      }
    }
    if( unknownArticle ){
        pView.data.skuList.clear()
    }
  }


  void displayPartSeedPrompt( LcView pView ) {
    pView.panel.lblStatus.setText( pView.data.claveCodificada )
  }


  public Boolean isOrder( String bill, LcView pView ){
    Boolean isOrder = false
    ArticuloService service = ServiceManager.partService
    String site = StringUtils.trimToEmpty(Registry.currentSite.toString())
    PedidoLc pedidoLc =  service.buscaPedidoLc( StringUtils.trimToEmpty(bill) )
    if( pedidoLc != null ){
      if( pedidoLc.fechaRecepcion == null ){
        isOrder = true
      } else {
        pView.data.claveCodificada = "El pedido ${bill} ya fue recibido"
      }
    } else {
      pView.data.claveCodificada = "No existe el Pedido ${bill}"
    }
    return isOrder
  }


  public void refreshUI( LcView pView ) {
      Integer quantity = 0
      quantity = pView.data.skuList.size()
      /*for(LcSku article : pView.data.skuList){
          quantity = quantity+article.qty
      }*/
    if( pView.data.postReference != "" && pView.data.skuList.size() <= 0 ){
        pView.panel.lblStatus.setText( 'Articulo '+'['+pView.data.claveCodificada+']'+' no existe' )
    } else {
        pView.panel.lblStatus.setText( pView.data.accessStatus() )
    }
    pView.panel.editQuantity = true
    pView.panel.txtRef.setText( pView.data.postReference )
    pView.panel.txtType.setText( String.format( '%d', quantity ) )
    pView.panel.txtEffDate.setText("")
    pView.panel.txtUser.setText("")
    pView.panel.browserSku.fireTableDataChanged()
  }

  Boolean searchRemission( LcView pView ) {
    InventarioService service = ServiceManager.inventoryService
    List<TransInv> trList = service.listarTransaccionesPorTipoAndReferencia( InvTrViewMode.RECEIPT.trType.idTipoTrans,
        pView.data.receiptDocument.fullRef )
    if ( trList.size > 0 ) {
      pView.data.flagOnDocument = true
      pView.data.documentWarning = String.format( pView.panel.MSG_RECEIPT_WARNING,
          trList.size, LcViewMode.RECEIPT.trType.idTipoTrans, pView.data.receiptDocument.fullRef,
          pView.adapter.getText( trList[ 0 ].fecha ), pView.adapter.getText( trList[ trList.size - 1 ].fecha ) )
    }
    return ( trList.size() > 0 )
  }


  void obtenerFacturasPorEnviar( LcView pView ){
      pView.data.skuList.clear()
      NotaVentaService orderMaster = ServiceManager.orderService
      List<PedidoLc> lstPedido = orderMaster.obtienePedidosLcPorEnviar()
      List<PedidoLc> pedido = new ArrayList<>()
      for(PedidoLc pedidoLc1 : lstPedido){
        String ticket = StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+StringUtils.trimToEmpty(pedidoLc1.id)
        NotaVenta notaVenta = orderMaster.obtenerNotaVentaPorTicket( ticket )
        if(pedidoLc1.id.isNumber() && notaVenta != null && !notaVenta.sFactura.equalsIgnoreCase(TAG_CNCELADA)){
           pedido.add( pedidoLc1 )
        }
      }
      Collections.sort(pedido, new Comparator<PedidoLc>() {
          @Override
          int compare(PedidoLc o1, PedidoLc o2) {
              return o1.id.compareTo(o2.id)
          }
      })
      Integer contador = 1
      for(PedidoLc pedidoLc : pedido){
          String articulo = ""
          String graduacion = ""
          Integer cantidad = 0
          String descripcion = ""
          for(PedidoLcDet pedidoLcDet : pedidoLc.pedidoLcDets){
            articulo = pedidoLcDet.modelo
            graduacion = graduacion+" "+pedidoLcDet.curvaBase+","+pedidoLcDet.diametro+","+pedidoLcDet.esfera+","+
                    pedidoLcDet.cilindro+","+pedidoLcDet.eje
            for(int i = 0;i<5;i++){
              if(graduacion.startsWith(" ")){
                graduacion = graduacion.replaceFirst(" ","")
              } else if(graduacion.startsWith(",")){
                graduacion = graduacion.replaceFirst(",","")
              }
            }
            descripcion = descripcion+" "+String.format("%s(%s)",articulo, graduacion)
            cantidad = cantidad+pedidoLcDet.cantidad
          }
          LcSku lc = new LcSku( contador, pedidoLc.fechaAlta, pedidoLc.id, descripcion, cantidad )
          contador = contador+1
          pView.data.skuList.add( lc )
      }
  }



    void onSkuDoubleClicked( LcView pView ) {
      if ( pView.panel.tBrowser.selectedRow >= 0 ) {
        LcSku line = pView.data.skuList[ pView.panel.tBrowser.selectedRow ]
        ArticuloService itemMaster = ServiceManager.partService
        PedidoLc pedidoLc = itemMaster.buscaPedidoLc(StringUtils.trimToEmpty(line.graduation))
        if( pedidoLc != null ){
            EnvioPedidoLCDialog dialogPedido = new EnvioPedidoLCDialog( pedidoLc )
            dialogPedido.show()
            obtenerFacturasPorEnviar( pView )
            pView.panel.browserSku.fireTableDataChanged()
        }
      }
    }


    void onSkuOneClicked( LcView pView ) {
        if ( pView.panel.tBrowser.selectedRow >= 0 ) {
            LcSku line = pView.data.skuList[ pView.panel.tBrowser.selectedRow ]
            ArticuloService itemMaster = ServiceManager.partService
            NotaVentaService orderMaster = ServiceManager.orderService
            SucursalService siteMaster = ServiceManager.siteService
            PedidoLc pedidoLc = itemMaster.buscaPedidoLc(StringUtils.trimToEmpty(line.graduation))
            NotaVenta nota = orderMaster.obtenerNotaVentaPorTicket( StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+StringUtils.trimToEmpty(line.graduation))
            if( pedidoLc != null ){
              ClienteService clientMaster = ServiceManager.customerService
              Integer idCLiente = 0
              try{
                idCLiente = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(pedidoLc.cliente))
              } catch ( NumberFormatException e ) { println e }
              Cliente cliente = clientMaster.obtenerCliente(idCLiente.intValue())
              SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy")
              pView.panel.txtIdPedido.setText( pedidoLc.id )
              pView.panel.txtEffDate.setText( df.format(pedidoLc.fechaAlta) )
              pView.panel.txtRef.setText( StringUtils.trimToEmpty(cliente?.nombreCompleto) )
              pView.panel.txtUser.setText( nota.idEmpleado )
              pView.panel.txtRemarks.setText( nota.getObservacionesNv() )
            }
        }
    }


    void onSkuOneRightClicked( LcView pView, MouseEvent ev ){
        if ( pView.panel.tBrowser.selectedRow >= 0 ) {
            LcSku line = pView.data.skuList[ pView.panel.tBrowser.selectedRow ]
            ArticuloService itemMaster = ServiceManager.partService
            PedidoLc pedidoLc = itemMaster.buscaPedidoLc(StringUtils.trimToEmpty(line.graduation))
            if( pedidoLc != null ){
                pView.panel.sb.popupMenu {
                    menuItem( text: 'Reusar',
                            actionPerformed: {
                                ReuseOrderLcDialog dialogReuse = new ReuseOrderLcDialog( org.apache.commons.lang.StringUtils.trimToEmpty(pedidoLc.id) )
                                dialogReuse.show()
                                obtenerFacturasPorEnviar( pView )
                                pView.panel.browserSku.fireTableDataChanged()
                            }
                    )
                }.show( ev.component, ev.x, ev.y )
            }
        }
    }
}
