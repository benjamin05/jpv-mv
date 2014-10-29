package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.model.Articulo
import mx.lux.pos.model.Generico
import mx.lux.pos.model.ModeloLc
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.model.PedidoLcDet
import mx.lux.pos.model.Precio
import mx.lux.pos.model.QArticulo
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.ModelLc
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.swing.*
import java.text.NumberFormat

@Slf4j
@Component
class ItemController {

  private static final String MSJ_ARCHIVO_GENERADO = 'El archivo de inventario fue generado correctamente en %s'
  private static final String TXT_ARCHIVO_GENERADO = 'Archivo de Inventario'
  private static final String MSJ_ARCHIVO_NO_GENERADO = 'No se genero correctamente el archivo de inventario'
  private static final String TAG_GENERICO_H = 'H'
  private static final String TAG_GEN_TIPO_C = 'C'
  private static ArticuloService articuloService

  @Autowired
  public ItemController( ArticuloService articuloService ) {
    this.articuloService = articuloService
  }

  static Item findItem( Integer id ) {
    log.debug( "obteniendo articulo con id: ${id}" )
    Item.toItem( articuloService.obtenerArticulo( id ) )
  }

  static List<Item> findItems( String code ) {
    log.debug( "buscando articulos con articulo: ${code}" )
    def results = articuloService.listarArticulosPorCodigo( code )
    results.collect {
      Item.toItem( it )
    }
  }

  static List<Item> findItemsLike( String input ) {
    log.debug( "buscando articulos con articulo similar a: $input" )
    def results = articuloService.listarArticulosPorCodigoSimilar( input )
    results.collect {
      Item.toItem( it )
    }
  }

  static List<Item> findItemsByQuery( final String query ) {
    log.debug( "buscando de articulos con query: $query" )
      if ( StringUtils.isNotBlank( query ) ) {
      List<Articulo> items = findPartsByQuery( query )
      if (items.size() > 0) {
        log.debug( "Items:: ${items.first()?.dump()} " )
        return items?.collect { Item.toItem( it ) }
      }
    }

    return [ ]
  }

  static List<Articulo> findPartsByQuery( final String query ) {
    return findPartsByQuery( query, true )
  }

  static List<Articulo> findPartsByQuery( final String query, Boolean incluyePrecio ) {
    List<Articulo> items = [ ]
    if ( StringUtils.isNotBlank( query ) ) {
      /*if ( query.integer ) {
        log.debug( "busqueda por articulo exacto ${query}" )
        items.add( articuloService.obtenerArticulo( query.toInteger(), incluyePrecio ) )
      } else {*/
        def anyMatch = '*'
        def colorMatch = ','
        def typeMatch = '+'
        if ( query.contains( anyMatch ) ) {
          def tokens = query.tokenize( anyMatch )
          def code = tokens?.first() ?: null
            log.warn( "bien3" )
            log.debug( "busqueda con codigo similar: ${code}" )

          items = articuloService.listarArticulosPorCodigoSimilar( code, incluyePrecio ) ?: [ ]
        } else {
          def tokens = query.replaceAll( /[+|,]/, '|' ).tokenize( '|' )
          def code = tokens?.first() ?: null
          log.debug( "busqueda con codigo exacto: ${code}" )
          items = articuloService.listarArticulosPorCodigo( code, incluyePrecio ) ?: [ ]
        }
        if ( query.contains( colorMatch ) ) {
          String color = query.find( /\,(\w+)/ ) { m, c -> return c }
          log.debug( "busqueda con color: ${color}" )
          items = items.findAll { it?.codigoColor?.equalsIgnoreCase( color ) ||
                  it?.idCb?.equalsIgnoreCase( color )}
        }
        if ( query.contains( typeMatch ) ) {
          if( query.startsWith( typeMatch ) ){
            String type = query.replace("+","")
            log.debug( "busqueda con tipo: ${type}" )
            items = [ ]
            items = articuloService.obtenerListaArticulosPorIdGenerico( type )
          } else if( query.startsWith( "D"+typeMatch ) ){
            String type = query.replace("D+","")
            log.debug( "busqueda con tipo: ${type}" )
            items = [ ]
            items = articuloService.obtenerListaArticulosPorDescripcion( type )
          } else {
            String type = query.find( /\+(\w+)/ ) { m, t -> return t }
            log.debug( "busqueda con tipo: ${type}" )
            items = items.findAll { it?.idGenerico?.equalsIgnoreCase( type ) }
          }
        }
      //}
    }
    return items
  }

