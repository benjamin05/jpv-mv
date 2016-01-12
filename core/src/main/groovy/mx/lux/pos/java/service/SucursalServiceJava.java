package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.Sucursal;
import mx.lux.pos.model.TipoParametro;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

public class SucursalServiceJava {

  static final Logger log = LoggerFactory.getLogger(SucursalServiceJava.class);

  private static final String TAG_GENERICO_H = "H";



  public Boolean validarSucursal( Integer pSucursal ) {
    return SucursalesQuery.BuscaSucursalPorIdSuc( pSucursal ) != null;
  }



  public SucursalesJava obtenSucursalActual( ) throws ParseException {
    log.debug( "obteniendo sucursal actual" );
    Parametros parametro = ParametrosQuery.BuscaParametroPorId(TipoParametro.ID_SUCURSAL.getValue());
    if ( StringUtils.isNumeric(parametro.getValor()) && StringUtils.isNumeric(parametro.getValor()) ) {
      Integer id = NumberFormat.getInstance().parse(parametro.getValor()).intValue();
      log.debug( "sucursal solicitada ${id}" );
      return SucursalesQuery.BuscaSucursalPorIdSuc( id );
    }else{
      return null;
    }
  }


}
