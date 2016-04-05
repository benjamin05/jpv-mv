package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.EmpleadoQuery;
import mx.lux.pos.java.querys.JbQuery;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class JbTrack {

	String rx;
	String estado;
	String obs;
	String emp;
	String idViaje;
	Date fecha;
	String idMod;
	String idJbTrack;
    JbJava jb;
	
	
	public String getRx() {
		return StringUtils.trimToEmpty(rx);
	}
	public void setRx(String rx) {
		this.rx = rx;
	}
	public String getEstado() {
		return StringUtils.trimToEmpty(estado);
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getObs() {
		return StringUtils.trimToEmpty(obs);
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public String getEmp() {
		return StringUtils.trimToEmpty(emp);
	}
	public void setEmp(String emp) {
		this.emp = emp;
	}
	public String getIdViaje() {
		return StringUtils.trimToEmpty(idViaje);
	}
	public void setIdViaje(String idViaje) {
		this.idViaje = idViaje;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getIdMod() {
		return StringUtils.trimToEmpty(idMod);
	}
	public void setIdModM(String idModM) {
		this.idMod = idModM;
	}
	public String getIdJbTrack() {
		return StringUtils.trimToEmpty(idJbTrack);
	}
	public void setIdJbTrack(String idJbTrack) {
		this.idJbTrack = idJbTrack;
	}

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public JbJava getJb() {
        return jb;
    }

    public void setJb(JbJava jb) {
        this.jb = jb;
    }

    public JbTrack setValores(String rx, String estado, String obs, String emp,
                              String idViaje, Date fecha, String idMod, String idJbTrack){
		this.setRx(rx);
        this.setEstado(estado);
        this.setObs(obs);
        this.setEmp(emp);
        this.setIdViaje(idViaje);
        this.setFecha(fecha);
        this.setIdModM(idMod);
        this.setIdJbTrack(idJbTrack);
        this.setJb( jbJava() );
		return this;
	}


    public JbJava jbJava(){
      JbJava jbJava = new JbJava();
      jbJava = JbQuery.buscarPorRx(this.rx);
      return jbJava;
    }


    public JbTrack mapeoJbTrack( ResultSet rs ) throws SQLException {
        this.setRx(rs.getString("rx"));
        this.setEstado(rs.getString("estado"));
        this.setObs(rs.getString("obs"));
        this.setEmp(rs.getString("emp"));
        this.setIdViaje(rs.getString("id_viaje"));
        this.setFecha(rs.getDate("fecha"));
        this.setIdModM(rs.getString("id_mod"));
        this.setIdJbTrack(rs.getString("id_jbtrack"));
        this.setJb( jbJava() );
        return this;
    }


}
