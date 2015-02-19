package mx.lux.pos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table( name = "ciudades", schema = "public" )
public class Ciudades implements Serializable {


    @Id
    @Column( name = "nombre" )
    private String nombre;

    @Column( name = "estado" )
    private String estado;

    @Column( name = "ciudad" )
    private String ciudad;

    @Column( name = "rango1" )
    private String rango1;

    @Column( name = "rango2" )
    private String rango2;

    @Column( name = "rango3" )
    private String rango3;

    @Column( name = "rango4" )
    private String rango4;




    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
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

    public String getRango3() {
        return rango3;
    }

    public void setRango3(String rango3) {
        this.rango3 = rango3;
    }

    public String getRango4() {
        return rango4;
    }

    public void setRango4(String rango4) {
        this.rango4 = rango4;
    }
}
