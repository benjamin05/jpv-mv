package mx.lux.pos.ui.view.driver

import mx.lux.pos.model.Articulo
import mx.lux.pos.model.ShipmentLine
import mx.lux.pos.model.Sucursal
import mx.lux.pos.model.TransInv
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.InventarioService
import mx.lux.pos.ui.controller.InvTrController
import mx.lux.pos.ui.model.InvTrSku
import mx.lux.pos.ui.model.InvTrViewMode
import mx.lux.pos.ui.model.adapter.InvTrAdapter
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.panel.InvTrView
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JOptionPane

class InvTrMassiveReceiptDriver extends InvTrDriver {

    Logger logger = LoggerFactory.getLogger( InvTrDriver.class )

  // Public methods
  Boolean assign( InvTrView pView ) {
    Boolean validated
    // Validate
    validated = ( pView.data.getSkuList().size() > 0 )

    // Assign
    if ( validated ) {
      // No assign, no modifications allowed, 
      // As the remission was processed a transaction is generated
    }

    return validated
  }

  void enableUI( InvTrView pView ) {
    super.enableUI( pView )
    pView.panel.comboSiteTo.setLocked( true )
    UI_Standards.setLocked( pView.panel.txtPartSeed, false )
    UI_Standards.setLocked( pView.panel.txtRemarks, false )
    /*pView.panel.btnPrint.setEnabled( !pView.data.flagOnDocument )
    pView.panel.btnPrint.setText( pView.panel.TXT_BTN_REGISTER_CAPTION )
    pView.panel.selector.setVisible( false )*/
    pView.panel.lblType.setVisible( true )
    pView.panel.txtType.setVisible( true )
    pView.panel.lblType.setText( pView.panel.TXT_TR_QUANTITY_LABEL )
    pView.panel.txtType.setText( "0" )
    pView.panel.txtType.setEditable( false )
    pView.panel.selector.setVisible( false )
    pView.panel.btnPrint.setEnabled( true )
  }

  void flagRemission( InvTrView pView ) {
    pView.panel.lblStatus.setText( pView.data.documentWarning )
  }

