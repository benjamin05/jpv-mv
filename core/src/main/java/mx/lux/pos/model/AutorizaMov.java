package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table( name = "autoriza_mov", schema = "public" )
public class AutorizaMov implements Serializable {


    private static final long serialVersionUID = 9176240650066320528L;


    @Column( name = "id_empleado" )
    private String idEmpleado;

    @Temporal( TemporalType.DATE )
    @Column( name = "fecha" )
    private Date fecha;

    @Id
    @Temporal( TemporalType.TIME )
    @Column( name = "hora" )
    private Date hora;

    @Column( name = "tipo_transaccion" )
    private Integer tipoTransaccion;

    @Column( name = "factura" )
    private String factura;

    @Column( name = "notas" )
    private String notas;


    @ManyToOne
    @NotFound( action = NotFoundAction.IGNORE )
    @JoinColumn( name = "tipo_transaccion", insertable = false, updatable = false )
    private TipoTransaccion tipoTrans;

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

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public Integer getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(Integer tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public TipoTransaccion getTipoTrans() {
        return tipoTrans;
    }

    public void setTipoTrans(TipoTransaccion tipoTrans) {
        this.tipoTrans = tipoTrans;
    }

}
