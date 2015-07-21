package mx.lux.pos.service.business

import mx.lux.pos.java.querys.TransInvDetQuery
import mx.lux.pos.java.querys.TransInvQuery
import mx.lux.pos.java.repository.TransInvJava
import mx.lux.pos.model.*
import mx.lux.pos.repository.NotaVentaRepository
import mx.lux.pos.repository.TipoTransInvRepository
import mx.lux.pos.repository.TransInvDetalleRepository
import mx.lux.pos.repository.TransInvRepository
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.io.ZInFile
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class InventorySearch {

  private static TransInvRepository trInvMaster
  private static TransInvDetalleRepository trInvDetail
  private static TipoTransInvRepository trTypeCatalog
  private static ArticuloService partMaster
  private static NotaVentaRepository notaVentaRepository

  private static final String TAG_STARTS_FACT = 'A0'
  private static final String TAG_TRANSACCION_VENTA = 'VENTA'

  @Autowired InventorySearch( TransInvRepository pTrMstr, TransInvDetalleRepository pTrDet, ArticuloService pParts,
                              TipoTransInvRepository pTypeCatalog, NotaVentaRepository pNotaVentaRepository
  ) {
    trInvMaster = pTrMstr
    trInvDetail = pTrDet
    partMaster = pParts
    trTypeCatalog = pTypeCatalog
    notaVentaRepository = pNotaVentaRepository
  }

  static List<Integer> buildSkuList( List<Articulo> pPartList ) {
    List<Integer> list = new ArrayList<Integer>( pPartList.size() )
    for ( Articulo part : pPartList ) {
      list.add( part.getId() )
    }
    return list
  }

  static void loadDetails( List<TransInv> pMaster ) {
    for ( TransInv tr in pMaster ) {
      tr.setTrDet( trInvDetail.findByIdTipoTransAndFolio( tr.idTipoTrans, tr.folio ) )
    }
  }

  static void loadDetailsJava( List<TransInvJava> pMaster ) {
    for ( TransInvJava tr in pMaster ) {
      tr.setTrDet( TransInvDetQuery.buscaTransInvDetPorIdTipoYFolio( tr.idTipoTrans, tr.folio ) )
    }
  }

  static List<TransInv> detailToMaster( List<TransInvDetalle> pDetails ) {
    List<TransInv> mstrList = new ArrayList<TransInv>()
    for ( TransInvDetalle det in pDetails ) {
      List<TransInv> one = trInvMaster.findByIdTipoTransAndFolio( det.idTipoTrans, det.folio )
      TransInv mstr = ( one.size() > 0 ? one.get( 0 ) : null )
      if ( mstr != null ) {
        if ( !mstrList.contains( mstr ) ) {
          mstrList.add( mstr )
        }
      }
    }
    Collections.sort( mstrList )
    return mstrList
  }

  static List<TransInv> listarTransaccionesPorRangoFecha( Date pRangeStart, Date pRangeEnd ) {
    List<TransInv> selected = trInvMaster.findByFechaBetween( pRangeStart, pRangeEnd )
    loadDetails( selected )
    return selected
  }

  static List<TransInv> listarTransaccionesPorTipo( String pIdTipoTrans ) {
    List<TransInv> selected = trInvMaster.findByIdTipoTrans( pIdTipoTrans )
    loadDetails( selected )
    return selected
  }

  static List<TransInv> listarTransaccionesPorSucursalDestino( Integer pSiteTo ) {
    List<TransInv> selected = trInvMaster.findBySucursalDestino( pSiteTo )
    loadDetails( selected )
    return selected
  }

  static List<TransInv> listarTransaccionesPorSku( Integer pSku ) {
    List<TransInvDetalle> details = trInvDetail.findBySku( pSku )
    List<TransInv> selected = detailToMaster( details )
    loadDetails( selected )
    return selected
  }

  static List<TransInv> listarTransaccionesPorArticulo( String pPartCodeSeed ) {
    List<Articulo> partList = partMaster.listarArticulosPorCodigoSimilar( pPartCodeSeed, false )
    List<TransInvDetalle> details = new ArrayList<TransInvDetalle>()
    if ( partList.size() > 0 ) {
      List<Integer> skuList = buildSkuList( partList )
      details = trInvDetail.findBySkuIn( skuList )
    }
    List<TransInv> selected = detailToMaster( details )
    loadDetails( selected )
    return selected
  }

  static List<TransInv> listarTransaccionesPorTipoAndReferencia( String pTrType, String pReference ) {
    List<TransInv> selected = trInvMaster.findByIdTipoTransAndReferencia( pTrType, pReference )
    loadDetails( selected )
    return selected
  }

  static List<TransInv> listarTransaccionesPorFecha( Date pDateFrom, Date pDateTo ) {
    Date dtFrom = DateUtils.truncate( pDateFrom, Calendar.DATE )
    Date dtTo = DateUtils.truncate( pDateTo, Calendar.DATE )
    List<TransInv> lstTrans = trInvMaster.findByFechaBetween( dtFrom, dtTo )
    List<TransInv> selected = new ArrayList<>()
    for(TransInv trans : lstTrans){
      if(StringUtils.trimToEmpty(trans.idTipoTrans).equalsIgnoreCase(TAG_TRANSACCION_VENTA) ){
        NotaVenta nota = notaVentaRepository.findOne( StringUtils.trimToEmpty(trans.referencia) )
        if( nota != null && nota.detalles.size() > 0 ){
          selected.add( trans )
        }
      } else {
        selected.add( trans )
      }
    }
    loadDetails( selected )
    return selected
  }

  static TransInv obtenerTransaccion( String pIdTipoTrans, Integer pFolio ) {
    TransInv tr = null
    List<TransInv> trList = trInvMaster.findByIdTipoTransAndFolio( pIdTipoTrans, pFolio )
    if ( trList.size() > 0 ) {
      loadDetails( trList )
      tr = trList.get( 0 )
    }
    return tr
  }


  static TransInvJava obtenerTransaccionJava( String pIdTipoTrans, Integer pFolio ) {
    TransInvJava tr = null
    List<TransInvJava> trList = TransInvQuery.buscaTransInvPorTipoYFolio( pIdTipoTrans, pFolio )
    if ( trList.size() > 0 ) {
      loadDetailsJava( trList )
      tr = trList.get( 0 )
    }
    return tr
  }

  static Boolean esTipoTransaccionAjuste( String pIdTipoTrans ) {
    Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_AJUSTE )
    return p.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) )
  }

  static Boolean esTipoTransaccionDevolucion( String pIdTipoTrans ) {
    Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_CANCELACION_EXTRA )
    return p.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) )
  }

  static Boolean esTipoTransaccionSalida( String pIdTipoTrans ) {
    Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_SALIDA )
    return p.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) )
  }

  static Boolean esTipoTransaccionOtraSalida( String pIdTipoTrans ) {
    Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_OTRA_SALIDA )
    return p.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) )
  }

  static Boolean esTipoTransaccionSalidaSucursal( String pIdTipoTrans ) {
     Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_SALIDA_ALMACEN )
     return p.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) )
  }

    static Boolean esTipoTransaccionEntrada( String pIdTipoTrans ) {
        Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_ENTRADA_ALMACEN )
        Parametro p1 = Registry.find( TipoParametro.TRANS_INV_TIPO_RECIBE_REMISION )
        Parametro p2 = Registry.find( TipoParametro.TRANS_INV_TIPO_OTRA_ENTRADA )
        Boolean tipoEntrada = false
        if(p.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) ) || p1.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) )
                || p2.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) )){
            tipoEntrada = true
        }
        return tipoEntrada
    }

    static void generateInFile( Date pDateFrom, Date pDateTo ) {
    ZInFile file = new ZInFile( DateUtils.truncate( pDateFrom, Calendar.DATE ) )
    file.setInvTrList( listarTransaccionesPorFecha( pDateFrom, pDateTo ) )
    file.write()
  }

  static TipoTransInv findTrType( String pTipoTransInv ) {
    return trTypeCatalog.findOne( pTipoTransInv )
  }


  static List<TransInv> listarTransaccionesPorReferencia( String pReferencia ) {
      List<NotaVenta> nota = new ArrayList<>()
      if(pReferencia.isNumber()){
        String factura = pReferencia
        if(factura.length() < 6){
          factura = String.format( "%06d", Integer.parseInt(pReferencia) )
        }
        nota = notaVentaRepository.findByFactura( factura.trim() )
      }
      QTransInv trans = QTransInv.transInv
      List<TransInv> lstTransacciones = trInvMaster.findAll( trans.referencia.eq(pReferencia.trim()) )
      if(nota.size() > 0){
        List<TransInv> lstTransaccionesTmp = trInvMaster.findAll( trans.referencia.eq(nota.first().id) )
        if( lstTransaccionesTmp.size() > 0){
          lstTransacciones.addAll( lstTransaccionesTmp )
        }
      }
      loadDetails( lstTransacciones )
      return lstTransacciones
  }
}
