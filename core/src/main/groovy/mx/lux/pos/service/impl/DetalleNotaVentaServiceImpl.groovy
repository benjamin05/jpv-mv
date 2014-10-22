package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.model.LogSP
import mx.lux.pos.model.QLogSP
import mx.lux.pos.repository.DetalleNotaVentaRepository
import mx.lux.pos.repository.NotaVentaRepository
import mx.lux.pos.repository.LogSPRepository
import mx.lux.pos.service.DetalleNotaVentaService
import mx.lux.pos.service.business.Registry
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
import java.text.NumberFormat

@Slf4j
@Service( "detalleNotaVentaService" )
@Transactional( readOnly = true )
class DetalleNotaVentaServiceImpl implements DetalleNotaVentaService {

  @Resource
  private DetalleNotaVentaRepository detalleNotaVentaRepository

  @Resource
  private NotaVentaRepository notaVentaRepository

  @Resource
  private LogSPRepository logSPRepository

  @Override
  DetalleNotaVenta obtenerDetalleNotaVenta( String idFactura, Integer idArticulo ) {
    log.info( "obteniendo detalleNotaVenta con idFactura: ${idFactura} idArticulo: ${idArticulo}" )
    if ( StringUtils.isNotBlank( idFactura ) && idArticulo ) {
      return detalleNotaVentaRepository.findByIdFacturaAndIdArticulo( idFactura, idArticulo )
    } else {
      log.warn( 'no se obtiene detalleNotaVenta, parametros invalidos' )
    }
    return null
  }

  @Override
  List<DetalleNotaVenta> listarDetallesNotaVentaPorIdFactura( String idFactura ) {
    log.info( "listando detallesNotaVenta con idFactura: ${idFactura}" )
    if ( StringUtils.isNotBlank( idFactura ) ) {
      return detalleNotaVentaRepository.findByIdFacturaOrderByIdArticuloAsc( idFactura ) ?: [ ]
    } else {
      log.warn( 'no se listan detallesNotaVenta, parametros invalidos' )
    }
    return [ ]
  }

  @Override
  DetalleNotaVenta obtenerDetalleNotaVentaPoridFacturaidArticulo( String idFactura, Integer idArticulo ){
    log.debug( "obtenerDetalleNotaVentaPoridFacturaidArticulo( String idFactura, Integer idArticulo )" )

    DetalleNotaVenta detNotaVenta = detalleNotaVentaRepository.findByIdFacturaAndIdArticulo( idFactura, idArticulo )
    return detNotaVenta
  }


  @Override
  Boolean verificaValidacionSP( Integer idArticulo, String idFactura, String respuesta ){
      log.debug( "verificaValidacionSP( )" )
      Boolean param = false
      Boolean log = false
      Boolean verificacion = false
      String parametro = Registry.isActiveValidSP()
      String[] valid = parametro.split( /\|/ )
      if(valid.length > 1 && valid[0].trim().equalsIgnoreCase('si')){
        param = true
      }
      QLogSP lg = QLogSP.logSP
      List<LogSP> lstLogs = logSPRepository.findAll( lg.fechaRespuesta.isNull(), lg.fechaLlamada.asc() )
      if( lstLogs.size() > 0 ){
        LogSP logSp = lstLogs.last()
        Date fechaUltimaLLamada = logSp.fechaLlamada
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaUltimaLLamada);
        cal.add(Calendar.HOUR, 1);
        Date fechaHoraDespues = cal.getTime()
        if( new Date().after( fechaHoraDespues )){
          log = true
        }
      } else {
        log = true
      }

      if( param && log ){
        verificacion = true
      }
      return verificacion
  }



  LogSP saveLogSP( Integer idArticulo, String idFactura, String respuesta ){
    log.debug( "saveLogSP( )" )
    String[] resp = respuesta != null ? respuesta.split(/\|/) : []
    Boolean respuestaServ = false
    Date fechaLlamada = new Date()
    Date fechaRespuesta = null
    if( StringUtils.trimToEmpty(respuesta).length() > 0 ){
        fechaRespuesta = new Date()
    }
    if( resp.length > 0 ){
      String valor = resp[0]
      if(valor.trim().equalsIgnoreCase('si')){
        respuestaServ = true
      }
    }
    if( idFactura == null ){
      String[] lastIdFactura = notaVentaRepository.getLastNotaVentaSequence().split("A")
      Integer num = 0
      try{
        num = NumberFormat.getInstance().parse( lastIdFactura[1])
      } catch (NumberFormatException e){
        println e
      }
      idFactura = String.format( "%s%05d", 'A', num+1 )
    }
    LogSP log = new LogSP()
    log.idFactura = idFactura
    log.respuesta = respuestaServ
    log.idArticulo = idArticulo
    log.fechaLlamada = fechaLlamada
    log.fechaRespuesta = fechaRespuesta
    log = logSPRepository.saveAndFlush( log )
    return log
  }



}
