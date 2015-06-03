package mx.lux.pos.querys;

import mx.lux.pos.repository.OrdenPromDetJava;
import mx.lux.pos.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrdenPromDetQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<OrdenPromDetJava> BuscaOrdenPromDetPorIdFactura( String idFactura ){
	  List<OrdenPromDetJava> lstOrdenPromDet = new ArrayList<OrdenPromDetJava>();
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from gparametro where id_parametro = '%s';", StringUtils.trimToEmpty(idFactura));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
              OrdenPromDetJava ordenPromDetJava = new OrdenPromDetJava();
              ordenPromDetJava = ordenPromDetJava.mapeoOrdenPromDet(rs);
              lstOrdenPromDet.add(ordenPromDetJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstOrdenPromDet;
	}
	
}
