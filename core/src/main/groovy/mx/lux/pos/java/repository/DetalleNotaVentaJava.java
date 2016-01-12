package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.querys.ArticulosQuery;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class DetalleNotaVentaJava {

	String idFactura;
	Integer idArticulo;
	String idTipoDetalle;
	Double cantidadFac;
	BigDecimal precioUnitLista;
	BigDecimal precioUnitFinal;
	String idSync;
	Date fechaMod;
	String idMod;
	Integer idSucursal;
	String surte;
	String idRepVenta;
	BigDecimal precioCalcLista;
	BigDecimal precioCalcOferta;
	BigDecimal precioFactura;
	BigDecimal precioConv;
	Integer id;
    ArticulosJava articulo;

    public String getIdFactura() {
        return StringUtils.trimToEmpty(idFactura);
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public Integer getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getIdTipoDetalle() {
        return StringUtils.trimToEmpty(idTipoDetalle);
    }

    public void setIdTipoDetalle(String idTipoDetalle) {
        this.idTipoDetalle = idTipoDetalle;
    }

    public Double getCantidadFac() {
        return cantidadFac;
    }

    public void setCantidadFac(Double cantidadFac) {
        this.cantidadFac = cantidadFac;
    }

    public BigDecimal getPrecioUnitLista() {
        return precioUnitLista;
    }

    public void setPrecioUnitLista(BigDecimal precioUnitLista) {
        this.precioUnitLista = precioUnitLista;
    }

    public BigDecimal getPrecioUnitFinal() {
        return precioUnitFinal;
    }

    public void setPrecioUnitFinal(BigDecimal precioUnitFinal) {
        this.precioUnitFinal = precioUnitFinal;
    }

    public String getIdSync() {
        return StringUtils.trimToEmpty(idSync);
    }

    public void setIdSync(String idSync) {
        this.idSync = idSync;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getIdMod() {
        return StringUtils.trimToEmpty(idMod);
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getSurte() {
        return StringUtils.trimToEmpty(surte);
    }

    public void setSurte(String surte) {
        this.surte = surte;
    }

    public String getIdRepVenta() {
        return StringUtils.trimToEmpty(idRepVenta);
    }

    public void setIdRepVenta(String idRepVenta) {
        this.idRepVenta = idRepVenta;
    }

    public BigDecimal getPrecioCalcLista() {
        return precioCalcLista;
    }

    public void setPrecioCalcLista(BigDecimal precioCalcLista) {
        this.precioCalcLista = precioCalcLista;
    }

    public BigDecimal getPrecioCalcOferta() {
        return precioCalcOferta;
    }

    public void setPrecioCalcOferta(BigDecimal precioCalcOferta) {
        this.precioCalcOferta = precioCalcOferta;
    }

    public BigDecimal getPrecioFactura() {
        return precioFactura;
    }

    public void setPrecioFactura(BigDecimal precioFactura) {
        this.precioFactura = precioFactura;
    }

    public BigDecimal getPrecioConv() {
        return precioConv;
    }

    public void setPrecioConv(BigDecimal precioConv) {
        this.precioConv = precioConv;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArticulosJava getArticulo() {
        return articulo;
    }

    public void setArticulo(ArticulosJava articulo) {
        this.articulo = articulo;
    }

    public DetalleNotaVentaJava setValores( ResultSet rs ) throws SQLException, ParseException {
        this.setIdFactura(rs.getString("id_factura"));
		this.setIdArticulo(rs.getInt("id_articulo"));
		this.setIdTipoDetalle(rs.getString("id_tipo_detalle"));
		this.setCantidadFac(rs.getDouble("cantidad_fac"));
		this.setPrecioUnitLista(Utilities.toBigDecimal(rs.getString("precio_unit_lista")));
		this.setPrecioUnitFinal(Utilities.toBigDecimal(rs.getString("precio_unit_final")));
		this.setIdSync(rs.getString("id_sync"));
        this.setFechaMod(rs.getDate("fecha_mod"));
        this.setIdMod(rs.getString("id_mod"));
        this.setIdSucursal(rs.getInt("id_sucursal"));
        this.setSurte(rs.getString("surte"));
        this.setIdRepVenta(rs.getString("id_rep_venta"));
        this.setPrecioCalcLista(Utilities.toBigDecimal(rs.getString("precio_calc_lista")));
        this.setPrecioCalcOferta(Utilities.toBigDecimal(rs.getString("precio_calc_oferta")));
        this.setPrecioFactura(Utilities.toBigDecimal(rs.getString("precio_factura")));
        this.setPrecioConv(Utilities.toBigDecimal(rs.getString("precio_conv")));
        this.setId(rs.getInt("id"));
        this.setArticulo( articulo() );
		
		return this;
	}

    private ArticulosJava articulo( ) throws ParseException {
      ArticulosJava articulosJava = new ArticulosJava();
      articulosJava = ArticulosQuery.busquedaArticuloPorId( idArticulo );
      return articulosJava;
    }
}
