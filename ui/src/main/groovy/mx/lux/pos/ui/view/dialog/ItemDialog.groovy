package mx.lux.pos.ui.view.dialog

import groovy.swing.SwingBuilder
import mx.lux.pos.ui.controller.ItemController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.model.SurteSwitch
import mx.lux.pos.ui.resources.UI_Standards
import mx.lux.pos.ui.view.panel.OrderPanel
import net.miginfocom.swing.MigLayout
import org.apache.commons.lang.StringUtils

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.util.List

class ItemDialog extends JDialog implements ItemListener{

  private SwingBuilder sb
  private OrderItem tmpOrderItem
  private OrderItem orderItem
  private Order order
  private List<Item> items
  private List<String> colors
  private JSpinner quantity
  private JLabel lblQuatity
  private OrderPanel op
  private JComboBox surte
  private JTextField txtTicket
  private JLabel lblTicket
  private JLabel lblTicketInvalid
  private List<String> surteOption
  private Boolean surteVisible

  private final TAG_REUSO = 'R'
  private final TAG_ID_GEN_TIPO_LC = 'NC'

    ItemDialog( Component parent, Order order, final OrderItem orderItem, Component orderP) {
    op = orderP
    this.orderItem = orderItem
    this.order = order
    sb = new SwingBuilder()
    tmpOrderItem = new OrderItem(
        item: orderItem?.item,
        quantity: orderItem?.quantity
    )
        items = [ ]
        colors = [ ]
        surteOption = []
        surteVisible = OrderController.surteEnabled(orderItem?.tipo.trim())
        if(surteVisible == true){
            surteOption = OrderController.surteOption(orderItem?.tipo.trim(),orderItem?.delivers.trim())
        }
        items.addAll( ItemController.findItems( tmpOrderItem.item?.name ) )
    colors.addAll( items*.color )
    buildUI( parent )
    if( ItemController.esLenteContacto(orderItem.item.id) && TAG_ID_GEN_TIPO_LC.equalsIgnoreCase(StringUtils.trimToEmpty(orderItem.item.genericType))){
      quantity.visible = false
      lblQuatity.visible = false
    }
    doBindings()
  }

  private void buildUI( Component parent ) {
    sb.dialog( this,
        title: "Artículo ${tmpOrderItem.item?.name ?: ''}",
        location: parent.locationOnScreen,
        resizable: true,
        modal: true,
        preferredSize: [ 350, 190 ],
        pack: true,
        layout: new MigLayout( 'wrap 3', '[fill]20[fill]20[]' )
    ) {
      label( 'Artículo' )
      lblQuatity = label( 'Cantidad' )
      label('Surte', visible: surteVisible )
      label( tmpOrderItem.item?.name )
      quantity = spinner( model: spinnerNumberModel( minimum: 1, stepSize: 1, value: tmpOrderItem.quantity ) )
      surte = comboBox( items: surteOption, visible: surteVisible )
      surte.addItemListener( this )
      lblTicket = label( text: 'Ticket:', visible: false, constraints: 'hidemode 3' )
      txtTicket = textField( visible: false, constraints: 'span,hidemode 3' )
      lblTicketInvalid = label( text: 'Ticket o armazon invalido', visible: false, constraints: 'span,hidemode 3', foreground: UI_Standards.WARNING_FOREGROUND )

      panel( layout: new MigLayout( 'right', '[fill,100!]' ), constraints: 'span' ) {
        button( 'Borrar', actionPerformed: doDelete )
        button( 'Aplicar', actionPerformed: doSubmit )
        button( 'Cancelar', defaultButton: true, actionPerformed: {dispose()} )
      }
    }
  }

  private void doBindings( ) {
    sb.build {
      bean( quantity, value: bind( source: tmpOrderItem, sourceProperty: 'quantity', mutual: true ) )
    }
  }

  private def colorChanged = { ItemEvent ev ->
    if ( ev.stateChange == ItemEvent.SELECTED ) {
      tmpOrderItem.item = items.find {
        it?.color?.equalsIgnoreCase( ev.item as String )
      }
    } else {
      tmpOrderItem.item = null
    }
  }

  private def doDelete = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false

    Order o = OrderController.removeOrderItemFromOrder( order.id, orderItem )
      if(orderItem?.tipo.trim().equals('A')){
          op.armazonString = null
      }
    OrderController.removePedidoLc( order.id, orderItem.item.id )
    source.enabled = true
    this.setVisible(false)
  }

    private SurteSwitch surteSu(Item item, SurteSwitch surteSwitch){
        if(surteSwitch?.surteSucursal==false){
            if(item?.type?.trim().equals('A') && item?.stock > 0 ){
                surteSwitch?.surteSucursal=true
            }else{
                AuthorizationDialog authDialog = new AuthorizationDialog(this, "Esta operacion requiere autorizaci\u00f3n")
                authDialog.show()
                println('Autorizado: ' + authDialog.authorized)
                if (authDialog.authorized) {
                    surteSwitch?.surteSucursal=true
                } else {
                    OrderController.notifyAlert('Se requiere autorizacion para esta operacion', 'Se requiere autorizacion para esta operacion')
                }
            }
        }
        return surteSwitch
    }


    private def doSubmit = { ActionEvent ev ->
    JButton source = ev.source as JButton
    source.enabled = false
    println('Seleccionado: '+surte.selectedItem?.toString())
    Boolean ticketValido = OrderController.validReusoTicket( txtTicket.text.trim(), orderItem.item.id )
    String lote = OrderController.findBatchByIdAndArticle( order.id, orderItem.item.id )
    if( StringUtils.trimToEmpty(lote).length() > 0 ){
      String[] lotes = lote.split(",")
      if( lotes.size() > tmpOrderItem.quantity ){
        lote = ""
        for(int i=0;i<tmpOrderItem.quantity;i++){
          lote = lote+","+lotes[i]
        }
        lote = lote.replaceFirst(",","")
      }
    }
    if( txtTicket.visible && ticketValido ){
        OrderController.removeOrderItemFromOrder( order.id, orderItem )
        OrderController.addOrderItemToOrder( order.id, tmpOrderItem, surte.selectedItem?.toString(), lote )
        source.enabled = true
        dispose()
      } else if( !txtTicket.visible ){
        OrderController.removeOrderItemFromOrder( order.id, orderItem )
        OrderController.addOrderItemToOrder( order.id, tmpOrderItem, surte.selectedItem?.toString(), lote )
        source.enabled = true
        dispose()
      } else if( txtTicket.visible && !ticketValido ){
        if(OrderController.ticketToday( StringUtils.trimToEmpty(txtTicket.text) )){
          lblTicketInvalid.text = "El reuso no aplica en ventas del mismo día"
        } else {
          lblTicketInvalid.text = "Ticket o armazon invalido"
        }
        lblTicketInvalid.visible = true
        source.enabled = true
      }
  }

    @Override
    void itemStateChanged(ItemEvent e) {
        if( TAG_REUSO.equalsIgnoreCase(e.item.toString().trim()) ){
          lblTicket.visible = true
          txtTicket.visible = true
          txtTicket.text = ''
        } else {
          lblTicket.visible = false
          txtTicket.visible = false
          lblTicketInvalid.visible = false
        }
    }
}
