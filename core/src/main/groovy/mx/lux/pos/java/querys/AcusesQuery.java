package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AcusesQuery {

	private static ResultSet rs;
    private static Statement stmt;

    public static void saveAcuses(AcusesJava acusesJava) {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("INSERT INTO acuses (id_acuse,contenido,fecha_carga,fecha_acuso,id_tipo,folio,intentos)" +
              "VALUES(%d,'%s',%s,%s,'%s','%s',%d);", acusesJava.getIdAcuse(), StringUtils.trimToEmpty(acusesJava.getContenido()),
              Utilities.toString(acusesJava.getFechaCarga(), formatTimeStamp), Utilities.toString(acusesJava.getFechaAcuso(), formatTimeStamp),
              StringUtils.trimToEmpty(acusesJava.getIdTipo()), StringUtils.trimToEmpty(acusesJava.getFolio()),
              acusesJava.getIntentos());
      db.insertQuery( sql );
      db.close();
    }
	
}
