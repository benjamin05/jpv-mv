package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class CierreDiarioJava {

    Date fecha;
	String estado;
    Date fechaBtn;
    Date horaCierre;
	String observaciones;
    BigDecimal ventaBruta;
    BigDecimal ventaNeta;
    BigDecimal cancelaciones;
    BigDecimal modificaciones;
    BigDecimal ingresoBruto;
    BigDecimal ingresoNeto;
    BigDecimal devoluciones;
    BigDecimal efectivoRecibido;
    BigDecimal efectivoExternos;
    BigDecimal efectivoDevoluciones;
    BigDecimal efectivoNeto;
    BigDecimal usdRecibido;
    BigDecimal usdDevoluciones;
    Integer ventasCantidad;
    Integer modificacionesCantidad;
    Integer cancelacionesCantidad;
	String facturaInicial;
    String facturaFinal;
    Boolean verificado;

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaBtn() {
        return fechaBtn;
    }

    public void setFechaBtn(Date fechaBtn) {
        this.fechaBtn = fechaBtn;
    }

    public Date getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(Date horaCierre) {
        this.horaCierre = horaCierre;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigDecimal getVentaBruta() {
        return ventaBruta;
    }

    public void setVentaBruta(BigDecimal ventaBruta) {
        this.ventaBruta = ventaBruta;
    }

    public BigDecimal getVentaNeta() {
        return ventaNeta;
    }

    public void setVentaNeta(BigDecimal ventaNeta) {
        this.ventaNeta = ventaNeta;
    }

    public BigDecimal getCancelaciones() {
        return cancelaciones;
    }

    public void setCancelaciones(BigDecimal cancelaciones) {
        this.cancelaciones = cancelaciones;
    }

    public BigDecimal getModificaciones() {
        return modificaciones;
    }

    public void setModificaciones(BigDecimal modificaciones) {
        this.modificaciones = modificaciones;
    }

    public BigDecimal getIngresoBruto() {
        return ingresoBruto;
    }

    public void setIngresoBruto(BigDecimal ingresoBruto) {
        this.ingresoBruto = ingresoBruto;
    }

    public BigDecimal getIngresoNeto() {
        return ingresoNeto;
    }

    public void setIngresoNeto(BigDecimal ingresoNeto) {
        this.ingresoNeto = ingresoNeto;
    }

    public BigDecimal getDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(BigDecimal devoluciones) {
        this.devoluciones = devoluciones;
    }

    public BigDecimal getEfectivoRecibido() {
        return efectivoRecibido;
    }

    public void setEfectivoRecibido(BigDecimal efectivoRecibido) {
        this.efectivoRecibido = efectivoRecibido;
    }

    public BigDecimal getEfectivoExternos() {
        return efectivoExternos;
    }

    public void setEfectivoExternos(BigDecimal efectivoExternos) {
        this.efectivoExternos = efectivoExternos;
    }

    public BigDecimal getEfectivoDevoluciones() {
        return efectivoDevoluciones;
    }

    public void setEfectivoDevoluciones(BigDecimal efectivoDevoluciones) {
        this.efectivoDevoluciones = efectivoDevoluciones;
    }

    public BigDecimal getEfectivoNeto() {
        return efectivoNeto;
    }

    public void setEfectivoNeto(BigDecimal efectivoNeto) {
        this.efectivoNeto = efectivoNeto;
    }

    public BigDecimal getUsdRecibido() {
        return usdRecibido;
    }

    public void setUsdRecibido(BigDecimal usdRecibido) {
        this.usdRecibido = usdRecibido;
    }

    public BigDecimal getUsdDevoluciones() {
        return usdDevoluciones;
    }

    public void setUsdDevoluciones(BigDecimal usdDevoluciones) {
        this.usdDevoluciones = usdDevoluciones;
    }

    public Integer getVentasCantidad() {
        return ventasCantidad;
    }

    public void setVentasCantidad(Integer ventasCantidad) {
        this.ventasCantidad = ventasCantidad;
    }

    public Integer getModificacionesCantidad() {
        return modificacionesCantidad;
    }

    public void setModificacionesCantidad(Integer modificacionesCantidad) {
        this.modificacionesCantidad = modificacionesCantidad;
    }

    public Integer getCancelacionesCantidad() {
        return cancelacionesCantidad;
    }

    public void setCancelacionesCantidad(Integer cancelacionesCantidad) {
        this.cancelacionesCantidad = cancelacionesCantidad;
    }

    public String getFacturaInicial() {
        return facturaInicial;
    }

    public void setFacturaInicial(String facturaInicial) {
        this.facturaInicial = facturaInicial;
    }

    public String getFacturaFinal() {
        return facturaFinal;
    }

    public void setFacturaFinal(String facturaFinal) {
        this.facturaFinal = facturaFinal;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    public CierreDiarioJava mapeoCierreDiario(ResultSet rs) throws SQLException{
	  this.setFecha(rs.getDate("fecha"));
	  this.setEstado(rs.getString("estado"));
	  this.setFechaBtn(rs.getDate("fecha_btn"));
      this.setHoraCierre(rs.getTime("hora_cierre"));
      this.setObservaciones(rs.getString("observaciones"));
      this.setVentaBruta(Utilities.toBigDecimal(rs.getString("venta_bruta")));
      this.setVentaNeta(Utilities.toBigDecimal(rs.getString("venta_neta")));
      this.setCancelaciones(Utilities.toBigDecimal(rs.getString("cancelaciones")));
      this.setModificaciones(Utilities.toBigDecimal(rs.getString("modificaciones")));
      this.setIngresoBruto(Utilities.toBigDecimal(rs.getString("ingreso_bruto")));
      this.setIngresoNeto(Utilities.toBigDecimal(rs.getString("ingreso_neto")));
      this.setDevoluciones(Utilities.toBigDecimal(rs.getString("devoluciones")));
      this.setEfectivoRecibido(Utilities.toBigDecimal(rs.getString("efectivo_recibido")));
      this.setEfectivoExternos(Utilities.toBigDecimal(rs.getString("efectivo_externos")));
      this.setEfectivoDevoluciones(Utilities.toBigDecimal(rs.getString("efectivo_devoluciones")));
      this.setEfectivoNeto(Utilities.toBigDecimal(rs.getString("efectivo_neto")));
      this.setUsdRecibido(Utilities.toBigDecimal(rs.getString("usd_recibido")));
      this.setUsdDevoluciones(Utilities.toBigDecimal(rs.getString("usd_devoluciones")));
      this.setVentasCantidad(rs.getInt("ventas_cantidad"));
      this.setModificacionesCantidad(rs.getInt("modificaciones_cantidad"));
      this.setCancelacionesCantidad(rs.getInt("cancelaciones_cantidad"));
      this.setFacturaInicial(StringUtils.trimToEmpty(rs.getString("factura_inicial")));
      this.setFacturaFinal(StringUtils.trimToEmpty(rs.getString("factura_final")));
      this.setVerificado(rs.getBoolean("verificado"));
	  return this;
	}
	
	
}
