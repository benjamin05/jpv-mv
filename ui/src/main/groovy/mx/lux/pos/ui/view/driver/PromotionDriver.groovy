package mx.lux.pos.ui.view.driver

import mx.lux.pos.java.querys.PromocionQuery
import mx.lux.pos.java.repository.PromocionJava
import mx.lux.pos.model.CuponMv
import mx.lux.pos.model.DescuentoClave
import mx.lux.pos.model.IPromotionAvailable
import mx.lux.pos.model.PromotionAvailable
import mx.lux.pos.model.PromotionDiscount
import mx.lux.pos.model.PromotionModel
import mx.lux.pos.java.service.PromotionServiceJava
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.controller.CustomerController
import mx.lux.pos.ui.controller.OrderController
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.Order
import mx.lux.pos.model.Descuento
import mx.lux.pos.service.PromotionService
import mx.lux.pos.service.business.PromotionCommit
import mx.lux.pos.ui.model.ICorporateKeyVerifier
import mx.lux.pos.ui.model.IPromotionDrivenPanel
import mx.lux.pos.ui.model.OrderItem
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.view.dialog.DiscountCouponDialog
import mx.lux.pos.ui.view.dialog.DiscountDialog
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import java.text.NumberFormat

class PromotionDriver implements TableModelListener, ICorporateKeyVerifier {

  private static final String MSG_POST_DISCOUNT_FAILED = "Hubo un error al habilitar descuento.\nNotifique a soporte técnico."
  private static final String MSG_POST_FAILED = "Hubo un error al registrar promociones.\nNotifique a soporte técnico."
  private static final String TXT_POST_DISCOUNT_TITLE = "Aplicacion de Descuentos"
  private static final String TXT_POST_TITLE = "Registro de Promociones"
  private static final String TAG_TIPO_DESCUENTO_CUPON = "M"
  private static final String TAG_TIPO_DESCUENTO = 'DESCUENTO CUPON'
  private static final String TAG_TIPO_DESCUENTO_LC = 'DESCUENTO CUPON LC'

  private static final Logger log = LoggerFactory.getLogger( PromotionDriver.class )

  private static PromotionDriver instance

  IPromotionDrivenPanel view
  PromotionModel promotionModel
  DiscountDialog dlgDiscount
  Boolean itemsTableEventsEnabled = true

  private PromotionDriver( ) {  }

  // Internal methods
  protected void setDlgDiscount( DiscountDialog pDialog ) {
    this.dlgDiscount = pDialog
  }

  protected void setItemsTableEventsEnabled( Boolean pEnabled ) {
    this.itemsTableEventsEnabled = pEnabled
  }

    public void updatePromotionList( ) {
    view.promotionList.clear()
    view.promotionList.addAll( this.model.listAvailablePromotions() )
    if ( this.model.hasOrderDiscountApplied() ) {
      view.promotionList.add( this.model.orderDiscount )
      Collections.sort( view.promotionList )
    }
    view.promotionModel.fireTableDataChanged()
  }

    protected void updatePromotionListDiscount( ) {
        view.promotionList.clear()
        view.promotionList.addAll( this.model.listAvailablePromotions() )
        if ( this.model.hasOrderDiscountApplied() ) {
            view.promotionList.add( this.model.orderDiscount )
            Collections.sort( view.promotionList )
        }
        //view.promotionModel.fireTableDataChanged()
    }

  // Properties
  static PromotionDriver getInstance() {
    if (instance == null) {
      instance = new PromotionDriver()
    }
    return instance
  }

  void enableItemsTableEvents( Boolean pEnabled ) {
    this.setItemsTableEventsEnabled( pEnabled )
  }

  PromotionModel getModel( ) {
    return this.promotionModel
  }

  PromotionService getService( ) {
    return ServiceManager.promotionService
  }

  PromotionServiceJava getServiceJava( ) {
    return ServiceManager.promotionServiceJava
  }

  Boolean isDiscountEnabled( ) {
    boolean enabled = ( ( !this.model.isAnyApplied() )
        && ( !this.model.hasOrderDiscountApplied() )
        && ( view.order.total.compareTo( BigDecimal.ZERO ) > 0 )
    )
    return enabled
  }

  Boolean isCorporateDiscountEnabled( ) {
    boolean enabled = ( ( !this.model.hasOrderDiscountApplied() )
        && ( view.order.total.compareTo( BigDecimal.ZERO ) > 0 )
    )
    return enabled
  }

