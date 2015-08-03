package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.OrdenPromDetQuery;
import mx.lux.pos.java.querys.PedidoLcQuery;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    List<PedidoLcDetJava> pedidoLcDets;

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

    public List<PedidoLcDetJava> getPedidoLcDets() {
        return pedidoLcDets;
    }

    public void setPedidoLcDets(List<PedidoLcDetJava> pedidoLcDets) {
        this.pedidoLcDets = pedidoLcDets;
    }

    public PedidoLcJava mapeoPedidoLc(ResultSet rs) throws SQLException, ParseException {
	  this.setIdPedido(rs.getString("id_pedido"));
      this.setFolio(rs.getString("folio"));
      this.setCliente(rs.getString("cliente"));
      this.setSucursal(rs.getString("sucursal"));
      this.setFechaAcuse(rs.getDate("fecha_alta"));
      this.setFechaAcuse(rs.getDate("fecha_acuse"));
      this.setFechaRecepcion(rs.getDate("fecha_recepcion"));
      this.setFechaEntrega(rs.getDate("fecha_entrega"));
      this.setFechaEnvio(rs.getDate("fecha_envio"));
      this.setPedidoLcDets( pedidoLcDet() );
	  return this;
	}


    private List<PedidoLcDetJava> pedidoLcDet( ) throws ParseException {
      List<PedidoLcDetJava> lstPedidoLcDet = new ArrayList<PedidoLcDetJava>();
      lstPedidoLcDet = PedidoLcQuery.buscaPedidoLcDetPorIdPedido( idPedido );
      return lstPedidoLcDet;
    }


}
