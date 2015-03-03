package mx.lux.pos.ui.controller

import groovy.util.logging.Slf4j
import mx.lux.pos.model.Comprobante
import mx.lux.pos.model.Contribuyente
import mx.lux.pos.model.DetalleNotaVenta
import mx.lux.pos.model.NotaVenta
import mx.lux.pos.service.ComprobanteService
import mx.lux.pos.service.ContribuyenteService
import mx.lux.pos.service.NotaVentaService
import mx.lux.pos.service.TicketService
import mx.lux.pos.ui.model.Invoice
import mx.lux.pos.ui.model.Session
import mx.lux.pos.ui.model.SessionItem
import mx.lux.pos.ui.model.User
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.awt.*
import java.util.List

@Slf4j
@Component
class InvoiceController {

  private static ComprobanteService comprobanteService
  private static ContribuyenteService contribuyenteService
  private static TicketService ticketService
  private static NotaVentaService notaVentaService

  private static final String TAG_GENERICO_B = 'B'

  @Autowired
  InvoiceController(
      ComprobanteService comprobanteService,
      ContribuyenteService contribuyenteService,
      TicketService ticketService,
      NotaVentaService notaVentaService
  ) {
    this.comprobanteService = comprobanteService
    this.contribuyenteService = contribuyenteService
    this.ticketService = ticketService
    this.notaVentaService = notaVentaService
  }

  static Invoice getInvoice( String invoiceId ) {
    log.info( "buscando invoice por invoiceId: ${invoiceId}" )
    Comprobante result = comprobanteService.obtenerComprobante( invoiceId )
    Invoice invoice = Invoice.toInvoice( result )
    log.debug( "obtiene invoice invoiceId: ${invoice?.invoiceId}" )
    return invoice
  }

  static Invoice findInvoiceByTicket( String ticket ) {
    log.info( "buscando invoice por ticket: ${ticket}" )
    List<Comprobante> results = comprobanteService.listarComprobantesPorTicket( ticket )
    if ( results?.any() ) {
      Invoice invoice = Invoice.toInvoice( results.first() )
      log.debug( "obtiene invoice invoiceId: ${invoice?.invoiceId}" )
      return invoice
    }
    return null
  }

  static boolean isValidRfc( String rfc ) {
    log.info( "validando rfc ${rfc}" )
    return contribuyenteService.esRfcValido( rfc )
  }

