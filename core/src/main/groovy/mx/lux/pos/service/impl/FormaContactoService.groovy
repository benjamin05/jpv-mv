package mx.lux.pos.service.impl

import mx.lux.pos.model.FormaContacto
import mx.lux.pos.model.Jb


public interface FormaContactoService {

    FormaContacto findFCbyRx(String rx)

    FormaContacto saveFC (FormaContacto formaContacto)

    FormaContacto saveFCFam (String idFactura, Jb jbFam, String idFactura2)

    List<FormaContacto> findByidCliente( Integer idCliente )


}