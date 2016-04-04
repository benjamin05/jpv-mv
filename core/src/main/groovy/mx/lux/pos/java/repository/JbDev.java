package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.querys.EmpleadoQuery;
import mx.lux.pos.java.querys.SucursalesQuery;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

public class JbDev {

	Integer idDev;
	String factura;
	String sucursal;
	String apartado;
	String idViaje;
	String documento;
	String arm;
	String col;
    Date fechaEnvio;
    Date fecha;
    String idMod;
    Boolean rx;
    Integer idSobre;
    SucursalesJava sucursales;


    public Integer getIdDev() {
        return idDev;
    }

    public void setIdDev(Integer idDev) {
        this.idDev = idDev;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getApartado() {
        return apartado;
    }

    public void setApartado(String apartado) {
        this.apartado = apartado;
    }

    public String getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(String idViaje) {
        this.idViaje = idViaje;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getArm() {
        return arm;
    }

    public void setArm(String arm) {
        this.arm = arm;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public Boolean getRx() {
        return rx;
    }

    public void setRx(Boolean rx) {
        this.rx = rx;
    }

    public Integer getIdSobre() {
        return idSobre;
    }

    public void setIdSobre(Integer idSobre) {
        this.idSobre = idSobre;
    }

    public SucursalesJava getSucursales() {
        return sucursales;
    }

    public void setSucursales(SucursalesJava sucursales) {
        this.sucursales = sucursales;
    }

    public JbDev mapeoJbDev(ResultSet rs) throws SQLException {
	  this.setIdDev(rs.getInt("id_dev"));
      this.setFactura(rs.getString("factura"));
      this.setSucursal(rs.getString("sucursal"));
      this.setApartado(rs.getString("apartado"));
      this.setIdViaje(rs.getString("id_viaje"));
      this.setDocumento(rs.getString("documento"));
      this.setArm(rs.getString("arm"));
      this.setCol(rs.getString("col"));
      this.setFechaEnvio(rs.getDate("fecha_envio"));
      this.setFecha(rs.getDate("fecha"));
      this.setIdMod(rs.getString("id_mod"));
      this.setRx(Utilities.toBoolean(rs.getBoolean("rx")));
      this.setIdSobre(rs.getInt("id_sobre"));
      this.setSucursales( sucursal() );
	  return this;
	}


    public SucursalesJava sucursal( ){
      SucursalesJava sucursalesJava = new SucursalesJava();
      Integer idSuc = 0;
      try{
        idSuc = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(sucursal)).intValue();
      } catch ( ParseException ex ){
        System.out.println(ex);
      }
      sucursalesJava = SucursalesQuery.BuscaSucursalPorIdSuc( idSuc );
      return sucursalesJava;
    }
}
