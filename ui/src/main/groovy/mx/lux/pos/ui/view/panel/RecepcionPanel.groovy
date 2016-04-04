package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Invoice
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.view.dialog.PopUpMenu
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent

class RecepcionPanel extends JPanel{

  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm'

  private SwingBuilder sb
  private JTextField txtViaje
  private JTextField txtFolio
  private JSpinner dateEnd
  private List<JbJava> lstBySend = new ArrayList<>()
  private List<JbJava> lstNotSend = new ArrayList<>()
  private List<String> dominios
  private Order order
  private Invoice invoice
  private Date today = new Date()
  private DefaultTableModel devModel
  public DefaultTableModel noSendModel
  public DefaultTableModel bySendModel
  private String travel
  private JTable sendTable
  private JTable notSendTable

  private static final String TAG_CANCELADO = 'T'

  RecepcionPanel( ) {
    sb = new SwingBuilder()
    buildUI()
    doBindings()
  }

  private void buildUI( ) {
    sb.panel( this, layout: new MigLayout('wrap', '[fill,grow]', '[fill,grow]') ) {
      panel( layout: new MigLayout( 'center,wrap 2', '[fill][fill,grow]' ) ) {
          label( 'Fecha' )
          dateEnd = spinner( model: spinnerDateModel(), enabled: false )
          dateEnd.editor = new JSpinner.DateEditor( dateEnd as JSpinner, 'dd/MM/yyyy' )
          dateEnd.value = today
          dateEnd.addChangeListener( new ChangeListener() {
              @Override
              void stateChanged( ChangeEvent e ) {
                  /*if ( dateEnd.value < dateStart.value ) {
                      dateStart.value = DateUtils.addDays( dateEnd.value as Date, -10 )
                  }*/
              }
          } )
          label( 'Viaje' )
          txtViaje = textField( text: travel, enabled: false )
          label( 'Folio' )
          txtFolio = textField( )
      }

      panel( layout: new MigLayout( 'wrap 2', '150[fill,200!,center]100[fill,200!,center]150', '[270!]' ) ) {
        /*scrollPane( ) {
          table(selectionMode: ListSelectionModel.SINGLE_SELECTION) {
            devModel = tableModel(list: new ArrayList<String>()) {
              propertyColumn(header: "Devoluciones SP", propertyName: "", editable: false)
            } as DefaultTableModel
          }
        }*/
        scrollPane( ) {
          notSendTable = table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClickNotSend) {
            noSendModel = tableModel(list: lstNotSend) {
              closureColumn( header: 'No Enviar', read: {JbJava tmp -> tmp?.rx}, preferredWidth: 200)
            } as DefaultTableModel
          }
        }
        scrollPane( ) {
          sendTable = table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClickSend) {
            bySendModel = tableModel(list: lstBySend) {
              closureColumn( header: 'Enviar', read: {JbJava tmp -> tmp?.rx}, preferredWidth: 200)
            } as DefaultTableModel
          }
        }
      }

      panel( layout: new MigLayout( 'center', '80[fill,100!]80' ) ) {
        button( 'Cerrar Via'  )//actionPerformed: doPrintInvoice )
        button( 'Reimpresion' )//actionPerformed: doPrintReference )
        button( 'Previo', actionPerformed: doPrintPreviousPacking )//actionPerformed: doShowInvoice )
        button( 'Actualizar' )//actionPerformed: doRequest )
      }
    }
  }

  public void doBindings( ) {
    bySendModel.rowsModel.setValue(lstBySend);
    noSendModel.rowsModel.setValue(lstNotSend);
    noSendModel.fireTableDataChanged()
    bySendModel.fireTableDataChanged();
  }

  public void updateData(){
    lstBySend.clear()
    lstNotSend.clear()
    lstBySend = OrderController.jbBySend()
    lstNotSend = OrderController.jbNotSend()
    travel = OrderController.findCurrentTravel()
  }

  private void clearFields( ) {

  }

  public void limpiaPantalla(){
    lstBySend = OrderController.jbBySend()
    lstNotSend = OrderController.jbNotSend()
    travel = OrderController.findCurrentTravel()
    bySendModel.fireTableDataChanged()
    noSendModel.fireTableDataChanged()
  }


  private def doShowItemClickSend = { MouseEvent ev ->
    if (SwingUtilities.isRightMouseButton(ev)) {
      JbJava selectedData = ev.source.selectedElement as JbJava
      if( selectedData != null ){
        PopUpMenu menu = new PopUpMenu( ev.component, ev.component.getX(), ev.component.getY(), StringUtils.trimToEmpty(selectedData.rx), "envio,send", this );
      }
    }
  }

  private def doShowItemClickNotSend = { MouseEvent ev ->
    if (SwingUtilities.isRightMouseButton(ev)) {
      JbJava selectedData = ev.source.selectedElement as JbJava
      if( selectedData != null ){
        PopUpMenu menu = new PopUpMenu( ev.component, ev.component.getX(), ev.component.getY(), StringUtils.trimToEmpty(selectedData.rx), "envio,notsend", this );
      }
    }
  }


  private def doPrintPreviousPacking = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    OrderController.printPreviousPacking()
    source.enabled = true
  }


}
