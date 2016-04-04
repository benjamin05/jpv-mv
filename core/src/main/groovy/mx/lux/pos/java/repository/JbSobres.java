package mx.lux.pos.java.repository;

import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class JbSobres {

	Integer idSobre;
	String folioSobre;
	String dest;
	String emp;
	String area;
	String contenido;
	String idViaje;
	Date fechaEnvio;
    Date fecha;
    String idMod;
    String rx;
    Integer id;


    public Integer getIdSobre() {
        return idSobre;
    }

    public void setIdSobre(Integer idSobre) {
        this.idSobre = idSobre;
    }

    public String getFolioSobre() {
        return folioSobre;
    }

    public void setFolioSobre(String folioSobre) {
        this.folioSobre = folioSobre;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(String idViaje) {
        this.idViaje = idViaje;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JbSobres mapeoJbSobres(ResultSet rs) throws SQLException {
	  this.setIdSobre(rs.getInt("id_sobre"));
	  this.setFolioSobre(rs.getString("folio_sobre"));
	  this.setDest(rs.getString("dest"));
	  this.setEmp(rs.getString("emp"));
	  this.setArea(rs.getString("area"));
	  this.setContenido(rs.getString("contenido"));
	  this.setIdViaje(rs.getString("id_viaje"));
      this.setFechaEnvio(rs.getDate("fecha_envio"));
      this.setFecha(rs.getDate("fecha"));
      this.setIdMod(rs.getString("id_mod"));
      this.setRx(rs.getString("rx"));
      this.setId(rs.getInt("id"));
	  return this;
	}
}
