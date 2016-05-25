package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RepoDetJava {

    String factura;
    Integer numOrden;
    String suc;
	String ojo;
    String tipo;
    String vOld;
    String vNew;
    String campo;
    Date fecha;


    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public Integer getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(Integer numOrden) {
        this.numOrden = numOrden;
    }

    public String getSuc() {
        return suc;
    }

    public void setSuc(String suc) {
        this.suc = suc;
    }

    public String getOjo() {
        return ojo;
    }

    public void setOjo(String ojo) {
        this.ojo = ojo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getvOld() {
        return vOld;
    }

    public void setvOld(String vOld) {
        this.vOld = vOld;
    }

    public String getvNew() {
        return vNew;
    }

    public void setvNew(String vNew) {
        this.vNew = vNew;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    public RepoDetJava mapeoRepoDet(ResultSet rs) throws SQLException{
	  this.setFactura(rs.getString("factura"));
      this.setNumOrden(rs.getInt("num_orden"));
      this.setSuc(rs.getString("suc"));
      this.setOjo(rs.getString("ojo"));
      this.setTipo(rs.getString("tipo"));
      this.setvOld(rs.getString("v_old"));
      this.setvNew(rs.getString("v_new"));
      this.setCampo(rs.getString("campo"));
      this.setFecha(rs.getTimestamp("fecha"));
	  return this;
	}
	
	
}
