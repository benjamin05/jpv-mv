package mx.lux.pos.service.impl

import com.mysema.query.BooleanBuilder
import com.mysema.query.types.Predicate
import groovy.util.logging.Slf4j
import mx.lux.pos.java.querys.ArticulosQuery
import mx.lux.pos.java.repository.ArticulosJava
import mx.lux.pos.model.*
import mx.lux.pos.repository.ArticuloRepository
import mx.lux.pos.repository.DetalleNotaVentaRepository
import mx.lux.pos.repository.GenericoRepository
import mx.lux.pos.repository.MontoGarantiaRepository
import mx.lux.pos.repository.NotaVentaRepository
import mx.lux.pos.repository.PedidoLcDetRepository
import mx.lux.pos.repository.PedidoLcRepository
import mx.lux.pos.repository.PrecioRepository
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.repository.ModeloLcRepository
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.util.CustomDateUtils
import org.apache.commons.lang3.StringUtils
import org.apache.velocity.app.VelocityEngine
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.velocity.VelocityEngineUtils

import javax.annotation.Resource
import java.text.NumberFormat

@Slf4j
@Service( 'articuloService' )
@Transactional( readOnly = true )
class ArticuloServiceImpl implements ArticuloService {

  @Resource
  private ArticuloRepository articuloRepository

  @Resource
  private PrecioRepository precioRepository

  @Resource
  private PedidoLcRepository pedidoLcRepository

  @Resource
  private PedidoLcDetRepository pedidoLcDetRepository

  @Resource
  private NotaVentaRepository notaVentaRepository

  @Resource
  private DetalleNotaVentaRepository detalleNotaVentaRepository

  @Resource
  private ModeloLcRepository modeloLcRepository

  @Resource
  private MontoGarantiaRepository montoGarantiaRepository

  @Resource
  private GenericoRepository genericoRepository

  @Resource
  private VelocityEngine velocityEngine

  private static final Integer CANT_CARACTEREZ_SKU = 6
  private static final Integer CANT_CARACTEREZ_COD_BAR = 15
  private static final String TAG_SURTE_SUCURSAL = 'S'
  private static final String TAG_GENERICO_H = 'H'
  private static final String TAG_GENERICO_B = 'B'
  private static final String TAG_ID_GEN_TIPO_LC = 'NC'
  private static final String TAG_ARTICULOS_VIGENTES = 'V'

  private Articulo establecerPrecio( Articulo articulo ) {
     log.debug( "estableciendo precio para el articulo id: ${articulo?.id} articulo: ${articulo?.articulo}" )
    if ( articulo?.id ) {
       log.debug( "obteniendo lista de precios" )
      List<Precio> precios = precioRepository.findByArticulo( articulo.articulo )
      if ( precios?.any() ) {
        Precio precioLista = precios.find { Precio tmp ->
          'L'.equalsIgnoreCase( tmp?.lista )
        }
        BigDecimal lista = precioLista?.precio ?: 0
        Precio precioOferta = precios.find { Precio tmp ->
          'O'.equalsIgnoreCase( tmp?.lista )
        }
        BigDecimal oferta = precioOferta?.precio ?: 0
        log.debug( "precio lista valor: ${precioLista?.precio} id: ${precioLista?.id} lista: ${precioLista?.lista}" )
        log.debug( "precio oferta valor: ${precioOferta?.precio} id: ${precioOferta?.id} lista: ${precioOferta?.lista}" )
        articulo.precio = oferta && ( oferta < lista ) ? oferta : lista
        articulo.precioO = oferta && ( oferta < lista ) ? oferta : 0
        log.debug( "se establece precio ${articulo?.precio} para articulo id: ${articulo?.id}" )
      }
    }
    log.debug( "Return articulo:: ${articulo?.descripcion} " )
    return articulo
  }
    @Override
    Articulo findbyName(String dioptra){
        Articulo articulo = articuloRepository.findbyName(dioptra)
    }

  @Override
  Articulo obtenerArticulo( Integer id ) {
    return obtenerArticulo( id, true )
  }


  @Override
  Articulo obtenerArticulo( Integer id, boolean incluyePrecio ) {
    log.info( "obteniendo articulo con id: ${id} incluye precio: ${incluyePrecio}" )
    Articulo articulo = articuloRepository.findOne( id ?: 0 )
    if ( articulo?.id && incluyePrecio ) {
      return establecerPrecio( articulo )
    }
    return articulo
  }


