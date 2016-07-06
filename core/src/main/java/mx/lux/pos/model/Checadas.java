package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table( name = "checadas", schema = "public" )
public class Checadas implements Serializable {

    private static final long serialVersionUID = 4883571090212555946L;


    @Id
    @Column( name = "id" )
    private Integer id;

    @Column( name = "sucursal" )
    private String sucursal;

    @Column( name = "fecha" )
    private Date fecha;

    @Temporal( TemporalType.TIME )
    @Column( name = "hora" )
    private Date hora;

    @Column( name = "id_empleado" )
    private String idEmpleado;




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
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

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}
