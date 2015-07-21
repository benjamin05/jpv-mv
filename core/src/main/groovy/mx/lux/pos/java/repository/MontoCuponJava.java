package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MontoCuponJava {

	String tipo;
	String generico;
    BigDecimal montoMinimo;
    BigDecimal montoMaximo;
    BigDecimal monto;
    Integer id;
    BigDecimal montoTercerPar;
	String subtipo;
    Integer cantidad;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getGenerico() {
        return generico;
    }

    public void setGenerico(String generico) {
        this.generico = generico;
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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontoTercerPar() {
        return montoTercerPar;
    }

    public void setMontoTercerPar(BigDecimal montoTercerPar) {
        this.montoTercerPar = montoTercerPar;
    }

    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public MontoCuponJava mapeoMontoCupon(ResultSet rs) throws SQLException{
	  this.setTipo(rs.getString("tipo"));
	  this.setGenerico(rs.getString("generico"));
	  this.setMontoMinimo(Utilities.toBigDecimal(rs.getString("monto_minimo")));
      this.setMontoMaximo(Utilities.toBigDecimal(rs.getString("monto_maximo")));
      this.setMonto(Utilities.toBigDecimal(rs.getString("monto")));
      this.setId(rs.getInt("id"));
      this.setMontoTercerPar(Utilities.toBigDecimal(rs.getString("monto_tercer_par")));
      this.setSubtipo(rs.getString("subtipo"));
      this.setCantidad(rs.getInt("cantidad"));
	  return this;
	}
	
	
}
