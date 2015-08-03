package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public TmpServiciosJava setValores( ResultSet rs) throws SQLException {
	  this.setIdServ(rs.getInt("id_serv"));
      this.setIdFactura(rs.getString("id_factura"));
      this.setIdCliente(rs.getString("id_cliente"));
      this.setCliente(rs.getString("cliente"));
      this.setDejo(rs.getString("dejo"));
      this.setInstruccion(rs.getString("instruccion"));
      this.setEmp(rs.getString("emp"));
      this.setServicio(rs.getString("servicio"));
      this.setCondicion(rs.getString("condicion"));
      this.setFechaProm(rs.getDate("fecha_prom"));
      return this;
	}
	
	
}
