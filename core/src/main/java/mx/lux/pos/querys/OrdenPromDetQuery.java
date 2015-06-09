package mx.lux.pos.querys;

import mx.lux.pos.Utilities;
import mx.lux.pos.repository.NotaVentaJava;
import mx.lux.pos.repository.OrdenPromDetJava;
import mx.lux.pos.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
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
        String sql = String.format("select * from orden_prom_det where id_factura = '%s';", StringUtils.trimToEmpty(idFactura));
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


    public static void eliminaListaOrdenPromDet( List<OrdenPromDetJava> lstOrdenPromDetJava ){
      for(OrdenPromDetJava ordenPromDetJava : lstOrdenPromDetJava){
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          String sql = String.format("DELETE FROM orden_prom_det WHERE id_orden_prom_det = %d;", ordenPromDetJava.getIdOrdenPromDet());
          stmt.executeUpdate(sql);
          con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
      }
    }


    public static OrdenPromDetJava saveOrUpdateOrdenPromDet(OrdenPromDetJava ordenPromDetJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      OrdenPromDetJava ordenPromDet = null;
      if( ordenPromDetJava != null && ordenPromDetJava.getIdOrdenPromDet() != null ){
        sql = String.format("UPDATE orden_prom_det SET id = %d, id_factura = '%s', id_prom = %d, id_suc = %d," +
                "id_art = %d, descuento_monto = %s, descuento_porcentaje = %f WHERE id_orden_prom_det = %d;",
                ordenPromDetJava.getId(), ordenPromDetJava.getIdFactura(), ordenPromDetJava.getIdSuc(),
                ordenPromDetJava.getIdArt(), Utilities.toMoney(ordenPromDetJava.getDescuentoMonto()),+
                ordenPromDetJava.getDescuentoPorcentaje(), ordenPromDetJava.getIdOrdenPromDet() );
        db.updateQuery(sql);
      } else {
        sql = String.format("INSERT INTO orden_prom_det (id,id_factura,id_prom,id_suc,id_art,descuento_monto,descuento_porcentaje)" +
                "VALUES(%d,'%s',%d,%d,%d,%s,%f);", ordenPromDetJava.getId(),StringUtils.trimToEmpty(ordenPromDetJava.getIdFactura()),
                ordenPromDetJava.getIdProm(), ordenPromDetJava.getIdSuc(), ordenPromDetJava.getIdArt(),
                Utilities.toMoney(ordenPromDetJava.getDescuentoMonto()),ordenPromDetJava.getDescuentoPorcentaje());
        db.insertQuery( sql );
      }
      db.close();
      BigDecimal id = BigDecimal.ZERO;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        sql = "";
        sql = String.format("SELECT last_value FROM orden_prom_det_id_orden_prom_det_seq;");
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
            ordenPromDet = new OrdenPromDetJava();
            ordenPromDet = ordenPromDet.mapeoOrdenPromDet( rs );
          }
          con.close();
        }
      } catch (SQLException err) {
            System.out.println( err );
      }

      return ordenPromDet;
    }
}
