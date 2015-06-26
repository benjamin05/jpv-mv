package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.model.Jb;
import mx.lux.pos.java.repository.PagoJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PagoQuery {

	private static ResultSet rs;
    private static Statement stmt;
    private Jb jb;


    public static PagoJava busquedaPagosPorId(Integer idPago) throws ParseException{
      PagoJava pago = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if( idPago != null ){
          sql = String.format("SELECT * FROM pagos WHERE id_pago = %d;", idPago);
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            PagoJava pagoJava = new PagoJava();
            pagoJava.setValores( rs );
          }
          con.close();
        } else {
          System.out.println( "No existen el idPago: "+idPago );
        }
      } catch (SQLException err) {
            System.out.println( err );
      }
      return pago;
    }



	public static List<PagoJava> busquedaPagosPorIdFactura(String idFactura) throws ParseException{
      List<PagoJava> lstPagos = new ArrayList<PagoJava>();

      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(idFactura).length() > 0){
          sql = String.format("SELECT * FROM pagos WHERE id_factura = '%s';", StringUtils.trimToEmpty(idFactura));
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            PagoJava pagoJava = new PagoJava();
            pagoJava.setValores( rs );
            lstPagos.add(pagoJava);
          }
          con.close();
        } else {
          System.out.println( "No existen la nota: "+idFactura );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstPagos;
	}


    public static List<PagoJava> busquedaPagosPorIdFPagoAndIdFactura(String idFPago, String idFactura) throws ParseException{
      List<PagoJava> lstPagos = new ArrayList<PagoJava>();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(idFactura).length() > 0 && StringUtils.trimToEmpty(idFPago).length() > 0){
          sql = String.format("SELECT * FROM pagos WHERE id_f_pago = '%s' AND id_factura = '%s';",
                  StringUtils.trimToEmpty(idFPago), StringUtils.trimToEmpty(idFactura));
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            PagoJava pagoJava = new PagoJava();
            pagoJava.setValores( rs );
            lstPagos.add(pagoJava);
          }
          con.close();
        } else {
          System.out.println( "No existen la nota: "+idFactura );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstPagos;
    }


    public static List<PagoJava> busquedaPagosPorReferenciaPago(String referenciaPago) throws ParseException{
      List<PagoJava> lstPagos = new ArrayList<PagoJava>();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(referenciaPago).length() > 0){
          sql = String.format("SELECT * FROM pagos WHERE referencia_pago = '%s';", StringUtils.trimToEmpty(referenciaPago));
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            PagoJava pagoJava = new PagoJava();
            pagoJava.setValores( rs );
            lstPagos.add(pagoJava);
          }
          con.close();
        } else {
          System.out.println( "No existen la nota: "+referenciaPago );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstPagos;
    }


    public static PagoJava saveOrUpdatePago(PagoJava pagoJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      PagoJava pago = null;
      if( pagoJava != null && pagoJava.getIdPago() != null ){
        pagoJava.trim();
        sql = String.format("UPDATE pagos SET id_factura = '%s', id_banco = '%s', id_forma_pago = '%s', tipo_pago = '%s'," +
                    "referencia_pago = '%s', monto_pago = %s, fecha_pago = %s, id_empleado = '%s', id_recibo = '%s'," +
                "parcialidad = '%s', id_f_pago = '%s', clave_p = '%s', ref_clave = '%s', id_banco_emi = '%s', id_term = '%s'," +
                "id_plan = '%s', confirm = '%s', por_dev = %s WHERE id_pago = %d;", pagoJava.getIdFactura(), pagoJava.getIdBanco(),
                pagoJava.getIdFormaPago(), pagoJava.getTipoPago(), pagoJava.getReferenciaPago(), Utilities.toMoney(pagoJava.getMontoPago()),
                Utilities.toString(pagoJava.getFechaPago(), formatTimeStamp), pagoJava.getIdEmpleado(), pagoJava.getIdRecibo(),
                pagoJava.getParcialidad(), pagoJava.getIdFPago(), pagoJava.getClaveP(), pagoJava.getRefClave(), pagoJava.getIdBancoEmi(),
                pagoJava.getIdTerm(), pagoJava.getIdPlan(), Utilities.toBoolean(pagoJava.getConfirm()), pagoJava.getPorDev(), pagoJava.getIdPago());
            db.updateQuery(sql);
      } else if( pagoJava != null ){
        pagoJava.trim();
        sql = String.format("INSERT INTO pagos (id_factura, id_banco, id_forma_pago, tipo_pago," +
                "referencia_pago, monto_pago, fecha_pago, id_empleado, id_recibo," +
                "parcialidad, id_f_pago, clave_p, ref_clave, id_banco_emi, id_term," +
                "id_plan, confirm, por_dev)" +
                "VALUES('%s','%s','%s','%s','%s',%s,%s,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%s);",  pagoJava.getIdFactura(), pagoJava.getIdBanco(),
                pagoJava.getIdFormaPago(), pagoJava.getTipoPago(), pagoJava.getReferenciaPago(), Utilities.toMoney(pagoJava.getMontoPago()),
                Utilities.toString(pagoJava.getFechaPago(), formatTimeStamp), pagoJava.getIdEmpleado(), pagoJava.getIdRecibo(),
                pagoJava.getParcialidad(), pagoJava.getIdFPago(), pagoJava.getClaveP(), pagoJava.getRefClave(), pagoJava.getIdBancoEmi(),
                pagoJava.getIdTerm(), pagoJava.getIdPlan(), Utilities.toBoolean(pagoJava.getConfirm()), pagoJava.getPorDev());
        db.insertQuery( sql );
      }
      db.close();
      if(pagoJava != null && pagoJava.getIdPago() != null ){
        pago = busquedaPagosPorId( pagoJava.getIdPago() );
      } else {
        BigDecimal id = BigDecimal.ZERO;
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          sql = "";
          sql = String.format("SELECT last_value FROM pagos_id_pago_seq;");
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            id = rs.getBigDecimal("last_value");
          }
          con.close();
          if( id.compareTo(BigDecimal.ZERO) > 0 ){
            pago = busquedaPagosPorId( id.intValue() );
          }
        } catch (SQLException err) {
                System.out.println( err );
        }
      }
      return pago;
    }


}
