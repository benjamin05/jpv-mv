package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.ClienteProcesoEtapa;
import mx.lux.pos.model.TipoParametro;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class TransInvServiceJava {

  static final Logger log = LoggerFactory.getLogger(TransInvServiceJava.class);

  public TransInvJava obtieneUltimaTransaccionPorIdArticulo( Integer idArticulo ) throws ParseException {
    return TransInvQuery.buscaTransInvPorIdArticulo( idArticulo );
  }


}
