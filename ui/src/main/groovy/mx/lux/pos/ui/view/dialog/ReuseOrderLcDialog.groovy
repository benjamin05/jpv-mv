package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.CancellationController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.model.Order
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import java.awt.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class ReuseOrderLcDialog extends JDialog {

  private def sb = new SwingBuilder()

  private JTextField txtorderLc
  private JLabel lblWarning
  private String idOrder
  public boolean button = false

  ReuseOrderLcDialog( String idOrder ) {
    Order order = OrderController.findOrderByTicket(StringUtils.trimToEmpty(Registry.currentSite.toString())+"-"+idOrder)
    this.idOrder = order.id
    buildUI()
  }

  // UI Layout Definition
  void buildUI( ) {
    sb.dialog( this,
        title: "Reuso de pedido",
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 360, 220 ],
        location: [ 200, 250 ],
    ) {
      panel() {
        borderLayout()
        panel( constraints: BorderLayout.CENTER, layout: new MigLayout( "wrap 2", "20[][grow,fill]40", "20[]10[]" ) ) {
          label( text: "Seleccione el pedido a reusar", constraints: "span 2" )
          label( text: " ", constraints: "span 2" )
          label( text: "Id Pedido:" )
          txtorderLc = textField()
          lblWarning = label( text: "Pedido invalido para reuso.", foreground: UI_Standards.WARNING_FOREGROUND,
                  constraints: "span 2", visible: false )
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( text: "Reusar", preferredSize: UI_Standards.BUTTON_SIZE,
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

  // UI Response
  protected void onButtonCancel( ) {
    dispose()
  }

  protected void onButtonOk( ) {
    Boolean valid = OrderController.isReuseOrderLc( StringUtils.trimToEmpty(txtorderLc.text))
    if( valid ){
      if( CancellationController.sendTransferOrderLc( txtorderLc.text, StringUtils.trimToEmpty(idOrder) ) ){
        dispose()
      } else {
        lblWarning.visible = true
      }
    } else {
      lblWarning.visible = true
    }
  }
}
