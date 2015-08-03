package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DevolucionJava {

    Integer idMod;
    Integer idPago;
	String idFormaPago;
    Integer idBanco;
	String referenciaDevolucion;
    BigDecimal montoDevolucion;
    Date fechaDevolucion;
    String transf;
    String tipo;
    Integer id;
    String devEfectivo;

    public Integer getIdMod() {
        return idMod;
    }

    public void setIdMod(Integer idMod) {
        this.idMod = idMod;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public String getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(String idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public Integer getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(Integer idBanco) {
        this.idBanco = idBanco;
    }

    public String getReferenciaDevolucion() {
        return referenciaDevolucion;
    }

    public void setReferenciaDevolucion(String referenciaDevolucion) {
        this.referenciaDevolucion = referenciaDevolucion;
    }

    public BigDecimal getMontoDevolucion() {
        return montoDevolucion;
    }

    public void setMontoDevolucion(BigDecimal montoDevolucion) {
        this.montoDevolucion = montoDevolucion;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getTransf() {
        return transf;
    }

    public void setTransf(String transf) {
        this.transf = transf;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDevEfectivo() {
        return devEfectivo;
    }

    public void setDevEfectivo(String devEfectivo) {
        this.devEfectivo = devEfectivo;
    }

    public DevolucionJava mapeoDevolucion(ResultSet rs) throws SQLException{
	  this.setIdMod(rs.getInt("id_mod"));
	  this.setIdPago(rs.getInt("id_pago"));
	  this.setIdFormaPago(rs.getString("id_forma_pago"));
      this.setIdBanco(rs.getInt("id_banco"));
      this.setReferenciaDevolucion(rs.getString("referencia_devolucion"));
      this.setMontoDevolucion(Utilities.toBigDecimal(rs.getString("monto_devolucion")));
      this.setFechaDevolucion(rs.getDate("fecha_devolucion"));
      this.setTransf(rs.getString("transf"));
      this.setTipo(rs.getString("tipo"));
      this.setId(rs.getInt("id"));
      this.setDevEfectivo(rs.getString("dev_efectivo"));
	  return this;
	}
	
	
}
