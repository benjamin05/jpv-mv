package mx.lux.pos.querys;

import mx.lux.pos.Utilities;
import mx.lux.pos.model.*;
import mx.lux.pos.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class NotaVentaQuery {

	private static ResultSet rs;
    private static Statement stmt;

    static final Logger log = LoggerFactory.getLogger(NotaVentaQuery.class);

	public static NotaVentaJava busquedaNotaById(String idNotaVenta) throws ParseException{
      NotaVentaJava notaVentaJava = new NotaVentaJava();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if( idNotaVenta.length() > 0 ){
          sql = "SELECT * FROM nota_venta WHERE id_factura = '"+StringUtils.trimToEmpty(idNotaVenta)+"';";
        } else {
          notaVentaJava = null;
        }
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          notaVentaJava.setValores(rs.getString("id_factura"), rs.getString("id_empleado"), rs.getInt("id_cliente"), rs.getString("id_convenio"),
                rs.getInt("id_rep_venta"), rs.getString("tipo_nota_venta"), rs.getDate("fecha_rec_ord"), rs.getString("tipo_cli"),
            	rs.getBoolean("f_expide_factura"), Utilities.toBigDecimal(rs.getString("venta_total")), Utilities.toBigDecimal(rs.getString("venta_neta")),
                Utilities.toBigDecimal(rs.getString("suma_pagos")), rs.getDate("fecha_hora_factura"), rs.getDate("fecha_prometida"),
            	rs.getDate("fecha_entrega"), rs.getBoolean("f_armazon_cli"), rs.getInt("por100_descuento"), Utilities.toBigDecimal(rs.getString("monto_descuento")),
            	rs.getString("tipo_descuento"), rs.getString("id_empleado_descto"), rs.getBoolean("f_resumen_notas_mo"), rs.getString("s_factura"),
            	rs.getInt("numero_orden"), rs.getString("tipo_entrega"), rs.getString("observaciones_nv"), rs.getString("id_sync"), rs.getDate("fecha_mod"),
                rs.getString("id_mod"),rs.getInt("id_sucursal"), rs.getString("factura"), rs.getString("cant_lente"), rs.getString("udf2"), rs.getString("udf3"),
                rs.getString("udf4"), rs.getString("udf5"), rs.getString("suc_dest"), rs.getString("t_deduc"), rs.getInt("receta"), rs.getString("emp_entrego"),
                rs.getString("lc"), rs.getDate("hora_entrega"), rs.getBoolean("descuento"), rs.getBoolean("pol_ent"), rs.getString("tipo_venta"),
                Utilities.toBigDecimal(rs.getString("poliza")), rs.getString("codigo_lente"));
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
  	  return notaVentaJava;
	}



    NotaVenta registrarNotaVenta( NotaVentaJava notaVenta ) throws ParseException {
      log.info( "registrando notaVenta id: ${notaVenta?.id}," );
      log.info( "fechaHoraFactura: ${notaVenta?.fechaHoraFactura?.format( DATE_TIME_FORMAT )}" );
      if ( StringUtils.isNotBlank( notaVenta.getIdFactura() ) ) {
        String idNotaVenta = notaVenta.getIdFactura();
        if ( busquedaNotaById(idNotaVenta) != null ) {
          notaVenta.setIdSucursal(sucursalRepository.getCurrentSucursalId());
                BigDecimal total = BigDecimal.ZERO
                List<DetalleNotaVenta> detalles = detalleNotaVentaRepository.findByIdFacturaOrderByCantidadFacAsc( idNotaVenta )
                detalles?.each { DetalleNotaVenta detalleNotaVenta ->
                    BigDecimal precio = detalleNotaVenta?.precioUnitFinal ?: 0
                    Integer cantidad = detalleNotaVenta?.cantidadFac ?: 0
                    BigDecimal subtotal = precio.multiply( cantidad )
                    total = total.add( subtotal )
                }

                BigDecimal pagado = BigDecimal.ZERO
                List<Pago> pagos = pagoRepository.findByIdFactura( idNotaVenta )
                pagos?.each { Pago pago ->
                    BigDecimal monto = pago?.monto ?: 0
                    pagado = pagado.add( monto )
                }
                log.debug( "ventaNeta: ${notaVenta.ventaNeta} -> ${total}" )
                log.debug( "ventaTotal: ${notaVenta.ventaTotal} -> ${total}" )
                log.debug( "sumaPagos: ${notaVenta.sumaPagos} -> ${pagado}" )
                BigDecimal diferencia = notaVenta?.ventaNeta?.subtract(total)
                if( //notaVenta?.montoDescuento?.compareTo(BigDecimal.ZERO) > 0 &&
                        ((notaVenta?.ventaNeta?.subtract(total) < new BigDecimal(0.05)) && (notaVenta?.ventaNeta?.subtract(total) > new BigDecimal(-0.05))) ){
                    log.debug( "redondeo monto total" )
                    DetalleNotaVenta det = null
                    for(DetalleNotaVenta detalleNotaVenta : detalles){
                        Articulo articulo = detalleNotaVenta.articulo
                        if( articulo == null ){
                            articulo = articuloRepository.findOne( detalleNotaVenta.idArticulo )
                        }
                        if( !StringUtils.trimToEmpty(articulo.idGenerico).equalsIgnoreCase("J") ){
                            det = detalleNotaVenta
                        }
                    }
                    if( detalles.size() > 0 && det != null ){
                        //DetalleNotaVenta det =  detalles.first()
                        BigDecimal monto = det.precioUnitFinal.add(diferencia)
                        if( diferencia.compareTo(BigDecimal.ZERO) > 0 || diferencia.compareTo(BigDecimal.ZERO) < 0 ){
                            det.setPrecioUnitFinal( monto )
                            det.setPrecioFactura( monto )
                            detalleNotaVentaRepository.save( det )
                            detalleNotaVentaRepository.flush()
                        }
                    }
                } else {
                    notaVenta.ventaNeta = total
                    notaVenta.ventaTotal = total
                }

                notaVenta.sumaPagos = pagado
                notaVenta.tipoNotaVenta = TAG_TIPO_NOTA_VENTA
                try {
                    notaVenta = notaVentaRepository.save( notaVenta )
                    notaVentaRepository.flush()
                    log.info( "notaVenta registrada id: ${notaVenta?.id}" )

                } catch ( ex ) {
                    log.error( "problema al registrar notaVenta: ${notaVenta?.dump()}", ex )
                }
            } else {
                log.warn( "no se registra notaVenta, id no existe" )
            }
      } else {
        log.warn( "no se registra notaVenta, parametros invalidos" )
      }
      return notaVenta;
    }

}
