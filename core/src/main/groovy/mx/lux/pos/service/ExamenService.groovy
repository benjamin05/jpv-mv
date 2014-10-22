package mx.lux.pos.service

import mx.lux.pos.model.Examen

interface ExamenService {


    Examen guardarExamen( Examen examen )

    Examen actualizarExamen( Examen examen )

    Examen obtenerExamenPorIdCliente( Integer idCliente )
}
