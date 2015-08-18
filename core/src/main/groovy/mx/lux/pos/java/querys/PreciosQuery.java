package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.PreciosJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PreciosQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<PreciosJava> buscaPreciosPorArticulo( String articulo ){
	  List<PreciosJava> lstPrecios = new ArrayList<PreciosJava>();
      PreciosJava preciosJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from precios where articulo = '%s';", StringUtils.trimToEmpty(articulo));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          preciosJava = new PreciosJava();
          preciosJava = preciosJava.mapeoPrecios(rs);
          lstPrecios.add(preciosJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstPrecios;
	}
	
}
