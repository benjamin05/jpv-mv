package mx.lux.pos.service.business

import mx.lux.pos.java.querys.PromocionQuery
import mx.lux.pos.java.repository.PromocionJava
import mx.lux.pos.model.*
import mx.lux.pos.repository.GrupoArticuloDetRepository
import mx.lux.pos.repository.GrupoArticuloRepository
import mx.lux.pos.repository.PromocionRepository
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.io.PromotionsAdapter
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource
import java.text.NumberFormat

class PromotionCommit {

  @Resource
  private PromocionRepository promocionRepository

  private static Logger log = LoggerFactory.getLogger( PromotionCommit.class )

  static final BigDecimal asAmount( Double pDoubleValue ) {
    return NumberFormat.getInstance().parse( String.format( "%.2f", pDoubleValue ) )
  }

  static final BigDecimal asPercent( Double pDoubleValue ) {
    return NumberFormat.getInstance().parse( String.format( "%.1f", pDoubleValue ) )
  }

  static final void deleteDiscounts( String pOrderNbr, Boolean saveOrder ) {
    List<Descuento> discountList = RepositoryFactory.discounts.findByIdFactura( pOrderNbr )
    if ( discountList.size() > 0 ) {
      for(Descuento desc : discountList){
        RepositoryFactory.discounts.delete( desc.id )
        RepositoryFactory.discounts.flush()
      }
    }
  }

  static final void deletePromotions( String pOrderNbr ) {
    List<OrdenPromDet> ordenPromDetList = RepositoryFactory.orderLinePromotionDetail.findByIdFactura( pOrderNbr )
    if ( ordenPromDetList.size() > 0 ) {
      RepositoryFactory.orderLinePromotionDetail.deleteInBatch( ordenPromDetList )
    }
    RepositoryFactory.orderLinePromotionDetail.flush()

    List<OrdenProm> ordenPromList = RepositoryFactory.orderPromotionDetail.findByIdFactura( pOrderNbr )
    if ( ordenPromList.size() > 0 ) {
      RepositoryFactory.orderPromotionDetail.deleteInBatch( ordenPromList )
    }
    RepositoryFactory.orderPromotionDetail.flush()
  }

  static final void writeDiscounts( PromotionModel pModel, Boolean saveOrder ) {
    deleteDiscounts( pModel.order.orderNbr, saveOrder )
    if ( pModel.hasOrderDiscountApplied() ) {
      String empId = PromotionQuery.findEmpId( pModel.order.orderNbr )
      Descuento descuento = new Descuento()
      descuento.idFactura = pModel.order.orderNbr
        descuento.clave = pModel.orderDiscount.corporateKey
        if(pModel?.orderDiscount?.discountType?.idType != null){
             if(pModel?.orderDiscount?.discountType?.idType.trim().equals('P')){
             descuento.clave = pModel.orderDiscount.discountType?.description
            }
        }
        if ( pModel.orderDiscount.discountPercent < 1 ) {
        descuento.porcentaje = String.format( "%.0f", pModel.orderDiscount.discountPercent * 100.0 )
      } else {
        descuento.porcentaje = "100"
      }
      descuento.idEmpleado = empId
      descuento.idTipoD = pModel.orderDiscount.discountType.idType
      descuento.tipoClave = pModel.orderDiscount.discountType.description
      RepositoryFactory.discounts.saveAndFlush( descuento )
    }
  }

  static final void writePromotions( PromotionModel pModel ) {
    deletePromotions( pModel.order.orderNbr )
    if ( pModel.isAnyApplied() ) {
      Integer siteNbr = PromotionQuery.findSiteNbr( pModel.order.orderNbr )
      OrdenPromDetList opdl = new OrdenPromDetList( siteNbr, pModel )
      if ( opdl.list.size() > 0 ) {
        OrdenPromList opl = new OrdenPromList( opdl )
        List<OrdenProm> commited = new ArrayList<OrdenProm>()
        for ( OrdenProm op : opl.list ) {
          commited.add( RepositoryFactory.orderPromotionDetail.save( op ) )
        }
        RepositoryFactory.orderPromotionDetail.flush()
        opdl.setRelation( commited )
        for(OrdenPromDet det : opdl.list){
          det.setDescuentoPorcentaje( det.descuentoPorcentaje.setScale(2, BigDecimal.ROUND_CEILING) )
        }
        RepositoryFactory.orderLinePromotionDetail.save( opdl.list )
        RepositoryFactory.orderLinePromotionDetail.flush()
      }
    }
  }

