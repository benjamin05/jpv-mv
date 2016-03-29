package mx.lux.pos.ui.view.dialog.dailyclose

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.ui.MainWindow
import mx.lux.pos.ui.controller.DailyCloseController
import mx.lux.pos.ui.controller.IOController
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.DailyClose
import mx.lux.pos.ui.model.Deposit
import mx.lux.pos.ui.model.Dioptra
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.view.dialog.WaitDialog
import mx.lux.pos.ui.view.panel.OrderPanel
import mx.lux.pos.ui.view.renderer.DateCellRenderer
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.text.NumberFormat
import java.util.List

class DailyCloseDepositsDialog extends JDialog {

  private SwingBuilder sb
  private DailyClose dailyClose
  private Date closeDate
  private JPanel parent
  private JLabel grossSell
  private JLabel modifications
  private JLabel cancelations
  private JLabel netSell
  private JLabel grossIncome
  private JLabel returns
  private JLabel netIncome
  private JLabel lblClosingDay
  private JTextArea observations
  private DefaultTableModel depositsModel
  private List<Deposit> deposits
  User user = Session.get( SessionItem.USER ) as User
  Boolean isManager = IOController.getInstance().isManager(user.username)

  DailyCloseDepositsDialog( JPanel parent, DailyClose dailyClose ) {
    this.dailyClose = dailyClose
    this.closeDate = dailyClose?.date
    this.parent = parent
    sb = new SwingBuilder()
    deposits = [ ] as ObservableList
    buildUI()
    fetchDeposits()
    doBindings()
  }

  private void fetchDeposits( ) {
    deposits.clear()
    deposits.addAll( DailyCloseController.findDepositsByDay( closeDate ) )
    depositsModel.fireTableDataChanged()
  }

  private void buildUI( ) {
    Point location = MainWindow.instance.location
    location.x += 30
    location.y += 30
    sb.dialog( this,
        title: "Depositos del d\u00eda ${closeDate?.format( 'dd/MM/yyyy' )} - D\u00eda ${dailyClose.isOpen() ? 'Abierto' : 'Cerrado'}",
        resizable: false,
        pack: true,
        modal: true,
        parent: parent,
        layout: new MigLayout( 'fill,wrap', '[fill]' ),
        location: location
    ) {
      sellsInconmePanel()
      depositsPanel()
      buttonsPanel()
    }
  }

  private void sellsInconmePanel( ) {
    sb.panel( layout: new MigLayout( 'fill,ins 0', '[fill]', '[fill]' ) ) {
      panel( border: titledBorder( 'Ventas' ), layout: new MigLayout( 'fill,wrap 2', '[][grow,right]', '[top]' ) ) {
        label( 'Venta bruta' )
        grossSell = label()
        label( 'Modificaciones' )
        modifications = label()
        label( 'Cancelaciones' )
        cancelations = label()
        label( 'Venta neta' )
        netSell = label()
      }
      panel( border: titledBorder( 'Ingresos' ), layout: new MigLayout( 'fill,wrap 2', '[][grow,right]', '[top]' ) ) {
        label( 'Ingresos brutos' )
        grossIncome = label()
        label( 'Devoluciones' )
        returns = label()
        label( 'Ingresos netos' )
        netIncome = label()
      }
    }
  }

  private void depositsPanel( ) {
    sb.panel( layout: new MigLayout( 'fill,wrap 2', '[fill][fill,100]', '[fill]' ) ) {
      scrollPane( constraints: 'h 250!' ) {
        table( selectionMode: ListSelectionModel.SINGLE_SELECTION, mousePressed: doPress ) {
          depositsModel = tableModel( list: deposits ) {
            closureColumn( header: 'No.', read: {Deposit tmp -> tmp?.number}, maxWidth: 30 )
            closureColumn( header: 'Fecha ingreso', read: {Deposit tmp -> tmp?.enterDate}, cellRenderer: new DateCellRenderer() )
            closureColumn( header: 'Fecha deposito', read: {Deposit tmp -> tmp?.depositDate}, cellRenderer: new DateCellRenderer() )
            closureColumn( header: 'Tipo', read: {Deposit tmp -> tmp?.depositType} )
            closureColumn( header: 'Banco', read: {Deposit tmp -> tmp?.bank} )
            closureColumn( header: 'Referencia', read: {Deposit tmp -> tmp?.reference} )
            closureColumn( header: 'Importe', read: {Deposit tmp -> tmp?.ammount}, cellRenderer: new MoneyCellRenderer() )
          } as DefaultTableModel
        }
      }

      panel( border: loweredEtchedBorder(), layout: new MigLayout( 'wrap', '[fill]' ) ) {
        button( text: 'Resumen diario', enabled: isManager, actionPerformed: printDailyDigest )
        button( text: 'Cierres de Term.', enabled: isManager, actionPerformed: closeTerminalsAction )
        button( text: 'Corregir Term.', enabled: dailyClose.isOpen() && isManager, actionPerformed: fixTerminalsAction )
        button( text: 'Nuevo Deposito', enabled: dailyClose.isOpen() && isManager,
            actionPerformed: {
              new EditDepositDialog( this, closeDate, null, false ).show()
              fetchDeposits()
            }
        )
        button( text: 'Archivos Maestro', visible: !dailyClose.isOpen(), actionPerformed: generateFiles, constraints: 'hidemode 3' )
      }

      scrollPane( border: titledBorder( title: 'Observaciones' ) ) {
        observations = textArea( document: new UpperCaseDocument() )
      }
      panel( layout: new MigLayout( 'wrap', '[fill,grow]' ) ) {
        button( 'Cerrar d\u00eda', enabled: dailyClose.isOpen() && isManager, constraints: 'skip', actionPerformed: doCloseDay )
      }
    }
  }

