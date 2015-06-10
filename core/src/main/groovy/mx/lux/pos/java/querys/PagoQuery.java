package mx.lux.pos.java.querys;

import mx.lux.pos.model.Jb;
import mx.lux.pos.java.repository.PagoJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PagoQuery {

	private static ResultSet rs;
    private static Statement stmt;
    private Jb jb;
	
	public static List<PagoJava> busquedaPagosPorIdFactura(String idFactura) throws ParseException{
      List<PagoJava> lstPagos = new ArrayList<PagoJava>();

      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(idFactura).length() > 0){
          sql = String.format("SELECT * FROM pagos WHERE id_factura = '%s';", StringUtils.trimToEmpty(idFactura));
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            PagoJava pagoJava = new PagoJava();
            pagoJava.setValores( rs );
            lstPagos.add(pagoJava);
          }
          con.close();
        } else {
          System.out.println( "No existen la nota: "+idFactura );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstPagos;
	}

}
