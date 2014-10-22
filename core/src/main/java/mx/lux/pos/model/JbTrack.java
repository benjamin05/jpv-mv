package mx.lux.pos.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table( name = "jb_track", schema = "public" )
public class JbTrack implements Serializable {


    @Id
    @GeneratedValue( strategy = GenerationType.AUTO, generator = "jbTrack_id_seq" )
    @SequenceGenerator( name = "jbTrack_id_seq", sequenceName = "jbTrack_id_seq" )
    @Column(name="id_jbtrack")
    private Integer id_jbtrack;

    @Column( name = "rx" )
    private String rx;

    @Column( name = "estado" )
    private String estado;

    @Column( name = "obs" )
    private String obs;

    @Column( name = "emp" )
    private String emp;

    @Column( name = "id_viaje" )
    private String id_viaje;

    @Column( name = "fecha" )
    private Date fecha;


    @Column( name = "id_mod" )
    private String id_mod;

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public String getId_viaje() {
        return id_viaje;
    }

    public void setId_viaje(String id_viaje) {
        this.id_viaje = id_viaje;
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

    public Integer getId_jbtrack() {
        return id_jbtrack;
    }

    public void setId_jbtrack(Integer id_jbtrack) {
        this.id_jbtrack = id_jbtrack;
    }
}
