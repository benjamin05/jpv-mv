package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	public String getIdViaje() {
		return StringUtils.trimToEmpty(idViaje);
	}
	public void setIdViaje(String idViaje) {
		this.idViaje = idViaje;
	}
	public String getCaja() {
		return StringUtils.trimToEmpty(caja);
	}
	public void setCaja(String caja) {
		this.caja = caja;
	}
	public String getIdCliente() {
		return StringUtils.trimToEmpty(idCliente);
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
		return StringUtils.trimToEmpty(empAtendio);
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
		return StringUtils.trimToEmpty(material);
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getSurte() {
		return StringUtils.trimToEmpty(surte);
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
		return StringUtils.trimToEmpty(jbTipo);
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
		return StringUtils.trimToEmpty(cliente);
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public String getIdMod() {
		return StringUtils.trimToEmpty(idMod);
	}
	public void setIdMod(String idMod) {
		this.idMod = idMod;
	}
	public String getObsExt() {
		return StringUtils.trimToEmpty(obsExt);
	}
	public void setObsExt(String obsExt) {
		this.obsExt = obsExt;
	}
	public String getRetAuto() {
		return StringUtils.trimToEmpty(retAuto);
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
		return StringUtils.trimToEmpty(tipoVenta);
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
		return StringUtils.trimToEmpty(idGrupo);
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
		return StringUtils.trimToEmpty(externo);
	}
	public void setExterno(String externo) {
		this.externo = externo;
	}
	
	public JbJava setValores( ResultSet rs ) throws SQLException {
		this.setRx(rs.getString("rx"));
		this.setEstado(rs.getString("estado"));
		this.setIdViaje(rs.getString("id_viaje"));
		this.setCaja(rs.getString("caja"));
		this.setIdCliente(rs.getString("id_cliente"));
        this.setRoto(Utilities.toInteger(rs.getString("roto")));
		this.setEmpAtendio(rs.getString("emp_atendio"));
		this.setNumLlamada(rs.getInt("num_llamada"));
		this.setMaterial(rs.getString("material"));
		this.setSurte(rs.getString("surte"));
		this.setSaldo(Utilities.toBigDecimal(rs.getString("saldo")));
		this.setJbTipo(rs.getString("jb_tipo"));
		this.setVolverLlamar(rs.getDate("volver_llamar"));
		this.setFechaPromesa(rs.getDate("fecha_promesa"));
		this.setFechaMod(rs.getDate("fecha_mod"));
		this.setCliente(rs.getString("cliente"));
		this.setIdMod(rs.getString("id_mod"));
		this.setObsExt(rs.getString("obs_ext"));
		this.setRetAuto(rs.getString("ret_auto"));
		this.setNoLlamar(Utilities.toBoolean(rs.getBoolean("no_llamar")));
		this.setTipoVenta(rs.getString("tipo_venta"));
		this.setFechaVenta(rs.getDate("fecha_venta"));
		this.setIdGrupo(StringUtils.trimToEmpty(rs.getString("id_grupo")));
		this.setNoEnviar(Utilities.toBoolean(rs.getBoolean("no_enviar")));
        this.setExterno(rs.getString("externo"));
		
		return this;
	}
	
	
}
