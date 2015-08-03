package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class FormaContactoJava {

	String rx;
    Integer idCliente;
    Integer idTipoContacto;
	String contacto;
	String observaciones;
    Date fechaMod;
    Integer idSucursal;
    TipoContactoJava tipoContacto;

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdTipoContacto() {
        return idTipoContacto;
    }

    public void setIdTipoContacto(Integer idTipoContacto) {
        this.idTipoContacto = idTipoContacto;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public TipoContactoJava getTipoContacto() {
        return tipoContacto;
    }

    public void setTipoContacto(TipoContactoJava tipoContacto) {
        this.tipoContacto = tipoContacto;
    }

    public FormaContactoJava mapeoFormaContacto(ResultSet rs) throws SQLException{
	  this.setRx(rs.getString("rx"));
	  this.setIdTipoContacto(rs.getInt("id_tipo_contacto"));
	  this.setContacto(rs.getString("contacto"));
      this.setObservaciones(rs.getString("observaciones"));
      this.setFechaMod(rs.getDate("fecha_mod"));
      this.setIdSucursal(rs.getInt("id_sucursal"));
	  return this;
	}
	
	
}
