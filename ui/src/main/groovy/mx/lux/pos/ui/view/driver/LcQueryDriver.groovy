package mx.lux.pos.ui.view.driver

import mx.lux.pos.model.LcType
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.PedidoLcDet
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.ui.model.LcSku
import mx.lux.pos.ui.model.Order
import mx.lux.pos.model.TransInvDetalle
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.adapter.InvTrAdapter
import mx.lux.pos.ui.model.adapter.LcAdapter
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.view.dialog.ReuseOrderLcDialog
import mx.lux.pos.ui.view.panel.LcView
import org.apache.commons.lang3.StringUtils
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS

import java.awt.event.MouseEvent

class LcQueryDriver extends LcDriver {

  // Internal methods
  protected boolean isReprintable(PedidoLc pTrans) {
    boolean reprintable = false
    /*if (LcType.ADJUST.equals( pTrans.idTipoTrans ) ) {
      reprintable = true
    } else if (LcType.ISSUE.equals( pTrans.idTipoTrans ) ) {
      reprintable = true
    } else if (LcType.RETURN_XO.equals( pTrans.idTipoTrans )) {
      reprintable = true
    } else if (LcType.RECEIPT.equals( pTrans.idTipoTrans )) {
        reprintable = true
    } else if (LcType.OUTBOUND.equals( pTrans.idTipoTrans )) {
        reprintable = true
    } else if (LcType.INBOUND.equals( pTrans.idTipoTrans )) {
        reprintable = true
    }*/
    return reprintable
  }

  // Public methods
  void enableUI( LcView pView ) {
    super.enableUI( pView )
    pView.panel.tBrowser.columnModel.getColumn(0).setMaxWidth(50)
    pView.panel.tBrowser.columnModel.getColumn(0).setMinWidth(50)
    pView.panel.tBrowser.columnModel.getColumn(1).setMaxWidth(100)
    pView.panel.tBrowser.columnModel.getColumn(1).setMinWidth(100)
    pView.panel.tBrowser.columnModel.getColumn(2).setHeaderValue(new String("Graduacion"))
    pView.panel.tBrowser.columnModel.getColumn(2).setMaxWidth(220)
    pView.panel.tBrowser.columnModel.getColumn(2).setMinWidth(220)
    pView.panel.tBrowser.columnModel.getColumn(3).setMaxWidth(100)
    pView.panel.tBrowser.columnModel.getColumn(3).setMinWidth(100)
      pView.panel.tBrowser.columnModel.getColumn(4).setMaxWidth(90)
      pView.panel.tBrowser.columnModel.getColumn(4).setMinWidth(90)
      pView.panel.tBrowser.columnModel.getColumn(5).setMaxWidth(90)
      pView.panel.tBrowser.columnModel.getColumn(5).setMinWidth(90)
      pView.panel.tBrowser.columnModel.getColumn(6).setMaxWidth(90)
      pView.panel.tBrowser.columnModel.getColumn(6).setMinWidth(90)
      pView.panel.tBrowser.columnModel.getColumn(7).setMaxWidth(90)
      pView.panel.tBrowser.columnModel.getColumn(7).setMinWidth(90)
    pView.panel.tBrowser.columnModel.getColumn(8).setMinWidth(0)
    pView.panel.tBrowser.columnModel.getColumn(8).setMaxWidth(0)
    pView.panel.lblType.setVisible( true )
    pView.panel.txtType.setVisible( true )
    pView.panel.selector.setVisible( true )
  }
  
  public void refreshUI( LcView pView ) {
    pView.panel.lblStatus.setText( pView.data.accessStatus( ) )
    if ( pView?.data?.qryInvTr != null ) {
        Integer cantArticulos = 0;
        for(PedidoLcDet detalle : pView?.data?.qryInvTr?.pedidoLcDets ){
            cantArticulos = cantArticulos+detalle.cantidad
        }
      pView.panel.txtType.setText( StringUtils.trimToEmpty( cantArticulos.toString() ) )
      pView.panel.txtNbr.setText( pView.adapter.getText( pView.data.qryInvTr,  LcAdapter.FLD_TR_NBR).equalsIgnoreCase("null") ? "" :
          pView.adapter.getText( pView.data.qryInvTr,  LcAdapter.FLD_TR_NBR) )
      pView.panel.txtIdPedido.setText( pView.adapter.getText( pView.data.qryInvTr,  LcAdapter.FLD_ID ) )
      pView.panel.txtEffDate.setText( pView.adapter.getText( pView.data.qryInvTr,  LcAdapter.FLD_TR_EFF_DATE ) )
        if( !pView.data.qryInvTr.cliente.isEmpty() ){
            pView.panel.txtRef.setText( pView.data?.qryInvTr?.cliente != null ? CustomerController.findCustomerById(pView.data?.qryInvTr?.cliente).fullName : '' )
        } else {
            pView.panel.txtRef.setText( '' )
        }
      pView.panel.txtUser.setText(  pView.adapter.getText( pView.data.qryUser ) )
      pView.panel.comboSiteTo.setSelection( pView.data.qrySiteTo )
      Order order = OrderController.findOrderByTicket(StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+StringUtils.trimToEmpty(pView.data.qryInvTr.id))
      if( order !=  null ){
        pView.panel.txtRemarks.setText( StringUtils.trimToEmpty(order.comments) )
      } else {
        pView.panel.txtRemarks.setText( "" )
      }

      pView.panel.btnPrint.setEnabled( this.isReprintable(pView.data.qryInvTr) )
    } else {
      pView.panel.txtType.setText( "" )
      pView.panel.txtNbr.setText( "" )
      pView.panel.txtIdPedido.setText( "" )
      pView.panel.txtEffDate.setText( "" )
      pView.panel.txtRef.setText( "" )
      pView.panel.txtUser.setText( "" )
      pView.panel.comboSiteTo.setText( "" )
      pView.panel.txtRemarks.setText( "" )
    }
    if( pView.data.skuList.size() > 0 ){
      pView.panel.txtNbr.setText( OrderController.estaCancelada(pView.adapter.getText( pView.data.qryInvTr,  LcAdapter.FLD_ID )) ? "CANCELADA" :
          StringUtils.trimToEmpty(pView.data.qryInvTr.folio) )
    }
    pView.panel.editQuantity = false
    pView.panel.selector.setText( pView.data.selectorText )
    pView.panel.browserSku.fireTableDataChanged()
  }

}
