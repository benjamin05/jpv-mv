package mx.lux.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table( name = "tipo_contacto", schema = "public" )
public class TipoContacto implements Serializable {

    private static final long serialVersionUID = -8093676108930126263L;

    @Id
    @Column( name = "id_tipo_contacto" )
    private Integer id_tipo_contacto;

    @Column( name = "descripcion" )
    private String descripcion;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId_tipo_contacto() {
        return id_tipo_contacto;
    }

    public void setId_tipo_contacto(Integer id_tipo_contacto) {
        this.id_tipo_contacto = id_tipo_contacto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