    static List<Item> findItemByArticleAndColor( String query, String color  ) {
        log.debug( "buscando de un articulo con query: $query" )

        if ( StringUtils.isNotBlank( query ) ) {

            List<Articulo> items = new ArrayList<Articulo>()
            try{
            items = articuloService.findArticuloyColor( query, color )
            } catch( Exception e ){
                System.out.println( e )
            }
            return items?.collect { Item.toItem( it ) }
        }
        return [ ]
    }

  static String getManualPriceTypeList( ) {
    String list = articuloService.obtenerListaGenericosPrecioVariable()
    log.debug( "Determina la lista de Genericos precio variable: ${ list } " )
    return list
  }


  static void generateInventoryFile( ){
    log.debug( "generateInventoryFile( )" )
    Boolean archGenerado = articuloService.generarArchivoInventario()
    if( archGenerado ){
      JOptionPane.showMessageDialog( new JDialog(), String.format(MSJ_ARCHIVO_GENERADO, Registry.archivePath), TXT_ARCHIVO_GENERADO, JOptionPane.INFORMATION_MESSAGE )
    } else {
      JOptionPane.showMessageDialog( new JDialog(), MSJ_ARCHIVO_NO_GENERADO, TXT_ARCHIVO_GENERADO, JOptionPane.INFORMATION_MESSAGE )
    }

  }


  static Precio findPrice( Articulo articulo ){
     Precio precio = articuloService.findPriceByArticle( articulo )
     return precio
  }

  static Boolean esInventariable( Integer idArticulo ){
    Boolean esInventariable = articuloService.esInventariable( idArticulo )
    return esInventariable
  }



  static Item findItemsById( Integer idItem ) {
      log.debug( "buscando de articulos con id: $idItem" )
      Articulo items = articuloService.obtenerArticulo( idItem )
        if (items != null) {
            log.debug( "Item: ${items?.dump()} " )
            return Item.toItem( items )
        }
      return [ ]
  }


  static Articulo findArticle( Integer id ) {
    log.debug( "obteniendo articulo con id: ${id}" )
    return articuloService.obtenerArticulo( id )
  }

  static Boolean esLenteContacto( Integer idArticulo ){
    Boolean esLenteContacto = articuloService.esLenteContacto( idArticulo )
    return esLenteContacto
  }

  static ModelLc findLenteContacto( Integer idArticulo ){
    ModeloLc model = articuloService.findLenteContacto( idArticulo )
    if( model != null && model.id != null ){
      return ModelLc.toModelLc( model )
    } else {
      return null
    }
  }


  static Boolean validaRangosLc( String rango, String value ){
      Boolean esValido = false
      String[] rangos = rango.split(",")
      for(String valor : rangos){
        Double start = 0.00
        Double end = 0.00
        Double jump = 0.00
        Double insertado = 0.00
        String[] range = valor.split(":")
        if( range.length >= 3 ){
          try{
            start = NumberFormat.getInstance().parse(range[0]).doubleValue()
            end = NumberFormat.getInstance().parse(range[1]).doubleValue()
            jump = NumberFormat.getInstance().parse(range[2]).doubleValue()
            insertado = NumberFormat.getInstance().parse(value.replace("+","")).doubleValue()
            if( insertado >= start && insertado <= end ){
              if(validaValor( start, end, jump, insertado )){
                esValido = true
              }
            }
          } catch ( NumberFormatException e ) { println e }
        }
      }
    return esValido
  }


