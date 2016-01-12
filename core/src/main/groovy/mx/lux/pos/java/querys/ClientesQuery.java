package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.ClientesJava;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        con.close();
        while (rs.next()) {
          clientesJava = new ClientesJava();
          clientesJava.mapeoCliente( rs );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      }
  	  return clientesJava;
	}


    public static ClientesJava busquedaClienteByOrigen(String cliOri) throws ParseException{
      ClientesJava clientesJava = null;
      if(StringUtils.trimToEmpty(cliOri).length() > 0 ){
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          String sql = "";
          sql = String.format("SELECT * FROM clientes WHERE cli_ori = '%s';", StringUtils.trimToEmpty(cliOri));
          rs = stmt.executeQuery(sql);
          con.close();
          while (rs.next()) {
            clientesJava = new ClientesJava();
            clientesJava.mapeoCliente( rs );
          }
        } catch (SQLException err) {
          System.out.println( err );
        }
      }
      return clientesJava;
    }


    public static List<ClientesJava> listaClientesPorApePatOrApeMatOrNombreAndFechaNac(String apellidoPat,String apellidoMat,String nombre, Date fechaNac) throws ParseException{
      ClientesJava clientesJava = null;
      List<ClientesJava> lstClientes = new ArrayList<ClientesJava>();
      String paternoSql = "";
      String maternoSql = "";
      String nombreSql = "";
      String fechaNacSql = "";
      if( StringUtils.trimToEmpty(apellidoPat).length() > 0 ){
        paternoSql = String.format("AND apellido_pat_cli = '%s'", StringUtils.trimToEmpty(apellidoPat));
      }
      if( StringUtils.trimToEmpty(apellidoMat).length() > 0 ){
        maternoSql = "AND apellido_mat_cli like '%"+StringUtils.trimToEmpty(apellidoMat)+"%'";
      }
      if( StringUtils.trimToEmpty(nombre).length() > 0 ){
        nombreSql = "AND nombre_cli like '%"+StringUtils.trimToEmpty(nombre)+"%'" ;
      }
      if( fechaNac != null){
        fechaNacSql = String.format("AND fecha_nac = %s", Utilities.toString(fechaNac, "yyyy-MM-dd"));
      }
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = "select * from clientes where id_cliente is not null "+paternoSql+" "+maternoSql+" "+nombreSql+" "+fechaNacSql+"" +
                "ORDER BY apellido_pat_cli ASC limit 50;";
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          clientesJava = new ClientesJava();
          clientesJava.mapeoCliente( rs );
          lstClientes.add( clientesJava );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstClientes;
    }


    public static List<ClientesJava> listaClientesFechaNac(Date fecha) throws ParseException{
      ClientesJava clientesJava = null;
      List<ClientesJava> lstClientes = new ArrayList<ClientesJava>();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("select * from clientes where date(fecha_nac) = %s order by apellido_pat_cli," +
                "apellido_mat_cli, nombre_cli limit 50;", Utilities.toString(fecha, "yyyy-MM-dd"));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          clientesJava = new ClientesJava();
          clientesJava.mapeoCliente( rs );
          lstClientes.add( clientesJava );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstClientes;
    }


    public static List<ClientesJava> listaClientesPorNombreCompletoAndFechaNac(String apellidoPat, Date fecha) throws ParseException{
      ClientesJava clientesJava = null;
      List<ClientesJava> lstClientes = new ArrayList<ClientesJava>();
      try {
            Connection con = Connections.doConnect();
            stmt = con.createStatement();
            String sql = "";
            sql = "select * from clientes where apellido_pat_cli like upper( "+StringUtils.trimToEmpty(apellidoPat)+" || '%' )" +
                    "and date(fecha_nac) = "+fecha+" order by apellido_pat_cli, apellido_mat_cli, nombre_cli limit 50;";
            rs = stmt.executeQuery(sql);
            con.close();
            while (rs.next()) {
                clientesJava = new ClientesJava();
                clientesJava.mapeoCliente( rs );
                lstClientes.add( clientesJava );
            }
        } catch (SQLException err) {
            System.out.println( err );
        }
        return lstClientes;
    }
}
