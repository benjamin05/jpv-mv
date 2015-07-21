package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.Examen;
import mx.lux.pos.model.QExamen;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public class ExamenServiceJava {

  static final Logger log = LoggerFactory.getLogger(ExamenServiceJava.class);


  public ExamenJava obtenerExamenPorIdCliente( Integer idCliente ) {
    log.info( "obtenerExamenPorIdCliente" );
    ExamenJava examen = null;
    List<ExamenJava> lstExamenes = ExamenQuery.buscaExamenesPorIdCliente(idCliente );
      if( lstExamenes.size() > 0 ){
        for(ExamenJava exam : lstExamenes){
          if(!exam.getTipoOft().equalsIgnoreCase("SE")){
            examen = exam;
          }
        }
      }
      return examen;
  }


  public ExamenJava guardarExamen( ExamenJava examen ) throws ParseException {
    log.info( "guardando examen" );
    return ExamenQuery.saveOrUpdateExamen(examen);
  }


}
