package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.DescuentosJava;
import mx.lux.pos.java.repository.DevolucionJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DevolucionQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static DevolucionJava buscaDevolucionPorIdPagoMontoAndTransf( Integer idPago, BigDecimal monto, String transf ){
	  DevolucionJava devolucion = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from dev where id_pago = %d AND monto_devolucion = %s AND transf = '%s';",
                idPago, Utilities.toMoney(monto), StringUtils.trimToEmpty(transf));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          devolucion = new DevolucionJava();
          devolucion = devolucion.mapeoDevolucion(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return devolucion;
	}


    public static void eliminaDevolucion( DevolucionJava devolucionJava ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("DELETE FROM dev WHERE id = %d;", devolucionJava.getId());
        stmt.executeUpdate(sql);
        con.close();
      } catch (SQLException e) {
                e.printStackTrace();
      }
    }


}
