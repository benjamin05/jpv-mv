package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BancoEmisorJava {

	Integer idBancoEmi;
	String descripcion;
	String tipo;

    public Integer getIdBancoEmi() {
        return idBancoEmi;
    }

    public void setIdBancoEmi(Integer idBancoEmi) {
        this.idBancoEmi = idBancoEmi;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BancoEmisorJava mapeoBancoEmisor(ResultSet rs) throws SQLException{
		this.setIdBancoEmi(rs.getInt("id_banco_emi"));
		this.setDescripcion(rs.getString("descripcion"));
		this.setTipo(rs.getString("tipo"));
		return this;
	}
	
	
}
