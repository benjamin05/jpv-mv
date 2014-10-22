package mx.lux.pos.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table( name = "tmp_servicios", schema = "public" )
public class TmpServicios implements Serializable {


    private static final long serialVersionUID = -2921740576181746900L;


    @Id
    @GeneratedValue( strategy = GenerationType.AUTO, generator = "tmp_serv_id_serv_seq" )
    @SequenceGenerator( name = "tmp_serv_id_serv_seq", sequenceName = "tmp_serv_id_serv_seq" )
    @Column( name = "id_serv" )
    private Integer id_serv;

    @Column( name = "id_factura" )
    private String id_factura;

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

    public Integer getId_serv() {
        return id_serv;
    }

    public void setId_serv(Integer id_serv) {
        this.id_serv = id_serv;
    }

    public String getId_factura() {
        return id_factura;
    }

    public void setId_factura(String id_factura) {
        this.id_factura = id_factura;
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
}
