package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.model.FormaContacto
import mx.lux.pos.model.IPromotionAvailable
import mx.lux.pos.ui.controller.ContactController
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.dialog.ContactDialogNewCustomer
import mx.lux.pos.ui.view.dialog.NewCustomerAndRxDialog
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.ItemEvent
import java.awt.event.MouseEvent
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.List

/**
 * Created by magno on 30/09/14.
 */
class BusquedaClientePanel extends JPanel {

    private static final String TXT_TAB_TITLE = 'Cliente'

    private SwingBuilder sb
    private Customer customer = new Customer()
    private String defaultState
    private List<String> states
    private List<String> domains
    private List<LinkedHashMap<String, String>> locations
    private List<LinkedHashMap<String, Object>> titles
    private JTextField firstName
    private JTextField txtBirthDate
    private boolean edit
    private NewCustomerAndRxDialog CustomerAndDialog = null

    private static JComboBox dominio

    private static JTextField txtTelefono
    private static JTextField txtSms
    private static JTextField txtEmail
    private List<FormaContacto> formasContacto = new ArrayList<FormaContacto>()

    private String birthDate

    private static Integer formaContacto = 0
    private static final Integer TAG_ID_TELEFONO = 4
    private static final Integer TAG_ID_SMS = 3
    private static final Integer TAG_ID_CORREO = 1


    private JTable tFormas
    private DefaultTableModel model

    BusquedaClientePanel(Component parent) {
        CustomerAndDialog = parent
//        edit = editar
//        dominios = CustomerController.findAllCustomersDomains()
//        if (editar == true) {
//            this.formasContacto = ContactController.findCustomerContact(customer?.id)
//        }

        sb = new SwingBuilder()
//        this.customer = customer
/*        defaultState = CustomerController.findDefaultState()
        states = CustomerController.findAllStates()
        titles = CustomerController.findAllCustomersTitles()
        tipos = CustomerController.findAllContactTypes()
        dominios = CustomerController.findAllCustomersDomains()
        locations = []
        tmpHomeContact = new Contact(type: ContactType.HOME_PHONE)
        tmpEmailContact = new Contact(type: ContactType.EMAIL)
        initialize(customer)
        if (this.customer.dob == null) {
            birthDate = ""
        } else {
            birthDate = this.customer.dob.format("dd-MM-yyyy")
        }*/
        buildUI()
        doBindings()
    }

    Customer getCustomer() {
        return customer
    }

    private void initialize(Customer customer) {
        if (edit == true) {
            this.customer.type = customer?.type
            this.customer.rfc = customer.rfc
            this.customer.gender = customer.gender
            this.customer.address = customer.address
        } else {
            this.customer.type = CustomerType.DOMESTIC
            this.customer.rfc = CustomerType.DOMESTIC.rfc
            this.customer.gender = GenderType.MALE
            this.customer.address = new Address(state: defaultState)
        }
        if (customer?.id) {
            this.customer.id = customer.id
            this.customer.name = customer.name
            this.customer.fathersName = customer.fathersName
            this.customer.mothersName = customer.mothersName
            this.customer.title = customer.title
            this.customer.legalEntity = customer.legalEntity
            this.customer.rfc = customer.rfc
            this.customer.dob = customer.dob
            this.customer.gender = customer.gender
            if (customer.address) {
                this.customer.address = new Address(
                        primary: customer.address.primary,
                        zipcode: customer.address.zipcode,
                        location: customer.address.location,
                        city: customer.address.city,
                        state: customer.address.state
                )
            }
        }
    }

    private void buildUI() {
        sb.panel(this, layout: new MigLayout('fill,wrap', '[fill]')) {
//            panel(border: titledBorder(''), layout: new MigLayout('wrap 4', '[][fill,grow][][fill,grow]', '[][]')){
            panel(border: titledBorder(''), layout: new MigLayout('wrap 4', '[][fill,grow][][fill,grow]', '[][]')) {
                label('Saludo')
//                salutation = comboBox(items: titles*.title, itemStateChanged: titleChanged)

                label('Sexo')
//                gender = comboBox(items: GenderType.values())

                label('Nombre')
//                firstName = textField(document: new UpperCaseDocument())

                label('Apellido Paterno')
//                fathersName = textField(document: new UpperCaseDocument())

                label('Apellido Materno')
//                mothersName = textField(document: new UpperCaseDocument())

                label('F. Nacimiento')
                //dob = spinner( model: spinnerDateModel() )
//                txtBirthDate = textField(text: birthDate)
/*                txtBirthDate.addFocusListener(new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                    }

                    @Override
                    void focusLost(FocusEvent e) {
                        txtBirthDate.setText(setValidDate(txtBirthDate.text))
                    }
                }
                )*/

            }
        }
    }


