package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DescuentosClaveJava {

	String claveDescuento;
	BigDecimal porcenajeDescuento;
	String descripcionDescuento;
    String tipo;
    Boolean vigente;
    Boolean cupon;

    public String getClaveDescuento() {
        return claveDescuento;
    }

    public void setClaveDescuento(String claveDescuento) {
        this.claveDescuento = claveDescuento;
    }

    public BigDecimal getPorcenajeDescuento() {
        return porcenajeDescuento;
    }

    public void setPorcenajeDescuento(BigDecimal porcenajeDescuento) {
        this.porcenajeDescuento = porcenajeDescuento;
    }

    public String getDescripcionDescuento() {
        return descripcionDescuento;
    }

    public void setDescripcionDescuento(String descripcionDescuento) {
        this.descripcionDescuento = descripcionDescuento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getVigente() {
        return vigente;
    }

    public void setVigente(Boolean vigente) {
        this.vigente = vigente;
    }

    public Boolean getCupon() {
        return cupon;
    }

    public void setCupon(Boolean cupon) {
        this.cupon = cupon;
    }

    public DescuentosClaveJava mapeoDescuentosClave(ResultSet rs) throws SQLException{
	  this.setClaveDescuento(rs.getString("clave_descuento"));
      this.setPorcenajeDescuento(Utilities.toBigDecimal(rs.getString("porcenaje_descuento")));
      this.setDescripcionDescuento(rs.getString("descripcion_descuento"));
      this.setTipo(rs.getString("tipo"));
      this.setVigente(rs.getBoolean("vigente"));
      this.setCupon(rs.getBoolean("cupon"));
      return this;
	}
	
	
}
