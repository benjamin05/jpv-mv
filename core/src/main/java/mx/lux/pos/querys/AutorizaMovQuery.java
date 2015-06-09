package mx.lux.pos.querys;

import mx.lux.pos.repository.AutorizaMovJava;
import mx.lux.pos.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AutorizaMovQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static AutorizaMovJava buscaAutorizaMovPorFactura( String factura ){
      AutorizaMovJava autorizaMovJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from autoriza_mov where factura = '%s';", StringUtils.trimToEmpty(factura));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          autorizaMovJava = new AutorizaMovJava();
          autorizaMovJava = autorizaMovJava.mapeoAurotizaMov( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return autorizaMovJava;
	}
	
}
