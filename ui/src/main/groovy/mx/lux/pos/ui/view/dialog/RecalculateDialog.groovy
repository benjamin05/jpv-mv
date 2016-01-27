package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class RecalculateDialog extends JDialog implements FocusListener{

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtSku
  private JTextField txtArticulo
  private JTextField txtColor
  private JTextField txtExistIni
  private JTextField txtExistFin
  private JLabel lblWarning
  private Date selectedDateStart
  private Date selectedDateEnd

  public boolean button = false

    RecalculateDialog( ) {
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Recalcula Existencia",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 360, 220 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 4", "20[fill][grow,fill][fill][grow,fill]20", "10[]10[]" ) ) {
          //label( text: "Seleccione las fechas de los días a consultar", constraints: "span 2" )
          //label( text: " ", constraints: "span 2" )
          label( text: "Sku:" )
          txtSku = textField()
          lblWarning = label( " ", constraints: 'span 2' )
          txtSku.addFocusListener(this)
          label( text: "Articulo:" )
          txtArticulo = textField()
          label( text: "Color:" )
          txtColor = textField()
          label( text: "Existencia Actual:" )
          txtExistIni = textField()
          label( text: "Existencia Correcta:" )
          txtExistFin = textField()
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
    if ( selectedDateStart == null || selectedDateEnd == null ) {
      selectedDateStart = DateUtils.truncate( new Date(), Calendar.MONTH )
      selectedDateEnd = DateUtils.truncate( new Date(), Calendar.DATE )
    }
    txtSku.setText( "" )
    txtArticulo.setText( "" )
    txtColor.setText( "" )
    txtExistFin.setText( "" )
    txtExistIni.setText( "" )
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

  // UI Response
  protected void onButtonCancel( ) {
    /*selectedDateStart = null
    selectedDateEnd = null
    button = false
    setVisible( false )*/
    dispose()
  }

  protected void onButtonOk( ) {
    /*selectedDateStart = dv.parse( txtDateStart.getText() )
    selectedDateEnd = dv.parse( txtDateEnd.getText() )
    button = true
    setVisible( false )*/
  }



  public void focusGained(FocusEvent e) {

  }

  public void focusLost(FocusEvent e) {
    lblWarning.setText(" ")
    Integer idItem = null
    try{
      idItem = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtSku.text))
    } catch ( NumberFormatException ex ) { println(ex.message) }
    if( idItem != null ){
        Item item = ItemController.findItem( idItem )
        Integer existCalc = ItemController.generateInventoryFile()
        txtArticulo.setText( StringUtils.trimToEmpty(item.name) )
        txtColor.setText( StringUtils.trimToEmpty(item.color) )
        txtExistIni.setText( StringUtils.trimToEmpty(item.stock.toString()) )

    } else {
      lblWarning.setText("No existe el articulo.")
    }
  }

}
