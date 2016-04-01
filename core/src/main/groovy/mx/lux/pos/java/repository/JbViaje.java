package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class JbViaje {

	String idViaje;
	String folio;
	Date hora;
	Date fecha;
	Boolean abierto;
	String emp;

    public String getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(String idViaje) {
        this.idViaje = idViaje;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Boolean getAbierto() {
        return abierto;
    }

    public void setAbierto(Boolean abierto) {
        this.abierto = abierto;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public JbViaje setValores( ResultSet rs ) throws SQLException {
      this.setIdViaje(rs.getString("id_viaje"));
      this.setFolio(rs.getString("id_viaje"));
      this.setFecha(rs.getDate("fecha"));
      this.setHora(rs.getDate("hora"));
      this.setAbierto(Utilities.toBoolean(rs.getBoolean("abierto")));
      this.setEmp(rs.getString("emp"));
	  return this;
	}
}
