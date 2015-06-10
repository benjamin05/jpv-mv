package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.SucursalesJava;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}
