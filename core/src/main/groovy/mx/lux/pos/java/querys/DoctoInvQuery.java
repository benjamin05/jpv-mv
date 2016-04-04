package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.DoctoInvJava;
import mx.lux.pos.java.repository.JbJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctoInvQuery {

	private static ResultSet rs;
    private static Statement stmt;


    public static DoctoInvJava saveDoctoInv(DoctoInvJava doctoInvJava) {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("INSERT INTO docto_inv (id_docto,id_tipo_docto,fecha,usuario,referencia,id_sync,id_mod,fecha_mod," +
              "id_sucursal,notas,cantidad,estado,sistema)" +
              "VALUES('%s','%s',%s,'%s','%s','%s','%s',%s,%d,'%s','%s','%s','%s');", StringUtils.trimToEmpty(doctoInvJava.getIdDocto()),
              StringUtils.trimToEmpty(doctoInvJava.getIdTipoDocto()),Utilities.toString(doctoInvJava.getFecha(), formatTimeStamp),
              StringUtils.trimToEmpty(doctoInvJava.getUsuario()),StringUtils.trimToEmpty(doctoInvJava.getReferencia()),
              StringUtils.trimToEmpty(doctoInvJava.getIdSync()),StringUtils.trimToEmpty(doctoInvJava.getIdMod()),
              Utilities.toString(doctoInvJava.getFechaMod(), formatTimeStamp),doctoInvJava.getIdSucursal(),
              StringUtils.trimToEmpty(doctoInvJava.getNotas()), StringUtils.trimToEmpty(doctoInvJava.getCantidad()),
              StringUtils.trimToEmpty(doctoInvJava.getEstado()), StringUtils.trimToEmpty(doctoInvJava.getSistema()));
      db.insertQuery( sql );
      db.close();
      return doctoInvJava;
    }


    public static List<DoctoInvJava> buscarDoctoInvPorIdTipoDoctoYEstado( String idTipoDocto, String estado ){
      List<DoctoInvJava> lstDoctoInv = new ArrayList<DoctoInvJava>();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from docto_inv where id_tipo_docto = '%s' AND estado = '%s';",
                StringUtils.trimToEmpty(idTipoDocto), StringUtils.trimToEmpty(estado));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          DoctoInvJava doctoInv = new DoctoInvJava();
          doctoInv = doctoInv.mapeoDoctoInvJava(rs);
          lstDoctoInv.add(doctoInv);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstDoctoInv;
    }

}