    @Override
    Articulo obtenerArticuloPorArticulo( String article, boolean incluyePrecio ) {
        log.info( "obteniendo articulo : ${article} incluye precio: ${incluyePrecio}" )
        QArticulo art = QArticulo.articulo1
        List<Articulo> lstArticulos = articuloRepository.findAll( art.articulo.eq(article) )
        Articulo articulo = new Articulo()
        if( lstArticulos.size() > 0 ){
          articulo = lstArticulos.first()
        }
        if ( articulo?.id && incluyePrecio ) {
            return establecerPrecio( articulo )
        }
        return articulo
    }

  @Override
  List<Articulo> listarArticulosPorCodigo( String articulo ) {
    return listarArticulosPorCodigo( articulo, true )
  }

  @Override
  List<Articulo> listarArticulosPorCodigo( String articulo, boolean incluyePrecio ) {
    log.info( "listando articulos con articulo: ${articulo} incluye precio: ${incluyePrecio}" )
    Predicate predicate = QArticulo.articulo1.articulo.equalsIgnoreCase( articulo )
    List<Articulo> resultados = articuloRepository.findAll( predicate, QArticulo.articulo1.codigoColor.asc() ) as List<Articulo>
    if ( incluyePrecio ) {
      return resultados?.collect { Articulo tmp ->
        establecerPrecio( tmp )
      }
    }
    return resultados
  }


  @Override
  Articulo listarArticulosPorSku( Integer articulo, boolean incluyePrecio ) {
    log.info( "listando articulos con articulo: ${articulo} incluye precio: ${incluyePrecio}" )
    Predicate predicate = QArticulo.articulo1.id.eq( articulo )
    Articulo resultados = articuloRepository.findOne( predicate )
    if( resultados != null && !resultados.idGenerico.equalsIgnoreCase("H") ){
      QArticulo qArticulo = QArticulo.articulo1
      List<Articulo> lstArt = articuloRepository.findAll(qArticulo.articulo.eq(StringUtils.trimToEmpty(resultados.getArticulo()))) as List<Articulo>;
      if( lstArt.size() > 1 ){
        for(Articulo a : lstArt){
          if(resultados != null && a.id.equals(resultados.id)){
            if(StringUtils.trimToEmpty(resultados.codigoColor).length() <= 0){
              resultados = null;
            }
          }
        }
      }
    }
    if ( incluyePrecio ) {
      return establecerPrecio( resultados )
    }
    return resultados
  }


  @Override
  List<Articulo> listarArticulosPorCodigoSimilar( String articulo ) {
    return listarArticulosPorCodigoSimilar( articulo, true )
  }

  @Override
  List<Articulo> listarArticulosPorCodigoSimilar( String articulo, boolean incluyePrecio ) {
    log.info( "listando articulos con articulo similar: ${articulo}" )
      log.warn( "bien5" )
    Predicate predicate = QArticulo.articulo1.articulo.startsWithIgnoreCase( articulo )

    List<Articulo> resultados = articuloRepository.findAll( predicate, QArticulo.articulo1.articulo.asc() ) as List<Articulo>
    if ( incluyePrecio ) {
      return resultados?.collect { Articulo tmp ->
        establecerPrecio( tmp )
      }
    }
    return resultados
  }

  @Override
  Integer obtenerExistencia( Integer id ) {
    Articulo articulo = obtenerArticulo( id, false )
    return articulo?.cantExistencia ?: 0
  }

  @Override
  Boolean validarArticulo( Integer id ) {
    return articuloRepository.exists( id )
  }

  @Override
  Boolean validarArticuloSurte( DetalleNotaVenta detalle ) {
    return (TAG_SURTE_SUCURSAL.equalsIgnoreCase(detalle.surte )
            || (TAG_GENERICO_H.equalsIgnoreCase(StringUtils.trimToEmpty(detalle.articulo.idGenerico))
            && TAG_ID_GEN_TIPO_LC.equalsIgnoreCase(StringUtils.trimToEmpty(detalle.articulo.idGenTipo))))
  }

  @Override
  @Transactional
  Boolean registrarArticulo( Articulo pArticulo ) {
    if ( pArticulo != null ) {
      pArticulo = articuloRepository.save( pArticulo )
      return pArticulo?.id > 0
    }
    return false
  }

  @Override
  @Transactional
  Boolean registrarListaArticulos( List<Articulo> pListaArticulo ) {
    boolean registrado = false
    if ( ( pListaArticulo != null ) && ( pListaArticulo.size() > 0 ) ) {
      articuloRepository.save( pListaArticulo )
      articuloRepository.flush()
      registrado = true
    }
    return registrado
  }

