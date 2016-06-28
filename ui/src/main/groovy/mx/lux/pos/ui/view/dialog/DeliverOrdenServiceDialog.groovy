package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
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

class DeliverOrdenServiceDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtObs
  private String rx

  public boolean button = false

  DeliverOrdenServiceDialog( String rx ) {
    this.rx = rx
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Entregar",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 400, 250 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( border: titledBorder( title: ''), constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "20[][grow,fill]40", "20[]10[]" ) ) {
          label( text: "Rx" )
          textField( text: rx, editable: false )
          label( text: "Observaciones" )
          txtObs = textField( )
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

  Date getSelectedDateStart( ) {
    return selectedDateStart
  }

  Date getSelectedDateEnd( ) {
    return selectedDateEnd
  }

  void setDefaultDates( Date pDateStart, Date pDateEnd ) {

  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    if( StringUtils.trimToEmpty(txtObs.text).length() > 0 ){
      OrderController.deliverOrderService( StringUtils.trimToEmpty(rx), StringUtils.trimToEmpty(txtObs.text))
      dispose()
    } else {
      sb.optionPane(message: 'Verifique los datos',messageType: JOptionPane.ERROR_MESSAGE).
              createDialog(this, 'Error').show()
    }
  }
}
