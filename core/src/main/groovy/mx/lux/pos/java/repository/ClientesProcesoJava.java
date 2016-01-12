package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

public class ClientesProcesoJava {

    Integer idCliente;
	String etapa;
	String idSync;
    Date fechaMod;
	String idMod;
    Integer idSucursal;
    Collection<NotaVentaJava> notaVentas;
    ClientesJava cliente;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getIdSync() {
        return idSync;
    }

    public void setIdSync(String idSync) {
        this.idSync = idSync;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Collection<NotaVentaJava> getNotaVentas() {
        return notaVentas;
    }

    public void setNotaVentas(Collection<NotaVentaJava> notaVentas) {
        this.notaVentas = notaVentas;
    }

    public ClientesJava getCliente() {
        return cliente;
    }

    public void setCliente(ClientesJava cliente) {
        this.cliente = cliente;
    }

    public ClientesProcesoJava mapeoClientesProceso(ResultSet rs) throws SQLException{
	  this.setIdCliente(rs.getInt("id_cliente"));
      this.setEtapa(rs.getString("etapa"));
      this.setIdSync(rs.getString("id_sync"));
      this.setFechaMod(rs.getDate("fecha_mod"));
      this.setIdMod(rs.getString("id_mod"));
      this.setIdSucursal(rs.getInt("id_sucursal"));
	  return this;
	}
	
	
}
