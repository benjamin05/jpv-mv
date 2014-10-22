package mx.lux.pos.model

import org.apache.commons.lang3.StringUtils

class InvOhDet {
  
  String id
  String desc
  String brand
  Integer qty = 0
  Integer count = 0
  Integer qtyByBrand = 0
  BigDecimal price = BigDecimal.ZERO
  Integer sku = 0
  
  void setId( String pId ) {
    this.id = StringUtils.trimToEmpty( pId ).toUpperCase( )
  }
  
  String toString() {
    return String.format( "[%s] Id:%s  Qty:%d", this.getClass( ).getSimpleName( ), this.id, this.qty )
  }
}