  void init( IPromotionDrivenPanel pOrderPanel ) {
    this.view = pOrderPanel
    this.promotionModel = new PromotionModel()
  }

  // Commands
  void requestApplyPromotion( IPromotionAvailable pPromotion ) {
    log.debug( String.format( "Apply Promotion: %s", pPromotion.toString() ) )
    if ( pPromotion instanceof PromotionAvailable ) {
      service.requestApplyPromotion( this.model, pPromotion )
      this.updatePromotionList()
      view.refreshData()
    }
  }


    void getApplyPromotion( IPromotionAvailable pPromotion ) {
        log.debug( String.format( "Apply Promotion: %s", pPromotion.toString() ) )
        if ( pPromotion instanceof PromotionAvailable ) {
            service.getApplyPromotion( this.model, pPromotion )
            this.updatePromotionListDiscount()
            //view.refreshData()
        }
    }

  void requestCancelPromotion( IPromotionAvailable pPromotion ) {
    log.debug( String.format( "Cancel Promotion: %s", pPromotion.toString() ) )
    if ( pPromotion instanceof PromotionAvailable ) {
      serviceJava.requestCancelPromotion( this.model, pPromotion )
      this.updatePromotionList()
      view.refreshData()
    }

    if( pPromotion instanceof PromotionDiscount ){
        serviceJava.requestCancelPromotionDiscount( this.model, pPromotion )
        this.updatePromotionList()
        view.refreshData()
    }
  }


  void requestCancelPromotionJava( IPromotionAvailable pPromotion ) {
    log.debug( String.format( "Cancel Promotion: %s", pPromotion.toString() ) )
    if ( pPromotion instanceof PromotionAvailable ) {
      service.requestCancelPromotion( this.model, pPromotion )
      this.updatePromotionList()
      view.refreshData()
    }
    if( pPromotion instanceof PromotionDiscount ){
            service.requestCancelPromotionDiscount( this.model, pPromotion )
            this.updatePromotionList()
            view.refreshData()
    }
  }


  void requestDiscount( ) {
    log.debug( "Discount Selected" )
    DiscountDialog dlgDiscount = new DiscountDialog( false )
    dlgDiscount.setOrderTotal( view.order.total )
    dlgDiscount.setMaximumDiscount( service.requestTopStoreDiscount() )
    dlgDiscount.activate()
    if ( dlgDiscount.getDiscountSelected() ) {
      log.debug( String.format( "Discount Selected: %,.2f (%,.1f%%)", dlgDiscount.getDiscountAmt(),
          dlgDiscount.getDiscountPct() )
      )
      Double discount = dlgDiscount.getDiscountAmt() / view.order.total
      if ( service.requestOrderDiscount( this.model, "", discount ) ) {
        log.debug( this.model.orderDiscount.toString() )
        this.updatePromotionList()
        view.refreshData()
      } else {
        JOptionPane.showMessageDialog( view as JComponent, MSG_POST_DISCOUNT_FAILED, TXT_POST_DISCOUNT_TITLE,
            JOptionPane.ERROR_MESSAGE
        )
      }
    }
  }


  void requestCorporateDiscount( ) {
    log.debug( "Corporate Discount Selected" )
    DiscountDialog dlgDiscount = new DiscountDialog( true )
    BigDecimal total = BigDecimal.ZERO
    for(OrderItem oi : view.order.items){
      if( !Registry.genericsWithoutDiscount.contains(StringUtils.trimToEmpty(oi.item.type))  ){
        total = total.add( oi.item.price.multiply(oi.quantity) )
      }
    }
    dlgDiscount.setOrderTotal( total )
    dlgDiscount.setVerifier( this )
    dlgDiscount.activate()
    if ( dlgDiscount.getDiscountSelected() ) {
      log.debug( String.format( "Corporate Discount Selected: %,.2f (%,.1f%%)", dlgDiscount.getDiscountAmt(),
          dlgDiscount.getDiscountPct() ) )
      Double discount = dlgDiscount.getDiscountAmt() / total
      if ( service.requestOrderDiscount( this.model, dlgDiscount.corporateKey, discount ) ) {
        log.debug( this.model.orderDiscount.toString() )
        this.updatePromotionList()
        view.refreshData()
      } else {
        JOptionPane.showMessageDialog( view as JComponent, MSG_POST_DISCOUNT_FAILED, TXT_POST_DISCOUNT_TITLE,
            JOptionPane.ERROR_MESSAGE
        )
      }
    }
  }

