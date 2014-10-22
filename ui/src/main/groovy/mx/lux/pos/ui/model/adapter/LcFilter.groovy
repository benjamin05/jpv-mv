package mx.lux.pos.ui.model.adapter

import mx.lux.pos.model.Articulo
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.model.PedidoLcDet
import mx.lux.pos.model.TransInv
import mx.lux.pos.model.TransInvDetalle
import mx.lux.pos.ui.resources.ServiceManager
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.apache.commons.lang3.time.DateUtils

class LcFilter extends Filter<PedidoLc> {

  LcAdapter adapter = new LcAdapter()
  Date dateFrom, dateTo
  String trType, partCode, reference
  Integer siteTo
  String sku

  String TAG_FACTURA = 'A0'

  // Public Methods
  Boolean isDateRangeActive() {
    return ( ( dateFrom != null ) && ( dateTo != null ) ) && reference == null
  }
  
  Boolean isPartCodeActive() {
    return ( partCode != null )
  }

  Boolean isSiteToActive() {
    return ( siteTo != null )
  }

  Boolean isSkuActive() {
    return ( sku != null )
  }

  Boolean isTrTypeActive() {
    return ( trType != null )
  }

  Boolean isReferenceActive() {
      return ( reference != null )
  }

  void reset() {
    dateFrom = null
    dateTo = null
    trType = null
    partCode = null
    sku = null
    siteTo = null
    reference = null
  }
  
  void resetDateRange( ) {
    this.dateFrom = null
    this.dateTo = null
  }
  void setDateRange( Date pDate ) {
    setDateRange( pDate, pDate )
  }
  void setDateRange( Date pDateFrom, Date pDateTo) {
    this.dateFrom = pDateFrom != null ? DateUtils.truncate( pDateFrom, Calendar.DATE ) : null
    this.dateTo = pDateTo != null ? new Date( DateUtils.ceiling( pDateTo, Calendar.DAY_OF_MONTH ).getTime() - 1 ) : null;
    //DateUtils.truncate( pDateTo, Calendar.DATE )
  }
  
  void setPartCode( String pPartCode ) {
    partCode = StringUtils.trimToNull( pPartCode.trim( ).toUpperCase( ) )
  }

  void setSiteTo( String pSiteValue ) {
    String value = StringUtils.trimToNull( pSiteValue )
    if ( value != null) {
      try {
        siteTo = NumberUtils.createInteger( value )
      } catch (Exception e) {
        println String.format( "[Filter] SiteTo:<%s> Error:<%s> ", value, e.getMessage() )
      }
    }
  }

  void setSku( String pSkuValue ) {
    String value = StringUtils.trimToNull( pSkuValue )
    if ( value != null) {
      sku = value
    }
  }

  void setTrType( String pTrType ) {
    trType = StringUtils.trimToNull( StringUtils.trimToEmpty(pTrType).toUpperCase( ) )
  }

  void setReference( String pReference ) {
      reference = StringUtils.trimToNull( pReference.trim( ).toUpperCase( ) )
  }

  String toString() {
    String str = "[Filter] "
    if ( isDateRangeActive( ) )
      str += String.format( "DateRange:<%s - %s>", adapter.getText( dateFrom ), adapter.getText( dateTo ) )
    if ( isTrTypeActive( ) ) str += String.format(  "TrType:<%s>", trType )
    if ( isReferenceActive( ) ) str += String.format(  "TrReference:<%s>", reference )
    if ( isSiteToActive( ) ) str += String.format(  "SiteTo:<%d>", siteTo )
    if ( isSkuActive( ) ) str += String.format(  "TrType:<%d>", sku )
    if ( isPartCodeActive( ) ) str += String.format(  "TrType:<%s*>", partCode )
    return str
  }
  
  // Filter Methods
  Boolean select( PedidoLc pInvTr ) {
    Boolean selected = true
    if ( selected && (isDateRangeActive( ) && !this.isTrTypeActive( )) ) {
      selected = ( ( dateFrom.compareTo( pInvTr.fechaAlta ) <= 0 )
                && ( dateTo.compareTo( pInvTr.fechaAlta ) >= 0 ) )
    }
    if ( selected && this.isTrTypeActive( ) ) {
      selected = ( pInvTr.id.equalsIgnoreCase( trType ) )
    }
    if ( selected && this.isReferenceActive( ) ) {
      if( !pInvTr.folio.startsWith(TAG_FACTURA) ){
        selected = ( pInvTr.folio.equalsIgnoreCase( reference ) )
      }
    }
    if ( selected && this.isSiteToActive( ) ) {
      selected = ( siteTo.equals( pInvTr.sucursal ) )
    }
    if ( selected && this.isSkuActive( ) ) {
      /*Boolean found = false
      for ( PedidoLcDet trDet : pInvTr.pedidoLcDets ) {
        if ( sku.equalsIgnoreCase( trDet.modelo ) ) {
          found = true
          break
        }
      }
      selected = found*/
    }
    if ( selected && isPartCodeActive( ) ) {
      Boolean found = false
      for ( PedidoLcDet trDet : pInvTr.pedidoLcDets ) {
        Articulo part = ServiceManager.partService.obtenerArticuloPorArticulo( trDet.modelo, false )
        if (part.articulo.toUpperCase().startsWith( partCode ) ) {
          found = true
          break
        }
      }
      selected = found
    }
    return selected
  }

}
