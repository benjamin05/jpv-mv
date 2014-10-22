package mx.lux.pos.model;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

public class MultipagoDet {

    private String factura;
    private Date fechaVenta;
    private BigDecimal importe;
    private String articulos;
    private String formasPago;


    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getArticulos() {
        return articulos;
    }

    public void setArticulos(String articulos) {
        this.articulos = articulos;
    }

    public String getFormasPago() {
        return formasPago;
    }

    public void setFormasPago(String formasPago) {
        this.formasPago = formasPago;
    }
}