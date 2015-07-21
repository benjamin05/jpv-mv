package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.EstadoJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EstadoQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static EstadoJava BuscaEstadoPorIdEstado( String idEstado ){
      EstadoJava estadoJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM rep where id_estado = '%s';", StringUtils.trimToEmpty(idEstado));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          estadoJava = new EstadoJava();
          estadoJava = estadoJava.mapeoEstado( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return estadoJava;
	}
	
}
