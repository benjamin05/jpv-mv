package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.java.querys.ArticulosQuery
import mx.lux.pos.java.querys.DetalleNotaVentaQuery
import mx.lux.pos.java.querys.NotaVentaQuery
import mx.lux.pos.java.querys.PedidoLcQuery
import mx.lux.pos.java.querys.TransInvQuery
import mx.lux.pos.java.repository.DetalleNotaVentaJava
import mx.lux.pos.java.repository.ModeloLcJava
import mx.lux.pos.java.repository.NotaVentaJava
import mx.lux.pos.java.repository.PagoJava
import mx.lux.pos.java.repository.PedidoLcDetJava
import mx.lux.pos.java.repository.PedidoLcJava
import mx.lux.pos.java.repository.TransInvJava
import mx.lux.pos.java.service.NotaVentaServiceJava
import mx.lux.pos.java.service.TransInvServiceJava
import mx.lux.pos.model.Articulo
import mx.lux.pos.model.Generico
import mx.lux.pos.model.ModeloLc
import mx.lux.pos.model.MontoGarantia
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.model.PedidoLc
import mx.lux.pos.model.PedidoLcDet
import mx.lux.pos.model.Precio
import mx.lux.pos.java.querys.MontoGarantiaQuery
import mx.lux.pos.java.repository.ArticulosJava
import mx.lux.pos.java.repository.MontoGarantiaJava
import mx.lux.pos.service.ArticuloService
import mx.lux.pos.java.service.ArticulosServiceJava
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.service.TicketService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.ui.model.Item
import mx.lux.pos.ui.model.Order
import mx.lux.pos.ui.model.ModelLc
import mx.lux.pos.ui.model.User
import mx.lux.pos.ui.model.OrderItem
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.swing.*
import java.text.NumberFormat
import java.text.SimpleDateFormat

@Slf4j
@Component
class ItemController {

  private static final String MSJ_ARCHIVO_GENERADO = 'El archivo de inventario fue generado correctamente en %s'
  private static final String TXT_ARCHIVO_GENERADO = 'Archivo de Inventario'
  private static final String MSJ_ARCHIVO_NO_GENERADO = 'No se genero correctamente el archivo de inventario'
  private static final String TAG_GENERICO_H = 'H'
  private static final String TAG_GENERICO_A = 'A'
  private static final String TAG_GEN_TIPO_C = 'C'
  private static final String TAG_SURTE_PINO = 'P'
  private static final String TAG_TIPO_PAGO_TRANSFERENCIA = 'TR'
  private static ArticuloService articuloService
  private static ArticulosServiceJava articulosServiceJava
  private static TicketService ticketService
  private static NotaVentaService notaVentaService
  private static NotaVentaServiceJava notaVentaServiceJava
  private static TransInvServiceJava transInvServiceJava

