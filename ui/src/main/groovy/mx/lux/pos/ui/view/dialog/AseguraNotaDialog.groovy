package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.repository.NotaVentaJava
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.Branch
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils

import javax.swing.*
import java.awt.*

class AseguraNotaDialog extends JDialog {

    private def sb

    private Component component
    private static Branch branch
    private static JTextField factura = new JTextField()
    private static JTextField sucursal = new JTextField()
    static NotaVentaJava notaVenta = null

    AseguraNotaDialog( ) {
      sb = new SwingBuilder()
      branch = Session.get(SessionItem.BRANCH) as Branch
      buildUI()
    }


    void buildUI() {
        sb.dialog(this,
              title: 'Numero de nota que se desea asegurar',
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


    NotaVentaJava getNotaVenta (){
      return notaVenta
    }


    private void doSave(){
     Boolean registro =  OrderController.validaAplicaGarantia(factura.text)
     if(!registro){
       sb.optionPane(
          message: 'Ticket no valido',
          messageType: JOptionPane.ERROR_MESSAGE
       ).createDialog(this, 'No se puede registrar el seguro')
         .show()
     } else {
       //Order order = OrderController.findOrderByTicket(StringUtils.trimToEmpty(sucursal.text)+"-"+StringUtils.trimToEmpty(factura.text))
       Order order = OrderController.findOrderByTicketJava(StringUtils.trimToEmpty(factura.text))
       notaVenta = OrderController.findOrderJavaByidOrder( StringUtils.trimToEmpty(order.id) )
     }
     doCancel()
    }

     private void doCancel(){
       //  this.setVisible(false)
       dispose()
     }

}