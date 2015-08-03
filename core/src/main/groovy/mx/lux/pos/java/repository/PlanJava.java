package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlanJava {

	String idPlan;
	String descripcion;
	String idBancoDep;

    public String getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(String idPlan) {
        this.idPlan = idPlan;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdBancoDep() {
        return idBancoDep;
    }

    public void setIdBancoDep(String idBancoDep) {
        this.idBancoDep = idBancoDep;
    }

    public PlanJava mapeoPlan(ResultSet rs) throws SQLException{
		PlanJava plan = new PlanJava();
		plan.setIdPlan(rs.getString("id_plan"));
		plan.setDescripcion(rs.getString("descripcion"));
		plan.setIdBancoDep(rs.getString("id_banco_dep"));
		return plan;
	}
	
	
}
