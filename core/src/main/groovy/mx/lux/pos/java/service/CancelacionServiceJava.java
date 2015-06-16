package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public class CancelacionServiceJava {

  static final Logger log = LoggerFactory.getLogger(CancelacionServiceJava.class);


  public void actualizaGrupo( String idFactura, String trans ) throws ParseException {
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(idFactura);
    JbJava trabajo = JbQuery.buscarPorRx(nota.getFactura());
    if( trabajo.getIdGrupo().length() > 0 ){
      List<JbJava> grupo = JbQuery.buscaJbPorIdGrupo(trabajo.getIdGrupo());
      if(grupo.size() > 1){
        Integer noEntCant = 0;
        for(JbJava jbTmp : grupo){
          if(!jbTmp.getEstado().trim().equalsIgnoreCase("TE") && !jbTmp.getEstado().trim().equalsIgnoreCase("CN")){
            noEntCant = noEntCant+1;
          }
        }
        if(noEntCant == 0){
          List<JbJava> lstTmp = JbQuery.buscaJbPorIdGrupo( trabajo.getIdGrupo() );
          JbJava jbGrupo = lstTmp.size() > 0 ? lstTmp.get(0) : null;
          if(jbGrupo != null){
            jbGrupo.setEstado(trans.equalsIgnoreCase("E") ? "TE" : "CN");
            JbQuery.updateEstadoJbRx( jbGrupo.getRx(), jbGrupo.getEstado() );
          }
          JbLlamadaJava llamada = JbQuery.buscaJbLlamadaPorIdGrupo( trabajo.getIdGrupo() );
          if(llamada != null){
            JbQuery.eliminaJbLLamada(llamada.getRx());
          }
        }
      }
    }
  }
}