  public void processRemission( InvTrView pView ) {
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

  public void refreshUI( InvTrView pView ) {
      /*Integer quantity = 0
      for(InvTrSku article : pView.data.skuList){
          quantity = quantity+article.qty
      }
    if( pView.data.postReference != "" && pView.data.skuList.size() <= 0 ){
        pView.panel.lblStatus.setText( 'Articulo '+'['+pView.data.claveCodificada+']'+' no existe' )
    } else {
        pView.panel.lblStatus.setText( pView.data.accessStatus() )
    }
    pView.panel.txtEffDate.setText( pView.adapter.getText( pView.data, InvTrAdapter.FLD_TODAY ) )
    pView.panel.txtRef.setText( pView.data.postReference )
      pView.panel.txtType.setText( String.format( '%d', quantity ) )
    pView.panel.txtUser.setText( pView.adapter.getText( pView.data.currentUser ) )
    pView.panel.browserSku.fireTableDataChanged()*/
      renderFlaggedItems( pView )
      Integer quantity = 0
      for(InvTrSku article : pView.data.skuList){
          quantity = quantity+article.qty
      }
      pView.panel.lblStatus.setText( pView.data.accessStatus( ) )
      pView.panel.txtEffDate.setText( pView.adapter.getText( pView.data, InvTrAdapter.FLD_TODAY ) )
      pView.panel.txtUser.setText( pView.adapter.getText( pView.data.currentUser ) )
      pView.panel.browserSku.fireTableDataChanged( )
      pView.panel.txtType.setText( String.format( '%d', quantity ) )
      if(quantity > 0){
          pView.panel.comboSiteTo.setSelection( pView.panel.comboSiteTo.selection )
      } else {
          pView.panel.comboSiteTo.setItems(new ArrayList<Sucursal>())
      }
      if( !pView.data.viewMode.text.contains("ENVIO A ALMACEN") ){
        pView.panel.lblCauseIssue.visible = false
        pView.panel.cbReasonsIssue.visible = false
      }
  }

  Boolean searchRemission( InvTrView pView ) {
    InventarioService service = ServiceManager.inventoryService
    List<TransInv> trList = service.listarTransaccionesPorTipoAndReferencia( InvTrViewMode.RECEIPT.trType.idTipoTrans,
        pView.data.receiptDocument.fullRef )
    if ( trList.size > 0 ) {
      pView.data.flagOnDocument = true
      pView.data.documentWarning = String.format( pView.panel.MSG_RECEIPT_WARNING,
          trList.size, InvTrViewMode.RECEIPT.trType.idTipoTrans, pView.data.receiptDocument.fullRef,
          pView.adapter.getText( trList[ 0 ].fecha ), pView.adapter.getText( trList[ trList.size - 1 ].fecha ) )
    }
    return ( trList.size() > 0 )
  }


    Boolean isQuantityValid( InvTrView pView ) {
        Boolean valid = false
        String qtyText = ""
        try {
            String[] quantity = pView.panel.txtPartSeed.text.split(',')
            String contador = '1'
            if( quantity.length > 1 ){
                contador = quantity[1]
            }
            qtyText = StringUtils.trimToEmpty( contador )
            Integer qty = Integer.parseInt( qtyText )
            valid = (qty > 0)
        } catch ( NumberFormatException e ) {
            logger.debug( String.format( 'Unable to parse Quantity: %s', qtyText))
        }
        return valid
    }


    void assignQuantity( InvTrView pView ) {
        Integer qty = 1
        String qtyText = ""
        try {
            String[] quantity = pView.panel.txtPartSeed.text.split(',')
            String contador = '1'
            if( quantity.length > 1 ){
                contador = quantity[1]
            }
            qtyText = StringUtils.trimToEmpty( contador )
            qty = Integer.parseInt( qtyText )
        } catch ( NumberFormatException e ) {
            logger.debug( String.format( 'Unable to parse Quantity: %s', qtyText))
        }
        pView.data.postQty = qty
    }


    void flagQuantity( InvTrView pView, Boolean flagged ) {
        UI_Standards.setFlagged( pView.panel.txtType, flagged )
        if ( flagged ) {
            pView.data.txtStatus = pView.panel.MSG_UNABLE_TO_PARSE_QTY
            pView.panel.lblStatus.setText( pView.panel.MSG_UNABLE_TO_PARSE_QTY )
        }
    }


    Boolean isPartSeedValid( InvTrView pView ) {
        String seed = pView.panel.txtPartSeed.getText( ).trim( ).toUpperCase( )
        Boolean valid = (! StringUtils.isEmpty( seed ) )
        if (valid) {
            valid = ! seed.equals( pView.panel.TXT_SEED_ISSUE_PROMPT.trim( ).toUpperCase( ) )
        }
        return valid
    }


    void assignPartSeed( InvTrView pView ) {
      println pView.panel.comboSiteTo
        pView.data.flagOnSiteTo &= ( pView.panel.comboSiteTo.selection == null )
        pView.data.flagOnRemarks &= ( ! isRemarksValid( pView ) )
        super.assignPartSeed( pView )
    }

    private Boolean isRemarksValid( InvTrView pView ) {
        String rmks = pView.panel.txtRemarks.getText( ).trim( )
        Boolean valid = (! StringUtils.isEmpty( rmks ) )
        if (valid) {
            valid = ! rmks.equalsIgnoreCase( pView.panel.TXT_REMARKS_PROMPT.trim( ) )
        }
        return valid
    }


    void displayPartSeedPrompt( InvTrView pView ) {
        pView.panel.txtPartSeed.setText( pView.panel.TXT_SEED_ISSUE_PROMPT )
    }


    protected void renderFlaggedItems( InvTrView pView )  {
        if ( pView.data.flagOnSiteTo ) {
            pView.panel.comboSiteTo.renderAsFlagged( )
            pView.panel.comboSiteTo.setText( pView.panel.TXT_SITE_TO_PROMPT )
        } else {
            pView.panel.comboSiteTo.renderAsFlagged( false )
        }

        if ( pView.data.flagOnPartSeed ) {
            UI_Standards.setFlagged( pView.panel.txtPartSeed )
            pView.panel.txtPartSeed.setText( pView.panel.TXT_SEED_ISSUE_PROMPT )
        } else {
            UI_Standards.setFlagged( pView.panel.txtPartSeed, false )
        }

        if ( pView.data.flagOnRemarks ) {
            UI_Standards.setFlagged( pView.panel.txtRemarks  )
            pView.panel.txtRemarks.setText( pView.panel.TXT_REMARKS_PROMPT )
        } else {
            UI_Standards.setFlagged( pView.panel.txtRemarks, false )
        }
    }


  void onSkuDoubleClicked( InvTrView pView ) {
    logger.debug( "[Driver] Double clicked on sku table" )
    if ( pView.panel.tBrowser.selectedRow >= 0 ) {
      InvTrSku line = pView.data.skuList[ pView.panel.tBrowser.selectedRow ]
      String msg = String.format( "Desea eliminar del ticket de entrada el artículo:\n\n  [%d] %s\n \n ", line.sku, line.description )
      Integer selection = JOptionPane.showConfirmDialog( pView.panel, msg, pView.panel.TXT_CONFIRM_TITLE,
              JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE )
      if ( selection.equals( JOptionPane.OK_OPTION ) ) {
        pView.data.skuList.remove( line )
        pView.fireRefreshUI( )
      }
    }
  }
}
