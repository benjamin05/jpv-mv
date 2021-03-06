package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Parametros {

	String idParametro;
	String descr;
	String valor;
	
	
	public String getIdParametro() {
		return idParametro;
	}
	public void setIdParametro(String idParametro) {
		this.idParametro = idParametro;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public Parametros mapeoParametro(ResultSet rs) throws SQLException{
		this.setIdParametro(rs.getString("id_parametro"));
		this.setDescr(rs.getString("descr"));
		this.setValor(rs.getString("valor"));
		return this;
	}
	
	
}
