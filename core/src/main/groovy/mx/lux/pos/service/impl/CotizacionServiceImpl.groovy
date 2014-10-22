package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.model.*
import mx.lux.pos.repository.*
import mx.lux.pos.service.*
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.CotizacionService
import mx.lux.pos.service.business.Registry
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

@Slf4j
@Service( 'cotizacionService' )
@Transactional( readOnly = true )
class CotizacionServiceImpl implements CotizacionService {

  static final String TXT_QUOTE_SOLD = 'La cotización:%06d ya fue vendida.'
  static final String TXT_UNABLE_TO_FIND_QUOTE = 'La cotización:%d no está registrada.'
  private static final String TAG_ARTICULO_NO_VIGENTE = 'C'
  private static final String TAG_GENERICOS_INVENTARIABLES = 'A,E'

  @Resource
  private CotizacionRepository cotizacionRepository

  @Resource
  private CotizaDetRepository cotizaDetRepository

  @Resource
  private NotaVentaRepository notaVentaRepository

  @Resource
  private ConvenioRepository convenioRepository

  @Resource
  private CondicionIcGenerRepository condicionIcGenerRepository

  @Resource
  private ArticuloRepository articuloRepository

  @Resource
  private PrecioRepository precioRepository

  @Resource
  private RecetaRepository recetaRepository

  @Resource
  private ExamenService examenService



  @Override
  @Transactional
  Cotizacion registrarCotizacion( Cotizacion cotizacion ) {

    cotizacionRepository.save( cotizacion )

  }

  @Override
  @Transactional
  CotizaDet registrarCotizaDet( CotizaDet cotizaDet ) {

    cotizaDetRepository.save( cotizaDet )

  }

  @Override
  BigDecimal obtenerPrecio( Integer idArticulo, String conv, BigDecimal precioInicial ) {
    log.debug( "obtenerPrecioService" )

    BigDecimal precioFinal = BigDecimal.ZERO

    try {
      List<InstitucionIc> lstConvenio = convenioRepository.findById( conv )
      InstitucionIc convenio = lstConvenio.first()

      Articulo part = articuloRepository.findOne( idArticulo )

      Iterable<Precio> lstPrecios = precioRepository.findAll( QPrecio.precio1.articulo.eq( part.articulo ) )
      String precioArticulo = lstPrecios.first().precio

      QCondicionIcGener condiciones = QCondicionIcGener.condicionIcGener
      Iterable<CondicionIcGener> lstCondicion = condicionIcGenerRepository.findAll( condiciones.idGenerico.equalsIgnoreCase( articulos.idGenerico ).
          and( condiciones.id.equalsIgnoreCase( convenio.id ) ) )
      CondicionIcGener condicion = lstCondicion.first()
      BigDecimal descuento = lstCondicion.first().porcentajeDescto

      if ( convenio.tipoConvenio.equals( "G" ) ) {
        precioFinal = precioInicial.subtract( precioInicial.multiply( condicion.porcentajeDescto.divide( new BigDecimal( 100 ) ) ) )
      }

      if ( !convenio.tipoConvenio.equals( "G" ) ) {

        QPrecio price = QPrecio.precio1
        Precio precios = precioRepository.findAll( price.lista.eq( convenio.tipoConvenio )
            .and( price.articulo.eq( articulos.articulo ) ) )
        BigDecimal precio1 = precioInicial.subtract( precioInicial.multiply( condicion.porcentajeDescto.
            divide( new BigDecimal( 100 ) ) ) )

        if ( convenio.mejorPrecio ) {

          if ( precios.precio > 0 ) {
            precioFinal = precios.precio.min( precio1 )
          } else {
            precioFinal = precio1
          }

        } else {
          precioFinal = precios.precio
        }
      }
    } catch ( Exception e ) {
      log.error( "Error al obtener el precio del articulo:", e )
    }
    return precioFinal

  }

  @Transactional
  Integer copyFromOrder( String pOrderNbr, Integer pCustomerId, String pIdEmpleado ) {
    Cotizacion quote = null
      println('Prueba*1 '+ pOrderNbr)
    NotaVenta order = RepositoryFactory.orders.findOne( pOrderNbr )
      println('Prueba*2 '+ order.getId())
    if ( order != null ) {
      log.debug( String.format( 'Quote.copyFrom(Order:%s, Items:%d)', order.id, order.detalles.size() ) )
      Cliente cust = RepositoryFactory.customerCatalog.findOne( order.idCliente )
      quote = this.createQuote()
      quote.idCliente = pCustomerId
      quote.idEmpleado = pIdEmpleado
      quote.idReceta = order.receta
      quote.fechaCotizacion = new Date()
      if( order.receta != null ){
        QReceta rx = QReceta.receta
        Receta receta = recetaRepository.findOne(rx.id.eq(order.receta))
        if(receta != null){
          Examen examen = examenService.obtenerExamenPorIdCliente( order.idCliente )
          examen.setTipoOft( "CO" )
        }
      }
      if ( cust != null ) {
        quote.nombre = cust.nombreCompleto
        String telefono = ''
        if ( StringUtils.trimToNull( cust.telefonoCasa ) != null ) {
          telefono = ", "+"C:"+cust.telefonoCasa;
        }
        if ( StringUtils.trimToNull( cust.telefonoTrabajo ) != null ) {
          telefono = telefono+", "+cust.telefonoTrabajo
        }
        if ( StringUtils.trimToNull( cust.telefonoAdicional ) != null ) {
          telefono = telefono+", "+"M:"+cust.telefonoAdicional
        }
        telefono = telefono.replaceFirst(", ","")
        quote.tel = telefono
        quote.titulo = cust.titulo
        quote.observaciones = order.observacionesNv
      }
      try {
        quote = RepositoryFactory.quotes.saveAndFlush( quote )
        for ( DetalleNotaVenta orderLine : order.detalles ) {
          CotizaDet quoteItem = this.createQuoteItem( quote, orderLine.articulo )
          quoteItem.cantidad = orderLine.cantidadFac
          try {
            RepositoryFactory.quoteDetail.save( quoteItem )
          } catch ( Exception e ) {
            log.error( e.getMessage() )
            log.error( quoteItem.toString() )
          }
        }
      } catch ( Exception e ) {
        log.error( e.getMessage() )
        log.error( quote.toString() )
      }
    } else {
      log.error( String.format( 'Unable to quote Order:%s' ), pOrderNbr )
    }
    return quote.idCotiza
  }

