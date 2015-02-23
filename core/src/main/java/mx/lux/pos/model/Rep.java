package mx.lux.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table( name = "rep", schema = "public" )
public class Rep implements Serializable {


    @Id
    @Column( name = "id_estado" )
    private String idEstado;

    @Column( name = "nombre" )
    private String nombre;

    @Column( name = "edo1" )
    private String edo1;

    @Column( name = "rango1" )
    private String rango1;

    @Column( name = "rango2" )
    private String rango2;



    public String getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEdo1() {
        return edo1;
    }

    public void setEdo1(String edo1) {
        this.edo1 = edo1;
    }

    public String getRango1() {
        return rango1;
    }

    public void setRango1(String rango1) {
        this.rango1 = rango1;
    }

    public String getRango2() {
        return rango2;
    }

    public void setRango2(String rango2) {
        this.rango2 = rango2;
    }
}
