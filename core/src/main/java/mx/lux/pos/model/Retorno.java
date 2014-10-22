package mx.lux.pos.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table( name = "retorno", schema = "public" )
public class Retorno implements Serializable {


    private static final long serialVersionUID = 9190794480951873710L;

    @Id
    @Column( name = "id_transaccion" )
    private Integer id;

    @Column( name = "ticket_origen" )
    private String ticketOrigen;

    @Column( name = "ticket_destino" )
    private String ticketDestino;

    @Type( type = "mx.lux.pos.model.MoneyAdapter" )
    @Column( name = "monto" )
    private BigDecimal monto;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTicketOrigen() {
        return ticketOrigen;
    }

    public void setTicketOrigen(String ticketOrigen) {
        this.ticketOrigen = ticketOrigen;
    }

    public String getTicketDestino() {
        return ticketDestino;
    }

    public void setTicketDestino(String ticketDestino) {
        this.ticketDestino = ticketDestino;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
