package mx.lux.pos.repository;

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
	
	
	public String getRx() {
		return rx;
	}
	public void setRx(String rx) {
		this.rx = rx;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public String getEmp() {
		return emp;
	}
	public void setEmp(String emp) {
		this.emp = emp;
	}
	public String getIdViaje() {
		return idViaje;
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
		return idMod;
	}
	public void setIdModM(String idModM) {
		this.idMod = idModM;
	}
	public String getIdJbTrack() {
		return idJbTrack;
	}
	public void setIdJbTrack(String idJbTrack) {
		this.idJbTrack = idJbTrack;
	}
	
	public static JbTrack setValores( String rx, String estado, String obs, String emp,
			String idViaje, Date fecha, String idMod, String idJbTrack ){
		JbTrack jbTrack = new JbTrack();
		jbTrack.setRx(rx);
		jbTrack.setEstado(estado);
		jbTrack.setObs(obs);
		jbTrack.setEmp(emp);
		jbTrack.setIdViaje(idViaje);
		jbTrack.setFecha(fecha);
		jbTrack.setIdModM(idMod);
		jbTrack.setIdJbTrack(idJbTrack);
		return jbTrack;
	}
}
