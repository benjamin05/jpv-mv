package mx.lux.pos.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table( name = "jb_servicios", schema = "public" )
public class JbServicios implements Serializable {
    @Id
    @Column( name = "servicio" )
    private String servicio;

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }
}
