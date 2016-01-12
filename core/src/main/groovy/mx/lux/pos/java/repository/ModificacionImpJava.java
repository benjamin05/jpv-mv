package mx.lux.pos.java.repository;

import mx.lux.pos.java.Utilities;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ModificacionImpJava {

    Integer idMod;
	String statusOld;
	BigDecimal ventaOld;
    BigDecimal ventaNew;
	String tipoOld;
    String tipoNew;
    Integer pcOld;
    Integer pcNew;
    BigDecimal montoOld;
    BigDecimal montoNew;
    String empOld;
    String empNew;
    String convOld;
    String convNew;

    public Integer getIdMod() {
        return idMod;
    }

    public void setIdMod(Integer idMod) {
        this.idMod = idMod;
    }

    public String getStatusOld() {
        return statusOld;
    }

    public void setStatusOld(String statusOld) {
        this.statusOld = statusOld;
    }

    public BigDecimal getVentaOld() {
        return ventaOld;
    }

    public void setVentaOld(BigDecimal ventaOld) {
        this.ventaOld = ventaOld;
    }

    public BigDecimal getVentaNew() {
        return ventaNew;
    }

    public void setVentaNew(BigDecimal ventaNew) {
        this.ventaNew = ventaNew;
    }

    public String getTipoOld() {
        return tipoOld;
    }

    public void setTipoOld(String tipoOld) {
        this.tipoOld = tipoOld;
    }

    public String getTipoNew() {
        return tipoNew;
    }

    public void setTipoNew(String tipoNew) {
        this.tipoNew = tipoNew;
    }

    public Integer getPcOld() {
        return pcOld;
    }

    public void setPcOld(Integer pcOld) {
        this.pcOld = pcOld;
    }

    public Integer getPcNew() {
        return pcNew;
    }

    public void setPcNew(Integer pcNew) {
        this.pcNew = pcNew;
    }

    public BigDecimal getMontoOld() {
        return montoOld;
    }

    public void setMontoOld(BigDecimal montoOld) {
        this.montoOld = montoOld;
    }

    public BigDecimal getMontoNew() {
        return montoNew;
    }

    public void setMontoNew(BigDecimal montoNew) {
        this.montoNew = montoNew;
    }

    public String getEmpOld() {
        return empOld;
    }

    public void setEmpOld(String empOld) {
        this.empOld = empOld;
    }

    public String getEmpNew() {
        return empNew;
    }

    public void setEmpNew(String empNew) {
        this.empNew = empNew;
    }

    public String getConvOld() {
        return convOld;
    }

    public void setConvOld(String convOld) {
        this.convOld = convOld;
    }

    public String getConvNew() {
        return convNew;
    }

    public void setConvNew(String convNew) {
        this.convNew = convNew;
    }

    public ModificacionImpJava mapeoModificaionImp(ResultSet rs) throws SQLException{
	  this.setIdMod(rs.getInt("id_mod"));
	  this.setStatusOld(rs.getString("status_old"));
	  this.setVentaOld(Utilities.toBigDecimal(rs.getString("venta_old")));
      this.setVentaNew(Utilities.toBigDecimal(rs.getString("venta_new")));
      this.setTipoOld(rs.getString("tipo_old"));
      this.setTipoNew(rs.getString("tipo_new"));
      this.setPcOld(rs.getInt("pc_old"));
      this.setPcNew(rs.getInt("pc_new"));
      this.setMontoOld(Utilities.toBigDecimal(rs.getString("monto_old")));
      this.setMontoNew(Utilities.toBigDecimal(rs.getString("monto_new")));
      this.setEmpOld(rs.getString("emp_old"));
      this.setEmpNew(rs.getString("emp_new"));
      this.setConvOld(rs.getString("conv_old"));
      this.setConvNew(rs.getString("conv_new"));
	  return this;
	}
	
	
}
