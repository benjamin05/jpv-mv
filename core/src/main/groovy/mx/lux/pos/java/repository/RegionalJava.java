package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.EmpleadoQuery;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class RegionalJava {

    Integer id;
	String idEmpresa;
    String idEmpleado;
    String nombre;
	String credencial;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCredencial() {
        return credencial;
    }

    public void setCredencial(String credencial) {
        this.credencial = credencial;
    }

    public RegionalJava mapeoRegional(ResultSet rs) throws SQLException, ParseException {
	  this.setIdEmpresa(rs.getString("id_empresa"));
      this.setIdEmpleado(rs.getString("id_empleado"));
      this.setNombre(rs.getString("nombre"));
      this.setCredencial(rs.getString("credencial"));
      this.setId(rs.getInt("id"));
	  return this;
	}


    /*public EmpleadoJava empleado( ){
      EmpleadoJava empleadoJava = new EmpleadoJava();
      empleadoJava = EmpleadoQuery.buscaEmpPorIdEmpleado(idEmpleado);
      return empleadoJava;
    }*/
}
