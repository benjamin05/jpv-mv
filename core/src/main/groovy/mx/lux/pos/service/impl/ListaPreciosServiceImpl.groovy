package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.model.*
import mx.lux.pos.repository.*
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.ListaPreciosService
import mx.lux.pos.service.business.Registry
import mx.lux.pos.util.CustomDateUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
import javax.sql.DataSource

@Slf4j
@Service( 'listaPreciosService' )
@Transactional( readOnly = true )
class ListaPreciosServiceImpl implements ListaPreciosService {

  @Resource
  private ListaPreciosRepository listaPreciosRepository

  @Resource
  private ParametroRepository parametroRepository

  @Resource
  private SucursalRepository sucursalRepository

  @Resource
  private ArticuloRepository articuloRepository

  @Resource
  private PrecioRepository precioRepository

  @Resource
  private DataSource invDataSource

  private static final String TAG_ACK_RECEIVED = 'recibe_lp'
  private static final String TAG_ACK_LOAD = 'carga_lp'

  @Override
  String obtenRutaPorRecibir( ) {
    log.debug( "obteniendo ruta por recibir" )
    def parametro = parametroRepository.findOne( TipoParametro.RUTA_POR_RECIBIR.value )
    log.debug( "ruta por recibir: ${parametro?.valor}" )
    return parametro?.valor
  }

  @Override
  String obtenRutaListaPrecios( ) {
    log.debug( "obteniendo ruta lista de precios" )
    def parametro = parametroRepository.findOne( TipoParametro.RUTA_LISTA_PRECIOS.value )
    log.debug( "ruta lista de precios: ${parametro?.valor}" )
    return parametro?.valor
  }

  @Override
  String obtenRutaRecibidos( ) {
    log.debug( "obteniendo ruta recibidos" )
    def parametro = parametroRepository.findOne( TipoParametro.RUTA_RECIBIDOS.value )
    log.debug( "ruta recibidos: ${parametro?.valor}" )
    return parametro?.valor
  }

  @Override
  @Transactional
  ListaPrecios registrarListaPrecios( ListaPrecios listaPrecios ) {
    log.debug( "registrando lista precios: ${listaPrecios?.id}, ${listaPrecios?.filename}" )
    if ( StringUtils.isNotBlank( listaPrecios?.id ) && StringUtils.isNotBlank( listaPrecios?.filename ) ) {
      listaPrecios = listaPreciosRepository.save( listaPrecios )
      if ( listaPrecios?.id ) {
        def urlTexto = generaUrlServicioWeb( TipoParametro.URL_RECIBE_LISTA_PRECIOS, listaPrecios.id, null, listaPrecios.id )
        /*log.debug( "invocando ${urlTexto}" )
        def resp = urlTexto?.toURL()?.text
        log.debug( "respuesta: ${resp}" )*/
      }
      return listaPrecios
    }
    return null
  }

  @Override
  List<Articulo> validarListaPrecios( ListaPrecios listaPrecios, List<Articulo> articulos ) {
    log.debug( "validando lista de precios ${listaPrecios?.id} con articulos: ${articulos*.articulo}" )
    if ( listaPrecios?.id && articulos?.any() ) {
      List<Articulo> resultados = [ ]
      List<Articulo> filtro = filtraArticulosParaSucursal( articulos )
      filtro.each { tmpArticulo ->
        log.debug( "obteniendo articulo: ${tmpArticulo?.articulo} en lista de precios a validar" )
        def a = QArticulo.articulo1
        List<Articulo> existencias = articuloRepository.findAll(
            a.id.eq( tmpArticulo?.id ).and( a.cantExistencia.gt( 0 ) )
            //a.articulo.eq( tmpArticulo?.articulo ).and( a.existencia.cantidad.gt( 0 ) )
        ) as List<Articulo>
        existencias.each {
          /*log.debug( "validando articulo: ${it?.articulo}, id: ${it?.id}, color: ${it?.codigoColor}, cantidad: ${it?.existencia?.cantidad}" )
          try {
            def sql = new Sql( invDataSource )
            def row = sql.firstRow( "SELECT * FROM verify WHERE articulo=${it?.articulo} AND color=${it?.codigoColor}" )
            log.debug( "obteniendo ubicacion: ${row?.ubic} de articulo: ${it?.articulo} color: ${it?.codigoColor}" )
            it.ubicacion = row?.ubic ?: ''
            sql.close()
          } catch ( ex ) {
            log.error( "no se pudo obtener ubicacion", ex )
          }*/
          resultados.add( it )
        }
      }
      return resultados
    }
    return [ ]
  }

