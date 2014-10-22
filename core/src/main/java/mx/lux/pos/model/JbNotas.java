package mx.lux.pos.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table( name = "jb_notas", schema = "public" )
public class JbNotas implements Serializable {


    @Id
    @Column( name = "id_nota" )
    private Integer id_nota;

    @Column( name = "id_cliente" )
    private String id_cliente;

    @Column( name = "cliente" )
    private String cliente;

    @Column( name = "dejo" )
    private String dejo;

    @Column( name = "instruccion" )
    private String instruccion;

    @Column( name = "emp" )
    private String emp;

    @Column( name = "servicio" )
    private String servicio;

    @Column( name = "condicion" )
    private String condicion;


    @Column( name = "fecha_prom" )
    private Date fecha_prom;

    @Column( name = "fecha_orden" )
    private Date fecha_orden;

    @Column( name = "fecha_mod" )
    private Date fecha_mod;

    @Column( name = "tipo_serv" )
    private String tipo_serv;

    @Column( name = "id_mod" )
    private String id_mod;

    public Integer getId_nota() {
        return id_nota;
    }

    public void setId_nota(Integer id_nota) {
        this.id_nota = id_nota;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDejo() {
        return dejo;
    }

    public void setDejo(String dejo) {
        this.dejo = dejo;
    }

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruccion) {
        this.instruccion = instruccion;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public Date getFecha_prom() {
        return fecha_prom;
    }

    public void setFecha_prom(Date fecha_prom) {
        this.fecha_prom = fecha_prom;
    }

    public Date getFecha_orden() {
        return fecha_orden;
    }

    public void setFecha_orden(Date fecha_orden) {
        this.fecha_orden = fecha_orden;
    }

    public Date getFecha_mod() {
        return fecha_mod;
    }

    public void setFecha_mod(Date fecha_mod) {
        this.fecha_mod = fecha_mod;
    }

    public String getTipo_serv() {
        return tipo_serv;
    }

    public void setTipo_serv(String tipo_serv) {
        this.tipo_serv = tipo_serv;
    }

    public String getId_mod() {
        return id_mod;
    }

    public void setId_mod(String id_mod) {
        this.id_mod = id_mod;
    }
}
