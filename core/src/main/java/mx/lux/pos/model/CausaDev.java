package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table( name = "causa_dev", schema = "public" )
public class CausaDev implements Serializable {


    private static final long serialVersionUID = -2926488910998601336L;

    @Id
    @Column( name = "id_causa" )
    private Integer idCausa;

    @Column( name = "tipo_docto" )
    private String tipoDocto;

    @Column( name = "causa" )
    private String causa;



    public Integer getIdCausa() {
        return idCausa;
    }

    public void setIdCausa(Integer idCausa) {
        this.idCausa = idCausa;
    }

    public String getTipoDocto() {
        return tipoDocto;
    }

    public void setTipoDocto(String tipoDocto) {
        this.tipoDocto = tipoDocto;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }
}
