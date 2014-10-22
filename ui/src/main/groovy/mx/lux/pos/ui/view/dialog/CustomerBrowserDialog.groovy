package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.model.Customer
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.resources.UI_Standards
import net.miginfocom.swing.MigLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.*
import java.awt.event.MouseEvent
import java.util.List

class CustomerBrowserDialog extends JDialog {

  private static final String TXT_DIALOG_TITLE = 'Buscar cliente'
  private static final String TXT_NAME_LABEL = 'Nombre'
  private static final String TXT_SEARCH_LABEL = 'Buscar'
  private static final String TXT_NEW_LABEL = 'Nuevo'
  private static final String TXT_SELECT_LABEL = 'Seleccionar'
  private static final String TXT_CANCEL_LABEL = 'Cancelar'

  private SwingBuilder sb = new SwingBuilder()
  private Logger logger = LoggerFactory.getLogger( this.getClass() )

  private JTextField txtName
  private JButton btnSelect
  private JTable brCustomer

  private Customer customer
  private List<Customer> customerList = new ArrayList<Customer>()
  private DefaultTableModel customersModel
  private Boolean cancelled
  private Boolean newRequested
  private boolean uiEnabled

  CustomerBrowserDialog( ) {
    this.buildUI()
  }

  // Internal Methods
  protected String toString( Customer pCustomer ) {
    return pCustomer.fullName
  }

  // UI Layout
  protected void buildUI( ) {
    sb.dialog( this,
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 640, 360 ] as Dimension,
        location: [ 70, 35 ] as Point,
        title: TXT_DIALOG_TITLE
    ) {
      borderLayout()
      panel( constraints: BorderLayout.PAGE_START,
      ) {
        borderLayout()
        panel( constraints: BorderLayout.CENTER,
            layout: new MigLayout( 'wrap 2', '[][fill,grow]' ),
        ) {
          label( TXT_NAME_LABEL )
          txtName = textField( document: new UpperCaseDocument() )
        }
        panel( constraints: BorderLayout.PAGE_END ) {
          borderLayout()
          panel( constraints: BorderLayout.LINE_END ) {
            button( TXT_SEARCH_LABEL,
                preferredSize: UI_Standards.BUTTON_SIZE,
                actionPerformed: { onSearch() },
            )
          }
        }
      }
      scrollPane( constraints: BorderLayout.CENTER ) {
        this.brCustomer = table( selectionMode: ListSelectionModel.SINGLE_SELECTION,
            mouseClicked: { MouseEvent e -> onMouseClickedOnCustomer( e ) }
        ) {
          customersModel = tableModel( list: this.customerList ) {
            closureColumn( header: 'Nombre', read: { Customer tmp -> this.toString( tmp ) } )
          } as DefaultTableModel
        }
      }
      panel( constraints: BorderLayout.PAGE_END ) {
        borderLayout()
        panel( constraints: BorderLayout.LINE_START ) {
          button( TXT_NEW_LABEL,
              preferredSize: UI_Standards.BUTTON_SIZE,
              actionPerformed: { onInsertNew() }
          )
        }
        panel( constraints: BorderLayout.LINE_END ) {
          this.btnSelect = button( TXT_SELECT_LABEL,
              preferredSize: UI_Standards.BUTTON_SIZE,
              actionPerformed: { onSelect() }
          )
          button( TXT_CANCEL_LABEL,
              preferredSize: UI_Standards.BUTTON_SIZE,
              actionPerformed: { onCancel() }
          )
        }
      }

    }
  }


  // UI Management
  void disableUI( ) {
    this.uiEnabled = false
  }

  void enableUI( ) {
    this.uiEnabled = true

  }

  void updateUI( ) {
    this.btnSelect.setEnabled( this.brCustomer.selectedRowCount > 0 )
  }

  // Public Methods
  void activate( ) {
    this.cancelled = true
    this.newRequested = false
    this.disableUI()
    this.txtName.text = ''
    this.customerList.clear()
    this.updateUI()
    this.enableUI()
    this.visible = true
  }

  boolean getCancelled( ) {
    return this.cancelled
  }

  Customer getCustomer( ) {
    return this.customer
  }

  Boolean isNewRequested( ) {
    return this.newRequested
  }

  // UI Triggers
  void onCancel( ) {
    if ( this.uiEnabled ) {
      this.disableUI()
      this.cancelled = true
      this.newRequested = false
      this.enableUI()
      this.visible = false
    }
  }

  void onInsertNew( ) {
    if ( this.uiEnabled ) {
      this.disableUI()
      this.cancelled = true
      this.newRequested = true
      this.enableUI()
      this.visible = false
    }
  }

  void onMouseClickedOnCustomer( MouseEvent pEvent ) {
    if ( this.uiEnabled ) {
      this.disableUI()
      if ( pEvent.clickCount > 1 ) {
        sb.doLater {
          this.onSelect()
        }
      }
      this.updateUI()
      this.enableUI()
    }
  }

  void onSearch( ) {
    if ( this.uiEnabled ) {
      this.disableUI()
      this.customerList.clear()
      this.customerList.addAll( CustomerController.requestCustomerBasedOnHint( this.txtName.text ) )
      this.customersModel.fireTableDataChanged()
      this.updateUI()
      this.enableUI()
    }
  }

  void onSelect( ) {
    if ( this.uiEnabled ) {
      this.disableUI()
      this.customer = this.customerList.get( this.brCustomer.selectedRow )
      this.cancelled = false
      this.newRequested = false
      this.enableUI()
      this.visible = false
    }
  }
}