package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.DescuentosClaveJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DescuentosClaveQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static DescuentosClaveJava buscaDescuentoClavePorClave( String clave ){
	  DescuentosClaveJava descuentoClave = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from descuentos_clave where clave_descuento = '%s';", StringUtils.trimToEmpty(clave));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          descuentoClave = new DescuentosClaveJava();
          descuentoClave = descuentoClave.mapeoDescuentosClave(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return descuentoClave;
	}


    public static void saveDescuentoClave(DescuentosClaveJava descuentosClaveJava) {
      Connections db = new Connections();
      DescuentosClaveJava descuentosClave = null;
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      if( buscaDescuentoClavePorClave(descuentosClaveJava.getClaveDescuento()) == null ){
        sql = String.format("INSERT INTO descuentos_clave (clave_descuento,porcenaje_descuento,descripcion_descuento,tipo,vigente,cupon,monto_minimo)" +
                "VALUES('%s',%d,'%s','%s',%s,%s,%s);", StringUtils.trimToEmpty(descuentosClaveJava.getClaveDescuento()),
                descuentosClaveJava.getPorcenajeDescuento().intValue(), descuentosClaveJava.getDescripcionDescuento(),
                descuentosClaveJava.getTipo(), Utilities.toBoolean(descuentosClaveJava.getVigente()),
                descuentosClaveJava.getCupon() != null ? Utilities.toBoolean(descuentosClaveJava.getCupon()) : null,
                (descuentosClaveJava.getMontoMinimo() != null) ? Utilities.toMoney(descuentosClaveJava.getMontoMinimo()) : null);
      } else {
        sql = String.format("UPDATE descuentos_clave SET porcenaje_descuento = %d, descripcion_descuento = '%s', " +
                "tipo = '%s', vigente = %s, cupon = %s, monto_minimo = %s WHERE clave_descuento = '%s';",
                descuentosClaveJava.getPorcenajeDescuento().intValue(), descuentosClaveJava.getDescripcionDescuento(),
                descuentosClaveJava.getTipo(), Utilities.toBoolean(descuentosClaveJava.getVigente()),
                descuentosClaveJava.getCupon() != null ? Utilities.toBoolean(descuentosClaveJava.getCupon()) : null,
                (descuentosClaveJava.getMontoMinimo() != null) ? Utilities.toMoney(descuentosClaveJava.getMontoMinimo()) : null,
                StringUtils.trimToEmpty(descuentosClaveJava.getClaveDescuento()));
      }
        db.insertQuery( sql );
        db.close();
    }
}
