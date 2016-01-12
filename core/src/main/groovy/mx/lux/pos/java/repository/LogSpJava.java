package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class LogSpJava {

	String idFactura;
    Boolean respuesta;
	Integer idArticulo;
	Date fechaLlamada;
    Date fechaRespuesta;

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public Boolean getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Boolean respuesta) {
        this.respuesta = respuesta;
    }

    public Integer getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }

    public Date getFechaLlamada() {
        return fechaLlamada;
    }

    public void setFechaLlamada(Date fechaLlamada) {
        this.fechaLlamada = fechaLlamada;
    }

    public Date getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(Date fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public LogSpJava mapeoLogSp(ResultSet rs) throws SQLException{
	  this.setIdFactura(rs.getString("id_factura"));
	  this.setRespuesta(Utilities.toBoolean(rs.getBoolean("respuesta")));
	  this.setIdArticulo(rs.getInt("id_articulo"));
      this.setFechaLlamada(rs.getDate("fecha_llamada"));
      this.setFechaRespuesta(rs.getDate("fecha_respuesta"));
	  return this;
	}
	
	
}
