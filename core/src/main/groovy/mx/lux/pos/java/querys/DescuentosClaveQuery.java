package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.DescuentosClaveJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DescuentosClaveQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static DescuentosClaveJava buscaDescuentoClavePorClave( String clave ){
	  DescuentosClaveJava descuentoClave = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from descuentos_clave where clave_descuento = '%s';", StringUtils.trimToEmpty(clave));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          descuentoClave = new DescuentosClaveJava();
          descuentoClave = descuentoClave.mapeoDescuentosClave(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return descuentoClave;
	}
	
}
