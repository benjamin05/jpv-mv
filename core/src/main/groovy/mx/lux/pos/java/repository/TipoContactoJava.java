package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoContactoJava {

	Integer idTipoContacto;
	String descripcion;

    public Integer getIdTipoContacto() {
        return idTipoContacto;
    }

    public void setIdTipoContacto(Integer idTipoContacto) {
        this.idTipoContacto = idTipoContacto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoContactoJava mapeoTipoContacto(ResultSet rs) throws SQLException{
	  this.setIdTipoContacto(rs.getInt("id_tipo_contacto"));
	  this.setDescripcion(rs.getString("descripcion"));
	  return this;
	}
	
	
}
