package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AutorizaMovJava {

	String idEmpleado;
    Date fecha;
    Date hora;
	Integer tipoTransaccion;
	String factura;
    String notas;
	
	
	public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public Integer getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(Integer tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public AutorizaMovJava mapeoAurotizaMov(ResultSet rs) throws SQLException{
	  this.setIdEmpleado(rs.getString("id_empleado"));
      this.setFecha(rs.getDate("fecha"));
      this.setHora(rs.getDate("hora"));
      this.setTipoTransaccion(rs.getInt("tipo_transaccion"));
      this.setFactura(rs.getString("factura"));
      this.setNotas(rs.getString("notas"));
	  return this;
	}
	
	
}
