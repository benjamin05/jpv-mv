package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbSobres
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.OperationType
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.awt.event.MouseEvent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.List

class EnvelopesDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private DefaultTableModel envelopesModel
  private List<JbSobres> lstEnvelopes

  public boolean button = false

  EnvelopesDialog( ) {
    lstEnvelopes = OrderController.findPendingEnvelopes()
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Sobres",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 500, 400 ],
        location: [ 200, 200 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap ", "[grow,fill]", "[]" ) ) {
            scrollPane(border: titledBorder(title: 'Art\u00edculos'), constraints: 'span', mouseClicked: doShowItemClick) {
                table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClick) {
                    envelopesModel = tableModel(list: lstEnvelopes) {
                        closureColumn(
                                header: 'Sobre',read: { JbSobres tmp -> "${StringUtils.trimToEmpty(tmp.folioSobre)}" },
                        )
                        closureColumn(
                                header: 'Destinatario',read: { JbSobres tmp -> StringUtils.trimToEmpty(tmp?.dest) }
                        )
                        closureColumn(
                                header: 'Area',read: {JbSobres tmp -> StringUtils.trimToEmpty(tmp?.area)},
                        )
                        closureColumn(
                                header: 'Contenido',read: { JbSobres tmp -> StringUtils.trimToEmpty(tmp?.contenido) },
                        )
                    } as DefaultTableModel
                }
            }
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Cerrar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonCancel() }
            )
          }
        }
      }

    }
  }

  // UI Management
  public void refreshUI( ) {
    lstEnvelopes = OrderController.findPendingEnvelopes()
    envelopesModel.rowsModel.value = lstEnvelopes
    envelopesModel.fireTableDataChanged()
  }

  // Public Methods
  void activate( ) {

  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }


  private def doShowItemClick = { MouseEvent ev ->
    JbSobres jbSobres = null
    if( ev?.source instanceof JTable ){
      jbSobres = ev?.source?.selectedElement as JbSobres
    }
    if (SwingUtilities.isLeftMouseButton(ev)) {
      if (ev.clickCount == 2 && jbSobres != null) {
        EditEnvelopeDialog dialog = new EditEnvelopeDialog( jbSobres )
        dialog.show()
        refreshUI()
      }
    } else if (SwingUtilities.isRightMouseButton(ev)){
      PopUpEnvelopeMenu menu = new PopUpEnvelopeMenu( this, ev, jbSobres )
    }
  }


}
