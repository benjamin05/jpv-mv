package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.TransInvDetJava;
import mx.lux.pos.java.repository.TransInvJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TransInvDetQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<TransInvDetJava> buscaTransInvDetPorIdTipoYFolio( String idTipoTrans, Integer folio ){
	  List<TransInvDetJava> lstTransInvDet = new ArrayList<TransInvDetJava>();
      TransInvDetJava transInvDetJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from trans_inv_det where id_tipo_trans = '%s' AND folio = %d;",
                StringUtils.trimToEmpty(idTipoTrans), folio);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          transInvDetJava = new TransInvDetJava();
          transInvDetJava = transInvDetJava.mapeoTransInvDet(rs);
          lstTransInvDet.add(transInvDetJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstTransInvDet;
	}


    public static void saveOrUpdateTransInvDet(TransInvDetJava transInvDetJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      if( transInvDetJava.getNumReg() != null ){
        sql = String.format("UPDATE trans_inv_det SET id_tipo_trans = '%s', folio = %d, linea = %d, sku = %d," +
                "tipo_mov = '%s', cantidad = %d WHERE num_reg = %d;", transInvDetJava.getIdTipoTrans(), transInvDetJava.getFolio(),
                transInvDetJava.getLinea(), transInvDetJava.getSku(), transInvDetJava.getTipoMov(),
                transInvDetJava.getCantidad(), transInvDetJava.getNumReg());
      } else {
        sql = String.format("INSERT INTO trans_inv_det (id_tipo_trans, folio, linea, sku, tipo_mov, cantidad)" +
                "VALUES('%s', %d, %d, %d, '%s', %d);", transInvDetJava.getIdTipoTrans(), transInvDetJava.getFolio(),
                transInvDetJava.getLinea(), transInvDetJava.getSku(), transInvDetJava.getTipoMov(),
                transInvDetJava.getCantidad());
      }
      db.insertQuery( sql );
      db.close();
    }


}
