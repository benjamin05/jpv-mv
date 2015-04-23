package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.AccessController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class ChangeIpBoxDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtEmployeeNum
  private JLabel lblMsg1
  private JLabel lblMsg2

  public boolean button = false

    ChangeIpBoxDialog( ) {
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Cambia Ip de Caja",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 450, 220 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "20[][grow,fill]40", "20[]10[]" ) ) {
          label( text: " ", constraints: "span 2" )
          label( text: "Nueva Ip" )
          txtEmployeeNum = textField()
          lblMsg1 = label( text: "Error al guardar ip", visible: false, constraints: 'span' )
          lblMsg2 = label( text: " ", visible: false, constraints: 'span' )
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Aceptar", preferredSize: UI_Standards.BUTTON_SIZE,
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

  // UI Management
  protected void refreshUI( ) {

  }

  // Public Methods
  void activate( ) {
    refreshUI()
    setVisible( true )
  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    Boolean valid = OrderController.changeIpBox( StringUtils.trimToEmpty(txtEmployeeNum.text) )
    if( valid ){
      dispose()
    } else {
      lblMsg1.visible = false
    }
    //dispose()
  }
}
