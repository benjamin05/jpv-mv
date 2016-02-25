package mx.lux.pos.ui.view.panel

import mx.lux.pos.model.InvAdjustSheet
import mx.lux.pos.model.Shipment
import mx.lux.pos.ui.controller.LcController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.model.adapter.LcAdapter
import mx.lux.pos.ui.view.component.NavigationBar.Command
import mx.lux.pos.ui.view.component.NavigationBarListener
import mx.lux.pos.ui.view.driver.*
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

// Separates ui mask and ui behavior
//   ui mask defined in LcPanel
//   ui behavior defined in LcDriver and its subclasses
class LcView implements NavigationBarListener {

  private static final Logger logger = LoggerFactory.getLogger( LcView.class )

  LcController controller
  Lc data
  LcAdapter adapter = new LcAdapter()
  LcPanel panel
  LcDriver driver
  LcDriver queryDriver = new LcQueryDriver()
  LcDriver receiptDriver = new LcReceiptDriver()
  LcDriver issueDriver = new LcIssueDriver()
  //LcDriver adjustDriver = new InvTrAdjustDriver()
  //LcDriver fileAdjustDriver = new InvTrFileAdjustDriver()
  LcDriver returnDriver = new LcReturnDriver()
  LcDriver doNothingDriver = new LcDriver()
  LcDriver sendOrderDriver = new LcSendOrderDriver()
  //LcDriver outboundDriver = new InvTrOutBoundDriver()
  //LcDriver inboundDriver = new InvTrInBoundDriver()

  Boolean uiDisabled = false

  MouseListener skuListener = null

    LcView( ) {
    controller = LcController.instance
    data = new Lc()
    panel = new LcPanel( this )
    wireComponents()
  }

  // Actions
  void fireConsumePartSeed( ) {
    driver.consumePartSeed( this )
  }

  void fireDisplay( ) {
    driver.enableUI( this )
    driver.refreshUI( this )
  }

  void fireRefreshUI( ) {
    driver.refreshUI( this )
  }

  void fireResetUI( ) {
    driver.resetUI( this )
  }

  // Inter process communications
  void notifyDocument( Shipment pDocument ) {
    data.receiptDocument = pDocument
    data.adjustDocument = null
    driver.processRemission( this )
    fireResetUI()
    fireDisplay()

    LcView view = this
    Thread.start( {
      driver.searchRemission( view )
      if ( data.flagOnDocument ) {
        driver.flagRemission( view )
      }
    } )
  }

  void notifyDocument( InvAdjustSheet pDocument ) {
    data.receiptDocument = null
    data.adjustDocument = pDocument
    if ( pDocument.site == ( Session.get( SessionItem.BRANCH ) as Branch ).id ) {
      driver.processAdjust( this )
      fireResetUI()
      fireDisplay()

      LcView view = this
      Thread.start( {
        driver.searchAdjust( view )
        if ( data.flagOnDocument ) {
          driver.flagAdjust( view )
        }
      } )
    } else {
      JOptionPane.showMessageDialog( this.panel, String.format( this.panel.MSG_INCORRECT_BRANCH, pDocument.site ),
          this.data.viewMode.toString(), JOptionPane.ERROR_MESSAGE )
      SwingUtilities.invokeLater( new Runnable( ) {
        void run( ) {
          panel.comboViewMode.setSelection( LcViewMode.QUERY )
        }
      })
    }
  }

