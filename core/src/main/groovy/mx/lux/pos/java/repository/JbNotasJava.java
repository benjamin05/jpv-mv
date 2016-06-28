package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class JbNotasJava {

	Integer idNota;
	String idCliente;
    String cliente;
    String dejo;
    String instruccion;
    String emp;
    String servicio;
    String condicion;
    Date fechaProm;
    Date fechaOrden;
    Date fechaMod;
    String tipoServ;
    String idMod;




    public Integer getIdNota() {
        return idNota;
    }

    public void setIdNota(Integer idNota) {
        this.idNota = idNota;
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

    public Date getFechaOrden() {
        return fechaOrden;
    }

    public void setFechaOrden(Date fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getTipoServ() {
        return tipoServ;
    }

    public void setTipoServ(String tipoServ) {
        this.tipoServ = tipoServ;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public JbNotasJava mapeoJbNotas( ResultSet rs ) throws SQLException, ParseException {
      this.setIdNota(rs.getInt("id_nota"));
      this.setIdCliente(rs.getString("id_cliente"));
      this.setCliente(rs.getString("cliente"));
      this.setDejo(rs.getString("dejo"));
      this.setInstruccion(rs.getString("instruccion"));
      this.setEmp(rs.getString("emp"));
      this.setServicio(rs.getString("servicio"));
      this.setCondicion(rs.getString("condicion"));
      this.setFechaProm(rs.getDate("fecha_prom"));
      this.setFechaOrden(rs.getDate("fecha_orden"));
      this.setFechaMod(rs.getDate("fecha_mod"));
      this.setTipoServ(rs.getString("tipo_serv"));
      this.setIdMod(rs.getString("id_mod"));
      return this;
    }


}
