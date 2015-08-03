package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TerminalJava {

    Integer idSuc;
	String idTerminal;
    Integer idBancoDep;
	String descripcion;
	String afiliacion;
    Boolean promocion;
    String numero;

    public Integer getIdSuc() {
        return idSuc;
    }

    public void setIdSuc(Integer idSuc) {
        this.idSuc = idSuc;
    }

    public String getIdTerminal() {
        return idTerminal;
    }

    public void setIdTerminal(String idTerminal) {
        this.idTerminal = idTerminal;
    }

    public Integer getIdBancoDep() {
        return idBancoDep;
    }

    public void setIdBancoDep(Integer idBancoDep) {
        this.idBancoDep = idBancoDep;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAfiliacion() {
        return afiliacion;
    }

    public void setAfiliacion(String afiliacion) {
        this.afiliacion = afiliacion;
    }

    public Boolean getPromocion() {
        return promocion;
    }

    public void setPromocion(Boolean promocion) {
        this.promocion = promocion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }


    public TerminalJava mapeoTerminal(ResultSet rs) throws SQLException{
	  TerminalJava terminal = new TerminalJava();
	  terminal.setIdSuc(rs.getInt("id_suc"));
	  terminal.setIdTerminal(rs.getString("id_terminal"));
	  terminal.setIdBancoDep(rs.getInt("id_banco_dep"));
      terminal.setDescripcion(rs.getString("descripcion"));
      terminal.setAfiliacion(rs.getString("afiliacion"));
      terminal.setPromocion(Utilities.toBoolean(rs.getBoolean("promocion")));
      terminal.setNumero(rs.getString("numero"));
	  return terminal;
	}
	
	
}
