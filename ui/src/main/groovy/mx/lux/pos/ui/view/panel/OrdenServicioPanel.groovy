package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.EmpleadoJava
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.JbRotos
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Customer
import mx.lux.pos.ui.model.Invoice
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.dialog.ConsultCustomerDialog
import mx.lux.pos.ui.view.dialog.PopUpMenu
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.text.SimpleDateFormat

class OrdenServicioPanel extends JPanel{

  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm'
  private static final String TAG_ESTADO_EP = 'EP'
  private static final String TAG_ESTADO_REP = 'REP'
  private static final String TAG_ESTADO_RS = 'RS'
  private static final String TAG_ESTADO_TE = 'TE'
  private static final String TAG_ESTADO_CN = 'CN'
  private static final String TAG_ESTADO_PE = 'PE'

  private SwingBuilder sb
  private JTextField txtOrden
  private JTextField txtCliente
  private JSpinner dateEnd
  private List<JbJava> lstServiceOrders = new ArrayList<>()
  private List<JbRotos> lstRotos = new ArrayList<>()
  private Order order
  private Invoice invoice
  private Date today = new Date()
  public DefaultTableModel receiveModel
  public DefaultTableModel orderServiceModel

  public SimpleDateFormat df = new SimpleDateFormat("HH:mm")

  private static final String TAG_CANCELADO = 'T'

  OrdenServicioPanel( ) {
    sb = new SwingBuilder()
    buildUI()
    lstServiceOrders = OrderController.findJbServicerOrders( )
    doBindings()
  }

  private void buildUI( ) {
    sb.panel( this, layout: new MigLayout('wrap ', '[fill,grow]', '[fill]') ) {
      panel( border: titledBorder(title: 'Busqueda'), layout: new MigLayout( 'center,wrap 5', '[fill][fill,grow][fill][fill,grow][fill,grow]' ) ) {
          label( 'Orden' )
          txtOrden = textField( document: new UpperCaseDocument() )
          label( 'Cliente' )
          txtCliente = textField( document: new UpperCaseDocument() )
          button( 'Buscar', actionPerformed: doSearchRx, maximumSize: UI_Standards.BUTTON_SIZE, constraints: 'span2' )
      }
      panel( layout: new MigLayout( 'wrap ', '[fill,grow]', '[]' ) ) {
        scrollPane( mouseClicked: doShowItemClickSend ) {
          table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClickSend){
            receiveModel = tableModel(list: lstServiceOrders) {
              closureColumn( header: 'Orden', read: {JbJava tmp -> tmp.rx}, preferredWidth: 30)
              closureColumn( header: 'Cliente', read: {JbJava tmp -> tmp.cliente}, preferredWidth: 80)
              closureColumn( header: 'Fecha Orden', read: {JbJava tmp -> tmp.fechaVenta}, cellRenderer: new DateCellRenderer(), preferredWidth: 60)
              closureColumn( header: 'Fecha Promesa', read: {JbJava tmp -> tmp.fechaPromesa}, cellRenderer: new DateCellRenderer(), preferredWidth: 60)
            } as DefaultTableModel
          }
        }
      }
    }
  }

  public void doBindings( ) {
    //lstServiceOrders = OrderController.findJbServicerOrders( )
    receiveModel.rowsModel.setValue(lstServiceOrders)
    receiveModel.fireTableDataChanged()
  }


  private def doSearchRx = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    lstRotos.clear()
    if( StringUtils.trimToEmpty(txtOrden.text).length() > 0 && StringUtils.trimToEmpty(txtCliente.text).length() > 0 ){
      lstServiceOrders.clear()
      List<JbJava> lstServiceOrdersTmp = OrderController.findJbAllServicerOrders()
      for( JbJava jb : lstServiceOrdersTmp ){
        if( StringUtils.trimToEmpty(txtOrden.text).equalsIgnoreCase(StringUtils.trimToEmpty(jb.rx)) &&
                StringUtils.trimToEmpty(jb.cliente).contains(StringUtils.trimToEmpty(txtCliente.text.toUpperCase())) ){
          lstServiceOrders.add(jb)
        }
      }
    } else if( StringUtils.trimToEmpty(txtOrden.text).length() > 0 ){
      lstServiceOrders.clear()
      List<JbJava> lstServiceOrdersTmp = OrderController.findJbAllServicerOrders( )
      for( JbJava jb : lstServiceOrdersTmp ){
        if( StringUtils.trimToEmpty(jb.rx).equals(StringUtils.trimToEmpty(txtOrden.text)) ){
          lstServiceOrders.add(jb)
        }
      }
    } else if(StringUtils.trimToEmpty(txtCliente.text).length() > 0) {
      lstServiceOrders.clear()
      List<JbJava> lstServiceOrdersTmp = OrderController.findJbAllServicerOrders( )
      for( JbJava jb : lstServiceOrdersTmp ){
        if( StringUtils.trimToEmpty(jb.cliente).contains(StringUtils.trimToEmpty(txtCliente.text)) ){
          lstServiceOrders.add(jb)
        }
      }
    } else {
      lstServiceOrders.clear()
      lstServiceOrders = OrderController.findJbServicerOrders( )
    }
    doBindings()
    source.enabled = true
  }



  private def doShowItemClickSend = { MouseEvent ev ->
    if (SwingUtilities.isRightMouseButton(ev)) {
      if( ev?.source instanceof JTable ){
        JbJava selectedData = ev?.source?.selectedElement as JbJava
        if( selectedData != null ){
          PopUpMenu menu = new PopUpMenu( ev.component, ev.component.getX(), ev.component.getY(), StringUtils.trimToEmpty(selectedData.rx), "ordenServicio", this );
        }
      } else {
        PopUpMenu menu = new PopUpMenu( ev.component, ev.component.getX(), ev.component.getY(), "", "ordenServicio", this );
      }
    } else if (SwingUtilities.isLeftMouseButton(ev)) {
      if (ev.clickCount == 2 && ev?.source instanceof JTable) {
        JbJava selectedData = ev.source.selectedElement as JbJava
        Customer customer = CustomerController.findCustomerById( StringUtils.trimToEmpty(selectedData.idCliente) )
        if( customer != null && customer.id != null ){
          ConsultCustomerDialog dialog = new ConsultCustomerDialog( this, customer, false )
          dialog.show()
        }
      }
    }
  }

  private static String empleadoJava( String idEmp ){
    EmpleadoJava emp = OrderController.buscaEmpleado(idEmp)
    String nombre = StringUtils.trimToEmpty(emp?.nombreEmpleado)+" "+StringUtils.trimToEmpty(emp?.apPatEmpleado)+" "+StringUtils.trimToEmpty(emp?.apMatEmpleado)
    return nombre
  }


}
