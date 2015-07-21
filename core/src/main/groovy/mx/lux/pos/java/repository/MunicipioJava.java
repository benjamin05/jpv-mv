package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.EstadoQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MunicipioJava {

	String idEstado;
	String idLocalidad;
	String nombre;
    String rango1;
    String rango2;
    String rango3;
    String rango4;
    Integer id;
    EstadoJava estado;

    public String getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    public String getIdLocalidad() {
        return idLocalidad;
    }

    public void setIdLocalidad(String idLocalidad) {
        this.idLocalidad = idLocalidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRango1() {
        return rango1;
    }

    public void setRango1(String rango1) {
        this.rango1 = rango1;
    }

    public String getRango2() {
        return rango2;
    }

    public void setRango2(String rango2) {
        this.rango2 = rango2;
    }

    public String getRango3() {
        return rango3;
    }

    public void setRango3(String rango3) {
        this.rango3 = rango3;
    }

    public String getRango4() {
        return rango4;
    }

    public void setRango4(String rango4) {
        this.rango4 = rango4;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EstadoJava getEstado() {
        return estado;
    }

    public void setEstado(EstadoJava estado) {
        this.estado = estado;
    }

    public MunicipioJava mapeoMunicipio(ResultSet rs) throws SQLException{
      this.setIdEstado(rs.getString("id_estado"));
	  this.setIdLocalidad(rs.getString("id_localidad"));
	  this.setNombre(rs.getString("nombre"));
      this.setRango1(rs.getString("rango1"));
      this.setRango2(rs.getString("rango2"));
      this.setRango3(rs.getString("rango3"));
      this.setRango4(rs.getString("rango4"));
      this.setId(rs.getInt("id"));
      this.setEstado( estadoJava() );
	  return this;
	}

    public EstadoJava estadoJava( ){
        EstadoJava estadoJava = new EstadoJava();
        estadoJava = EstadoQuery.BuscaEstadoPorIdEstado( idEstado );
        return estadoJava;
    }
}
