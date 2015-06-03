package mx.lux.pos.service;


import mx.lux.pos.Utilities;
import mx.lux.pos.model.TipoParametro;
import mx.lux.pos.querys.*;
import mx.lux.pos.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public class NotaVentaServiceJava {

    private static final String TAG_TIPO_NOTA_VENTA = String.valueOf('F');
    static final Logger log = LoggerFactory.getLogger(NotaVentaQuery.class);

    public static NotaVentaJava registrarNotaVenta(NotaVentaJava notaVenta) throws ParseException {
        log.info( "registrando notaVenta id: ${notaVenta?.id}," );
        log.info( "fechaHoraFactura: ${notaVenta?.fechaHoraFactura?.format( DATE_TIME_FORMAT )}" );
        if ( StringUtils.isNotBlank( notaVenta.getIdFactura() ) ) {
            String idNotaVenta = notaVenta.getIdFactura();
            if ( NotaVentaQuery.busquedaNotaById(idNotaVenta) != null ) {
                notaVenta.setIdSucursal(Utilities.toInteger(ParametrosQuery.BuscaParametroPorId(TipoParametro.ID_SUCURSAL.getValue()).getValor()));
                BigDecimal total = BigDecimal.ZERO;
                List<DetalleNotaVentaJava> detalles = DetalleNotaVentaQuery.busquedaDetallesNotaVenPorIdFactura(idNotaVenta);
                for(DetalleNotaVentaJava detalleNotaVenta : detalles){
                    BigDecimal precio = detalleNotaVenta.getPrecioUnitFinal();
                    Integer cantidad = detalleNotaVenta.getCantidadFac().intValue();
                    BigDecimal subtotal = precio.multiply( new BigDecimal(cantidad) );
                    total = total.add( subtotal );
                }
                BigDecimal pagado = BigDecimal.ZERO;
                List<PagoJava> pagos = PagoQuery.busquedaPagosPorIdFactura( idNotaVenta );
                for(PagoJava pago : pagos){
                    BigDecimal monto = pago.getMontoPago();
                    pagado = pagado.add( monto );
                }
                log.debug( "ventaNeta: "+total );
                log.debug( "ventaTotal: "+total );
                log.debug( "sumaPagos: "+pagado );
                BigDecimal diferencia = notaVenta.getVentaNeta().subtract(total);
                if( ((notaVenta.getVentaNeta().subtract(total)).compareTo(new BigDecimal(0.05)) < 0) &&
                        (notaVenta.getVentaNeta().subtract(total).compareTo(new BigDecimal(-0.05)) > 0) ){
                    log.debug( "redondeo monto total" );
                    DetalleNotaVentaJava det = null;
                    for(DetalleNotaVentaJava detalleNotaVenta : detalles){
                        ArticulosJava articulo = ArticulosQuery.busquedaArticuloPorId(detalleNotaVenta.getIdArticulo());
                        if( !StringUtils.trimToEmpty(articulo.getIdGenerico()).equalsIgnoreCase("J") ){
                            det = detalleNotaVenta;
                        }
                    }
                    if( detalles.size() > 0 && det != null ){
                        BigDecimal monto = det.getPrecioUnitFinal().add(diferencia);
                        if( diferencia.compareTo(BigDecimal.ZERO) > 0 || diferencia.compareTo(BigDecimal.ZERO) < 0 ){
                            det.setPrecioUnitFinal( monto );
                            det.setPrecioFactura( monto );
                            DetalleNotaVentaQuery.updateDetalleNotaVenta(det);
                        }
                    }
                } else {
                    notaVenta.setVentaNeta(total);
                    notaVenta.setVentaTotal(total);
                }

                notaVenta.setSumaPagos(pagado);
                notaVenta.setTipoNotaVenta(TAG_TIPO_NOTA_VENTA);
                try {
                    notaVenta = NotaVentaQuery.updateNotaVenta(notaVenta);
                    log.info( "notaVenta registrada id: ${notaVenta?.id}" );
                } catch ( Exception ex ) {
                    log.error( "problema al registrar notaVenta: ${notaVenta?.dump()}", ex );
                }
            } else {
                log.warn( "no se registra notaVenta, id no existe" );
            }
        } else {
            log.warn( "no se registra notaVenta, parametros invalidos" );
        }
        return notaVenta;
    }


    public EmpleadoJava obtenerEmpleadoDeNotaVenta(String pOrderId) throws ParseException {
      EmpleadoJava employee = null;
      if ( StringUtils.trimToNull( pOrderId ) != null ) {
        NotaVentaJava order = NotaVentaQuery.busquedaNotaById(StringUtils.trimToEmpty(pOrderId));
        if ( ( order != null ) && ( StringUtils.trimToNull( order.getIdEmpleado() ) != null ) ) {
          employee = EmpleadoQuery.buscaEmpPorIdEmpleado(StringUtils.trimToEmpty(order.getIdEmpleado()));
        }
      }
      return employee;
    }
}
