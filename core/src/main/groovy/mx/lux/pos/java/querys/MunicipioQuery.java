package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.MunicipioJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MunicipioQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static MunicipioJava BuscaMunicipioPorEstadoYLocalidad( String idEstado, String idLocalidad ){
      MunicipioJava municipioJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM munici where id_estado = '%s' AND id_localidad = '%s';", StringUtils.trimToEmpty(idEstado), StringUtils.trimToEmpty(idLocalidad));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          municipioJava = new MunicipioJava();
          municipioJava = municipioJava.mapeoMunicipio( rs );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return municipioJava;
	}
	
}
