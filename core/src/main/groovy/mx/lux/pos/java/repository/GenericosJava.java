package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GenericosJava {

	String idGenerico;
	String descripcionGenerico;
	String idSync;
    Date fechaMod;
    String idMod;
    Integer idSucursal;
    String surte;
    Boolean inventariable;

    public String getIdGenerico() {
        return idGenerico;
    }

    public void setIdGenerico(String idGenerico) {
        this.idGenerico = idGenerico;
    }

    public String getDescripcionGenerico() {
        return descripcionGenerico;
    }

    public void setDescripcionGenerico(String descripcionGenerico) {
        this.descripcionGenerico = descripcionGenerico;
    }

    public String getIdSync() {
        return idSync;
    }

    public void setIdSync(String idSync) {
        this.idSync = idSync;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getSurte() {
        return surte;
    }

    public void setSurte(String surte) {
        this.surte = surte;
    }

    public Boolean getInventariable() {
        return inventariable;
    }

    public void setInventariable(Boolean inventariable) {
        this.inventariable = inventariable;
    }

    public GenericosJava mapeoGenericos(ResultSet rs) throws SQLException{
	  this.setIdGenerico(rs.getString("id_generico"));
	  this.setDescripcionGenerico(rs.getString("descripcion_generico"));
	  this.setIdSync(rs.getString("id_sync"));
      this.setFechaMod(rs.getDate("fecha_mod"));
      this.setIdMod(rs.getString("id_mod"));
      this.setIdSucursal(rs.getInt("id_sucursal"));
      this.setSurte(rs.getString("surte"));
      this.setInventariable(Utilities.toBoolean(rs.getBoolean("inventariable")));
	  return this;
	}
	
	
}
