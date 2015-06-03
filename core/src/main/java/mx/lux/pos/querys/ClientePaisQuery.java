package mx.lux.pos.querys;

import mx.lux.pos.repository.ClientePaisJava;
import mx.lux.pos.repository.MunicipioJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientePaisQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static ClientePaisJava BuscaClientePaisPoridCliente( Integer idCliente ){
      ClientePaisJava clientePaisJava = null;
      if( idCliente != null ){
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM cliente_pais where id_cliente = %d;", idCliente);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          clientePaisJava = new ClientePaisJava();
          clientePaisJava = clientePaisJava.mapeoClientePais( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      }
	  return clientePaisJava;
	}
	
}
