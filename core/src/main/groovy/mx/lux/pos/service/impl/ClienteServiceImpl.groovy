package mx.lux.pos.service.impl

import groovy.util.logging.Slf4j
import mx.lux.pos.model.*
import mx.lux.pos.repository.*
import mx.lux.pos.repository.impl.RepositoryFactory
import mx.lux.pos.service.ClienteService
import mx.lux.pos.service.business.Registry
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

@Slf4j
@Service( 'clienteService' )
@Transactional( readOnly = true )
class ClienteServiceImpl implements ClienteService {

  @Resource
  private ClienteRepository clienteRepository

  @Resource
  private TituloRepository tituloRepository

  @Resource
  private DominioRepository dominioRepository

  @Resource
  private RecetaRepository recetaRepository

  @Resource
  private ConvenioRepository convenioRepository

  @Resource
  private ParametroRepository parametroRepository

  @Resource
  private ClientePaisRepository clientePaisRepository

  @Resource
  private ClienteProcesoRepository clienteProcesoRepository

  @Resource
  private NotaVentaRepository notaVentaRepository

  @Resource
  private SucursalRepository sucursalRepository

  private static final Integer TAG_ID_TELEFONO = 4
  private static final Integer TAG_ID_SMS = 3
  private static final Integer TAG_ID_CORREO = 1
  private static final String TAG_CANCELADA = "T"

  protected void eliminarCliente( Integer id ) {
    if ( !Registry.genericCustomer.equals( id ) ) {
      try {
        ClientePais cp = clientePaisRepository.findOne( id )
        if ( cp != null ) {
          clientePaisRepository.delete( cp.id )
          clientePaisRepository.flush()
        }
        Cliente c = clienteRepository.findOne( id )
        if ( c != null ) {
          clienteRepository.delete( c.id )
          clienteRepository.flush()
        }
        log.debug( String.format( "Se elimino cliente:%d", id ) )
      } catch ( Exception e ) {
        log.debug( String.format( "Error al eliminar cliente: %d", id ) )
        log.debug( e.getMessage() )
      }
    }
  }

  @Override
  Cliente obtenerCliente( Integer id ) {
    log.debug( "obteniendo cliente id: ${id}" )
      Cliente cliente =  clienteRepository.findOne( id )
      if(cliente ==null){
          cliente = new Cliente()
      }
   return  cliente
  }

  @Override
  List<Cliente> buscarCliente( String nombre, String apellidoPaterno, String apellidoMaterno ) {
    log.debug( "buscando cliente: $nombre $apellidoPaterno $apellidoMaterno" )
    nombre = StringUtils.trimToNull( nombre )
    apellidoPaterno = StringUtils.trimToNull( apellidoPaterno )
    apellidoMaterno = StringUtils.trimToNull( apellidoMaterno )
    if ( nombre || apellidoPaterno || apellidoMaterno ) {
      def result = clienteRepository.findByNombreApellidos( nombre, apellidoPaterno, apellidoMaterno )
      log.debug( "se obtiene lista con: ${result?.size()} elementos" )
      return result
    }
    log.warn( "parametros insuficientes" )
    return [ ] as List<Cliente>
  }

  Cliente agregarCliente( Cliente cliente ) {
    return agregarCliente( cliente, null, null )
  }

    @Override
    Cliente actualizaCliente(Cliente cliente) {
        return clienteRepository.saveAndFlush( cliente )

    }

