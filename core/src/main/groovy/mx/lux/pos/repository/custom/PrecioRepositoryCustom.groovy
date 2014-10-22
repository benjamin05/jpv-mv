package mx.lux.pos.repository.custom


import mx.lux.pos.model.Precio

interface PrecioRepositoryCustom {

    Precio findbyArt(String articulo)
}
