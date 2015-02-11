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
    String nombre;
    String idBanco;
    String cuenta;
    String clabe;
    String correo;

    public DevTmp( Integer idPago ){
      this.idPago = idPago;
      monto = BigDecimal.ZERO;
      idMod = 0;
    }

    public void AcumulaDevoluciones( Devolucion devolucion ){
      String[] dataDev = devolucion.getDevEfectivo().split(",");
      idFactura = devolucion.getModificacion().getIdFactura();
      idFormaPago = devolucion.getIdFormaPago();
      monto = monto.add(devolucion.getMonto());
      idMod = devolucion.getIdMod();
      factura = devolucion.getModificacion().getNotaVenta().getFactura();
      idTerminal = devolucion.getPago().getIdTerminal();
      if(dataDev.length > 1){
        nombre = dataDev[0];
        idBanco = dataDev[1];
        cuenta = dataDev[2];
        clabe = dataDev[3];
        correo = dataDev[4];
      } else {
        nombre = "";
        idBanco = "";
        cuenta = "";
        clabe = "";
        correo = "";
      }
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(String idBanco) {
        this.idBanco = idBanco;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getClabe() {
        return clabe;
    }

    public void setClabe(String clabe) {
        this.clabe = clabe;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
