package mx.lux.pos.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table( name = "pedido_lc_det", schema = "public" )
public class PedidoLcDet implements Serializable {


    private static final long serialVersionUID = -2155738677717472733L;

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO, generator = "pedido_lc_det_id_seq" )
    @SequenceGenerator( name = "pedido_lc_det_id_seq", sequenceName = "pedido_lc_det_id_seq" )
    @Column( name = "num_reg" )
    private Integer numReg;

    @Column( name = "id_pedido" )
    private String id;

    @Column( name = "curva_base" )
    private String curvaBase;

    @Column( name = "diametro" )
    private String diametro;

    @Column( name = "esfera" )
    private String esfera;

    @Column( name = "cilindro" )
    private String cilindro;

    @Column( name = "modelo" )
    private String modelo;

    @Column( name = "eje" )
    private String eje;

    @Column( name = "color" )
    private String color;

    @Column( name = "cantidad" )
    private Integer cantidad;

    /*@ManyToOne
    @NotFound( action = NotFoundAction.IGNORE )
    @JoinColumn( name = "id_pedido", insertable = false, updatable = false )
    private PedidoLc pedidoLc ;*/


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurvaBase() {
        return curvaBase;
    }

    public void setCurvaBase(String curvaBase) {
        this.curvaBase = curvaBase;
    }

    public String getDiametro() {
        return diametro;
    }

    public void setDiametro(String diametro) {
        this.diametro = diametro;
    }

    public String getEsfera() {
        return esfera;
    }

    public void setEsfera(String esfera) {
        this.esfera = esfera;
    }

    public String getCilindro() {
        return cilindro;
    }

    public void setCilindro(String cilindro) {
        this.cilindro = cilindro;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getEje() {
        return eje;
    }

    public void setEje(String eje) {
        this.eje = eje;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getNumReg() {
        return numReg;
    }

    public void setNumReg(Integer numReg) {
        this.numReg = numReg;
    }

    /*public PedidoLc getPedidoLc() {
        return pedidoLc;
    }

    public void setPedidoLc(PedidoLc pedidoLc) {
        this.pedidoLc = pedidoLc;
    }*/
}
