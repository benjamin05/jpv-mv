package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Order
import net.miginfocom.swing.MigLayout

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class ArmRxDialog extends JDialog {

    private def sb

    private Component component
    private static ButtonGroup material
    private static ButtonGroup acabado
    private static JTextField forma = new JTextField()
    private static JLabel lforma = new JLabel()
    private static JRadioButton pasta
    private static JRadioButton opacado
    private static JRadioButton metal
    private static JRadioButton pulido
    private static JRadioButton nylon
    private static JRadioButton aire
    private static JRadioButton ninguno
    private static String idNotaV
    private static String armazon
    private static String titulo  = 'Armazon'
    private static Order order

    static Order getOrder() {
        return order
    }

    ArmRxDialog(Component parent, Order order, String armazonString) {
        println( 'Armazoon: '+armazonString)
        sb = new SwingBuilder()
        this.order = order
        component = parent
        idNotaV = order.id
        armazon = armazonString
        if(armazon != null){
            titulo = 'Armazon ' + armazon
        }
        buildUI()
    }


    void buildUI() {
        sb.dialog(this,
                title: titulo,
                resizable: false,
                pack: true,
                modal: true,
                preferredSize: [190, 235],
                location: [ 200, 250 ],
                undecorated: true,
        ) {
                 panel(layout: new MigLayout("wrap 2","[]20[]","[][][][][]20[][]")) {
                     material =   buttonGroup()
                     acabado = buttonGroup()
                     label(text: 'Material'  )
                     label(text: 'Acabado')
                     pasta = radioButton(text:"Pasta", buttonGroup:material)
                     pasta.setSelected(true)
                     pasta.addActionListener(new ActionListener() {
                         public void actionPerformed(ActionEvent e) {
                             forma?.visible = false
                             lforma?.visible = false
                         }
                     })
                     pasta.setActionCommand('Pasta')
                     opacado =  radioButton(text:"Opacado", buttonGroup:acabado)

                     opacado.setActionCommand("Opacado")
                     metal = radioButton(text:"Metal", buttonGroup:material)
                     metal.setActionCommand('Metal')
                     metal.addActionListener(new ActionListener() {
                         public void actionPerformed(ActionEvent e) {
                             forma?.visible = false
                             lforma?.visible = false
                         }
                     })
                     pulido = radioButton(text:"Pulido", buttonGroup:acabado)
                     pulido.setActionCommand('Pulido')
                     nylon = radioButton(text:"Nylon", buttonGroup:material)
                     nylon.setActionCommand('Nylon')
                     nylon.addActionListener(new ActionListener() {
                         public void actionPerformed(ActionEvent e) {
                             forma?.visible = false
                             lforma?.visible = false
                         }
                     })

                     ninguno = radioButton(text:"Ninguno", buttonGroup: acabado )
                     ninguno.setSelected(true)
                     ninguno.setActionCommand('Ninguno')
                     aire = radioButton(text:"Aire", buttonGroup:material)

                     aire.setActionCommand('Aire')
                     aire.addActionListener(new ActionListener() {
                         public void actionPerformed(ActionEvent e) {
                             txtForma(e)
                             forma?.visible = true
                             lforma?.visible = true

                         }
                     })
                     label()
                     lforma = label(text: 'Forma',visible:false)
                     forma = textField(minimumSize: [70, 20],visible:false)
                     button(text: 'Cancelar',actionPerformed: {doCancel()}, enabled: false)
                     button(text: 'Aceptar',actionPerformed: {doSave()})
                 }

        }

    }

    private void txtForma(ActionEvent e) {
        forma.text = armazon
    }

    private void doSave(){
        String opciones =  material.selection.actionCommand
        if(!acabado.selection.actionCommand.equals('Ninguno')){
          opciones = opciones   +', '+acabado.selection.actionCommand
        }
        String form = forma.text
        order = OrderController.saveFrame(idNotaV,opciones,form)
        doCancel()
    }

     private void doCancel(){
         this.setVisible(false)
     }

}

