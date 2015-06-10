package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.ClientesProcesoJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ClientesProcesoQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static ClientesProcesoJava buscaClientesProcesoPorIdCliente( Integer idCliente ){
	  ClientesProcesoJava clientesProcesoJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM clientes_proceso where id_cliente = %d;", idCliente);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          clientesProcesoJava = new ClientesProcesoJava();
          clientesProcesoJava = clientesProcesoJava.mapeoClientesProceso(rs);
        }
        con.close();
      } catch (SQLException err) {
            System.out.println( err );
      }
	  return clientesProcesoJava;
	}



    public static ClientesProcesoJava saveOrUpdateClientesProceso(ClientesProcesoJava clientesProceso) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      ClientesProcesoJava clientesProcesoJava = null;
      if( clientesProceso != null && clientesProceso.getIdCliente() != null ){
        clientesProcesoJava = buscaClientesProcesoPorIdCliente( clientesProceso.getIdCliente() );
      }
      if( clientesProcesoJava != null ){
        sql = String.format("UPDATE clientes_proceso SET id_cliente = %d, etapa = '%s', id_sync = '%s', fecha_mod = '%s'," +
                "id_mod = '%s', id_sucursal = %d WHERE id_cliente = %d;",
                clientesProceso.getIdCliente(), clientesProceso.getEtapa(), clientesProceso.getIdSync(),
                Utilities.toString(clientesProceso.getFechaMod(), formatTimeStamp),clientesProceso.getIdMod(),
                clientesProceso.getIdSucursal(), clientesProceso.getIdCliente());
                db.updateQuery(sql);
      } else if( clientesProceso != null && clientesProceso.getIdCliente() != null ){
        sql = String.format("INSERT INTO clientes_proceso (id_cliente,etapa,id_sucursal) VALUES(%d,'%s',%d);",
                clientesProceso.getIdCliente(), clientesProceso.getEtapa(), clientesProceso.getIdSucursal());
                db.insertQuery( sql );
      }
      db.close();
      clientesProcesoJava = buscaClientesProcesoPorIdCliente( clientesProceso.getIdCliente() );

      return clientesProcesoJava;
    }


    public static List<ClientesProcesoJava> buscaClientesProcesoPorEtapa( String etapa ){
      List<ClientesProcesoJava> lstClientes = new ArrayList<ClientesProcesoJava>();
      ClientesProcesoJava clientesProcesoJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM clientes_proceso where etapa = '%s';", StringUtils.trimToEmpty(etapa));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          clientesProcesoJava = new ClientesProcesoJava();
          clientesProcesoJava = clientesProcesoJava.mapeoClientesProceso(rs);
          lstClientes.add( clientesProcesoJava );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstClientes;
    }


}