package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.*;
import mx.lux.pos.service.business.InventorySearch;
import mx.lux.pos.service.business.Registry;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CierreDiarioServiceJava {

  static final Logger log = LoggerFactory.getLogger(CierreDiarioServiceJava.class);

  private static final Double VALOR_CERO = 0.005;


  public static Boolean rehacerArchivosCierrre( Date fecha ) throws ParseException {
    Boolean rehacerArchivos = false;
    CierreDiarioJava cierreDiarioJava = CierreDiarioQuery.buscaCierreDiarioPorFecha( fecha );
    if( StringUtils.trimToEmpty(cierreDiarioJava.getEstado()).equalsIgnoreCase("c") ){
      Date fechaFin = new Date( DateUtils.ceiling(fecha, Calendar.DAY_OF_MONTH).getTime() - 1 );
      Parametros parametro = ParametrosQuery.BuscaParametroPorId(TipoParametro.CONV_NOMINA.getValue());
      String convenios = parametro.getValor();
      List<NotaVentaJava> notas = new ArrayList<NotaVentaJava>();
      List<NotaVentaJava> notasTmp = NotaVentaQuery.busquedaNotasPorFechaAndFacturaNotEmpty(fecha, fechaFin);
      for(NotaVentaJava tmp : notasTmp){
        if( StringUtils.trimToEmpty(tmp.getIdConvenio()).length() <= 0 && StringUtils.trimToEmpty(tmp.getFactura()).length() > 0 ){
          notas.add(tmp);
        }
      }
      log.debug( "notas obtenidas: "+notas.size() );
      BigDecimal ventaBruta = BigDecimal.ZERO;
      BigDecimal desc = BigDecimal.ZERO;
      Integer cantDesc = 0;
      for(NotaVentaJava nota : notas){
        for(DetalleNotaVentaJava det : nota.getDetalles() ){
          BigDecimal precio = det.getPrecioUnitLista().multiply( new BigDecimal(det.getCantidadFac()) );
          ventaBruta = ventaBruta.add(precio);
        }
        List<OrdenPromDetJava> lstOrdenPromDet = OrdenPromDetQuery.BuscaOrdenPromDetPorIdFactura( nota.getIdFactura() );
        for( OrdenPromDetJava promo : lstOrdenPromDet ){
          desc = desc.add( promo.getDescuentoMonto() );
          cantDesc = cantDesc+1;
        }
        if( nota.getMontoDescuento().abs().compareTo(new BigDecimal(VALOR_CERO)) > 0 ){
          desc = desc.add( nota.getMontoDescuento() );
          cantDesc = cantDesc+1;
        }
      }
      BigDecimal modificaciones = BigDecimal.ZERO;
      BigDecimal cancelaciones = BigDecimal.ZERO;
      Integer modificados = 0;
      Integer cancelados = 0;
      List<ModificacionJava> mods = ModificacionQuery.buscaModificaionPorFecha(fecha, fechaFin);
      for(ModificacionJava mod : mods){
            ModificacionImpJava imp = ModificacionQuery.buscaModificacionImpPorIdMod(mod.getIdMod());
            if ( imp != null && imp.getIdMod() != null ) {
                BigDecimal anterior = imp.getVentaOld();
                BigDecimal nuevo = imp.getVentaNew();
                modificaciones = modificaciones.add(anterior.subtract(nuevo));
                modificados = modificados+1;
            } else if ( "can".equalsIgnoreCase( mod.getTipo() ) ) {
                NotaVentaJava notaVentaJava = NotaVentaQuery.busquedaNotaById( mod.getIdFactura() );
                cancelaciones = cancelaciones.add( notaVentaJava.getVentaNeta() );
                cancelados = cancelados+1;
            }
      }
      modificaciones = modificaciones.add(desc);
      BigDecimal ventaNeta = ( ventaBruta.subtract(cancelaciones).subtract(modificaciones) );
      if( ventaNeta.compareTo(cierreDiarioJava.getVentaNeta()) > 0 || ventaNeta.compareTo(cierreDiarioJava.getVentaNeta()) < 0 ){
        rehacerArchivos = true;
      }
    }
    return rehacerArchivos;
  }


  public static void marcarValidado( Date fecha ) throws ParseException {
    CierreDiarioJava cierreDiario = CierreDiarioQuery.buscaCierreDiarioPorFecha( fecha );
    if( cierreDiario != null ){
      cierreDiario.setVerificado( true );
      CierreDiarioQuery.updateCierreDiario( cierreDiario );
    }
  }



  /*public static void regenerarArchivosCierreDiario( Date fechaCierre, String observaciones ) {
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    Parametro ubicacion = Registry.find(TipoParametro.RUTA_CIERRE);
    try {
      generarFicheroZD( fechaCierre, sucursal, ubicacion.valor )
      generarFicheroZO( fechaCierre, sucursal, ubicacion.valor )
      generarFicheroZP( fechaCierre, sucursal, ubicacion.valor )
      generarFicheroZM( fechaCierre, sucursal, ubicacion.valor )
      generarFicheroZS( fechaCierre, sucursal, ubicacion.valor )
      generarFicheroZV( fechaCierre, sucursal, ubicacion.valor )
      generarFicheroZT( fechaCierre, sucursal, ubicacion.valor )
      generarFicheroCO( fechaCierre, sucursal, ubicacion.valor )
      generarFicheroff( fechaCierre, sucursal, ubicacion.valor )
      generarFicheroZZ( fechaCierre, sucursal, ubicacion.valor )

      String dateClose = df.format(fechaCierre)
      String today = df.format( new Date() )
      generarFicheroInv( fechaCierre )
      InventorySearch.generateInFile(fechaCierre, fechaCierre)
      archivarCierre( fechaCierre )
    } catch ( Exception e ) {
      log.error( e.getMessage(), e );
    }
  }*/


}
