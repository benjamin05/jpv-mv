package mx.lux.pos.repository.custom

import mx.lux.pos.model.TmpServicios

interface TmpServiciosRepositoryCustom {

   TmpServicios findbyIdFactura(String idFactura)
}
