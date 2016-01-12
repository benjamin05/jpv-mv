package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class FacturasImpuestosJava {

	String idFactura;
	String idImpuesto;
	String rfc;
    Integer idSucursal;
    Date fecha;
    String idMod;

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(String idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
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

    public FacturasImpuestosJava mapeoParametro(ResultSet rs) throws SQLException{
	  this.setIdFactura(rs.getString("id_factura"));
	  this.setIdImpuesto(rs.getString("id_impuesto"));
	  this.setRfc(rs.getString("rfc"));
      this.setIdSucursal(rs.getInt("id_sucursal"));
      this.setFecha(rs.getDate("fecha"));
      this.setIdMod(rs.getString("id_mod"));
	  return this;
	}
	
	
}