  @Override
  Boolean esInventariable( Integer id ) {
    boolean inventariable = false
    Articulo articulo = obtenerArticulo( id, false )
    if ( articulo != null ) {
      Generico genre = RepositoryFactory.genres.findOne( articulo.idGenerico )
      inventariable = genre?.inventariable
    }
    return inventariable
  }

  @Override
  List<Articulo> obtenerListaArticulosPorId( List<Integer> pListaId ) {
    return articuloRepository.findByIdIn( pListaId )
  }

  @Override
  Boolean actualizarArticulosConSombra( Collection<ArticuloSombra> pShadowSet ) {
    log.debug( String.format( "[Service] Actualizar articulos: %,d en lista", pShadowSet.size() ) )
    Boolean actualizado = false
    try {
      List<Articulo> updatedList = new ArrayList<Articulo>()
      for ( ArticuloSombra shadow in pShadowSet ) {
        Articulo part = articuloRepository.findOne( shadow.id_articulo )
        if ( part != null ) {
          shadow.updateArticulo( part )
        } else {
          if ( shadow.isValidForNew() ) {
            part = shadow.createArticulo()
          }
        }
        if ( part != null ) {
          updatedList.add( part )
        }
      }
      actualizado = registrarListaArticulos( updatedList )
    } catch ( Exception e ) {
      log.error( "[Service] ERROR! Actualizando articulos", e )
    }
    return actualizado
  }

  Collection<Generico> listarGenericos( Collection<String> pIdGenericoSet ) {
    log.debug( "Listar Genericos(%d Ids)", pIdGenericoSet.size() )
    Collection<Generico> lista = new ArrayList<Generico>()
    if ( pIdGenericoSet.size() > 0 ) {
      lista = RepositoryFactory.genres.findByIdIn( pIdGenericoSet )
    }
    return lista
  }

  List<Articulo> findArticuloyColor( String articulo, String color ) {
    log.debug( "findArticuloyColor()" )

    List<Articulo> lstArticulos = new ArrayList<Articulo>()
    List<Articulo> lstArticulos2 = new ArrayList<Articulo>()
    Integer idArticulo = 0

     if ( !articulo.contains( "-" ) && !articulo.contains( "/" ) && !articulo.contains( "+" ) && !articulo.contains( "." ) && articulo.isNumber() ) {
      try{
        if( articulo.length() > CANT_CARACTEREZ_COD_BAR ){
          articulo = articulo.substring( 1 )
        }
          if( articulo.length() > CANT_CARACTEREZ_SKU ){
            idArticulo = Integer.parseInt( articulo.substring( 0, CANT_CARACTEREZ_SKU ) )
          } else {
            idArticulo = Integer.parseInt( articulo )
          }
      }catch ( Exception e ){
        log.error( "No se introdujo el SKU del articulo", e  )
      }
    }

    QArticulo art = QArticulo.articulo1
    lstArticulos2 = articuloRepository.findAll( art.id.eq( idArticulo ).or( art.articulo.eq( articulo ) ) ) as List
    if ( lstArticulos2.size() == 0 || lstArticulos2.size() > 1 ) {
      log.debug( "if de Articulos" )
      BooleanBuilder colour = new BooleanBuilder()
      if ( color.length() == 0 ) {
        colour.and( art.codigoColor.isNull() )
      } else {
        colour.and( art.codigoColor.eq( color ) )
      }
      lstArticulos2 = articuloRepository.findAll( art.id.eq( idArticulo ).or( art.articulo.eq( articulo ) ).and( colour ) ) as List
    }
    if ( lstArticulos2.size() > 0 ) {
      lstArticulos = lstArticulos2
    }

    return lstArticulos
  }


  String obtenerListaGenericosPrecioVariable( ) {
    return Registry.getManualPriceTypeList()
  }

  Boolean useShortItemDescription( ) {
    return Registry.isShortDescription()
  }


