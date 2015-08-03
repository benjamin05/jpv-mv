package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.Parametros;
import mx.lux.pos.java.repository.TipoContactoJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TipoContactoQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<TipoContactoJava> buscaTodoTipoContacto( ){
	  List<TipoContactoJava> lstTiposContacto = new ArrayList<TipoContactoJava>();
      TipoContactoJava tipoContactoJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM tipo_contacto;");
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          tipoContactoJava = new TipoContactoJava();
          tipoContactoJava = tipoContactoJava.mapeoTipoContacto(rs);
          lstTiposContacto.add(tipoContactoJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstTiposContacto;
	}


    public static TipoContactoJava buscaTipoContactoPorIdTipoContacto( Integer idTipoContacto ){
      TipoContactoJava tipoContactoJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM tipo_contacto WHERE id_tipo_contacto = %d;", idTipoContacto);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          tipoContactoJava = new TipoContactoJava();
          tipoContactoJava = tipoContactoJava.mapeoTipoContacto(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return tipoContactoJava;
    }
}
