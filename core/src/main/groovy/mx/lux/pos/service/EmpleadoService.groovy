package mx.lux.pos.service

import mx.lux.pos.model.Empleado
import mx.lux.pos.model.Parametro

interface EmpleadoService {

  Empleado obtenerEmpleado( String id )

  void actualizarPass( Empleado empleado )

  Parametro parametro( String idParametro )

  Parametro saveParametro( Parametro parametro )

  Empleado importaEmpleado( String idEmpleado )
}
