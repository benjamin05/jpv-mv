package mx.lux.pos.model

import mx.lux.pos.java.querys.PromocionQuery
import mx.lux.pos.java.repository.PromocionJava
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
    if( StringUtils.trimToEmpty(discountType.text).equalsIgnoreCase("Descuentos CRM") ){
      if( StringUtils.trimToEmpty(discountType.description).length() >= 11 && !StringUtils.trimToEmpty(discountType.description).substring(0,4).isNumber() ){
        String clave = ""
        for(int i=0;i<StringUtils.trimToEmpty(discountType.description).length();i++){
          if(StringUtils.trimToEmpty(discountType.description.charAt(i).toString()).isNumber()){
            Integer number = 0
            try{
              number = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(discountType.description.charAt(i).toString()))
            } catch ( NumberFormatException e ) { println e }
            clave = clave+StringUtils.trimToEmpty((10-number).toString())
          } else {
            clave = clave+0
          }
        }
        String strDiscount = StringUtils.trimToEmpty(clave).substring(3,5)
        Integer percentajeInt = 0
        try{
              percentajeInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(strDiscount))
        } catch ( NumberFormatException e) { e.printStackTrace() }
        Double discountAmount = percentajeInt.doubleValue()*Registry.multiplyDiscountCrm
        amount = order.regularAmount-discountAmount
      } else if( StringUtils.trimToEmpty(discountType.description).length() >= 10 && StringUtils.trimToEmpty(discountType.description).substring(0,4).isNumber() ){
        List<PromocionJava> lstPromo = PromocionQuery.buscaPromocionesCrm( )
        PromocionJava promo = null
        for(PromocionJava p : lstPromo){
          String descPromo = StringUtils.trimToEmpty(p.descripcion.replaceAll(" ",""))
          String descClave = "crm:${StringUtils.trimToEmpty(discountType.description.substring(0,4))}"
          if(descPromo.startsWith(descClave)){
            promo = p
          } else {
            descClave = "CRM:${StringUtils.trimToEmpty(discountType.description.substring(0,4))}"
            if(descPromo.startsWith(descClave)){
              promo = p
            }
          }
        }
        //if( promo == null ){
          amount = promo != null ? order.regularAmount-promo.precioDescontado : BigDecimal.ZERO
        //}
      }
    } else {
      for ( PromotionOrderDetail orderDetail : order.orderDetailSet.values( ) ) {
        amount += orderDetail.finalAmount
      }
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
