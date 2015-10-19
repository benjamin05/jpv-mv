package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ImpHistorialJava {

    Integer idCliente;
	String idSucOri;
    Date fechaCompra;
    String factura;
	BigDecimal importe;
    String obs;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdSucOri() {
        return idSucOri;
    }

    public void setIdSucOri(String idSucOri) {
        this.idSucOri = idSucOri;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public ImpHistorialJava mapeoImpHistorial(ResultSet rs) throws SQLException{
	  this.setIdCliente(rs.getInt("id_cliente"));
	  this.setIdSucOri(rs.getString("id_suc_ori"));
      this.setFechaCompra(rs.getDate("fecha_compra"));
      this.setFactura(rs.getString("factura"));
      this.setImporte(Utilities.toBigDecimal(rs.getString("importe")));
      this.setObs(rs.getString("obs"));
	  return this;
	}
	
	
}
