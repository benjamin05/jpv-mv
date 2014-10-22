package mx.lux.pos.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BodegasDetalle {

    private Date fecha;
    private Date fechaPromesa;
    private String factura;
    private List <Articulo> lstArticulos;
    private String cliente;
    private String contacto;
    private BigDecimal venta;
    private BigDecimal saldo;


    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public List<Articulo> getLstArticulos() {
        return lstArticulos;
    }

    public void setLstArticulos(List<Articulo> lstArticulos) {
        this.lstArticulos = lstArticulos;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public BigDecimal getVenta() {
        return venta;
    }

    public void setVenta(BigDecimal venta) {
        this.venta = venta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Date getFechaPromesa() {
        return fechaPromesa;
    }

    public void setFechaPromesa(Date fechaPromesa) {
        this.fechaPromesa = fechaPromesa;
    }
}
