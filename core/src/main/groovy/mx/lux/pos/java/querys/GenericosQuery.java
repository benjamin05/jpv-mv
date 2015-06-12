package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.GenericosJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GenericosQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static GenericosJava buscaGenericosPorId( String idGenerico ){
	  GenericosJava genericosJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from genericos where id_generico = '%s';", StringUtils.trimToEmpty(idGenerico));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          genericosJava = new GenericosJava();
          genericosJava = genericosJava.mapeoGenericos(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return genericosJava;
	}
	
}