  Boolean generarArchivoInventario( ){
    log.debug( "generarArchivoInventario( )" )

    Parametro ubicacion = Registry.find( TipoParametro.RUTA_POR_ENVIAR )
    Parametro sucursal = Registry.find( TipoParametro.ID_SUCURSAL )
    String nombreFichero = "${ String.format("%02d", NumberFormat.getInstance().parse(sucursal.valor)) }.${ CustomDateUtils.format( new Date(), 'dd-MM-yyyy' ) }.${ CustomDateUtils.format( new Date(), 'HHmm' ) }.inv"
    log.info( "Generando archivo ${ nombreFichero }" )
    QArticulo articulo = QArticulo.articulo1
    List<Articulo> lstArticulos = articuloRepository.findAll( articulo.cantExistencia.ne( 0 ).and(articulo.cantExistencia.isNotNull()), articulo.id.asc() )
    def datos = [
        articulos:lstArticulos
    ]
    Boolean generado = true
    try{
      String fichero = "${ ubicacion.valor }/${ nombreFichero }"
      log.debug( "Generando Fichero: ${ fichero }" )
      log.debug( "Plantilla: fichero-inv.vm" )
      File file = new File( fichero )
      if ( file.exists() ) { file.delete() } // Borramos el fichero si ya existe para crearlo de nuevo
      log.debug( 'Creando Writer' )
      FileWriter writer = new FileWriter( file )
      datos.writer = writer
      log.debug( 'Merge template' )
      VelocityEngineUtils.mergeTemplate( velocityEngine, "template/fichero-inv.vm", "ASCII", datos, writer )
      log.debug( 'Writer close' )
      writer.close()

    }catch(Exception e){
      log.error( "Error al generar archivo de inventario", e )
      generado = false
    }
    return generado
  }


  Precio findPriceByArticle( Articulo articulo ){
    List<Precio> precios = precioRepository.findByArticulo( articulo.articulo )
    Precio precio = new Precio()
    if(precios.size() > 0){
      precio = precios.first()
    } else {
      precio = null
    }
    return precio
  }


  @Override
  Boolean validaUnSoloPaquete( List<Integer> lstIds, Integer idArticulo ){
    log.debug( "validaUnSoloPaquete( )" )
    String paquetes = Registry.packages
    String[] paquete = paquetes.split(',')
    Boolean esUnSoloPaq = true
    Boolean esPaquete = false
    Boolean existePaquete = false
    Articulo articulo = articuloRepository.findOne( idArticulo )
    List<Articulo> lstArticulo = new ArrayList<Articulo>()
    for(Integer id : lstIds){
      Articulo articulo1 = new Articulo()
      articulo1 = articuloRepository.findOne( id )
      if(articulo1 != null){
        lstArticulo.add( articulo1 )
      }
    }
    if( articulo != null ){
      for(int i = 0;i<paquete.length;i++){
        if(paquete[i].equalsIgnoreCase(articulo.articulo.trim())){
            esPaquete = true
        }
      }
    }
    for(Articulo art : lstArticulo){
      for(int i=0;i<paquete.length;i++){
        if( paquete[i].equalsIgnoreCase(art.articulo.trim()) ){
            existePaquete = true
        }
      }
    }
    if( esPaquete && existePaquete ){
      esUnSoloPaq = false
    }
    return  esUnSoloPaq
  }


    @Override
  Boolean validaUnSoloLente( List<Integer> lstIds, Integer idArticulo ){
    log.debug( "validaUnSoloLente( )" )
    Boolean esUnSoloLente = true
    Boolean esLente = false
    Boolean existeLente = false
    Articulo articulo = articuloRepository.findOne( idArticulo )
    List<Articulo> lstArticulo = new ArrayList<Articulo>()
    for(Integer id : lstIds){
      Articulo articulo1 = new Articulo()
      articulo1 = articuloRepository.findOne( id )
      if(articulo1 != null){
        lstArticulo.add( articulo1 )
      }
    }
    if( articulo != null ){
      if(StringUtils.trimToEmpty(articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_B)){
        esLente = true
      }
    }
    for(Articulo art : lstArticulo){
      if( StringUtils.trimToEmpty(art.idGenerico).equalsIgnoreCase(TAG_GENERICO_B) ){
        existeLente = true
      }
    }
    if( esLente && existeLente ){
      esUnSoloLente = false
    }
    return  esUnSoloLente
  }


  @Override
  Boolean esLenteContacto( Integer id ){
    Boolean esLc = false
    Articulo articulo = articuloRepository.findOne( id )
    if( StringUtils.trimToEmpty( articulo.idGenerico ).equalsIgnoreCase(TAG_GENERICO_H) ){
        esLc = true
    }
    return esLc
  }


  @Override
  ModeloLc findLenteContacto( Integer id ){
    ModeloLc modeloLc = new ModeloLc()
    Articulo articulo = articuloRepository.findOne( id )
    if( articulo != null ){
      modeloLc = modeloLcRepository.findOne( StringUtils.trimToEmpty(articulo.articulo))
    }
    return modeloLc && modeloLc.id != null ? modeloLc : null
  }


