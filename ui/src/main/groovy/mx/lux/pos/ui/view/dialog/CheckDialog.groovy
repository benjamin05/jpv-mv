package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.ChecadasJava
import mx.lux.pos.ui.controller.AccessController
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class CheckDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JPasswordField txtcardCode
  private JLabel lblDate
  private JLabel lblEmployee

  public boolean button = false

  CheckDialog( ) {
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Checador",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 500, 200 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 3", "[][grow,fill][fill]", "20[]10[]" ) ) {
          label( text: "Codigo" )
          txtcardCode = passwordField( )
          lblDate = label( text: StringUtils.trimToEmpty(new Date().format("dd/MM/yyyy HH:mm:ss")) )
          label( " " )
          lblEmployee = label( text: "", constraints: 'span 2' )
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Checar", preferredSize: UI_Standards.BUTTON_SIZE,
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

  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    if( StringUtils.trimToEmpty(txtcardCode.text).length() > 0 ){
      ChecadasJava check = AccessController.checkEmployee( StringUtils.trimToEmpty(txtcardCode.text) )
      if( check != null ){
        lblDate.text = "${StringUtils.trimToEmpty(check.fecha.format("dd/MM/yyyy"))} ${StringUtils.trimToEmpty(check.hora.format("HH:mm:ss"))}"
        lblEmployee.text = "${StringUtils.trimToEmpty(check.fecha.format("dd/MM/yyyy"))} ${StringUtils.trimToEmpty(check.hora.format("HH:mm:ss"))} ${AccessController.findRegionalByIdEmp(check.idEmpleado).nombre}"
        txtcardCode.text = ""
      } else {
        lblEmployee.text = "Credencial incorrecta"
        txtcardCode.text = ""
      }
    } else {
      lblDate.text = StringUtils.trimToEmpty(new Date().format("dd/MM/yyyy HH:mm:ss"))
      txtcardCode.text = ""
      lblEmployee.text = ""
    }
  }
}
