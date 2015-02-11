package mx.lux.pos.service

import mx.lux.pos.model.Articulo
import mx.lux.pos.model.MontoGarantia
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.ArticuloSombra
import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.model.Generico
import mx.lux.pos.model.ModeloLc
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.model.PedidoLcDet
import mx.lux.pos.model.Precio

interface ArticuloService {

  Articulo obtenerArticulo( Integer id )

  Articulo obtenerArticulo( Integer id, boolean incluyePrecio )

  Articulo obtenerArticuloPorArticulo( String articulo, boolean incluyePrecio )

  List<Articulo> listarArticulosPorCodigo( String articulo )

  List<Articulo> listarArticulosPorCodigo( String articulo, boolean incluyePrecio )

  List<Articulo> listarArticulosPorCodigoSimilar( String articulo )

  List<Articulo> listarArticulosPorCodigoSimilar( String articulo, boolean incluyePrecio )

  Integer obtenerExistencia( Integer id )

  Boolean validarArticulo( Integer id )

  Boolean registrarArticulo( Articulo pArticulo )

  Boolean registrarListaArticulos( List<Articulo> pListaArticulo )

  Boolean esInventariable( Integer id )

  List<Articulo> obtenerListaArticulosPorId( List<Integer> pListaId )

  Boolean actualizarArticulosConSombra( Collection<ArticuloSombra> pShadowSet )

  Collection<Generico> listarGenericos( Collection<String> pIdGenericoSet )

  List<Articulo> findArticuloyColor( String articulo, String color )

  String obtenerListaGenericosPrecioVariable( )

  Boolean useShortItemDescription( )

  Boolean generarArchivoInventario( )

  Articulo findbyName(String dioptra)

  Precio findPriceByArticle( Articulo articulo )

  Boolean validaUnSoloPaquete( List<Integer> lstIds, Integer idArticulo )

  Boolean validaUnSoloLente( List<Integer> lstIds, Integer idArticulo )

  Boolean validarArticuloSurte( DetalleNotaVenta detalle )

  Boolean esLenteContacto( Integer id )

  ModeloLc findLenteContacto( Integer id )

  void guardarPedidoLc( PedidoLc pedidoLc )

  PedidoLc buscaPedidoLc( String idPedido )

  PedidoLcDet buscaPedidoLcDet( Integer idRegistro )

  List<PedidoLcDet> buscaPedidoLcDetPorId( String id )

  void guardarPedidoLcDet( PedidoLcDet pedidoLcDet )

  void updateLenteContacto( String idFactura )

  NotaVenta actualizaCantidadLc( Integer cantidad, String modelo, String idFactura )

  List<Articulo> obtenerListaArticulosPorIdGenerico( String idGenerico )

  List<Articulo> obtenerListaArticulosPorDescripcion( String descripcion )

  Boolean validaCodigoDioptra( String codigo )

  List<Generico> genericos()

  MontoGarantia obtenerMontoGarantia( BigDecimal precioArt )
}