  void requestCouponDiscount( String title ){
    if( CustomerController.validCustomerApplyCoupon( view.order.customer.id ) ){
      Item item = null
      BigDecimal total = BigDecimal.ZERO
      for(OrderItem tmp : view.order.items){
        if( StringUtils.trimToEmpty(tmp.item.type).equalsIgnoreCase("A") ){
          item = tmp.item
        }
        if( !Registry.genericsWithoutDiscount.contains(StringUtils.trimToEmpty(tmp.item.type)) ){
          total = total.add(tmp.item.price.multiply(tmp.quantity))
        }
      }
      if( StringUtils.trimToEmpty(title).equalsIgnoreCase("seguro") ){
        if( item == null ){
          item = new Item()
        }
        item.price = total
      }
      DiscountCouponDialog couponDiscount = new DiscountCouponDialog(true,view.order.id, item, title )
      couponDiscount.setOrderTotal( total )
      couponDiscount.setVerifier( this )
      couponDiscount.activate()
      if ( couponDiscount.getDiscountSelected() ) {
          Double discountAmount = 0.00
          if( StringUtils.trimToEmpty(couponDiscount.title).equalsIgnoreCase("CRM") ){
            total = couponDiscount.orderTotal
          }
          if(couponDiscount.getDiscountAmt() > new Double(total)){
            discountAmount = new Double(total)
          } else {
            discountAmount = couponDiscount.getDiscountAmt()
          }
          Double discount = discountAmount / total
          Boolean apl = false
          apl = model.setupOrderCouponDiscount(couponDiscount?.descuentoClave,discount )
          PromotionCommit.writeOrder( model )
          if ( apl  ) {
              this.updatePromotionList()
              view.refreshData()
          } else {
              JOptionPane.showMessageDialog( view as JComponent, MSG_POST_DISCOUNT_FAILED, TXT_POST_DISCOUNT_TITLE,
                      JOptionPane.ERROR_MESSAGE
              )
          }
      }
    } else {
        JOptionPane.showMessageDialog( view as JComponent, "Cliente invalido, dar de alta datos", "Cliente Invalido",
                JOptionPane.ERROR_MESSAGE
        )
    }
  }


  void addCouponDiscount( Order order, BigDecimal discountAmt, String idFirstOrder, BigDecimal montoCupon ){
    Double discountAmount = 0.00
    DescuentoClave descuentoClave = new DescuentoClave()
    descuentoClave.clave_descuento = ""//claveAleatoria(Registry.currentSite, discountAmt.intValue())
    descuentoClave.porcenaje_descuento = discountAmt.doubleValue()
    descuentoClave.tipo = TAG_TIPO_DESCUENTO_CUPON
    descuentoClave.descripcion_descuento = "Descuento Cupon"
    descuentoClave.vigente = true
    BigDecimal total = BigDecimal.ZERO
    for(OrderItem det : order.items){
      if( !Registry.genericsWithoutDiscount.contains(StringUtils.trimToEmpty(det.item.type)) ){
        total = total.add(det.item.price.multiply(det.quantity))
      }
    }
    if(discountAmt > new Double(total)){
          discountAmount = new Double(total)
      } else {
          discountAmount = discountAmt
      }
      Double discount = discountAmount / total
      Boolean apl = false
      model.loadOrder( OrderController.findOrderByidOrder( StringUtils.trimToEmpty(order.id) ) )
      apl = model.setupOrderCouponDiscount(descuentoClave,discount )
      PromotionCommit.writeOrder( model )
      if ( apl ) {
          CuponMv cuponMv = ServiceManager.orderService.obtenerCuponMvFuente( idFirstOrder )
        if( cuponMv != null ){
          cuponMv.fechaAplicacion = new Date()
          cuponMv.facturaDestino = order.id
        } else {
          cuponMv = new CuponMv()
          cuponMv.claveDescuento = ""//descuentoClave.clave_descuento
          cuponMv.facturaDestino = order.id
          cuponMv.facturaOrigen = idFirstOrder
          cuponMv.fechaAplicacion = new Date()
          cuponMv.montoCupon = montoCupon
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(new Date());
          calendar.add(Calendar.DAY_OF_YEAR, Registry.diasVigenciaCupon)
          cuponMv.fechaVigencia = calendar.getTime()
        }
        OrderController.saveCuponMv( cuponMv )
      } else {
          println "No se pudo insertar el cupon en la nota ${order.id}"
    }
  }


