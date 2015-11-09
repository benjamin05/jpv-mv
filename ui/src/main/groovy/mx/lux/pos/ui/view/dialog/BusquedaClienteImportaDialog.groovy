package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.model.ClienteProceso
import mx.lux.pos.model.Examen
import mx.lux.pos.repository.ExamenRepository
import mx.lux.pos.service.ExamenService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.service.impl.ExamenServiceImpl
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.model.Customer
import mx.lux.pos.ui.model.CustomerListener
import mx.lux.pos.ui.model.OperationType
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import javax.swing.table.TableRowSorter
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.MouseEvent
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.List;

/**
 * Created by magno on 30/09/14.
 */
class BusquedaClienteImportaDialog extends JDialog {
//    class CustomerActiveSelectionDialog extends JDialog {

    private static String TXT_DIALOG_TITLE = 'Búsqueda de cliente'
    private static String TXT_INSTRUCTIONS = '%d clientes activos en Proceso.'
    private static String TXT_CUST_NAME_LABEL = 'Cliente'
    private static String TXT_STAGE_LABEL = 'Etapa'
    private static String TXT_ORDER_COUNT_LABEL = 'Notas'

    private SwingBuilder sb = new SwingBuilder()
    //private Logger logger = LoggerFactory.getLogger(this.getClass())

    private Collection<ClienteProceso> customerList
    private ClienteProceso selection
    private JTable tClientes
    private Boolean requestNew
    private Boolean requestMod
    private String txtBirthDate
    private DefaultTableModel customersModel
    private DefaultTableModel externalCustomersModel
    private Customer customer
    private List<Customer> customers
    private List<Customer> externalCustomers
    private JTextField txtFechaNacimiento
    private JTextField txtApellidoPaterno
    private JTextField txtApellidoMaterno
    private JTextField txtNombre
    private JLabel labelMessage
    private JButton buttonBuscar
    private JButton buttonLimpiar
    private JButton buttonNuevo
    private JButton buttonCancelar

    BusquedaClienteImportaDialog(CustomerListener pListener) {
      customers = [ ] as ObservableList
      externalCustomers = [ ] as ObservableList
      this.buildUI()
      pListener.operationTypeSelected = OperationType.DEFAULT
    }

