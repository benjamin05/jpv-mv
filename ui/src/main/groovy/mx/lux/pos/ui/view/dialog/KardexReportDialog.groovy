package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class KardexReportDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtDateStart
  private JTextField txtDateEnd
  private JTextField txtSku
  private Date selectedDateStart
  private Date selectedDateEnd
  private String sku
  private String title
  private String label
  private String label2

  public boolean button = false

  KardexReportDialog( String title ) {
    this.title = title
    label = title.contains("Sku") ? "Sku:" : "Articulo:"
    label2 = title.contains("Sku") ? "Seleccione el Sku y las fechas a consultar" : "Seleccione el Articulo y las fechas a consultar"
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Seleccionar SKU y fechas",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 360, 265 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "20[][grow,fill]40", "20[]10[]" ) ) {
          label( text: label2, constraints: "span 2" )
          label( text: " ", constraints: "span 2" )
          label( text: label )
          txtSku = textField( document: new UpperCaseDocument() )
          label( text: "Fecha Inicio:" )
          txtDateStart = textField()
          label( text: "Fecha Fin:" )
          txtDateEnd = textField()
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Generar", preferredSize: UI_Standards.BUTTON_SIZE,
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
    selectedDateStart = DateUtils.truncate( new Date(), Calendar.MONTH )
    selectedDateEnd = DateUtils.truncate( new Date(), Calendar.DATE )
    txtDateStart.setText( df.format( selectedDateStart ) )
    txtDateEnd.setText( df.format( selectedDateEnd ) )
    txtSku.setText( '' )
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

  String getSku() {
    return sku
  }

  void setDefaultDates( Date pDateStart, Date pDateEnd ) {
    selectedDateStart = DateUtils.truncate( pDateStart, Calendar.DATE )
    selectedDateEnd = DateUtils.truncate( pDateEnd, Calendar.DATE )
  }

  // UI Response
  protected void onButtonCancel( ) {
    selectedDateStart = null
    selectedDateEnd = null
    sku = null
    button = false
    //setVisible( false )
    dispose()
  }

  protected void onButtonOk( ) {
    selectedDateStart = dv.parse( txtDateStart.getText() )
    selectedDateEnd = dv.parse( txtDateEnd.getText() )
    sku = txtSku.getText()
    button = true
    setVisible( false )
  }
}
