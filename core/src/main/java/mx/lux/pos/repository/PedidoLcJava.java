package mx.lux.pos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class PedidoLcJava {

	String idPedido;
	String folio;
	String cliente;
    String sucursal;
    Date fechaAlta;
    Date fechaAcuse;
    Date fechaRecepcion;
    Date fechaEntrega;
    Date fechaEnvio;

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaAcuse() {
        return fechaAcuse;
    }

    public void setFechaAcuse(Date fechaAcuse) {
        this.fechaAcuse = fechaAcuse;
    }

    public Date getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(Date fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public PedidoLcJava mapeoPedidoLc(ResultSet rs) throws SQLException{
	  this.setIdPedido(rs.getString("id_pedido"));
      this.setFolio(rs.getString("folio"));
      this.setCliente(rs.getString("cliente"));
      this.setSucursal(rs.getString("sucursal"));
      this.setFechaAcuse(rs.getDate("fecha_alta"));
      this.setFechaAcuse(rs.getDate("fecha_acuse"));
      this.setFechaRecepcion(rs.getDate("fecha_recepcion"));
      this.setFechaEntrega(rs.getDate("fecha_entrega"));
      this.setFechaEnvio(rs.getDate("fecha_envio"));
	  return this;
	}
	
	
}
