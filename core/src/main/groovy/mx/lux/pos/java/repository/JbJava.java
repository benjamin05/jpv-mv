package mx.lux.pos.java.repository;

import java.math.BigDecimal;
import java.util.Date;

public class JbJava {

	String rx;
	String estado;
	String idViaje;
	String caja;
	String idCliente;
	Integer roto;
	String empAtendio;
	Integer numLlamada;
	String material;
	String surte;
	BigDecimal saldo;
	String jbTipo;
	Date volverLlamar;
	Date fechaPromesa;
	Date fechaMod;
	String cliente;
	String idMod;
	String obsExt;
	String retAuto;
	Boolean noLlamar;
	String tipoVenta;
	Date fechaVenta;
	String idGrupo;
	Boolean noEnviar;
	String externo;
	
	
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
	public String getIdViaje() {
		return idViaje;
	}
	public void setIdViaje(String idViaje) {
		this.idViaje = idViaje;
	}
	public String getCaja() {
		return caja;
	}
	public void setCaja(String caja) {
		this.caja = caja;
	}
	public String getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}
	public Integer getRoto() {
		return roto;
	}
	public void setRoto(Integer roto) {
		this.roto = roto;
	}
	public String getEmpAtendio() {
		return empAtendio;
	}
	public void setEmpAtendio(String empAtendio) {
		this.empAtendio = empAtendio;
	}
	public Integer getNumLlamada() {
		return numLlamada;
	}
	public void setNumLlamada(Integer numLlamada) {
		this.numLlamada = numLlamada;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getSurte() {
		return surte;
	}
	public void setSurte(String surte) {
		this.surte = surte;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public String getJbTipo() {
		return jbTipo;
	}
	public void setJbTipo(String jbTipo) {
		this.jbTipo = jbTipo;
	}
	public Date getVolverLlamar() {
		return volverLlamar;
	}
	public void setVolverLlamar(Date volverLlamar) {
		this.volverLlamar = volverLlamar;
	}
	public Date getFechaPromesa() {
		return fechaPromesa;
	}
	public void setFechaPromesa(Date fechaPromesa) {
		this.fechaPromesa = fechaPromesa;
	}
	public Date getFechaMod() {
		return fechaMod;
	}
	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getIdMod() {
		return idMod;
	}
	public void setIdMod(String idMod) {
		this.idMod = idMod;
	}
	public String getObsExt() {
		return obsExt;
	}
	public void setObsExt(String obsExt) {
		this.obsExt = obsExt;
	}
	public String getRetAuto() {
		return retAuto;
	}
	public void setRetAuto(String retAuto) {
		this.retAuto = retAuto;
	}
	public Boolean getNoLlamar() {
		return noLlamar;
	}
	public void setNoLlamar(Boolean noLlamar) {
		this.noLlamar = noLlamar;
	}
	public String getTipoVenta() {
		return tipoVenta;
	}
	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}
	public Date getFechaVenta() {
		return fechaVenta;
	}
	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	public String getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(String idGrupo) {
		this.idGrupo = idGrupo;
	}
	public Boolean getNoEnviar() {
		return noEnviar;
	}
	public void setNoEnviar(Boolean noEnviar) {
		this.noEnviar = noEnviar;
	}
	public String getExterno() {
		return externo;
	}
	public void setExterno(String externo) {
		this.externo = externo;
	}
	
	public JbJava setValores( String rx, String estado, String idViaje, String caja, String idCliente, Integer roto,
			String empAtendio, Integer numLlamada, String material, String surte, BigDecimal saldo, String jbTipo,
			Date volverLlamar, Date fechaPromesa, Date fechaMod, String cliente, String idMod, String obsExt,
			String retAuto, Boolean noLlamar, String tipoVenta, Date fechaVenta, String idGrupo, Boolean noEnviar,
			String externo ){
		
		JbJava jb = new JbJava();
		this.setRx(rx);
		this.setEstado(estado);
		this.setIdViaje(idViaje);
		this.setCaja(caja);
		this.setIdCliente(idCliente);
		this.setRoto(roto);
		this.setEmpAtendio(empAtendio);
		this.setNumLlamada(numLlamada);
		this.setMaterial(material);
		this.setSurte(surte);
		this.setSaldo(saldo);
		this.setJbTipo(jbTipo);
		this.setVolverLlamar(volverLlamar);
		this.setFechaPromesa(fechaPromesa);
		this.setFechaMod(fechaMod);
		this.setCliente(cliente);
		this.setIdMod(idMod);
		this.setObsExt(obsExt);
		this.setRetAuto(retAuto);
		this.setNoLlamar(noLlamar);
		this.setTipoVenta(tipoVenta);
		this.setFechaVenta(fechaVenta);
		this.setIdGrupo(idGrupo);
		this.setNoEnviar(noEnviar);
		
		return jb;
	}
	
	
}