  static Boolean validaValor( Double start, Double end, Double jump, Double value ){
    Double range = start
      while ( range <= end ) {
        if( value == range ){
          return true
          break
        } else {
          range = range+jump
        }
      }
  }


    static Order saveRequest( String idOrder, String curva, String diametro, String esfera, String cilindro, String modelo,
                           String eje, String color, String quantity, Integer idCliente){
    PedidoLc pedido = articuloService.buscaPedidoLc( idOrder )
    if( pedido == null ){
      pedido = new PedidoLc()
      pedido.id = idOrder
      //pedido.folio = respuesta de liga
      pedido.cliente = idCliente.toString()
      pedido.sucursal = Registry.currentSite
      pedido.fechaAlta = new Date()
      articuloService.guardarPedidoLc( pedido )
    }

    Integer cantidad = 0
    try{
      cantidad = NumberFormat.getInstance().parse( quantity )
    } catch ( NumberFormatException e ){ println e }
    PedidoLcDet pedidoDet = new PedidoLcDet()
    pedidoDet.id = idOrder
    pedidoDet.curvaBase = curva
    pedidoDet.diametro = diametro
    pedidoDet.esfera = esfera
    pedidoDet.cilindro = cilindro
    pedidoDet.modelo = modelo
    pedidoDet.eje = eje
    pedidoDet.color = StringUtils.trimToEmpty(color).equalsIgnoreCase("null") ? "" : StringUtils.trimToEmpty(color)
    pedidoDet.cantidad = cantidad
    articuloService.guardarPedidoLcDet( pedidoDet )

    pedido = articuloService.buscaPedidoLc(idOrder)
    Integer quant = 0
    for(PedidoLcDet pedLcDet : pedido.pedidoLcDets){
      if(StringUtils.trimToEmpty(pedLcDet.modelo).equalsIgnoreCase(modelo)){
        quant = quant+pedLcDet.cantidad
      }
    }
    NotaVenta nota = new NotaVenta()
    if( quant > 0 ){
      nota = articuloService.actualizaCantidadLc( quant, modelo, idOrder )
    }
    return Order.toOrder( nota != null ? nota : new NotaVenta() )
  }

    static void updateRequest( String idOrder, String curva, String diametro, String esfera, String cilindro, String modelo,
                              String eje, String color, String quantity, Integer idCliente, Integer idRegistroPedido){
      PedidoLcDet pedidoLcDet = articuloService.buscaPedidoLcDet( idRegistroPedido )
      if( pedidoLcDet != null ){
        pedidoLcDet.curvaBase = curva
        pedidoLcDet.diametro = diametro
        pedidoLcDet.esfera = esfera
        pedidoLcDet.cilindro = cilindro
        pedidoLcDet.eje = eje
        pedidoLcDet.color = color
        articuloService.guardarPedidoLcDet( pedidoLcDet )
      }
    }


    static List<PedidoLcDet> findPedidoLcDetPorId( String idPedido ){
      articuloService.buscaPedidoLcDetPorId( idPedido )
    }


  static void updateLenteContacto( String idFactura ){
    articuloService.updateLenteContacto( idFactura )
  }



    static Boolean findLenteContactoStock( Integer idArticulo ){
       Articulo articulo = articuloService.obtenerArticulo( idArticulo )
        if( articulo != null ){
            return (StringUtils.trimToEmpty(articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_H)
            && StringUtils.trimToEmpty(articulo.idGenTipo).equalsIgnoreCase(TAG_GEN_TIPO_C))
        } else {
            return null
        }
    }



    static List<Articulo> findArticleByArticleAndColor( String query, String color  ) {
        log.debug( "buscando de un articulo con query: $query" )
        if ( StringUtils.isNotBlank( query ) ) {
            List<Articulo> items = new ArrayList<Articulo>()
            try{
                items = articuloService.findArticuloyColor( query, color )
            } catch( Exception e ){
                System.out.println( e )
            }
            return items
        }
        return [ ]
    }


  static List<Generico> generics(){
      return articuloService.genericos()
  }


}