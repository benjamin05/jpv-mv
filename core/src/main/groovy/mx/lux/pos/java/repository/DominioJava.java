package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DominioJava {

    Integer idDominio;
	String nombre;
	Date fechaMod;

    public Integer getIdDominio() {
        return idDominio;
    }

    public void setIdDominio(Integer idDominio) {
        this.idDominio = idDominio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public DominioJava mapeoDominio(ResultSet rs) throws SQLException{
	  this.setIdDominio(rs.getInt("id_dominio"));
	  this.setNombre(rs.getString("nombre"));
	  this.setFechaMod(rs.getDate("fecha_mod"));
	  return this;
	}
	
	
}
