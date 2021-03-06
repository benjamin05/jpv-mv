package mx.lux.pos.java.repository;

import mx.lux.pos.java.querys.TransInvDetQuery;
import mx.lux.pos.model.TransInv;
import mx.lux.pos.model.TransInvDetalle;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransInvJava {

    Integer numReg;
	String idTipoTrans;
    Integer folio;
    Date fecha;
    Integer idSucursal;
    Integer idSucursalDestino;
	String referencia;
	String observaciones;
    String idEmpleado;
    Date fechaMod;
    List<TransInvDetJava> trDet = new ArrayList<TransInvDetJava>();

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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Integer getIdSucursalDestino() {
        return idSucursalDestino;
    }

    public void setIdSucursalDestino(Integer idSucursalDestino) {
        this.idSucursalDestino = idSucursalDestino;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Date getFechaMod() {
        return fechaMod;
    }

    public void setFechaMod(Date fechaMod) {
        this.fechaMod = fechaMod;
    }

    public List<TransInvDetJava> getTrDet() {
        return trDet;
    }

    public void setTrDet(List<TransInvDetJava> trDet) {
        this.trDet = trDet;
    }

    public TransInvJava mapeoTransInv(ResultSet rs) throws SQLException{
	  this.setNumReg(rs.getInt("num_reg"));
	  this.setIdTipoTrans(rs.getString("id_tipo_trans"));
      this.setFolio(rs.getInt("folio"));
      this.setFecha(rs.getDate("fecha"));
      this.setIdSucursal(rs.getInt("id_sucursal"));
      this.setIdSucursalDestino(rs.getInt("id_sucursal_destino"));
      this.setReferencia(rs.getString("referencia"));
      this.setObservaciones(rs.getString("observaciones"));
      this.setIdEmpleado(rs.getString("id_empleado"));
      this.setFechaMod(rs.getDate("fecha_mod"));
      this.setTrDet( detalles() );
	  return this;
	}


    private List<TransInvDetJava> detalles( ) {
      List<TransInvDetJava> lstDetalles = new ArrayList<TransInvDetJava>();
      lstDetalles = TransInvDetQuery.buscaTransInvDetPorIdTipoYFolio(StringUtils.trimToEmpty(idTipoTrans), folio);
      return lstDetalles;
    }


    public void add( TransInvDetJava pTrDet ) {
      pTrDet.setIdTipoTrans(this.idTipoTrans);
      pTrDet.setFolio(this.folio);
      this.trDet.add( pTrDet );
    }

    public TransInvJava castToTransInvJava(TransInv transInv) throws SQLException{
        this.setNumReg(transInv.getNumReg());
        this.setIdTipoTrans(transInv.getIdTipoTrans());
        this.setFolio(transInv.getFolio());
        this.setFecha(transInv.getFecha());
        this.setIdSucursal(transInv.getSucursal());
        this.setIdSucursalDestino(transInv.getSucursalDestino());
        this.setReferencia(transInv.getReferencia());
        this.setObservaciones(transInv.getObservaciones());
        this.setIdEmpleado(transInv.getIdEmpleado());
        this.setFechaMod(transInv.getFechaMod());
        this.setTrDet( detalles() );
        return this;
    }
}
