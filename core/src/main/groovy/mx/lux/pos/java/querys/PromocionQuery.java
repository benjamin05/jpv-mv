package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.AcusesJava;
import mx.lux.pos.java.repository.PromocionJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PromocionQuery {

	private static ResultSet rs;
    private static Statement stmt;


    public static List<PromocionJava> buscaPromocionesCrm( ){
      List<PromocionJava> lstPromociones = new ArrayList<PromocionJava>();
      PromocionJava promocionJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "select * from promocion where descripcion like 'CRM:%' or descripcion like 'crm:%' ORDER BY id_promocion ASC;";
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          promocionJava = new PromocionJava();
          promocionJava = promocionJava.mapeoPromocion(rs);
          lstPromociones.add(promocionJava);
        }
      } catch (SQLException err) {
            System.out.println( err );
      }
      return lstPromociones;
    }


    public static PromocionJava buscaPromocionPorDescCrm( String descripcion ){
      PromocionJava promocionJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from promocion where descripcion = '%s';", StringUtils.trimToEmpty(descripcion));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          promocionJava = new PromocionJava();
          promocionJava = promocionJava.mapeoPromocion(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return promocionJava;
    }
}
