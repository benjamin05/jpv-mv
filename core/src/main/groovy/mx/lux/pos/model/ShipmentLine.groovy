package mx.lux.pos.model

import org.apache.commons.lang3.StringUtils

class ShipmentLine {

  Integer sku
  String partCode
  String colorCode
  String colorDesc
  String brand
  String type
  String subtype
  String barcode
  String partDesc
  Integer qty = 0
  
  ArticuloSombra getPartShadow() {
    ArticuloSombra shadow = new ArticuloSombra( sku )
    shadow.setArticulo( partCode )
    shadow.setCodigoColor( colorCode )
    shadow.setDescripcionColor( colorDesc )
    shadow.setTipo( type )
    shadow.setSubtipo( subtype )
    shadow.setDescripcionArt( partDesc )
    shadow.setMarca( brand )
    shadow.setCB( barcode )
    return shadow
  }

  void setBarcode( String pBarcode ) {
    this.barcode = StringUtils.trimToEmpty( pBarcode ).toUpperCase( )
  } 
   
  void setBrand( String pBrand ) {
    this.brand = StringUtils.trimToEmpty( pBrand ).toUpperCase( )
  }
  
  void setColorCode( String pColorCode ) {
    this.colorCode = StringUtils.trimToEmpty( pColorCode ).toUpperCase( )
  } 
   
  void setColorDesc( String pColorDesc ) {
    this.colorDesc = StringUtils.trimToEmpty( pColorDesc )
  } 
   
  void setPartCode( String pPartCode ) {
    this.partCode = StringUtils.trimToEmpty( pPartCode ).toUpperCase( )
  } 
  
  void setType( String pType ) {
    this.type = StringUtils.trimToEmpty( pType ).toUpperCase( )
  }

  void setSubtype( String pSubtype ) {
    this.subtype = StringUtils.trimToEmpty( pSubtype ).toUpperCase( )
  }

  void setPartDesc( String pPartDesc ) {
    this.partDesc = StringUtils.trimToEmpty( pPartDesc ).toUpperCase( )
  }
  
  String toString() {
    String.format( "Sku(%d): %s [%s: %s] Marca:<%s> Tipo:<%s> CB:<%s> Cant:<%d>", sku, partCode, colorCode,
        colorDesc, brand, type, barcode, qty )
  }
  
}
