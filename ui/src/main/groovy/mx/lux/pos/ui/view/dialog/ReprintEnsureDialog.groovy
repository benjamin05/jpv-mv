package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Branch
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.Warranty
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils

import javax.swing.*
import java.awt.*

class ReprintEnsureDialog extends JDialog {

    private def sb

    private Component component
    private static Branch branch
    private static JTextField factura = new JTextField()
    private static JTextField sucursal = new JTextField()
    static NotaVenta notaVenta = null

    ReprintEnsureDialog( ) {
      sb = new SwingBuilder()
      branch = Session.get(SessionItem.BRANCH) as Branch
      buildUI()
    }


    void buildUI() {
        sb.dialog(this,
              title: 'Reimpresion de seguro',
              resizable: false,
              pack: true,
              modal: true,
              preferredSize: [240, 100],
              location: [ 200, 250 ]
        ) {
            panel(layout: new MigLayout("wrap 4","[]10[][][]","[]20[]")) {
                label(text: 'Ticket: ')
                sucursal = textField(text:branch?.id.toString(),minimumSize: [70, 20], enabled: false)
                label(text: '-')
                factura = textField(minimumSize: [70, 20])
                label()
                button(text: 'Cancelar',actionPerformed: {doCancel()})
                label()
                button(text: 'Aceptar',actionPerformed: {doSave()})
            }
        }
    }


    NotaVenta getNotaVenta (){
      return notaVenta
    }


    private void doSave(){
      Order order = OrderController.findOrderByTicket(StringUtils.trimToEmpty(sucursal.text)+"-"+StringUtils.trimToEmpty(factura.text))
      NotaVenta notaWarranty = new NotaVenta()
      Boolean registro = OrderController.validWarranty( OrderController.findOrderByidOrder(StringUtils.trimToEmpty(order?.id)), true, null, notaWarranty.id, false )
      if(!registro){
       sb.optionPane(
          message: 'Ticket no valido',
          messageType: JOptionPane.ERROR_MESSAGE
       ).createDialog(this, 'No se puede registrar el seguro')
         .show()
      } else {
        OrderController.reprintEnsure( OrderController.findOrderJavaByidOrder(StringUtils.trimToEmpty(order.id)) )
       doCancel()
      }
    }

     private void doCancel(){
       dispose()
     }

}