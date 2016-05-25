package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RepoCausa {

    Integer idCausa;
    String descr;



    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Integer getIdCausa() {
        return idCausa;
    }

    public void setIdCausa(Integer idCausa) {
        this.idCausa = idCausa;
    }


    public RepoCausa mapeoRepoCausa(ResultSet rs) throws SQLException{
      this.setIdCausa( rs.getInt("id_causa"));
      this.setDescr( rs.getString("descr"));
	  return this;
	}
	
	
}
