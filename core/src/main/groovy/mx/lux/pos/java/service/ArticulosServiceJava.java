package mx.lux.pos.java.service;


import com.mysema.query.types.Predicate;
import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.*;
import mx.lux.pos.repository.impl.RepositoryFactory;
import mx.lux.pos.service.business.Registry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ArticulosServiceJava {

  static final Logger log = LoggerFactory.getLogger(ArticulosServiceJava.class);

  private static final String TAG_GENERICO_H = "H";
  private static final String TAG_GENERICO_B = "B";

  public ArticulosJava obtenerArticulo(Integer id) throws ParseException {
    return obtenerArticulo( id, true );
  }


  public ArticulosJava obtenerArticulo( Integer id, boolean incluyePrecio ) throws ParseException {
    ArticulosJava articulo = null;
    log.info( String.format("obteniendo articulo con id: %d incluye precio: %s",id, incluyePrecio) );
    if( id != null ){
      articulo = ArticulosQuery.busquedaArticuloPorId(id);
      if ( articulo.getIdArticulo() != null && incluyePrecio ) {
        return establecerPrecio( articulo );
      }
    }
    return articulo;
  }


  public ArticulosJava establecerPrecio( ArticulosJava articulo ) {
    log.debug( "estableciendo precio para el articulo id: %d articulo: %s",articulo.getIdArticulo(), articulo.getArticulo() );
    if ( articulo.getIdArticulo() != null ) {
      Boolean hasOfert = false;
      log.debug( "obteniendo lista de precios" );
      List<PreciosJava> precios = PreciosQuery.buscaPreciosPorArticulo(articulo.getArticulo());
      if ( precios.size() > 0 ) {
        PreciosJava precioLista = null;
        PreciosJava precioOferta = null;
        for(PreciosJava precio : precios){
          if( "L".equalsIgnoreCase(StringUtils.trimToEmpty(precio.getLista())) ){
            precioLista = precio;
          }
        }
        BigDecimal lista = precioLista != null ? precioLista.getPrecio() : BigDecimal.ZERO;
        for(PreciosJava precioJava : precios){
          if("O".equalsIgnoreCase( StringUtils.trimToEmpty(precioJava.getLista()) ) && precioJava.getPrecio().compareTo(BigDecimal.ZERO) > 0){
            precioOferta = precioJava;
            hasOfert = true;
          }
        }
        BigDecimal oferta = precioOferta != null ? precioOferta.getPrecio() : BigDecimal.ZERO;
        if( hasOfert && oferta.compareTo(lista) < 0 ){
          articulo.setPrecio(oferta);
          articulo.setPrecioO(oferta);
        } else {
          articulo.setPrecio(lista);
        }
        log.debug( String.format("se establece precio %f para articulo id: %d", articulo.getPrecio(),articulo.getIdArticulo()) );
      }
    }
    log.debug( "Return articulo:: ${articulo.descripcion} " );
    return articulo;
  }


  public Boolean esLenteContacto( Integer id ) throws ParseException {
    Boolean esLc = false;
    ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(id);
    if( StringUtils.trimToEmpty(articulo.getIdGenerico()).equalsIgnoreCase(TAG_GENERICO_H) ){
      esLc = true;
    }
    return esLc;
  }


  public ModeloLcJava findLenteContacto( Integer id ) throws ParseException {
    ModeloLcJava modeloLc = new ModeloLcJava();
    ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(id);
    if( articulo != null ){
      modeloLc = ModeloLcQuery.buscaModeloLcPorIdModelo(StringUtils.trimToEmpty(articulo.getArticulo()));
    }
    return (modeloLc != null && modeloLc.getIdModelo() != null) ? modeloLc : null;
  }


  public PedidoLcJava buscaPedidoLc( String idPedido ){
    return PedidoLcQuery.buscaPedidoLcPorId( idPedido );
  }


  public NotaVentaJava actualizaCantidadLc( Integer cantidad, String modelo, String idFactura ) throws ParseException {
    NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(idFactura);
    if(notaVenta != null){
      for(DetalleNotaVentaJava det : notaVenta.getDetalles()){
        if(StringUtils.trimToEmpty(det.getArticulo().getArticulo()).equalsIgnoreCase(modelo)){
          det.setCantidadFac( cantidad.doubleValue());
          DetalleNotaVentaQuery.updateDetalleNotaVenta( det );
        }
      }
      return notaVenta;
    } else {
      return null;
    }
  }


  public Boolean validaCodigoDioptra( String codigo ) throws ParseException {
    Boolean valido = true;
    List<ArticulosJava> lstArticulos = ArticulosQuery.busquedaArticuloPorArticulo(codigo);
    ArticulosJava articulo = lstArticulos.size() > 0 ? lstArticulos.get(0) : null;
    if( articulo == null ){
      valido = false;
    }
    return valido;
  }


  public Boolean validarArticulo( Integer id ) throws ParseException {
    return ArticulosQuery.busquedaArticuloPorId(id) != null;
  }



  public Boolean registrarListaArticulos( List<ArticulosJava> pListaArticulo ) throws ParseException {
    boolean registrado = false;
    if ( ( pListaArticulo != null ) && ( pListaArticulo.size() > 0 ) ) {
      for(ArticulosJava articulosJava : pListaArticulo){
        ArticulosQuery.saveOrUpdateArticulos( articulosJava );
      }
      registrado = true;
    }
    return registrado;
  }


  public Boolean esInventariable( Integer id ) throws ParseException {
    boolean inventariable = false;
    ArticulosJava articulo = obtenerArticulo(id, false);
    if ( articulo != null ) {
      GenericosJava genre = GenericosQuery.buscaGenericosPorId( articulo.getIdGenerico() );
      inventariable = genre.getInventariable();
    }
    return inventariable;
  }



  public List<ArticulosJava> listarArticulosPorCodigoSimilar( String articulo, boolean incluyePrecio ) throws ParseException {
    log.info( "listando articulos con articulo similar: "+articulo );
    log.warn( "bien5" );
    List<ArticulosJava> lstArticulos = new ArrayList<ArticulosJava>();
    List<ArticulosJava> resultados = ArticulosQuery.busquedaArticuloPorArticuloParecido(articulo);
    if ( incluyePrecio ) {
      for(ArticulosJava art : resultados){
        lstArticulos.add(establecerPrecio( art ));
      }
      return lstArticulos;
    }
    return resultados;
  }


  public List<ArticulosJava> listarArticulosPorCodigo( String articulo, boolean incluyePrecio ) throws ParseException {
    log.info( "listando articulos con articulo: "+articulo );
    List<ArticulosJava> lstArticlos = new ArrayList<ArticulosJava>();
    List<ArticulosJava> resultados = ArticulosQuery.busquedaArticuloPorArticulo(articulo);
    if ( incluyePrecio ) {
      for(ArticulosJava art : resultados){
        lstArticlos.add(establecerPrecio( art ));
      }
      return lstArticlos;
    }
    return resultados;
  }


  public ArticulosJava listarArticulosPorSku( Integer idArticulo, boolean incluyePrecio ) throws ParseException {
    log.info( "listando articulos con sku: "+idArticulo );
    ArticulosJava resultados = ArticulosQuery.busquedaArticuloPorId(idArticulo);
    if ( incluyePrecio ) {
      return establecerPrecio( resultados );
    }
    return resultados;
  }


  public List<ArticulosJava> obtenerListaArticulosPorIdGenerico( String idGenerico ) throws ParseException {
    List<ArticulosJava> lstArticulos = new ArrayList<ArticulosJava>();
    List<ArticulosJava> resultados = ArticulosQuery.busquedaArticuloPorIdGenerico(idGenerico);
    for(ArticulosJava art : resultados){
      establecerPrecio( art );
    }
    return lstArticulos;
  }



  public List<ArticulosJava> obtenerListaArticulosPorDescripcion( String descripcion ) throws ParseException {
    QArticulo qArticulo = QArticulo.articulo1;
    List<ArticulosJava> lstArticulos = new ArrayList<ArticulosJava>();
    List<ArticulosJava> resultados = ArticulosQuery.busquedaArticuloPorDescripcion(descripcion);
    for(ArticulosJava art : resultados){
      lstArticulos.add(establecerPrecio(art));
    }
    return lstArticulos;
  }


  public Boolean validaUnSoloPaquete( List<Integer> lstIds, Integer idArticulo ) throws ParseException {
    log.debug( "validaUnSoloPaquete( )" );
    String paquetes = Registry.getPackages();
    String[] paquete = paquetes.split(",");
    Boolean esUnSoloPaq = true;
    Boolean esPaquete = false;
    Boolean existePaquete = false;
    ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(idArticulo);
    List<ArticulosJava> lstArticulo = new ArrayList<ArticulosJava>();
    for(Integer id : lstIds){
      ArticulosJava articulo1 = new ArticulosJava();
      articulo1 = ArticulosQuery.busquedaArticuloPorId( id );
      if(articulo1 != null){
        lstArticulo.add( articulo1 );
      }
    }
    if( articulo != null ){
      for(int i = 0;i<paquete.length;i++){
        if(paquete[i].equalsIgnoreCase(articulo.getArticulo().trim())){
          esPaquete = true;
        }
      }
    }
    for(ArticulosJava art : lstArticulo){
      for(int i=0;i<paquete.length;i++){
        if( paquete[i].equalsIgnoreCase(art.getArticulo().trim()) ){
          existePaquete = true;
        }
      }
    }
    if( esPaquete && existePaquete ){
      esUnSoloPaq = false;
    }
    return  esUnSoloPaq;
  }


  public Boolean validaUnSoloLente( List<Integer> lstIds, Integer idArticulo ) throws ParseException {
    log.debug( "validaUnSoloLente( )" );
    Boolean esUnSoloLente = true;
    Boolean esLente = false;
    Boolean existeLente = false;
    ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(idArticulo);
    List<ArticulosJava> lstArticulo = new ArrayList<ArticulosJava>();
    for(Integer id : lstIds){
      ArticulosJava articulo1 = new ArticulosJava();
      articulo1 = ArticulosQuery.busquedaArticuloPorId( id );
      if(articulo1 != null){
        lstArticulo.add( articulo1 );
      }
    }
    if( articulo != null ){
      if(StringUtils.trimToEmpty(articulo.getIdGenerico()).equalsIgnoreCase(TAG_GENERICO_B)){
        esLente = true;
      }
    }
    for(ArticulosJava art : lstArticulo){
      if( StringUtils.trimToEmpty(art.getIdGenerico()).equalsIgnoreCase(TAG_GENERICO_B) ){
        existeLente = true;
      }
    }
    if( esLente && existeLente ){
      esUnSoloLente = false;
    }
    return  esUnSoloLente;
  }


  public void updateLenteContacto( String idFactura ) throws ParseException {
    PedidoLcJava pedido = PedidoLcQuery.buscaPedidoLcPorId( idFactura );
    List<PedidoLcDetJava> lstPedidosDet = PedidoLcQuery.buscaPedidoLcDetPorIdPedido(idFactura);
    if( pedido != null && lstPedidosDet.size() > 0 ){
      PedidoLcQuery.eliminaPedidoLc(pedido);
      NotaVentaJava nv = NotaVentaQuery.busquedaNotaById(idFactura);
      pedido.setIdPedido(StringUtils.trimToEmpty(nv.getFactura()));
      pedido.setFechaEntrega(nv.getFechaEntrega());
      PedidoLcQuery.savePedidoLc( pedido );
      for(PedidoLcDetJava pedidoDet : lstPedidosDet){
        pedidoDet.setIdPedido(nv.getFactura());
        PedidoLcQuery.savePedidoLcDet( pedidoDet );
      }
    }
  }


}
