package mx.lux.pos.model;


import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table( name = "jb", schema = "public" )
public class Jb implements Serializable {


    @Id
    @Column( name = "rx" )
    private String rx;

    @Column( name = "estado" )
    private String estado;

    @Column( name = "id_viaje" )
    private String id_viaje;

    @Column( name = "caja" )
    private String caja;

    @Column( name = "id_cliente" )
    private String id_cliente;

    @Column( name = "roto" )
    private Integer roto;

    @Column( name = "emp_atendio" )
    private String emp_atendio;

    @Column( name = "num_llamada" )
    private Integer num_llamada;

    @Column( name = "material" )
    private String material;

    @Column( name = "surte" )
    private String surte;

    @Type( type = "mx.lux.pos.model.MoneyAdapter" )
    @Column( name = "saldo" )
    private BigDecimal saldo;

    @Column( name = "jb_tipo" )
    private String jb_tipo;

    @Column( name = "volver_llamar" )
    private Date volver_llamar;

    @Column( name = "fecha_promesa" )
    private Date fecha_promesa;

    @Column( name = "fecha_mod" )
    private Date fecha_mod;

    @Column( name = "cliente" )
    private String cliente;

    @Column( name = "id_mod" )
    private String id_mod;

    @Column( name = "obs_ext" )
    private String obs_ext;

    @Column( name = "ret_auto" )
    private String ret_auto;

    @Column( name = "no_llamar" )
    private Boolean no_llamar;

    @Column( name = "tipo_venta" )
    private String tipo_venta;

    @Column( name = "fecha_venta" )
    private Date fecha_venta;

    @Column( name = "id_grupo" )
    private String id_grupo;

    @Column( name = "no_enviar" )
    private Boolean no_enviar;

    @Column( name = "externo" )
    private String externo;

    @ManyToOne
    @NotFound( action = NotFoundAction.IGNORE )
    @JoinColumn( name = "rx", referencedColumnName = "factura",insertable = false, updatable = false )
    private NotaVenta notaVenta;

    public void setRx(String rx) {
        this.rx = rx;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setId_viaje(String id_viaje) {
        this.id_viaje = id_viaje;
    }

    public void setCaja(String caja) {
        this.caja = caja;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public void setRoto(Integer roto) {
        this.roto = roto;
    }

    public void setEmp_atendio(String emp_atendio) {
        this.emp_atendio = emp_atendio;
    }

    public void setNum_llamada(Integer num_llamada) {
        this.num_llamada = num_llamada;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setSurte(String surte) {
        this.surte = surte;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void setJb_tipo(String jb_tipo) {
        this.jb_tipo = jb_tipo;
    }

    public void setVolver_llamar(Date volver_llamar) {
        this.volver_llamar = volver_llamar;
    }

    public void setFecha_promesa(Date fecha_promesa) {
        this.fecha_promesa = fecha_promesa;
    }

    public void setFecha_mod(Date fecha_mod) {
        this.fecha_mod = fecha_mod;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setId_mod(String id_mod) {
        this.id_mod = id_mod;
    }

    public void setObs_ext(String obs_ext) {
        this.obs_ext = obs_ext;
    }

    public void setRet_auto(String ret_auto) {
        this.ret_auto = ret_auto;
    }

    public void setNo_llamar(Boolean no_llamar) {
        this.no_llamar = no_llamar;
    }

    public void setTipo_venta(String tipo_venta) {
        this.tipo_venta = tipo_venta;
    }

    public void setFecha_venta(Date fecha_venta) {
        this.fecha_venta = fecha_venta;
    }

    public void setId_grupo(String id_grupo) {
        this.id_grupo = id_grupo;
    }

    public void setNo_enviar(Boolean no_enviar) {
        this.no_enviar = no_enviar;
    }

    public void setExterno(String externo) {
        this.externo = externo;
    }

    public String getRx() {
        return rx;
    }

    public String getEstado() {
        return estado;
    }

    public String getId_viaje() {
        return id_viaje;
    }

    public String getCaja() {
        return caja;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public Integer getRoto() {
        return roto;
    }

    public String getEmp_atendio() {
        return emp_atendio;
    }

    public Integer getNum_llamada() {
        return num_llamada;
    }

    public String getMaterial() {
        return material;
    }

    public String getSurte() {
        return surte;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getJb_tipo() {
        return jb_tipo;
    }

    public Date getVolver_llamar() {
        return volver_llamar;
    }

    public Date getFecha_promesa() {
        return fecha_promesa;
    }

    public Date getFecha_mod() {
        return fecha_mod;
    }

    public String getCliente() {
        return cliente;
    }

    public String getId_mod() {
        return id_mod;
    }

    public String getObs_ext() {
        return obs_ext;
    }

    public String getRet_auto() {
        return ret_auto;
    }

    public Boolean getNo_llamar() {
        return no_llamar;
    }

    public String getTipo_venta() {
        return tipo_venta;
    }

    public Date getFecha_venta() {
        return fecha_venta;
    }

    public String getId_grupo() {
        return id_grupo;
    }

    public Boolean getNo_enviar() {
        return no_enviar;
    }

    public String getExterno() {
        return externo;
    }

    public NotaVenta getNotaVenta() {
        return notaVenta;
    }

    public void setNotaVenta(NotaVenta notaVenta) {
        this.notaVenta = notaVenta;
    }
}
