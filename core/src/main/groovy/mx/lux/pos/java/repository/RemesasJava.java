package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RemesasJava {

    Integer idRemesa;
	String idTipoDocto;
	String idDocto;
	String docto;
    String clave;
    String letra;
    String archivo;
    Integer articulos;
    String estado;
    String sistema;
    Date fechaMod;
    Date fechaRecibido;
    Date fechaCarga;

    public Integer getIdRemesa() {
        return idRemesa;
    }

    public void setIdRemesa(Integer idRemesa) {
        this.idRemesa = idRemesa;
    }

    public String getIdTipoDocto() {
        return idTipoDocto;
    }

    public void setIdTipoDocto(String idTipoDocto) {
        this.idTipoDocto = idTipoDocto;
    }

    public String getIdDocto() {
        return idDocto;
    }

    public void setIdDocto(String idDocto) {
        this.idDocto = idDocto;
    }

    public String getDocto() {
        return docto;
    }

    public void setDocto(String docto) {
        this.docto = docto;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public Integer getArticulos() {
        return articulos;
    }

    public void setArticulos(Integer articulos) {
        this.articulos = articulos;
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

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public Date getFechaRecibido() {
        return fechaRecibido;
    }

    public void setFechaRecibido(Date fechaRecibido) {
        this.fechaRecibido = fechaRecibido;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public RemesasJava mapeoRemesas(ResultSet rs) throws SQLException{
	  this.setIdRemesa(rs.getInt("id_remesa"));
	  this.setIdTipoDocto(rs.getString("id_tipo_docto"));
	  this.setIdDocto(rs.getString("id_docto"));
      this.setDocto(rs.getString("docto"));
      this.setClave(rs.getString("clave"));
      this.setLetra(rs.getString("letra"));
      this.setArchivo(rs.getString("archivo"));
      this.setArticulos(rs.getInt("articulos"));
      this.setEstado(rs.getString("estado"));
      this.setSistema(rs.getString("sistema"));
      this.setFechaMod(rs.getDate("fecha_mod"));
      this.setFechaRecibido(rs.getDate("fecha_recibido"));
      this.setFechaCarga(rs.getDate("fecha_carga"));
	  return this;
	}
	
	
}
