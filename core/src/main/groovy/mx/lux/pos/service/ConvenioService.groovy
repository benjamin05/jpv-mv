package mx.lux.pos.service

import mx.lux.pos.model.InstitucionIc

interface ConvenioService {

    List<InstitucionIc> obtenerConvenios( String clave  )

}