  static final void writeOrder( PromotionModel pModel ) {
    NotaVenta dbOrder = RepositoryFactory.orders.findOne( pModel.order.orderNbr )
    Double netAmount = 0
    Double amountEnsure = 0
    Double amountRestOrder = 0
    Double amountDesc = 0
    String generic = ""
    Boolean crm = false
    Boolean allGen = false
    Boolean oneValGen = false
    Boolean twoValGen = false
    Boolean oneNotValGen = false
    BigDecimal discountAmount = BigDecimal.ZERO
    if( pModel.orderDiscount != null && StringUtils.trimToEmpty(pModel.orderDiscount.discountType.text).equalsIgnoreCase("Descuentos CRM") ){
      crm = true
      if(StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description).length() >= 11 &&
              !StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description).substring(0,4).isNumber()){
        generic = StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description).substring(1,3)
        String clave = ""
        Integer percentajeInt = 0
        for(int i=0;i<StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description).length();i++){
          if(StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description.charAt(i).toString()).isNumber()){
            Integer number = 0
            try{
              number = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description.charAt(i).toString()))
            } catch ( NumberFormatException e ) { println e }
            clave = clave+StringUtils.trimToEmpty((10-number).toString())
          } else {
            clave = clave+0
          }
        }
        String percentaje = StringUtils.trimToEmpty(clave).substring(3,5)
        try{
              percentajeInt = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(percentaje))
        } catch ( NumberFormatException e) { e.printStackTrace() }
        discountAmount = new BigDecimal(percentajeInt.doubleValue()*Registry.multiplyDiscountCrm)
        if( generic.contains("**") ){
              allGen = true
        } else if( generic.replace("!","\\!").contains("\\!") ){
              oneNotValGen = true
        } else if( generic.contains("_") ){
              oneValGen = true
        } else {
              twoValGen = true
        }
      } else if(StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description).length() >= 10 &&
              StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description).substring(0,4).isNumber() ){
        String descPromo = "crm:${StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description)}"
        PromocionJava promo = PromocionQuery.buscaPromocionPorDescCrm(descPromo)
        if( promo == null ){
          descPromo = "CRM:${StringUtils.trimToEmpty(pModel.orderDiscount.discountType.description)}"
          promo = PromocionQuery.buscaPromocionPorDescCrm(descPromo)
        }
        if(promo != null){
          generic = StringUtils.trimToEmpty(promo.idGenerico)
          discountAmount = promo.precioDescontado
          if( generic.contains("*") ){
            allGen = true
          } else if( StringUtils.trimToEmpty(promo.genericoc).length() <= 0 ){
            oneValGen = true
            generic = "_"+generic
          } else if( StringUtils.trimToEmpty(promo.genericoc).length() > 0 ){
            twoValGen = true
            generic = generic+StringUtils.trimToEmpty(promo.genericoc)
          }
        }
      }
    }
    for ( DetalleNotaVenta dbOrderLine : dbOrder.detalles ) {
      if( pModel.orderDiscount != null ){
        if( !Registry.genericsWithoutDiscount.contains(StringUtils.trimToEmpty(dbOrderLine.articulo.idGenerico))  ){
          PromotionOrderDetail orderDetail = pModel.order.orderDetailSet.get( dbOrderLine.idArticulo )
          if( crm ){
            if( allGen ){
              if ( orderDetail != null ) {
                dbOrderLine.precioUnitFinal = asAmount( orderDetail.finalPrice )
              } else {
                dbOrderLine.precioUnitFinal = dbOrderLine.precioUnitLista
              }
              dbOrderLine.precioFactura = dbOrderLine.precioUnitFinal
              netAmount += dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
              amountDesc = amountDesc+dbOrderLine.precioUnitLista.doubleValue()-dbOrderLine.precioUnitFinal.doubleValue()
            } else if( oneValGen ){
              if( StringUtils.trimToEmpty(dbOrderLine.articulo.idGenerico).equalsIgnoreCase(generic.substring(1)) ){
                if ( orderDetail != null ) {
                  dbOrderLine.precioUnitFinal = asAmount( orderDetail.finalPrice )
                } else {
                  dbOrderLine.precioUnitFinal = dbOrderLine.precioUnitLista
                }
                dbOrderLine.precioFactura = dbOrderLine.precioUnitFinal
                netAmount += dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
                amountDesc = amountDesc+dbOrderLine.precioUnitLista.doubleValue()-dbOrderLine.precioUnitFinal.doubleValue()
              } else {
                amountRestOrder = amountRestOrder+dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
              }
            } else if( twoValGen ){
              if( StringUtils.trimToEmpty(dbOrderLine.articulo.idGenerico).equalsIgnoreCase(generic.substring(0,1)) ||
                      StringUtils.trimToEmpty(dbOrderLine.articulo.idGenerico).equalsIgnoreCase(generic.substring(1)) ){
                if ( orderDetail != null ) {
                  dbOrderLine.precioUnitFinal = asAmount( orderDetail.finalPrice )
                } else {
                  dbOrderLine.precioUnitFinal = dbOrderLine.precioUnitLista
                }
                dbOrderLine.precioFactura = dbOrderLine.precioUnitFinal
                netAmount += dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
                amountDesc = amountDesc+dbOrderLine.precioUnitLista.doubleValue()-dbOrderLine.precioUnitFinal.doubleValue()
              } else {
                amountRestOrder = amountRestOrder+dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
              }
            } else if( oneNotValGen ){
              if( !StringUtils.trimToEmpty(dbOrderLine.articulo.idGenerico).equalsIgnoreCase(generic.substring(1)) ){
                if ( orderDetail != null ) {
                  dbOrderLine.precioUnitFinal = asAmount( orderDetail.finalPrice )
                } else {
                  dbOrderLine.precioUnitFinal = dbOrderLine.precioUnitLista
                }
                dbOrderLine.precioFactura = dbOrderLine.precioUnitFinal
                netAmount += dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
                amountDesc = amountDesc+dbOrderLine.precioUnitLista.doubleValue()-dbOrderLine.precioUnitFinal.doubleValue()
              } else {
                amountRestOrder = amountRestOrder+dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
              }
            }
          } else {
            if ( orderDetail != null ) {
              dbOrderLine.precioUnitFinal = asAmount( orderDetail.finalPrice )
            } else {
              dbOrderLine.precioUnitFinal = dbOrderLine.precioUnitLista
            }
            dbOrderLine.precioFactura = dbOrderLine.precioUnitFinal
            netAmount += dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
          }
          RepositoryFactory.orderLines.save( dbOrderLine )
        } else {
          amountEnsure = amountEnsure+dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
        }
      } else {
        PromotionOrderDetail orderDetail = pModel.order.orderDetailSet.get( dbOrderLine.idArticulo )
        if ( orderDetail != null ) {
          dbOrderLine.precioUnitFinal = asAmount( orderDetail.finalPrice )
        } else {
          dbOrderLine.precioUnitFinal = dbOrderLine.precioUnitLista
        }
        dbOrderLine.precioFactura = dbOrderLine.precioUnitFinal
        netAmount += dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
        RepositoryFactory.orderLines.save( dbOrderLine )
      }
    }
    BigDecimal diff = discountAmount.subtract(amountDesc)
    if( diff.compareTo(BigDecimal.ZERO) < 0 || diff.compareTo(BigDecimal.ZERO) > 0 ){
      amountDesc = amountDesc+diff.doubleValue()
      netAmount = netAmount-diff.doubleValue()
    }
    RepositoryFactory.orderLines.flush()
    netAmount = netAmount+amountEnsure+amountRestOrder
    dbOrder.ventaNeta = asAmount( netAmount.round() )
    dbOrder.ventaTotal = asAmount( netAmount.round() )

    if ( pModel.hasOrderDiscountApplied() ) {
      println pModel.orderDiscount.discountAmount.round()
      if( amountDesc > 0 && StringUtils.trimToEmpty(pModel.orderDiscount.discountType.text).equalsIgnoreCase("Descuentos CRM") ){
        dbOrder.montoDescuento = asAmount( amountDesc )
      } else {
        dbOrder.montoDescuento = asAmount( pModel.orderDiscount.discountAmount.round() )
      }
      dbOrder.por100Descuento = Math.round( pModel.orderDiscount.discountPercent * 100.0 ) as Integer
    } else {
      dbOrder.montoDescuento = BigDecimal.ZERO
      dbOrder.por100Descuento = 0
    }
    RepositoryFactory.orders.saveAndFlush( dbOrder )
  }


    static final void reWriteOrder( PromotionModel pModel ) {
        NotaVenta dbOrder = RepositoryFactory.orders.findOne( pModel.order.orderNbr )
        Double netAmount = 0
        for ( DetalleNotaVenta dbOrderLine : dbOrder.detalles ) {
            PromotionOrderDetail orderDetail = pModel.order.orderDetailSet.get( dbOrderLine.idArticulo )
            if ( orderDetail != null ) {
                dbOrderLine.precioUnitFinal = asAmount( orderDetail.finalPrice )
            } else {
                dbOrderLine.precioUnitFinal = dbOrderLine.precioUnitLista
            }
            dbOrderLine.precioFactura = dbOrderLine.precioUnitFinal
            netAmount += dbOrderLine.precioUnitFinal.doubleValue() * dbOrderLine.cantidadFac
            RepositoryFactory.orderLines.save( dbOrderLine )
        }
        RepositoryFactory.orderLines.flush()
        dbOrder.ventaNeta = asAmount( netAmount.round() )
        dbOrder.ventaTotal = asAmount( netAmount.round() )
        //if ( pModel.hasOrderDiscountApplied() ) {
        //try{
          dbOrder.montoDescuento = asAmount( pModel?.orderDiscount != null ? pModel?.orderDiscount?.discountAmount.round() : 0.00 )
          dbOrder.por100Descuento = Math.round( pModel?.orderDiscount != null ? pModel?.orderDiscount?.discountPercent * 100.0 : 0.00 ) as Integer
        //} catch( Exception e ){ println e }
        /*} else {
            dbOrder.montoDescuento = BigDecimal.ZERO
            dbOrder.por100Descuento = 0
        }*/
        RepositoryFactory.orders.saveAndFlush( dbOrder )
    }

  static void updatePromotions( List<PromotionsAdapter> lstPromociones ) {

    PromotionsAdapter promotionsAdapter = new PromotionsAdapter()
    PromocionRepository promociones = RepositoryFactory.promotionCatalog
    for ( PromotionsAdapter p : lstPromociones ) {
      /*Promocion promocion = promociones.findOne( p.idPromocion )
      if ( promocion != null ) {
        promociones.delete( promocion.idPromocion )
        promociones.flush()
      } else {*/
        Promocion promocion = new Promocion()
        promocion.idPromocion = p.idPromocion
      //}
      p.assignInto( promocion )
      promociones.save( promocion )
    }
    promociones.flush()
  }


  static void updateGroupPromotions( List<String> lstPromociones ) {
    GrupoArticuloRepository groups = RepositoryFactory.groupPartMaster
    GrupoArticuloDetRepository parts = RepositoryFactory.groupPartDetail

    List<GrupoArticuloImportLine> records = GrupoArticuloImportLine.adaptList( lstPromociones )
    Map<String, GrupoArticuloDet> buffer = new HashMap<String, GrupoArticuloDet>()
    GrupoArticulo currGroup = null
    for ( GrupoArticuloImportLine line : records ) {
      if ( ( currGroup == null ) || ( ! currGroup.equals( line.groupId ) ) ) {
        List<GrupoArticuloDet> partList = parts.findByIdGrupo( line.groupId )
        if ( partList.size() > 0 ) {
          try {
            parts.delete( partList )
            parts.flush()
          } catch ( Exception e ) {
            log.error( e.getMessage(), e )
          }
        }

        currGroup = groups.findOne( line.groupId )
        if ( currGroup == null ) {
          currGroup = new GrupoArticulo()
          currGroup.idGrupo = line.groupId
        }
        currGroup.descripcion = line.description
        try {
          groups.saveAndFlush( currGroup )
        } catch ( Exception e ) {
          log.error( e.getMessage(), e )
        }
      }

      String key = String.format( '%d:%s', line.groupId, line.partNbr )
      GrupoArticuloDet part = buffer.get( key )
      if ( part == null ) {
        part = new GrupoArticuloDet()
        part.idGrupo = line.groupId
        part.articulo = line.partNbr
        buffer.put( key, part )
      }
    }

    if ( buffer.size() > 0 ) {
      try {
        parts.save( buffer.values() )
        parts.flush()
      } catch ( Exception e ) {
        log.error( e.getMessage(), e )
      }
    }
  }

}
