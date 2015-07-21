package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.Parametros;
import mx.lux.pos.java.repository.TipoTransInvJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TipoTransInvQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static TipoTransInvJava buscaTipoTransInvPorIdTipo( String idTipoTrans ){
	  TipoTransInvJava tipoTransInvJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from tipo_trans_inv where id_tipo_trans = '%s';", StringUtils.trimToEmpty(idTipoTrans));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          tipoTransInvJava = new TipoTransInvJava();
          tipoTransInvJava = tipoTransInvJava.mapeoTipoTransInv(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return tipoTransInvJava;
	}


    public static void updateTipoTransInv(TipoTransInvJava tipoTransInvJava) {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("UPDATE tipo_trans_inv SET ultimo_folio = %d WHERE id_tipo_trans = '%s';",
              tipoTransInvJava.getUltimoFolio(), StringUtils.trimToEmpty(tipoTransInvJava.getIdTipoTrans()));
      db.insertQuery( sql );
      db.close();
    }
}
