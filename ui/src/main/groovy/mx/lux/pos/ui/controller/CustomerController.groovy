package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.java.querys.ImpHistorialQuery
import mx.lux.pos.java.querys.MunicipioQuery
import mx.lux.pos.java.repository.ClientesJava
import mx.lux.pos.java.repository.ExamenJava
import mx.lux.pos.java.repository.ImpHistorialJava
import mx.lux.pos.java.repository.MunicipioJava
import mx.lux.pos.java.service.ClienteServiceJava
import mx.lux.pos.java.service.ContactoServiceJava
import mx.lux.pos.java.service.ExamenServiceJava
import mx.lux.pos.java.service.NotaVentaServiceJava
import mx.lux.pos.java.service.RecetaServiceJava
import mx.lux.pos.model.*
import mx.lux.pos.java.querys.RecetaQuery
import mx.lux.pos.java.repository.ClientesProcesoJava
import mx.lux.pos.java.repository.RecetaJava
import mx.lux.pos.service.*
import mx.lux.pos.service.business.Registry
import mx.lux.pos.service.impl.FormaContactoService
import mx.lux.pos.ui.model.*
import mx.lux.pos.ui.view.dialog.*
import mx.lux.pos.ui.view.panel.OrderPanel
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.swing.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@Slf4j
@Component
class CustomerController {


    private static ClienteService clienteService
    private static EstadoService estadoService
    private static MunicipioService municipioService
    private static LocalidadService localidadService
    private static ConvenioService convenioService
    private static EmpleadoService empleadoService
    private static RecetaService recetaService
    private static ExamenService examenService
    private static SucursalService sucursalService
    private static PaisesService paisesService
    private static NotaVentaService notaService
    private static NotaVentaServiceJava notaServiceJava
    private static ContactoService contactoService
    private static ContactoServiceJava contactoServiceJava
    private static FormaContactoService formaContactoService
    private static ClienteServiceJava clienteServiceJava

    @Autowired
    public CustomerController(
            ClienteService clienteService,
            EstadoService estadoService,
            MunicipioService municipioService,
            LocalidadService localidadService,
            ConvenioService convenioService,
            EmpleadoService empleadoService,
            RecetaService recetaService,
            ExamenService examenService,
            SucursalService sucursalService,
            PaisesService paisesService,
            NotaVentaService notaService,
            ContactoService contactoService,
            FormaContactoService formaContactoService
    ) {
        this.clienteService = clienteService
        this.estadoService = estadoService
        this.municipioService = municipioService
        this.localidadService = localidadService
        this.convenioService = convenioService
        this.empleadoService = empleadoService
        this.recetaService = recetaService
        this.examenService = examenService
        this.sucursalService = sucursalService
        this.paisesService = paisesService
        this.notaService = notaService
        this.contactoService = contactoService
        this.formaContactoService = formaContactoService
        clienteServiceJava = new ClienteServiceJava()
        contactoServiceJava = new ContactoServiceJava()
        notaServiceJava = new NotaVentaServiceJava()
    }


    static List<String> findAllStates() {
        log.debug("obteniendo lista de nombres de los estados")
        def results = estadoService.listaEstados()
        results.collect {
            it.nombre
        }
    }

    static String findStateById(String id) {
        log.info("obteniendo nombre de estado con id: ${id}")
        Estado result = estadoService.obtenerEstado(id)
        return result?.nombre ?: null
    }

    static String findDefaultState() {
        log.debug("obteniendo nombre estado por default")
        Estado result = estadoService.obtenEstadoPorDefecto()
        return result?.nombre ?: null
    }

    static List<String> findCitiesByStateName(String stateName) {
        log.debug("obteniendo lista de ciudades con nombre estado: ${stateName}")
        def results = municipioService.listaMunicipiosPorEstado(stateName)
        results.collect {
            it.nombre
        }
    }

    static List<LinkedHashMap<String, String>> findLocationsByStateNameAndCityName(String stateName, String cityName) {
        log.debug("obteniendo localidades con nombre estado: ${stateName} y nombre ciudad: ${cityName}")
        def results = localidadService.listaLocalidadesPorEstadoYMunicipio(stateName, cityName)
        results.collect {
            [
                    location: it?.usuario,
                    zipcode: it?.codigo
            ]
        }
    }

    static List<Address> findAddresesByZipcode(String zipcode) {
        log.debug("obteniendo lista de address con zipcode: ${zipcode}")
        def results = localidadService.listaLocalidadesPorCodigo(zipcode)
        results?.collect {
            new Address(
                    zipcode: it?.codigo,
                    location: it?.usuario,
                    city: it?.municipio?.nombre,
                    state: it?.municipio?.estado?.nombre
            )
        }
    }

    static Customer getCustomer(Integer id) {
        log.debug("obteniendo cliente id: ${id}")
        def result = clienteService.obtenerCliente(id)
        Customer.toCustomer(result)
    }

    static List<Customer> findCustomers(Customer sample) {
        log.debug("obteniendo lista de customer similar a: ${sample}")
        def results = clienteService.buscarCliente(sample?.name, sample?.fathersName, sample?.mothersName)
        log.debug("se obtiene lista con: ${results?.size()} customers")
        results.collect {
            Customer.toCustomer(it)
        }
    }

    static List<Customer> findCustomersFechaNacimientoApellidoPaterno(Customer sample) {

        log.debug("findCustomersFechaNacimientoApellidoPaterno ($sample)")

        def results = clienteService.buscarClienteApellidoPatAndFechaNac(sample?.fathersName, sample?.fechaNacimiento)
        log.debug("se obtiene lista con: ${results?.size()} customers")
        results.collect {
            Customer.toCustomer(it)
        }

//        return results
    }

