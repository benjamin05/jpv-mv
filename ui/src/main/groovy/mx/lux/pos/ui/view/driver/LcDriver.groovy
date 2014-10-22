package mx.lux.pos.ui.view.driver

import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.panel.LcView

import java.awt.event.MouseEvent

class LcDriver {

  // Internal Methods

  // Public Methods
  Boolean assign( LcView pView ) { return true }

  void assignPartSeed( LcView pView ) {
    pView.data.partSeed = pView.panel.txtPartSeed.getText().trim().toUpperCase()
    pView.data.flagOnPartSeed = false
    pView.panel.txtPartSeed.setText( pView.data.partSeed )
  }

  void assignQuantity( LcView pView ) {
    pView.data.postQty = 1
  }

  void consumePartSeed( LcView pView ) {
    pView.data.partSeed = ""
    pView.panel.txtPartSeed.setText( pView.data.partSeed )
  }

  void displayPartSeedPrompt( LcView pView ) {
    pView.panel.txtPartSeed.setText( "" )
  }

  Boolean doBeforeSave( LcView pView ) {
    return true
  }

  void enableUI( LcView pView ) {
    pView.panel.comboSiteTo.setLocked( true )
    UI_Standards.setLocked( pView.panel.txtType )
    UI_Standards.setLocked( pView.panel.txtIdPedido )
    UI_Standards.setLocked( pView.panel.txtNbr )
    UI_Standards.setLocked( pView.panel.txtEffDate )
    UI_Standards.setLocked( pView.panel.txtRef )
    UI_Standards.setLocked( pView.panel.txtUser )
    UI_Standards.setLocked( pView.panel.txtPartSeed )
    UI_Standards.setLocked( pView.panel.txtRemarks )

    pView.panel.selector.setVisible( false )
    pView.panel.btnCancel.setVisible( false )
    pView.panel.btnPrint.setEnabled( false )
    pView.panel.lblType.setText( pView.panel.TXT_TR_TYPE_LABEL )
  }

  void flagAdjust( LcView pView ) { }

  void flagQuantity( LcView pView, Boolean flagged ) {
    UI_Standards.setFlagged( pView.panel.txtType, flagged )
    if ( flagged ) {
      pView.data.txtStatus = pView.panel.MSG_UNABLE_TO_PARSE_QTY
      pView.panel.lblStatus.setText( pView.panel.MSG_UNABLE_TO_PARSE_QTY )
    }
  }

  void flagRemission( LcView pView ) { }

  Boolean isPartSeedValid( LcView pView ) { return false }

  Boolean isQuantityValid( LcView pView ) { return true }

  void processAdjust( LcView pView ) { }

  void processRemission( LcView pView ) { }

  void resetUI( LcView pView ) {
    pView.panel.comboSiteTo.setText( "" )
    pView.panel.txtType.setText( "" )
    pView.panel.selector.setText( "" )

    pView.panel.txtNbr.setText( "" )
    pView.panel.txtIdPedido.setText( "" )
    pView.panel.txtEffDate.setText( "" )
    pView.panel.txtRef.setText( "" )
    pView.panel.txtUser.setText( "" )

    pView.panel.txtPartSeed.setText( "" )
    pView.panel.txtRemarks.setText( "" )

    pView.panel.lblStatus.setText( "" )
    pView.panel.btnCancel.setVisible( false )
    pView.panel.btnPrint.setText( pView.panel.TXT_BTN_PRINT_CAPTION )
  }

  void refreshUI( LcView pView ) { }

  Boolean searchAdjust( LcView pView ) { return false }

  Boolean searchRemission( LcView pView ) { return false }

  void onButtonCancel( LcView pView ) {
    pView.panel.btnCancel.setVisible( false )
  }

  void onSkuDoubleClicked( LcView pView ) { }

  void onSkuOneClicked( LcView pView ) { }

  void onSkuOneRightClicked( LcView pView, MouseEvent ev ) { }

  Boolean isOrder( String bill, LcView pView ) { return false }
}
