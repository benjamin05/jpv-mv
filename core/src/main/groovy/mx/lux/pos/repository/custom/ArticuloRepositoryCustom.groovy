package mx.lux.pos.repository.custom

import mx.lux.pos.model.Articulo

interface ArticuloRepositoryCustom {

    Articulo findbyName(String articulo)

    Articulo findbyId(Integer idArticulo)
}
