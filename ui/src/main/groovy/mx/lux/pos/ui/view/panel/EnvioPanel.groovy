package mx.lux.pos.ui.view.panel

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.java.repository.JbJava
import mx.lux.pos.model.IPromotionAvailable
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.InvoiceController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.controller.TaxpayerController
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.view.dialog.PopUpMenu
import mx.lux.pos.ui.view.dialog.SuggestedTaxpayersDialog
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils

import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.util.List

class EnvioPanel extends JPanel{

  private static final String DATE_TIME_FORMAT = 'dd-MM-yyyy HH:mm'

  private SwingBuilder sb
  private JTextField txtViaje
  private JTextField txtFolio
  private JSpinner dateEnd
  private List<JbJava> lstBySend
  private List<JbJava> lstNotSend
  private List<String> dominios
  private Order order
  private Invoice invoice
  private Date today = new Date()
  private DefaultTableModel devModel
  private DefaultTableModel noSendModel
  private DefaultTableModel bySendModel
  private String travel
  private JTable sendTable
  private JTable notSendTable

  private static final String TAG_CANCELADO = 'T'

  EnvioPanel( ) {
    sb = new SwingBuilder()
    updateData()
    buildUI()
    doBindings()
  }

  private void buildUI( ) {
    sb.panel( this, layout: new MigLayout('wrap', '[fill,grow]', '[fill,grow]') ) {
      panel( layout: new MigLayout( 'center,wrap 6', '[][fill,120!]30[][fill,100!]30[][fill,100!]' ) ) {
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
              closureColumn(
                header: 'No Enviar',
                read: { JbJava tmp -> tmp?.rx },
                minWidth: 200,
                maxWidth: 200
              )
            } as DefaultTableModel
          }
        }
        scrollPane( ) {
          sendTable = table(selectionMode: ListSelectionModel.SINGLE_SELECTION, mouseClicked: doShowItemClickSend) {
            bySendModel = tableModel(list: lstBySend) {
              closureColumn(
                header: 'Enviar',
                read: { JbJava tmp -> tmp?.rx },
                minWidth: 200,
                maxWidth: 200
              )
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
    sb.build {
      noSendModel.fireTableDataChanged()
      bySendModel.fireTableDataChanged();
    }
    noSendModel.fireTableDataChanged()
    bySendModel.fireTableDataChanged();
  }

  public void updateData(){
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
  }


}