  Cotizacion createQuote( ) {
    Cotizacion quote = new Cotizacion();
    quote.idSucursal = Registry.currentSite
    quote.fechaMod = new Date()
    return quote
  }

  CotizaDet createQuoteItem( Cotizacion pQuote, Articulo pItem ) {
    CotizaDet quoteItem = new CotizaDet();
    quoteItem.idCotiza = pQuote.idCotiza
    quoteItem.sku = pItem.id
    quoteItem.idSucursal = pQuote.idSucursal
    quoteItem.fechaMod = new Date()
    quoteItem.articulo = pItem.articulo
    quoteItem.color = pItem.codigoColor
    return quoteItem
  }

  @Transactional
  Map<String, Object> toOrder( Integer pQuoteId ) {
    Map<String, Object> status = new TreeMap<String, Object>()
    Cotizacion quote = RepositoryFactory.quotes.findOne( pQuoteId )
    if ( quote != null ) {
      if(StringUtils.trimToEmpty(quote.idFactura) == '' ){
      if ( this.isQuoteOpen( quote.idCotiza ) ) {
        NotaVenta order = ServiceFactory.salesOrders.abrirNotaVenta(quote.idCliente.toString(), quote.idSucursal.toString())
        order.idCliente = quote.idCliente
        order.idEmpleado = quote.idEmpleado
        order.receta = quote.idReceta
        ServiceFactory.salesOrders.registrarNotaVenta( order )
        for ( CotizaDet quoteItem : quote.cotizaDet ) {
          Articulo item = ServiceFactory.partMaster.obtenerArticulo( quoteItem.sku )
          if( !item.sArticulo.equalsIgnoreCase(TAG_ARTICULO_NO_VIGENTE) ){
              String surte = 'S'
              if(!TAG_GENERICOS_INVENTARIABLES.contains(item.idGenerico)){
                  surte = ' '
              }
              DetalleNotaVenta orderLine = new DetalleNotaVenta(
                      idArticulo: item.id,
                      cantidadFac: 1,
                      precioUnitLista: item.precio,
                      precioUnitFinal: item.precio,
                      precioCalcLista: item.precio,
                      precioFactura: item.precio,
                      precioCalcOferta: 0,
                      precioConv: 0,
                      idTipoDetalle: 'N',
                      surte: surte
              )
              ServiceFactory.salesOrders.registrarDetalleNotaVentaEnNotaVenta( order.id, orderLine )
          }
        }
        quote.idFactura = StringUtils.trimToEmpty( order.factura )
        quote.fechaVenta = StringUtils.trimToEmpty( order.factura ).trim().length() > 0 ? new Date() : null
        status.put( 'orderNbr', order.id )
      } else {
        String msg = String.format( TXT_QUOTE_SOLD, quote.idCotiza, quote.idFactura, quote.fechaVenta )
        status.put( 'statusMessage', msg )
      }
      } else {
        status.put( 'statusMessage', String.format(TXT_QUOTE_SOLD, pQuoteId) )
      }
    } else {
      status.put( 'statusMessage', String.format(TXT_UNABLE_TO_FIND_QUOTE, pQuoteId) )
    }
    return status
  }

  Boolean isQuoteOpen( Integer pQuoteNbr ) {
    Boolean open = false
    Cotizacion quote = RepositoryFactory.quotes.findOne( pQuoteNbr )
    if ( quote != null ) {
      if ( StringUtils.trimToNull( quote.idFactura ) == null ) {
        open = true
      } else {
        NotaVenta order = RepositoryFactory.orders.findOne( quote.idFactura )
        if ( order != null ) {
          open = StringUtils.isBlank( order.factura )
        } else {
          open = true
        }
      }
    }
    return open
  }


  void updateQuote( String idFactura, Integer numQuote ){
    Cotizacion cotizacion = cotizacionRepository.findOne( numQuote )
    NotaVenta nota = notaVentaRepository.findOne( idFactura )
    if(nota != null && StringUtils.trimToEmpty(nota.factura).length() > 0){
      cotizacion.idFactura = nota.factura.trim()
      cotizacion.fechaVenta = new Date()
      cotizacionRepository.save( cotizacion )
      cotizacionRepository.flush()
    }
  }


  Cotizacion obtenerCotizacion( Integer idCotizacion ){
      return RepositoryFactory.quotes.findOne( idCotizacion )
  }
}
