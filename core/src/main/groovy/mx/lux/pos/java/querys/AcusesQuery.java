package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.Parametros;
import mx.lux.pos.java.repository.TransInvDetJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AcusesQuery {

	private static ResultSet rs;
    private static Statement stmt;

    public static AcusesJava saveAcuses(AcusesJava acusesJava) {
      Connections db = new Connections();
      AcusesJava acuses = null;
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      if( acusesJava.getIdAcuse() == null ){
        sql = String.format("INSERT INTO acuses (contenido,fecha_carga,fecha_acuso,id_tipo,folio,intentos)" +
                "VALUES('%s',%s,%s,'%s','%s',%d);", StringUtils.trimToEmpty(acusesJava.getContenido()),
                Utilities.toString(acusesJava.getFechaCarga(), formatTimeStamp), Utilities.toString(acusesJava.getFechaAcuso(), formatTimeStamp),
                StringUtils.trimToEmpty(acusesJava.getIdTipo()), StringUtils.trimToEmpty(acusesJava.getFolio()),
                acusesJava.getIntentos());
      } else {
        sql = String.format("UPDATE acuses SET contenido = '%s',fecha_carga = %s, fecha_acuso = %s, id_tipo = '%s',folio = '%s'," +
                "intentos = %d WHERE id_acuse = %d;", StringUtils.trimToEmpty(acusesJava.getContenido()),
                Utilities.toString(acusesJava.getFechaCarga(), formatTimeStamp), Utilities.toString(acusesJava.getFechaAcuso(), formatTimeStamp),
                StringUtils.trimToEmpty(acusesJava.getIdTipo()), StringUtils.trimToEmpty(acusesJava.getFolio()),
                acusesJava.getIntentos(), acusesJava.getIdAcuse());
      }
      db.insertQuery( sql );
      db.close();
      BigDecimal id = BigDecimal.ZERO;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        sql = "";
        sql = String.format("SELECT last_value FROM acuses_id_acuse_seq;");
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          id = rs.getBigDecimal("last_value");
        }
        con.close();
        if( id.compareTo(BigDecimal.ZERO) > 0 ){
          con = Connections.doConnect();
          stmt = con.createStatement();
          sql = "";
          sql = String.format("SELECT * FROM acuses WHERE id_acuse = %d;", id.intValue());
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            acuses = new AcusesJava();
            acuses.mapeoAcuses(rs);
          }
          con.close();
        }
      } catch (SQLException err) {
            System.out.println( err );
      }
      return acuses;
    }
	
}
