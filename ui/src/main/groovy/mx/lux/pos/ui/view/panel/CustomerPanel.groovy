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
import org.apache.poi.util.StringUtil

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

class CustomerPanel extends JPanel {

    private static final String TXT_TAB_TITLE = 'Cliente'

    private SwingBuilder sb
    private Customer customer = new Customer()
    private String defaultState
    private List<String> states
    private List<String> domains
    private List<LinkedHashMap<String, String>> locations
    private List<LinkedHashMap<String, Object>> titles
    private Contact tmpHomeContact
    private Contact tmpEmailContact
    private JTextField firstName
    private JTextField fathersName
    private JTextField mothersName
    private JTextField primary
    private JTextField homePhone
    private JTextField email
    private JComboBox salutation
    private JComboBox stateField
    private JComboBox locationField
    private JComboBox gender
    private JComboBox city
    private JComboBox zipcode
    private JComboBox domain
    private JSpinner dob
    private JTextField txtBirthDate
    private JPanel customerPanel
    private static JTextField correo
    private static JLabel arroba
    private boolean edit
    public boolean cancel = false
    private NewCustomerAndRxDialog CustomerAndDialog = null

    private ButtonGroup typeContact
    private JRadioButton cbHouse
    private JRadioButton cbCell
    private JRadioButton cbEmail

    private static JComboBox tipo
    private static List<String> tipos
    private static JComboBox dominio
    private static List<String> dominios
    private static JTextField infoTipo

    private static JTextField txtTelefono
    private static JTextField txtSms
    private static JTextField txtEmail
    private static JPanel contactEdit
    private static JPanel contactReg
    private static JPanel addContact
    private List<FormaContacto> formasContacto = new ArrayList<FormaContacto>()

    private String birthDate

    private static Integer formaContacto = 0
    private static final Integer TAG_ID_TELEFONO = 4
    private static final Integer TAG_ID_SMS = 3
    private static final Integer TAG_ID_CORREO = 1


    private JTable tFormas
    private DefaultTableModel model

