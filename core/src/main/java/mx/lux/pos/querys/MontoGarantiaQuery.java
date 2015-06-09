package mx.lux.pos.querys;

import mx.lux.pos.repository.MontoGarantiaJava;
import mx.lux.pos.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MontoGarantiaQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static MontoGarantiaJava buscaMontoGarantiaPorMontoGarantia( BigDecimal monto ){
	  MontoGarantiaJava montoGarantiaJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from monto_garantia where monto_garantia = '%f';", monto);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          montoGarantiaJava = new MontoGarantiaJava();
          montoGarantiaJava = montoGarantiaJava.mapeoMontoGarantia(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return montoGarantiaJava;
	}
	
}
