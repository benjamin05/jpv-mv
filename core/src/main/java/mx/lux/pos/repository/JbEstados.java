package mx.lux.pos.repository;

public class JbEstados {

	String idEdo;
	String llamada;
	String descr;
	
	
	public String getIdEdo() {
		return idEdo;
	}
	public void setIdEdo(String idEdo) {
		this.idEdo = idEdo;
	}
	public String getLlamada() {
		return llamada;
	}
	public void setLlamada(String llamada) {
		this.llamada = llamada;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	public static JbEstados setValores( String idEstado, String llamada, String descripcion ){
		JbEstados jbEstados = new JbEstados();
		jbEstados.setIdEdo(idEstado);
		jbEstados.setLlamada(llamada);
		jbEstados.setDescr(descripcion);
		return jbEstados;
	}
}