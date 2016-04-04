package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Invoice
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.dialog.PopUpMenu
import mx.lux.pos.ui.view.dialog.ReceiveDialog
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent

class RecepcionPanel extends JPanel{

  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm'
  private static final String TAG_ESTADO_EP = 'EP'

  private SwingBuilder sb
  private JTextField txtRx
  private JTextField txtViaje
  private JSpinner dateEnd
  private List<JbJava> lstBySend = new ArrayList<>()
  private List<JbJava> lstNotSend = new ArrayList<>()
  private List<String> dominios
  private Order order
  private Invoice invoice
  private Date today = new Date()
  public DefaultTableModel receiveModel

  private static final String TAG_CANCELADO = 'T'

  RecepcionPanel( ) {
    sb = new SwingBuilder()
    buildUI()
    doBindings()
  }

  private void buildUI( ) {
    sb.panel( this, layout: new MigLayout('wrap ', '[fill,grow]', '[fill]') ) {
      panel( border: titledBorder(title: 'Busqueda'), layout: new MigLayout( 'center,wrap 2', '[fill][fill,grow]' ), maximumSize: [250,220] ) {
          label( 'Rx' )
          txtRx = textField( )
          label( 'Viaje' )
          txtViaje = textField( )
          button( 'Buscar', actionPerformed: doSearchRx, maximumSize: UI_Standards.BUTTON_SIZE, constraints: 'span2' )
      }
      panel( layout: new MigLayout( 'wrap ', '[fill,grow]', '[]' ) ) {
        scrollPane( ) {
          table(selectionMode: ListSelectionModel.SINGLE_SELECTION){//, mouseClicked: doShowItemClickNotSend) {
            receiveModel = tableModel(list: new ArrayList<String>()) {
              closureColumn( header: 'Hora', read: {String tmp -> tmp}, preferredWidth: 60)
              closureColumn( header: 'Rx', read: {String tmp -> tmp}, preferredWidth: 50)
              closureColumn( header: 'Estado', read: {String tmp -> tmp}, preferredWidth: 100)
              closureColumn( header: 'Cliente', read: {String tmp -> tmp}, preferredWidth: 180)
              closureColumn( header: 'Material', read: {String tmp -> tmp}, preferredWidth: 180)
            } as DefaultTableModel
          }
        }
      }
    }
  }

  public void doBindings( ) {

  }


  private def doSearchRx = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    if( StringUtils.trimToEmpty(txtRx.text).length() > 0 ){
      if( StringUtils.trimToEmpty(txtViaje.text).length() > 0 ){
        JbJava jb = OrderController.findJbByRx( StringUtils.trimToEmpty(txtRx.text) )
        if( jb != null ){
          if( StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase(TAG_ESTADO_EP) ){
            ReceiveDialog dialog = new ReceiveDialog( jb )
            dialog.show()
          }
        }
      } else {
        sb.optionPane( message: 'Viaje no valido',messageType: JOptionPane.ERROR_MESSAGE)
              .createDialog(this, 'Error').show()
      }
    } else {
      sb.optionPane( message: 'La Rx no existe',messageType: JOptionPane.ERROR_MESSAGE)
              .createDialog(this, 'Error').show()
    }
    source.enabled = true
  }


}
