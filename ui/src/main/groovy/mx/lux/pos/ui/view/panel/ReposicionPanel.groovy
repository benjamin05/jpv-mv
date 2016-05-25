package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.ClientesJava
import mx.lux.pos.java.repository.EmpleadoJava
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.JbRotos
import mx.lux.pos.java.repository.JbTrack
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Invoice
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.dialog.PopUpMenu
import mx.lux.pos.ui.view.dialog.ReceiveDialog
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.text.SimpleDateFormat

class ReposicionPanel extends JPanel{

  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm'
  private static final String TAG_ESTADO_EP = 'EP'
  private static final String TAG_ESTADO_REP = 'REP'
  private static final String TAG_ESTADO_RS = 'RS'
  private static final String TAG_ESTADO_TE = 'TE'
  private static final String TAG_ESTADO_CN = 'CN'
  private static final String TAG_ESTADO_PE = 'PE'

  private SwingBuilder sb
  private JTextField txtRx
  private JTextField txtCliente
  private JSpinner dateEnd
  private List<JbJava> lstReceived = new ArrayList<>()
  private List<JbRotos> lstRotos = new ArrayList<>()
  private Order order
  private Invoice invoice
  private Date today = new Date()
  public DefaultTableModel receiveModel
  public DefaultTableModel rotosModel

  public SimpleDateFormat df = new SimpleDateFormat("HH:mm")

  private static final String TAG_CANCELADO = 'T'

    ReposicionPanel( ) {
    sb = new SwingBuilder()
    lstReceived = OrderController.findJbRotos()
    buildUI()
    doBindings()
    receiveModel.rowsModel.setValue(lstReceived)
    receiveModel.fireTableDataChanged()
  }

