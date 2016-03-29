package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.OrdenPromDetQuery;
import mx.lux.pos.java.querys.TipoContactoQuery;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public FormaContactoJava mapeoFormaContacto(ResultSet rs) throws SQLException, ParseException {
	  this.setRx(rs.getString("rx"));
	  this.setIdTipoContacto(rs.getInt("id_tipo_contacto"));
	  this.setContacto(rs.getString("contacto"));
      this.setObservaciones(rs.getString("observaciones"));
      this.setFechaMod(rs.getDate("fecha_mod"));
      this.setIdSucursal(rs.getInt("id_sucursal"));
      this.setTipoContacto(tipoContactoJava());
	  return this;
	}


    private TipoContactoJava tipoContactoJava( ) throws ParseException {
      TipoContactoJava tipoContactoJava = TipoContactoQuery.buscaTipoContactoPorIdTipoContacto(this.idTipoContacto);
      return tipoContactoJava;
    }
}