    CustomerPanel(Component parent, Customer customer, boolean editar) {
        CustomerAndDialog = parent
        edit = editar
        dominios = CustomerController.findAllCustomersDomains()
        if (editar == true) {
            this.formasContacto = ContactController.findCustomerContact(customer?.id)

        }
        sb = new SwingBuilder()
        this.customer = customer
        defaultState = CustomerController.findDefaultState()
        states = CustomerController.findAllStates()
        titles = CustomerController.findAllCustomersTitles()
        tipos = CustomerController.findAllContactTypes()
        dominios = CustomerController.findAllCustomersDomains()
        locations = []
        tmpHomeContact = new Contact(type: ContactType.HOME_PHONE)
        tmpEmailContact = new Contact(type: ContactType.EMAIL)
        initialize(customer)
        if( this.customer.dob == null ){
            birthDate = ""
        } else {
            birthDate = this.customer.dob.format("dd-MM-yyyy")
        }
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
            panel(border: titledBorder(''), layout: new MigLayout('wrap 4', '[][fill,grow][][fill,grow]','[][]')) {
                label('Saludo')
                salutation = comboBox(items: titles*.title, itemStateChanged: titleChanged)

                label('Sexo')
                gender = comboBox(items: GenderType.values())

                label('Nombre')
                firstName = textField(document: new UpperCaseDocument())

                label( 'Apellido Paterno' )
                fathersName = textField( document: new UpperCaseDocument() )

                label( 'Apellido Materno' )
                mothersName = textField( document: new UpperCaseDocument() )

                label( 'F. Nacimiento' )
                //dob = spinner( model: spinnerDateModel() )
                txtBirthDate = textField( text: birthDate )
                txtBirthDate.addFocusListener( new FocusListener() {
                    @Override
                    void focusGained(FocusEvent e) {
                    }

                    @Override
                    void focusLost(FocusEvent e) {
                      txtBirthDate.setText(setValidDate( txtBirthDate.text ))
                    }
                })

            }

            panel(border: titledBorder('Contacto'), layout: new MigLayout("wrap 5", "[fill][fill][fill,grow][fill][fill,grow]")) {
                typeContact = buttonGroup()

                cbEmail = radioButton( buttonGroup: typeContact, actionPerformed: {principalSelected( TAG_ID_CORREO )},
                        constraints: 'hidemode 3', visible: !edit )
                label( text: 'Correo:', constraints: 'hidemode 3', visible: !edit )
                txtEmail = textField( constraints: 'hidemode 3', visible: !edit )
                arroba = label(text: '@', visible: !edit)
                dominio = comboBox(items: dominios, visible: !edit,editable:true )


                cbCell = radioButton( buttonGroup: typeContact, actionPerformed: {principalSelected( TAG_ID_SMS )},
                        constraints: 'hidemode 3', visible: !edit )
                label( text: 'Celular:', constraints: 'hidemode 3', visible: !edit )
                txtSms = textField( constraints: 'hidemode 3', visible: !edit )
                label()
                label()

                cbHouse = radioButton( buttonGroup: typeContact, actionPerformed: {principalSelected( TAG_ID_TELEFONO )},
                        constraints: 'hidemode 3', visible: !edit )
                label( text: 'Telefono:', constraints: 'hidemode 3', visible: !edit )
                txtTelefono = textField( constraints: 'hidemode 3', visible: !edit )
                label()
                label()



                contactReg = panel( layout: new MigLayout("wrap", "[fill,grow]"), visible: true, constraints: 'span 5') {
                    scrollPane(
                            mouseClicked: { MouseEvent ev -> onMouseClickedAtContact(ev) },
                            mouseReleased: { MouseEvent ev -> onMouseClickedAtContact(ev) }) {
                        tFormas = table(selectionMode: ListSelectionModel.SINGLE_SELECTION,
                                mouseClicked: { MouseEvent ev -> onMouseClickedAtContact(ev) },
                                mouseReleased: { MouseEvent ev -> onMouseClickedAtContact(ev) }) {
                            model = tableModel(list: formasContacto) {
                                closureColumn(header: 'Tipo de Contacto', minWidth: 100, read: { row -> return row.tipoContacto.descripcion })
                                closureColumn(header: 'Dato', minWidth: 180, read: { row -> return row.contacto })
                            } as DefaultTableModel
                        }
                    }
                }


            }

            panel(border: titledBorder('Dirección'), layout: new MigLayout('wrap 3', '[][fill,grow][]'), constraints: 'hidemode 3') {
                label('Calle y Número')
                primary = textField(document: new UpperCaseDocument(), constraints: 'span 2')

                label('Estado')
                stateField = comboBox(items: states, itemStateChanged: stateChanged, constraints: 'span 2')

                label('Delegación/Mnpo')
                city = comboBox(itemStateChanged: cityChanged, constraints: 'span 2')

                label('Colonia')
                locationField = comboBox(itemStateChanged: locationChanged, constraints: 'span 2')

                label('C.P.')
                zipcode = comboBox(editable: true)
                button('Buscar', actionPerformed: doSearch)
            }




            panel(layout: new MigLayout('right', '[fill,100!]')) {
                /*button('Borrar',
                        visible: customer?.id ? true : false,
                        actionPerformed: doDelete,
                        preferredSize: UI_Standards.BUTTON_SIZE
                )*/
                button('Aplicar',
                        actionPerformed: doSubmit,
                        preferredSize: UI_Standards.BUTTON_SIZE

                )
                button('Limpiar',
                        visible: customer?.id ? false : true,
                        actionPerformed: doClear,
                        constraints: 'hidemode 3',
                        preferredSize: UI_Standards.BUTTON_SIZE
                )
            }
        }

