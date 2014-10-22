package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.model.User
import net.miginfocom.swing.MigLayout

import javax.swing.*

class CapturaSuyoDialog extends JDialog {

    private def sb

    private static String titulo  = 'Captura de Armazon de Cliente'
    private static ArrayList<String> servicios  = []
    private static  Order order
    private static JComboBox servicio
    private static  JTextArea dejo
    private static  JTextArea instrucciones
    private static  JTextArea condiciones
    private static User user
    private static Boolean cancel

    CapturaSuyoDialog(Order order, User u, Boolean cancel) {
        sb = new SwingBuilder()
        this.order = order
        user = u
        servicios = OrderController.findAllServices()
        this.cancel = cancel
        buildUI()
    }


    void buildUI() {
        sb.dialog(this,
                title: titulo,
                resizable: false,
                defaultCloseOperation: 0,
                pack: true,
                modal: true,
                preferredSize: [510, 360],
                layout: new MigLayout('wrap,center', '[fill,grow]'),
                location: [ 200, 200 ]
        ) {
                 panel(layout: new MigLayout("wrap 3","[right][fill,grow][fill,grow]","[][][][]")) {
                      label(text: 'Dejó: ')

                     scrollPane( border: titledBorder( constraints: "span 2" ), minimumSize:[250,70]) {
                       dejo = textArea(document: new UpperCaseDocument(), lineWrap: true, minimumSize: [250, 70] )
                     }

                     label(text: 'Servicio: ')
                     servicio = comboBox( items: servicios, constraints: "span 2" )
                     label(text: 'Instruccion: ')
                     scrollPane( border: titledBorder( constraints: "span 2" ), minimumSize:[250,70] ) {
                        instrucciones = textArea(document: new UpperCaseDocument(), lineWrap: true, minimumSize: [250, 70] )
                     }
                     label(text: 'Condiciones Generales: ')
                     scrollPane( border: titledBorder( constraints: "span 2" ) , minimumSize:[250,70]) {
                       condiciones =  textArea(document: new UpperCaseDocument(), lineWrap: true, minimumSize: [250, 70] )
                     }
                 }

            panel(layout: new MigLayout('wrap 3', '[fill,grow][fill,grow][fill,grow]')) {
                button(text: 'Cancelar',visible: cancel,actionPerformed: {doCancel()})
                label()
                button(text: 'Aceptar',actionPerformed: {doSave()})

            }
        }
    }


     private void doCancel(){
         this.setVisible(false)
     }

    private void doSave(){
      OrderController.saveSuyo(order,user, dejo?.text, instrucciones?.text,condiciones?.text,servicio?.selectedItem?.toString() )
     doCancel()
    }


}



