package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.Empleado;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
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

}