    private void doBindings() {
        sb.build {
/*            bean(firstName, text: bind(source: customer, sourceProperty: 'name', mutual: true))
            bean(fathersName, text: bind(source: customer, sourceProperty: 'fathersName', mutual: true))
            bean(mothersName, text: bind(source: customer, sourceProperty: 'mothersName', mutual: true))
            bean(salutation, selectedItem: bind(source: customer, sourceProperty: 'title', mutual: true))
            //bean(dob, value: bind(source: customer, sourceProperty: 'dob', mutual: true))
            bean(gender, selectedItem: bind(source: customer, sourceProperty: 'gender', mutual: true))
            bean(primary, text: bind(source: customer.address, sourceProperty: 'primary', mutual: true))
            bean(stateField, selectedItem: bind(source: customer.address, sourceProperty: 'state', mutual: true))
            bean(city, selectedItem: bind(source: customer.address, sourceProperty: 'city', mutual: true))
            bean(locationField, selectedItem: bind(source: customer.address, sourceProperty: 'location', mutual: true))
            bean(zipcode, selectedItem: bind(source: customer.address, sourceProperty: 'zipcode', mutual: true))
            txtBirthDate.setText(birthDate)*/
        }
        /*if (edit == true) {
          this.formasContacto.clear()
          this.formasContacto = ContactController.findCustomerContact(customer?.id)

        }*/
        //model.fireTableDataChanged()
    }

