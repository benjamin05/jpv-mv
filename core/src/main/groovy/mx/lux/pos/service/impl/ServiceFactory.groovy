package mx.lux.pos.service.impl

import mx.lux.pos.java.service.ArticulosServiceJava
import mx.lux.pos.java.service.InventarioServiceJava
import mx.lux.pos.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ServiceFactory {

  private static EmpleadoService employeeCatalog
  private static InventarioService inventory
  private static NotaVentaService salesOrders
  private static ArticuloService partMaster
  private static SucursalService sites
  private static TicketService ticketEngine
  private static EstadoService states
  private static IOService ioServices
  private static CierreDiarioService dailyClose
  private static MensajeService messages
  private static CotizacionService quotes
  private static ClienteService customers
  private static InventarioServiceJava inventarioServiceJava
  private static ArticulosServiceJava articulosServiceJava

  @Autowired
  ServiceFactory( ArticuloService pArticuloService, InventarioService pInventarioService,
                  NotaVentaService pNotaVentaService, EmpleadoService pEmpMaster,
                  TicketService pTicketEngine, SucursalService pSiteService, EstadoService pEstadoService,
                  IOService pIOServices, CierreDiarioService pCierreDiarioService,
                  MensajeService pMensajeService, CotizacionService pCotizacionService, ClienteService pClienteService
  ) {
    employeeCatalog = pEmpMaster
    inventory = pInventarioService
    salesOrders = pNotaVentaService
    partMaster = pArticuloService
    sites = pSiteService
    ticketEngine = pTicketEngine
    states = pEstadoService
    ioServices = pIOServices
    dailyClose = pCierreDiarioService
    messages = pMensajeService
    quotes = pCotizacionService
    customers = pClienteService
    inventarioServiceJava = new InventarioServiceJava();
    articulosServiceJava = new ArticulosServiceJava()
  }

  static EmpleadoService getEmployeeCatalog( ) {
    return employeeCatalog
  }

  static InventarioService getInventory( ) {
    return inventory
  }

  static InventarioServiceJava getInventoryJava( ) {
    return inventarioServiceJava
  }

  static ArticulosServiceJava getPartsJava( ) {
    return articulosServiceJava
  }

  static NotaVentaService getSalesOrders( ) {
    return salesOrders
  }

  static ArticuloService getPartMaster( ) {
    return partMaster
  }

  static SucursalService getSites( ) {
    return sites
  }

  static TicketService getTicketEngine( ) {
    return ticketEngine
  }

  static EstadoService getStates( ) {
    return states
  }

  static IOService getIoServices( ) {
    return ioServices
  }

  static CierreDiarioService getDailyClose( ) {
    return dailyClose
  }

  static MensajeService getMessages( ) {
    return messages
  }

  static CotizacionService getQuotes( ) {
    return quotes
  }

  static ClienteService getCustomers() {
    return customers
  }

}
