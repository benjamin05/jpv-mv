package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.OrdenPromJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class OrdenPromQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<OrdenPromJava> buscaListaOrdenPromPorIdFactura( String idFactura ){
	  List<OrdenPromJava> lstOrdenProm = new ArrayList<OrdenPromJava>();
      OrdenPromJava ordenPromJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from orden_prom where id_factura = '%s';", StringUtils.trimToEmpty(idFactura));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          ordenPromJava = new OrdenPromJava();
          ordenPromJava = ordenPromJava.mapeoOrdenProm(rs);
          lstOrdenProm.add(ordenPromJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstOrdenProm;
	}



    public static OrdenPromJava buscaListaOrdenPromPorId( Integer id ){
      OrdenPromJava ordenPromJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from orden_prom where id = %d;", id);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          ordenPromJava = new OrdenPromJava();
          ordenPromJava = ordenPromJava.mapeoOrdenProm(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return ordenPromJava;
    }


    public static void eliminaListaOrdenProm( List<OrdenPromJava> lstOrdenPromJava ){
      for(OrdenPromJava ordenPromJava : lstOrdenPromJava){
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          String sql = String.format("DELETE FROM orden_prom WHERE id = %d;", ordenPromJava.getId());
          stmt.executeUpdate(sql);
          con.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    public static OrdenPromJava saveOrUpdateOrdenProm(OrdenPromJava ordenPromJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      OrdenPromJava ordenProm = null;
      if( ordenPromJava != null && ordenPromJava.getId() != null ){
        sql = String.format("UPDATE orden_prom SET id_factura = '%s', id_prom = %d, id_suc = %d, total_desc_monto = %s," +
                "WHERE id = %d;",
                ordenPromJava.getIdFactura(), ordenPromJava.getIdProm(), ordenPromJava.getIdSuc(),
                Utilities.toMoney(ordenPromJava.getTotalDescMonto()), ordenPromJava.getId() );
        db.updateQuery(sql);
      } else {
        sql = String.format("INSERT INTO orden_prom (id_factura,id_prom,id_suc,total_desc_monto)" +
                "VALUES('%s',%d,%d,%s);", StringUtils.trimToEmpty(ordenPromJava.getIdFactura()), ordenPromJava.getIdProm(),
                ordenPromJava.getIdSuc(), Utilities.toMoney(ordenPromJava.getTotalDescMonto()));
            db.insertQuery( sql );
      }
      db.close();
      if( ordenPromJava.getId() != null ){
        ordenProm = buscaListaOrdenPromPorId( ordenPromJava.getId() );
      } else {
        BigDecimal id = BigDecimal.ZERO;
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          sql = "";
          sql = String.format("SELECT last_value FROM orden_prom_id_seq;");
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            id = rs.getBigDecimal("last_value");
          }
          con.close();
          if( id.compareTo(BigDecimal.ZERO) > 0 ){
            con = Connections.doConnect();
            stmt = con.createStatement();
            sql = "";
            sql = String.format("SELECT * FROM orden_prom WHERE id = %d;", id.intValue());
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
              ordenProm = new OrdenPromJava();
              ordenProm = ordenProm.mapeoOrdenProm( rs );
            }
            con.close();
          }
        } catch (SQLException err) {
          System.out.println( err );
        }
      }

        return ordenProm;
    }


}
