package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.DominioJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DominioQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<DominioJava> buscaTodoDominios( ){
	  List<DominioJava> lstDominios = new ArrayList<DominioJava>();
      DominioJava dominioJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM dominios;");
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          dominioJava = new DominioJava();
          dominioJava = dominioJava.mapeoDominio(rs);
          lstDominios.add(dominioJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstDominios;
	}
	
}
