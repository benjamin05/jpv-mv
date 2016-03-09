package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table( name = "log_asigna_subgerente", schema = "public" )
public class LogAsignaSubgerente implements Serializable {

    private static final long serialVersionUID = -7973413533888666366L;

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO, generator = "log_id_asigna_subgerente_seq" )
    @SequenceGenerator( name = "log_id_asigna_subgerente_seq", sequenceName = "log_id_asigna_subgerente_seq" )
    @Column( name = "id_log" )
    private Integer id;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha" )
    private Date fecha;

    @Column( name = "empleado_asigno" )
    private String empleadoAsigno;

    @Column( name = "empleado_asignado" )
    private String empleadoAsignado;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_inicial" )
    private Date fechaInicial;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_final" )
    private Date fechaFinal;

    @Column( name = "horas" )
    private Integer horas;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEmpleadoAsigno() {
        return empleadoAsigno;
    }

    public void setEmpleadoAsigno(String empleadoAsigno) {
        this.empleadoAsigno = empleadoAsigno;
    }

    public String getEmpleadoAsignado() {
        return empleadoAsignado;
    }

    public void setEmpleadoAsignado(String empleadoAsignado) {
        this.empleadoAsignado = empleadoAsignado;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Integer getHoras() {
        return horas;
    }

    public void setHoras(Integer horas) {
        this.horas = horas;
    }
}
