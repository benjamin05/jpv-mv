package mx.lux.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table( name = "banco_dev", schema = "public" )
public class BancoDev implements Serializable {


    private static final long serialVersionUID = 56218835777912073L;

    @Id
    @Column( name = "id" )
    private Integer id;

    @Column( name = "nombre" )
    private String nombre;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
