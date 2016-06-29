package mx.lux.pos.java.querys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.*;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmpleadoQuery {
	
	private static ResultSet rs;
    private static Statement stmt;

	public static EmpleadoJava buscaEmpPorIdEmpleado(String idEmpleado){
		EmpleadoJava emp = new EmpleadoJava();
		try {
            Connection con = Connections.doConnect();
            stmt = con.createStatement();
            String sql = String.format("select * from empleado where id_empleado = '%s';", StringUtils.trimToEmpty(idEmpleado));
            rs = stmt.executeQuery(sql);
            con.close();
            while (rs.next()) {
             emp = emp.mapeoEmpleado( rs );
            }
        } catch (SQLException err) {
            System.out.println( err );
        }
		return emp.getIdEmpleado() != null ? emp : null;
	}



    public static RegionalJava buscaRegionalPorClaveTarjeta(String claveTarjeta){
      RegionalJava emp = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from regional where credencial = '%s';", StringUtils.trimToEmpty(claveTarjeta));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          emp = new RegionalJava();
          emp = emp.mapeoRegional(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
          e.printStackTrace();
      }
        return emp != null ? emp : null;
    }



    public static void saveChecada(ChecadasJava checada) {
        Connections db = new Connections();
        String sql = "";
        String formatDate = "yyyy-MM-dd";
        String formatTime = "HH:mm:ss.SSS";
        String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
        sql = String.format("INSERT INTO checadas VALUES('%s',%s,%s,'%s','%s');", StringUtils.trimToEmpty(checada.getSucursal()),
                Utilities.toString(checada.getFecha(),formatDate),
                Utilities.toString(checada.getHora(), formatTime),StringUtils.trimToEmpty(checada.getEmpresa()),
                StringUtils.trimToEmpty(checada.getIdEmpleado()));
        db.insertQuery( sql );
        db.close();
    }


    public static List<ChecadasJava> buscaChecadasPorFecha(Date fecha){
      ChecadasJava checada = null;
      List<ChecadasJava> lstChecadas = new ArrayList<ChecadasJava>();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from checadas where fecha = %s ORDER BY id_empleado,hora ASC;", Utilities.toString(fecha, "yyyy-MM-dd"));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          checada = new ChecadasJava();
          checada = checada.mapeoChecadas(rs);
          lstChecadas.add(checada);
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
          e.printStackTrace();
      }
        return lstChecadas;
    }


    public static List<ChecadasJava> buscaChecadasPorRangoFecha(Date fechaInicio, Date fechaFin){
      List<ChecadasJava> lstChecadas = new ArrayList<ChecadasJava>();
      ChecadasJava checada = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM checadas WHERE fecha BETWEEN %s AND %s ORDER BY id_empleado,hora ASC;",
                  Utilities.toString(fechaInicio, "yyyy-MM-dd HH:ss"), Utilities.toString(fechaFin, "yyyy-MM-dd HH:ss"));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          checada = new ChecadasJava();
          checada = checada.mapeoChecadas(rs);
          lstChecadas.add(checada);
        }
      } catch (SQLException err) {
        System.out.println(err);
      } catch (ParseException e) {
        e.printStackTrace();
      }
        return lstChecadas;
    }



    public static RegionalJava buscaRegionalPorIdEmpleado(String idEmpleado){
      RegionalJava emp = new RegionalJava();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from regional where id_empleado = '%s';", StringUtils.trimToEmpty(idEmpleado));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          emp = emp.mapeoRegional(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
          e.printStackTrace();
      }
        return emp.getIdEmpleado() != null ? emp : null;
    }


    public static void saveRegional(RegionalJava regional) {
      Connections db = new Connections();
      String sql = "";
      sql = String.format("INSERT INTO regional VALUES('%s','%s','%s','%s');", StringUtils.trimToEmpty(regional.getIdEmpresa()),
              StringUtils.trimToEmpty(regional.getIdEmpleado()),StringUtils.trimToEmpty(regional.getNombre()),
              StringUtils.trimToEmpty(regional.getCredencial()));
      db.insertQuery( sql );
      db.close();
    }
}
