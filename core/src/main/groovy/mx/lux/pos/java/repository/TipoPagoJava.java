package mx.lux.pos.java.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoPagoJava {

	String idPago;
	String descripcion;
	String tipoSoi;
    String tipoCon;
    String f1;
    String f2;
    String f3;
    String f4;
    String f5;

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoSoi() {
        return tipoSoi;
    }

    public void setTipoSoi(String tipoSoi) {
        this.tipoSoi = tipoSoi;
    }

    public String getTipoCon() {
        return tipoCon;
    }

    public void setTipoCon(String tipoCon) {
        this.tipoCon = tipoCon;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getF3() {
        return f3;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public String getF4() {
        return f4;
    }

    public void setF4(String f4) {
        this.f4 = f4;
    }

    public String getF5() {
        return f5;
    }

    public void setF5(String f5) {
        this.f5 = f5;
    }


    public TipoPagoJava mapeoTipoPago(ResultSet rs) throws SQLException{
		TipoPagoJava tipoPago = new TipoPagoJava();
		tipoPago.setIdPago(rs.getString("id_pago"));
		tipoPago.setTipoSoi(rs.getString("tipo_soi"));
		tipoPago.setTipoCon(rs.getString("tipo_con"));
        tipoPago.setF1(rs.getString("f1"));
        tipoPago.setF2(rs.getString("f2"));
        tipoPago.setF3(rs.getString("f3"));
        tipoPago.setF4(rs.getString("f4"));
        tipoPago.setF5(rs.getString("f5"));
		return tipoPago;
	}
	
	
}
