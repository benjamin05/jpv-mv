package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.java.querys.EmpleadoQuery
import mx.lux.pos.java.repository.RegionalJava
import mx.lux.pos.model.AcusesTipo
import mx.lux.pos.model.Empleado
import mx.lux.pos.model.InvAdjustLine
import mx.lux.pos.model.LogAsignaSubgerente
import mx.lux.pos.model.Parametro
import mx.lux.pos.model.TipoParametro
import mx.lux.pos.repository.AcusesTipoRepository
import mx.lux.pos.repository.EmpleadoRepository
import mx.lux.pos.repository.LogAsignaSubgerenteRepository
import mx.lux.pos.repository.ParametroRepository
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.EmpleadoService
import mx.lux.pos.service.business.Registry
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
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
  private LogAsignaSubgerenteRepository logAsignaSubgerenteRepository

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


  @Transactional
  @Override
  void insertaSubgerente( String idEmpleado, String idEmpleadoAsigno, Date fechaInicial, Date fechaFinal, Integer horas ){
    log.debug( "insertaSubgerente( )" )
    LogAsignaSubgerente log = new LogAsignaSubgerente()
    log.fecha = new Date()
    log.empleadoAsigno = StringUtils.trimToEmpty( idEmpleadoAsigno )
    log.empleadoAsignado = StringUtils.trimToEmpty( idEmpleado )
    log.fechaInicial = fechaInicial
    log.fechaFinal = fechaFinal
    log.horas = horas
    logAsignaSubgerenteRepository.saveAndFlush( log )
  }


  @Override
  LogAsignaSubgerente obtenerSubgerenteActual(  ){
    List<LogAsignaSubgerente> logs = logAsignaSubgerenteRepository.findSubmanagersProgrammed()
    LogAsignaSubgerente log = null
    for(LogAsignaSubgerente logTmp : logs){
      if( logTmp.fechaInicial == null ){
        Date fecha = DateUtils.truncate( logTmp.fecha, Calendar.DAY_OF_MONTH )
        Date hoy = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH )
        if( fecha.compareTo(hoy) == 0 ){
          Calendar cal = Calendar.getInstance();
          cal.setTime(logTmp.fecha);
          cal.add(Calendar.HOUR_OF_DAY, logTmp.horas);
          Date vigencia = cal.getTime();
          if( vigencia.compareTo(new Date()) >= 0){
            log = logTmp
          }
        }
      } else {
        if( logTmp.fechaInicial.compareTo(new Date()) <= 0 && logTmp.fechaFinal.compareTo(new Date()) >= 0 ){
          log= logTmp
        }
      }
    }
    if( log != null && log.id == null ){
      log = null
    }
    return log
  }



  @Override
  List<LogAsignaSubgerente> obtenerSubgerentesActualYProgramados( ){
    List<LogAsignaSubgerente> log = new ArrayList<>()
    List<LogAsignaSubgerente> logTmp = logAsignaSubgerenteRepository.findSubmanagersProgrammed()
    for(LogAsignaSubgerente logData : logTmp){
      if( logData.horas != null ){
        Calendar cal = Calendar.getInstance();
        cal.setTime(logData.fecha);
        cal.add(Calendar.HOUR_OF_DAY, logData.horas);
        Date vigencia = cal.getTime();
        if( vigencia.compareTo(new Date()) > 0 ){
          log.add( logData )
        }
      } else {
        log.add( logData )
      }
    }
    return log
  }


  @Override
  void cargaArchivoRegionales( ){
    Parametro ubicacion = Registry.find( TipoParametro.RUTA_POR_RECIBIR )
    Parametro parametro = RepositoryFactory.registry.findOne( TipoParametro.RUTA_RECIBIDOS.value )
    String ubicacionSource = ubicacion.valor
    String ubicacionsDestination = parametro.valor
    File source = new File( ubicacionSource )
    File destination = new File( ubicacionsDestination )
    if ( source.exists() && destination.exists() ) {
      source.eachFile() { file ->
        String[] dataName = StringUtils.trimToEmpty(file.getName()).split(/\./)
        if ( dataName.last().equalsIgnoreCase( "ereg" ) ) {
          file.eachLine { String line ->
            String[] linea = StringUtils.trimToEmpty(line).split(/\|/)
            if( linea.length >= 4 ){
              RegionalJava regional = new RegionalJava()
              regional.idEmpresa = linea[0]
              regional.idEmpleado = linea[1]
              regional.nombre = linea[2]
              regional.credencial = linea[3]
              EmpleadoQuery.saveOrUpdateRegional( regional )
            }
          }
          def newFile = new File( destination, file.name )
          def moved = file.renameTo( newFile )
        }
      }
    }
  }


}
