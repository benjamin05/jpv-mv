package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ModificacionJava {

    Integer idMod;
	String idFactura;
	String tipo;
    Date fecha;
	String empleado;
    String causa;
    String obs;

    public Integer getIdMod() {
        return idMod;
    }

    public void setIdMod(Integer idMod) {
        this.idMod = idMod;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public ModificacionJava mapeoModificaion(ResultSet rs) throws SQLException{
	  this.setIdMod(rs.getInt("id_mod"));
	  this.setIdFactura(rs.getString("id_factura"));
	  this.setTipo(rs.getString("tipo"));
      this.setFecha(rs.getDate("fecha"));
      this.setEmpleado(rs.getString("empleado"));
      this.setCausa(rs.getString("causa"));
      this.setObs(rs.getString("obs"));
	  return this;
	}
	
	
}
