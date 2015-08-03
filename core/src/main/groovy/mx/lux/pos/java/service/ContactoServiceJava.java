package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.TipoContacto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public class ContactoServiceJava {

  static final Logger log = LoggerFactory.getLogger(ContactoServiceJava.class);

  List<TipoContactoJava> obtenerTiposContacto() {
    List<TipoContactoJava> contactos = TipoContactoQuery.buscaTodoTipoContacto();
    return contactos;
  }

}
