package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class ReprintTripDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtDate
  private JTextField txtTrip
  private JLabel lblWarning

  private SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy")
  private SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy")
  private SimpleDateFormat df3 = new SimpleDateFormat("ddMMyyyy")

  public boolean button = false

    ReprintTripDialog( ) {
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Reimprimir Packing:",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 360, 220 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( border: titledBorder(""), constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "[fill][grow,fill]", "20[]10[]" ) ) {
          label( text: "Viaje" )
          txtTrip = textField( text: "1" )
          label( text: "Fecha" )
          txtDate = textField( text: df1.format(new Date()))
          lblWarning = label( foreground: UI_Standards.WARNING_FOREGROUND, /*constraints: 'hidemode 3'*/ visible: false )
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Imprimir", preferredSize: UI_Standards.BUTTON_SIZE,
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

  void setDefaultDates( Date pDateStart, Date pDateEnd ) {

  }

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    if( StringUtils.trimToEmpty(txtTrip.text).length() > 0 && StringUtils.trimToEmpty(txtDate.text).length() > 0 ){
      Boolean validDate = true
      Date datePacking = null
      try{
        datePacking = df1.parse(StringUtils.trimToEmpty(txtDate.text))
      } catch ( ParseException ex ){
        println( ex )
        validDate = false
      }
      if( !validDate ){
        try{
          datePacking = df2.parse(StringUtils.trimToEmpty(txtDate.text))
        } catch ( ParseException ex ){
          println( ex )
          validDate = false
        }
      }
      if( ! validDate ){
        try{
          datePacking = df3.parse(StringUtils.trimToEmpty(txtDate.text))
        } catch ( ParseException ex ){
          println( ex )
          validDate = false
        }
      }
      if( validDate ){
        OrderController.reprintPacking(StringUtils.trimToEmpty(txtTrip.text), datePacking)
        dispose()
      } else {
        lblWarning.text = "Formato de fecha incorrecto"
        lblWarning.visible = true
      }
    } else {
      lblWarning.text = "Verifique los datos"
      lblWarning.visible = true
    }
  }
}
