package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.CuponMvJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CuponMvQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<CuponMvJava> buscaCuponMvPorFacturaOrigen( String idFactura ){
	  List<CuponMvJava> lstCupones = new ArrayList<CuponMvJava>();
      CuponMvJava cuponMvJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from cupon_mv where factura_origen = '%s' ORDER BY fecha_vigencia DESC;",
                StringUtils.trimToEmpty(idFactura));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          cuponMvJava = new CuponMvJava();
          cuponMvJava = cuponMvJava.mapeoCuponMv(rs);
          lstCupones.add(cuponMvJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstCupones;
	}



    public static List<CuponMvJava> buscaCuponMvPorFacturaDestino( String idFactura ){
      List<CuponMvJava> lstCupones = new ArrayList<CuponMvJava>();
      CuponMvJava cuponMvJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from cupon_mv where factura_destino = '%s';", StringUtils.trimToEmpty(idFactura));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
                cuponMvJava = new CuponMvJava();
                cuponMvJava = cuponMvJava.mapeoCuponMv(rs);
                lstCupones.add(cuponMvJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstCupones;
    }


    public static CuponMvJava buscaCuponMvPorFacturaDestinoAndFacturaOrigen( String idFacturaDestino, String idFacturaOrigen ){
      CuponMvJava cuponMvJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from cupon_mv where factura_destino = '%s' AND factura_origen = '%s';",
                StringUtils.trimToEmpty(idFacturaDestino), StringUtils.trimToEmpty(idFacturaOrigen));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          cuponMvJava = new CuponMvJava();
          cuponMvJava = cuponMvJava.mapeoCuponMv(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return cuponMvJava;
    }


  public static void deleteCuponMv( String clave ){
    try {
      Connection con = Connections.doConnect();
      stmt = con.createStatement();
      String sql = String.format("DELETE FROM cupon_mv WHERE clave_descuento = '%s';", StringUtils.trimToEmpty(clave));
      stmt.executeUpdate(sql);
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  public static void insertClave(String clave, String facturaOrigen, String facturaDestino, Date fechaAplicacion, Date fechaVigencia) throws ParseException {
    Connections db = new Connections();
    String sql = "";
    String formatDate = "yyyy-MM-dd";
    String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
    sql = String.format("INSERT INTO cupon_mv(clave_descuento,factura_origen,factura_destino,fecha_aplicacion,fecha_vigencia)" +
            "VALUES('%s','%s','%s',%s,%s);", StringUtils.trimToEmpty(clave), facturaOrigen, facturaDestino,
            Utilities.toString(fechaAplicacion, formatTimeStamp), Utilities.toString(fechaVigencia, formatDate));
    db.insertQuery(sql);
    db.close();
  }


  public static CuponMvJava buscaCuponMvPorClave( String clave ){
    CuponMvJava cuponMvJava = null;
    try {
      Connection con = Connections.doConnect();
      stmt = con.createStatement();
      String sql = String.format("select * from cupon_mv where clave_descuento = '%s';", StringUtils.trimToEmpty(clave));
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        cuponMvJava = new CuponMvJava();
        cuponMvJava = cuponMvJava.mapeoCuponMv(rs);
      }
      con.close();
    } catch (SQLException err) {
            System.out.println( err );
    }
    return cuponMvJava;
  }



  public static void updateCuponMv(CuponMvJava cuponMvJava) throws ParseException {
    Connections db = new Connections();
    String sql = "";
    String formatDate = "yyyy-MM-dd";
    String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
    sql = String.format("UPDATE cupon_mv SET clave_descuento = '%s', factura_origen = '%s', factura_destino = '%s'," +
            "fecha_aplicacion = %s, fecha_vigencia = %s, monto_cupon = %s;", StringUtils.trimToEmpty(cuponMvJava.getClaveDescuento()),
            cuponMvJava.getFacturaOrigen(), cuponMvJava.getFacturaDestino(), Utilities.toString(cuponMvJava.getFechaAplicacion(), formatTimeStamp),
            Utilities.toString(cuponMvJava.getFechaVigencia(), formatDate), Utilities.toMoney(cuponMvJava.getMontoCupon()));
    db.insertQuery(sql);
    db.close();
  }


  public static void insertCuponMv(CuponMvJava cuponMvJava) throws ParseException {
    Connections db = new Connections();
    String sql = "";
    String formatDate = "yyyy-MM-dd";
    String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
    sql = String.format("INSERT INTO cupon_mv (clave_descuento, factura_origen, factura_destino," +
            "fecha_aplicacion, fecha_vigencia, monto_cupon) VALUES('%s','%s','%s',%s,%s,%s);",
            StringUtils.trimToEmpty(cuponMvJava.getClaveDescuento()), cuponMvJava.getFacturaOrigen(),
            cuponMvJava.getFacturaDestino(), Utilities.toString(cuponMvJava.getFechaAplicacion(), formatTimeStamp),
            Utilities.toString(cuponMvJava.getFechaVigencia(), formatDate), Utilities.toMoney(cuponMvJava.getMontoCupon()));
    db.insertQuery(sql);
    db.close();
  }


}