  private def doPress = { MouseEvent ev ->
    Deposit selection = ev.source.selectedElement as Deposit
    if ( SwingUtilities.isRightMouseButton( ev ) && dailyClose.isOpen() && selection?.id ) {
      sb.popupMenu {
        menuItem( 'Nuevo deposito',
            actionPerformed: {
              new EditDepositDialog( this, closeDate, null, false ).show()
              fetchDeposits()
            }
        )
        menuItem( 'Editar deposito',
            actionPerformed: {
              new EditDepositDialog( this, closeDate, selection?.id, true ).show()
              fetchDeposits()
            }
        )
        menuItem( 'Eliminar deposito',
            actionPerformed: {
              boolean isDeleted = DailyCloseController.deleteDeposit( selection?.id )
              if ( isDeleted ) {
                fetchDeposits()
                //sb.optionPane().showMessageDialog( null, "Se ha eliminado el Deposito correctamente", 'Deposito', JOptionPane.INFORMATION_MESSAGE )
              } else {
                sb.optionPane().showMessageDialog( null, "Se ha producido un error al eliminar el Deposito", 'Error', JOptionPane.ERROR_MESSAGE )
              }
            }
        )
      }.show( ev.component, ev.x, ev.y )
    }
  }

  private def buttonsPanel( ) {
    sb.panel( layout: new MigLayout( 'fill', '[right]' ) ) {
      button( text: '     Salir    ', actionPerformed: { dispose() } )
    }
  }

  private def currencyConverter = {
    NumberFormat.getCurrencyInstance( Locale.US ).format( it ?: 0 )
  }

  private void doBindings( ) {
    sb.build {
      bean( grossSell, text: bind( source: dailyClose, sourceProperty: 'grossSell', converter: currencyConverter ) )
      bean( modifications, text: bind( source: dailyClose, sourceProperty: 'modifications', converter: currencyConverter ) )
      bean( cancelations, text: bind( source: dailyClose, sourceProperty: 'cancelations', converter: currencyConverter ) )
      bean( netSell, text: bind( source: dailyClose, sourceProperty: 'netSell', converter: currencyConverter ) )
      bean( grossIncome, text: bind( source: dailyClose, sourceProperty: 'grossIncome', converter: currencyConverter ) )
      bean( returns, text: bind( source: dailyClose, sourceProperty: 'returns', converter: currencyConverter ) )
      bean( netIncome, text: bind( source: dailyClose, sourceProperty: 'netIncome', converter: currencyConverter ) )
      bean( observations, text: bind( source: dailyClose, sourceProperty: 'observations', mutual: true ) )
    }
  }

  private def doCloseDay = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    Boolean succesClose = false
    validDioptraByDate( closeDate )
    if( DailyCloseController.dayHasDeposit(closeDate) ){
      WaitDialog dialog = new WaitDialog( "Cierre", "Cerrando dia. Espere un momento." )
      sb.doOutside {
        succesClose = DailyCloseController.closeDailyClose( closeDate, observations.text )
        Long time = DailyCloseController.timeWait()
        sleep( time )
        dialog.dispose()
        if ( succesClose ) {
          sb.optionPane().showMessageDialog( null, 'Se ha cerrado correctamente', 'Ok', JOptionPane.INFORMATION_MESSAGE )
        } else {
          sb.optionPane().showMessageDialog( null, 'Error al cerrar', 'Error', JOptionPane.ERROR_MESSAGE )
        }
      }
      dialog.show()
    } else {
      sb.optionPane().showMessageDialog( null, 'Error al cerrar, no existen depositos', 'Error', JOptionPane.ERROR_MESSAGE )
      source.enabled = true
    }
  }

  private def generateFiles = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    if ( DailyCloseController.closeDailyClose( closeDate, observations.text ) ) {
      sb.optionPane().showMessageDialog( null, 'Se ha cerrado correctamente', 'Ok', JOptionPane.INFORMATION_MESSAGE )
    } else {
      sb.optionPane().showMessageDialog( null, 'Error al cerrar', 'Error', JOptionPane.ERROR_MESSAGE )
    }
    source.enabled = true
  }

  private def printDailyDigest = {
    DailyCloseController.printDailyDigest( closeDate )
  }

  private def fixTerminalsAction = {
    new TerminalFixDialog( closeDate ).show()
  }

  private def closeTerminalsAction = {
    new TerminalCloseDialog( closeDate ).show()
  }

  private static void validDioptraByDate (Date date ){
    List<Order> lstOrders = OrderController.findOrdersByDate( date )
    Dioptra dioptra = new Dioptra()
    Dioptra antDioptra = new Dioptra()
    for( Order order : lstOrders ){
      for(OrderItem it : order.items){
        Item item = ItemController.findItemsById(it.item.id)
        if( item != null ){
          String indexDioptra = item?.indexDiotra
          if (!StringUtils.trimToNull(indexDioptra).equals(null) && !StringUtils.trimToNull(item?.indexDiotra).equals(null)) {
            Dioptra nuevoDioptra = OrderController.generaDioptra(item?.indexDiotra)
            dioptra = OrderController.validaDioptra(dioptra, nuevoDioptra)
            antDioptra = OrderController.addDioptra(order, OrderController.codigoDioptra(dioptra))
            order?.dioptra = OrderController.codigoDioptra(antDioptra)
          } else {
            order?.dioptra = OrderController.codigoDioptra(antDioptra)
          }
        }
      }
      if( order?.dioptra != null && StringUtils.trimToEmpty(order?.dioptra).contains("null") ){
        order?.dioptra = ""
      }
      OrderController.saveOrder( order )
    }
  }


}
