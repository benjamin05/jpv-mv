package mx.lux.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table( name = "acuses_tipo", schema = "public" )
public class AcusesTipo implements Serializable {


    @Id
    @Column( name = "id_tipo" )
    private String id_tipo;

    @Column( name = "pagina" )
    private String pagina;

    @Column( name = "descr" )
    private String descr;


    public String getId_tipo() {
        return id_tipo;
    }

    public void setId_tipo(String id_tipo) {
        this.id_tipo = id_tipo;
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
