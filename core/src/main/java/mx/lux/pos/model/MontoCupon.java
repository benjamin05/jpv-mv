package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table( name = "monto_cupon", schema = "public" )
public class MontoCupon implements Serializable {

    @Id
    @Column( name = "id" )
    private Integer id;

    @Column( name = "tipo" )
    private String tipo;

    @Column( name = "generico" )
    private String generico;

    @Type( type = "mx.lux.pos.model.MoneyAdapter" )
    @Column( name = "monto_minimo" )
    private BigDecimal montoMinimo;

    @Type( type = "mx.lux.pos.model.MoneyAdapter" )
    @Column( name = "monto_maximo" )
    private BigDecimal montoMaximo;

    @Type( type = "mx.lux.pos.model.MoneyAdapter" )
    @Column( name = "monto" )
    private BigDecimal monto;

    @Type( type = "mx.lux.pos.model.MoneyAdapter" )
    @Column( name = "monto_tercer_par" )
    private BigDecimal montoTercerPar;

    @Column( name = "subtipo" )
    private String subtipo;

    @Column( name = "cantidad" )
    private Integer cantidad;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getGenerico() {
        return generico;
    }

    public void setGenerico(String generico) {
        this.generico = generico;
    }

    public BigDecimal getMontoMinimo() {
        return montoMinimo;
    }

    public void setMontoMinimo(BigDecimal montoMinimo) {
        this.montoMinimo = montoMinimo;
    }

    public BigDecimal getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(BigDecimal montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getMontoTercerPar() {
        return montoTercerPar;
    }

    public void setMontoTercerPar(BigDecimal montoTercerPar) {
        this.montoTercerPar = montoTercerPar;
    }

    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
