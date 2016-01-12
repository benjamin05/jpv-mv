package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.TmpServiciosJava;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TmpServiciosQuery {

	private static ResultSet rs;
    private static Statement stmt;

    public static void saveTmpServicio (TmpServiciosJava tmpServiciosJava) {
      String format = "yyyy-MM-dd HH:mm:ss.SSS";
      String sql = String.format("INSERT INTO tmp_servicios (id_factura,id_cliente,cliente,dejo,instruccion,emp,servicio,condicion,fecha_prom)" +
              "VALUES('%s','%s','%s','%s','%s','%s','%s','%s',%s);",
              tmpServiciosJava.getIdFactura(),tmpServiciosJava.getIdCliente(),tmpServiciosJava.getCliente(),
              tmpServiciosJava.getDejo(),tmpServiciosJava.getInstruccion(),tmpServiciosJava.getEmp(),
              tmpServiciosJava.getServicio(),tmpServiciosJava.getCondicion(),
              Utilities.toString(tmpServiciosJava.getFechaProm(),format));
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }


    public static TmpServiciosJava buscaTmpServiciosPorIdFactura( String idFactura ){
      TmpServiciosJava tmpServiciosJava = new TmpServiciosJava();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM tmp_servicios where id_factura = '%s';", StringUtils.trimToEmpty(idFactura));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          tmpServiciosJava = new TmpServiciosJava();
          tmpServiciosJava = tmpServiciosJava.setValores( rs );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return tmpServiciosJava;
    }

}