  @Override
  @Transactional
  ListaPrecios cargarListaPrecios( ListaPrecios listaPrecios, List<Articulo> articulos ) {
    log.debug( "cargando lista de precios: ${listaPrecios?.id} con articulos: ${articulos*.articulo}" )
    if ( listaPrecios?.id && listaPrecios?.tipoCarga && articulos?.any() ) {
        log.debug( "Lista de precios datos:: ${listaPrecios.dump()}" )
      ListaPrecios result = listaPreciosRepository.findOne( listaPrecios.id )
      if ( result != null && result?.tipoCarga.length() == 0 && result?.fechaCarga == null ) {
        listaPrecios.fechaCarga = new Date()
        listaPrecios = listaPreciosRepository.save( listaPrecios )
        procesaCargaArticulos( articulos )
        if ( listaPrecios?.id ) {
          def urlTexto = generaUrlServicioWeb( TipoParametro.URL_CARGA_LISTA_PRECIOS, listaPrecios.id, listaPrecios.tipoCarga, listaPrecios.id )
          /*log.debug( "invocando ${urlTexto}" )
          def resp = urlTexto?.toURL()?.text
          log.debug( "respuesta: ${resp}" )*/
        }
        return listaPrecios
      } else {
        log.debug( "lista de precios ${listaPrecios?.id}, ya cargada ${result?.fechaCarga} o no registrada" )
        return result
      }
    }
    return null
  }

  private String generaUrlServicioWeb( TipoParametro tipoParametro, String idCambio, String tipoCarga, String idLp ) {
    log.debug( "generando url de servicio web: ${tipoParametro}, ${idCambio}, ${tipoCarga}" )
    boolean esCarga
    if ( TipoParametro.URL_RECIBE_LISTA_PRECIOS.equals( tipoParametro ) ) {
      esCarga = false
    } else if ( TipoParametro.URL_CARGA_LISTA_PRECIOS.equals( tipoParametro ) ) {
      esCarga = true
    } else {
      return null
    }
    if ( StringUtils.isNotBlank( idCambio ) ) {
      def parametro = parametroRepository.findOne( tipoParametro.value )
      def urlBase = parametro?.valor
      def idSucursal = obtenSucursalActual()
      def valores = "id_suc=${idSucursal}&id_cambio=${idCambio}&fecha=${new Date().format( 'ddMMyyyy' )}"
      if ( esCarga ) {
        valores += "&tipo=${tipoCarga?.charAt( 0 ) ?: ''}"
          Integer idSuc = Registry.currentSite
          AcuseRepository acuses = RepositoryFactory.acknowledgements
          Acuse acuse = new Acuse()
          acuse.idTipo = TAG_ACK_LOAD
          try {
              acuse = acuses.saveAndFlush( acuse )
              log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
          } catch ( Exception e ) {
              log.error( e.getMessage() )
          }
          acuse.contenido = ''
          acuse.contenido += String.format( 'fechaVal=%s|', CustomDateUtils.format(new Date(), 'ddMMyyyy') )
          acuse.contenido += String.format( 'id_cambioVal=%s|', idLp.trim() )
          acuse.contenido += String.format( 'id_sucVal=%s|', idSuc.toString().trim() )
          acuse.contenido += String.format( 'tipoVal=%s|', tipoCarga.trim().equalsIgnoreCase('MANUAL') ? 'M': 'A' )
          acuse.fechaCarga = new Date()
          try {
              acuse = acuses.saveAndFlush( acuse )
              log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
          } catch ( Exception e ) {
              log.error( e.getMessage() )
          }
      } else {
          Integer idSuc = Registry.currentSite
          AcuseRepository acuses = RepositoryFactory.acknowledgements
          Acuse acuse = new Acuse()
          acuse.idTipo = TAG_ACK_RECEIVED
          try {
              acuse = acuses.saveAndFlush( acuse )
              log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
          } catch ( Exception e ) {
              log.error( e.getMessage() )
          }
          acuse.contenido = ''
          acuse.contenido += String.format( 'fechaVal=%s|', CustomDateUtils.format(new Date(), 'ddMMyyyy') )
          acuse.contenido += String.format( 'id_cambioVal=%s|', idLp.trim() )
          acuse.contenido += String.format( 'id_sucVal=%s|', idSuc.toString().trim() )
          acuse.fechaCarga = new Date()
          try {
              acuse = acuses.saveAndFlush( acuse )
              log.debug( String.format( 'Acuse: (%d) %s -> %s', acuse.id, acuse.idTipo, acuse.contenido ) )
          } catch ( Exception e ) {
              log.error( e.getMessage() )
          }
      }
      log.debug( "url base: ${urlBase}" )
      log.debug( "valores: ${valores}" )
      return "${urlBase}?${valores}"
    }
    return null
  }

