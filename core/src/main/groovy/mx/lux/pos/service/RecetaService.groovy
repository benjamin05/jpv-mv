package mx.lux.pos.service

import mx.lux.pos.model.Receta

interface RecetaService {

    Receta guardarReceta( Receta receta )

    List<Receta> recetaCliente(Integer IdCliente)

    Receta findbyId(Integer idRx)

    void generaAcuse(String orderID)
}
