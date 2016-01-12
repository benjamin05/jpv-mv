package mx.lux.pos.java.service.business;

import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.*;
import mx.lux.pos.java.querys.model.OrdenPromDetJavaList;
import mx.lux.pos.java.querys.model.OrdenPromJavaList;
import mx.lux.pos.repository.impl.RepositoryFactory;
import mx.lux.pos.service.business.Registry;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Ivan Martinez Mendoza.
 * User: sucursal
 * Date: 9/06/15
 * Time: 09:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class PromotionCommitJava {

  public static final BigDecimal asAmount(Double pDoubleValue) throws ParseException {
    return new BigDecimal(NumberFormat.getInstance().parse( String.format( "%.2f", pDoubleValue ) ).doubleValue());
  }

  public static final BigDecimal asPercent(Double pDoubleValue) throws ParseException {
    return new BigDecimal(NumberFormat.getInstance().parse( String.format( "%.1f", pDoubleValue ) ).doubleValue());
  }

  public static void writePromotions(PromotionModel pModel) throws ParseException {
    if( pModel.getOrder() != null ){
      deletePromotions( pModel.getOrder().getOrderNbr() );
      if ( pModel.isAnyApplied() ) {
        Integer siteNbr = PromotionQueryJava.findSiteNbr(pModel.getOrder().getOrderNbr());
        OrdenPromDetJavaList opdl = new OrdenPromDetJavaList( siteNbr, pModel );
        if ( opdl.getList().size() > 0 ) {
          OrdenPromJavaList opl = new OrdenPromJavaList( opdl );
          List<OrdenPromJava> commited = new ArrayList<OrdenPromJava>();
          for ( OrdenPromJava op : opl.getList() ) {
            commited.add( OrdenPromQuery.saveOrUpdateOrdenProm( op ) );
          }
          opdl.setRelation( commited );
          for(OrdenPromDetJava det : opdl.getList()){
            det.setDescuentoPorcentaje( new BigDecimal(det.getDescuentoPorcentaje()).setScale(2, BigDecimal.ROUND_CEILING).doubleValue() );
            OrdenPromDetQuery.saveOrUpdateOrdenPromDet( det );
          }
        }
      }
    }
  }


  static final void deletePromotions( String pOrderNbr ) {
    List<OrdenPromDetJava> ordenPromDetList = OrdenPromDetQuery.BuscaOrdenPromDetPorIdFactura(pOrderNbr);
    if ( ordenPromDetList.size() > 0 ) {
      OrdenPromDetQuery.eliminaListaOrdenPromDet(ordenPromDetList);
    }
    List<OrdenPromJava> ordenPromList = OrdenPromQuery.buscaListaOrdenPromPorIdFactura(pOrderNbr);
    if ( ordenPromList.size() > 0 ) {
      OrdenPromQuery.eliminaListaOrdenProm(ordenPromList);
    }
  }


  public static final void writeDiscounts(PromotionModel pModel, Boolean saveOrder) throws ParseException {
    if( pModel.getOrder() != null ){
      deleteDiscounts( pModel.getOrder().getOrderNbr(), saveOrder );
      if ( pModel.hasOrderDiscountApplied() ) {
        String empId = PromotionQueryJava.findEmpId( pModel.getOrder().getOrderNbr() );
        DescuentosJava descuento = new DescuentosJava();
        descuento.setIdFactura(pModel.getOrder().getOrderNbr());
        descuento.setClave(pModel.getOrderDiscount().getCorporateKey());
        if(pModel.getOrderDiscount().getDiscountType().getIdType() != null){
          if(pModel.getOrderDiscount().getDiscountType().getIdType().trim().equalsIgnoreCase("P")){
            descuento.setClave(pModel.getOrderDiscount().getDiscountType().getDescription());
          } else if(StringUtils.trimToEmpty(pModel.getOrderDiscount().getDiscountType().getText()).equalsIgnoreCase("Descuentos CRM")){
            descuento.setClave(pModel.getOrderDiscount().getDiscountType().getDescription());
          }
        }
        if ( pModel.getOrderDiscount().getDiscountPercent() < 1 ) {
          descuento.setPorcentaje(String.format("%.0f", pModel.getOrderDiscount().getDiscountPercent() * 100.0));
        } else {
          descuento.setPorcentaje("100");
        }
        descuento.setIdEmpleado(empId);
        descuento.setIdTipoD(pModel.getOrderDiscount().getDiscountType().getIdType());
        if(StringUtils.trimToEmpty(pModel.getOrderDiscount().getDiscountType().getText()).equalsIgnoreCase("Descuentos CRM")){
          descuento.setTipoClave("DIRECCION");
        } else {
          descuento.setTipoClave(pModel.getOrderDiscount().getDiscountType().getDescription());
        }
        DescuentosQuery.saveOrUpdateDescuentos(descuento);
      }
    }
  }


  static final void deleteDiscounts( String pOrderNbr, Boolean saveOrder ) {
    List<DescuentosJava> discountList = DescuentosQuery.buscaDescuentosPorIdFactura(pOrderNbr);
    if ( discountList.size() > 0 ) {
      for(DescuentosJava desc : discountList){
        DescuentosQuery.eliminaDescuento(desc);
      }
    }
  }


  static final void writeOrder( PromotionModel pModel ) throws ParseException {
    NotaVentaJava dbOrder = NotaVentaQuery.busquedaNotaById(pModel.getOrder().getOrderNbr());
    Double netAmount = 0.00;
    Double amountEnsure = 0.00;
    for ( DetalleNotaVentaJava dbOrderLine : dbOrder.getDetalles() ) {
      if( pModel.getOrderDiscount() != null ){
        if( !Registry.getGenericsWithoutDiscount().contains(StringUtils.trimToEmpty(dbOrderLine.getArticulo().getIdGenerico()))  ){
          PromotionOrderDetail orderDetail = pModel.getOrder().getOrderDetailSet().get(dbOrderLine.getIdArticulo());
          if ( orderDetail != null ) {
            dbOrderLine.setPrecioUnitFinal(asAmount(orderDetail.getFinalPrice()));
          } else {
            dbOrderLine.setPrecioUnitFinal(dbOrderLine.getPrecioUnitLista());
          }
          dbOrderLine.setPrecioFactura(dbOrderLine.getPrecioUnitFinal());
          netAmount += dbOrderLine.getPrecioUnitFinal().doubleValue() * dbOrderLine.getCantidadFac();
          DetalleNotaVentaQuery.updateDetalleNotaVenta(dbOrderLine);
        } else {
          amountEnsure = amountEnsure+dbOrderLine.getPrecioUnitFinal().doubleValue() * dbOrderLine.getCantidadFac();
        }
      } else {
        PromotionOrderDetail orderDetail = pModel.getOrder().getOrderDetailSet().get(dbOrderLine.getIdArticulo());
        if ( orderDetail != null ) {
          dbOrderLine.setPrecioUnitFinal(asAmount(orderDetail.getFinalPrice()));
        } else {
          dbOrderLine.setPrecioUnitFinal(dbOrderLine.getPrecioUnitLista());
        }
        dbOrderLine.setPrecioFactura(dbOrderLine.getPrecioUnitFinal());
        netAmount += dbOrderLine.getPrecioUnitFinal().doubleValue() * dbOrderLine.getCantidadFac();
        DetalleNotaVentaQuery.updateDetalleNotaVenta(dbOrderLine);
      }
    }
    netAmount = netAmount+amountEnsure;
    dbOrder.setVentaNeta(asAmount((double) Math.round(netAmount)));
    dbOrder.setVentaTotal(asAmount( (double) Math.round(netAmount) ));
    if ( pModel.hasOrderDiscountApplied() ) {
      System.out.println((double) Math.round(pModel.getOrderDiscount().getDiscountAmount()));
      dbOrder.setMontoDescuento(asAmount( (double) Math.round(pModel.getOrderDiscount().getDiscountAmount()) ));
      dbOrder.setPor100Descuento((int) Math.round( pModel.getOrderDiscount().getDiscountPercent() * 100.0 ));
    } else {
      dbOrder.setMontoDescuento(BigDecimal.ZERO);
      dbOrder.setPor100Descuento(0);
    }
    NotaVentaQuery.updateNotaVenta( dbOrder );
  }


}
