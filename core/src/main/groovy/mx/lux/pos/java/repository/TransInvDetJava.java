package mx.lux.pos.java.repository;

import mx.lux.pos.model.TransInvDetalle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class TransInvDetJava {

    Integer numReg;
	String idTipoTrans;
    Integer folio;
    Integer linea;
    Integer sku;
	String tipoMov;
	Integer cantidad;

    public Integer getNumReg() {
        return numReg;
    }

    public void setNumReg(Integer numReg) {
        this.numReg = numReg;
    }

    public String getIdTipoTrans() {
        return idTipoTrans;
    }

    public void setIdTipoTrans(String idTipoTrans) {
        this.idTipoTrans = idTipoTrans;
    }

    public Integer getFolio() {
        return folio;
    }

    public void setFolio(Integer folio) {
        this.folio = folio;
    }

    public Integer getLinea() {
        return linea;
    }

    public void setLinea(Integer linea) {
        this.linea = linea;
    }

    public Integer getSku() {
        return sku;
    }

    public void setSku(Integer sku) {
        this.sku = sku;
    }

    public String getTipoMov() {
        return tipoMov;
    }

    public void setTipoMov(String tipoMov) {
        this.tipoMov = tipoMov;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public TransInvDetJava mapeoTransInvDet(ResultSet rs) throws SQLException{
	  this.setNumReg(rs.getInt("num_reg"));
	  this.setIdTipoTrans(rs.getString("id_tipo_trans"));
      this.setFolio(rs.getInt("folio"));
      this.setLinea(rs.getInt("linea"));
      this.setSku(rs.getInt("sku"));
      this.setTipoMov(rs.getString("tipo_mov"));
      this.setCantidad(rs.getInt("cantidad"));
	  return this;
	}


    public TransInvDetJava castToTransInvDetJava(TransInvDetalle transInvDetalle) throws SQLException{
        this.setNumReg(transInvDetalle.getNumReg());
        this.setIdTipoTrans(transInvDetalle.getIdTipoTrans());
        this.setFolio(transInvDetalle.getFolio());
        this.setLinea(transInvDetalle.getLinea());
        this.setSku(transInvDetalle.getSku());
        this.setTipoMov(transInvDetalle.getTipoMov());
        this.setCantidad(transInvDetalle.getCantidad());
        return this;
    }

}
