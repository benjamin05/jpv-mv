package mx.lux.pos.model

import mx.lux.pos.service.business.Registry
import org.apache.commons.lang.StringUtils

import java.text.NumberFormat


class PromotionDiscount implements IPromotionAvailable {

  
  private static final String TXT_ALL_ITEMS = "*"
  
  PromotionDiscountType discountType
  String corporateKey
  Double discountPercent
  PromotionOrder order
  
  PromotionDiscount( PromotionDiscountType pDiscountType ) {
    this.setDiscountType( pDiscountType )
    this.corporateKey = ""
    this.discountPercent = 0.0
  }
  
  static PromotionDiscount getDiscountInstance( ) {
    return new PromotionDiscount( PromotionDiscountType.StoreDiscount )
  }

  static PromotionDiscount getCorporateDiscountInstance( ) {
    return new PromotionDiscount( PromotionDiscountType.CorporateDiscount )
  }

    static PromotionDiscount getCouponDiscountInstance(DescuentoClave descuentoClave ) {
        String descTmp = "Descuento Tienda"
        if( descuentoClave != null && descuentoClave.descripcion_descuento.equalsIgnoreCase("NA")){
            descTmp = descuentoClave.porcenaje_descuento+"% "+"descuento sobre venta"
        } else if( descuentoClave != null ){
          if( StringUtils.trimToEmpty(descuentoClave.descripcion_descuento).equalsIgnoreCase("Seguro") ){
            descTmp = "Redencion de Seguro"
          } else {
            descTmp = descuentoClave.descripcion_descuento
          }
        }
        String idTypeTmp = ""
        if( descuentoClave != null ){
          if( (StringUtils.trimToEmpty(descuentoClave.clave_descuento).isNumber() ||
                  StringUtils.trimToEmpty(descuentoClave.clave_descuento).equalsIgnoreCase("PrEdad")) &&
                  (StringUtils.trimToEmpty(descuentoClave.descripcion_descuento).equalsIgnoreCase("Descuento Corporativo") ||
                          StringUtils.trimToEmpty(descuentoClave.descripcion_descuento).equalsIgnoreCase("Promocion Edad")) ||
                  StringUtils.trimToEmpty(descuentoClave.descripcion_descuento).equalsIgnoreCase("Descuentos CRM") ){
            idTypeTmp = "AP"
          }
        }
         String idType = StringUtils.trimToEmpty(idTypeTmp).length() > 0 ? idTypeTmp : 'P'
         String description  = descuentoClave != null ? descuentoClave?.clave_descuento : ""
         String text   = descTmp
        return new PromotionDiscount(  PromotionDiscountType.PromotionDiscount(  idType,  description, text, descuentoClave ))
    }



  
  // Public methods
  void apply( Boolean pApply ) {
    for ( PromotionOrderDetail orderDetail : order.orderDetailSet.values( ) ) {
      orderDetail.orderDiscountPercent = ( pApply ? this.discountPercent : 0.0 )
    }
  }
  
  Boolean getApplied( ) {
    return true
  }
  
  String getDescription( ) {
    return discountType.text
  }
  
  String getPartNbrList( ) {
    return TXT_ALL_ITEMS
  }
  
  Double getBaseAmount( ) {
    Double amount = 0.0
    for ( PromotionOrderDetail orderDetail : order.orderDetailSet.values( ) ) {
      amount += orderDetail.orderDiscountBaseAmount
    }
    return amount
  }
  
  Double getDiscountAmount( ) {
    return this.baseAmount - this.promotionAmount  
  }
  
  Double getPromotionAmount( ) {
    Double amount = 0.0
      for ( PromotionOrderDetail orderDetail : order.orderDetailSet.values( ) ) {
        amount += orderDetail.finalAmount
      }
    return amount
  }
  
  void setCorporateKey( String pCorporateKey ) {
    if ( PromotionDiscountType.CorporateDiscount.equals( this.discountType ) ) {
      this.corporateKey = pCorporateKey
    } else {
      this.corporateKey = ""
    }
  }
  
  // Entity
  int compareTo( IPromotionAvailable pPromotion ) {
    int result = -1
    if ( pPromotion instanceof PromotionDiscount ) {
      result = 0
    }
    return result
  }
  
  String toString( ) {
    String corporateKey = ""
    if ( PromotionDiscountType.CorporateDiscount.equals( this.discountType ) ) {
      corporateKey = String.format( ": %s", this.corporateKey )
    }
    String str = String.format( "[%s%s] BaseAmount:%,.2f  DiscountAmount:%,.2f(%,.1f%%)", 
        this.discountType.toString( ), corporateKey, this.baseAmount, this.discountAmount, 
        ( this.discountPercent * 100.0 ) )
  }
  
}
