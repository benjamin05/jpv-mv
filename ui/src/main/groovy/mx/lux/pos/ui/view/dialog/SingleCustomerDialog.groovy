package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.model.Customer
import mx.lux.pos.ui.model.CustomerType
import mx.lux.pos.ui.model.Rx
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.component.*
import mx.lux.pos.ui.view.panel.RXPanel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import java.awt.*

class SingleCustomerDialog extends JDialog implements IView {

  enum CustomerMode {
    DEFAULT, STATISTICS, FOREIGN
  }

  private static final String TXT_DIALOG_TITLE = 'Información de Cliente'

  private Logger logger = LoggerFactory.getLogger( this.getClass() )
  private SwingBuilder sb = new SwingBuilder()

  CustomerMode currentMode
  JTabbedPane panes
  CustomerPanel custPanel
  CustomerPanel custPanelDefault, custPanelForeign, custPanelStatistics
  RXPanel rxPanel

  Customer customer
  Boolean cancelled
  Boolean rxEnabled

  SingleCustomerDialog(  ) {
    this.currentMode = CustomerMode.DEFAULT
    this.rxEnabled = false
    buildUI( )
  }

  // UI Layout
  protected void buildUI(  ) {
    sb.dialog( this,
        resizable: true,
        pack: true,
        modal: true,
        preferredSize: [ 750, 600 ] as Dimension,
        location: [ 70, 35 ] as Point,
        title: TXT_DIALOG_TITLE
    ) {
      borderLayout()
      this.panes = tabbedPane( constraints: BorderLayout.CENTER )

      panel( constraints: BorderLayout.PAGE_END ) {
        borderLayout()
        panel( constraints: BorderLayout.LINE_END ) {
          button( text: 'Aceptar', preferredSize: UI_Standards.BUTTON_SIZE, actionPerformed: { onButtonOk() } )
          button( text: 'Cancelar', preferredSize: UI_Standards.BUTTON_SIZE, actionPerformed: { onButtonCancel() } )
        }
      }
    }
  }

  // Public methods
  void activate( ) {
    this.panes.removeAll()
    if ( CustomerMode.DEFAULT.equals( this.currentMode ) ) {
      this.custPanel = this.getDefaultCustomerPanel()
    } else if ( CustomerMode.STATISTICS.equals( this.currentMode ) ) {
      this.custPanel = this.getStatisticsCustomerPanel()
    } else if ( CustomerMode.FOREIGN.equals( this.currentMode ) ) {
      this.custPanel = this.getForeignCustomerPanel()
    }
    this.panes.add( this.custPanel )
    if ( this.isRxEnabled() ) {
      this.panes.add( this.getRxPanel() )
    }
    this.disableUI()
    this.custPanel.customer = this.customer
    if ( this.isRxEnabled() ) {
      this.getRxPanel().customer = this.customer
    }
    this.refreshUI()
    this.enableUI()
    this.cancelled = true
    this.visible = true
  }

  Customer getCustomer( ) {
    return null
  }

  void setCustomer( Customer pCustomer ) {
    if ( CustomerType.DOMESTIC.equals( pCustomer.type ) ) {
      this.setCurrentMode( CustomerMode.DEFAULT )
    } else {
      this.setCurrentMode( CustomerMode.FOREIGN )
    }
    this.customer = pCustomer
  }

  Rx getRxSelected( ) {

  }

  void disableRx( ) {
    this.rxEnabled = false
  }

  void enableRx( ) {
    this.rxEnabled = true
  }

  void setCurrentMode( CustomerMode pMode ) {
    this.currentMode = pMode
  }
  // Internal methods
  CustomerPanel getDefaultCustomerPanel() {
    if (this.custPanelDefault == null) {
      this.custPanelDefault = new CustomerPanelDefault()
    }
    return this.custPanelDefault
  }

  CustomerPanel getForeignCustomerPanel() {
    if (this.custPanelForeign == null) {
      this.custPanelForeign = new CustomerPanelForeign()
    }
    return this.custPanelForeign
  }

  CustomerPanel getStatisticsCustomerPanel() {
    if (this.custPanelStatistics == null) {
      this.custPanelStatistics = new CustomerPanelStatistics()
    }
    return this.custPanelStatistics
  }

  RXPanel getRxPanel() {
    if (this.rxPanel == null) {
      this.rxPanel = new RXPanel()
    }
    return this.rxPanel
  }

  Boolean isRxEnabled() {
    return this.rxEnabled
  }

  // View management
  void assign( ) {
    ( this.panes.selectedComponent as IView )?.assign()
  }

  void disableUI( ) {
    for ( Component c : this.getPanes().components) {
      ( c as IView )?.disableUI()
    }
  }

  void enableUI( ) {
    for ( Component c : this.getPanes().components) {
      ( c as IView )?.enableUI()
    }
  }

  void refreshUI( ) {
    for ( Component c : this.getPanes().components) {
      ( c as IView )?.refreshUI()
    }
  }

  Boolean validateInput( ) {
    return ( this.panes.selectedComponent as IView )?.validateInput()
  }

  // UI triggers
  void onButtonCancel( ) {
    this.cancelled = true
    this.visible = false
  }

  void onButtonOk( ) {
    if ( this.validateInput() ) {
      this.assign()
      this.cancelled = false
      this.visible = false
    }
  }

}
