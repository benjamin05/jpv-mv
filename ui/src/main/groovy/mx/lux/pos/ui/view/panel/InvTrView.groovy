package mx.lux.pos.ui.view.panel

import mx.lux.pos.model.CausaDev
import mx.lux.pos.model.InvAdjustSheet
import mx.lux.pos.model.Shipment
import mx.lux.pos.model.TransInvDetalle
import mx.lux.pos.ui.controller.InvTrController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.model.adapter.InvTrAdapter
import mx.lux.pos.ui.view.component.NavigationBar.Command
import mx.lux.pos.ui.view.component.NavigationBarListener
import mx.lux.pos.ui.view.driver.*
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.event.MouseListener

// Separates ui mask and ui behavior
//   ui mask defined in InvTrPanel
//   ui behavior defined in InvTrDriver and its subclasses
class InvTrView implements NavigationBarListener {

  private static final Logger logger = LoggerFactory.getLogger( InvTrView.class )

  InvTrController controller
  InvTr data
  InvTrAdapter adapter = new InvTrAdapter()
  InvTrPanel panel
  InvTrDriver driver
  InvTrDriver queryDriver = new InvTrQueryDriver()
  InvTrDriver receiptDriver = new InvTrReceiptDriver()
  InvTrDriver massiveReceiptDriver = new InvTrMassiveReceiptDriver()
  InvTrDriver issueDriver = new InvTrIssueDriver()
  InvTrDriver otherIssueDriver = new InvTrOtherIssueDriver()
  InvTrDriver otherReceiptDriver = new InvTrOtherReceiptDriver()
  InvTrDriver adjustDriver = new InvTrAdjustDriver()
  InvTrDriver fileAdjustDriver = new InvTrFileAdjustDriver()
  InvTrDriver returnDriver = new InvTrReturnDriver()
  InvTrDriver doNothingDriver = new InvTrDriver()
  InvTrDriver outboundDriver = new InvTrOutBoundDriver()
  InvTrDriver inboundDriver = new InvTrInBoundDriver()

  Boolean uiDisabled = false

  MouseListener skuListener = null

  InvTrView( ) {
    controller = InvTrController.instance
    data = new InvTr()
    panel = new InvTrPanel( this )
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

    InvTrView view = this
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

      InvTrView view = this
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
          panel.comboViewMode.setSelection( InvTrViewMode.QUERY )
        }
      })
    }
  }

  void notifyViewMode( InvTrViewMode pViewMode ) {
        logger.debug( String.format( "[View] Applying View Mode <%s>", pViewMode.text ) )
    data.viewMode = pViewMode
    if ( !data.viewMode.equals( panel.comboViewMode.getSelection() ) ) {
      panel.comboViewMode.setSelection( data.viewMode )
    }
    if ( InvTrViewMode.ISSUE.equals( pViewMode ) ) {
      driver = issueDriver
    } else if ( InvTrViewMode.RECEIPT.equals( pViewMode ) ) {
      driver = receiptDriver
    } else if ( InvTrViewMode.QUERY.equals( pViewMode ) ) {
      driver = queryDriver
    } else if ( InvTrViewMode.ADJUST.equals( pViewMode ) ) {
      driver = adjustDriver
    } else if ( InvTrViewMode.RETURN.equals( pViewMode ) ) {
      driver = returnDriver
    } else if ( InvTrViewMode.FILE_ADJUST.equals( pViewMode ) ) {
      driver = fileAdjustDriver
    } else if ( InvTrViewMode.OUTBOUND.equals( pViewMode ) ) {
      driver = outboundDriver
    } else if ( InvTrViewMode.INBOUND.equals( pViewMode ) ) {
      driver = inboundDriver
    } else  if ( InvTrViewMode.OTHER_ISSUE.equals( pViewMode ) ) {
      driver = otherIssueDriver
    } else if ( InvTrViewMode.OTHER_RECEIPT.equals( pViewMode ) ) {
      driver = otherReceiptDriver
    } else if ( InvTrViewMode.ISSUE_FRAMES.equals( pViewMode ) ) {
        driver = issueDriver
    } else if ( InvTrViewMode.ISSUE_ACCESORIES.equals( pViewMode ) ) {
        driver = issueDriver
    }  else if ( InvTrViewMode.MASSIVE_RECEIPT.equals( pViewMode ) ) {
      driver = massiveReceiptDriver
    }  else {
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
    panel.comboViewMode.setSelection( InvTrViewMode.QUERY )
  }

  void onButtonCancel( ) {
    driver.onButtonCancel( this )
  }

  void onButtonPrint( ) {
    panel.btnPrint.setEnabled(false)
    println panel.btnPrint.enabled
    logger.debug( "[View] Print button selected" )
    if ( driver.assign( this ) ) {
      if (driver.doBeforeSave( this )) {
        if ( InvTrViewMode.QUERY.equals(data.viewMode) ) {
          if( InvTrViewMode.ISSUE.trType.idTipoTrans.equals(StringUtils.trimToEmpty(data.qryInvTr.idTipoTrans)) ){
            Boolean approved = false
            for(TransInvDetalle det : data.qryInvTr.trDet){
              if( det.cantidad > 0 || det.cantidad < 0 ){
                approved = true
              }
            }
            if( approved ){
              controller.requestPrint( data.qryInvTr.idTipoTrans, data.qryInvTr.folio )
            } else {
              JOptionPane.showMessageDialog( this.panel, "Esperar autorizacion, para realizar devolucion",
                    "Autorizacion", JOptionPane.INFORMATION_MESSAGE )
            }
          } else {
            controller.requestPrint( data.qryInvTr.idTipoTrans, data.qryInvTr.folio )
          }
        } else {
          Boolean onlyFrames = true
          for(InvTrSku part : data.skuList){
            if(!StringUtils.trimToEmpty(part.part.idGenerico).equalsIgnoreCase("A")){
              onlyFrames = false
              break
            }
          }
          if( OrderController.dayIsOpen() ){
            if ( InvTrViewMode.ISSUE.equals(data.viewMode) && onlyFrames ) {
              String causaDev = this.panel.cbReasonsIssue.selectedItem as String
              this.data.postRemarks = this.panel.txtRemarks.text+","+causaDev
              Integer folio = controller.generatedIssueFile( this )
              if( folio != null && folio > 0 ){
                controller.requestPrint( InvTrViewMode.ISSUE.trType.idTipoTrans, folio )
                JOptionPane.showMessageDialog( this.panel, "Autorizacion de Devolucion Enviada",
                        "Autorizacion", JOptionPane.INFORMATION_MESSAGE )
                controller.showQueryTransaction( this )
              } else {
                JOptionPane.showMessageDialog( this.panel, "Error al enviar Autorizacion de Devolucion",
                      "Error", JOptionPane.ERROR_MESSAGE )
              }
            } else {
              controller.requestSaveAndPrint( this )
            }
          } else {
            panel.sb.optionPane(message: 'No se puede realizar la transaccion. El dia esta cerrado', optionType: JOptionPane.DEFAULT_OPTION)
                .createDialog(new JTextField(), "Dia cerrado").show()
          }
        }
      }
    }else {
      logger.debug( "[View] Input not valid" )
      this.fireRefreshUI()
    }
    panel.btnPrint.setEnabled(true)
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

  void onViewModeChanged( ) {
    if ( !uiDisabled ) {
      logger.debug( "[View] Request view mode change" )
      SwingUtilities.invokeLater( new Runnable() {
        public void run( ) {
          controller.requestViewModeChange( InvTrView.this )
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

  List<CausaDev> findCausas(){
    return controller.findCausasDev( )
  }


}
