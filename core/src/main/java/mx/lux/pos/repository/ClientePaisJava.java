package mx.lux.pos.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ClientePaisJava {

    Integer idCliente;
	String ciudad;
	String pais;
	Date fecha;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public ClientePaisJava mapeoClientePais(ResultSet rs) throws SQLException{
		this.setIdCliente(rs.getInt("id_cliente"));
		this.setCiudad(rs.getString("ciudad"));
		this.setPais(rs.getString("pais"));
        this.setFecha(rs.getDate("fecha"));
		return this;
	}
	
	
}
