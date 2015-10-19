package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.CierreDiarioJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CierreDiarioQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static CierreDiarioJava buscaCierreDiarioPorFecha( Date fecha){
	  CierreDiarioJava cierreDiarioJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM cierre_diario where fecha = %s;", Utilities.toString(fecha, "yyyy-MM-dd"));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          cierreDiarioJava = new CierreDiarioJava();
          cierreDiarioJava = cierreDiarioJava.mapeoCierreDiario(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return cierreDiarioJava;
	}


    public static List<CierreDiarioJava> buscaCierresDiariosNoValidados( ){
      List<CierreDiarioJava> lstCierres = new ArrayList<CierreDiarioJava>();
      CierreDiarioJava cierreDiarioJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM cierre_diario where verificado = false;");
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          cierreDiarioJava = new CierreDiarioJava();
          cierreDiarioJava = cierreDiarioJava.mapeoCierreDiario(rs);
          lstCierres.add(cierreDiarioJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstCierres;
    }


    public static CierreDiarioJava updateCierreDiario(CierreDiarioJava cierreDiarioJava) {
      Connections db = new Connections();
      CierreDiarioJava cierreDiario = null;
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      if( cierreDiarioJava.getFecha() != null ){
        sql = String.format("UPDATE cierre_diario SET estado = '%s', fecha_btn = %s, hora_cierre = %s,observaciones = '%s'," +
                "venta_bruta = %s,venta_neta = %s,cancelaciones = %s,modificaciones = %s,ingreso_bruto = %s,ingreso_neto = %s," +
                "devoluciones = %s,efectivo_recibido = %s,efectivo_externos = %s,efectivo_devoluciones = %s,efectivo_neto = %s," +
                "usd_recibido = %s,usd_devoluciones = %s,ventas_cantidad = %d,modificaciones_cantidad = %d," +
                "cancelaciones_cantidad = %d,factura_inicial = '%s',factura_final = '%s',verificado = %s WHERE fecha = %s;",
                StringUtils.trimToEmpty(cierreDiarioJava.getEstado()), Utilities.toString(cierreDiarioJava.getFechaBtn(), formatDate),
                Utilities.toString(cierreDiarioJava.getHoraCierre(), formatTime),StringUtils.trimToEmpty(cierreDiarioJava.getObservaciones()),
                Utilities.toMoney(cierreDiarioJava.getVentaBruta()), Utilities.toMoney(cierreDiarioJava.getVentaNeta()),
                Utilities.toMoney(cierreDiarioJava.getCancelaciones()), Utilities.toMoney(cierreDiarioJava.getModificaciones()),
                Utilities.toMoney(cierreDiarioJava.getIngresoBruto()), Utilities.toMoney(cierreDiarioJava.getIngresoNeto()),
                Utilities.toMoney(cierreDiarioJava.getDevoluciones()), Utilities.toMoney(cierreDiarioJava.getEfectivoRecibido()),
                Utilities.toMoney(cierreDiarioJava.getEfectivoExternos()), Utilities.toMoney(cierreDiarioJava.getEfectivoDevoluciones()),
                Utilities.toMoney(cierreDiarioJava.getEfectivoNeto()), Utilities.toMoney(cierreDiarioJava.getUsdRecibido()),
                Utilities.toMoney(cierreDiarioJava.getUsdDevoluciones()), cierreDiarioJava.getVentasCantidad(),
                cierreDiarioJava.getModificacionesCantidad(), cierreDiarioJava.getCancelacionesCantidad(),
                cierreDiarioJava.getFacturaInicial(), cierreDiarioJava.getFacturaFinal(), Utilities.toBoolean(cierreDiarioJava.getVerificado()),
                Utilities.toString(cierreDiarioJava.getFecha(), formatDate));
        db.insertQuery( sql );
        db.close();

        cierreDiario = buscaCierreDiarioPorFecha( cierreDiarioJava.getFecha() );
      }
      return cierreDiario;
    }


}
