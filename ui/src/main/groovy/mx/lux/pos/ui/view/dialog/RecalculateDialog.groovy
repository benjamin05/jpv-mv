package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.User
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
  private Integer idArticulo
  private Integer stock
  private Integer oldStock

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
        preferredSize: [ 480, 280 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 4", "20[fill][grow,fill][fill][grow,fill]20", "10[]10[]" ) ) {
          //label( text: "Seleccione las fechas de los días a consultar", constraints: "span 2" )
          //label( text: " ", constraints: "span 2" )
          label( text: "Sku:" )
          txtSku = textField()
          label( text: " ", constraints: "span 2" )
          //button( text: "Limpiar", actionPerformed: { refreshUI() }, constraints: 'span 2' )
          label( "" )
          lblWarning = label( "                       ", constraints: 'span 2', foreground: UI_Standards.WARNING_FOREGROUND )
          label( " ", constraints: 'span 2' )
          txtSku.addFocusListener(this)
          label( text: "Articulo:" )
          txtArticulo = textField( editable: false )
          label( text: "Color:" )
          txtColor = textField( editable: false )
          label( text: "Existencia Actual:" )
          txtExistIni = textField( editable: false )
          label( text: "Existencia Correcta:" )
          txtExistFin = textField( editable: false )
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Aplicar", preferredSize: UI_Standards.BUTTON_SIZE,
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
    txtSku.setText( "" )
    txtArticulo.setText( "" )
    txtColor.setText( "" )
    txtExistFin.setText( "" )
    txtExistIni.setText( "" )
    lblWarning.setText(" ")
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
    dispose()
  }

  protected void onButtonOk( ) {
    if( idArticulo != null && stock != null && oldStock != stock){
      User u = Session.get(SessionItem.USER) as User
      if( ItemController.updateStock( idArticulo, stock, u ) ){
        sb.optionPane(message: 'Se actualizo correctamente la existencia.',
                messageType: JOptionPane.DEFAULT_OPTION).
                createDialog(this, 'Actualizado').show()
        dispose()
      } else {
        sb.optionPane(message: "Error al actualizar la existencia.", optionType: JOptionPane.DEFAULT_OPTION)
                .createDialog(new JTextField(), "Error")
                .show()
      }
    } else {
      lblWarning.setText( "Verifique los datos" )
    }
  }



  public void focusGained(FocusEvent e) {

  }

  public void focusLost(FocusEvent e) {
    lblWarning.setText(" ")
    if( StringUtils.trimToEmpty(txtSku.text).length() > 0 ){
      Integer idItem = null
      try{
        idItem = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtSku.text))
      } catch ( NumberFormatException ex ) { println(ex.message) }
      if( idItem != null ){
        Item item = ItemController.findItem( idItem.intValue() )
        stock = ItemController.calculateStock( idItem.intValue() )
        oldStock = item.stock
        if( item != null ){
          idArticulo = item.id
          txtArticulo.setText( StringUtils.trimToEmpty(item.name) )
          txtColor.setText( StringUtils.trimToEmpty(item.color) )
          txtExistIni.setText( StringUtils.trimToEmpty(item.stock.toString()) )
          txtExistFin.setText( StringUtils.trimToEmpty(stock.toString()) )
        } else {
          lblWarning.setText("No existe el articulo.")
        }
      } else {
        lblWarning.setText("No existe el articulo.")
      }
    }
  }

}
