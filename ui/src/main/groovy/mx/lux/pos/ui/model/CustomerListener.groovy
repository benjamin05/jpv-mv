package mx.lux.pos.ui.model

import mx.lux.pos.model.IPromotionAvailable

interface CustomerListener {

  void reset( )

  void setCustomer( Customer pCustomer )

  void setOperationTypeSelected( OperationType pOperation )

  void setOrder( Order pOrder )

  void disableUI()

  void enableUI()

  void setPromotion( Order pOrder )
}