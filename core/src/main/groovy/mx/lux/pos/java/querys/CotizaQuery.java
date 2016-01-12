package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.CotizaJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CotizaQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static CotizaJava buscaCotizaPorId( Integer idCotizacion ){
	  CotizaJava cotizaJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from cotiza where id_cotiza = %d;", idCotizacion);
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          cotizaJava = new CotizaJava();
          cotizaJava = cotizaJava.mapeoCotiza(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return cotizaJava;
	}



    public static CotizaJava saveOrUpdateCotiza(CotizaJava cotizaJava) throws ParseException {
      Connections db = new Connections();
      CotizaJava cotiza = null;
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      if( cotizaJava.getIdCotiza() != null ){
        sql = String.format("UPDATE cotiza SET id_sucursal = %d, id_cliente = %d, id_empleado = '%s', id_receta = %d," +
                    "fecha_mod = %s, id_factura = '%s', fecha_venta = %s, nombre = '%s', telefono = '%s', observaciones = '%s'," +
                "udf1 = '%s', titulo = '%s', fecha_cotizacion = %s WHERE id_cotiza = %d;",
                cotizaJava.getIdSucursal(), cotizaJava.getIdCliente(), cotizaJava.getIdEmpleado(), cotizaJava.getIdReceta(),
                Utilities.toString(cotizaJava.getFechaMod(), formatTimeStamp), cotizaJava.getIdFactura(),
                Utilities.toString(cotizaJava.getFechaVenta(), formatTimeStamp), cotizaJava.getNombre(), cotizaJava.getTelefono(),
                cotizaJava.getObservaciones(), StringUtils.trimToEmpty(cotizaJava.getUdf1()), cotizaJava.getTitulo(),
                Utilities.toString(cotizaJava.getFechaCotizacion(), formatTimeStamp), cotizaJava.getIdCotiza());
      } else {
        sql = String.format("INSERT INTO cotiza (id_sucursal, id_cliente, id_empleado, id_receta, fecha_mod," +
                "id_factura, fecha_venta, nombre, telefono, observaciones, udf1, titulo, fecha_cotizacion)" +
                "VALUES(%d,%d,'%s',%d,%s,'%s',%s,'%s','%s','%s','%s','%s',%s);",
                cotizaJava.getIdSucursal(), cotizaJava.getIdCliente(), cotizaJava.getIdEmpleado(), cotizaJava.getIdReceta(),
                Utilities.toString(cotizaJava.getFechaMod(), formatTimeStamp), cotizaJava.getIdFactura(),
                Utilities.toString(cotizaJava.getFechaVenta(), formatTimeStamp), cotizaJava.getNombre(), cotizaJava.getTelefono(),
                cotizaJava.getObservaciones(), StringUtils.trimToEmpty(cotizaJava.getUdf1()), cotizaJava.getTitulo(),
                Utilities.toString(cotizaJava.getFechaCotizacion(), formatTimeStamp));
      }

      db.insertQuery( sql );
      db.close();
      if( cotizaJava.getIdCotiza() != null ){
          cotiza = buscaCotizaPorId( cotizaJava.getIdCotiza() );
      } else {
        BigDecimal id = BigDecimal.ZERO;
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          sql = "";
          sql = String.format("SELECT last_value FROM cotiza_id_cotiza_seq;");
          rs = stmt.executeQuery(sql);
          con.close();
          while (rs.next()) {
            id = rs.getBigDecimal("last_value");
          }
          if( id.compareTo(BigDecimal.ZERO) > 0 ){
            con = Connections.doConnect();
            stmt = con.createStatement();
            sql = "";
            sql = String.format("SELECT * FROM cotiza WHERE id_cotiza = %d;", id.intValue());
            rs = stmt.executeQuery(sql);
            con.close();
            while (rs.next()) {
              cotiza = new CotizaJava();
              cotiza = cotiza.mapeoCotiza(rs);
            }
          }
        } catch (SQLException err) {
          System.out.println( err );
        }
      }
      return cotiza;
    }


    public static CotizaJava buscaCotizaPorIdFactura( String idFactura ){
      CotizaJava cotizaJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from cotiza where id_factura = '%s';", StringUtils.trimToEmpty(idFactura));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          cotizaJava = new CotizaJava();
          cotizaJava = cotizaJava.mapeoCotiza(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return cotizaJava;
    }
}
