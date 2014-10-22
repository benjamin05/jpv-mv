package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table( name = "log_sp", schema = "public" )
public class LogSP implements Serializable {

    private static final long serialVersionUID = 7716018676070276681L;

    @Column( name = "id_factura" )
    private String idFactura;

    @Column( name = "respuesta" )
    private Boolean respuesta;

    @Column( name = "id_articulo" )
    private Integer idArticulo;

    @Id
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "fecha_llamada" )
    private Date fechaLlamada;


    @Column( name = "fecha_respuesta" )
    private Date fechaRespuesta;



    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public Boolean getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Boolean respuesta) {
        this.respuesta = respuesta;
    }

    public Integer getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }

    public Date getFechaLlamada() {
        return fechaLlamada;
    }

    public void setFechaLlamada(Date fechaLlamada) {
        this.fechaLlamada = fechaLlamada;
    }

    public Date getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(Date fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

}
