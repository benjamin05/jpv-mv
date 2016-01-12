package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.TipoParametro;
import mx.lux.pos.util.SubtypeCouponsUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class TicketServiceJava {

  static final Logger log = LoggerFactory.getLogger(TicketServiceJava.class);


  /*private File generaTicket( String template, Map<String, Object> items ) {
    log.info( "generando archivo de ticket con plantilla: "+template );
    if ( StringUtils.isNotBlank(template) && !items.isEmpty() ) {
      try {
        String fileName = items.containsKey("nombre_ticket") ? (String) items.get("nombre_ticket") : "ticket";
        File file = File.createTempFile( fileName, null );
        file.withWriter { BufferedWriter writer ->
                    items.writer = writer
                    VelocityEngineUtils.mergeTemplate(velocityEngine, template, "ASCII", items, writer)
                    true
                }
                log.debug( "archivo generado en: ${file.path}" )
                return file
      } catch ( Exception ex ) {
        log.error( "error al generar archivo de ticket: ${ex.message}", ex );
      }
    } else {
      log.warn( "parametros no validos" );
    }
    return null;
  }*/


  /*private void imprimeTicket( String template, Map<String, Object> items ) {
    File ticket = generaTicket( template, items )

        if ( ticket?.exists() ) {
            try {
                def parametro = parametroRepository.findOne( TipoParametro.IMPRESORA_TICKET.value )
                def cmd = "${parametro?.valor} " + "${ticket.path}"
                log.info( "ejecuta: ${cmd}" )


                //Evita pasmarse cuando no hay impresora conectada
                try
                {
                    def proc = cmd.execute()
                    int exitVal = proc.exitValue();
                    println("Process exitValue: " + exitVal);
                    proc.waitFor()

                } catch (Throwable t)
                {

                }

                //Evita pasmarse cuando no hay impresora conectada


            } catch ( ex ) {
                log.error( "error durante la ejecucion del comando de impresion: ${ex.message}", ex )
            }
        } else {
            log.warn( "archivo de ticket no generado, no se puede imprimir" )
        }
  }


  public void imprimeCupon( CuponMvJava cuponMv, String titulo, BigDecimal monto ) throws ParseException {
    log.debug( "imprimeCupon( )" );
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    String restrictions = "";
    String restrictions1 = "";
    String titulo2 = "";
    if( cuponMv != null ){
      if( StringUtils.trimToEmpty(cuponMv.getClaveDescuento()).startsWith("F") ){
        restrictions = "APLICA EN LA COMPRA MINIMA DE $1000.00";
        restrictions1 = "CONSULTA CONDICIONES EN TIENDA.";
      } else if( StringUtils.trimToEmpty(cuponMv.getClaveDescuento()).startsWith("H") ){
        restrictions = "APLICAN RESTRICCIONES";
      }
    }
    if( StringUtils.trimToEmpty(cuponMv.getClaveDescuento()).startsWith("H") ){
      NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaByFactura(StringUtils.trimToEmpty(cuponMv.getFacturaOrigen()));
      if( notaVenta != null ){
        Integer contador = 0;
        for(DetalleNotaVentaJava det : notaVenta.getDetalles()){
          if( StringUtils.trimToEmpty(det.getArticulo().idGenerico).equalsIgnoreCase("H") ){
            titulo2 = SubtypeCouponsUtils.getTitle2(det.getArticulo().getSubtipo());
          }
        }
      }
    }
    if( cuponMv != null ){
      def datos = [
            titulo: titulo,
                    titulo2: titulo2,
                    monto: String.format('$%s', monto),
                    clave: cuponMv.claveDescuento,
                    fecha_vigencia: df.format(cuponMv.fechaVigencia),
                    restrictions: restrictions,
                    restrictions1: restrictions1
      ]
      this.imprimeTicket( 'template/ticket-cupon.vm', datos );
    } else {
      log.debug( String.format( "Cupon (%s) not found.", cuponMv.getClaveDescuento() ) );
    }
  }*/



}
