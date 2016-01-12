package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.Parametros;
import mx.lux.pos.java.repository.PlanJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PlanQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static PlanJava BuscaPlanPorIdPlan( String idPlan ){
	  PlanJava planJava = new PlanJava();
      Parametros parametro = new Parametros();
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from  plan where id_plan = '%s';", StringUtils.trimToEmpty(idPlan));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          planJava = planJava.mapeoPlan( rs );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return planJava;
	}
	
}
