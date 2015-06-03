package mx.lux.pos.repository;

import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class EmpleadoJava {
	private String idEmpleado;
	private String nombreEmpleado;
	private String apPatEmpleado;
	private String apMatEmpleado;
	private Integer idPuesto;
	private String passwd;
	private String idSync;
	private Date fechaMod;
	private String idMod;
	private Integer idSucursal;
	private Integer idEmpresa;

    public String getIdEmpleado() {
		return this.idEmpleado;
	}

	public void setIdEmpleado( String idEmpleado ) {
		this.idEmpleado = idEmpleado;
	}

	public String getNombreEmpleado() {
		return this.nombreEmpleado;
	}

	public void setNombreEmpleado( String nombreEmpleado ) {
		this.nombreEmpleado = nombreEmpleado;
	}

	public String getApPatEmpleado() {
		return apPatEmpleado;
	}

	public void setApPatEmpleado( String apPatEmpleado ) {
		this.apPatEmpleado = apPatEmpleado;
	}

	public String getApMatEmpleado() {
		return this.apMatEmpleado;
	}

	public void setApMatEmpleado( String apMatEmpleado ) {
		this.apMatEmpleado = apMatEmpleado;
	}

	public Integer getIdPuesto() {
		return this.idPuesto;
	}

	public void setIdPuesto( Integer idPuesto ) {
		this.idPuesto = idPuesto;
	}

	public String getPasswd() {
		return this.passwd;
	}

	public void setPasswd( String passwd ) {
		this.passwd = passwd;
	}

	public String getIdSync() {
		return this.idSync;
	}

	public void setIdSync( String idSync ) {
		this.idSync = idSync;
	}

	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod( Date fechaMod ) {
		this.fechaMod = fechaMod;
	}

	public String getIdMod() {
		return this.idMod;
	}

	public void setIdMod( String idMod ) {
		this.idMod = idMod;
	}

	public Integer getIdEmpresa() {
		return this.idEmpresa;
	}

	public void setIdEmpresa( Integer idEmpresa ) {
		this.idEmpresa = idEmpresa;
	}
	
	public Integer getIdSucursal() {
		return idSucursal;
	}

	public void setIdSucursal(Integer idSucursal) {
		this.idSucursal = idSucursal;
	}

	public String getNombreApellidos() {
		return new StringBuilder( nombreEmpleado ).append( " " ).append( apPatEmpleado ).append( " " ).append( apMatEmpleado ).toString();
	}
	
	public EmpleadoJava mapeoEmpleado( ResultSet rs ){
		EmpleadoJava emp = new EmpleadoJava();
		try{
		emp.setIdEmpleado(rs.getString("id_empleado"));
        emp.setNombreEmpleado(rs.getString("nombre_empleado"));        
        emp.setApPatEmpleado(rs.getString("ap_pat_empleado"));        
        emp.setApMatEmpleado(rs.getString("ap_mat_empleado"));        
        emp.setIdPuesto(rs.getInt("id_puesto"));        
        emp.setPasswd(rs.getString("passwd"));        
        emp.setIdSync(rs.getString("id_sync"));        
        emp.setFechaMod(rs.getDate("fecha_mod"));        
        emp.setIdMod(rs.getString("id_mod"));        
        emp.setIdSucursal(rs.getInt("id_sucursal"));        
        emp.setIdEmpresa(rs.getInt("id_empresa"));
		} catch (SQLException err) {
            System.out.println( err );
        }
		return emp;
	}
	

    String getNombreCompleto(){
      return StringUtils.trimToEmpty(this.getNombreEmpleado())+" "+StringUtils.trimToEmpty(this.getApPatEmpleado())+" "+StringUtils.trimToEmpty(this.getApMatEmpleado());
    }
}