        //dob.editor = new JSpinner.DateEditor(dob as JSpinner, 'dd-MM-yyyy')
    }



    private void doBindings() {
        sb.build {
            bean(firstName, text: bind(source: customer, sourceProperty: 'name', mutual: true))
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
            txtBirthDate.setText( birthDate )
        }
        /*if (edit == true) {
          this.formasContacto.clear()
          this.formasContacto = ContactController.findCustomerContact(customer?.id)

        }*/
        //model.fireTableDataChanged()
    }

    private def doSearch = { ActionEvent ev ->
        JButton source = ev.source as JButton
        source.enabled = false
        List<Address> results = CustomerController.findAddresesByZipcode(zipcode.selectedItem as String) ?: []
        if (results.any()) {
            JOptionPane inputPane = sb.optionPane(message: 'Selecciona una colonia',
                    selectionValues: results*.location,
                    optionType: JOptionPane.OK_CANCEL_OPTION
            )
            inputPane.createDialog(zipcode, 'Resultados de búsqueda por C.P.').show()
            String selection = inputPane?.inputValue as String
            Address tmpAddress = results.find { Address tmp ->
                tmp?.location?.equalsIgnoreCase(selection)
            }
            if (tmpAddress != null) {
                sb.doOutside {
                    stateField.selectedItem = tmpAddress.state
                    city.selectedItem = tmpAddress.city
                    locationField.selectedItem = tmpAddress.location
                }
            }
        } else {
            sb.optionPane(message: 'No se encontraron resultados')
                    .createDialog(zipcode, 'No se encontraron resultados')
                    .show()
        }
        source.enabled = true
    }

    private def doDelete = { ActionEvent ev ->
        JButton source = ev.source as JButton
        source.enabled = false
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
        if( StringUtils.trimToEmpty(txtTelefono.text) != '' ){
            if( StringUtils.trimToEmpty(txtTelefono.text).length() == 10 ){

            } else {
                validData = false
                sb.optionPane(message: 'El telefono debe tener 10 digitos')
                        .createDialog(txtTelefono, 'Telefono incorrecto')
                        .show()
            }
        }
        if( StringUtils.trimToEmpty(txtSms.text) != '' ){
            if( StringUtils.trimToEmpty(txtSms.text).length() == 10 ){

            } else {
                validData = false
                sb.optionPane(message: 'El telefono debe tener 10 digitos')
                        .createDialog(txtTelefono, 'Telefono incorrecto')
                        .show()
            }
        }
        if( StringUtils.trimToEmpty(txtBirthDate.text).length() > 0 ){
          Date validDate = null
          SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy")
          Integer dayInt = 0
          Integer monthInt = 0
          Integer yearInt = 0
          try{
            dayInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtBirthDate.text).substring(0,2))
            monthInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtBirthDate.text).substring(3,5))
            yearInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(txtBirthDate.text).substring(6,10))
              validDate = df.parse( StringUtils.trimToEmpty(txtBirthDate.text) )
              Calendar fecha = new GregorianCalendar();
              Integer currentYear = fecha.get(Calendar.YEAR);
              if( validDate != null && validDate.before(new Date()) && (dayInt <= 31 && monthInt <= 12 && yearInt < currentYear) ){
                validData = true
              } else {
                validData = false
                sb.optionPane(message: 'La fecha de nacimiento debe ser menor a la fecha actual')
                      .createDialog(txtTelefono, 'Fecha incorrecta')
                      .show()
              }
          } catch ( Exception e ) {println e}
          if( validDate == null ){
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

        if ( ! validaDatos() ) {
            source.enabled = true
            return
        }

        SimpleDateFormat dF = new SimpleDateFormat("dd-MM-yyyy");
        customer?.contacts?.clear()
        Boolean validData = true

        if (isValidInput()) {
            customer.dob = StringUtils.trimToEmpty(txtBirthDate.text).length() > 0 ? dF.parse( txtBirthDate.text ) : null
            Customer tmpCustomer = new Customer()
            if (edit == false) {
                tmpCustomer = CustomerController.addCustomer(this.customer)
                CustomerController.addClienteProceso( tmpCustomer )   //Se agrega registro en la tabla cliente_proceso
                CustomerController.changeMainContact( customer.id, formaContacto )
            } else {
                tmpCustomer = customer
                CustomerController.addClienteProceso( tmpCustomer )   //Se agrega registro en la tabla cliente_proceso
            }
            if (tmpCustomer?.id) {
                customer = tmpCustomer
                CustomerController.saveContact(customer,0,'')
                for(int b=1;b<=3;b++){
                CustomerController.saveContact(customer,b,'')
                }
                for (int a = 0; a < this.formasContacto.size(); a++) {

                    if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 1) {
                         CustomerController.saveContact(customer,0,this.formasContacto.getAt(a)?.contacto)
                    } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 2) {
                        CustomerController.saveContact(customer,1,this.formasContacto.getAt(a)?.contacto)
                    } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 3) {
                        CustomerController.saveContact(customer,2,this.formasContacto.getAt(a)?.contacto)
                    } else  if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 4) {
                        CustomerController.saveContact(customer,3,this.formasContacto.getAt(a)?.contacto)
                    }
                }
                if( StringUtils.trimToEmpty(txtTelefono.text) != '' ){
                    if( StringUtils.trimToEmpty(txtTelefono.text).length() == 10 ){
                      CustomerController.saveContact(customer,2,StringUtils.trimToEmpty(txtTelefono.text))
                    } else {
                        validData = false
                        sb.optionPane(message: 'El telefono debe tener 10 digitos')
                                .createDialog(txtTelefono, 'Telefono incorrecto')
                                .show()
                    }
                }
                if( StringUtils.trimToEmpty(txtSms.text) != '' ){
                  if( StringUtils.trimToEmpty(txtSms.text).length() == 10 ){
                    CustomerController.saveContact(customer,3,StringUtils.trimToEmpty(txtSms.text))
                  } else {
                    validData = false
                      sb.optionPane(message: 'El telefono debe tener 10 digitos')
                              .createDialog(txtTelefono, 'Telefono incorrecto')
                      .show()
                  }
                }
                if( StringUtils.trimToEmpty(txtEmail.text) != '' ){
                    String correo = txtEmail?.text + '@' + dominio?.selectedItem?.toString()
                    CustomerController.saveContact(customer,0,StringUtils.trimToEmpty(correo))
                }
                if( validData ){
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
                if ( this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 1 ) {
                   list.remove('Correo')
                } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 2) {
                    list.remove('Recados')
                } else if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 3) {
                    list.remove('Telefono')
                } else  if (this.formasContacto.getAt(a)?.tipoContacto?.id_tipo_contacto == 4) {
                    list.remove('SMS')
                }
            }

            if(list.size()>0){

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
                    if(contacto.borrar){
                        this.formasContacto.remove(index)
                        model.fireTableDataChanged()
                    }else{
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

    boolean getCancel() {
        return cancel
    }

    private def doClear = {
        firstName.text = null
        fathersName.text = null
        mothersName.text = null
        salutation.selectedItem = null
        dob.value = new Date()
        Calendar cal = Calendar.getInstance()
        cal.setTime( new Date() )
        cal.add(Calendar.YEAR, -5)
        birthDate = ""
        gender.selectedItem = GenderType.MALE
        primary.text = null
        stateField.selectedItem = defaultState
        city.selectedItem = null
        locationField.selectedItem = null
        zipcode.selectedItem = null
    }

    private def titleChanged = { ItemEvent ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            String title = ev.item
            def tmpTitle = titles.find {
                it?.title?.equalsIgnoreCase(title)
            }
            switch (tmpTitle?.gender) {
                case 'f':
                    gender.selectedItem = GenderType.FEMALE
                    break
                case 'm':
                    gender.selectedItem = GenderType.MALE
                    break
            }
        }
    }



    private def stateChanged = { ItemEvent ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            String stateName = ev.item
            List<String> results = CustomerController.findCitiesByStateName(stateName) ?: []
            results.each {
                city.addItem(it)
            }
            city.selectedIndex = -1
        } else {
            city.removeAllItems()
            locationField.removeAllItems()
            zipcode.removeAllItems()
            locations.clear()
        }
    }

    private def cityChanged = { ItemEvent ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            String stateName = stateField.selectedItem
            String cityName = ev.item
            Set<String> zipcodes = []
            locations = CustomerController.findLocationsByStateNameAndCityName(stateName, cityName) ?: []
            locations.each {
                locationField.addItem(it?.location)
                zipcodes.add(it?.zipcode)
            }
            locationField.selectedIndex = -1
            zipcodes.sort().each {
                zipcode.addItem(it)
            }
        } else {
            locationField.removeAllItems()
            zipcode.removeAllItems()
            locations.clear()
        }
    }

    private def locationChanged = { ItemEvent ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            String locationName = ev.item
            def result = locations.find {
                it?.location?.equalsIgnoreCase(locationName)
            }
            zipcode.selectedItem = result?.zipcode
        }
    }

    String getTitle() {
        return TXT_TAB_TITLE
    }

    private void addContact() {
        contactReg.visible = true
        contactEdit.visible = true
        addContact.visible = false

    }

    private def typeChanged = { ItemEvent ev ->
        if (ev.stateChange == ItemEvent.SELECTED) {
            String typeName = ev.item
            if (typeName.trim() != 'CORREO') {
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

    protected void principalSelected( Integer formaContacto) {
        this.formaContacto = formaContacto
    }

    static String setValidDate( String date ){
        date = StringUtils.trimToEmpty( date )
        SimpleDateFormat df = new SimpleDateFormat( "ddMMyyyy" )
        SimpleDateFormat dfHyphen = new SimpleDateFormat( "dd-MM-yyyy" )
        SimpleDateFormat dfDiagonal = new SimpleDateFormat( "dd/MM/yyyy" )
        String dateFormat = ''
        if(date.length() <= 0){
          dateFormat = date
        } else {
          if( date.contains( '/' ) ){
            dateFormat = date.replaceAll("/","-")
          } else if( date.contains( '-' ) ){
            dateFormat = date
          } else {
            if(date.length() == 8 && date.isNumber()){
              String day = date.substring(0,2)
              String month = date.substring(2,4)
              String year = date.substring(4,8)
              Integer dayInt = 0
              Integer monthInt = 0
              Integer yearInt = 0
              try{
                dayInt = NumberFormat.getInstance().parse(day)
                monthInt = NumberFormat.getInstance().parse(month)
                yearInt = NumberFormat.getInstance().parse(year)
              } catch ( NumberFormatException ex ){
                println ex
              }
              Calendar fecha = new GregorianCalendar();
              Integer currentYear = fecha.get(Calendar.YEAR);
              if( dayInt > 31 || monthInt > 12 || yearInt >= currentYear){
                JOptionPane.showMessageDialog(new JLabel(), 'Verifique los valores de la fecha de nacimiento.',
                      'Fecha incorrecta', JOptionPane.INFORMATION_MESSAGE)
              } else {
                Date dateTmp = df.parse(date)
                dateFormat = dfHyphen.format(dateTmp)
              }
            } else {
              JOptionPane.showMessageDialog(new JLabel(), 'La fecha de nacimiento debe tener el formato dd-MM-yyyy',
                      'Fecha incorrecta', JOptionPane.INFORMATION_MESSAGE)
            }
          }
        }
        return dateFormat
    }

    private Boolean validaDatos() {

        Boolean completo = true


        if ( firstName.getText().equals("") ) {
            completo = false
        }

        if ( fathersName.getText().equals("") ) {
            completo = false
        }

        if ( mothersName.getText().equals("") ) {
            completo = false
        }

        if ( txtBirthDate.getText().equals("") ) {
            completo = false
        }

        if ( this.edit == true ) {
            if ( model.size() == 0 ) {
                completo = false
            }
        } else {
            if (formaContacto == 1) { // Correo
                if (txtEmail.getText().equals("")) {
                    completo = false
                }

                if (StringUtils.trimToEmpty(dominio.getSelectedItem().toString()).equals("")) {
                    completo = false
                }
            }

            if (formaContacto == 3) { // SMS
                if (txtSms.getText().equals("")) {
                    completo = false
                }
            }

            if (formaContacto == 4) { // Telefono
                if (txtTelefono.getText().equals("")) {
                    completo = false
                }
            }

            if (formaContacto == 0) {
                completo = false
            }
        }

        if ( primary.getText().equals("") ) {
            completo = false
        }

        if ( stateField.getSelectedItem() == null ) {
            completo = false
        }

        if ( city.getSelectedItem() == null ) {
            completo = false
        }

        if ( locationField.getSelectedItem() == null ) {
            completo = false
        }

        if ( StringUtils.trimToEmpty( zipcode.getSelectedItem().toString() ).equals("") ) {
            completo = false
        }

        if ( ! completo ) {
            JOptionPane.showMessageDialog(new JLabel(), 'Verificar captura o poner ND en el campo faltante',
                    'Captura incorrecta', JOptionPane.INFORMATION_MESSAGE)
        }

        return completo
    }
}