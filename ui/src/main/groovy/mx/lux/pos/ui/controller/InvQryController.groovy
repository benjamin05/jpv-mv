package mx.lux.pos.ui.controller

import mx.lux.pos.model.Articulo
import mx.lux.pos.model.InvOhSummary
import mx.lux.pos.ui.model.InvOhData
import mx.lux.pos.ui.resources.ServiceManager
import mx.lux.pos.ui.view.dialog.InvLcTicketDialog
import mx.lux.pos.ui.view.dialog.InvOhTicketDialog
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InvQryController {
  
  private Logger logger = LoggerFactory.getLogger( InvQryController.class )

  // Constructor: Implemented as singleton
  protected static InvQryController instance
  protected InvQryController() { }
  static InvQryController getInstance() {
    if ( instance == null ) {
      instance = new InvQryController()
    }
    return instance
  }
  
  // Requests
  void requestInvOhTicket() {
    this.requestInvOhTicket( new InvOhTicketDialog() )
  }

  void requestInvLcTicket() {
    this.requestInvLcTicket( new InvLcTicketDialog() )
  }
  
  void requestInvOhTicket( InvOhTicketDialog pDialog ) {
    logger.debug( "Request InvOHTicket" )
    InvOhData invData = new InvOhData()
    dispatchInventoryRequest( invData )
    pDialog.setOhData( invData )  
    pDialog.activate()
    if ( pDialog.printRequested ) {
      logger.debug( String.format( "Request InvOH Ticket print (%s, %s)", pDialog.genreSelected, pDialog.brandSelected ) )
      InvOhSummary ticketRequest = invData.getSummary( pDialog.genreSelected, pDialog.brandSelected, false )
      ServiceManager.ticketService.imprimeResumenExistencias( ticketRequest )      
    }
  }

  void requestInvLcTicket( InvLcTicketDialog pDialog ) {
    logger.debug( "Request InvLCTicket" )
    InvOhData invData = new InvOhData()
    dispatchInventoryLcRequest( invData )
    pDialog.setOhData( invData )
    pDialog.activate()
    if ( pDialog.printRequested ) {
      logger.debug( String.format( "Request InvOH Ticket print (%s, %s)", pDialog.genreSelected, pDialog.brandSelected ) )
      InvOhSummary ticketRequest = invData.getSummary( pDialog.genreSelected, pDialog.brandSelected, pDialog.resumidoSelected )
      ServiceManager.ticketService.imprimeResumenExistenciasLc( ticketRequest )
    }
  }
  
  // Dispatchers
  protected void dispatchInventoryRequest( InvOhData pInvOhData ) {
    logger.debug( "Dispatch Inventory Request")
    Collection<Articulo> partsOH = ServiceManager.inventoryService.listarArticulosConExistencia( )
    pInvOhData.input = partsOH
  }

  protected void dispatchInventoryLcRequest( InvOhData pInvOhData ) {
    logger.debug( "Dispatch Inventory Request")
    Collection<Articulo> partsOH = ServiceManager.inventoryService.listarArticulosLcConExistencia( )
    pInvOhData.input = partsOH
  }
}