  static Invoice requestInvoice( Invoice invoice, Boolean lenteDesglosado, Boolean rxDesglosado, Boolean clientDesglosado, Boolean lenteArmazonDesglosado ) {
    log.info( "solicitando invoice para el ticket: ${invoice?.ticket}" )
    if ( StringUtils.isNotBlank( invoice?.ticket ) && StringUtils.isNotBlank( invoice?.orderId ) ) {
      User user = Session.get( SessionItem.USER ) as User
      Boolean hasRx = false
      String receta = ''
      NotaVenta notaVenta = notaVentaService.obtenerNotaVenta( invoice.orderId )
      if(notaVenta != null){
        for(DetalleNotaVenta det : notaVenta.detalles){
          if(det.articulo.idGenerico.trim().equalsIgnoreCase(TAG_GENERICO_B)){
            hasRx = true
          }
        }
        if(hasRx){
          String odEsfR = notaVenta.rx.odEsfR.replace('+','')
          odEsfR = odEsfR.replace('-','')
          String odCilR = notaVenta.rx.odCilR.replace('+','')
          odCilR = odCilR.replace('-','')
          String odAdcR = notaVenta.rx.odAdcR.replace('+','')
          odAdcR = odAdcR.replace('-','')
          String oiEsfR = notaVenta.rx.oiEsfR.replace('+','')
          oiEsfR = oiEsfR.replace('-','')
          String oiCilR = notaVenta.rx.oiCilR.replace('+','')
          oiCilR = oiCilR.replace('-','')
          String oiAdcR = notaVenta.rx.oiAdcR.replace('+','')
          oiAdcR = oiAdcR.replace('-','')
          NotaVenta nota = notaVentaService.obtenerNotaVenta( invoice.orderId )
          String rxData = rxDesglosado ? "RX: OD- ${odEsfR.trim()} ${odCilR.trim()} ${odAdcR.trim()} OI- ${oiEsfR.trim()} ${oiCilR.trim()} ${oiAdcR.trim()}" : ''
          String clientData = clientDesglosado ? "PACIENTE: ${nota != null ? nota.cliente.nombreCompleto :''}" : ''
          receta = StringUtils.trimToEmpty(rxData)+" "+StringUtils.trimToEmpty(clientData)
        }
      }
      Comprobante comprobante = new Comprobante(
          rfc: invoice.rfc,
          ticket: invoice.ticket,
          idEmpleado: user?.username,
          idFactura: invoice.orderId,
          razon: invoice.bizName,
          tipoFactura: invoice.invoiceType ?: 'N',
          calle: invoice.primary,
          colonia: invoice.location,
          municipio: invoice.city,
          estado: invoice.state,
          pais: invoice.country ?: 'MEXICO',
          codigoPostal: invoice.zipcode,
          email: invoice.email,
          rx: invoice.rx ?: '0',
          paciente: invoice.patient ?: '0',
          observaciones: receta
      )
      comprobante = comprobanteService.registrarComprobante( comprobante, lenteDesglosado, rxDesglosado, clientDesglosado, lenteArmazonDesglosado )
      if ( comprobante?.id ) {
        Integer idCliente = comprobante.idCliente.isInteger() ? comprobante.idCliente.toInteger() : null
        Contribuyente contribuyente = new Contribuyente(
            idCliente: idCliente,
            rfc: comprobante.rfc,
            nombre: comprobante.razon,
            domicilio: comprobante.calle,
            colonia: comprobante.colonia,
            ciudad: comprobante.municipio,
            idEstado: comprobante.estado,
            codigoPostal: comprobante.codigoPostal,
            email: comprobante.email
        )
        contribuyenteService.registrarContribuyente( contribuyente )
      }
      invoice = Invoice.toInvoice( comprobante )
      log.debug( "invoice generado con invoiceId: ${invoice?.invoiceId}" )
      return invoice
    }
    return null
  }

  static void printInvoiceReference( String invoiceId ) {
    log.info( "imprimiendo ticket referencia comprobante fiscal con invoiceId: ${invoiceId}" )
    if ( StringUtils.isNotBlank( invoiceId ) ) {
      ticketService.imprimeReferenciaFiscal( invoiceId )
    } else {
      log.warn( 'no se imprime ticket referencia comprobante fiscal, parametros invalidos' )
    }
  }

  static void printInvoice( String invoiceId ) {
    log.info( "imprimiendo ticket comprobante fiscal con invoiceId: ${invoiceId}" )
    if ( StringUtils.isNotBlank( invoiceId ) ) {
      ticketService.imprimeComprobanteFiscal( invoiceId )
    } else {
      log.warn( 'no se imprime ticket comprobante fiscal, parametros invalidos' )
    }
  }

  static void showInvoice( String invoiceId ) {
    log.info( "desplegando pdf comprobante fiscal con invoiceId: ${invoiceId}" )
    if ( StringUtils.isNotBlank( invoiceId ) ) {
      List<File> files = comprobanteService.descargarArchivosComprobante( invoiceId )
      if ( files?.any() ) {
        File pdf = files.last()
        if ( Desktop.isDesktopSupported() ) {
          Desktop.getDesktop().open( pdf )
        }
      } else {
        log.warn( 'no se despliega pdf comprobante fiscal, no se obtiene archivo' )
      }
    } else {
      log.warn( 'no se despliega pdf comprobante fiscal, parametros invalidos' )
    }
  }
}
