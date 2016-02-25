package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.DetalleNotaVenta;
import mx.lux.pos.model.LogSP;
import mx.lux.pos.model.QArticulo;
import mx.lux.pos.model.QLogSP;
import mx.lux.pos.service.business.Registry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class DetalleNotaVentaServiceJava {

  static final Logger log = LoggerFactory.getLogger(DetalleNotaVentaServiceJava.class);

  public Boolean verificaValidacionSP( Integer idArticulo, String idFactura, String respuesta ){
    log.debug( "verificaValidacionSP( )" );
    Boolean param = false;
    Boolean log = false;
    Boolean verificacion = false;
    String parametro = Registry.isActiveValidSP();
    String[] valid = parametro.split( "\\|" );
    if(valid.length > 1 && valid[0].trim().equalsIgnoreCase("si")){
      param = true;
    }
    List<LogSpJava> lstLogs = LogSpQuery.buscaLogSpPorFechaNull();
    if( lstLogs.size() > 0 ){
      LogSpJava logSp = lstLogs.get(lstLogs.size()-1);
      Date fechaUltimaLLamada = logSp.getFechaLlamada();
      Calendar cal = Calendar.getInstance();
      cal.setTime(fechaUltimaLLamada);
      cal.add(Calendar.HOUR, 1);
      Date fechaHoraDespues = cal.getTime();
      if( new Date().after( fechaHoraDespues )){
        log = true;
      }
    } else {
      log = true;
    }
    if( param && log ){
      verificacion = true;
    }
    return verificacion;
  }


  public LogSpJava saveLogSP( Integer idArticulo, String idFactura, String respuesta ) throws ParseException {
    log.debug( "saveLogSP( )" );
    String[] resp = respuesta != null ? respuesta.split("\\|") : new String[0];
    Boolean respuestaServ = false;
    Date fechaLlamada = new Date();
    Date fechaRespuesta = null;
    if( StringUtils.trimToEmpty(respuesta).length() > 0 ){
      fechaRespuesta = new Date();
    }
    if( resp.length > 0 ){
      String valor = resp[0];
      if(valor.trim().equalsIgnoreCase("si")){
        respuestaServ = true;
      }
    }
    if( idFactura == null ){
      String[] lastIdFactura = NotaVentaQuery.getLastNotaVentaSequence().split("A");
      Integer num = 0;
      try{
        num = NumberFormat.getInstance().parse( lastIdFactura[1]).intValue();
      } catch (NumberFormatException e){
        System.out.println(e);
      }
      idFactura = String.format( "%s%05d", 'A', num+1 );
    }
    LogSpJava log = new LogSpJava();
    log.setIdFactura(idFactura);
    log.setRespuesta(respuestaServ);
    log.setIdArticulo(idArticulo);
    log.setFechaLlamada(fechaLlamada);
    log.setFechaRespuesta(fechaRespuesta);
    log = LogSpQuery.saveLogSp(log);
    return log;
  }



  public List<DetalleNotaVentaJava> listarDetallesNotaVentaPorIdFactura( String idFactura ) throws ParseException {
    log.info( "listando detallesNotaVenta con idFactura: "+ idFactura );
    if ( StringUtils.isNotBlank(idFactura) ) {
      List<DetalleNotaVentaJava> lstDetalles = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFactura( idFactura );
      Collections.sort( lstDetalles, new Comparator<DetalleNotaVentaJava>() {
        @Override
        public int compare(DetalleNotaVentaJava o1, DetalleNotaVentaJava o2) {
          return o1.getIdArticulo().compareTo(o2.getIdArticulo());
        }
      });
      return lstDetalles;
    } else {
      log.warn( "no se listan detallesNotaVenta, parametros invalidos" );
    }
    return new ArrayList<DetalleNotaVentaJava>();
  }
}