  @Override
  PedidoLc buscaPedidoLc( String idPedido ){
    PedidoLc pedidoLc = pedidoLcRepository.findOne( idPedido )
    return pedidoLc
  }

  @Override
  PedidoLcDet buscaPedidoLcDet( Integer idRegistro ){
    PedidoLcDet pedidoLcDet = pedidoLcDetRepository.findOne( idRegistro )
  }

  @Override
  List<PedidoLcDet> buscaPedidoLcDetPorId( String id ){
    QPedidoLcDet qPedidoLcDet = QPedidoLcDet.pedidoLcDet
    return pedidoLcDetRepository.findAll( qPedidoLcDet.id.eq(id) )
  }


  @Override
  void guardarPedidoLc( PedidoLc pedidoLc ){
      pedidoLcRepository.save( pedidoLc )
      pedidoLcRepository.flush()
  }


  @Override
  void guardarPedidoLcDet( PedidoLcDet pedidoLcDet ){
    pedidoLcDetRepository.save( pedidoLcDet )
    pedidoLcDetRepository.flush()
  }


  @Override
  void updateLenteContacto( String idFactura ){
    PedidoLc pedido = pedidoLcRepository.findOne( idFactura )
    QPedidoLcDet pedidoLcDet = QPedidoLcDet.pedidoLcDet
    List<PedidoLcDet> lstPedidosDet = pedidoLcDetRepository.findAll( pedidoLcDet.id.eq(idFactura) )
    if( pedido != null && lstPedidosDet.size() > 0 ){
      pedidoLcRepository.delete( idFactura )
      pedidoLcRepository.flush()
      NotaVenta nv = notaVentaRepository.findOne( idFactura )
      pedido.id = nv.factura
      pedido.fechaEntrega = nv.fechaEntrega
      pedidoLcRepository.save( pedido )
      pedidoLcRepository.flush()
      for(PedidoLcDet pedidoDet : lstPedidosDet){
        pedidoDet.id = nv.factura
        pedidoLcDetRepository.save( pedidoDet )
        pedidoLcDetRepository.flush()
      }
    }
  }


  @Override
  NotaVenta actualizaCantidadLc( Integer cantidad, String modelo, String idFactura ){
    NotaVenta notaVenta = notaVentaRepository.findOne( idFactura )
    if(notaVenta != null){
      for(DetalleNotaVenta det : notaVenta.detalles){
        if(StringUtils.trimToEmpty(det.articulo.articulo).equalsIgnoreCase(modelo)){
          det.cantidadFac = cantidad.doubleValue()
          detalleNotaVentaRepository.save( det )
          detalleNotaVentaRepository.flush()
        }
      }
      return notaVenta
    } else {
      return null
    }
  }



  @Override
  List<Articulo> obtenerListaArticulosPorIdGenerico( String idGenerico ){
    QArticulo qArticulo = QArticulo.articulo1
    List<Articulo> resultados = articuloRepository.findAll( qArticulo.idGenerico.eq(idGenerico).
            and(qArticulo.sArticulo.eq(TAG_ARTICULOS_VIGENTES)) ) as List<Articulo>
    return resultados?.collect { Articulo tmp ->
      establecerPrecio( tmp )
    }
  }



  @Override
  List<Articulo> obtenerListaArticulosPorDescripcion( String descripcion ){
    QArticulo qArticulo = QArticulo.articulo1
    List<Articulo> resultados = articuloRepository.findAll( qArticulo.descripcion.like("%"+descripcion+"%").
            and(qArticulo.sArticulo.eq(TAG_ARTICULOS_VIGENTES)) ) as List<Articulo>
    return resultados?.collect { Articulo tmp ->
      establecerPrecio( tmp )
    }
  }


  @Override
  Boolean validaCodigoDioptra( String codigo ){
    Boolean valido = true
    QArticulo qArticulo = QArticulo.articulo1
    Articulo articulo = articuloRepository.findOne( qArticulo.articulo.eq(codigo) )
    if( articulo == null ){
      valido = false
    }
    return valido
  }



  @Override
  List<Generico> genericos( ){
    return genericoRepository.findAll()
  }


  @Override
  MontoGarantia obtenerMontoGarantia( BigDecimal precioArt ){
    QMontoGarantia qMontoGarantia = QMontoGarantia.montoGarantia1
    return montoGarantiaRepository.findOne( qMontoGarantia.montoGarantia.eq(precioArt) )
  }


}
