package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ExamenJava {

    Integer idExamen;
    Integer idCliente;
	String idAtendio;
	String avSaOdLejosEx;
	String avSaOiLejosEx;
    String objOdEsfEx;
    String objOdCilEx;
    String objOdEjeEx;
    String objOiEsfEx;
    String objOiCilEx;
    String objOiEjeEx;
    String objDiEx;
    String subOdEsfEx;
    String subOdCilEx;
    String subOdEjeEx;
    String subOdAdcEx;
    String subOdAdiEx;
    String subOdAvEx;
    String subOiEsfEx;
    String subOiCilEx;
    String subOiEjeEx;
    String subOiAdcEx;
    String subOiAdiEx;
    String subOiAvEx;
    String observacionesEx;
    String idSync;
    Date fechaMod;
    String idMod;
    Integer idSucursal;
    String diOd;
    String diOi;
    String udf1;
    String udf2;
    String udf3;
    String factura;
    String tipoCli;
    String tipoOft;
    Date fechaAlta;
    Integer idOftalmologo;
    Date horaAlta;
    String idExOri;

    public Integer getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(Integer idExamen) {
        this.idExamen = idExamen;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdAtendio() {
        return idAtendio;
    }

    public void setIdAtendio(String idAtendio) {
        this.idAtendio = idAtendio;
    }

    public String getAvSaOdLejosEx() {
        return avSaOdLejosEx;
    }

    public void setAvSaOdLejosEx(String avSaOdLejosEx) {
        this.avSaOdLejosEx = avSaOdLejosEx;
    }

    public String getAvSaOiLejosEx() {
        return avSaOiLejosEx;
    }

    public void setAvSaOiLejosEx(String avSaOiLejosEx) {
        this.avSaOiLejosEx = avSaOiLejosEx;
    }

    public String getObjOdEsfEx() {
        return objOdEsfEx;
    }

    public void setObjOdEsfEx(String objOdEsfEx) {
        this.objOdEsfEx = objOdEsfEx;
    }

    public String getObjOdCilEx() {
        return objOdCilEx;
    }

    public void setObjOdCilEx(String objOdCilEx) {
        this.objOdCilEx = objOdCilEx;
    }

    public String getObjOdEjeEx() {
        return objOdEjeEx;
    }

    public void setObjOdEjeEx(String objOdEjeEx) {
        this.objOdEjeEx = objOdEjeEx;
    }

    public String getObjOiEsfEx() {
        return objOiEsfEx;
    }

    public void setObjOiEsfEx(String objOiEsfEx) {
        this.objOiEsfEx = objOiEsfEx;
    }

    public String getObjOiCilEx() {
        return objOiCilEx;
    }

    public void setObjOiCilEx(String objOiCilEx) {
        this.objOiCilEx = objOiCilEx;
    }

    public String getObjOiEjeEx() {
        return objOiEjeEx;
    }

    public void setObjOiEjeEx(String objOiEjeEx) {
        this.objOiEjeEx = objOiEjeEx;
    }

    public String getObjDiEx() {
        return objDiEx;
    }

    public void setObjDiEx(String objDiEx) {
        this.objDiEx = objDiEx;
    }

    public String getSubOdEsfEx() {
        return subOdEsfEx;
    }

    public void setSubOdEsfEx(String subOdEsfEx) {
        this.subOdEsfEx = subOdEsfEx;
    }

    public String getSubOdCilEx() {
        return subOdCilEx;
    }

    public void setSubOdCilEx(String subOdCilEx) {
        this.subOdCilEx = subOdCilEx;
    }

    public String getSubOdEjeEx() {
        return subOdEjeEx;
    }

    public void setSubOdEjeEx(String subOdEjeEx) {
        this.subOdEjeEx = subOdEjeEx;
    }

    public String getSubOdAdcEx() {
        return subOdAdcEx;
    }

    public void setSubOdAdcEx(String subOdAdcEx) {
        this.subOdAdcEx = subOdAdcEx;
    }

    public String getSubOdAdiEx() {
        return subOdAdiEx;
    }

    public void setSubOdAdiEx(String subOdAdiEx) {
        this.subOdAdiEx = subOdAdiEx;
    }

    public String getSubOdAvEx() {
        return subOdAvEx;
    }

    public void setSubOdAvEx(String subOdAvEx) {
        this.subOdAvEx = subOdAvEx;
    }

    public String getSubOiEsfEx() {
        return subOiEsfEx;
    }

    public void setSubOiEsfEx(String subOiEsfEx) {
        this.subOiEsfEx = subOiEsfEx;
    }

    public String getSubOiCilEx() {
        return subOiCilEx;
    }

    public void setSubOiCilEx(String subOiCilEx) {
        this.subOiCilEx = subOiCilEx;
    }

    public String getSubOiEjeEx() {
        return subOiEjeEx;
    }

    public void setSubOiEjeEx(String subOiEjeEx) {
        this.subOiEjeEx = subOiEjeEx;
    }

    public String getSubOiAdcEx() {
        return subOiAdcEx;
    }

    public void setSubOiAdcEx(String subOiAdcEx) {
        this.subOiAdcEx = subOiAdcEx;
    }

    public String getSubOiAdiEx() {
        return subOiAdiEx;
    }

    public void setSubOiAdiEx(String subOiAdiEx) {
        this.subOiAdiEx = subOiAdiEx;
    }

    public String getSubOiAvEx() {
        return subOiAvEx;
    }

    public void setSubOiAvEx(String subOiAvEx) {
        this.subOiAvEx = subOiAvEx;
    }

    public String getObservacionesEx() {
        return observacionesEx;
    }

    public void setObservacionesEx(String observacionesEx) {
        this.observacionesEx = observacionesEx;
    }

    public String getIdSync() {
        return idSync;
    }

    public void setIdSync(String idSync) {
        this.idSync = idSync;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public String getIdMod() {
        return idMod;
    }

    public void setIdMod(String idMod) {
        this.idMod = idMod;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getDiOd() {
        return diOd;
    }

    public void setDiOd(String diOd) {
        this.diOd = diOd;
    }

    public String getDiOi() {
        return diOi;
    }

    public void setDiOi(String diOi) {
        this.diOi = diOi;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf3() {
        return udf3;
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getTipoCli() {
        return tipoCli;
    }

    public void setTipoCli(String tipoCli) {
        this.tipoCli = tipoCli;
    }

    public String getTipoOft() {
        return tipoOft;
    }

    public void setTipoOft(String tipoOft) {
        this.tipoOft = tipoOft;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Integer getIdOftalmologo() {
        return idOftalmologo;
    }

    public void setIdOftalmologo(Integer idOftalmologo) {
        this.idOftalmologo = idOftalmologo;
    }

    public Date getHoraAlta() {
        return horaAlta;
    }

    public void setHoraAlta(Date horaAlta) {
        this.horaAlta = horaAlta;
    }

    public String getIdExOri() {
        return idExOri;
    }

    public void setIdExOri(String idExOri) {
        this.idExOri = idExOri;
    }

    public ExamenJava mapeoParametro(ResultSet rs) throws SQLException{
	  this.setIdExamen(rs.getInt("id_examen"));
	  this.setIdCliente(rs.getInt("id_cliente"));
	  this.setIdAtendio(rs.getString("id_atendio"));
      this.setAvSaOdLejosEx(rs.getString("av_sa_od_lejos_ex"));
      this.setAvSaOiLejosEx(rs.getString("av_sa_oi_lejos_ex"));
      this.setObjOdEsfEx(rs.getString("obj_od_esf_ex"));
      this.setObjOdCilEx(rs.getString("obj_od_cil_ex"));
      this.setObjOdEjeEx(rs.getString("obj_od_eje_ex"));
      this.setObjOiEsfEx(rs.getString("obj_oi_esf_ex"));
      this.setObjOiCilEx(rs.getString("obj_oi_cil_ex"));
      this.setObjOiEjeEx(rs.getString("obj_oi_eje_ex"));
      this.setObjDiEx(rs.getString("obj_di_ex"));
      this.setSubOdEsfEx(rs.getString("sub_od_esf_ex"));
      this.setSubOdCilEx(rs.getString("sub_od_cil_ex"));
      this.setSubOdEjeEx(rs.getString("sub_od_eje_ex"));
      this.setSubOdAdcEx(rs.getString("sub_od_adc_ex"));
      this.setSubOdAdiEx(rs.getString("sub_od_adi_ex"));
      this.setSubOdAvEx(rs.getString("sub_od_av_ex"));
      this.setSubOiEsfEx(rs.getString("sub_oi_esf_ex"));
      this.setSubOiCilEx(rs.getString("sub_oi_cil_ex"));
      this.setSubOiEjeEx(rs.getString("sub_oi_eje_ex"));
      this.setSubOiAdcEx(rs.getString("sub_oi_adc_ex"));
      this.setSubOiAdiEx(rs.getString("sub_oi_adi_ex"));
      this.setSubOiAvEx(rs.getString("sub_oi_av_ex"));
      this.setObservacionesEx(rs.getString("observaciones_ex"));
      this.setIdSync(rs.getString("id_sync"));
      this.setFechaMod(rs.getDate("fecha_mod"));
      this.setIdMod(rs.getString("id_mod"));
      this.setIdSucursal(rs.getInt("id_sucursal"));
      this.setDiOd(rs.getString("di_od"));
      this.setDiOi(rs.getString("di_oi"));
      this.setUdf1(rs.getString("udf1"));
      this.setUdf2(rs.getString("udf2"));
      this.setUdf3(rs.getString("udf3"));
      this.setFactura(rs.getString("factura"));
      this.setTipoCli(rs.getString("tipo_cli"));
      this.setTipoOft(rs.getString("tipo_oft"));
      this.setFechaAlta(rs.getDate("fecha_alta"));
      this.setIdOftalmologo(rs.getInt("id_oftalmologo"));
      this.setHoraAlta(rs.getDate("hora_alta"));
      this.setIdExOri(rs.getString("id_ex_ori"));
	  return this;
	}
	
	
}
