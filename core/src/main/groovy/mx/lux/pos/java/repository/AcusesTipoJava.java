package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AcusesTipoJava {

    String idTipo;
    String pagina;
	String descr;

    public String getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(String idTipo) {
        this.idTipo = idTipo;
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public AcusesTipoJava mapeoAcusesTipo(ResultSet rs) throws SQLException{
	  this.setIdTipo(rs.getString("id_tipo"));
	  this.setPagina(rs.getString("pagina"));
      this.setDescr(rs.getString("descr"));
      return this;
	}
	
	
}
