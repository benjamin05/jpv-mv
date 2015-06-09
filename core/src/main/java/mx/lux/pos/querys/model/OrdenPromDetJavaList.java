package mx.lux.pos.querys.model;

import mx.lux.pos.model.*;
import mx.lux.pos.repository.OrdenPromDetJava;
import mx.lux.pos.repository.OrdenPromJava;
import mx.lux.pos.service.business.PromotionCommit;
import mx.lux.pos.service.business.PromotionCommitJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Ivan Martinez Mendoza.
 * User: sucursal
 * Date: 9/06/15
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrdenPromDetJavaList {

  private static final Double ZERO_TOLERANCE = 0.0001;


  List<OrdenPromDetJava> list = new ArrayList<OrdenPromDetJava>( );
  Integer siteNbr;

  OrdenPromDetJavaList( Integer pSiteNbr ) {
        this.setSiteNbr( pSiteNbr );
  }

  public OrdenPromDetJavaList(Integer pSiteNbr, PromotionModel pModel) throws ParseException {
    this( pSiteNbr );
    this.loadFromModel( pModel );
  }

  void setSiteNbr( Integer pSiteNbr ) {
    this.siteNbr = pSiteNbr;
    for ( OrdenPromDetJava orderPromLine : list ) {
      orderPromLine.setIdSuc(this.siteNbr);
    }
  }


  void loadFromModel( PromotionModel pModel ) throws ParseException {
    this.list.clear( );
    for ( PromotionAvailable promotion : pModel.listAvailablePromotions( ) ) {
      if ( promotion.getApplied() ) {
        for ( PromotionApplied promotionLine : promotion.getAppliesToList() ) {
          OrdenPromDetJava promDet = this.add( pModel.getOrder().getOrderNbr(), promotionLine.getSku() );
          promDet.setIdSuc(this.siteNbr);
          promDet.setIdProm(promotion.getIdPromotion());
          promDet.setPrecioBase(asAmount( promDet.getPrecioBase().doubleValue() +  promotionLine.getBaseAmount() ));
          promDet.setDescuentoMonto(asAmount( promDet.getDescuentoMonto().doubleValue() + promotionLine.getDiscountAmount() ));
          if ( Math.abs( promDet.getPrecioBase().doubleValue() ) > ZERO_TOLERANCE ) {
            Double percent = ( 100.0 * promDet.getDescuentoMonto().doubleValue() ) /  promDet.getPrecioBase().doubleValue();
            promDet.setDescuentoPorcentaje(asPercent( percent ).doubleValue());
          } else {
            promDet.setDescuentoPorcentaje(BigDecimal.ZERO.doubleValue());
          }
        }
      }
    }
  }


  OrdenPromDetJava add( String pOrderNbr, Integer pSku ) {
    OrdenPromDetJava det = find( pOrderNbr, pSku );
    if ( det == null ) {
      det = new OrdenPromDetJava();
      det.setIdFactura(StringUtils.trimToEmpty(pOrderNbr).toUpperCase());
      det.setIdArt(pSku);
      det.setIdProm(0);
      det.setIdSuc(0);
      det.setPrecioBase(BigDecimal.ZERO);
      det.setDescuentoMonto(BigDecimal.ZERO);
      det.setDescuentoPorcentaje(BigDecimal.ZERO.doubleValue());
      this.list.add( det );
    }
    return det;
  }


  protected OrdenPromDetJava find( String pOrderNbr, Integer pSku ) {
    OrdenPromDetJava found = null;
    for ( OrdenPromDetJava det : this.list ) {
      if ( det.equals(pOrderNbr, pSku) ) {
        found = det;
        break;
      }
    }
    return found;
  }


  protected static final BigDecimal asAmount( Double pDoubleValue ) throws ParseException {
    return PromotionCommitJava.asAmount(pDoubleValue);
  }

  protected static final BigDecimal asPercent( Double pDoubleValue ) throws ParseException {
    return PromotionCommitJava.asPercent( pDoubleValue );
  }


  public List<OrdenPromDetJava> getList() {
    return list;
  }

  public Integer getSiteNbr() {
    return siteNbr;
  }


  public void setRelation(List<OrdenPromJava> pOrdenPromList) {
    for ( OrdenPromJava op : pOrdenPromList ) {
      for ( OrdenPromDetJava opd : this.list ) {
        if ( StringUtils.trimToEmpty(opd.getIdFactura()).equalsIgnoreCase( StringUtils.trimToEmpty(op.getIdFactura()) ) &&
                opd.getIdProm().equals(op.getIdProm()) ) {
          opd.setId(op.getId());
        }
      }
    }
  }


}
