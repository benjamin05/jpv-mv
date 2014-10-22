package mx.lux.pos.ui.model

interface IPromotion {

  String getDescripcion( )

  String getArticulo( )

  BigDecimal getPrecioLista( )

  BigDecimal getDescuento( )

  BigDecimal getPrecioNeto( )
  
}
