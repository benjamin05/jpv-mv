package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.model.AcusesTipo
import mx.lux.pos.model.Empleado
import mx.lux.pos.model.Parametro
import mx.lux.pos.model.TipoParametro
import mx.lux.pos.repository.AcusesTipoRepository
import mx.lux.pos.repository.EmpleadoRepository
import mx.lux.pos.repository.ParametroRepository
import mx.lux.pos.service.EmpleadoService
import mx.lux.pos.service.business.Registry
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
import java.text.NumberFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@Slf4j
@Service( 'empleadoService' )
@Transactional( readOnly = true )
class EmpleadoServiceImpl implements EmpleadoService {

  @Resource
  private EmpleadoRepository empleadoRepository

  @Resource
  private ParametroRepository parametroRepository

  @Resource
  private AcusesTipoRepository acusesTipoRepository

  static final String TAG_ACUSE_IMPORTA_EMPLEADO = 'importa_emp'
  static final Integer TAG_ID_EMPRESA = 7

  @Override
  Empleado obtenerEmpleado( String id ) {
    log.info( "obteniendo empleado id: ${id}" )
    if ( StringUtils.isNotBlank( id ) ) {
      Empleado empleado = empleadoRepository.findOne( id )
      if ( empleado?.id ) {
        return empleado
      } else {
        log.warn( "empleado no existe" )
      }
    } else {
      log.warn( "no se obtiene empleado, parametros invalidos" )
    }
    return null
  }

  @Override
  void actualizarPass( Empleado empleado ){
      log.info( "actualizando password de empleado id: ${empleado.id}" )
      if ( StringUtils.isNotBlank( empleado.id ) ) {
          Empleado emp = empleadoRepository.save( empleado )
          empleadoRepository.flush()
      }
  }

  @Override
  Parametro parametro( String idParametro ){
    Parametro parametro = parametroRepository.findOne( idParametro )
    return  parametro
  }


  @Override
  @Transactional
  Parametro saveParametro( Parametro parametro ){
    parametroRepository.saveAndFlush( parametro )
  }



  @Override
  @Transactional
  Empleado importaEmpleado( String idEmpleado ){
    Empleado newEmpleado = new Empleado()
    AcusesTipo acuseUrl = acusesTipoRepository.findOne( TAG_ACUSE_IMPORTA_EMPLEADO )
    String url = StringUtils.trimToEmpty(acuseUrl.pagina)
    Integer idSucursal = Registry.currentSite
    if( StringUtils.trimToEmpty(url).length() > 0 ){
      url += String.format( '?arg=%s|%s', TAG_ID_EMPRESA.toString().trim(), StringUtils.trimToEmpty(idEmpleado) )
        String response = ""
        ExecutorService executor = Executors.newFixedThreadPool(1)
        int timeoutSecs = 15
        final Future<?> future = executor.submit(new Runnable() {
            public void run() {
                try {
                    log.debug( "Liga importar empleado: ${url}" )
                    response = url.toURL().text
                    response = response?.find( /<XX>\s*(.*)\s*<\/XX>/ ) {m, r -> return r}
                    log.debug( "Respuesta: ${response}" )
                } catch (Exception e) {
                    throw new RuntimeException(e)
                }
            }
        })
        try {
            future.get(timeoutSecs, TimeUnit.SECONDS)
        }  catch (Exception e) {println e}

      try {
        String[] data = response.split( /\|/ )
        if( data.length >= 4 ){
          Integer idPuesto = 0
          try{
            idPuesto = NumberFormat.getInstance().parse( data[0] ).intValue()
          } catch ( Exception e ){ println e }
          newEmpleado.id = StringUtils.trimToEmpty( idEmpleado )
          newEmpleado.idSucursal = idSucursal
          newEmpleado.idPuesto = idPuesto
          newEmpleado.passwd = '1234'
          newEmpleado.nombre = data[1]
          newEmpleado.apellidoPaterno = data[2]
          newEmpleado.apellidoMaterno = data[3]
          newEmpleado.idEmpresa = TAG_ID_EMPRESA
          newEmpleado = empleadoRepository.save( newEmpleado )
          empleadoRepository.flush()
        } else {
          newEmpleado.nombre = data[1]
        }
      } catch ( Exception e ) {
        log.debug( "Error al procesar url, ${e.message}" )
      }
    }
    return newEmpleado
  }


}
