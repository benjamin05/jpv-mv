package mx.lux.pos.ui.view.component

import groovy.swing.SwingBuilder
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Payment
import mx.lux.pos.ui.view.driver.PromotionDriver
import mx.lux.pos.ui.view.panel.OrderPanel

import javax.swing.*
import java.awt.event.MouseEvent

class DiscountContextMenu extends JPopupMenu {

  private SwingBuilder sb = new SwingBuilder( )
  private PromotionDriver driver
  private JMenuItem menuDiscount
  private JMenuItem menuCorporateDiscount
  private JMenuItem menuCouponDiscount
  private JMenuItem menuCrmDiscount
  private OrderPanel orderPanel

  
  DiscountContextMenu( PromotionDriver pDriver ) {
    driver = pDriver
    buildUI( )
  }
  
  protected buildUI( ) {
    sb.popupMenu( this ) {
      menuDiscount = menuItem( text: "Descuento Tienda",
      visible: Registry.activeStoreDiscount,
      actionPerformed: { onDiscountSelected( ) },
      )
      menuCorporateDiscount = menuItem( text: "Descuento Corporativo", 
        visible: true,
        actionPerformed: { onCorporateDiscountSelected( ) },
      )
      menuCouponDiscount = menuItem( text: "Descuento por Cupon",
        visible: true,
        actionPerformed: { onCouponDiscountSelected( ) },
      )
      menuCrmDiscount = menuItem( text: "Descuento CRM",
        visible: true,
        actionPerformed: { onCrmDiscountSelected( ) },
      )
      menuCouponDiscount = menuItem( text: "Seguro",
        visible: true,
        actionPerformed: { onWarrantyDiscountSelected( ) },
      )
    }
  }
  
  // Public Methods
  void activate( MouseEvent pEvent, OrderPanel panel ) {
    menuDiscount.setEnabled( driver.isDiscountEnabled( ) )
    menuCorporateDiscount.setEnabled( driver.isDiscountEnabled( ) )
    menuCouponDiscount.setEnabled( driver.isDiscountEnabled( ) )
    menuCrmDiscount.setVisible( Registry.getCrmActive() )
    //menuCorporateDiscount.setEnabled( driver.isCorporateDiscountEnabled( ) )
    //menuCouponDiscount.setEnabled(driver.isCorporateDiscountEnabled())
    show( pEvent.getComponent(), pEvent.getX(), pEvent.getY() )
    orderPanel = panel
  } 
  
  // UI Response
  protected void onDiscountSelected( ) {
    driver.requestDiscount( )
    for(Payment payment : driver.view.order.payments){
      OrderController.removePaymentFromOrder( driver.view.order.id, payment )
    }
    OrderController.saveOrder( driver.view.order )
    orderPanel.updateOrder( driver.view.order.id )
  }
  
  protected void onCorporateDiscountSelected( ) {
    driver.requestCorporateDiscount( )
    for(Payment payment : driver.view.order.payments){
      OrderController.removePaymentFromOrder( driver.view.order.id, payment )
    }
    OrderController.saveOrder( driver.view.order )
    orderPanel.updateOrder( driver.view.order.id )
  }

  protected void onCouponDiscountSelected(){
      driver.requestCouponDiscount( "Descuento por Cupon" )
      for(Payment payment : driver.view.order.payments){
          OrderController.removePaymentFromOrder( driver.view.order.id, payment )
      }
      OrderController.saveOrder( driver.view.order )
      orderPanel.updateOrder( driver.view.order.id )
  }



  protected void onWarrantyDiscountSelected(){
    driver.requestCouponDiscount( "Seguro" )
    for(Payment payment : driver.view.order.payments){
            OrderController.removePaymentFromOrder( driver.view.order.id, payment )
    }
    OrderController.saveOrder( driver.view.order )
    orderPanel.updateOrder( driver.view.order.id )
  }



  protected void onCrmDiscountSelected(){
    driver.requestCouponDiscount( "CRM" )
    for(Payment payment : driver.view.order.payments){
      OrderController.removePaymentFromOrder( driver.view.order.id, payment )
    }
    OrderController.saveOrder( driver.view.order )
    orderPanel.updateOrder( driver.view.order.id )
  }


}
