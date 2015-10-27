package mx.lux.pos.java.repository;

import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class CotizaJava {

    Integer idCotiza;
    Integer idSucursal;
    Integer idCliente;
    String idEmpleado;
    Integer idReceta;
    Date fechaMod;
	String idFactura;
    Date fechaVenta;
	String nombre;
	String telefono;
    String observaciones;
    String udf1;
    String titulo;
    Date fechaCotizacion;

    public Integer getIdCotiza() {
        return idCotiza;
    }

    public void setIdCotiza(Integer idCotiza) {
        this.idCotiza = idCotiza;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Integer getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(Integer idReceta) {
        this.idReceta = idReceta;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFechaCotizacion() {
        return fechaCotizacion;
    }

    public void setFechaCotizacion(Date fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }

    public CotizaJava mapeoCotiza(ResultSet rs) throws SQLException{
	  this.setIdCotiza(rs.getInt("id_cotiza"));
	  this.setIdSucursal(rs.getInt("id_sucursal"));
	  this.setIdCliente(rs.getInt("id_cliente"));
      this.setIdEmpleado(rs.getString("id_empleado"));
      this.setIdReceta(rs.getInt("id_receta"));
      this.setFechaMod(rs.getDate("fecha_mod"));
      this.setIdFactura(rs.getString("id_factura"));
      this.setFechaVenta(rs.getDate("fecha_venta"));
      this.setNombre(rs.getString("nombre"));
      this.setTelefono(rs.getString("telefono"));
      this.setObservaciones(rs.getString("observaciones"));
      this.setUdf1(StringUtils.trimToEmpty(rs.getString("udf1")));
      this.setTitulo(rs.getString("titulo"));
      this.setFechaCotizacion(rs.getDate("fecha_cotizacion"));
	  return this;
	}
	
	
}