  void addPromoDiscountAge( Order order, BigDecimal discountAmt ){
    Double discountAmount = 0.00
    DescuentoClave descuentoClave = new DescuentoClave()
    descuentoClave.clave_descuento = "PrEdad"
    descuentoClave.porcenaje_descuento = discountAmt.doubleValue()
    descuentoClave.tipo = "AP"
    descuentoClave.descripcion_descuento = "Promocion Edad"
    descuentoClave.vigente = true
    BigDecimal total = BigDecimal.ZERO
    for(OrderItem det : order.items){
      if( !Registry.genericsWithoutDiscount.contains(StringUtils.trimToEmpty(det.item.type)) ){
        total = total.add(det.item.price.multiply(det.quantity))
      }
    }
    if(discountAmt > new Double(total)){
      discountAmount = new Double(total)
    } else {
      discountAmount = discountAmt
    }
    Double discount = discountAmount / total
    Boolean apl = false
    model.loadOrder( OrderController.findOrderByidOrder( StringUtils.trimToEmpty(order.id) ) )
    apl = model.setupOrderCouponDiscount(descuentoClave,discount )
    if( StringUtils.trimToEmpty(model?.orderDiscount?.discountType?.description).equalsIgnoreCase("PREDAD") ){
      model.orderDiscount.corporateKey = "PREDAD"
    }
    PromotionCommit.writeOrder( model )
    if ( apl ) {
      this.updatePromotionList()
      view.refreshData()
    } else {
      println "No se pudo insertar el cupon en la nota ${order.id}"
    }
  }


    void addCouponDiscountTransf( Order order, BigDecimal discountAmt, String clave, BigDecimal montoCupon ){
        /*Double discountAmount = 0.00
        DescuentoClave descuentoClave = new DescuentoClave()
        descuentoClave.clave_descuento = clave
        descuentoClave.porcenaje_descuento = discountAmt.doubleValue()
        descuentoClave.tipo = TAG_TIPO_DESCUENTO_CUPON
        descuentoClave.descripcion_descuento = "Descuento Cupon"
        descuentoClave.vigente = true
        if(discountAmt > new Double(order.total)){
            discountAmount = new Double(order.total)
        } else {
            discountAmount = discountAmt
        }
        Double discount = discountAmount / order.total
        Boolean apl = false
        model.loadOrder( OrderController.findOrderByidOrder( StringUtils.trimToEmpty(order.id) ) )
        apl = model.setupOrderCouponDiscount(descuentoClave,discount )
        PromotionCommit.writeOrder( model )
        if ( apl ) {*/
        CuponMv cuponMv = ServiceManager.orderService.obtenerCuponMvClave( clave )
        if( cuponMv != null ){
          cuponMv.fechaAplicacion = null
          cuponMv.facturaDestino = ""
          cuponMv.fechaVigencia = new Date()
        }
        OrderController.saveCuponMv( cuponMv )
        String titulo = cuponMv.claveDescuento.startsWith("8") ? "CUPON SEGUNDO PAR" : "CUPON TERCER PAR"
        OrderController.printCuponTicket( cuponMv, titulo, cuponMv.montoCupon )
        /*} else {
            println "No se pudo insertar el cupon en la nota ${order.id}"
        }*/
    }


  void requestPromotionSave(String idNotaVenta, Boolean saveOrder) {
    log.debug( "Request promotion persist" )
    service.saveTipoDescuento(idNotaVenta,this.model?.orderDiscount?.discountType?.idType)
    service.requestPersist( this.model, saveOrder )
  }

  void requestPromotionJavaSave(String idNotaVenta, Boolean saveOrder) {
    log.debug( "Request promotion persist" )
    serviceJava.saveTipoDescuento(idNotaVenta,this.model?.orderDiscount?.discountType?.idType)
    serviceJava.requestPersist( this.model, saveOrder )
  }

  Boolean requestVerify( String pCorporateKey, Double pDiscountPct ) {
    log.debug( String.format( "RequestVerify(%s, %.1f%%)", pCorporateKey, pDiscountPct ) )
    return this.service.requestVerify( pCorporateKey, pDiscountPct )
  }

