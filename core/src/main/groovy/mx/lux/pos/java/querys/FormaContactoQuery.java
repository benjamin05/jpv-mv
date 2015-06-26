package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.FormaContactoJava;
import mx.lux.pos.java.repository.Parametros;
import mx.lux.pos.java.repository.RecetaJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FormaContactoQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<FormaContactoJava> buscaFormaContactoPorIdCliente( Integer idCliente ){
	  List<FormaContactoJava> lstFormasContacto = new ArrayList<FormaContactoJava>();
      FormaContactoJava formaContactoJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM forma_contacto where id_cliente = %d;", idCliente);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          formaContactoJava = new FormaContactoJava();
          formaContactoJava = formaContactoJava.mapeoFormaContacto(rs);
          lstFormasContacto.add(formaContactoJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstFormasContacto;
	}


    public static FormaContactoJava buscaFormaContactoPorRx( String rx ){
      FormaContactoJava formaContactoJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM forma_contacto where rx = '%s';", StringUtils.trimToEmpty(rx));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          formaContactoJava = new FormaContactoJava();
          formaContactoJava = formaContactoJava.mapeoFormaContacto(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return formaContactoJava;
    }


    public static FormaContactoJava saveFormaContacto (FormaContactoJava formaContactoJava) {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      FormaContactoJava formaContacto = null;
      if( formaContactoJava != null ){
        sql = String.format("INSERT INTO forma_contacto (rx,id_cliente,id_tipo_contacto,contacto,observaciones,fecha_mod,id_sucursal)" +
                  "VALUES('%s',%d,%d,'%s','%s',%s,%d);", formaContactoJava.getRx(), formaContactoJava.getIdCliente(), formaContactoJava.getIdTipoContacto(),
                  formaContactoJava.getContacto(), StringUtils.trimToEmpty(formaContactoJava.getObservaciones()), Utilities.toString(formaContactoJava.getFechaMod(), formatTimeStamp),
                  formaContactoJava.getIdSucursal());
        db.insertQuery( sql );
        db.close();
        formaContacto = buscaFormaContactoPorRx( formaContactoJava.getRx() );
      }
      return formaContacto;
    }


}