    @Override
  @Transactional
  Cliente agregarCliente( Cliente cliente, String city, String country ) {
    log.debug( "agregando cliente: ${cliente?.dump()}" )
    if ( StringUtils.isNotBlank( cliente?.nombre ) ) {
      cliente.idSucursal = sucursalRepository.getCurrentSucursalId()
      try {
        if ( cliente.id != null ) {
          this.eliminarCliente( cliente.id )
        }
        cliente = clienteRepository.saveAndFlush( cliente )
        if ( StringUtils.isNotBlank( country ) ) {
          ClientePais clientePais = new ClientePais(
              id: cliente.id,
              ciudad: city,
              pais: country
          )
          try {
            clientePais = clientePaisRepository.save( clientePais )
            clientePaisRepository.flush()
            log.debug( "clientePais registrado id: ${clientePais.id}" )
          } catch ( ex ) {
            log.error( "problema al registrar clientePais: ${clientePais?.dump()}", ex )
          }
        }
        log.debug( "se registra cliente con id: ${cliente?.id}" )
        return cliente
      } catch ( ex ) {
        log.error( "problema al registrar cliente: ${cliente?.dump()}", ex )
      }
    }
    return null
  }

  @Override
  Cliente obtenerClientePorDefecto( ) {
    log.debug( "obteniendo cliente generico" )

    Parametro idClienteParametro = parametroRepository.findOne( TipoParametro.ID_CLIENTE_GENERICO.value )

   String idCliente = idClienteParametro?.valor

    if ( idCliente?.isInteger() ) {
      return clienteRepository.findOne( idCliente?.toInteger() )
    }
    return null
  }

  @Override
  List<Titulo> listarTitulosClientes( ) {
    log.debug( "listando titulos de clientes" )
    List<Titulo> titles = new ArrayList<Titulo>()
    titles.addAll( tituloRepository.findAll() )
    return titles
  }

  @Override
  List<Dominio> listarDominiosClientes( ) {
    log.debug( "listando dominios frecuentes" )
    dominioRepository.findByNombreNotNull()
  }

  @Transactional
  void agregarClienteProceso( ClienteProceso clienteProceso) {
    ClienteProcesoRepository repository = RepositoryFactory.customersOnSite
    /*
    ClienteProceso clienteProc = repository.findOne( idCliente )
    if ( clienteProc == null ) {
      clienteProc = new ClienteProceso()
      clienteProc.idCliente = idCliente
      clienteProc.idSucursal = Registry.currentSite
      clienteProc.idMod = idEmpleado
      clienteProc.etapa = ClienteProcesoEtapa.SALES
    }
    clienteProc.fechaMod = new Date()
    */
    try {

      repository.save( clienteProceso )

    } catch ( Exception e ) {

      this.log.error( e.getMessage() )

    }
  }

  List<ClienteProceso> obtenerClientesEnProceso( Boolean pLoaded ) {
    ClienteProcesoRepository onSite = RepositoryFactory.customersOnSite
    ClienteRepository customerRep = RepositoryFactory.customerCatalog
    List<ClienteProceso> customers = new ArrayList<ClienteProceso>()
    List<ClienteProceso> customersTmp = new ArrayList<ClienteProceso>()
    customersTmp.addAll( onSite.findByEtapa( ClienteProcesoEtapa.PAYMENT.toString() ) )
    if ( pLoaded ) {
      for(ClienteProceso customer : customersTmp){
        Cliente cliente = customerRep.findOne( customer.idCliente )
        if( cliente != null ){
          customers.add( customer )
        } else {
          onSite.delete( customer.idCliente )
          onSite.flush()
        }
      }
      for ( ClienteProceso cliente : customers ) {
        this.llenarCliente( cliente )
        this.llenarNotaVentas( cliente )
      }
    }
    return customers
  }

  List<ClienteProceso> obtenerClientesEnCaja( Boolean pLoaded ) {
    ClienteProcesoRepository onSite = RepositoryFactory.customersOnSite
    List<ClienteProceso> customers = new ArrayList<ClienteProceso>()

    customers.addAll( onSite.findByEtapa( ClienteProcesoEtapa.PAYMENT.toString() ) )
    if ( pLoaded ) {
      for ( ClienteProceso cliente : customers ) {
        this.llenarCliente( cliente )
        this.llenarNotaVentas( cliente )
      }
    }
    return customers
  }

