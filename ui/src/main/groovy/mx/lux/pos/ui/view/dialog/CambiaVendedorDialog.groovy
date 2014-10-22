package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import net.miginfocom.swing.MigLayout

import javax.swing.*
import java.awt.*

class CambiaVendedorDialog extends JDialog {

    private def sb
    private Component component

    private static JTextField idVendedor = new JTextField()
    private static String vendedor

    static String getVendedor() {
        return vendedor
    }

    CambiaVendedorDialog(Component parent, String idVendedor) {
      sb = new SwingBuilder()
      component = parent
      vendedor = idVendedor
      buildUI()
    }

    void buildUI() {
        sb.dialog(this,
                title: 'Cambio de Vendedor',
                resizable: false,
                pack: true,
                modal: true,
                preferredSize: [170, 80],
                location: [ 200, 250 ]
        ) {
                 panel(layout: new MigLayout("wrap 2","[][]","[][]")) {

                     label(text: 'Vendedor')
                     idVendedor = textField(text:vendedor,minimumSize: [70, 20])

                     button(text: 'Cancelar',actionPerformed: {doCancel()})
                     button(text: 'Aceptar',actionPerformed: {doSave()})
                 }

        }

    }

    private void doSave(){
        vendedor = idVendedor?.text
         doCancel()
    }

     private void doCancel(){
         this.setVisible(false)
     }

}