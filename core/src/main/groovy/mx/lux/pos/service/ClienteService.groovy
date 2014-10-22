package mx.lux.pos.service

import mx.lux.pos.model.*

interface ClienteService {

  Cliente obtenerCliente( Integer id )

  List<Cliente> buscarCliente( String nombre, String apellidoPaterno, String apellidoMaterno )

  Cliente agregarCliente( Cliente cliente )

  Cliente actualizaCliente(Cliente cliente)

  Cliente agregarCliente( Cliente cliente, String city, String country )

  Cliente obtenerClientePorDefecto( )

  List<Titulo> listarTitulosClientes( )

  List<Dominio> listarDominiosClientes( )

  void agregarClienteProceso( ClienteProceso clienteProceso)

  List<ClienteProceso> obtenerClientesEnProceso( Boolean pLoaded )

  List<ClienteProceso> obtenerClientesEnCaja( Boolean pLoaded )

  void actualizarClienteEnProceso( Integer pIdCliente )

  void eliminarClienteProceso( Integer pIdCliente )

  List<Receta> obtenerRecetas( Integer idCliente )

  List<Cliente> listBasedOnHint( String pHint )

  List<Cliente> listAll( )

  ClienteProceso obtieneClienteProceso( Integer idCliente )

  void eliminarTodoClienteProceso( )

  List<ClienteProceso> obtenerClientesMultipago(  )

  void saveMainFC (Integer idCliente, Integer formaContacto)

  List<NotaVenta> obtenernotasActivas( Integer idCliente )
}
