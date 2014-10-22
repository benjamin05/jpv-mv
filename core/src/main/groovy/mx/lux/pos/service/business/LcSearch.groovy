package mx.lux.pos.service.business

import mx.lux.pos.model.*
import mx.lux.pos.repository.ClienteRepository
import mx.lux.pos.repository.NotaVentaRepository
import mx.lux.pos.repository.TipoTransInvRepository
import mx.lux.pos.repository.PedidoLcDetRepository
import mx.lux.pos.repository.PedidoLcRepository
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.io.ZInFile
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class LcSearch {

  private static PedidoLcRepository trInvMaster
  private static ClienteRepository clientMaster
  private static PedidoLcDetRepository trInvDetail
  private static TipoTransInvRepository trTypeCatalog
  private static ArticuloService partMaster
  private static NotaVentaRepository notaVentaRepository

  private static final String TAG_STARTS_FACT = 'A0'
  private static final String TAG_TRANSACCION_VENTA = 'VENTA'

  @Autowired
  LcSearch( PedidoLcRepository pTrMstr, PedidoLcDetRepository pTrDet, ArticuloService pParts,
                              TipoTransInvRepository pTypeCatalog, NotaVentaRepository pNotaVentaRepository,
                              ClienteRepository pClienteRepository
  ) {
    trInvMaster = pTrMstr
    clientMaster = pClienteRepository
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

  static void loadDetails( List<PedidoLc> pMaster ) {
    for ( PedidoLc tr in pMaster ) {
      tr.setLcDet( trInvDetail.findById( tr.id ) )
    }
  }

  static List<PedidoLc> detailToMaster( List<PedidoLcDet> pDetails ) {
    List<PedidoLc> mstrList = new ArrayList<PedidoLc>()
    for ( PedidoLcDet det in pDetails ) {
      List<PedidoLc> one = new ArrayList<>()
      QPedidoLc pedido = QPedidoLc.pedidoLc
      PedidoLc pedidoLc = trInvMaster.findOne( pedido.id.eq(det.id) )
      one.add( pedidoLc )
      PedidoLc mstr = ( one.size() > 0 ? one.get( 0 ) : null )
      if ( mstr != null ) {
        if ( !mstrList.contains( mstr ) ) {
          mstrList.add( mstr )
        }
      }
    }
    Collections.sort( mstrList, new Comparator<PedidoLc>() {
        @Override
        int compare(PedidoLc o1, PedidoLc o2) {
            return o1.id.compareTo(o2.id)
        }
    } )
    return mstrList
  }

  static List<PedidoLc> listarTransaccionesPorRangoFecha( Date pRangeStart, Date pRangeEnd ) {
    List<PedidoLc> selected = new ArrayList<>()
    List<PedidoLc> lstPedidos = trInvMaster.findByFechaAltaBetween( pRangeStart, pRangeEnd )
    if(lstPedidos.size() < 10){
      Calendar cal = Calendar.getInstance()
      cal.setTime(pRangeStart)
      cal.add(Calendar.DATE, -1);
      lstPedidos = trInvMaster.findByFechaAltaBetween( cal.getTime(), pRangeEnd )
    }
    for(PedidoLc pedido : lstPedidos){
      if( !pedido.id.startsWith("A") ){
        selected.add( pedido )
      }
    }
    loadDetails( selected )
    return selected
  }

  static List<PedidoLc> listarUltimasransacciones( ) {
    List<PedidoLc> selected = trInvMaster.findLastTen()
    return selected
  }

  static List<PedidoLc> listarTransaccionesPorIdPedido( String pIdTipoTrans ) {
    List<PedidoLc> selected = trInvMaster.findById( pIdTipoTrans )
    loadDetails( selected )
    return selected
  }

  static List<PedidoLc> listarTransaccionesPorSucursalDestino( String pSiteTo ) {
    List<PedidoLc> selected = trInvMaster.findBySucursal( pSiteTo )
    loadDetails( selected )
    return selected
  }

  static List<PedidoLc> listarTransaccionesPorCliente( String pCliente ) {
    QCliente cliente = QCliente.cliente
    String client = pCliente.toUpperCase()
    List<Cliente> details = clientMaster.findAll( cliente.nombre.like(client).
            or(cliente.apellidoPaterno.like(client)).or(cliente.apellidoMaterno.like(client)) )
    List<PedidoLc> selected = new ArrayList<>()
    for(Cliente cli : details){
      QPedidoLc pedidoLc = QPedidoLc.pedidoLc
      selected.addAll( trInvMaster.findAll(pedidoLc.cliente.eq(StringUtils.trimToEmpty(cli.id.toString())).
              and(pedidoLc.id.notLike('A'))) )
    }
    loadDetails( selected )
    return selected
  }

  static List<PedidoLc> listarTransaccionesPorArticulo( String pPartCodeSeed ) {
    Articulo articulo = partMaster.obtenerArticuloPorArticulo( pPartCodeSeed, false )
    List<PedidoLcDet> details = new ArrayList<PedidoLc>()
    List<Articulo> partList = new ArrayList<>()
    partList.add(articulo)
    if ( partList != null ) {
      List<Integer> skuList = buildSkuList( partList )
      QPedidoLcDet pedido = QPedidoLcDet.pedidoLcDet
      details = trInvDetail.findAll( pedido.modelo.eq(pPartCodeSeed.toUpperCase()).
              and(pedido.id.notLike("A")) )
    }
    List<PedidoLc> selected = detailToMaster( details )
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

  static Boolean esTipoTransaccionSalidaSucursal( String pIdTipoTrans ) {
     Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_SALIDA_ALMACEN )
     return p.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) )
  }

    static Boolean esTipoTransaccionEntrada( String pIdTipoTrans ) {
        Parametro p = Registry.find( TipoParametro.TRANS_INV_TIPO_ENTRADA_ALMACEN )
        Parametro p1 = Registry.find( TipoParametro.TRANS_INV_TIPO_RECIBE_REMISION )
        Boolean tipoEntrada = false
        if(p.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) ) || p1.valor.equalsIgnoreCase( StringUtils.trimToEmpty( pIdTipoTrans ) )){
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


  static List<PedidoLc> listarTransaccionesPorFolio( String pReferencia ) {
     QPedidoLc pedidoLc = QPedidoLc.pedidoLc
     List<PedidoLc> lstPedidosPorFolio = trInvMaster.findAll( pedidoLc.folio.eq(pReferencia).
             and(pedidoLc.id.notLike("A")) )
      loadDetails( lstPedidosPorFolio )
      return lstPedidosPorFolio
  }
}
