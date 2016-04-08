package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbSobres
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class EditEnvelopeDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtSobre
  private JTextField txtDest
  private JTextField txtArea
  private JTextField txtContenido

  public boolean button = false
  public JbSobres jbSobre

  EditEnvelopeDialog( JbSobres jbSobre ) {
    this.jbSobre = jbSobre
    buildUI()
    updateData()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Datos Sobre",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 360, 300 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( border: titledBorder(title: ''), constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "20[][grow,fill]40", "20[]10[]" ) ) {
          label( text: "Sobre" )
          txtSobre = textField()
          label( text: "Destinatario" )
          txtDest = textField()
          label( text: "Area" )
          txtArea = textField()
          label( text: "Contenido" )
          txtContenido = textField()
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Guardar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonOk() }
            )
            button( text: "Cerrar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonCancel() }
            )
          }
        }
      }

    }
  }

  private void updateData(){
    if(jbSobre != null){
      txtSobre.text = StringUtils.trimToEmpty(jbSobre.folioSobre)
      txtDest.text = StringUtils.trimToEmpty(jbSobre.dest)
      txtArea.text = StringUtils.trimToEmpty(jbSobre.area)
      txtContenido.text = StringUtils.trimToEmpty(jbSobre.contenido)
    }
  }


  // UI Management
  protected void refreshUI( ) {

  }

  // Public Methods

  void setDefaultDates( Date pDateStart, Date pDateEnd ) {

  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    OrderController.saveEnvelope(txtSobre.text, txtDest.text, txtArea.text, txtContenido.text)
    dispose()
  }
}
