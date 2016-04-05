package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.java.repository.JbTrack
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
import java.text.SimpleDateFormat

class RecepcionPanel extends JPanel{

  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm'
  private static final String TAG_ESTADO_EP = 'EP'
  private static final String TAG_ESTADO_REP = 'REP'
  private static final String TAG_ESTADO_RS = 'RS'
  private static final String TAG_ESTADO_TE = 'TE'
  private static final String TAG_ESTADO_CN = 'CN'
  private static final String TAG_ESTADO_PE = 'PE'

  private SwingBuilder sb
  private JTextField txtRx
  private JTextField txtViaje
  private JSpinner dateEnd
  private List<JbTrack> lstReceived = new ArrayList<>()
  private Order order
  private Invoice invoice
  private Date today = new Date()
  public DefaultTableModel receiveModel

  public SimpleDateFormat df = new SimpleDateFormat("HH:mm")

  private static final String TAG_CANCELADO = 'T'

  RecepcionPanel( ) {
    sb = new SwingBuilder()
    lstReceived = OrderController.findJbReveivedToday()
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
            receiveModel = tableModel(list: lstReceived) {
              closureColumn( header: 'Hora', read: {JbTrack tmp -> df.format(tmp.fecha)}, preferredWidth: 40)
              closureColumn( header: 'Rx', read: {JbTrack tmp -> tmp.rx}, preferredWidth: 50)
              closureColumn( header: 'Estado', read: {JbTrack tmp -> tmp.estado}, preferredWidth: 40)
              closureColumn( header: 'Cliente', read: {JbTrack tmp -> tmp.jb.cliente}, preferredWidth: 250)
              closureColumn( header: 'Material', read: {JbTrack tmp -> tmp.jb.material}, preferredWidth: 250)
            } as DefaultTableModel
          }
        }
      }
    }
  }

  public void doBindings( ) {
    txtViaje.text = ''
    txtRx.text = ''
    receiveModel.rowsModel.setValue(lstReceived)
    receiveModel.fireTableDataChanged()
  }


  private def doSearchRx = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    if( StringUtils.trimToEmpty(txtRx.text).length() > 0 ){
      if( StringUtils.trimToEmpty(txtViaje.text).length() > 0 || !StringUtils.trimToEmpty(txtViaje.text).isNumber() ){
        JbJava jb = OrderController.findJbByRx( StringUtils.trimToEmpty(txtRx.text) )
        if( jb != null ){
          if( StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase(TAG_ESTADO_EP) ||
                  StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase(TAG_ESTADO_REP) ){
            ReceiveDialog dialog = new ReceiveDialog( jb, StringUtils.trimToEmpty(txtViaje.text) )
            dialog.show()
            lstReceived = OrderController.findJbReveivedToday()
            doBindings()
          } else if( StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase(TAG_ESTADO_PE) ){
            Integer question = JOptionPane.showConfirmDialog(new JDialog(), "El trabajo no ha sido enviado ¿Desea recibirlo?",
                    "Trabajo no enviado", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)
            if( question == 0 ){
              ReceiveDialog dialog = new ReceiveDialog( jb, StringUtils.trimToEmpty(txtViaje.text) )
              dialog.show()
              lstReceived = OrderController.findJbReveivedToday()
              doBindings()
            }
          } else if( StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase(TAG_ESTADO_RS) ){
            sb.optionPane( message: 'El trabajo ya fue recibido',messageType: JOptionPane.ERROR_MESSAGE)
                  .createDialog(this, 'Error').show()
          } else if( StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase(TAG_ESTADO_TE) ){
            sb.optionPane( message: 'No se puede recibir un trabajo Entregado',messageType: JOptionPane.ERROR_MESSAGE)
                  .createDialog(this, 'Error').show()
          } else if( StringUtils.trimToEmpty(jb.estado).equalsIgnoreCase(TAG_ESTADO_CN) ){
            sb.optionPane( message: 'No se puede recibir un trabajo Cancelado',messageType: JOptionPane.ERROR_MESSAGE)
                  .createDialog(this, 'Error').show()
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
