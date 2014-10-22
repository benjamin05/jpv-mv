package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class DevTmp implements Serializable {

    String idFactura;
    String idFormaPago;
    BigDecimal monto;
    Integer idMod;
    String factura;
    Integer idPago;
    String idTerminal;

    public DevTmp( Integer idPago ){
      this.idPago = idPago;
      monto = BigDecimal.ZERO;
      idMod = 0;
    }

    public void AcumulaDevoluciones( Devolucion devolucion ){
      idFactura = devolucion.getModificacion().getIdFactura();
      idFormaPago = devolucion.getIdFormaPago();
      monto = monto.add(devolucion.getMonto());
      idMod = devolucion.getIdMod();
      factura = devolucion.getModificacion().getNotaVenta().getFactura();
      idTerminal = devolucion.getPago().getIdTerminal();
    }


    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(String idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getIdMod() {
        return idMod;
    }

    public void setIdMod(Integer idMod) {
        this.idMod = idMod;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public String getIdTerminal() {
        return idTerminal;
    }

    public void setIdTerminal(String idTerminal) {
        this.idTerminal = idTerminal;
    }
}
