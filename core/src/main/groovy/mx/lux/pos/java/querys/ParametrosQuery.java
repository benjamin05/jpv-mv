package mx.lux.pos.java.querys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

public class ParametrosQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static Parametros BuscaParametroPorId( String idParametro ){
	  List<Parametros> lstParametros = new ArrayList<Parametros>();
      Parametros parametro = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from gparametro where id_parametro = '%s';", StringUtils.trimToEmpty(idParametro));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          parametro = new Parametros();
          parametro = parametro.mapeoParametro(rs);
          lstParametros.add(parametro);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstParametros.size() > 0 ? lstParametros.get(0): new Parametros();
	}
	
}
