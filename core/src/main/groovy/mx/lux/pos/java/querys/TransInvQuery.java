package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.Parametros;
import mx.lux.pos.java.repository.TransInvJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TransInvQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<TransInvJava> BuscaTransInvPorTipoYReferencia( String idTipoTrans, String referencia ){
	  List<TransInvJava> lstTransInv = new ArrayList<TransInvJava>();
      TransInvJava transInvJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from trans_inv where id_tipo_trans = '%s' AND referencia = '%s';",
                StringUtils.trimToEmpty(idTipoTrans), StringUtils.trimToEmpty(referencia));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          transInvJava = new TransInvJava();
          transInvJava = transInvJava.mapeoTransInv( rs );
          lstTransInv.add(transInvJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstTransInv;
	}



    public static List<TransInvJava> buscaTransInvPorTipoYFolio( String idTipoTrans, Integer folio ){
      List<TransInvJava> lstTransInv = new ArrayList<TransInvJava>();
      TransInvJava transInvJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from trans_inv where id_tipo_trans = '%s' AND folio = %d;",
                StringUtils.trimToEmpty(idTipoTrans), folio);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          transInvJava = new TransInvJava();
          transInvJava = transInvJava.mapeoTransInv( rs );
          lstTransInv.add(transInvJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstTransInv;
    }



    public static TransInvJava saveOrUpdateTransInv(TransInvJava transInvJava) throws ParseException {
      Connections db = new Connections();
      TransInvJava transInv = null;
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      if( transInvJava.getNumReg() != null ){
        sql = String.format("UPDATE trans_inv SET id_tipo_trans = '%s', folio = %d, fecha = %s, id_sucursal = %d," +
                "id_sucursal_destino = %d, referencia = '%s', observaciones = '%s', id_empleado = '%s' WHERE num_reg = %d;",
                transInvJava.getIdTipoTrans(), transInvJava.getFolio(), Utilities.toString(transInvJava.getFecha(), formatDate),
                transInvJava.getIdSucursal(), transInvJava.getIdSucursalDestino(), transInvJava.getReferencia(),
                transInvJava.getObservaciones(), transInvJava.getIdEmpleado(), transInvJava.getNumReg());
        } else {
            sql = String.format("INSERT INTO trans_inv (id_tipo_trans, folio, fecha, id_sucursal, id_sucursal_destino," +
                    "referencia, observaciones, id_empleado) VALUES('%s',%d, %s, %d, %d, '%s', '%s', '%s');",
                    transInvJava.getIdTipoTrans(), transInvJava.getFolio(), Utilities.toString(transInvJava.getFecha(), formatDate),
                    transInvJava.getIdSucursal(), transInvJava.getIdSucursalDestino(), transInvJava.getReferencia(),
                    transInvJava.getObservaciones(), transInvJava.getIdEmpleado());
        }

        db.insertQuery( sql );
        db.close();
        List<TransInvJava> lstTrans = BuscaTransInvPorTipoYReferencia( transInvJava.getIdTipoTrans(), transInvJava.getReferencia() );
        if( lstTrans.size() > 0 ){
          transInv = lstTrans.get(0);
        }
        return transInv;
    }

}
