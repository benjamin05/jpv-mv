package mx.lux.pos.querys;

import mx.lux.pos.repository.MunicipioJava;
import mx.lux.pos.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        while (rs.next()) {
          municipioJava = new MunicipioJava();
          municipioJava = municipioJava.mapeoMunicipio( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return municipioJava;
	}
	
}