  void llenarCliente( ClienteProceso pCliente ) {
    pCliente.setCliente( clienteRepository.findOne( pCliente.idCliente ) )
  }

  void llenarNotaVentas( ClienteProceso pCliente ) {
    List<NotaVenta> orders = RepositoryFactory.orders.findByIdCliente( pCliente.idCliente )
    List<NotaVenta> openOrders = new ArrayList<NotaVenta>()
    for ( NotaVenta order : orders ) {
      if ( StringUtils.isBlank( order.factura ) ) {
        openOrders.add( order )
      }
    }
    pCliente.setNotaVentas( openOrders )
  }

  @Transactional
  void actualizarClienteEnProceso( Integer pIdCliente ) {
    ClienteProcesoRepository repository = RepositoryFactory.customersOnSite

    /* Problema 1.- Problema con boton "Continuar" cuando no hay receta y es cliente "Publico General".
           --Generado por que no se almacena o existe el cliente en la tabla ClienteProceso


        //Codigo Erroneo
      ClienteProceso clienteProc = repository.findOne( pIdCliente )
    ClienteProcesoEtapa etapa = ClienteProcesoEtapa.parse( clienteProc.etapa )

          this.llenarNotaVentas( clienteProc )
          for ( NotaVenta order : clienteProc.notaVentas ) {
              if ( order.detalles.size() > 0 ) {
                  etapa = ClienteProcesoEtapa.PAYMENT
              }
          }
          clienteProc.etapa = etapa.toString()
          repository.save( clienteProc )
        //Fin Codigo Erroneo
      */



     ClienteProceso nuevo = new ClienteProceso()
      nuevo.setIdCliente(pIdCliente)
      nuevo.setEtapa('proceso')
      nuevo.setFechaMod( new Date())
      nuevo.setIdSucursal(82)
      nuevo.setIdSync('1')
      nuevo.setIdSucursal(9999)
      nuevo.setIdMod('1')
      repository.saveAndFlush(nuevo)

      log.info('cliente almacenado en ClienteProceso')

      ClienteProceso clienteProc = repository.findOne( pIdCliente )
    ClienteProcesoEtapa etapa = ClienteProcesoEtapa.parse( clienteProc.etapa )
      if (pIdCliente == 1) {
          log.info('*prueba1')
  } else {
          this.llenarNotaVentas( clienteProc )
          for ( NotaVenta order : clienteProc.notaVentas ) {
              if ( order.detalles.size() > 0 ) {
                  etapa = ClienteProcesoEtapa.PAYMENT
              }
          }
          clienteProc.etapa = etapa.toString()
          repository.save( clienteProc )
      }

   // Fin Problema 1
  }
    @Transactional
  void eliminarClienteProceso( Integer pIdCliente ) {
    ClienteProcesoRepository repository = RepositoryFactory.customersOnSite
    ClienteProceso clienteProc = repository.findOne( pIdCliente )
    if ( clienteProc != null ) {
      repository.delete( clienteProc )
    }
  }

  @Override
  List<Receta> obtenerRecetas( Integer idCliente ) {
    log.debug( "obteniendo recetas" )
    List<Receta> lstRecetas = new ArrayList<Receta>()
    if ( idCliente != null ) {
      lstRecetas.addAll( recetaRepository.findByIdCliente( idCliente ) )
      Collections.sort( lstRecetas, this.getRecetaSorter( ) )
    }
    return lstRecetas
  }

  private Comparator<Receta> recetaSorter
  private Comparator<Receta> getRecetaSorter() {
    if (this.recetaSorter == null) {
      this.recetaSorter = new Comparator<Receta>() {
        int compare( Receta receta_1, Receta receta_2 ) {
          return (-1 * receta_1.fechaReceta.compareTo( receta_2.fechaReceta ))
        }
      }
    }
    return this.recetaSorter
  }

