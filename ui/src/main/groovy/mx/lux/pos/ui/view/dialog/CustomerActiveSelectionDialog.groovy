package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.model.ClienteProceso
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.resources.UI_Standards
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import javax.swing.table.TableRowSorter
import java.awt.*
import java.awt.event.MouseEvent
import java.util.List;

class CustomerActiveSelectionDialog extends JDialog {

  private static String TXT_DIALOG_TITLE = 'Seleccionar Cliente en Proceso'
  private static String TXT_INSTRUCTIONS = '%d clientes activos en Proceso.'
  private static String TXT_CUST_NAME_LABEL = 'Cliente'
  private static String TXT_STAGE_LABEL = 'Etapa'
  private static String TXT_ORDER_COUNT_LABEL = 'Notas'

  private SwingBuilder sb = new SwingBuilder()
  private Logger logger = LoggerFactory.getLogger( this.getClass() )

  private Collection<ClienteProceso> customerList
  private ClienteProceso selection
  private JLabel lblInstructions
  private JTable tClientes
  private DefaultTableModel model
  private Boolean requestNew
  private JTextField search

  private Integer idCliente
  private Integer idSucursal


  CustomerActiveSelectionDialog( ) {
    this.customerList = new ArrayList<ClienteProceso>()
    this.buildUI()
  }

  // Dialog Layout
  protected void buildUI( ) {
    sb.dialog( this,
        title: TXT_DIALOG_TITLE,
        location: [ 100, 150 ] as Point,
        preferredSize: [ 480, 320 ] as Dimension,
        resizable: false,
        modal: true,
        pack: true,
    ) {
      borderLayout()
      panel(border: BorderFactory.createEmptyBorder( 10, 10, 5, 10 ),
          constraints: BorderLayout.CENTER) {
        borderLayout()

        panel( constraints: BorderLayout.PAGE_START,
            border: BorderFactory.createEmptyBorder( 10, 10, 5, 10 )
        ) {
          borderLayout()

                label( text: 'Cliente:', constraints: BorderLayout.LINE_START )
                search =  textField(name: 'TxArea',
                        border:BorderFactory.createLineBorder(Color.gray),
                        preferredSize: [ 30, 20 ],
                        keyReleased:{onAlter(this.model,this.tClientes, this.search)})

          lblInstructions = label( text: TXT_INSTRUCTIONS, constraints: BorderLayout.AFTER_LAST_LINE )
        }
        scrollPane( constraints: BorderLayout.CENTER ) {
          tClientes = table( selectionMode: ListSelectionModel.SINGLE_SELECTION,
              mouseClicked: onCustomerClick ) {
            model = tableModel( list: customerList ) {
              closureColumn( header: TXT_CUST_NAME_LABEL,
                  minWidth: 240,
                  read: { ClienteProceso c -> c.cliente.nombreCompleto }
              )
              closureColumn( header: TXT_STAGE_LABEL,
                  preferredWidth: 100,
                  read: { ClienteProceso c -> c.etapa }
              )
              closureColumn( header: TXT_ORDER_COUNT_LABEL,
                  maxWidth: 80,
                  read: { ClienteProceso c -> c.notaVentas.size() }
              )
            } as DefaultTableModel
          }
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {

            button( 'No Venta', preferredSize: UI_Standards.BUTTON_SIZE, actionPerformed: { onNoSale() } )
            button( 'Nuevo', preferredSize: UI_Standards.BUTTON_SIZE, actionPerformed: { onNew() } )
            button( 'Aceptar', preferredSize: UI_Standards.BUTTON_SIZE, actionPerformed: { onSelection() } )
            button( 'Cancelar', preferredSize: UI_Standards.BUTTON_SIZE, actionPerformed: { onCancel() } )
          }
        }
      }
    }
  }

  // Internal Methods
  protected Comparator<ClienteProceso> getSorter( ) {
    Comparator<ClienteProceso> sorter = new Comparator<ClienteProceso>() {
      int compare( ClienteProceso cust1, ClienteProceso cust2 ) {
        return cust1.cliente.nombreCompleto.compareToIgnoreCase( cust2.cliente.nombreCompleto )
      }
    }
    return sorter
  }

  // UI Management
  protected void updateUI( ) {
    this.model.fireTableDataChanged()
    this.lblInstructions.text = String.format( TXT_INSTRUCTIONS, ( customerList != null ? customerList.size() : 0 ) )
  }

  // Public methods
  void activate( ) {
    this.updateUI()
    this.selection = null
    requestNew = false
    this.setVisible( true )
  }

  ClienteProceso getCustomerSelected( ) {
      return selection
  }

  Boolean isNewRequested() {
    return requestNew
  }

  void setCustomerList( List<ClienteProceso> pCustomerList ) {
    this.customerList.clear()
    Collections.sort( pCustomerList, this.getSorter() )
    this.customerList.addAll( pCustomerList )
  }

  // Triggers
  private def onCustomerClick = { MouseEvent ev ->
    if ( SwingUtilities.isLeftMouseButton( ev ) ) {
      ClienteProceso cliente = ev.source.selectedElement
      if ( ev.clickCount >= 2 && cliente?.idCliente ) {
        idCliente = cliente.idCliente
        idSucursal = cliente.idSucursal
        onSelection()
      } else {
        idCliente = cliente.idCliente
        idSucursal = cliente.idSucursal
      }
    }
  }

  protected void onCancel( ) {
    selection = null
    this.setVisible( false )
  }

  protected void onAlter(DefaultTableModel model, JTable tClientes,JTextField search){
      TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model)
      tClientes.setRowSorter(sorter)
      if (search.text.length() == 0) {
          sorter.setRowFilter(null)
      } else {
          sorter.setRowFilter(RowFilter.regexFilter(search.text.toUpperCase()))
      }
  }

  protected void onNew( ) {
    requestNew = true
    selection = null
    this.setVisible( false )
  }

  protected void onNoSale( ){
    if( !model.getColumnModel().selectionModel.selectionEmpty ){
      Map<String, String> selected = model.rowModel.getValue().getProperties()
      ClienteProceso cliente = CustomerController.findProccesClient( selected.get('idCliente') )
      NoSaleDialog noSale = new NoSaleDialog( this, cliente.idCliente, cliente.idSucursal, true )
      noSale.show()
      dispose()
    } else {
        sb.optionPane(message: "Seleccione un Cliente", optionType: JOptionPane.DEFAULT_OPTION)
                .createDialog(new JTextField(), "Alerta")
                .show()
    }
  }

  protected void onSelection( ) {
    int index = tClientes.convertRowIndexToModel(tClientes.getSelectedRow())
    if (tClientes.selectedRowCount > 0) {
      this.logger.debug( String.format('Selected Row:%d', index) )
      selection = this.customerList.getAt( index)
      this.setVisible( false )
    } else {
      this.logger.debug( 'No Row Selected' )
      sb.doLater {
        this.onCancel()
      }
    }
  }

}
