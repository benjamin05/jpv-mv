package mx.lux.pos.service;


import mx.lux.pos.model.Articulo;
import mx.lux.pos.model.Precio;
import mx.lux.pos.querys.ArticulosQuery;
import mx.lux.pos.querys.PreciosQuery;
import mx.lux.pos.repository.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public class ArticulosServiceJava {

  static final Logger log = LoggerFactory.getLogger(ArticulosServiceJava.class);

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


}
