package mx.lux.pos.java.service;


import mx.lux.pos.java.querys.*;
import mx.lux.pos.java.repository.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CancelacionServiceJava {

  static final Logger log = LoggerFactory.getLogger(CancelacionServiceJava.class);


  public void actualizaGrupo( String idFactura, String trans ) throws ParseException {
    NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(idFactura);
    JbJava trabajo = JbQuery.buscarPorRx(nota.getFactura());
    if( trabajo.getIdGrupo().length() > 0 ){
      List<JbJava> grupo = JbQuery.buscaJbPorIdGrupo(trabajo.getIdGrupo());
      if(grupo.size() > 1){
        Integer noEntCant = 0;
        for(JbJava jbTmp : grupo){
          if(!jbTmp.getEstado().trim().equalsIgnoreCase("TE") && !jbTmp.getEstado().trim().equalsIgnoreCase("CN")){
            noEntCant = noEntCant+1;
          }
        }
        if(noEntCant == 0){
          List<JbJava> lstTmp = JbQuery.buscaJbPorIdGrupo( trabajo.getIdGrupo() );
          JbJava jbGrupo = lstTmp.size() > 0 ? lstTmp.get(0) : null;
          if(jbGrupo != null){
            jbGrupo.setEstado(trans.equalsIgnoreCase("E") ? "TE" : "CN");
            JbQuery.updateEstadoJbRx( jbGrupo.getRx(), jbGrupo.getEstado() );
          }
          JbLlamadaJava llamada = JbQuery.buscaJbLlamadaPorIdGrupo( trabajo.getIdGrupo() );
          if(llamada != null){
            JbQuery.eliminaJbLLamada(llamada.getRx());
          }
        }
      }
    }
  }


  public List<NotaVentaJava> listarNotasVentaOrigenDeNotaVenta(String idNotaVenta) throws ParseException {
    List<NotaVentaJava> notas = new ArrayList<NotaVentaJava>();
    log.info("obteniendo notaVenta origen de notaVenta id: "+idNotaVenta);
    if (StringUtils.isNotBlank(idNotaVenta)) {
      List<PagoJava> transferencias = PagoQuery.busquedaPagosPorIdFPagoAndIdFactura("TR", idNotaVenta);
      log.debug("obtiene pagos tipo transferencia");
      if (transferencias.size() > 0) {
        for( PagoJava pago : transferencias ){
          NotaVentaJava nota = NotaVentaQuery.busquedaNotaById(pago.getReferenciaPago());
          if( nota != null ){
            notas.add( nota );
          }
        }
      } else {
        log.warn("no se obtiene notasVenta origen, notaVenta no ha recibido transferencias");
      }
    } else {
      log.warn("no se obtienen notasVenta origen, parametros invalidos");
    }
    Collections.sort(notas, new Comparator<NotaVentaJava>() {
        @Override
        public int compare(NotaVentaJava o1, NotaVentaJava o2) {
            return o1.getFechaHoraFactura().compareTo(o2.getFechaHoraFactura());
        }
    });
    return notas;
  }


  public void restablecerValoresDeCancelacion(String idNotaVenta) throws ParseException {
    log.info("restableciendo valores de cancelacion");
    List<PagoJava> lstPagos = PagoQuery.busquedaPagosPorReferenciaPago(idNotaVenta);
    List<PagoJava> lstPagosTransf = PagoQuery.busquedaPagosPorIdFactura(idNotaVenta);
    BigDecimal sumaPagos = BigDecimal.ZERO;
    BigDecimal sumaPagosTransf = BigDecimal.ZERO;
    for (PagoJava pagoTransf : lstPagosTransf) {
      sumaPagosTransf = sumaPagosTransf.add(pagoTransf.getMontoPago());
    }
    for (PagoJava pago : lstPagos) {
      sumaPagos = sumaPagos.add(pago.getMontoPago());
    }
    for( PagoJava pagoTransf : lstPagos ){
      NotaVentaJava notaVenta = NotaVentaQuery.busquedaNotaById(pagoTransf.getIdFactura());
      if( StringUtils.trimToEmpty( notaVenta.getFactura() ).isEmpty() && !StringUtils.trimToEmpty(pagoTransf.getRefClave()).isEmpty() ){
        String[] idPagoTransf = pagoTransf.getRefClave().split(":");
        PagoJava pagoFuente = PagoQuery.busquedaPagosPorId( Integer.parseInt( idPagoTransf[1].trim() ) );
        DevolucionJava devolucion = DevolucionQuery.buscaDevolucionPorIdPagoMontoAndTransf(pagoFuente.getIdPago(), pagoTransf.getMontoPago(),
                pagoTransf.getIdFactura());
        if( devolucion != null ){
          BigDecimal montoTotal = pagoTransf.getMontoPago().add(pagoFuente.getPorDev());
          DevolucionQuery.eliminaDevolucion(devolucion);
          pagoFuente.setPorDev(montoTotal);
          PagoQuery.saveOrUpdatePago( pagoFuente );
        }
      }
    }
  }



  public BigDecimal obtenerCreditoDeNotaVenta(String idNotaVenta) throws ParseException {
    log.info("obteniendo credito de notaVenta id: "+idNotaVenta);
    if (StringUtils.isNotBlank(idNotaVenta)) {
      List<ModificacionJava> mods = ModificacionQuery.buscaModificaionPorIdFacturaAndTipo(idNotaVenta, "can");
      log.debug("cantidad de modificaciones: "+mods.size());
      ModificacionJava modificacion = mods.size() > 0 ? mods.get(0) : null;
      if (modificacion != null && modificacion.getIdMod() != null) {
        log.debug("obtiene modificacion: "+modificacion.getIdMod());
        BigDecimal porDevolver = BigDecimal.ZERO;
        List<PagoJava> pagos = PagoQuery.busquedaPagosPorIdFactura(idNotaVenta);
        for(PagoJava pmt : pagos){
            porDevolver = porDevolver.add(pmt.getPorDev());
        }
        log.debug("obtiene credito: ${porDevolver}");
        return porDevolver;
      } else {
        log.warn("no se obtiene credito de notaVenta, notaVenta sin cancelacion");
      }
    } else {
      log.warn("no se obtiene credito de notaVenta, parametros invalidos");
    }
    return null;
  }


}
