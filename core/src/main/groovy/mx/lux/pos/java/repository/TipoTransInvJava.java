package mx.lux.pos.java.repository;

import mx.lux.pos.model.TipoMov;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoTransInvJava {

	String idTipoTrans;
	String descripcion;
	String tipoMov;
    Integer ultimoFolio;
    TipoMov tipoMovObj;

    public String getIdTipoTrans() {
        return idTipoTrans;
    }

    public void setIdTipoTrans(String idTipoTrans) {
        this.idTipoTrans = idTipoTrans;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoMov() {
        return tipoMov;
    }

    public void setTipoMov(String tipoMov) {
        this.tipoMov = tipoMov;
    }

    public Integer getUltimoFolio() {
        return ultimoFolio;
    }

    public void setUltimoFolio(Integer ultimoFolio) {
        this.ultimoFolio = ultimoFolio;
    }

    public TipoMov getTipoMovObj() {
        return TipoMov.parse( tipoMov );
    }

    public void setTipoMovObj(TipoMov tipoMovObj) {
        this.tipoMovObj = tipoMovObj;
    }

    public TipoTransInvJava mapeoTipoTransInv(ResultSet rs) throws SQLException{
	  this.setIdTipoTrans(rs.getString("id_tipo_trans"));
	  this.setDescripcion(rs.getString("descripcion"));
      this.setTipoMov(rs.getString("tipo_mov"));
      this.setUltimoFolio(rs.getInt("ultimo_folio"));
	  return this;
	}
	
	
}