  // Listening for events
  void tableChanged( TableModelEvent pEvent ) {
    if ( itemsTableEventsEnabled ) {
      log.debug( "Driver notified of TableModel changes" )
      log.debug( String.format( "Order: %s", view.order.id ) )
      String orderNbr = ""
      if ( view.order != null ) {
        orderNbr = StringUtils.trimToEmpty( view.order.id )
      }
      service.updateOrder( this.model, orderNbr )
      log.debug( this.model.availablePromotionList.toString() )
      SwingUtilities.invokeLater( new Runnable() {
        void run( ) {
          PromotionDriver.this.updatePromotionList()
          view.refreshData()
        }
      } )
    }
  }


  void updatePromotionClient( Order order ){
    Descuento desc = OrderController.findDiscount( order )
    if( desc != null && desc.id != null ){
      BigDecimal ventaTotal = BigDecimal.ZERO
      String genericoNoApplica = StringUtils.trimToEmpty(Registry.genericsWithoutDiscount)
      String generic = ""
      Boolean crm = false
      Boolean allGen = false
      Boolean oneValGen = false
      Boolean oneNotValGen = false
      Boolean twoValGen = false
      if( StringUtils.trimToEmpty(desc.clave).length() == 11 && (desc.clave.replace("*","\\*").contains("*") || desc.clave.replace("!","\\!").contains("\\!") ||
              desc.clave.replace("_","\\_").contains("\\_") || StringUtils.trimToEmpty(desc.idTipoD).equalsIgnoreCase("AP") ) &&
              !StringUtils.trimToEmpty(desc.clave).substring(0,4).isNumber() ){
        crm = true
        generic = StringUtils.trimToEmpty(desc.clave).substring(1,3)
        if( generic.contains("**") ){
          allGen = true
        } else if( generic.contains("_") ){
          oneValGen = true
        } else if( generic.replace("!","\\!").contains("\\!") ){
          oneNotValGen = true
        } else {
          twoValGen = true
        }
      } else if(StringUtils.trimToEmpty(desc.clave).length() >= 10 && StringUtils.trimToEmpty(desc.idTipoD).equalsIgnoreCase("AP") &&
              StringUtils.trimToEmpty(desc.clave).substring(0,4).isNumber()){
        crm = true
        List<PromocionJava> lstPromo = PromocionQuery.buscaPromocionesCrm( )
        PromocionJava promo = null
        for(PromocionJava p : lstPromo){
          String descPromo = StringUtils.trimToEmpty(p.descripcion.replaceAll(" ",""))
          String descClave = "crm:${StringUtils.trimToEmpty(desc.clave.substring(0,4))}"
          if(descPromo.startsWith(descClave)){
            promo = p
          } else {
            descClave = "CRM:${StringUtils.trimToEmpty(desc.clave.substring(0,4))}"
            if(descPromo.startsWith(descClave)){
              promo = p
            }
          }
        }
        if( promo != null ){
          generic = StringUtils.trimToEmpty(promo.idGenerico)
          if( generic.contains("*") ){
            allGen = true
          } else if( StringUtils.trimToEmpty(promo.genericoc).length() <= 0 ){
            oneValGen = true
            generic = "_"+generic
          } else if( StringUtils.trimToEmpty(promo.genericoc).length() > 0 ){
            twoValGen = true
            generic = generic+StringUtils.trimToEmpty(promo.genericoc)
          }
        }
      }
      for(OrderItem oi : order.items){
        if( !genericoNoApplica.equalsIgnoreCase(StringUtils.trimToEmpty(oi.item.type)) ){
          if( crm ){
            if( allGen ){
              ventaTotal = ventaTotal.add(oi.item.price.multiply(oi.quantity))
            } else if( oneValGen ) {
              //if( StringUtils.trimToEmpty(oi.item.type).equalsIgnoreCase(generic.substring(1)) ){
                ventaTotal = ventaTotal.add(oi.item.price.multiply(oi.quantity))
              //}
            } else if( twoValGen ) {
              /*if( StringUtils.trimToEmpty(oi.item.type).equalsIgnoreCase(generic.substring(0,1)) ||
                      StringUtils.trimToEmpty(oi.item.type).equalsIgnoreCase(generic.substring(1)) ){*/
                ventaTotal = ventaTotal.add(oi.item.price.multiply(oi.quantity))
              //}
            } else if( oneNotValGen ){
              //if( !StringUtils.trimToEmpty(oi.item.type).equalsIgnoreCase(generic.substring(1)) ){
                ventaTotal = ventaTotal.add(oi.item.price.multiply(oi.quantity))
              //}
            }
          } else {
            ventaTotal = ventaTotal.add(oi.item.price.multiply(oi.quantity))
          }
        }
      }
      Double discount = desc.getNotaVenta().getMontoDescuento() / (ventaTotal+desc.getNotaVenta().getMontoDescuento())
      Boolean apl = false
      DescuentoClave descuentoClave = null
      if( desc?.descuentosClave != null ){
        descuentoClave = desc?.descuentosClave
      } else {
        String descripcionDesc = StringUtils.trimToEmpty(desc?.clave).startsWith("H") ? TAG_TIPO_DESCUENTO_LC : TAG_TIPO_DESCUENTO
        if(StringUtils.trimToEmpty(desc?.clave).length() > 0 && StringUtils.trimToEmpty(desc?.clave).isNumber()){
          descripcionDesc = "Descuento Corporativo"
        } else if(StringUtils.trimToEmpty(desc?.clave).length() <= 0) {
          descripcionDesc = "Descuento Tienda"
        } else if((StringUtils.trimToEmpty(desc?.clave).length() == 11 || StringUtils.trimToEmpty(desc?.clave).length() == 10) && StringUtils.trimToEmpty(desc?.idTipoD).equalsIgnoreCase("AP")) {
            descripcionDesc = "Descuentos CRM"
        } else if((StringUtils.trimToEmpty(desc?.clave).startsWith("L") || StringUtils.trimToEmpty(desc?.clave).startsWith("N") ||
                StringUtils.trimToEmpty(desc?.clave).startsWith("S")) &&
                (StringUtils.trimToEmpty(desc?.clave).length() == 10 || StringUtils.trimToEmpty(desc?.clave).length() == 11)) {
            descripcionDesc = "Redencion de Seguro"
        }
        descuentoClave = new DescuentoClave()
        descuentoClave.clave_descuento = desc?.clave
        descuentoClave.porcenaje_descuento = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(desc?.porcentaje)).doubleValue()
        descuentoClave.descripcion_descuento = descripcionDesc
        descuentoClave.tipo = TAG_TIPO_DESCUENTO_CUPON
        descuentoClave.vigente = true
      }
      apl = model.setupOrderCouponDiscount(descuentoClave,discount )
      PromotionCommit.writeOrder( model )
      String clave = ""
      if( StringUtils.trimToEmpty(desc?.clave).length() > 0 && StringUtils.trimToEmpty(desc?.clave).isNumber() ){
        clave = StringUtils.trimToEmpty(desc?.clave)
      }
      if ( clave.trim().length() > 0 ) {
        if( service.recoverOrderDiscount( this.model, clave, discount ) ){
          log.debug( this.model.orderDiscount.toString() )
          this.updatePromotionList()
          view.refreshData()
        }
      } /*else if ( service.recoverOrderDiscount( this.model, desc?.clave, discount ) ) {
        log.debug( this.model.orderDiscount.toString() )
        this.updatePromotionList()
        view.refreshData()
      }*/ else if ( apl  ) {
        this.updatePromotionList()
        view.refreshData()
      }
    }
  }


    static String claveAleatoria(String factura) {
        String digitos = "" + "00"
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (factura.size() < 4) {
            factura = factura?.padLeft( 4, '0' )
        } else {
            factura = factura.substring(0,4);
        }
        String resultado = digitos?.padLeft( 3, '0' ) + factura
        for (int i = 0; i < resultado.size(); i++) {
            int numAleatorio = (int) (Math.random() * abc.size());
            if (resultado.charAt(i) == '0') {
                resultado = replaceCharAt(resultado, i, abc.charAt(numAleatorio))
            } else {
                int numero = Integer.parseInt ("" + resultado.charAt(i));
                numero = 10 - numero
                char diff = Character.forDigit(numero, 10);
                resultado = replaceCharAt(resultado, i, diff)
            }
        }
        return resultado;
    }

    static String replaceCharAt(String s, int pos, char c) {
        StringBuffer buf = new StringBuffer( s );
        buf.setCharAt( pos, c );
        return buf.toString( );
    }
}
