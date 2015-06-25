package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.MontoCuponJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MontoCuponQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static MontoCuponJava buscaMontoCuponPorGenericoTipoYMontoIgual( String idGenerico, String tipo, BigDecimal precio ){
	  MontoCuponJava montoCupon = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from monto_cupon where generico = '%s' AND tipo = '%s'" +
                "AND monto_minimo = %s AND monto_maximo = %s;", StringUtils.trimToEmpty(idGenerico), StringUtils.trimToEmpty(tipo),
                Utilities.toMoney(precio), Utilities.toMoney(precio));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          montoCupon = new MontoCuponJava();
          montoCupon = montoCupon.mapeoMontoCupon(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return montoCupon;
	}


    public static MontoCuponJava buscaMontoCuponPorGenericoTipoYMontoMenorMayor( String idGenerico, String tipo, BigDecimal precio ){
      MontoCuponJava montoCupon = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from monto_cupon where generico = '%s' AND tipo = '%s'" +
                "AND monto_minimo < %s AND monto_maximo > %s;", StringUtils.trimToEmpty(idGenerico), StringUtils.trimToEmpty(tipo),
                Utilities.toMoney(precio), Utilities.toMoney(precio));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          montoCupon = new MontoCuponJava();
          montoCupon = montoCupon.mapeoMontoCupon(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return montoCupon;
    }



    public static MontoCuponJava buscaMontoCuponPorGenericoYMontoMenorMayor( String idGenerico, BigDecimal precio ){
      MontoCuponJava montoCupon = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from monto_cupon where generico = '%s' AND monto_minimo < %s AND monto_maximo > %s;",
                StringUtils.trimToEmpty(idGenerico), Utilities.toMoney(precio), Utilities.toMoney(precio));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          montoCupon = new MontoCuponJava();
          montoCupon = montoCupon.mapeoMontoCupon(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return montoCupon;
    }



    public static List<MontoCuponJava> buscaMontoCuponPorGenericoYSubtipo( String idGenerico, String subtipo ){
      List<MontoCuponJava> lstMontoCupon = new ArrayList<MontoCuponJava>();
      MontoCuponJava montoCupon = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from monto_cupon where generico = '%s' AND subtipo = '%s';",
                StringUtils.trimToEmpty(idGenerico), StringUtils.trimToEmpty(subtipo));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          montoCupon = new MontoCuponJava();
          montoCupon = montoCupon.mapeoMontoCupon(rs);
          lstMontoCupon.add(montoCupon);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstMontoCupon;
    }


}