    protected void buildUI() {
        sb.dialog( this,
                title: TXT_DIALOG_TITLE,
                location: [ 70, 150 ] as Point,
                resizable: true,
                preferredSize: [ 800, 450 ],
                modal: true,
                pack: true,
                layout: new MigLayout( 'wrap 3', '[fill][fill,grow]' )
        ) {
                panel(layout: new MigLayout('wrap 5', '[fill,grow]', '[][][]')) {
                    label('Fecha de Nacimiento:', constraints: 'span')

                    txtFechaNacimiento = textField( text: txtBirthDate, horizontalAlignment: SwingConstants.CENTER )
                    txtFechaNacimiento.addFocusListener( new FocusListener() {
                        @Override
                        void focusGained(FocusEvent e) {
                        }

                        @Override
                        void focusLost(FocusEvent e) {
                            txtFechaNacimiento.setText(setValidDate( txtFechaNacimiento.text ))
                        }
                    })
                }

                panel(layout: new MigLayout('wrap 2,', '[fill][fill,grow]', '[]')) {
                    label('Apellido Paterno:')
                    txtApellidoPaterno = textField(document: new UpperCaseDocument())
                    label('Apellido Materno:')
                    txtApellidoMaterno = textField(document: new UpperCaseDocument())
                    label('Nombre:')
                    txtNombre = textField(document: new UpperCaseDocument())
                }

                panel(layout: new MigLayout('fill,right,wrap', '[fill]')) {
                    buttonBuscar = button('Buscar', defaultButton: true, actionPerformed: doBuscar)
                    buttonLimpiar = button('Limpiar', actionPerformed: doLimpia)
                }

                labelMessage = label('Resultados:', constraints: 'span')

                scrollPane(constraints: 'span, h 100!',
                        verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        horizontalScrollBarPolicy: JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                ) {
                    table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doSeleccion) {

                        customersModel = tableModel(list: customers) {
                            closureColumn(header: 'Sucursal', read: { Registry.getCurrentSite() })
                            closureColumn(header: 'F. de Nacimiento', read: { Customer tmp -> tmp?.getFechaFormato("fechaNacimiento") })
                            closureColumn(header: 'Nombre', read: { Customer tmp -> tmp?.name })
                            closureColumn(header: 'Apellido Paterno', read: { Customer tmp -> tmp?.fathersName })
                            closureColumn(header: 'Apellido Materno', read: { Customer tmp -> tmp?.mothersName })
                            closureColumn(header: 'Celular', read: { Customer tmp -> tmp?.getFormaContactoMovil() })
                            closureColumn(header: 'Examen', read: { Customer tmp -> tmp?.getFechaFormato("fechaUltimoExamen") } )
                            closureColumn(header: 'Venta', read: { Customer tmp -> tmp?.getFechaFormato("fechaUltimaVenta") })

                        } as DefaultTableModel
                    }
                }
            label( " ", constraints: 'span' )
            scrollPane( constraints: 'span, h 100!' ) {
              table( selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: onImportCustomersClick ) {
                externalCustomersModel = tableModel( list: externalCustomers ) {
                  closureColumn( header: 'Sucursal', read: {Customer tmp -> tmp?.idBranch}, maxWidth: 80 )
                  closureColumn( header: 'Nombre', read: {Customer tmp -> tmp?.name} )
                  closureColumn( header: 'Fecha Nac.', read: {Customer tmp -> tmp?.fechaNacimiento}, cellRenderer: new DateCellRenderer(), minWidth: 90, maxWidth: 100 )
                  closureColumn( header: 'Ult. Venta', read: {Customer tmp -> tmp?.fechaUltimaVenta}, cellRenderer: new DateCellRenderer(), minWidth: 90, maxWidth: 100 )
                } as DefaultTableModel
              }
            }

                panel(layout: new MigLayout('right', '[fill]'), constraints: 'span') {
                    buttonNuevo = button('Cliente Nuevo', enabled: false, actionPerformed: doNuevo )
                    buttonCancelar = button('Cancelar', actionPerformed: doCancelar )
                }
        }
    }

    // Internal Methods
    protected Comparator<ClienteProceso> getSorter() {
        Comparator<ClienteProceso> sorter = new Comparator<ClienteProceso>() {
            int compare(ClienteProceso cust1, ClienteProceso cust2) {
                return cust1.cliente.nombreCompleto.compareToIgnoreCase(cust2.cliente.nombreCompleto)
            }
        }
        return sorter
    }

    // UI Management
    protected void updateUI() {
//        this.model.fireTableDataChanged()
//        this.lblInstructions.text = String.format(TXT_INSTRUCTIONS, (customerList != null ? customerList.size() : 0))
    }

    // Public methods
    void activate() {
        this.updateUI()
        this.selection = null
        requestNew = false
        this.setVisible(true)
    }

    Customer getCustomerSelected() {
        return customer
    }

    Boolean isNewRequested() {
        return requestNew
    }

    Boolean isModRequested() {
        return requestMod
    }

    private def doBuscar = { ActionEvent ev ->
      JButton source = ev.source as JButton
      source.enabled = false
      if ( StringUtils.trimToEmpty(txtFechaNacimiento.text).length() == 0 && StringUtils.trimToEmpty(txtApellidoPaterno.text).length() == 0 &&
              StringUtils.trimToEmpty(txtApellidoMaterno.text).length() == 0 && StringUtils.trimToEmpty(txtNombre.text).length() == 0 ) {
        source.enabled = true
        return null
      }
      customer = new Customer()
      SimpleDateFormat dF = new SimpleDateFormat("dd-MM-yyyy");
      customer.fechaNacimiento = StringUtils.trimToEmpty(txtFechaNacimiento.text).length() > 0 ? dF.parse( txtFechaNacimiento.text ) : null
      customer.fathersName = StringUtils.trimToEmpty(txtApellidoPaterno.getText())
      customer.mothersName = StringUtils.trimToEmpty(txtApellidoMaterno.getText())
      customer.name = StringUtils.trimToEmpty(txtNombre.getText())
      customers.clear()
      externalCustomers.clear()
      if ( customer.fathersName != null || customer.fechaNacimiento != null){
        customers.addAll(CustomerController.findCustomersFechaNacimientoApellidoPaterno(customer))
      }
      if ( customer.fathersName != null ){
        externalCustomers.addAll( CustomerController.listCustomersFromMainDb( customer ) )
      }

      customersModel.fireTableDataChanged()
      externalCustomersModel.fireTableDataChanged()
      buttonNuevo.enabled = true
      labelMessage.text = "Resultados: ${customers.size()+externalCustomers.size()}"
      source.enabled = true
      buttonNuevo.enabled = true
    }

    private def doLimpia = {
        customers.clear()
        externalCustomers.clear()
        customersModel.fireTableDataChanged()
        externalCustomersModel.fireTableDataChanged()
        txtApellidoPaterno.setText("")
        txtApellidoMaterno.setText("")
        txtNombre.setText("")
        txtBirthDate = ""
        txtFechaNacimiento.setText("")
        buttonNuevo.enabled = false
    }

    private def doNuevo = {
        requestMod = false
        requestNew = true
        selection = null
        this.setVisible(false)
    }

    private def doCancelar = {
       requestMod = false
        requestNew = false
        selection = null
        this.setVisible(false)
    }

    private def doSeleccion = { MouseEvent ev ->
        if ( SwingUtilities.isLeftMouseButton( ev ) ) {
            if ( ev.clickCount == 2 ) {
                customer = ev.source.selectedElement
                requestMod = true
                requestNew = false
                System.out.println("Valor cliente: " + customer)
                this.setVisible(false)
            }
        }
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

  private def onImportCustomersClick = { MouseEvent ev ->
    if ( SwingUtilities.isLeftMouseButton( ev ) ) {
      if ( ev.clickCount == 2 ) {
        customer = ev.source.selectedElement
        dispose()
        customer = CustomerController.importCustomersFromMainDb(customer)
        if( customer.id ){
          NewCustomerAndRxDialog dialog = new NewCustomerAndRxDialog( this, customer, true )
          dialog.show()
          if( dialog.canceled ){
            this.customer = dialog.customer
          }
        }
      }
    }
  }
}

