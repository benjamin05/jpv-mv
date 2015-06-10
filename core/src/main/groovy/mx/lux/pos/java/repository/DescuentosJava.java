package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DescuentosJava {

	String idFactura;
	String clave;
	String porcentaje;
    String idEmpleado;
    String idTipoD;
    Date fecha;
    String tipoClave;
    Integer id;

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getIdTipoD() {
        return idTipoD;
    }

    public void setIdTipoD(String idTipoD) {
        this.idTipoD = idTipoD;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipoClave() {
        return tipoClave;
    }

    public void setTipoClave(String tipoClave) {
        this.tipoClave = tipoClave;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DescuentosJava mapeoDescuentos(ResultSet rs) throws SQLException{
	  this.setIdFactura(rs.getString("id_factura"));
      this.setClave(rs.getString("clave"));
      this.setPorcentaje(rs.getString("porcentaje"));
      this.setIdEmpleado(rs.getString("id_empleado"));
      this.setIdTipoD(rs.getString("id_tipo_d"));
      this.setFecha(rs.getDate("fecha"));
      this.setTipoClave(rs.getString("tipo_clave"));
      this.setId(rs.getInt("id"));
	  return this;
	}
	
	
}