    static ClienteProceso addClienteProceso(Customer tmpCustomer) {
        Branch branch = Session.get(SessionItem.BRANCH) as Branch
        ClienteProceso clientePro = new ClienteProceso()
        String IdSync = '1'
        String IdMod = '0'
        clientePro.setIdCliente(tmpCustomer?.id)
        clientePro.setEtapa(ClienteProcesoEtapa.PAYMENT.toString())
        clientePro.setIdSync(IdSync)
        clientePro.setFechaMod(new Date())
        clientePro.setIdMod(IdMod)
        clientePro.setIdSucursal(branch?.id)
        clienteService.agregarClienteProceso(clientePro)
    }

    static Customer addCustomer(Customer customer) {
        log.debug("registrando cliente: ${customer?.dump()}")
        if (StringUtils.isNotBlank(customer?.name)) {
            Estado estado = estadoService.obtenEstadoPorNombre(customer?.address?.state)
            def results = localidadService.listaLocalidadesPorCodigoYNombre(customer.address?.zipcode, customer.address?.location)
            log.debug("resultados de localidades: ${results*.usuario}")
            def localidad = results?.any() ? results?.first() : null
            Cliente cliente = new Cliente()
            cliente.id = customer.id
            cliente.nombre = customer.name
            cliente.apellidoPaterno = customer.fathersName
            cliente.apellidoMaterno = customer.mothersName
            cliente.titulo = customer.title
            cliente.sexo = customer.gender?.equals(GenderType.MALE)
            cliente.fechaNacimiento = customer.dob
            cliente.direccion = customer.address?.primary
            cliente.colonia = customer.address?.location
            cliente.codigo = customer.address?.zipcode
            cliente.rfc = customer.rfc ?: customer.type?.rfc
            cliente.idEstado = localidad?.idEstado ?: estado?.id
            cliente.idLocalidad = localidad?.idLocalidad
            cliente.udf1 = customer.age
            if( StringUtils.trimToEmpty(customer.idBranch).length() > 0 ){
              Integer idSuc = Registry.currentSite
              try{
                idSuc = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(customer.idBranch))
              } catch ( NumberFormatException e ) { println e }
              cliente.idSucursal = idSuc
            }
            if( StringUtils.trimToEmpty(customer.cliOri).length() > 0 ){
              cliente.ori = StringUtils.trimToEmpty(customer.cliOri)
            }
            if( StringUtils.trimToEmpty(customer.histCuc).length() > 0 ){
              cliente.histCuc = StringUtils.trimToEmpty(customer.histCuc)
            }
            if( StringUtils.trimToEmpty(customer.histCli).length() > 0 ){
              cliente.histCli = StringUtils.trimToEmpty(customer.histCli)
            }
            cliente = clienteService.agregarCliente(cliente)
            if (cliente != null) {
                customer.id = cliente.id
            }
            return customer
        }
        return null
    }

    static Customer findDefaultCustomer() {
      log.debug("obteniendo customer por default")
      Customer.toCustomer(clienteServiceJava.obtenerClientePorDefecto())
    }

    static Customer findDefaultCustomerJava() {
      log.debug("obteniendo customer por default")
      Customer.toCustomer(clienteServiceJava.obtenerClientePorDefecto())
    }

    static List<LinkedHashMap<String, Object>> findAllCustomersTitles() {
        log.debug("obteniendo lista de titulos")
        def results = clienteService.listarTitulosClientes()
        results?.collect {
            [
                    title: it?.titulo,
                    gender: it?.sexoTitulo
            ]
        }
    }

    static List<String> findAllCustomersDomains() {
      log.debug("obteniendo lista de dominios")
      def results = clienteServiceJava.listarDominiosClientes()
      results?.collect {
        it?.nombre
      }
    }


    static List<LinkedHashMap<String, Object>> findAllConventions(String clave) {
        log.debug("obteniendo lista de convenios")
        def results = convenioService.obtenerConvenios(clave)
        results?.collect {
            [
                    id: it?.id,
                    iniciales: it?.inicialesIc
            ]
        }
    }


    static void saveContact(Customer customer, Integer tipo, String valor) {
        Cliente cliente = clienteService?.obtenerCliente(customer?.id)
        if (tipo == 0) {
            cliente?.email = valor
        } else if (tipo == 1) {
            cliente?.telefonoTrabajo = valor
        } else if (tipo == 2) {
            cliente?.telefonoCasa = valor
        } else if (tipo == 3) {
            cliente?.telefonoAdicional = valor
        }
        clienteService.actualizaCliente(cliente)
    }


    static void requestNewCustomer(CustomerListener pListener) {
        this.log.debug('Request New Customer')

        // Llamamos ventana de busqueda de clientes
//        BusquedaClientePanel busquedaCliente = new BusquedaClientePanel(pListener);

//        this.log.debug( 'Por visualizar' )
//        busquedaCliente.setVisible(true)
        pListener.operationTypeSelected = OperationType.DEFAULT
        Customer customer = new Customer()
        NewCustomerAndRxDialog dialog = new NewCustomerAndRxDialog(pListener, customer, false)
        dialog.setVisible(true)
        pListener.operationTypeSelected = OperationType.DEFAULT
    }


    static void requestCustomerMod(CustomerListener pListener, Customer cliente) {
        this.log.debug('Request New Customer')

        pListener.operationTypeSelected = OperationType.DEFAULT
        NewCustomerAndRxDialog dialog = new NewCustomerAndRxDialog(pListener, cliente, true)
        dialog.setVisible(true)
        pListener.operationTypeSelected = OperationType.DEFAULT
    }

    static void requestPendingCustomer(CustomerListener pListener) {
        this.log.debug('Request Customer on Site ')
        List<ClienteProceso> clientes = clienteService.obtenerClientesEnProceso(true)
        CustomerActiveSelectionDialog dialog = new CustomerActiveSelectionDialog()
        dialog.customerList = clientes
        dialog.activate()

        if (dialog.customerSelected != null) {
            Customer c = Customer.toCustomer(dialog.customerSelected.cliente)
            pListener.reset()
            pListener.disableUI()
            pListener.operationTypeSelected = OperationType.PENDING
            pListener.setCustomer(c)
            pListener.enableUI()
        } else if (dialog.isNewRequested()) {
            requestNewCustomer(pListener)
        } else {
            pListener.operationTypeSelected = OperationType.DEFAULT
        }
        dialog.dispose()
    }

    static void requestBusquedaCliente(CustomerListener pListener) {
      log.debug('Request Busqueda Cliente ')
      BusquedaClienteDialog dialog = new BusquedaClienteDialog(pListener)
      dialog.activate()
      if (dialog.isNewRequested())
        requestNewCustomer(pListener)

      if (dialog.isModRequested()) {
        Cliente cliente = clienteService.obtenerCliente( dialog.getCustomerSelected().id )
        Customer customer = Customer.toCustomer(cliente)
        requestCustomerMod( pListener, customer )
      }
    }

    static Order requestOrderByCustomer(CustomerListener pListener, Customer customer) {
      Order order = Order.toOrder(notaServiceJava.obtenerSiguienteNotaVenta(customer?.id))
      if (order == null) {
        Integer nueva = JOptionPane.showConfirmDialog(null, "Nueva Venta", "¿Desea abrir una nueva venta?", JOptionPane.YES_NO_OPTION);
        if (nueva == 0) {
          pListener.reset()
          pListener.disableUI()
          pListener.operationTypeSelected = OperationType.EDIT_PAYING
          pListener.setCustomer(customer)
          pListener.enableUI()
        } else {
          pListener.reset()
        }
      } else {
        pListener.reset()
        pListener.disableUI()
        pListener.operationTypeSelected = OperationType.PAYING
        pListener.setCustomer(customer)
        pListener.setOrder(order)
        pListener.setPromotion(order)
        pListener.enableUI()
      }
      return order
    }

    static void requestPayingCustomer(CustomerListener pListener, OperationType type ) {
      log.debug('Request Customer on Site ')
      //List<ClienteProceso> clientes = clienteService.obtenerClientesEnCaja(true)
      List<ClientesProcesoJava> clientes = clienteServiceJava.obtenerClientesEnCaja(true)
      OrderActiveSelectionDialog dialog = new OrderActiveSelectionDialog()
      dialog.customerList = clientes
      dialog.activate()
      if (dialog.orderSelected != null) {
        Order o = Order.toOrder(dialog.orderSelected.order)
        Customer c = Customer.toCustomer(dialog.orderSelected.customer)
        pListener.resetJava()
        pListener.disableUI()
        pListener.operationTypeSelected = type//OperationType.PAYING
        pListener.setCustomer(c)
        pListener.setOrder(o)
        pListener.setPromotion(o)
        pListener.enableUI()
      } else {
        pListener.operationTypeSelected = OperationType.DEFAULT
      }
      dialog.dispose()
    }


    static void requestEditPayingCustomer(CustomerListener pListener) {
        this.log.debug('Request Customer on Site ')
        List<ClienteProceso> clientes = clienteService.obtenerClientesEnCaja(true)
        OrderActiveSelectionDialog dialog = new OrderActiveSelectionDialog()
        dialog.customerList = clientes
        dialog.activate()
        if (dialog.orderSelected != null) {
            Order o = Order.toOrder(dialog.orderSelected.order)
            Customer c = Customer.toCustomer(dialog.orderSelected.customer)
            pListener.reset()
            pListener.disableUI()
            pListener.operationTypeSelected = OperationType.EDIT_PAYING
            pListener.setCustomer(c)
            pListener.setOrder(o)
            pListener.enableUI()
            pListener.setPromotion(o)
        } else {
            pListener.operationTypeSelected = OperationType.DEFAULT
        }
        dialog.dispose()
    }


    private static Customer openCustomerDialog(Customer customer, boolean editar) {
        log.error('llamando metodo deprecado: openCustomerDialog')
        return null
    }

    static void updateCustomerInSite(Integer idCliente) {
      //clienteService.actualizarClienteEnProceso(idCliente)
      clienteServiceJava.actualizarClienteEnProceso( idCliente )
    }

    static List<Rx> findAllPrescriptions(Integer idCliente) {
        log.debug("obteniendo recetas")
        //def results = clienteService.obtenerRecetas(idCliente)
        def results = RecetaQuery.buscaRecetasPorIdCliente(idCliente)
        results.collect {
            Rx.toRx(it)
        }
    }


    static String findOptometrista(String idOptometrista) {
        log.debug("obteniendo Optometrista")
        Empleado optometrista = empleadoService.obtenerEmpleado(idOptometrista)
        return optometrista?.nombreCompleto
    }

    static Integer findCurrentSucursal() {
        log.debug("obteniendo sucursal actual")
        Integer idSucursal = sucursalService.obtenSucursalActual().id
        return idSucursal
    }


    public static RecetaJava saveRx(Rx receta, String tipo) {
      log.debug("salvando Receta")
      RecetaJava rec = new RecetaJava()
      if (receta?.id != null) {
            rec.setIdReceta(receta.id)
            rec.setExamen(receta.exam)
            rec.setFechaReceta(receta.rxDate)
            rec.setTipoOpt(receta.typeOpt)
            rec.setIdCliente(receta.idClient)
            rec.setfImpresa(receta.fPrint)
            rec.setIdSync(receta.idSync)
            rec.setFechaMod(new Date())
            rec.setIdMod(receta.modId)
            rec.setIdSucursal(receta.idStore)
            rec.setMaterialArm(receta.materialArm)
            rec.setTratamientos(receta.treatment)
            rec.setUdf5(receta.udf5)
            rec.setUdf6(receta.udf6)
            rec.setIdRxOri(receta.idRxOri)
            rec.setIdOptometrista(receta.idOpt)
            rec.setFolio(receta.folio)
            rec.setsUsoAnteojos(receta.useGlasses)
            rec.setOdEsfR(receta.odEsfR)
            rec.setOdCilR(receta.odCilR)
            rec.setOdEjeR(receta.odEjeR.trim())
            rec.setOdAdcR(receta.odAdcR)
            rec.setOdAdiR(receta.odAdiR)
            rec.setOdAvR(receta.odAvR.substring(3))
            rec.setDiOd(receta.diOd)
            rec.setOdPrismaH(receta.odPrismH)
            rec.setOdPrismaV(receta.odPrismaV ?: '')
            rec.setDiLejosR(receta.diLejosR)
            rec.setOiEsfR(receta.oiEsfR)
            rec.setOiCilR(receta.oiCilR)
            rec.setOiEjeR(receta.oiEjeR.trim())
            rec.setOiAdcR(receta.oiAdcR)
            rec.setOiAdiR(receta.oiAdiR)
            rec.setOiAvR(receta.oiAvR.substring(3))
            rec.setDiOi(receta.diOi)
            rec.setOiPrismaH(receta.oiPrismH)
            rec.setOiPrismaV(receta.oiPrismaV ?: '')
            rec.setDiCercaR(receta.diCercaR)
            rec.setAltOblR(receta.altOblR.trim())
            rec.setObservacionesR(receta.observacionesR)
            //rec = recetaService.guardarReceta(rec)
        rec = RecetaQuery.saveOrUpdateRx( rec )
      } else {
            Examen examen = examenService.obtenerExamenPorIdCliente(receta.idClient)
            if (examen != null) {

            } else {
                examen = new Examen()
                examen.setIdCliente(receta.idClient)
                examen.setIdAtendio(receta.idOpt)
                examen.setIdSync('1')
                examen.setFechaMod(new Date())
                examen.setId_mod('0');
                examen.setIdSucursal(receta.idStore)
                examen.setFechaAlta(new Date())
                examen.setTipoOft(tipo)
                examen = examenService.guardarExamen(examen)
            }
            rec.setExamen(examen.id)
            rec.setFechaReceta(new Date())
            rec.setTipoOpt('')
            rec.setIdCliente(receta.idClient)
            rec.setfImpresa(false)
            rec.setIdSync('1')
            rec.setFechaMod(new Date())
            rec.setIdMod('0')
            rec.setIdSucursal(receta.idStore)
            rec.setMaterialArm('')
            rec.setTratamientos('')
            rec.setUdf5('')
            rec.setUdf6(receta.udf6 != null ? receta.udf6 : "")
            rec.setIdRxOri('')
            rec.setIdOptometrista(receta.idOpt)
            rec.setFolio(receta.folio)
            rec.setsUsoAnteojos(receta.useGlasses)
            rec.setOdEsfR(receta.odEsfR)
            rec.setOdCilR(receta.odCilR)
            rec.setOdEjeR(receta.odEjeR)
            rec.setOdAdcR(receta.odAdcR)
            rec.setOdAdiR(receta.odAdiR)
            rec.setDiOd(receta.diOd)
            rec.setOdPrismaH(receta.odPrismH)
            rec.setOdPrismaV(receta.odPrismaV)
            rec.setDiLejosR(receta.diLejosR)
            rec.setOiEsfR(receta.oiEsfR)
            rec.setOiCilR(receta.oiCilR)
            rec.setOiEjeR(receta.oiEjeR)
            rec.setOiAdcR(receta.oiAdcR)
            rec.setOiAdiR(receta.oiAdiR)
            rec.setDiOi(receta.diOi)
            rec.setOiPrismaH(receta.oiPrismH)
            rec.setOiPrismaV(receta.oiPrismaV)
            rec.setDiCercaR(receta.diCercaR)
            rec.setAltOblR(receta.altOblR.trim())
            rec.setObservacionesR(receta.observacionesR)
            //rec = recetaService.guardarReceta(rec)
          rec = RecetaQuery.saveOrUpdateRx( rec )
      }
      return rec
    }

    private static SingleCustomerDialog customerDialog

    static SingleCustomerDialog getCustomerDialog() {
        if (customerDialog == null) {
            customerDialog = new SingleCustomerDialog()
        }
        return customerDialog
    }

    private static CustomerBrowserDialog customerBrowser

    static CustomerBrowserDialog getCustomerBrowser() {
        if (customerBrowser == null) {
            customerBrowser = new CustomerBrowserDialog()
        }
        return customerBrowser
    }

    protected static void activateCustomerDialog() {
        getCustomerDialog().disableRx()
        getCustomerDialog().activate()
        if (!getCustomerDialog().cancelled) {
            Customer c = getCustomerDialog().getCustomer()
            log.debug(String.format('Customer: %s (%04d)', c.fullName, c.id))
            addCustomer(c)
        }
    }

    static Integer requestNewLocalCustomer() {
        Customer c = new Customer(type: CustomerType.DOMESTIC)
        getCustomerDialog().setCustomer(c)
        getCustomerDialog().setCurrentMode(SingleCustomerDialog.CustomerMode.DEFAULT)
        return activateCustomerDialog()
    }

    static Integer requestNewStatisticsCustomer() {
        Customer c = new Customer(type: CustomerType.DOMESTIC)
        getCustomerDialog().setCustomer(c)
        getCustomerDialog().setCurrentMode(SingleCustomerDialog.CustomerMode.STATISTICS)
        return activateCustomerDialog()
    }

    static Integer requestNewForeignCustomer() {
        Customer c = new Customer(type: CustomerType.FOREIGN)
        getCustomerDialog().setCustomer(c)
        getCustomerDialog().setCurrentMode(SingleCustomerDialog.CustomerMode.FOREIGN)
        return activateCustomerDialog()
    }

    static Customer requestCustomerBrowser() {
        Customer cust = null
        getCustomerBrowser().activate()
        if (!getCustomerBrowser().cancelled && (getCustomerBrowser().customer != null)) {
            cust = getCustomerBrowser().customer
        } else if (getCustomerBrowser().isNewRequested()) {
            cust = this.requestNewLocalCustomer()
        }
        return cust
    }

    static void requestCustomerDialog(Customer pCustomer) {
        getCustomerDialog().setCustomer(pCustomer)
        activateCustomerDialog()
    }

    static List<Customer> requestCustomerBasedOnHint(String pHint) {
        List<Cliente> clienteList
        if (StringUtils.isNotBlank(pHint)) {
            clienteList = clienteService.listBasedOnHint(pHint.trim())
        } else {
            clienteList = clienteService.listAll()
        }
        return Customer.toList(clienteList)
    }

    static String countryCustomer(Order order) {
        String paisCliente = ''
        Cliente cliente = clienteService.obtenerCliente(order.customer.id)
        if (cliente != null) {
            paisCliente = cliente.clientePais.pais
        }
        return paisCliente
    }

    static List<String> countries() {
        List<String> lstPaises = new ArrayList<>()
        List<Paises> paises = paisesService.obtenerPaises()
        for (Paises pais : paises) {
            lstPaises.add(pais.pais)
        }
        return lstPaises
    }

    static void saveOrderCountries(String pais) {
        paisesService.guardarOrdenPais(pais)
    }

    static void saveCountries(String pais) {
        paisesService.guardarPais(pais)
    }


    static List<String> findAllContactTypes() {
      log.debug("obteniendo lista de nombres de los estados")
      def results = contactoServiceJava.obtenerTiposContacto()
      results.collect {
        it.descripcion
      }
    }

    static List<Rx> findRxByCustomer(Integer idCliente) {
        log.debug(String.format('obteniendo recetas del cliente %s', idCliente))
        Cliente
        List<Receta> results = recetaService.recetaCliente(idCliente)
        results.collect { Rx.toRx(it) }
    }

    static ClienteProceso findProccesClient(Integer idCliente) {
        log.debug('findProccesClient()')
        ClienteProceso cliente = clienteService.obtieneClienteProceso(idCliente)
    }

    static void deletedClienteProceso(Integer idCliente) {
        log.debug('deletedClienteProceso')
        clienteService.eliminarClienteProceso(idCliente)
    }


    static Boolean requestMultypayment(CustomerListener pListener, OrderPanel orderPanel) {
        this.log.debug('Request Customer of requestMultypayment on Site ')
        List<ClienteProceso> clientes = clienteService.obtenerClientesMultipago()
        OrderActiveMultypaymentDialog dialog = new OrderActiveMultypaymentDialog(orderPanel)
        dialog.customerList = clientes
        dialog.activate()
        pListener.operationTypeSelected = OperationType.DEFAULT
        dialog.dispose()
    }


    static List<Rx> requestRxByCustomer(Integer idCliente) {
        List<Rx> lstRx = new ArrayList<>()
        List<Receta> lstRexetas = recetaService.recetaCliente(idCliente)
        //List<RecetaJava> lstRexetas = RecetaQuery.buscaRecetasPorIdCliente(idCliente)
        Collections.sort(lstRexetas, new Comparator<Receta>() {
          @Override
          int compare(Receta o1, Receta o2) {
            return o2.fechaReceta.compareTo(o1.fechaReceta)
          }
        })
        log.debug("Total de Recetas = ${lstRexetas.size()}")
        for (Receta rx : lstRexetas) {
            lstRx.add(Rx.toRx(rx))
        }
        return lstRx
    }

    static List<Rx> requestRxByCustomerTmp(Integer idCliente) {
        List<Rx> lstRx = new ArrayList<>()
        List<Receta> lstRexetas = recetaService.recetaCliente(idCliente)
        //List<RecetaJava> lstRexetas = RecetaQuery.buscaRecetasPorIdCliente(idCliente)
        Collections.sort(lstRexetas, new Comparator<Receta>() {
            @Override
            int compare(Receta o1, Receta o2) {
                return o2.fechaReceta.compareTo(o1.fechaReceta)
            }
        })
        log.debug("Total de Recetas = ${lstRexetas.size()}")
        for (Receta rx : lstRexetas) {
            lstRx.add(Rx.toRx(rx))
        }
        return lstRx
    }


    static void changeMainContact(Integer idCustomer, Integer formaContacto) {
        clienteService.saveMainFC(idCustomer, formaContacto)
    }


    static void requestNewOrder(CustomerListener pListener) {
        pListener.reset()
    }


    static Customer findCustomerById(String idCliente) {
        Integer id = 0
        try {
            id = NumberFormat.getInstance().parse(idCliente)
        } catch (NumberFormatException e) {
            println e
        }
        Cliente cliente = clienteService.obtenerCliente(id)
        return Customer.toCustomer(cliente)
    }


    static void updateCustomer(Customer customer) {
        log.debug("registrando cliente: ${customer?.dump()}")
        if (StringUtils.isNotBlank(customer?.name)) {
            Estado estado = estadoService.obtenEstadoPorNombre(customer?.address?.state)
            def results = localidadService.listaLocalidadesPorCodigoYNombre(customer.address?.zipcode, customer.address?.location)
            log.debug("resultados de localidades: ${results*.usuario}")
            def localidad = results?.any() ? results?.first() : null
            Cliente cliente = clienteService.obtenerCliente(customer.id)

            cliente.nombre = customer.name
            cliente.apellidoPaterno = customer.fathersName
            cliente.apellidoMaterno = customer.mothersName
            cliente.titulo = null
//            cliente.titulo = customer.title
            cliente.sexo = customer.gender?.equals(GenderType.MALE)
            cliente.fechaNacimiento = customer.dob
            cliente.direccion = customer.address?.primary
            cliente.colonia = customer.address?.location
            cliente.codigo = customer.address?.zipcode
            cliente.idEstado = localidad?.idEstado ?: estado?.id
            cliente.idLocalidad = localidad?.idLocalidad
            cliente.udf1 = customer.age
            cliente = clienteService.actualizaCliente(cliente)
        }
    }

    static List<NotaVenta> findAllActiveOrders(Integer idCliente) {
        log.debug("obteniendo notas activas")
        List<NotaVenta> results = clienteService.obtenernotasActivas(idCliente)
        return results
    }

    static Examen buscarUltimoExamenPorIdCliente(Integer id) {
        Examen examen = examenService.obtenerExamenPorIdCliente(id)

        if (examen != null) {
            return examen
        }

        return null
    }

    static NotaVenta buscarUltimaNotaVentaPorIdCliente(Integer id) {
        NotaVenta notaVenta = notaService.obtenerUltimaNotaVentaPorCliente(id)

        if ( notaVenta != null )
            return notaVenta

        return null
    }

    static FormaContacto buscarFormaContactoPorIdClienteTipoContacto(Integer idCliente, Integer tipoContacto) {
        FormaContacto formaContacto = formaContactoService.findByidClienteTipoContacto(idCliente, tipoContacto)

        if ( formaContacto != null )
            return formaContacto

        return null
    }


  static validCustomerApplyCoupon( Integer idCustomer ){
    Boolean valid = true
    if( Registry.validCustomerToApplyCoupon() ){
      if( idCustomer == findDefaultCustomer().id ){
        valid = false
      }
    }
    return valid
  }



  static String findCustomerEmail(Integer idCliente) {
    String email = ""
    Cliente cliente = clienteService.obtenerCliente( idCliente )
    if( cliente != null ){
      email = StringUtils.trimToEmpty(cliente.email)
    }
    return email
  }


  static List<Customer> listCustomersFromMainDb(Customer sample) {
    log.debug("obteniendo lista de customer similar a: ${sample}")
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
    List<Customer> lstCustomers = new ArrayList<>();
    ExecutorService executor = Executors.newFixedThreadPool(1);
    List<String> respuesta = new ArrayList<>();
    int timeoutSecs = 20;
    String url = Registry.listCustomersUrl
    String variable = "${StringUtils.trimToEmpty(sample.fathersName)}|${StringUtils.trimToEmpty(sample.mothersName)}|" +
            "${StringUtils.trimToEmpty(sample.name)}|${StringUtils.trimToEmpty(Registry.currentSite.toString())}"
    url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s', variable ), 'UTF-8' ) )
    final Future<?> future = executor.submit(new Runnable() {
      public void run() {
        try {
          URL urlResp = url.toURL()
          println urlResp.text
          Boolean print = false
          urlResp.text.eachLine { String line ->
            if( print && line.length() > 0 && !line.contains("</XX>") ){
              respuesta.add(line)
            }
            if( line.contains("<XX>") ){
              print = true
            } else if( StringUtils.trimToEmpty(line).length() <= 0 || line.contains("</XX>") ){
              print = false
            }
          }
          println "Respuesta listar clientes: ${respuesta.size()}"
        } catch (Exception e) {
          throw new RuntimeException(e)
        }
      }
    })
    try {
      future.get(timeoutSecs, TimeUnit.SECONDS)
    } catch (Exception e) {
      future.cancel(true)
      log.warn("encountered problem while doing some work", e)
    }
    for(String resp : respuesta){
      resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
      resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
      resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
      resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
      resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
      String[] data = resp.split(/\|/)
      if( data.length >= 17 ){
        Customer c = new Customer();
        Integer id = 0
        //Integer idBranch = 0
        try{
          id = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(data[0])).intValue()
          //idBranch = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(data[1])).intValue()
        } catch ( NumberFormatException e ) { println e }
        c.setId(id)
        c.setIdBranch(StringUtils.trimToEmpty(data[1]))
        c.setName("${StringUtils.trimToEmpty(data[4])} ${StringUtils.trimToEmpty(data[3])} ${StringUtils.trimToEmpty(data[2])}")
        c.setFechaNacimiento( StringUtils.trimToEmpty(data[13]).length() > 0 ? formatter.parse(StringUtils.trimToEmpty(data[13])) : null )
        c.setFechaUltimaVenta( StringUtils.trimToEmpty(data[9]).length() > 0 ? formatter.parse(StringUtils.trimToEmpty(data[9])) : null )
        lstCustomers.add(c)
      }
    }
    return lstCustomers;
  }


  static Customer importCustomersFromMainDb(Customer sample) {
    log.debug("obteniendo lista de customer similar a: ${sample}")
    ExecutorService executor = Executors.newFixedThreadPool(1);
    List<String> respuesta = new ArrayList<>();
    int timeoutSecs = 20;
    String url = Registry.importCustomersUrl
    String variable = "${StringUtils.trimToEmpty(sample.idBranch)}|${StringUtils.trimToEmpty(sample.id.toString())}|" +
            "${StringUtils.trimToEmpty(Registry.currentSite.toString())}"
    url += String.format( '?arg=%s', URLEncoder.encode( String.format( '%s', variable ), 'UTF-8' ) )
    final Future<?> future = executor.submit(new Runnable() {
      public void run() {
        try {
          URL urlResp = url.toURL()
          println urlResp.text
          Boolean print = false
          urlResp.text.eachLine { String line ->
            if( print && line.length() > 0 && !line.contains("</XX>") ){
              respuesta.add(line)
            }
            if( line.contains("<XX>") ){
              print = true
            } else if( StringUtils.trimToEmpty(line).length() <= 0 || line.contains("</XX>") ){
              print = false
            }
          }
          println "Respuesta listar clientes: ${respuesta.size()}"
        } catch (Exception e) {
          throw new RuntimeException(e)
        }
      }
    })
    try {
      future.get(timeoutSecs, TimeUnit.SECONDS)
    } catch (Exception e) {
      future.cancel(true)
      log.warn("encountered problem while doing some work", e)
    }
    String[] data = respuesta.get(0).split(/\|/)
    String ori = data.length >= 31 ? StringUtils.trimToEmpty(data[30]) : ""
    ClientesJava cliente = ClienteServiceJava.obtenerClientePorOrigen(StringUtils.trimToEmpty(ori))
    if( cliente == null ){
      for(String resp : respuesta){
        sample = saveExternalCustomer( resp, sample )
      }
    } else {
      sample = Customer.toCustomer( cliente )
      sample.address.setCity(StringUtils.trimToEmpty(sample.address.city))
      sample.address.setLocation(StringUtils.trimToEmpty(sample.address.location))
      sample.address.setState(StringUtils.trimToEmpty(sample.address.state))
    }
    return sample
  }


  static Customer saveExternalCustomer( String resp, Customer sample ){
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
    resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
    resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
    resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
    resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
    resp = resp.replaceAll(/\|/+/\|/,/\|/+" "+/\|/)
    String[] data = resp.split(/\|/)
    if( StringUtils.trimToEmpty(data[0]).equalsIgnoreCase("1") ){
      MunicipioJava municipio = MunicipioQuery.BuscaMunicipioPorEstadoYLocalidad(StringUtils.trimToEmpty(data[3]), StringUtils.trimToEmpty(data[2]));
      Address address = new Address()
      address.setPrimary(StringUtils.trimToEmpty(data[11]))
      address.setZipcode(StringUtils.trimToEmpty(data[13]))
      address.setLocation(StringUtils.trimToEmpty(data[12]))
      address.setCity(municipio != null ? StringUtils.trimToEmpty(municipio?.nombre) : "")
      address.setState(municipio != null ? StringUtils.trimToEmpty(municipio?.estado?.nombre) : "")

      sample.setId(null)
      sample.setTitle(StringUtils.trimToEmpty(data[1]))
      sample.setName(StringUtils.trimToEmpty(data[9]))
      sample.setFathersName(StringUtils.trimToEmpty(data[6]))
      sample.setMothersName(StringUtils.trimToEmpty(data[7]))
      sample.setRfc(StringUtils.trimToEmpty(data[10]))
      sample.setDob(StringUtils.trimToEmpty(data[27]).length() > 0 ? formatter.parse(StringUtils.trimToEmpty(data[27])) : null)
      sample.setGender(GenderType.parse(!StringUtils.trimToEmpty(data[5]).equalsIgnoreCase("f")))
      sample.setAddress(address)
      sample.setCliOri(StringUtils.trimToEmpty(data[30]))
      sample.setHistCuc(StringUtils.trimToEmpty(data[31]))
      sample.setHistCli(StringUtils.trimToEmpty(data[32]))

      Integer edad = 25
      if(StringUtils.trimToEmpty(data[24]).length() > 0 ){
        try{
          edad = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(data[24]))
        } catch (NumberFormatException e ) { println e }
      }
      sample.setAge( edad );
      sample.setFechaNacimiento(StringUtils.trimToEmpty(data[27]).length() > 0 ? formatter.parse(StringUtils.trimToEmpty(data[27])) : null)
      sample = addCustomer( sample )

      if (StringUtils.trimToEmpty(StringUtils.trimToEmpty(data[14])).length() > 0) {
        saveContact( sample, 2, StringUtils.trimToEmpty(data[14]))
      }
      if (StringUtils.trimToEmpty(StringUtils.trimToEmpty(data[15])).length() > 0) {
        saveContact( sample, 1, StringUtils.trimToEmpty(data[15]))
      }
      if (StringUtils.trimToEmpty(StringUtils.trimToEmpty(data[17])).length() > 0) {
        saveContact( sample, 3, StringUtils.trimToEmpty(data[17]))
      }
      if (StringUtils.trimToEmpty(StringUtils.trimToEmpty(data[18])).length() > 0) {
        saveContact( sample, 0, StringUtils.trimToEmpty(data[18]))
      }
    } else if( StringUtils.trimToEmpty(data[0]).equalsIgnoreCase("2") ){
      User u = Session.get(SessionItem.USER) as User
      ExamenJava examen = new ExamenJava()
      examen.setIdCliente( sample.getId() )
      examen.setIdAtendio( u.username )
      examen.setAvSaOdLejosEx(StringUtils.trimToEmpty(data[3]))
      examen.setAvSaOiLejosEx(StringUtils.trimToEmpty(data[4]))
      examen.setObjOdEsfEx(StringUtils.trimToEmpty(data[5]))
      examen.setObjOdCilEx(StringUtils.trimToEmpty(data[6]))
      examen.setObjOdEjeEx(StringUtils.trimToEmpty(data[7]))
      examen.setObjOiEsfEx(StringUtils.trimToEmpty(data[8]))
      examen.setObjOiCilEx(StringUtils.trimToEmpty(data[9]))
      examen.setObjOiEjeEx(StringUtils.trimToEmpty(data[10]))
      examen.setObjDiEx(StringUtils.trimToEmpty(data[11]))
      examen.setSubOdEsfEx(StringUtils.trimToEmpty(data[12]))
      examen.setSubOdCilEx(StringUtils.trimToEmpty(data[13]))
      examen.setSubOdEjeEx(StringUtils.trimToEmpty(data[14]))
      examen.setSubOdAdcEx(StringUtils.trimToEmpty(data[15]))
      examen.setSubOdAdiEx(StringUtils.trimToEmpty(data[16]))
      examen.setSubOdAvEx(StringUtils.trimToEmpty(data[17]))
      examen.setSubOiEsfEx(StringUtils.trimToEmpty(data[18]))
      examen.setSubOiCilEx(StringUtils.trimToEmpty(data[19]))
      examen.setSubOiEjeEx(StringUtils.trimToEmpty(data[20]))
      examen.setSubOiAdcEx(StringUtils.trimToEmpty(data[21]))
      examen.setSubOiAdiEx(StringUtils.trimToEmpty(data[22]))
      examen.setSubOiAvEx(StringUtils.trimToEmpty(data[23]))
      examen.setObservacionesEx(StringUtils.trimToEmpty(data[24]))
      examen.setDiOd(StringUtils.trimToEmpty(data[26]))
      examen.setDiOi(StringUtils.trimToEmpty(data[27]))
      examen.setTipoCli(StringUtils.trimToEmpty(data[29]))
      examen.setTipoOft(StringUtils.trimToEmpty(data[30]))
      examen.setFechaAlta(new Date())
      examen.setIdOftalmologo(null)
      examen.setHoraAlta(new Date())
      examen.setIdExOri(StringUtils.trimToEmpty(data[34]))
      ExamenServiceJava.guardarExamen( examen )
    } else if( StringUtils.trimToEmpty(data[0]).equalsIgnoreCase("3") ){
      RecetaJava receta = new RecetaJava()
      receta.setExamen( ExamenServiceJava.obtenerExamenPorIdCliente(sample.id).idExamen )
      receta.setIdCliente( sample.id )
      receta.setFechaReceta( new Date() )
      receta.setsUsoAnteojos(StringUtils.trimToEmpty(data[4]))
      receta.setIdOptometrista(StringUtils.trimToEmpty(data[5]))
      receta.setTipoOpt(StringUtils.trimToEmpty(data[6]))
      receta.setOdEsfR(StringUtils.trimToEmpty(data[7]))
      receta.setOdCilR(StringUtils.trimToEmpty(data[8]))
      receta.setOdEjeR(StringUtils.trimToEmpty(data[9]))
      receta.setOdAdcR(StringUtils.trimToEmpty(data[10]))
      receta.setOdAdiR(StringUtils.trimToEmpty(data[11]))
      receta.setOdPrismaH(StringUtils.trimToEmpty(data[12]))
      receta.setOiEsfR(StringUtils.trimToEmpty(data[13]))
      receta.setOiCilR(StringUtils.trimToEmpty(data[14]))
      receta.setOiEjeR(StringUtils.trimToEmpty(data[15]))
      receta.setOiAdcR(StringUtils.trimToEmpty(data[16]))
      receta.setOiAdiR(StringUtils.trimToEmpty(data[17]))
      receta.setOiPrismaH(StringUtils.trimToEmpty(data[18]))
      receta.setDiLejosR(StringUtils.trimToEmpty(data[19]))
      receta.setDiCercaR(StringUtils.trimToEmpty(data[20]))
      receta.setOdAvR(StringUtils.trimToEmpty(data[21]))
      receta.setOiAvR(StringUtils.trimToEmpty(data[22]))
      receta.setAltOblR(StringUtils.trimToEmpty(data[23]))
      receta.setObservacionesR(StringUtils.trimToEmpty(data[24]))
      receta.setDiOd(StringUtils.trimToEmpty(data[27]))
      receta.setDiOi(StringUtils.trimToEmpty(data[28]))
      receta.setOdPrismaV(StringUtils.trimToEmpty(data[30]))
      receta.setOiPrismaV(StringUtils.trimToEmpty(data[31]))
      receta.setIdRxOri(StringUtils.trimToEmpty(data[33]))
      receta.setIdSync("1")
      receta.setFechaMod(new Date())
      receta.setIdMod("0")
      receta.setIdSucursal(Registry.currentSite)
      RecetaServiceJava.saveRx( receta )
    } else if( StringUtils.trimToEmpty(data[0]).equalsIgnoreCase("15") ){
      ImpHistorialJava impHistorialJava = new ImpHistorialJava()
      impHistorialJava.setIdCliente(sample.id)
      impHistorialJava.setIdSucOri(StringUtils.trimToEmpty(data[2]))
      impHistorialJava.setFechaCompra(StringUtils.trimToEmpty(data[3]).length() > 0 ? formatter1.parse(StringUtils.trimToEmpty(data[3])) : null)
      impHistorialJava.setFactura(StringUtils.trimToEmpty(data[5]))
      BigDecimal importe = BigDecimal.ZERO
      if( StringUtils.trimToEmpty(data[6]).length() > 0 ){
        try{
          importe = new BigDecimal(NumberFormat.getInstance().parse(StringUtils.trimToEmpty(data[6])).doubleValue())
        } catch ( NumberFormatException e ) { println e }
      }
      impHistorialJava.setImporte( importe )
      impHistorialJava.setObs(StringUtils.trimToEmpty(data[7]))
      ImpHistorialQuery.saveImpHistorial( impHistorialJava )
    }
    return sample
  }


}
