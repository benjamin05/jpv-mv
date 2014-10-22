package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Cacheable
@Table( name = "funcionalidad", schema = "public" )
public class Funcionalidad implements Serializable {


    @Id
    @Column( name = "id" )
    private String id;

    @Column( name = "activo" )
    private boolean activo;

    @PostLoad
    protected void onPostLoad() {
        id = StringUtils.trimToEmpty( id );
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