  void notifyViewMode( LcViewMode pViewMode ) {
        logger.debug( String.format( "[View] Applying View Mode <%s>", pViewMode.text ) )
    data.viewMode = pViewMode
    if ( !data.viewMode.equals( panel.comboViewMode.getSelection() ) ) {
      panel.comboViewMode.setSelection( data.viewMode )
    }
    if ( LcViewMode.ISSUE.equals( pViewMode ) ) {
      driver = issueDriver
    } else if ( LcViewMode.RECEIPT.equals( pViewMode ) ) {
      driver = receiptDriver
    } else if ( LcViewMode.QUERY.equals( pViewMode ) ) {
      driver = queryDriver
    } else if ( LcViewMode.ADJUST.equals( pViewMode ) ) {
      //driver = adjustDriver
    } else if ( LcViewMode.RETURN.equals( pViewMode ) ) {
      driver = returnDriver
    } else if ( LcViewMode.FILE_ADJUST.equals( pViewMode ) ) {
      //driver = fileAdjustDriver
    } else if ( LcViewMode.OUTBOUND.equals( pViewMode ) ) {
        //driver = outboundDriver
    } else if ( LcViewMode.INBOUND.equals( pViewMode ) ) {
        //driver = inboundDriver
    } else if ( LcViewMode.SEND_ORDER.equals( pViewMode ) ) {
        driver = sendOrderDriver
    } else  {
      panel.lblStatus.text = panel.TXT_UNDER_CONSTRUCTION_TEXT
      driver = doNothingDriver
    }
  }

  void notifyViewModeChangeCancelled( ) {
    logger.debug( String.format( "[View] View Mode <%s> cancelled. Restore: <%s>", panel.comboViewMode.selection, data.viewMode ) )
    uiDisabled = true
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run( ) {
            panel.comboViewMode.selection = data.viewMode
            uiDisabled = false
          }
        }
    )
  }

  // UI Behavior
  void wireComponents( ) {
    panel.selector.addNavigationListener( this )
    driver = doNothingDriver
  }

  // UI response
  void activate( ) {
    panel.comboViewMode.setSelection( LcViewMode.QUERY )
  }

  void onButtonCancel( ) {
    driver.onButtonCancel( this )
  }

  void onButtonPrint( ) {
    logger.debug( "[View] Print button selected" )
    if ( driver.assign( this ) ) {
      if (driver.doBeforeSave( this )) {
        if ( LcViewMode.QUERY.equals(data.viewMode) ) {
          controller.requestPrint( data.qryInvTr.idTipoTrans, data.qryInvTr.folio )
        } else {
          if( OrderController.dayIsOpen() ){
            controller.requestSaveAndPrint( this )
          } else {
            panel.sb.optionPane(message: 'No se pueden realizar la transaccion. El dia esta cerrado', optionType: JOptionPane.DEFAULT_OPTION)
                    .createDialog(new JTextField(), "Dia cerrado").show()
          }
        }
      }
    } else {
      logger.debug( "[View] Input not valid" )
      this.fireRefreshUI()
    }
  }


  void onPartSeedValueChanged( String pSeed ) {
    logger.debug( "[View] Part seed value changed" )
    boolean valid = false
    if ( driver.isQuantityValid( this ) ) {
      driver.assignQuantity( this )
      valid = true
    }
    driver.flagQuantity( this, !valid )
    if ( driver.isPartSeedValid( this ) ) {
      driver.assignPartSeed( this )
    } else if(driver.isOrder( StringUtils.trimToEmpty(pSeed), this )){
      driver.assignPartSeed( this )
      controller.requestOrder( this )
      driver.refreshUI( this )
      valid = false
    } else {
      driver.displayPartSeedPrompt( this )
      valid = false
    }
    if ( valid ) {
      controller.requestPart( this )
      driver.refreshUI( this )
    }
  }

  void onSkuDoubleClicked( ) {
    driver.onSkuDoubleClicked( this )
  }

  void onSkuOneClicked( ) {
    driver.onSkuOneClicked( this )
  }

  void onSkuOneRightClicked( MouseEvent ev ) {
    driver.onSkuOneRightClicked( this, ev )
  }

  void onViewModeChanged( ) {
    if ( !uiDisabled ) {
      logger.debug( "[View] Request view mode change" )
      SwingUtilities.invokeLater( new Runnable() {
        public void run( ) {
          controller.requestViewModeChange( LcView.this )
        }
      } )
    }
  }

  void requestItem( Command pCommand ) {
    controller.requestItem( this, pCommand )
  }

  void requestNewSearch( ) {
    controller.requestNewSearch( this )
  }

}
