package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AcusesJava {

    Integer idAcuse;
	String contenido;
    Date fechaCarga;
    Date fechaAcuso;
	String idTipo;
	String folio;
    Integer intentos;

    public Integer getIdAcuse() {
        return idAcuse;
    }

    public void setIdAcuse(Integer idAcuse) {
        this.idAcuse = idAcuse;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public Date getFechaAcuso() {
        return fechaAcuso;
    }

    public void setFechaAcuso(Date fechaAcuso) {
        this.fechaAcuso = fechaAcuso;
    }

    public String getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(String idTipo) {
        this.idTipo = idTipo;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Integer getIntentos() {
        return intentos;
    }

    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }

    public AcusesJava mapeoAcuses(ResultSet rs) throws SQLException{
	  this.setIdAcuse(rs.getInt("id_acuse"));
	  this.setContenido(rs.getString("contenido"));
      this.setFechaCarga(rs.getDate("fecha_carga"));
      this.setFechaAcuso(rs.getDate("fecha_acuso"));
	  this.setIdTipo(rs.getString("id_tipo"));
      this.setFolio(rs.getString("folio"));
      this.setIntentos(rs.getInt("intentos"));
	  return this;
	}
	
	
}
