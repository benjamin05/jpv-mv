package mx.lux.pos.service;


import mx.lux.pos.querys.NotaVentaQuery;
import mx.lux.pos.querys.RecetaQuery;
import mx.lux.pos.repository.NotaVentaJava;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class RecetaServiceJava {

    static final NotaVentaServiceJava notaVentaServiceJava = new NotaVentaServiceJava();
    static final Logger log = LoggerFactory.getLogger(NotaVentaQuery.class);

    public static void saveRx (NotaVentaJava notaVenta, Integer idReceta) throws ParseException {
      if ( StringUtils.isNotBlank(notaVenta.getIdFactura()) && idReceta != null && RecetaQuery.exists(idReceta) ) {
        notaVenta.setReceta(idReceta);
        NotaVentaServiceJava.registrarNotaVenta(notaVenta);
      } else {
        log.warn("No hay receta");
      }
    }
}
