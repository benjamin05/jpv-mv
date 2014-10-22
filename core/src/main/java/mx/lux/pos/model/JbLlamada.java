package mx.lux.pos.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table( name = "jb_llamada", schema = "public" )
public class JbLlamada implements Serializable {

    @Column( name = "num_llamada" )
    private Integer num_llamada;

    @Id
    @Column( name = "rx" )
    private String rx;

    @Column( name = "fecha" )
    private Date fecha;

    @Column( name = "estado" )
    private String estado;

    @Column( name = "contesto" )
    private String contesto;

    @Column( name = "emp_atendio" )
    private String emp_atendio;

    @Column( name = "emp_llamo" )
    private String emp_llamo;

    @Column( name = "tipo" )
    private String tipo;

    @Column( name = "obs" )
    private String obs;

    @Column( name = "grupo" )
    private Boolean grupo;

    @Column( name = "id_mod" )
    private String id_mod;

    public Integer getNum_llamada() {
        return num_llamada;
    }

    public void setNum_llamada(Integer num_llamada) {
        this.num_llamada = num_llamada;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getContesto() {
        return contesto;
    }

    public void setContesto(String contesto) {
        this.contesto = contesto;
    }

    public String getEmp_atendio() {
        return emp_atendio;
    }

    public void setEmp_atendio(String emp_atendio) {
        this.emp_atendio = emp_atendio;
    }

    public String getEmp_llamo() {
        return emp_llamo;
    }

    public void setEmp_llamo(String emp_llamo) {
        this.emp_llamo = emp_llamo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public Boolean getGrupo() {
        return grupo;
    }

    public void setGrupo(Boolean grupo) {
        this.grupo = grupo;
    }

    public String getId_mod() {
        return id_mod;
    }

    public void setId_mod(String id_mod) {
        this.id_mod = id_mod;
    }
}
