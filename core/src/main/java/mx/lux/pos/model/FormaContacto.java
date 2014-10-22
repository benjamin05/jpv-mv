package mx.lux.pos.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Cacheable
@Table( name = "forma_contacto", schema = "public" )
public class FormaContacto implements Serializable {

    private static final long serialVersionUID = 3627038677660169174L;

    @Id
    @Column( name = "rx" )
    private String rx;

    @Column( name = "id_cliente" )
    private Integer id_cliente;

    @Column( name = "id_tipo_contacto" )
    private Integer id_tipo_contacto;

    @Column( name = "contacto" )
    private String contacto;

    @Column( name = "observaciones" )
    private String observaciones;

    @Column( name = "fecha_mod" )
    private Date fecha_mod;

    @Column( name = "id_sucursal" )
    private Integer id_sucursal;

    //private Boolean principal;


    @ManyToOne
    @NotFound( action = NotFoundAction.IGNORE )
    @JoinColumn( name = "id_tipo_contacto", insertable = false, updatable = false )
    private TipoContacto tipoContacto;



    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public Integer getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Integer getId_tipo_contacto() {
        return id_tipo_contacto;
    }

    public void setId_tipo_contacto(Integer id_tipo_contacto) {
        this.id_tipo_contacto = id_tipo_contacto;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFecha_mod() {
        return fecha_mod;
    }

    public void setFecha_mod(Date fecha_mod) {
        this.fecha_mod = fecha_mod;
    }

    public Integer getId_sucursal() {
        return id_sucursal;
    }

    public void setId_sucursal(Integer id_sucursal) {
        this.id_sucursal = id_sucursal;
    }


    public TipoContacto getTipoContacto() {
        return tipoContacto;
    }

    public void setTipoContacto(TipoContacto tipoContacto) {
        this.tipoContacto = tipoContacto;
    }

    /*public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }*/
}
