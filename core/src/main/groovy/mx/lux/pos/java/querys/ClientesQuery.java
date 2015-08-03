package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.ClientesJava;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class ClientesQuery {

	private static ResultSet rs;
    private static Statement stmt;

    static final Logger log = LoggerFactory.getLogger(ClientesQuery.class);

	public static ClientesJava busquedaClienteById(Integer idCLiente) throws ParseException{
      ClientesJava clientesJava = null;
      if( idCLiente != null ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("SELECT * FROM clientes WHERE id_cliente = %d;", idCLiente);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          clientesJava = new ClientesJava();
          clientesJava.mapeoCliente( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      }
  	  return clientesJava;
	}

}
