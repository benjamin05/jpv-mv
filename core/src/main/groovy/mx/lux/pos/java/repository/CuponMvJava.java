package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class CuponMvJava {

	String claveDescuento;
	String facturaOrigen;
	String facturaDestino;
    Date fechaAplicacion;
    Date fechaVigencia;
    BigDecimal montoCupon;

    public String getClaveDescuento() {
        return claveDescuento;
    }

    public void setClaveDescuento(String claveDescuento) {
        this.claveDescuento = claveDescuento;
    }

    public String getFacturaOrigen() {
        return facturaOrigen;
    }

    public void setFacturaOrigen(String facturaOrigen) {
        this.facturaOrigen = facturaOrigen;
    }

    public String getFacturaDestino() {
        return facturaDestino;
    }

    public void setFacturaDestino(String facturaDestino) {
        this.facturaDestino = facturaDestino;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public Date getFechaVigencia() {
        return fechaVigencia;
    }

    public void setFechaVigencia(Date fechaVigencia) {
        this.fechaVigencia = fechaVigencia;
    }

    public BigDecimal getMontoCupon() {
        return montoCupon;
    }

    public void setMontoCupon(BigDecimal montoCupon) {
        this.montoCupon = montoCupon;
    }

    public CuponMvJava mapeoCuponMv(ResultSet rs) throws SQLException{
	  this.setClaveDescuento(rs.getString("clave_descuento"));
	  this.setFacturaOrigen(rs.getString("factura_origen"));
	  this.setFacturaDestino(rs.getString("factura_destino"));
      this.setFechaAplicacion(rs.getDate("fecha_aplicacion"));
      this.setFechaVigencia(rs.getDate("fecha_vigencia"));
      this.setMontoCupon(Utilities.toBigDecimal(rs.getString("monto_cupon")));
	  return this;
	}
	
	
}
