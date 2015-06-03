package mx.lux.pos.querys;

import mx.lux.pos.Utilities;
import mx.lux.pos.model.Jb;
import mx.lux.pos.repository.DetalleNotaVentaJava;
import mx.lux.pos.repository.PagoJava;
import mx.lux.pos.repository.TmpServiciosJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TmpServiciosQuery {

	private static ResultSet rs;
    private static Statement stmt;

    public static void saveTmpServicio (TmpServiciosJava tmpServiciosJava) {
      String format = "yyyy-MM-dd HH:mm:ss.SSS";
      String sql = String.format("INSERT INTO tmp_servicios (id_factura,id_cliente,cliente,dejo,instruccion,emp,servicio,condicion,fecha_prom)" +
              "VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s');",
              tmpServiciosJava.getIdFactura(),tmpServiciosJava.getIdCliente(),tmpServiciosJava.getCliente(),
              tmpServiciosJava.getDejo(),tmpServiciosJava.getInstruccion(),tmpServiciosJava.getEmp(),
              tmpServiciosJava.getServicio(),tmpServiciosJava.getCondicion(),
              Utilities.toString(tmpServiciosJava.getFechaProm(),format));
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }

}
