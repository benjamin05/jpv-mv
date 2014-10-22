package mx.lux.pos.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table( name = "pedido_lc", schema = "public" )
public class PedidoLc implements Serializable {


    private static final long serialVersionUID = 6207186838948044417L;

    @Id
    @Column( name = "id_pedido" )
    private String id;

    @Column( name = "folio" )
    private String folio;

    @Column( name = "cliente" )
    private String cliente;

    @Column( name = "sucursal" )
    private String sucursal;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_alta" )
    private Date fechaAlta;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_acuse" )
    private Date fechaAcuse;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_recepcion" )
    private Date fechaRecepcion;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_entrega" )
    private Date fechaEntrega;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_envio" )
    private Date fechaEnvio;

    @OneToMany( fetch = FetchType.EAGER )
    @NotFound( action = NotFoundAction.IGNORE )
    @JoinColumn( name = "id_pedido", insertable = false, updatable = false )
    private Set<PedidoLcDet> pedidoLcDets = new HashSet<PedidoLcDet>();




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaAcuse() {
        return fechaAcuse;
    }

    public void setFechaAcuse(Date fechaAcuse) {
        this.fechaAcuse = fechaAcuse;
    }

    public Date getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Set<PedidoLcDet> getPedidoLcDets() {
        return pedidoLcDets;
    }

    public void setPedidoLcDets(Set<PedidoLcDet> pedidoLcDets) {
        this.pedidoLcDets = pedidoLcDets;
    }

    public void setLcDet( List<PedidoLcDet> trDet ) {
        this.pedidoLcDets = new HashSet<PedidoLcDet>(trDet);
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}
