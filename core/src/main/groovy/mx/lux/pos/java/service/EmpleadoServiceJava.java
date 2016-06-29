package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.Empleado;
import mx.lux.pos.service.business.Registry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class EmpleadoServiceJava {

  static final Logger log = LoggerFactory.getLogger(EmpleadoServiceJava.class);

  private static final String TAG_GENERICO_H = "H";


  public EmpleadoJava obtenerEmpleado( String id ) {
    log.info( "obteniendo empleado id: ${id}" );
    if ( StringUtils.isNotBlank(id) ) {
      EmpleadoJava empleado = EmpleadoQuery.buscaEmpPorIdEmpleado( id );
      if ( empleado != null && StringUtils.trimToEmpty(empleado.getIdEmpleado()).length() > 0 ) {
        return empleado;
      } else {
        log.warn( "empleado no existe" );
      }
    } else {
      log.warn( "no se obtiene empleado, parametros invalidos" );
    }
    return null;
  }



  public ChecadasJava checadaEmpleado( String claveTarjeta ) {
    log.info( "obteniendo empleado con tarjeta: "+claveTarjeta );
    if ( StringUtils.isNotBlank(claveTarjeta) ) {
      RegionalJava empleado = EmpleadoQuery.buscaRegionalPorClaveTarjeta(claveTarjeta);
      if ( empleado != null && StringUtils.trimToEmpty(empleado.getIdEmpleado()).length() > 0 ) {
        ChecadasJava checada = new ChecadasJava();
        checada.setSucursal(StringUtils.trimToEmpty(Registry.getCurrentSite().toString()));
        checada.setFecha(new Date());
        checada.setHora( new Date());
        checada.setEmpresa("7");
        checada.setIdEmpleado(StringUtils.trimToEmpty(empleado.getIdEmpleado()));
        EmpleadoQuery.saveChecada(checada);
        return checada;
      } else {
        log.warn( "tarjeta de empleado no existe" );
      }
    } else {
      log.warn( "no se obtiene empleado, parametros invalidos" );
    }
    return null;
  }


}
