package mx.lux.pos.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table( name = "facturas_impuestos", schema = "public" )
public class FacturasImpuestos {

    @Id
    @Column( name = "id_factura" )
    private String idFactura;

    @Column( name = "id_impuesto" )
    private String idImpuesto;

    @Column( name = "rfc" )
    private String rfc;

    @Column( name = "id_sucursal" )
    private Integer idSucursal;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha" )
    private Date fecha;

    @Column( name = "id_mod" )
    private String id_mod;



    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(String idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getId_mod() {
        return id_mod;
    }

    public void setId_mod(String id_mod) {
        this.id_mod = id_mod;
    }
}
