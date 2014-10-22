package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table( name = "clientes_proceso", schema = "public" )
public class ClienteProceso implements Comparable<ClienteProceso> {

    @Id
    @Column( name = "id_cliente" )
    private Integer idCliente;

    @Column( name = "etapa", length = 10 )
    private String etapa;

    @Column( name = "id_sync", length = 1 )
    private String idSync;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_mod" )
    private Date fechaMod;

    @Column( name = "id_mod", length = 13 )
    private String idMod;

    @Column( name = "id_sucursal" )
    private Integer idSucursal;

    @Transient
    private Cliente cliente;

    @Transient
    private Collection<NotaVenta> notaVentas;

    public ClienteProceso() {
        this.setIdSync( "1" );
    }

    // Properties
    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente( Integer idCliente ) {
        this.idCliente = idCliente;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa( String etapa ) {
        this.etapa = ClienteProcesoEtapa.parse( etapa ).toString();
    }

    public String getIdSync() {
        return idSync;
    }

    public void setIdSync( String idSync ) {
        this.idSync = idSync;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod( Date fechaMod ) {
        this.fechaMod = fechaMod;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod( String idMod ) {
        this.idMod = StringUtils.trimToEmpty( idMod ).toUpperCase();
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal( Integer idSucursal ) {
        this.idSucursal = idSucursal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente( Cliente cliente ) {
        this.cliente = cliente;
    }

    public Collection<NotaVenta> getNotaVentas() {
        return notaVentas;
    }

    public void setNotaVentas( Collection<NotaVenta> notaVentas ) {
        this.notaVentas = notaVentas;
    }

    // Data Management
    @PrePersist
    public void onPersist() {
        this.setFechaMod( new Date() );
    }

    @PreUpdate
    public void onUpdate() {
        this.setFechaMod( new Date() );
    }

    // Identity
    public int compareTo(  ClienteProceso pCliente ) {
        return this.getIdCliente().compareTo( pCliente.getIdCliente() );
    }

    public boolean equals( Object pObj ) {
        boolean result = false;
        if ( this == pObj ) {
            result = true;
        } else if ( pObj instanceof ClienteProceso ) {
            if ( this.getIdCliente() != null ) {
                result = this.getIdCliente().equals( ( ( ClienteProceso ) pObj ).getIdCliente() );
            }
        }
        return result;
    }

    public int hashCode() {
        return this.getIdCliente().hashCode();
    }

    public String toString() {
        return String.format( "(%d) Etapa: %s", this.getIdCliente(), this.getEtapa() );
    }
}
