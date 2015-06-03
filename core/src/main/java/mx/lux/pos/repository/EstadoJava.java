package mx.lux.pos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EstadoJava {

	String idEstado;
	String nombre;
	String edo1;
    String rango1;
    String rango2;

    public String getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRango1() {
        return rango1;
    }

    public void setRango1(String rango1) {
        this.rango1 = rango1;
    }

    public String getRango2() {
        return rango2;
    }

    public void setRango2(String rango2) {
        this.rango2 = rango2;
    }

    public String getEdo1() {
        return edo1;
    }

    public void setEdo1(String edo1) {
        this.edo1 = edo1;
    }

    public EstadoJava mapeoEstado(ResultSet rs) throws SQLException{
      this.setIdEstado(rs.getString("id_estado"));
	  this.setNombre(rs.getString("nombre"));
      this.setEdo1(rs.getString("edo1"));
      this.setRango1(rs.getString("rango1"));
      this.setRango2(rs.getString("rango2"));
	  return this;
	}
}
