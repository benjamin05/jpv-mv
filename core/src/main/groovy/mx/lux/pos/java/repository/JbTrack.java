package mx.lux.pos.java.repository;

import org.apache.commons.lang.StringUtils;

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
