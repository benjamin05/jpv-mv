package mx.lux.pos.java.querys.model;

import mx.lux.pos.model.PromotionModel;
import mx.lux.pos.java.repository.OrdenPromDetJava;
import mx.lux.pos.java.repository.OrdenPromJava;
import mx.lux.pos.java.service.business.PromotionCommitJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Ivan Martinez Mendoza.
 * User: sucursal
 * Date: 9/06/15
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrdenPromJavaList {

    private static final Double ZERO_TOLERANCE = PromotionModel.getZERO_TOLERANCE();

    List<OrdenPromJava> list = new ArrayList<OrdenPromJava>( );
    Integer siteNbr;

  OrdenPromJavaList( Integer pSiteNbr ) {
    this.setSiteNbr( pSiteNbr );
  }

  public OrdenPromJavaList(OrdenPromDetJavaList pOrdenPromDetList) throws ParseException {
    this( pOrdenPromDetList.getSiteNbr() );
    this.loadFromOrdenPromDetList( pOrdenPromDetList );
  }

    // Internal Methods
  protected static final BigDecimal asAmount( Double pDoubleValue ) throws ParseException {
      return PromotionCommitJava.asAmount(pDoubleValue);
  }
  protected static final BigDecimal asPercent( Double pDoubleValue ) throws ParseException {
    return PromotionCommitJava.asPercent( pDoubleValue );
  }

  protected OrdenPromJava find( String pOrderNbr, Integer pPromotionId ) {
    OrdenPromJava found = null;
    for ( OrdenPromJava prom : this.list ) {
      if ( prom.equals( pOrderNbr, pPromotionId ) ) {
        found = prom;
        break;
      }
    }
    return found;
  }

    // Public Methods
  OrdenPromJava add( String pOrderNbr, Integer pPromotionId ) {
    OrdenPromJava prom = this.find( pOrderNbr, pPromotionId );
    if ( prom == null ) {
      prom = new OrdenPromJava( );
      prom.setIdFactura(StringUtils.trimToEmpty(pOrderNbr).toUpperCase());
      prom.setIdProm(pPromotionId);
      prom.setIdSuc(0);
      prom.setTotalDescMonto(BigDecimal.ZERO);
      this.list.add( prom );
    }
    return prom;
  }

  void loadFromOrdenPromDetList( OrdenPromDetJavaList pOrdenPromDetList ) throws ParseException {
    for ( OrdenPromDetJava det : pOrdenPromDetList.list ) {
      OrdenPromJava prom = this.add( det.getIdFactura(), det.getIdProm() );
      prom.setIdSuc(this.siteNbr);
      prom.setTotalDescMonto(asAmount( prom.getTotalDescMonto().doubleValue() + det.getDescuentoMonto().doubleValue() ));
    }
  }

  void setSiteNbr( Integer pSiteNbr ) {
    this.siteNbr = pSiteNbr;
    for ( OrdenPromJava prom : this.list ) {
      prom.setIdSuc(this.siteNbr);
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer( );
    sb.append( String.format( "[%s] %d elements", this.getClass( ).getSimpleName( ), this.list.size( ) ) );
    for ( OrdenPromJava prom : this.list ) {
      sb.append( String.format( "\n    %s", prom.toString( ) ) );
    }
    return sb.toString( );
  }


  public List<OrdenPromJava> getList() {
    return list;
  }

  public Integer getSiteNbr() {
    return siteNbr;
  }
}
