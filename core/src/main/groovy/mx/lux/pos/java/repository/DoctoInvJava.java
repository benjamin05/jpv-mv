package mx.lux.pos.java.repository;

import mx.lux.pos.model.DoctoInv;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DoctoInvJava {

    String idDocto;
	String idTipoDocto;
	Date fecha;
    String usuario;
    String referencia;
    String idSync;
    String idMod;
    Date fechaMod;
    Integer idSucursal;
    String notas;
    String cantidad;
    String estado;
    String sistema;



    public String getIdDocto() {
        return idDocto;
    }

    public void setIdDocto(String idDocto) {
        this.idDocto = idDocto;
    }

    public String getIdTipoDocto() {
        return idTipoDocto;
    }

    public void setIdTipoDocto(String idTipoDocto) {
        this.idTipoDocto = idTipoDocto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getIdSync() {
        return idSync;
    }

    public void setIdSync(String idSync) {
        this.idSync = idSync;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
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

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public DoctoInvJava castToDoctoInvJava(DoctoInv doctoInv) throws SQLException{
	  this.setIdDocto(doctoInv.getIdDocto());
	  this.setIdTipoDocto(doctoInv.getIdTipoDocto());
	  this.setFecha(doctoInv.getFecha());
      this.setUsuario(doctoInv.getUsuario());
      this.setReferencia(doctoInv.getReferencia());
      this.setIdSync(doctoInv.getIdSync());
      this.setIdMod(doctoInv.getIdMod());
      this.setFechaMod(doctoInv.getFechaMod());
      this.setIdSucursal(doctoInv.getIdSucursal());
      this.setNotas(doctoInv.getNotas());
      this.setCantidad(doctoInv.getCantidad());
      this.setEstado(doctoInv.getEstado());
      this.setSistema(doctoInv.getSistema());
	  return this;
	}
	
	
}
