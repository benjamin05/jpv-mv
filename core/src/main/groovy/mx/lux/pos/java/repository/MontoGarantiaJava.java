package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MontoGarantiaJava {

    Integer id;
	BigDecimal montoGarantia;
	BigDecimal montoMinimo;
	BigDecimal montoMaximo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontoGarantia() {
        return montoGarantia;
    }

    public void setMontoGarantia(BigDecimal montoGarantia) {
        this.montoGarantia = montoGarantia;
    }

    public BigDecimal getMontoMinimo() {
        return montoMinimo;
    }

    public void setMontoMinimo(BigDecimal montoMinimo) {
        this.montoMinimo = montoMinimo;
    }

    public BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public MontoGarantiaJava mapeoMontoGarantia(ResultSet rs) throws SQLException{
	  this.setId(rs.getInt("id"));
	  this.setMontoGarantia(Utilities.toBigDecimal(rs.getString("monto_garantia")));
	  this.setMontoMinimo(Utilities.toBigDecimal(rs.getString("monto_minimo")));
      this.setMontoMaximo(Utilities.toBigDecimal(rs.getString("monto_maximo")));
	  return this;
	}
	
	
}
