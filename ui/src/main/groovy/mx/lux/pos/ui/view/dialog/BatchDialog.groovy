package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.verifier.DateVerifier
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.Item

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class BatchDialog extends JDialog {

  private DateFormat df = new SimpleDateFormat( "dd/MM/yyyy" )
  private DateVerifier dv = DateVerifier.instance
  private def sb = new SwingBuilder()

  private JTextField txtBatch
  private JLabel lblWarrning
  private Order order
  private Item item
  private Boolean fillObligat
  private String title

  public boolean button = false

    BatchDialog( Order order, Item item, Boolean fillOblig ) {
    this.title = item.name
    this.fillObligat = fillOblig
    this.order = order
    this.item = item
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: title,
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 360, 150 ],
        location: [ 200, 250 ],
        undecorated: true,
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "20[][grow,fill]40", "20[]10[]" ) ) {
          label( text: "Lote:" )
          txtBatch = textField()
          lblWarrning = label( text: 'Lote invalido', foreground: UI_Standards.WARNING_FOREGROUND, visible: false, constraints: 'hidemode 3,span' )
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Agregar", preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onButtonOk() }
            )
            button( text: "Cancelar", preferredSize: UI_Standards.BUTTON_SIZE,
                enabled: !fillObligat,
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
    //button = false
    //setVisible( false )
    dispose()
  }

  protected void onButtonOk( ) {
    if(validData()){
      OrderController.saveBatch( order.id, item.id, StringUtils.trimToEmpty(txtBatch.text) )
      dispose()
    }
  }



  protected Boolean validData(){
    Boolean valid = false
    String batch = StringUtils.trimToEmpty(txtBatch.text)
    if( batch.length() >= 10 && OrderController.validalote(order.id, item.id, StringUtils.trimToEmpty(txtBatch.text)) ){
      valid = true
    } else {
      lblWarrning.visible = true
    }
    return valid
  }


}
