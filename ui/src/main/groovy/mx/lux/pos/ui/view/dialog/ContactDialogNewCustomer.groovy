package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.model.FormaContacto
import mx.lux.pos.model.TipoContacto
import mx.lux.pos.ui.controller.CustomerController
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils

import javax.swing.*
import java.awt.event.ItemEvent

class ContactDialogNewCustomer extends JDialog {

    private def sb

    private static JComboBox tipo
    private static List<String> tipos = []
    private static JComboBox dominio
    private static JLabel arroba
    private static List<String> dominios = []
    private static JTextField infoTipo
    private static JTextField correo
    private static JTextArea txtObservaciones
    private static Boolean edit
    private static FormaContacto formaContacto
    private static Boolean cHide = false
    private static String correoText = ''
    private static String infoTipoText = ''
    private static Boolean borrar
    private static String titulo

    FormaContacto getFormaContacto() {
        return formaContacto
    }

     Boolean getBorrar() {
        return borrar
    }

    ContactDialogNewCustomer(FormaContacto formaContacto, Boolean edit,List<String> tipos) {

        sb = new SwingBuilder()
        this.edit = edit
        this.borrar = false
        this.formaContacto = formaContacto
        this.tipos = tipos
        if (this.edit == true) {
             titulo = 'Editar Forma de Contacto'
            if (formaContacto?.tipoContacto?.descripcion.trim().equals('Correo')) {
                cHide = true
                this.tipos = [formaContacto?.tipoContacto?.descripcion.trim()]
                dominios = CustomerController.findAllCustomersDomains()
                correoText = formaContacto?.contacto.trim().substring(0, formaContacto?.contacto.trim().indexOf('@'))
            } else if (formaContacto?.tipoContacto?.descripcion.trim().equals('Recados')) {
                this.tipos = [formaContacto?.tipoContacto?.descripcion.trim()]
                infoTipoText = formaContacto?.contacto.trim()
            } else if (formaContacto?.tipoContacto?.descripcion.trim().equals('Telefono')) {
                this.tipos = [formaContacto?.tipoContacto?.descripcion.trim()]
                infoTipoText = formaContacto?.contacto.trim()
            } else if (formaContacto?.tipoContacto?.descripcion.trim().equals('SMS')) {
                this.tipos = [formaContacto?.tipoContacto?.descripcion.trim()]
                infoTipoText = formaContacto?.contacto.trim()
            }
        } else {
            this.titulo = 'Agregar Forma de Contacto'
            correoText = ''
            infoTipoText = ''
            this.tipos = tipos
            if(tipos.getAt(0).equals('Correo')){
            cHide = true
            }
            dominios = CustomerController.findAllCustomersDomains()
        }
        buildUI()
    }


    void buildUI() {
        sb.dialog(this,
                title: titulo,
                resizable: false,
                pack: true,
                modal: true,
                preferredSize: [320, 180],
                layout: new MigLayout('wrap,center', '[fill,grow]'),
                location: [200, 250]
        ) {
            panel(layout: new MigLayout("wrap 3", "[][][]", "[][]")) {
                tipo = comboBox(items: tipos, itemStateChanged: typeChanged)

                label()
                infoTipo = textField(minimumSize: [140, 20], visible: !cHide)
                infoTipo.text = infoTipoText
                correo = textField(minimumSize: [140, 20], visible: cHide)
                correo.text = correoText
                arroba = label(text: '@', visible: cHide)
                dominio = comboBox(items: dominios, visible: cHide,editable:true )
            }

            panel(layout: new MigLayout('wrap 3', '[fill,grow][fill,grow][fill,grow]')) {

                button(text: 'Borrar', visible: edit, actionPerformed: { doErase() })
                button(text: 'Cancelar', actionPerformed: { doCancel() })
                button(text: 'Aceptar', actionPerformed: { doSave() })

            }

        }

    }

    private void doErase(){
        borrar = true
        doCancel()
    }


    private void doSave() {
        String valor
        Boolean validData = true
        TipoContacto  tipoContacto = new TipoContacto()
        if (edit == true) {
            if (formaContacto?.tipoContacto?.descripcion.trim().equals('Correo')) {
                valor = correo?.text + '@' + dominio?.selectedItem?.toString()
            } else {
                valor = infoTipo?.text
            }
        } else {
            if (tipo?.selectedItem?.toString().equals('Correo')) {
                valor = correo?.text + '@' + dominio?.selectedItem?.toString()
                tipoContacto?.id_tipo_contacto = 1
                tipoContacto?.descripcion = 'Correo'
            } else if (tipo?.selectedItem?.toString().equals('Telefono')) {
                if(StringUtils.trimToEmpty(infoTipo?.text).length() == 10){
                  valor = infoTipo?.text
                  tipoContacto?.id_tipo_contacto = 3
                  tipoContacto?.descripcion = 'Telefono'
                } else {
                  validData = false
                    sb.optionPane(message: 'El telefono debe tener 10 digitos')
                            .createDialog(infoTipo, 'Telefono incorrecto')
                      .show()
                }
            } else if (tipo?.selectedItem?.toString().equals('Recados')) {
                valor = infoTipo?.text
                tipoContacto?.id_tipo_contacto = 2
                tipoContacto?.descripcion = 'Recados'
            } else if (tipo?.selectedItem?.toString().equals('SMS')) {
                if(StringUtils.trimToEmpty(infoTipo?.text).length() == 10){
                  valor = infoTipo?.text
                  tipoContacto?.id_tipo_contacto = 4
                  tipoContacto?.descripcion = 'SMS'
                } else {
                    validData = false
                    sb.optionPane(message: 'El telefono debe tener 10 digitos')
                            .createDialog(infoTipo, 'Telefono incorrecto')
                        .show()
                }
            }
            formaContacto?.tipoContacto = tipoContacto
        }
        if(validData){
          formaContacto?.contacto = valor
          doCancel()
        }
    }

    private void doCancel() {
        cHide = false
        this.setVisible(false)
    }

    private def typeChanged = { ItemEvent ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            String typeName = ev.item
            println(typeName.trim())
            if (typeName.trim() != 'Correo') {
                infoTipo.setVisible(true)
                correo.setVisible(false)
                arroba.setVisible(false)
                dominio.setVisible(false)
            } else {
                infoTipo.setVisible(false)
                correo.setVisible(true)
                arroba.setVisible(true)
                dominio.setVisible(true)
            }
        } else {

        }
    }

    void activate() {
      this.setVisible(true)
    }

}

