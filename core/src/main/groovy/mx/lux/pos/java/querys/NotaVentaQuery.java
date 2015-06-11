package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.NotaVentaJava;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class NotaVentaQuery {

	private static ResultSet rs;
    private static Statement stmt;

    static final Logger log = LoggerFactory.getLogger(NotaVentaQuery.class);
    static final String TAG_ID_SUCURSAL = "id_sucursal";
    private static final String TAG_TIPO_NOTA_VENTA = String.valueOf('F');

	public static NotaVentaJava busquedaNotaById(String idNotaVenta) throws ParseException{
      NotaVentaJava notaVentaJava = null;
      if( StringUtils.trimToEmpty(idNotaVenta).length() > 0 ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("SELECT * FROM nota_venta WHERE id_factura = '%s';", StringUtils.trimToEmpty(idNotaVenta));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          notaVentaJava = new NotaVentaJava();
          notaVentaJava.setValores( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      }
  	  return notaVentaJava;
	}


    public static Boolean exists(String idFactura) throws ParseException {
      return busquedaNotaById(idFactura) != null;
    }


    public static NotaVentaJava busquedaNotaByFactura(String factura) throws ParseException{
        NotaVentaJava notaVentaJava = new NotaVentaJava();
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          String sql = "";
          if( StringUtils.trimToEmpty(factura).length() > 0 ){
                sql = String.format("SELECT * FROM nota_venta WHERE factura = '%s';", StringUtils.trimToEmpty(factura));
          } else {
            notaVentaJava = null;
          }
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            assert notaVentaJava != null;
            notaVentaJava.setValores( rs );
          }
          con.close();
        } catch (SQLException err) {
            System.out.println( err );
        }
        return notaVentaJava;
    }

    public static NotaVentaJava busquedaNotaByReceta(Integer idReceta) throws ParseException{
      NotaVentaJava notaVentaJava = null;
      if( idReceta != null ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("SELECT * FROM nota_venta WHERE receta = %d;", idReceta);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          notaVentaJava = new NotaVentaJava();
          notaVentaJava.setValores( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      }
      return notaVentaJava;
    }

    public static NotaVentaJava updateNotaVenta (NotaVentaJava notaVentaJava) throws ParseException {
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String sql = String.format("update nota_venta set id_empleado = '%s', id_cliente = %d, id_convenio = '%s'," +
              "tipo_nota_venta = '%s', fecha_rec_ord = %s, venta_total = %s, venta_neta = %s, suma_pagos = %s, fecha_prometida = %s," +
              "fecha_entrega = %s, f_armazon_cli = '%s', por100_descuento = %d, monto_descuento = %s, tipo_descuento = '%s'," +
              "f_resumen_notas_mo = '%s', s_factura = '%s', tipo_entrega = '%s', observaciones_nv = '%s', factura = '%s'," +
              "cant_lente = '%s', udf2 = '%s', udf3 = '%s', udf4 = '%s', udf5 = '%s', receta = %d, emp_entrego = '%s', lc = '%s', hora_entrega = %s," +
              "codigo_lente = '%s' WHERE id_factura = '%s'",
              notaVentaJava.getIdEmpleado(), notaVentaJava.getIdCliente(), notaVentaJava.getIdConvenio(), notaVentaJava.getTipoNotaVenta(),
              Utilities.toString(notaVentaJava.getFechaRecOrd(), formatDate), Utilities.toMoney(notaVentaJava.getVentaTotal()),
              Utilities.toMoney(notaVentaJava.getVentaNeta()), Utilities.toMoney(notaVentaJava.getSumaPagos()),
              Utilities.toString(notaVentaJava.getFechaPrometida(), formatDate), Utilities.toString(notaVentaJava.getFechaEntrega(), formatDate),
              notaVentaJava.getfArmazonCli().toString(), notaVentaJava.getPor100Descuento(), Utilities.toMoney(notaVentaJava.getMontoDescuento()),
              notaVentaJava.getTipoDescuento(), notaVentaJava.getfResumenNotasMo().toString(), notaVentaJava.getsFactura(),
              notaVentaJava.getTipoEntrega(), notaVentaJava.getObservacionesNv(), notaVentaJava.getFactura(), notaVentaJava.getCantLente(), notaVentaJava.getUdf2(),
              notaVentaJava.getUdf3(), notaVentaJava.getUdf4(), notaVentaJava.getUdf5(), Utilities.trimtoNull(notaVentaJava.getReceta()), notaVentaJava.getEmpEntrego(),
              notaVentaJava.getLc(), Utilities.toString(notaVentaJava.getHoraEntrega(), formatTime), notaVentaJava.getCodigoLente(), notaVentaJava.getIdFactura().trim());
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
      return busquedaNotaById( StringUtils.trimToEmpty(notaVentaJava.getIdFactura()) );
    }


    public static String busquedaFacturaByIdFactura(String idNotaVenta) throws ParseException{
      String factura = "";
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if( idNotaVenta.length() > 0 ){
                sql = String.format("SELECT * FROM nota_venta WHERE id_factura = '%s';", StringUtils.trimToEmpty(idNotaVenta));
        }
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          factura = StringUtils.trimToEmpty(rs.getString("factura"));
        }
        con.close();
        } catch (SQLException err) {
          System.out.println( err );
        }
        return factura;
    }


    public static List<NotaVentaJava> busquedaNotaByIdClienteAndFacturaEmpty(Integer idCliente) throws ParseException{
      List<NotaVentaJava> lstNotas = new ArrayList<NotaVentaJava>();
      NotaVentaJava notaVentaJava = null;
      if( idCliente != null ){
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          String sql = "";
          sql = String.format("SELECT * FROM nota_venta WHERE id_cliente = %d AND factura = '' OR factura is null;", idCliente);
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            notaVentaJava = new NotaVentaJava();
            notaVentaJava.setValores( rs );
            lstNotas.add( notaVentaJava );
          }
          con.close();
        } catch (SQLException err) {
          System.out.println( err );
        }
      }
      return lstNotas;
    }


    public static Integer getFacturaSequence(){
      BigDecimal id = BigDecimal.ZERO;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("SELECT NEXTVAL('factura_seq');");
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          id = rs.getBigDecimal("nextval");
        }
        con.close();
      } catch (SQLException err) {
           System.out.println( err );
      }
      return id != null ? id.intValue() : 0;
    }


}
