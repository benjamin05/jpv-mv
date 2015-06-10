package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.TipoPagoJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TipoPagoQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static TipoPagoJava buscaParametroPoridFPago( String idformaPago ){
	  TipoPagoJava tipoPagoJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from tipo_pago where id_pago = '%s';", StringUtils.trimToEmpty(idformaPago));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          tipoPagoJava = new TipoPagoJava();
          tipoPagoJava = tipoPagoJava.mapeoTipoPago(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return tipoPagoJava;
	}
	
}
