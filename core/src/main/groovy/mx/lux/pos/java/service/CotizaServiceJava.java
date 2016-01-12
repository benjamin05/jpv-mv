package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class CotizaServiceJava {

  static final Logger log = LoggerFactory.getLogger(CotizaServiceJava.class);

  public void updateQuote( String idFactura, Integer numQuote ) throws ParseException {
    CotizaJava cotizacion = CotizaQuery.buscaCotizaPorIdFactura(idFactura);
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(idFactura);
    if(nota != null && cotizacion != null && StringUtils.trimToEmpty(nota.getFactura()).length() > 0){
      cotizacion.setIdFactura(nota.getFactura().trim());
      cotizacion.setFechaVenta(new Date());
      CotizaQuery.saveOrUpdateCotiza( cotizacion );
    }
  }


  public void updateidFacturaQuote( String idFactura, Integer numQuote ) throws ParseException {
    CotizaJava cotizacion = CotizaQuery.buscaCotizaPorId(numQuote);
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(idFactura);
    if(nota != null && cotizacion != null && StringUtils.trimToEmpty(nota.getIdFactura()).length() > 0){
      cotizacion.setIdFactura(StringUtils.trimToEmpty(nota.getIdFactura()));
      cotizacion.setFechaVenta(new Date());
      CotizaQuery.saveOrUpdateCotiza( cotizacion );
    }
  }


}
