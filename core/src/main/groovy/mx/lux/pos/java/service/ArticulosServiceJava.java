package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public class ArticulosServiceJava {

  static final Logger log = LoggerFactory.getLogger(ArticulosServiceJava.class);

  private static final String TAG_GENERICO_H = "H";

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


  private ArticulosJava establecerPrecio( ArticulosJava articulo ) {
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
          if("O".equalsIgnoreCase( StringUtils.trimToEmpty(precioJava.getLista()) )){
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
    ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorArticulo(codigo);
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

}