  private String obtenSucursalActual( ) {
    log.debug( "obteniendo id de sucursal actual" )
    def parametro = parametroRepository.findOne( TipoParametro.ID_SUCURSAL.value )
    if ( StringUtils.isNotBlank( parametro?.valor ) ) {
      log.debug( "id obtenida ${parametro.valor}" )
      return parametro.valor?.padLeft( 4, '0' )
    }
    return null
  }

  private void procesaCargaArticulos( final List<Articulo> articulos ) {
    List<Articulo> tmpArticulos = filtraArticulosParaSucursal( articulos )
    try{
      tmpArticulos.each { tmpArticulo ->
      log.debug( "procesando articulo de lista de precios ${tmpArticulo?.dump()}" )
      boolean esLista = tmpArticulo?.tipoPrecio?.matches( ~/P_.*(LUX|SEARS)/ ) || tmpArticulo?.tipoPrecio?.matches( 'M' )
      boolean esOferta = tmpArticulo?.tipoPrecio?.matches( ~/P_.+_O$/ ) || tmpArticulo?.tipoPrecio?.matches( 'MO' )
      boolean esEspecial = tmpArticulo?.tipoPrecio?.matches( ~/[1-9]/ )
      Parametro parametro = parametroRepository.findOne( TipoParametro.ID_SUCURSAL.value )
      Sucursal sucursal = sucursalRepository.findOne( Integer.parseInt( parametro.getValor() ) )
      def a = QArticulo.articulo1
      String art = tmpArticulo?.articulo != null ? tmpArticulo?.articulo.trim() : ''
      String color = tmpArticulo?.codigoColor != null ? tmpArticulo?.codigoColor.trim() : ''
      //Articulo articulo = articuloRepository.findOne( tmpArticulo?.id ) as Articulo
        Articulo articulo = articuloRepository.findOne(a.articulo.eq(art).
                and(a.codigoColor.eq(color)) ) as Articulo
      if ( articulo?.any() ) {
          if ( esLista || esEspecial ) {
              articulo.setPrecio( tmpArticulo.precio )
          }
          if ( esOferta || esEspecial ) {
              articulo.setPrecioO( tmpArticulo.precio )
          }
          articulo.setOperacion( tmpArticulo.operacion )
          articulo.setIdGenerico( tmpArticulo.idGenerico )
          articulo.setIdGenTipo( tmpArticulo.idGenTipo )
          articulo.setDescripcion( tmpArticulo.descripcion )
          articulo.setTipoPrecio( tmpArticulo.tipoPrecio )
          articulo.setPrecio( tmpArticulo.precio )
          articulo.setIdDisenoLente( tmpArticulo.idDisenoLente )
          articulo.setIdGenSubtipo( tmpArticulo.idGenSubtipo )
          articulo.setMarca( tmpArticulo.marca )
          articulo.setTipo( tmpArticulo.tipo )
          articulo.setSubtipo( tmpArticulo.subtipo )

          articuloRepository.saveAndFlush( articulo )
      } else {
        log.debug( "registrando nuevo articulo: ${tmpArticulo.articulo}" )
        tmpArticulo.setIdSucursal(sucursal.id)
          articuloRepository.save( tmpArticulo )
          articuloRepository.flush()
      }
      def tipoPrecio = esLista ? 'L' : ( esOferta ? 'O' : tmpArticulo?.tipoPrecio )
      Precio tmpPrecio = precioRepository.findByArticuloAndLista( tmpArticulo?.articulo, tipoPrecio )
      if ( 'D'.equalsIgnoreCase( tmpArticulo?.operacion ) && tmpPrecio?.id ) {
        log.debug( "eliminando precio articulo: ${tmpArticulo.articulo}, lista: ${tmpArticulo.tipoPrecio}" )
        precioRepository.delete( tmpPrecio.id )
      } else {
        if ( tmpPrecio?.id ) {
          log.debug( "actualizando precio articulo: ${tmpPrecio.articulo}, lista: ${tmpPrecio.lista}" )
          tmpPrecio.precio = tmpArticulo.precio
          tmpPrecio.fecha = new Date()
          tmpPrecio.surte = tmpArticulo.surte
          precioRepository.save( tmpPrecio )
        } else {
          log.debug( "registrando nuevo precio articulo: ${tmpArticulo.articulo}, lista: ${tipoPrecio}" )
          Precio precio = new Precio()
          precio.articulo = tmpArticulo.articulo
          precio.lista = tipoPrecio
          precio.precio = tmpArticulo.precio
          precio.fecha = new Date()
          precio.surte = tmpArticulo.surte
          precioRepository.save( precio )
        }
      }
    }
    }catch ( Exception e){
        System.out.println( e )
    }
  }

