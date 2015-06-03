package mx.lux.pos.repository;

import mx.lux.pos.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class OrdenPromDetJava {

    Integer idOrdenPromDet;
    Integer id;
	String idFactura;
	Integer idProm;
	Integer idSuc;
    Integer idArt;
    BigDecimal descuentoMonto;
    Double descuentoPorcentaje;
    Date fechaMod;

    public Integer getIdOrdenPromDet() {
        return idOrdenPromDet;
    }

    public void setIdOrdenPromDet(Integer idOrdenPromDet) {
        this.idOrdenPromDet = idOrdenPromDet;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public Integer getIdProm() {
        return idProm;
    }

    public void setIdProm(Integer idProm) {
        this.idProm = idProm;
    }

    public Integer getIdSuc() {
        return idSuc;
    }

    public void setIdSuc(Integer idSuc) {
        this.idSuc = idSuc;
    }

    public Integer getIdArt() {
        return idArt;
    }

    public void setIdArt(Integer idArt) {
        this.idArt = idArt;
    }

    public BigDecimal getDescuentoMonto() {
        return descuentoMonto;
    }

    public void setDescuentoMonto(BigDecimal descuentoMonto) {
        this.descuentoMonto = descuentoMonto;
    }

    public Double getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(Double descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public OrdenPromDetJava mapeoOrdenPromDet(ResultSet rs) throws SQLException{
	  OrdenPromDetJava ordenPromDet = new OrdenPromDetJava();
	  ordenPromDet.setIdOrdenPromDet(rs.getInt("id_orden_prom_det"));
	  ordenPromDet.setId(rs.getInt("id"));
      ordenPromDet.setIdFactura(rs.getString("id_factura"));
      ordenPromDet.setIdProm(rs.getInt("id_prom"));
      ordenPromDet.setIdSuc(rs.getInt("id_suc"));
      ordenPromDet.setIdArt(rs.getInt("id_art"));
      ordenPromDet.setDescuentoMonto(Utilities.toBigDecimal(rs.getString("descuento_monto")));
      ordenPromDet.setDescuentoPorcentaje( rs.getDouble("descuento_porcentaje"));
      ordenPromDet.setFechaMod( rs.getDate("fecha_mod"));
	  return ordenPromDet;
	}
	
	
}
