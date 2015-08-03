package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.EmpleadoQuery;
import mx.lux.pos.java.querys.FormaContactoQuery;
import mx.lux.pos.java.repository.EmpleadoJava;
import mx.lux.pos.java.repository.FormaContactoJava;
import mx.lux.pos.model.FormaContacto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FormaContactoServiceJava {

  static final Logger log = LoggerFactory.getLogger(FormaContactoServiceJava.class);

  public FormaContactoJava findFormaContactobyRx(String rx) {
    return FormaContactoQuery.buscaFormaContactoPorRx(rx);
  }


  public FormaContactoJava saveFC(FormaContactoJava formaContacto) {
    formaContacto = FormaContactoQuery.saveFormaContacto(formaContacto);
    return formaContacto;
  }


  public List<FormaContactoJava> findByidCliente( Integer idCliente ) {
    return FormaContactoQuery.buscaFormaContactoPorIdCliente( idCliente );

  }


}
