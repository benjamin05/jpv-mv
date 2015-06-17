package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.DetalleNotaVentaJava;
import mx.lux.pos.java.repository.FacturasImpuestosJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FacturasImpuestosQuery {

	private static ResultSet rs;
    private static Statement stmt;


    public static FacturasImpuestosJava updateFacturasImpuestos (FacturasImpuestosJava facturasImpuestosJava) throws ParseException {
      String sql = "";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("INSERT INTO facturas_impuestos (id_factura,id_impuesto,rfc,id_sucursal,fecha)" +
              "VALUES('%s','%s','%s',%d,%s)",
              StringUtils.trimToEmpty(facturasImpuestosJava.getIdFactura()),StringUtils.trimToEmpty(facturasImpuestosJava.getIdImpuesto()),
              StringUtils.trimToEmpty(facturasImpuestosJava.getRfc()), facturasImpuestosJava.getIdSucursal(),
              Utilities.toString(facturasImpuestosJava.getFecha(), formatTimeStamp));
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
      return facturasImpuestosJava;
    }
}
