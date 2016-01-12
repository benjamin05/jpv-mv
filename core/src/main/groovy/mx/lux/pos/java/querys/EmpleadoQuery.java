package mx.lux.pos.java.querys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import mx.lux.pos.java.repository.EmpleadoJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

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
            rs.next();            
            emp = emp.mapeoEmpleado( rs );
        } catch (SQLException err) {
            System.out.println( err );
        }
		return emp.getIdEmpleado() != null ? emp : null;
	}
}