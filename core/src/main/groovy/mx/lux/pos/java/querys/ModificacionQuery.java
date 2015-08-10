package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.ModificacionJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ModificacionQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<ModificacionJava> buscaModificaionPorIdFacturaAndTipo( String idFactura, String tipo ){
	  List<ModificacionJava> lstModificaciones = new ArrayList<ModificacionJava>();
      ModificacionJava modificacionJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from mod where id_factura = '%s' AND tipo = '%s';",
                StringUtils.trimToEmpty(idFactura), StringUtils.trimToEmpty(tipo));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          modificacionJava = new ModificacionJava();
          modificacionJava = modificacionJava.mapeoModificaion(rs);
          lstModificaciones.add(modificacionJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstModificaciones;
	}
	
}
