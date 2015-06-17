package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.SucursalesJava;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;

public class SucursalesQuery {
	
	private static ResultSet rs;
    private static Statement stmt;

	public static SucursalesJava BuscaSucursalPorIdSuc( Integer idSucursal ){
	  SucursalesJava sucursalesJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from sucursales where id_sucursal = '%d';", idSucursal);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          sucursalesJava = new SucursalesJava();
          sucursalesJava = sucursalesJava.mapeoSucursales( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return sucursalesJava.getIdSucursal() != null ? sucursalesJava : null;
	}


    public static Integer getCurrentSucursalId( ) throws ParseException{
      Integer sucursal = 0;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("SELECT esta_sucursal();");
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          sucursal = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(rs.getString("esta_sucursal"))).intValue();
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
        e.printStackTrace();
      }
        return sucursal;
    }
}
