package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.ModeloLcJava;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ModeloLcQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<ModeloLcJava> buscaTodoModeloLc( ){
	  List<ModeloLcJava> lstModelosLc = new ArrayList<ModeloLcJava>();
      ModeloLcJava modeloLcJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from modelo_lc");
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          modeloLcJava = new ModeloLcJava();
          modeloLcJava = modeloLcJava.mapeoModeloLc(rs);
          lstModelosLc.add(modeloLcJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstModelosLc;
	}
	
}