  @Autowired
  public ItemController( ArticuloService articuloService, TicketService ticketService, NotaVentaService notaVentaService ) {
    this.articuloService = articuloService
    this.ticketService = ticketService
    this.notaVentaService = notaVentaService
    articulosServiceJava = new ArticulosServiceJava()
    notaVentaServiceJava = new NotaVentaServiceJava()
    transInvServiceJava = new TransInvServiceJava()
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
      List<ArticulosJava> items = findPartsJavaByQuery( query )
      if (items.size() > 0) {
        log.debug( "Items:: ${items.first()?.dump()} " )
        return items?.collect { Item.toItem( it ) }
      }
    }
    return [ ]
  }

  static List<ArticulosJava> findPartsJavaByQuery( final String query ) {
    return findPartsJavaByQuery( query, true )
  }

  static List<ArticulosJava> findPartsJavaByQuery( final String query, Boolean incluyePrecio ) {
    List<ArticulosJava> items = [ ]
    if ( StringUtils.isNotBlank( query ) ) {
      def anyMatch = '*'
      def colorMatch = ','
      def typeMatch = '+'
      if ( query.contains( anyMatch ) ) {
        def tokens = query.tokenize( anyMatch )
        def code = tokens?.first() ?: null
        log.warn( "bien3" )
        log.debug( "busqueda con codigo similar: ${code}" )
        items = articulosServiceJava.listarArticulosPorCodigoSimilar( code, incluyePrecio ) ?: [ ]
      } else {
        def tokens = query.replaceAll( /[+|,]/, '|' ).tokenize( '|' )
        def code = tokens?.first() ?: null
        log.debug( "busqueda con codigo exacto: ${code}" )
        items = articulosServiceJava.listarArticulosPorCodigo( code, incluyePrecio ) ?: [ ]
        if( items.empty && code.isNumber() ){
          Integer sku = 0
          try {
            sku = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(code)).intValue()
          } catch ( NumberFormatException e ){
            println( e )
          }
          items.add(articulosServiceJava.listarArticulosPorSku( sku, incluyePrecio ))
        }
      }
      if ( query.contains( colorMatch ) ) {
        String color = query.find( /\,(\w+)/ ) { m, c -> return c }
        log.debug( "busqueda con color: ${color}" )
        items = items.findAll { it?.colorCode?.equalsIgnoreCase( color ) ||
                  it?.idCb?.equalsIgnoreCase( color )}
      }
      if ( query.contains( typeMatch ) ) {
        if( query.startsWith( typeMatch ) ){
          String type = query.replace("+","")
          log.debug( "busqueda con tipo: ${type}" )
          items = [ ]
          items = articulosServiceJava.obtenerListaArticulosPorIdGenerico( type )
        } else if( query.startsWith( "D"+typeMatch ) ){
          String type = query.replace("D+","")
          log.debug( "busqueda con tipo: ${type}" )
          items = [ ]
          items = articulosServiceJava.obtenerListaArticulosPorDescripcion( type )
        } else {
          String type = query.find( /\+(\w+)/ ) { m, t -> return t }
          log.debug( "busqueda con tipo: ${type}" )
          items = items.findAll { it?.idGenerico?.equalsIgnoreCase( type ) }
        }
      }
    }
    return items
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
                if( items.empty && code.isNumber() ){
                  Integer sku = 0
                  try {
                    sku = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(code)).intValue()
                  } catch ( NumberFormatException e ){
                    println( e )
                  }
                  items.add(articuloService.listarArticulosPorSku( sku, incluyePrecio ))
                }
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
    Boolean esInventariable = articulosServiceJava.esInventariable( idArticulo )
    return esInventariable
  }



  static Item findItemsById( Integer idItem ) {
    log.debug( "buscando de articulos con id: $idItem" )
    //Articulo items = articuloService.obtenerArticulo( idItem )
    ArticulosJava items = articulosServiceJava.obtenerArticulo( idItem )
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

  static ArticulosJava findArticleJava( Integer id ) {
    log.debug( "obteniendo articulo con id: ${id}" )
    return ArticulosQuery.busquedaArticuloPorId( id )
  }

  static Boolean esLenteContacto( Integer idArticulo ){
    Boolean esLenteContacto = articulosServiceJava.esLenteContacto( idArticulo )
    return esLenteContacto
  }

  static ModelLc findLenteContacto( Integer idArticulo ){
    ModeloLcJava model = articulosServiceJava.findLenteContacto( idArticulo )
    if( model != null && model.idModelo != null ){
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
    PedidoLcJava pedido = articulosServiceJava.buscaPedidoLc( idOrder )
    if( pedido == null ){
      pedido = new PedidoLcJava()
      pedido.idPedido = idOrder
      //pedido.folio = respuesta de liga
      pedido.cliente = idCliente.toString()
      pedido.sucursal = Registry.currentSite
      pedido.fechaAlta = new Date()
      PedidoLcQuery.savePedidoLc( pedido )
    }

    Integer cantidad = 0
    try{
      cantidad = NumberFormat.getInstance().parse( quantity )
    } catch ( NumberFormatException e ){ println e }
    PedidoLcDetJava pedidoDet = new PedidoLcDetJava()
    pedidoDet.idPedido = idOrder
    pedidoDet.curvaBase = curva
    pedidoDet.diametro = diametro
    pedidoDet.esfera = esfera
    pedidoDet.cilindro = cilindro
    pedidoDet.modelo = modelo
    pedidoDet.eje = eje
    pedidoDet.color = StringUtils.trimToEmpty(color).equalsIgnoreCase("null") ? "" : StringUtils.trimToEmpty(color)
    pedidoDet.cantidad = cantidad
    PedidoLcQuery.savePedidoLcDet( pedidoDet )

    pedido = articulosServiceJava.buscaPedidoLc(idOrder)
    Integer quant = 0
    for(PedidoLcDetJava pedLcDet : pedido.pedidoLcDets){
      if(StringUtils.trimToEmpty(pedLcDet.modelo).equalsIgnoreCase(modelo)){
        quant = quant+pedLcDet.cantidad
      }
    }
    NotaVentaJava nota = new NotaVentaJava()
    if( quant > 0 ){
      nota = articulosServiceJava.actualizaCantidadLc( quant, modelo, idOrder )
    }
    return Order.toOrder( nota != null ? nota : new NotaVentaJava() )
  }

    static void updateRequest( String idOrder, String curva, String diametro, String esfera, String cilindro, String modelo,
                              String eje, String color, String quantity, Integer idCliente, Integer idRegistroPedido){
      PedidoLcDetJava pedidoLcDet = PedidoLcQuery.buscaPedidoLcDetPorId( idRegistroPedido )
      if( pedidoLcDet != null ){
        pedidoLcDet.curvaBase = curva
        pedidoLcDet.diametro = diametro
        pedidoLcDet.esfera = esfera
        pedidoLcDet.cilindro = cilindro
        pedidoLcDet.eje = eje
        pedidoLcDet.color = color
        PedidoLcQuery.updatePedidoLcDet( pedidoLcDet )
      }
    }


    static List<PedidoLcDet> findPedidoLcDetPorId( String idPedido ){
      articuloService.buscaPedidoLcDetPorId( idPedido )
    }


  static void updateLenteContacto( String idFactura ){
    articulosServiceJava.updateLenteContacto( idFactura )
  }



  static Boolean findLenteContactoStock( Integer idArticulo ){
    ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId( idArticulo )
    if( articulo != null ){
      return (StringUtils.trimToEmpty(articulo.idGenerico).equalsIgnoreCase(TAG_GENERICO_H)
      && StringUtils.trimToEmpty(articulo.tipo).equalsIgnoreCase(TAG_GEN_TIPO_C))
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



  static BigDecimal warrantyValid( BigDecimal priceItem, Integer idWarranty ){
    BigDecimal warrantyAmount = BigDecimal.ZERO
    Articulo warranty = articuloService.obtenerArticulo( idWarranty, true )
    if( warranty != null ){
      MontoGarantia montoGarantia = articuloService.obtenerMontoGarantia( warranty.precio )
      if( montoGarantia != null ){
        if( montoGarantia.montoGarantia.compareTo(BigDecimal.ZERO) == 0 ){
          warrantyAmount = new BigDecimal(100)
        } else if(montoGarantia.montoMinimo.compareTo(priceItem) <= 0
                && montoGarantia.montoMaximo.compareTo(priceItem) >= 0 ){
          warrantyAmount = montoGarantia.montoGarantia
        }
      }
    }
    return warrantyAmount
  }


  static BigDecimal warrantyValidJava( BigDecimal priceItem, Integer idWarranty ){
    BigDecimal warrantyAmount = BigDecimal.ZERO
    ArticulosJava warranty = articulosServiceJava.obtenerArticulo( idWarranty, true )
    if( warranty != null ){
      MontoGarantiaJava montoGarantia = MontoGarantiaQuery.buscaMontoGarantiaPorMontoGarantia( warranty.precio )
      if( montoGarantia != null ){
        if( montoGarantia.montoGarantia.compareTo(BigDecimal.ZERO) == 0 ){
          warrantyAmount = new BigDecimal(100)
        } else if(montoGarantia.montoMinimo.compareTo(priceItem) <= 0
                && montoGarantia.montoMaximo.compareTo(priceItem) >= 0 ){
          warrantyAmount = montoGarantia.montoGarantia
        }
      }
    }
    return warrantyAmount
  }


  static void printWarranty( BigDecimal amount, String idItem, String typeWarranty, String idFactura, Boolean doubleEnsure ){
    NotaVentaJava nota = ticketService.imprimeGarantia( amount, idItem, typeWarranty, idFactura, doubleEnsure )
    if( nota != null ){
      NotaVentaQuery.updateNotaVenta( nota )
    }
  }


  static MontoGarantia findWarranty( BigDecimal warrantyAmount ){
    return articuloService.obtenerMontoGarantia( warrantyAmount )
  }



  static Integer calculateStock( Integer sku ){
    return articulosServiceJava.calculaExistencia( sku )
  }


  static Boolean updateStock( Integer idArticulo, Integer stock, User user ){
    Boolean update = false
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy")
    ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId( idArticulo )
    TransInvJava trans = transInvServiceJava.obtieneUltimaTransaccionPorIdArticulo( idArticulo )
    if( articulo != null ){
      articulo.existencia = stock
      ArticulosQuery.saveOrUpdateArticulos( articulo )
      if( trans != null ){
        trans.observaciones = StringUtils.trimToEmpty(trans.observaciones)+"|${StringUtils.trimToEmpty(idArticulo.toString())}|" +
                  "${StringUtils.trimToEmpty(user.username)}|${df.format(new Date())}|rec"
        TransInvQuery.saveOrUpdateTransInv( trans )
      }
      update = true
    }
    return update
  }


  static Item findFrameWithoutColor( Order order ) {
    log.debug( "findFrameWithoutColor" )
    Item item = null
    for(OrderItem i : order.items){
      if( TAG_GENERICO_A.equalsIgnoreCase(StringUtils.trimToEmpty(i.item.type)) &&
              StringUtils.trimToEmpty(i.item.color).length() <= 0 ){
        item = i.item
      }
    }
    return item
  }


  static void validTransSurtePino( String idOrder ){
    NotaVentaJava notaVentaJava = NotaVentaQuery.busquedaNotaById( idOrder )
    String oldOrder = ""
    Boolean cambiarSurte = false
    for(PagoJava pagoJava : notaVentaJava.pagos){
      if( StringUtils.trimToEmpty(pagoJava.idFPago).equalsIgnoreCase(TAG_TIPO_PAGO_TRANSFERENCIA) ){
        String[] data = StringUtils.trimToEmpty(pagoJava.refClave).split(":")
        oldOrder = StringUtils.trimToEmpty(data[0].length() > 0 ? data[0] : "")
      }
    }
    if( StringUtils.trimToEmpty(oldOrder).length() > 0 ){
      Integer frame = null
      for(DetalleNotaVentaJava det : notaVentaJava.detalles){
        if( StringUtils.trimToEmpty(det?.articulo?.idGenerico).equalsIgnoreCase(TAG_GENERICO_A) &&
                StringUtils.trimToEmpty(det?.surte).equalsIgnoreCase(TAG_SURTE_PINO) ){
          frame = det?.idArticulo
        }
      }
      if( frame != null ){
        NotaVentaJava originOrder = NotaVentaQuery.busquedaNotaById(oldOrder)
        for(DetalleNotaVentaJava det1 : originOrder.detalles){
          if( det1.idArticulo == frame ){
            cambiarSurte = true
          }
        }
        if( cambiarSurte ){
          for(DetalleNotaVentaJava det : notaVentaJava.detalles){
            if( det?.articulo?.idArticulo == frame ){
              det?.surte = 'S'
              DetalleNotaVentaQuery.updateDetalleNotaVenta( det )
            }
          }
        }
      }
    }
  }

  static Boolean hasSameFrame( String oldOrder, String newOrder ){
    Boolean hasSameFrame = false
    NotaVentaJava oldNota = NotaVentaQuery.busquedaNotaById(oldOrder)
    NotaVentaJava newNota = NotaVentaQuery.busquedaNotaById(newOrder)
    if( oldNota != null && newNota != null ){
      String oldFrames = ""
      String newFrames = ""
      for(DetalleNotaVentaJava det : oldNota.detalles){
        if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase("A") ){
          oldFrames = oldFrames+","+StringUtils.trimToEmpty(det.idArticulo.toString())
        }
      }
      for(DetalleNotaVentaJava det : newNota.detalles){
        if( StringUtils.trimToEmpty(det.articulo.idGenerico).equalsIgnoreCase("A") ){
          if(oldFrames.contains(StringUtils.trimToEmpty(det.idArticulo.toString()))){
            hasSameFrame = true
          }
        }
      }
    }
    return hasSameFrame
  }


}
