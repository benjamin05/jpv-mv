package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table( name = "cupon_mv", schema = "public" )
public class CuponMv implements Serializable {

    private static final long serialVersionUID = -7792611710156581037L;

    @Id
    @Column( name = "clave_descuento" )
    private String claveDescuento;

    @Column( name = "factura_origen" )
    private String facturaOrigen;

    @Column( name = "factura_destino" )
    private String facturaDestino;

    @Type( type = "mx.lux.pos.model.MoneyAdapter" )
    @Column( name = "monto_cupon" )
    private BigDecimal montoCupon;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_aplicacion" )
    private Date fechaAplicacion;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_vigencia" )
    private Date fechaVigencia;

    @ManyToOne
    @NotFound( action = NotFoundAction.IGNORE )
    @JoinColumn( name = "factura_origen", referencedColumnName = "factura", insertable = false, updatable = false )
    private NotaVenta notaVenta;



    public String getClaveDescuento() {
        return claveDescuento;
    }

    public void setClaveDescuento(String claveDescuento) {
        this.claveDescuento = claveDescuento;
    }

    public String getFacturaOrigen() {
        return facturaOrigen;
    }

    public void setFacturaOrigen(String facturaOrigen) {
        this.facturaOrigen = facturaOrigen;
    }

    public String getFacturaDestino() {
        return facturaDestino;
    }

    public void setFacturaDestino(String facturaDestino) {
        this.facturaDestino = facturaDestino;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public Date getFechaVigencia() {
        return fechaVigencia;
    }

    public void setFechaVigencia(Date fechaVigencia) {
        this.fechaVigencia = fechaVigencia;
    }

    public BigDecimal getMontoCupon() {
        return montoCupon;
    }

    public void setMontoCupon(BigDecimal montoCupon) {
        this.montoCupon = montoCupon;
    }

    public NotaVenta getNotaVenta() {
        return notaVenta;
    }

    public void setNotaVenta(NotaVenta notaVenta) {
        this.notaVenta = notaVenta;
    }
}
