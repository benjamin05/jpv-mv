package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Customer
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class NotSendDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtRx
  private JTextField txtCustomer
  private JTextField txtReason
  private JLabel lblWarning

  private String rx
  private Component component

  public boolean button = false

  NotSendDialog( Component component, String rx ) {
    this.rx = StringUtils.trimToEmpty(rx)
    this.component = component
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Rx: ${rx}",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 450, 155 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "[][grow,fill]", "20[]10[]" ) ) {
          label( text: "Razon" )
          txtReason = textField( )
          lblWarning = label( text: "Debe agregar la razon.", foreground: UI_Standards.WARNING_FOREGROUND, visible: false, constraints: 'span 2' )
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
    if( StringUtils.trimToEmpty(txtReason.text).length() > 0 ){
      OrderController.notSend( rx, StringUtils.trimToEmpty(txtReason.text) )
      dispose()
    } else {
      lblWarning.visible = true
    }
  }
}
