package mx.lux.pos.repository;

import java.math.BigDecimal;
import java.util.Date;

public class TmpServiciosJava {

	Integer idServ;
	String idFactura;
	String idCliente;
	String cliente;
	String dejo;
	String instruccion;
	String emp;
	String servicio;
	String condicion;
	Date fechaProm;

    public Integer getIdServ() {
        return idServ;
    }

    public void setIdServ(Integer idServ) {
        this.idServ = idServ;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
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

    public Date getFechaProm() {
        return fechaProm;
    }

    public void setFechaProm(Date fechaProm) {
        this.fechaProm = fechaProm;
    }

    public TmpServiciosJava setValores( String idFactura, String idCliente, String cliente, String dejo, String instruccion,
            String emp, String servicio, String condicion, Date fechaProm ){

		TmpServiciosJava tmpServiciosJava = new TmpServiciosJava();
        this.setIdFactura(idFactura);
        this.setIdCliente(idCliente);
        this.setCliente(cliente);
        this.setDejo(dejo);
        this.setInstruccion(instruccion);
        this.setEmp(emp);
        this.setServicio(servicio);
        this.setCondicion(condicion);
        this.setFechaProm(fechaProm);

		return tmpServiciosJava;
	}
	
	
}
