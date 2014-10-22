package mx.lux.pos.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CuponesMvDesc {

    private String cliente;
    private String telefono;
    private String facturaOri;
    private Date fechaVenta;
    private Date fechaEntrega;
    private BigDecimal montoCupon;
    private String tipoCupon;
    private Date vigencia;
    private String facturaDest;
    private Date fechaAplic;

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFacturaOri() {
        return facturaOri;
    }

    public void setFacturaOri(String facturaOri) {
        this.facturaOri = facturaOri;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public BigDecimal getMontoCupon() {
        return montoCupon;
    }

    public void setMontoCupon(BigDecimal montoCupon) {
        this.montoCupon = montoCupon;
    }

    public String getTipoCupon() {
        return tipoCupon;
    }

    public void setTipoCupon(String tipoCupon) {
        this.tipoCupon = tipoCupon;
    }

    public Date getVigencia() {
        return vigencia;
    }

    public void setVigencia(Date vigencia) {
        this.vigencia = vigencia;
    }

    public String getFacturaDest() {
        return facturaDest;
    }

    public void setFacturaDest(String facturaDest) {
        this.facturaDest = facturaDest;
    }

    public Date getFechaAplic() {
        return fechaAplic;
    }

    public void setFechaAplic(Date fechaAplic) {
        this.fechaAplic = fechaAplic;
    }
}
