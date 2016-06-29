package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.EmpleadoQuery;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class ChecadasJava {

    Integer id;
	String sucursal;
    Date fecha;
    Date hora;
	String empresa;
	String idEmpleado;
    EmpleadoJava empleado;
    String nombreEmp;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public EmpleadoJava getEmpleado() {
        return empleado;
    }

    public void setEmpleado(EmpleadoJava empleado) {
        this.empleado = empleado;
    }

    public String getNombreEmp() {
      RegionalJava emp = empleado();
      return "["+ StringUtils.trimToEmpty(idEmpleado)+"]"+StringUtils.trimToEmpty(emp != null ? emp.nombre : "");
    }

    public void setNombreEmp(String nombreEmp) {
        this.nombreEmp = nombreEmp;
    }



    public ChecadasJava mapeoChecadas(ResultSet rs) throws SQLException, ParseException {
	  this.setSucursal(rs.getString("sucursal"));
      this.setFecha(rs.getDate("fecha"));
      this.setHora(rs.getTime("hora"));
      this.setEmpresa(rs.getString("empresa"));
      this.setIdEmpleado(rs.getString("id_empleado"));
      this.setId(rs.getInt("id"));
      //this.setEmpleado(empleado());
      //this.setNombreEmp("["+ StringUtils.trimToEmpty(idEmpleado)+"]"+StringUtils.trimToEmpty(empleado.getNombreCompleto()));
	  return this;
	}


    public RegionalJava empleado( ){
      RegionalJava empleadoJava = new RegionalJava();
      empleadoJava = EmpleadoQuery.buscaRegionalPorIdEmpleado(idEmpleado);
      return empleadoJava;
    }
}
