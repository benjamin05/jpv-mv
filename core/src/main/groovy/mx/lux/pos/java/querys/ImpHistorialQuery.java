package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.ImpHistorialJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ImpHistorialQuery {

	private static ResultSet rs;
    private static Statement stmt;

    public static void saveImpHistorial(ImpHistorialJava impHistorialJava) {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("INSERT INTO imp_historial (id_cliente,id_suc_ori,fecha_compra,factura,importe,obs)" +
              "VALUES(%d,'%s',%s,'%s',%s,'%s');", impHistorialJava.getIdCliente(),
              StringUtils.trimToEmpty(impHistorialJava.getIdSucOri()), Utilities.toString(impHistorialJava.getFechaCompra(), formatDate),
              StringUtils.trimToEmpty(impHistorialJava.getFactura()), Utilities.toMoney(impHistorialJava.getImporte()),
              StringUtils.trimToEmpty(impHistorialJava.getObs()));
      db.insertQuery( sql );
      db.close();
    }
	
}