  private List<Articulo> filtraArticulosParaSucursal( final List<Articulo> articulos ) {
    log.debug( "filtrando lista de articulos para sucursal ${articulos*.articulo}" )
    def idSucursal = parametroRepository.findOne( TipoParametro.ID_SUCURSAL.value )?.valor
    log.debug( "id sucursal: ${idSucursal}" )
    Sucursal sucursal = sucursalRepository.findOne( idSucursal?.toInteger() )
    log.debug( "sucursal sears: ${sucursal?.sears}" )
    def sears = ~/P_SEARS(.+)?|[1-9]/
    def lux = ~/P_LUX(.+)?|[1-9]/
    def masVision = ~/M(.+)?|[1-9]/
    return articulos.findAll {
      it?.tipoPrecio?.matches( sucursal?.sears ? sears : masVision )
    }
  }

  Integer listasPreciosPendientes( ){
      log.debug( "Obteniendo listas de precios pendientes" )
      QListaPrecios lista = QListaPrecios.listaPrecios
      List<ListaPrecios> lstListaPrecios = listaPreciosRepository.findAll( lista.tipoCarga.isEmpty().or(lista.tipoCarga.isNull()).
              and(lista.fechaCarga.isNull()) ) as List<ListaPrecios>
      log.debug( "Numero de listas pendientes: ${lstListaPrecios.size()}" )

      return lstListaPrecios.size()
  }

}