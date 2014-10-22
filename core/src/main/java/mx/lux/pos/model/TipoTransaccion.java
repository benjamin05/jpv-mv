package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table( name = "tipo_transaccion", schema = "public" )
public class TipoTransaccion implements Serializable {


    private static final long serialVersionUID = 5586419670672017877L;

    @Id
    @Column( name = "tipo_transaccion" )
    private Integer tipoTransaccion;

    @Column( name = "descripcion" )
    private String descripcion;



}
