package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesTipoJava;
import mx.lux.pos.java.repository.LogSpJava;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LogSpQuery {

	private static ResultSet rs;
    private static Statement stmt;


    public static List<LogSpJava> buscaLogSpPorFechaNull( ){
      List<LogSpJava> lstLog = new ArrayList<LogSpJava>();
      LogSpJava logSpJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM log_sp where fecha_respuesta is null ORDER BY fecha_llamada ASC;");
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          logSpJava = new LogSpJava();
          logSpJava = logSpJava.mapeoLogSp(rs);
          lstLog.add(logSpJava);
        }
        con.close();
      } catch (SQLException err) {
            System.out.println( err );
      }
      return lstLog;
    }



    public static LogSpJava saveLogSp(LogSpJava logSpJava) {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("INSERT INTO log_sp (id_factura,respuesta,id_articulo,fecha_llamada,fecha_respuesta)" +
              "VALUES('%s',%s,%d,%s,%s);", StringUtils.trimToEmpty(logSpJava.getIdFactura()),
              logSpJava.getRespuesta(), logSpJava.getIdArticulo(), Utilities.toString(logSpJava.getFechaLlamada(), formatTimeStamp),
              Utilities.toString(logSpJava.getFechaRespuesta(), formatTimeStamp));
      db.insertQuery( sql );
      db.close();
      return logSpJava;
    }
}
