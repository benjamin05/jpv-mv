package mx.lux.pos.ui.controller

import mx.lux.pos.model.Cliente
import mx.lux.pos.model.ClientePais
import mx.lux.pos.repository.TransInvDetalleRepository
import mx.lux.pos.repository.TransInvRepository
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.ClienteService
import mx.lux.pos.service.ContactoService
import mx.lux.pos.service.ConvenioService
import mx.lux.pos.service.EmpleadoService
import mx.lux.pos.service.EstadoService
import mx.lux.pos.service.ExamenService
import mx.lux.pos.service.LocalidadService
import mx.lux.pos.service.MunicipioService
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.service.PaisesService
import mx.lux.pos.service.RecetaService
import mx.lux.pos.service.SucursalService
import mx.lux.pos.service.business.InventoryCommit
import mx.lux.pos.service.impl.FormaContactoService
import mx.lux.pos.ui.model.Address
import mx.lux.pos.ui.model.Customer
import mx.lux.pos.ui.model.CustomerType
import mx.lux.pos.ui.model.GenderType
import spock.lang.Specification

import static mx.lux.pos.ui.assertions.Assert.assertCustomerEquals

class CustomerControllerTest extends Specification {

  private CustomerController controller
  private ClienteService clienteService
  private EstadoService estadoService
  private MunicipioService municipioService
  private LocalidadService localidadService
  private ConvenioService convenioService
  private EmpleadoService empleadoService
  private RecetaService recetaService
  private ExamenService examenService
  private SucursalService sucursalService
  private PaisesService paisesService
  private NotaVentaService notaService
  private ContactoService contactoService
  private FormaContactoService formaContactoService
  private TransInvRepository transInvRepository
  private TransInvDetalleRepository transInvDetalleRepository

  def setup( ) {
    clienteService = Mock( ClienteService )
    estadoService = Mock( EstadoService )
    municipioService = Mock( MunicipioService )
    localidadService = Mock( LocalidadService )
    convenioService = Mock( ConvenioService )
    empleadoService = Mock( EmpleadoService )
    recetaService = Mock( RecetaService )
    examenService = Mock( ExamenService )
    sucursalService = Mock( SucursalService )
    paisesService = Mock( PaisesService )
    notaService = Mock( NotaVentaService )
    contactoService = Mock( ContactoService )
    formaContactoService = Mock( FormaContactoService )
    transInvRepository = Mock( TransInvRepository )
    transInvDetalleRepository = Mock( TransInvDetalleRepository )
    controller = new CustomerController(
        clienteService,
        estadoService,
        municipioService,
        localidadService,
        convenioService,
         empleadoService,
            recetaService,
            examenService,
            sucursalService,
            paisesService,
            notaService,
            contactoService,
            formaContactoService,
            //transInvRepository,
            //transInvDetalleRepository
    )
  }

  def 'regresa lista vacia al buscar clientes con parametros nulos o vacios'( ) {
    when:
    def actual = controller.findCustomers( customer )

    then:
    1 * clienteService.buscarCliente( _, _, _ ) >> [ ]

    then:
    0 * _

    then:
    actual == expected

    where:
    expected | customer
    [ ]      | null
    [ ]      | new Customer()
    [ ]      | new Customer( name: ' ' )
    [ ]      | new Customer( fathersName: ' ' )
    [ ]      | new Customer( mothersName: ' ' )
    [ ]      | new Customer( name: ' ', fathersName: ' ' )
    [ ]      | new Customer( name: ' ', mothersName: ' ' )
    [ ]      | new Customer( fathersName: ' ', mothersName: ' ' )
    [ ]      | new Customer( name: ' ', fathersName: ' ', mothersName: ' ' )
  }

  def 'regresa null al buscar cliente por default con cliente null'( ) {
    when:
    def actual = controller.findDefaultCustomer()

    then:
    1 * clienteService.obtenerClientePorDefecto()

    then:
    0 * _

    then:
    null == actual
  }

  def 'regresa cliente al buscar cliente por default'( ) {
    given:
    def expected = new Customer(
        id: 1,
        name: 'Publico General',
        gender: GenderType.MALE,
        type: CustomerType.DOMESTIC,
        rfc: CustomerType.DOMESTIC.rfc,
        address: new Address()
    )
    def cliente = new Cliente(
        id: 1,
        nombre: 'Publico General',
        rfc: CustomerType.DOMESTIC.rfc
    )

    when:
    def actual = controller.findDefaultCustomer()

    then:
    1 * clienteService.obtenerClientePorDefecto() >> cliente

    then:
    0 * _

    then:
    assertCustomerEquals( expected, actual )
  }

  def 'regresa cliente registrado al agregar cliente extranjero'( ) {
    given:
    def expected = new Customer(
        id: 1,
        name: 'JOHN',
        fathersName: 'DOE',
        type: CustomerType.FOREIGN,
        address: new Address(
            city: 'NEW YORK',
            country: 'USA'
        )
    )
    def customer = new Customer(
        name: 'JOHN',
        fathersName: 'DOE',
        type: CustomerType.FOREIGN,
        address: new Address(
            city: 'NEW YORK',
            country: 'USA'
        )
    )
    def cliente = new Cliente(
        id: 1,
        nombre: 'JOHN',
        apellidoPaterno: 'DOE'
    )
    def clientePais = new ClientePais(
        id: 1,
        ciudad: 'NEW YORK',
        pais: 'USA'
    )

    when:
    def actual = controller.addCustomer( customer )

    then:
    1 * estadoService.obtenEstadoPorNombre( _ )
    1 * localidadService.listaLocalidadesPorCodigoYNombre( _, _ )
    1 * clienteService.agregarCliente( { Cliente tmp ->
      assert tmp.nombre == cliente.nombre
      assert tmp.apellidoPaterno == cliente.apellidoPaterno
      assert tmp.apellidoMaterno == cliente.apellidoMaterno
      return tmp
    } as Cliente ) >> cliente
    1 * clienteService.agregarClientePais( { ClientePais tmp ->
      assert tmp.id == clientePais.id
      assert tmp.ciudad == clientePais.ciudad
      assert tmp.pais == clientePais.pais
      assert tmp.fecha?.dateString == clientePais.fecha?.dateString
      return tmp
    } as ClientePais ) >> clientePais

    then:
    0 * _

    then:
    assertCustomerEquals( expected, actual )
  }


}
