package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class PreciosJava {

	String lista;
	String articulo;
    BigDecimal precio;
    Date fecha;
	String surte;
    Integer id;

    public String getLista() {
        return lista;
    }

    public void setLista(String lista) {
        this.lista = lista;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getSurte() {
        return surte;
    }

    public void setSurte(String surte) {
        this.surte = surte;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PreciosJava mapeoPrecios(ResultSet rs) throws SQLException{
	  this.setLista(rs.getString("lista"));
      this.setArticulo(rs.getString("articulo"));
      this.setPrecio(Utilities.toBigDecimal(rs.getString("precio")));
      this.setFecha(rs.getDate("fecha"));
      this.setSurte(rs.getString("surte"));
      this.setId(rs.getInt("id"));
	  return this;
	}
	
	
}
