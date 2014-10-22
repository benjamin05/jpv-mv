package mx.lux.pos.ui.view.driver

import mx.lux.pos.model.Articulo
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.model.ShipmentLine
import mx.lux.pos.model.TransInv
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.InventarioService
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.InvTrSku
import mx.lux.pos.ui.model.InvTrViewMode
import mx.lux.pos.ui.model.LcSku
import mx.lux.pos.ui.model.LcViewMode
import mx.lux.pos.ui.model.adapter.InvTrAdapter
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.dialog.EnvioPedidoLCDialog
import mx.lux.pos.ui.view.panel.InvTrView
import mx.lux.pos.ui.view.panel.LcView
import org.apache.commons.lang.StringUtils

import javax.swing.JOptionPane

class LcReceiptDriver extends LcDriver {

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
    UI_Standards.setLocked( pView.panel.txtRemarks, false )
    UI_Standards.setLocked( pView.panel.txtPartSeed, false )
    pView.panel.tBrowser.columnModel.getColumn(0).setMaxWidth(50)
    pView.panel.tBrowser.columnModel.getColumn(0).setMinWidth(50)
    pView.panel.tBrowser.columnModel.getColumn(1).setMaxWidth(100)
    pView.panel.tBrowser.columnModel.getColumn(1).setMinWidth(100)
    pView.panel.tBrowser.columnModel.getColumn(2).setMaxWidth(580)
    pView.panel.tBrowser.columnModel.getColumn(2).setMinWidth(580)
    pView.panel.tBrowser.columnModel.getColumn(3).setMaxWidth(100)
    pView.panel.tBrowser.columnModel.getColumn(3).setMinWidth(100)
      pView.panel.tBrowser.columnModel.getColumn(4).setMinWidth(0)
      pView.panel.tBrowser.columnModel.getColumn(4).setMaxWidth(0)
      pView.panel.tBrowser.columnModel.getColumn(5).setMinWidth(0)
      pView.panel.tBrowser.columnModel.getColumn(5).setMaxWidth(0)
      pView.panel.tBrowser.columnModel.getColumn(6).setMinWidth(0)
      pView.panel.tBrowser.columnModel.getColumn(6).setMaxWidth(0)
      pView.panel.tBrowser.columnModel.getColumn(7).setMinWidth(0)
      pView.panel.tBrowser.columnModel.getColumn(7).setMaxWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(8).setMinWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(8).setMaxWidth(0)
    pView.panel.btnPrint.setEnabled( !pView.data.flagOnDocument )
    pView.panel.btnPrint.setText( pView.panel.TXT_BTN_REGISTER_CAPTION )
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
      for(LcSku article : pView.data.skuList){
          quantity = quantity+article.qty
      }
    if( pView.data.postReference != "" && pView.data.skuList.size() <= 0 ){
        pView.panel.lblStatus.setText( 'Articulo '+'['+pView.data.claveCodificada+']'+' no existe' )
    } else {
        pView.panel.lblStatus.setText( pView.data.accessStatus() )
    }
    pView.panel.editQuantity = true
    pView.panel.txtEffDate.setText( pView.adapter.getText( pView.data, InvTrAdapter.FLD_TODAY ) )
    pView.panel.txtRef.setText( pView.data.postReference )
    pView.panel.txtType.setText( String.format( '%d', quantity ) )
    pView.panel.txtUser.setText( pView.adapter.getText( pView.data.currentUser ) )
    pView.panel.browserSku.fireTableDataChanged()
    pView.panel.txtIdPedido.setText( StringUtils.trimToEmpty(pView.data.claveCodificada) )
    pView.panel.txtRef.setText( StringUtils.trimToEmpty(pView.data.cliente) )
    pView.panel.txtNbr.setText( StringUtils.trimToEmpty(pView.data.folio) )
    if(pView.data.skuList.size() > 0){
      pView.panel.txtPartSeed.editable = false
    } else {
      pView.panel.txtPartSeed.editable = true
    }
  }

  Boolean searchRemission( LcView pView ) {
    InventarioService service = ServiceManager.inventoryService
    List<TransInv> trList = service.listarTransaccionesPorTipoAndReferencia( InvTrViewMode.RECEIPT.trType.idTipoTrans,
        pView.data.receiptDocument.fullRef )
    if ( trList.size() > 0 ) {
      pView.data.flagOnDocument = true
      pView.data.documentWarning = String.format( pView.panel.MSG_RECEIPT_WARNING,
          trList.size(), LcViewMode.RECEIPT.trType.idTipoTrans, pView.data.receiptDocument.fullRef,
          pView.adapter.getText( trList[ 0 ].fecha ), pView.adapter.getText( trList[ trList.size() - 1 ].fecha ) )
    }
    return ( trList.size() > 0 )
  }


    void onSkuDoubleClicked( LcView pView ) {
      if ( pView.panel.tBrowser.selectedRow >= 0 ) {
        LcSku line = pView.data.skuList[ pView.panel.tBrowser.selectedRow ]
        String msg = String.format( pView.panel.MSG_CONFIRM_REMOVE_LC, line.description )
        Integer selection = JOptionPane.showConfirmDialog( pView.panel, msg, pView.panel.TXT_CONFIRM_TITLE,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE )
        if ( selection.equals( JOptionPane.OK_OPTION ) ) {
          pView.data.skuList.remove( line )
          pView.fireRefreshUI( )
        }
      }
    }

}