  private Comparator<Cliente> clienteSorter
  protected Comparator<Cliente> getClienteSorter() {
    if (this.clienteSorter == null) {
      this.clienteSorter = new Comparator<Cliente>() {
        int compare( Cliente cliente_1, Cliente cliente_2 ) {
          return (cliente_1.nombreCompleto.compareToIgnoreCase( cliente_2.nombreCompleto ))
        }
      }
    }
    return this.clienteSorter
  }

  List<Cliente> listBasedOnHint( String pHint ) {
    List<Cliente> custList = clienteRepository.listByTextContainedInName( pHint )
    custList.remove( Registry.genericCustomer )
    Collections.sort( custList, this.getClienteSorter() )
    return custList
  }

  List<Cliente> listAll( ) {
    List<Cliente> custList = clienteRepository.findAll()
    custList.remove( Registry.genericCustomer )
    Collections.sort( custList, this.getClienteSorter() )
    return custList
  }

    @Override
    ClienteProceso obtieneClienteProceso( Integer idCliente ) {
        return clienteProcesoRepository.findOne( idCliente )
    }


  @Transactional
  void eliminarTodoClienteProceso(  ) {
      ClienteProcesoRepository repository = RepositoryFactory.customersOnSite
      repository.deleteAll()
  }


  List<ClienteProceso> obtenerClientesMultipago(  ) {
      ClienteProcesoRepository onSite = RepositoryFactory.customersOnSite
      NotaVentaRepository notaVenaRepository = RepositoryFactory.orders
      DateUtils
      Date fechaStart = DateUtils.truncate( new Date(), Calendar.DAY_OF_MONTH )
      Date fechaEnd = new Date( DateUtils.ceiling( new Date(), Calendar.DAY_OF_MONTH ).getTime() - 1 )
      Integer idCliente = 0
      List<ClienteProceso> customers = new ArrayList<ClienteProceso>()
      List<ClienteProceso> lstClientes = new ArrayList<ClienteProceso>()
      QClienteProceso cp = QClienteProceso.clienteProceso
      customers = onSite.findAll( cp.fechaMod.between(fechaStart,fechaEnd), cp.idCliente.asc() ) as List<ClienteProceso>
      Integer idClienteGenerico = Registry.genericCustomer.id
      for ( ClienteProceso cliente : customers ) {
        List<NotaVenta> notas = new ArrayList<>()
        QNotaVenta nv = QNotaVenta.notaVenta
        List<NotaVenta> notasTmp = (List<NotaVenta>)notaVentaRepository.findAll(nv.idCliente.eq(cliente.idCliente).
                and(nv.fechaHoraFactura.between(fechaStart,fechaEnd)).and(nv.sFactura.ne('T')).
                and(nv.fechaPrometida.isNull()).and(nv.idCliente.ne(idClienteGenerico)))
        for(NotaVenta nota : notasTmp){
          if(nota.detalles.size() > 0){
            notas.add(nota)
          }
        }
          if( notas.size() > 1 ){
            this.llenarCliente( cliente )
            this.llenarNotaVentas( cliente )
            lstClientes.add( cliente )
          }
        idCliente = cliente.idCliente
      }
      return lstClientes
  }

  @Override
  void saveMainFC ( Integer idCliente, Integer formaContacto ){
      Cliente cliente = clienteRepository.findOne( idCliente )
      cliente.principal = formaContacto
      clienteRepository.save( cliente )
      clienteRepository.flush()
  }


  @Override
  List<NotaVenta> obtenernotasActivas( Integer idCliente ){
    QNotaVenta qNotaVenta = QNotaVenta.notaVenta
    List<NotaVenta> lstNotas = notaVentaRepository.findAll( qNotaVenta.idCliente.eq(idCliente).
            and(qNotaVenta.factura.isNotNull()).and(qNotaVenta.factura.isNotEmpty()) )
    return lstNotas
  }


}
