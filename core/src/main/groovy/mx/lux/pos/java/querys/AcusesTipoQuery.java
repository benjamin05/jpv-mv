package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.AcusesTipoJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AcusesTipoQuery {

	private static ResultSet rs;
    private static Statement stmt;


    public static AcusesTipoJava buscaAcuseTipoPorIdTipo( String idTipo ){
      AcusesTipoJava acusesTipoJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM acuses_tipo where id_tipo = '%s';", idTipo);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          acusesTipoJava = new AcusesTipoJava();
            acusesTipoJava = acusesTipoJava.mapeoAcusesTipo(rs);
        }
        con.close();
      } catch (SQLException err) {
            System.out.println( err );
      }
      return acusesTipoJava;
    }

}