    private boolean isValidInput() {
        Boolean validData = true
        if (StringUtils.isNotBlank(firstName.text)) {

        } else {
            validData = false
            sb.optionPane(
                    message: 'Se debe registrar el nombre',
                    messageType: JOptionPane.ERROR_MESSAGE
            ).createDialog(this, 'No se puede registrar la venta')
                    .show()
        }
        if (StringUtils.trimToEmpty(txtTelefono.text) != '') {
            if (StringUtils.trimToEmpty(txtTelefono.text).length() == 10) {

            } else {
                validData = false
                sb.optionPane(message: 'El telefono debe tener 10 digitos')
                        .createDialog(txtTelefono, 'Telefono incorrecto')
                        .show()
            }
        }
        if (StringUtils.trimToEmpty(txtSms.text) != '') {
            if (StringUtils.trimToEmpty(txtSms.text).length() == 10) {

            } else {
                validData = false
                sb.optionPane(message: 'El telefono debe tener 10 digitos')
                        .createDialog(txtTelefono, 'Telefono incorrecto')
                        .show()
            }
        }
        if (StringUtils.trimToEmpty(txtBirthDate.text).length() > 0) {
            Date validDate = null
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy")
            Integer dayInt = 0
            Integer monthInt = 0
            Integer yearInt = 0
            try {
                dayInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtBirthDate.text).substring(0, 2))
                monthInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtBirthDate.text).substring(3, 5))
                yearInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtBirthDate.text).substring(6, 10))
                validDate = df.parse(StringUtils.trimToEmpty(txtBirthDate.text))
                Calendar fecha = new GregorianCalendar();
                Integer currentYear = fecha.get(Calendar.YEAR);
                if (validDate != null && validDate.before(new Date()) && (dayInt <= 31 && monthInt <= 12 && yearInt < currentYear)) {
                    validData = true
                } else {
                    validData = false
                    sb.optionPane(message: 'La fecha de nacimiento debe ser menor a la fecha actual')
                            .createDialog(txtTelefono, 'Fecha incorrecta')
                            .show()
                }
            } catch (Exception e) {
                println e
            }
            if (validDate == null) {
                validData = false
                sb.optionPane(message: 'La fecha de nacimiento debe tener el formato dd-MM-yyyy')
                        .createDialog(txtTelefono, 'Fecha incorrecta')
                        .show()
            }
        }
        return validData
    }


    private def doSubmit = { ActionEvent ev ->
        JButton source = ev.source as JButton
        source.enabled = false
        SimpleDateFormat dF = new SimpleDateFormat("dd-MM-yyyy");
        customer?.contacts?.clear()
        Boolean validData = true
        if (isValidInput()) {
            customer.dob = StringUtils.trimToEmpty(txtBirthDate.text).length() > 0 ? dF.parse(txtBirthDate.text) : null
            Customer tmpCustomer = new Customer()
            if (edit == false) {
                tmpCustomer = CustomerController.addCustomer(this.customer)
                CustomerController.addClienteProceso(tmpCustomer)   //Se agrega registro en la tabla cliente_proceso
                CustomerController.changeMainContact(customer.id, formaContacto)
            } else {
                tmpCustomer = customer
            }
            if (tmpCustomer?.id) {
                customer = tmpCustomer
                CustomerController.saveContact(customer, 0, '')
                for (int b = 1; b <= 3; b++) {
                    CustomerController.saveContact(customer, b, '')
                }
                for (int a = 0; a < this.formasContacto.size(); a++) {

                    if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 1) {
                        CustomerController.saveContact(customer, 0, this.formasContacto.getAt(a)?.contacto)
                    } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 2) {
                        CustomerController.saveContact(customer, 1, this.formasContacto.getAt(a)?.contacto)
                    } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 3) {
                        CustomerController.saveContact(customer, 2, this.formasContacto.getAt(a)?.contacto)
                    } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 4) {
                        CustomerController.saveContact(customer, 3, this.formasContacto.getAt(a)?.contacto)
                    }
                }
                if (StringUtils.trimToEmpty(txtTelefono.text) != '') {
                    if (StringUtils.trimToEmpty(txtTelefono.text).length() == 10) {
                        CustomerController.saveContact(customer, 2, StringUtils.trimToEmpty(txtTelefono.text))
                    } else {
                        validData = false
                        sb.optionPane(message: 'El telefono debe tener 10 digitos')
                                .createDialog(txtTelefono, 'Telefono incorrecto')
                                .show()
                    }
                }
                if (StringUtils.trimToEmpty(txtSms.text) != '') {
                    if (StringUtils.trimToEmpty(txtSms.text).length() == 10) {
                        CustomerController.saveContact(customer, 3, StringUtils.trimToEmpty(txtSms.text))
                    } else {
                        validData = false
                        sb.optionPane(message: 'El telefono debe tener 10 digitos')
                                .createDialog(txtTelefono, 'Telefono incorrecto')
                                .show()
                    }
                }
                if (StringUtils.trimToEmpty(txtEmail.text) != '') {
                    String correo = txtEmail?.text + '@' + dominio?.selectedItem?.toString()
                    CustomerController.saveContact(customer, 0, StringUtils.trimToEmpty(correo))
                }
                if (validData) {
                    CustomerController.updateCustomer(tmpCustomer)
                    this.doCancel()
                }

            }
        }
        source.enabled = true
    }

    protected void onMouseClickedAtContact(MouseEvent pEvent) {
        List<String> list = ['Correo', 'Recados', 'Telefono', 'SMS']
        if (SwingUtilities.isRightMouseButton(pEvent) && (pEvent.getID() == MouseEvent.MOUSE_CLICKED)) {

            for (int a = 0; a < this.formasContacto.size(); a++) {
                println('id_tipo_contacto: ' + this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto)
                if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 1) {
                    list.remove('Correo')
                } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 2) {
                    list.remove('Recados')
                } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 3) {
                    list.remove('Telefono')
                } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 4) {
                    list.remove('SMS')
                }
            }

            if (list.size() > 0) {

                ContactDialogNewCustomer contacto = new ContactDialogNewCustomer(new FormaContacto(), false, list)
                contacto.activate()

                if (contacto.formaContacto?.tipoContacto?.id_tipo_contacto != null) {
                    this.formasContacto.add(contacto.formaContacto)
                    model.fireTableDataChanged()
                }

            }
        }
        if (SwingUtilities.isLeftMouseButton(pEvent)) {
            if (pEvent.clickCount == 2) {

                int index = tFormas.convertRowIndexToModel(tFormas.getSelectedRow())
                if (tFormas.selectedRowCount > 0) {

                    ContactDialogNewCustomer contacto = new ContactDialogNewCustomer(this.formasContacto.getAt(index), true, list)
                    contacto.activate()
                    if (contacto.borrar) {
                        this.formasContacto.remove(index)
                        model.fireTableDataChanged()
                    } else {
                        this.formasContacto.remove(index)
                        this.formasContacto.add(contacto.formaContacto)
                        model.fireTableDataChanged()
                    }
                }

            }
        }
    }

    private void doCancel() {
        CustomerAndDialog.setVisible(false)
        sb.dispose()
    }

    String getTitle() {
        return TXT_TAB_TITLE
    }
}



