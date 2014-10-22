package mx.lux.pos.service.business

import mx.lux.pos.model.*
import mx.lux.pos.repository.impl.RepositoryFactory
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils

class PromotionEngine {

  // Singleton
  static PromotionEngine instance = new PromotionEngine()

  private PromotionEngine( ) { }

  // Internal Methods

  // Public Methods
  Boolean applyOrderDiscount( PromotionModel pModel, String pCorporateKey, Double pDiscountPercent ) {
    Boolean applied = false
    if ( !pModel.hasOrderDiscountApplied() ) {
      pModel.setupOrderDiscount( StringUtils.trimToEmpty( pCorporateKey ), pDiscountPercent )
      PromotionCommit.writeOrder( pModel )
      applied = true
    }
    return applied
  }

  Boolean applyPromotion( PromotionModel pModel, PromotionAvailable pPromotion, Boolean pUserSelection ) {
    Boolean result = pModel.applyPromotion( pPromotion, pUserSelection )
    if ( result ) {
      PromotionCommit.writeOrder( pModel )
    }
    return result
  }

    Boolean getPromotion( PromotionModel pModel, PromotionAvailable pPromotion, Boolean pUserSelection ) {
        Boolean result = pModel.applyPromotion( pPromotion, pUserSelection )
        if ( result ) {
            PromotionCommit.reWriteOrder( pModel )
        }
        return result
    }

    Boolean applyPromotion( PromotionModel pModel, PromotionAvailable pPromotion ) {
        this.applyPromotion( pModel, pPromotion, false )
    }

    Boolean getPromotion( PromotionModel pModel, PromotionAvailable pPromotion ) {
        this.getPromotion( pModel, pPromotion, false )
    }

  Boolean cancelPromotion( PromotionModel pModel, PromotionAvailable pPromotion, Boolean pUserSelection ) {
    Boolean result = pModel.cancelPromotion( pPromotion, pUserSelection )
    if ( result ) {
      PromotionCommit.writeOrder( pModel )
    }
    return result
  }

  Boolean cancelPromotionDiscount( PromotionModel pModel, PromotionDiscount pPromotion, Boolean pUserSelection ) {
      Boolean result = pModel.cancelPromotionDiscount( pPromotion, pUserSelection )
      if ( result ) {
          pModel.orderDiscount = null
          PromotionCommit.writeOrder( pModel )
      }
      return result
  }

  void updateOrder( PromotionModel pModel, String pOrderNbr ) {
    String orderNbr = StringUtils.trimToEmpty( pOrderNbr )
    if ( orderNbr.length() > 0 ) {
      NotaVenta order = RepositoryFactory.getOrders().findOne( StringUtils.trimToEmpty( pOrderNbr ).toUpperCase() )
      updateOrder( pModel, order )
    } else {
      pModel.clear()
    }
  }

  void updateOrder( PromotionModel pModel, NotaVenta pOrder ) {
    if ( pOrder != null ) {
      if ( pModel.isOrderEquals( pOrder ) ) {
        pModel.updateOrder( pOrder )
      } else {
        pModel.clear()
        PromotionQuery.fillActivePromotions( pModel.activePromotionList, pOrder.fechaHoraFactura )
        pModel.loadOrder( pOrder )
      }
      PromotionQuery.fillPart( pModel.partSet )
      PromotionQuery.fillPricing( pModel.pricingSet, pModel.partSet )
      pModel.fillOrderDetail()
      pModel.refreshAvailablePromotions()
      if ( pModel.applyPromotionsAuto() ) {
        PromotionCommit.writeOrder( pModel )
      }
    } else {
      pModel.clear()
    }
  }

  Boolean verifyCorporateKey( String pCorporateKey, Double pDiscountPct ) {
    String key = StringUtils.trimToEmpty( pCorporateKey )
    Boolean verified = ( ( key.length() == 7 ) && ( StringUtils.isNumeric( key ) ) )

    // Verify Key is well formed
    if ( verified ) {
      verified = CorporateKeyVerifier.verify( key )
    }

    // Verify discount is under restriction
    if ( verified ) {
      Double pTopDiscount = NumberUtils.createDouble( key.substring( 1, 3 ) )
      println key.substring( 1, 3 )
      verified = ( pDiscountPct < pTopDiscount || (pTopDiscount == 0.00 && pDiscountPct <= 100.0) )
    }

    // Verify discount has not been registered
    if ( verified ) {
      List<Descuento> dbDiscounts = RepositoryFactory.discounts.findByClave( pCorporateKey )
      verified = ( dbDiscounts.size() == 0 )
    }

    return verified
  }

}
