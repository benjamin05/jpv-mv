package mx.lux.pos.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table( name = "modelo_lc", schema = "public" )
public class ModeloLc implements Serializable {


    private static final long serialVersionUID = 4132887581104671569L;

    @Id
    @Column( name = "id_modelo" )
    private String id;

    @Column( name = "modelo" )
    private String modelo;

    @Column( name = "curva" )
    private String curva;

    @Column( name = "diametro" )
    private String diametro;

    @Column( name = "esfera" )
    private String esfera;

    @Column( name = "cilindro" )
    private String cilindro;

    @Column( name = "eje" )
    private String eje;

    @Column( name = "color" )
    private String color;

    @Column( name = "id_proveedor" )
    private Integer idProveedor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCurva() {
        return curva;
    }

    public void setCurva(String curva) {
        this.curva = curva;
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

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
}
