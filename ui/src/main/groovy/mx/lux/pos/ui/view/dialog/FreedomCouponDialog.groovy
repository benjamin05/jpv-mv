package mx.lux.pos.ui.view.dialog

import groovy.model.DefaultTableModel
import groovy.swing.SwingBuilder
import mx.lux.pos.model.IPromotionAvailable
import mx.lux.pos.ui.controller.AccessController
import mx.lux.pos.ui.controller.CancellationController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.OrderToCancell
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.UpperCaseDocument
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.view.renderer.MoneyCellRenderer
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang3.StringUtils

import javax.swing.*
import java.util.List
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class FreedomCouponDialog extends JDialog {

  private SwingBuilder sb
  private String definedMessage
  private JTextField username
  private JPasswordField password
  private JLabel fullName
  private JLabel messages
  private DefaultTableModel tableModel
  private boolean authorized
  private List<OrderToCancell> lstOrders = new ArrayList<>()

    FreedomCouponDialog( Component parent ) {
    sb = new SwingBuilder()
    lstOrders.addAll( OrderController.findOrdersToCancell() )
    buildUI( parent )
  }

  boolean isAuthorized( ) {
    return authorized
  }

  private void buildUI( Component parent ) {
    sb.dialog( this,
        title: "Autorizar Operaci\u00f3n",
        location: parent.locationOnScreen,
        resizable: true,
        modal: true,
        pack: true,
        layout: new MigLayout( 'fill,wrap,center', '[fill]' )
    ) {
      label( definedMessage, font: new Font( '', Font.BOLD, 14 ) )

      panel( layout: new MigLayout( 'fill,wrap ', '[fill,grow]', '[fill,grow]' ) ) {
          scrollPane( border: titledBorder(title: "Notas imcompletas") ) {
              table(selectionMode: ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
                  this.tableModel = tableModel(list: lstOrders) {
                      propertyColumn(header: "Factura", propertyName: "idOrder", maxWidth: 100, editable: false)
                      propertyColumn(header: "Cliente", propertyName: "client", editable: false)
                      propertyColumn(header: "Descuento", propertyName: "discount", maxWidth: 200, editable: false)
                      closureColumn(header: "", type: Boolean, maxWidth: 25,
                              read: { row -> row.selected },
                              write: { row, newValue ->
                                  onTogglePromotion(row)
                              }
                      )
                  } as DefaultTableModel
              }
          }
      }

      panel( layout: new MigLayout( 'right', '[fill,100!]' ) ) {
        button( 'Aceptar', defaultButton: true, actionPerformed: doAuthorize )
        button( 'Cancelar', actionPerformed: {dispose()} )
      }
    }
  }

  private def usernameChanged = { KeyEvent ev ->
    JTextField source = ev.source as JTextField
    sb.doOutside {
      User user = AccessController.getUser( source.text )
      fullName.text = user?.fullName ?: null
    }
    pack()
  }

  protected static void onTogglePromotion(OrderToCancell orderToCancell) {
     orderToCancell.selected = true
  }

  private def doAuthorize = { ActionEvent ev ->
    List<OrderToCancell> lstSelected = new ArrayList<>()
    lstSelected.addAll( tableModel.getRowsModel().value as List<OrderToCancell> )
    for(OrderToCancell orderToCancell : lstSelected ){
      if( orderToCancell.selected != null && orderToCancell.selected ){
        List<Order> lstOrders = CancellationController.findOrderToResetValues(orderToCancell.idOrder)
        for (Order order : lstOrders) {
          CancellationController.resetValuesofCancellation(order.id)
        }
        OrderController.deleteOrder( StringUtils.trimToEmpty(orderToCancell.idOrder) )
        User u = Session.get(SessionItem.USER) as User
        OrderController.addLogOrderCancelled( StringUtils.trimToEmpty(orderToCancell.idOrder), StringUtils.trimToEmpty(u.username) )
      }
    }
    dispose()
  }


}
