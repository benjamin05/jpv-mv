package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RepoResp {

    String responsable;

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public RepoResp mapeoRepoResp(ResultSet rs) throws SQLException{
      this.setResponsable( rs.getString("responsable"));
	  return this;
	}
	
	
}