  private void buildUI( ) {
    sb.panel( this, layout: new MigLayout('wrap ', '[fill,grow]', '[fill]') ) {
      panel( border: titledBorder(title: 'Busqueda'), layout: new MigLayout( 'center,wrap 5', '[fill][fill,grow][fill][fill,grow][fill,grow]' ) ) {
          label( 'Rx' )
          txtRx = textField( )
          label( 'Cliente' )
          txtCliente = textField( )
          button( 'Buscar', actionPerformed: doSearchRx, maximumSize: UI_Standards.BUTTON_SIZE, constraints: 'span2' )
      }
      panel( layout: new MigLayout( 'wrap ', '[fill,grow]', '[]' ) ) {
        scrollPane( mouseClicked: doShowItemClickSend ) {
          table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClickSend){
            receiveModel = tableModel(list: lstReceived) {
              closureColumn( header: 'Rx', read: {JbJava tmp -> tmp.rx}, preferredWidth: 30)
              closureColumn( header: 'Material', read: {JbJava tmp -> tmp.material}, preferredWidth: 60)
              closureColumn( header: 'Cliente', read: {JbJava tmp -> tmp.cliente}, preferredWidth: 80)
            } as DefaultTableModel
          }
        }
      }
      panel( layout: new MigLayout( 'wrap ', '[fill,grow]', '[]' ) ) {
        scrollPane( ) {
          table(selectionMode: ListSelectionModel.SINGLE_SELECTION){
            rotosModel = tableModel(list: lstRotos) {
              closureColumn( header: 'No.', read: {JbRotos tmp -> tmp.getNumRoto()}, preferredWidth: 50)
              closureColumn( header: 'Fecha', read: {JbRotos tmp -> tmp.getFecha()}, cellRenderer: new DateCellRenderer(), preferredWidth: 50)
              closureColumn( header: 'Causa', read: {JbRotos tmp -> tmp.causa}, preferredWidth: 50)
              closureColumn( header: 'Elaboro', read: {JbRotos tmp -> empleadoJava(tmp?.repo?.emp)}, preferredWidth: 50)
              closureColumn( header: 'Tipo', read: {JbRotos tmp -> tmp?.repo?.tipo}, preferredWidth: 50)
            } as DefaultTableModel
          }
        }
      }
    }
  }

  public void doBindings( ) {
    //txtCliente.text = ''
    //txtRx.text = ''
    rotosModel.rowsModel.setValue(lstRotos)
    rotosModel.fireTableDataChanged()
  }


  private def doSearchRx = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    lstRotos.clear()
    if( StringUtils.trimToEmpty(txtRx.text).length() > 0 && StringUtils.trimToEmpty(txtCliente.text).length() > 0 ){
      List<JbJava> lstReceivedTmp = new ArrayList<>()
      lstReceived.clear()
      lstReceived.addAll(OrderController.findJbRotos())
      for(JbJava jb : lstReceived){
        if( StringUtils.trimToEmpty(txtRx.text).equalsIgnoreCase(StringUtils.trimToEmpty(jb.rx)) &&
                StringUtils.trimToEmpty(jb.cliente).contains(StringUtils.trimToEmpty(txtCliente.text.toUpperCase())) ){
          lstReceivedTmp.add(jb)
        }
      }
      lstReceived.clear()
      lstReceived.addAll(lstReceivedTmp)
      receiveModel.rowsModel.setValue(lstReceived)
      receiveModel.fireTableDataChanged()
    } else if( StringUtils.trimToEmpty(txtRx.text).length() > 0 ){
      List<JbJava> lstReceivedTmp = new ArrayList<>()
      lstReceived.clear()
      lstReceived.addAll(OrderController.findJbRotos())
      for(JbJava jb : lstReceived){
        if( StringUtils.trimToEmpty(txtRx.text).equalsIgnoreCase(StringUtils.trimToEmpty(jb.rx)) ){
          lstReceivedTmp.add(jb)
        }
      }
      lstReceived.clear()
      lstReceived.addAll(lstReceivedTmp)
      receiveModel.rowsModel.setValue(lstReceived)
      receiveModel.fireTableDataChanged()
    } else if(StringUtils.trimToEmpty(txtCliente.text).length() > 0) {
      List<JbJava> lstReceivedTmp = new ArrayList<>()
      lstReceived.clear()
      lstReceived.addAll(OrderController.findJbRotos())
      for(JbJava jb : lstReceived){
        if( StringUtils.trimToEmpty(jb.cliente).contains(StringUtils.trimToEmpty(txtCliente.text.toUpperCase())) ){
          lstReceivedTmp.add(jb)
        }
      }
      lstReceived.clear()
      lstReceived.addAll(lstReceivedTmp)
      receiveModel.rowsModel.setValue(lstReceived)
      receiveModel.fireTableDataChanged()
    } else {
      lstReceived = OrderController.findJbRotos()
      receiveModel.rowsModel.setValue(lstReceived)
      receiveModel.fireTableDataChanged()
    }
    doBindings()
    source.enabled = true
  }



  private def doShowItemClickSend = { MouseEvent ev ->
    if (SwingUtilities.isRightMouseButton(ev)) {
      if( ev?.source instanceof JTable ){
        JbJava selectedData = ev?.source?.selectedElement as JbJava
        if( selectedData != null ){
          PopUpMenu menu = new PopUpMenu( ev.component, ev.component.getX(), ev.component.getY(), StringUtils.trimToEmpty(selectedData.rx), "reposicion", this );
        }
      } else {
        PopUpMenu menu = new PopUpMenu( ev.component, ev.component.getX(), ev.component.getY(), "", "reposicion", this );
      }
    } else if (SwingUtilities.isLeftMouseButton(ev)) {
      if (ev.clickCount == 1 && ev?.source instanceof JTable) {
        JbJava selectedData = ev.source.selectedElement as JbJava
        if( selectedData != null ){
          lstRotos.clear()
          lstRotos.addAll( OrderController.findJbRotosDet(selectedData.rx))
          doBindings()
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
