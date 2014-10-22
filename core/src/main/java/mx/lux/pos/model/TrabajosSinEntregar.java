package mx.lux.pos.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrabajosSinEntregar {

    private String idEmpleado;
    private Date fecha;
    private String factura;
    private String idFactura;
    private BigDecimal monto;
    private BigDecimal saldo;

    private static final String TAG_CUPON = "C";

    /*public TrabajosSinEntregar(String idVendedor) {
        idEmpleado = idVendedor;

    }*/

   public void AcumulaPago( String idFactura,  BigDecimal monto, Date FechaPago ) {
        /*IngresoPorFactura ingreso = FindOrCreate( pagos, idFactura );
        ingreso.AcumulaPago( new BigDecimal(monto.doubleValue()), FechaPago );
        totalPagos = ( totalPagos.add( new BigDecimal(monto.doubleValue()) ) );*/
    }


    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

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

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
