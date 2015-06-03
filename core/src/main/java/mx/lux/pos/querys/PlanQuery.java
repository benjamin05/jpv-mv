package mx.lux.pos.querys;

import mx.lux.pos.repository.Parametros;
import mx.lux.pos.repository.PlanJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        while (rs.next()) {
          planJava.mapeoPlan( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return planJava;
	}
	
}
