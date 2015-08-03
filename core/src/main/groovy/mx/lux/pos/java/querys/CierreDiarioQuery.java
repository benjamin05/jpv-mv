package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.CierreDiarioJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CierreDiarioQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static CierreDiarioJava buscaCierreDiarioPorFecha( Date fecha){
	  CierreDiarioJava cierreDiarioJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM cierre_diario where fecha = %s;", Utilities.toString(fecha, "yyyy-MM-dd"));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          cierreDiarioJava = new CierreDiarioJava();
          cierreDiarioJava = cierreDiarioJava.mapeoCierreDiario(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return cierreDiarioJava;
	}
	
}
