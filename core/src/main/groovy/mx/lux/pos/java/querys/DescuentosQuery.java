package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.DescuentosJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DescuentosQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<DescuentosJava> buscaDescuentosPorIdFactura( String idFactura ){
	  List<DescuentosJava> lstDescuentos = new ArrayList<DescuentosJava>();
      DescuentosJava descuentosJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from descuentos where id_factura = '%s';", StringUtils.trimToEmpty(idFactura));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          descuentosJava = new DescuentosJava();
          descuentosJava = descuentosJava.mapeoDescuentos(rs);
          lstDescuentos.add( descuentosJava );
        }
        con.close();
      } catch (SQLException err) {
            System.out.println( err );
      }
	  return lstDescuentos;
	}


    public static DescuentosJava buscaDescuentosPorId( Integer id ){
      DescuentosJava descuentosJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from descuentos where id = %d;", id);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          descuentosJava = new DescuentosJava();
          descuentosJava = descuentosJava.mapeoDescuentos(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return descuentosJava;
    }


    public static void eliminaDescuento( DescuentosJava descuentosJava ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("DELETE FROM descuentos WHERE id = %d;", descuentosJava.getId());
        stmt.executeUpdate(sql);
        con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }



    public static DescuentosJava saveOrUpdateDescuentos(DescuentosJava descuentosJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      DescuentosJava descuentos = null;
      if( descuentosJava != null && descuentosJava.getId() != null ){
        sql = String.format("UPDATE descuentos SET id_factura = '%s', clave = '%s', porcentaje = '%s', id_empleado = '%s'," +
                "id_tipo_d = '%s', tipo_clave = '%s' WHERE id = %d;",
                descuentosJava.getIdFactura(), descuentosJava.getClave(), descuentosJava.getPorcentaje(),
                descuentosJava.getIdEmpleado(), descuentosJava.getIdTipoD(), descuentosJava.getTipoClave(), descuentosJava.getId() );
        db.updateQuery(sql);
      } else {
        sql = String.format("INSERT INTO descuentos (id_factura,clave,porcentaje,id_empleado,id_tipo_d,tipo_clave)" +
                "VALUES('%s','%s','%s','%s','%s','%s');", StringUtils.trimToEmpty(descuentosJava.getIdFactura()),
                descuentosJava.getClave(), descuentosJava.getPorcentaje(), descuentosJava.getIdEmpleado(),
                descuentosJava.getIdTipoD(), descuentosJava.getTipoClave());
        db.insertQuery( sql );
      }
      db.close();
      if( descuentosJava.getId() != null ){
        descuentos = buscaDescuentosPorId( descuentosJava.getId() );
      } else {
        BigDecimal id = BigDecimal.ZERO;
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          sql = "";
          sql = String.format("SELECT last_value FROM descuentos_id_seq;");
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            id = rs.getBigDecimal("last_value");
          }
          con.close();
          if( id.compareTo(BigDecimal.ZERO) > 0 ){
            con = Connections.doConnect();
            stmt = con.createStatement();
            sql = "";
            sql = String.format("SELECT * FROM descuentos WHERE id = %d;", id.intValue());
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
              descuentos = new DescuentosJava();
              descuentos = descuentos.mapeoDescuentos( rs );
            }
            con.close();
          }
        } catch (SQLException err) {
          System.out.println( err );
        }
      }

      return descuentos;
    }



    public static List<DescuentosJava> buscaDescuentosPorClaveAndIdFactura( String clave, String idFactura ){
      List<DescuentosJava> lstDescuentos = new ArrayList<DescuentosJava>();
      DescuentosJava descuentosJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from descuentos where id_factura = '%s' AND clave = '%S';",
                StringUtils.trimToEmpty(idFactura), StringUtils.trimToEmpty(clave));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          descuentosJava = new DescuentosJava();
          descuentosJava = descuentosJava.mapeoDescuentos(rs);
          lstDescuentos.add( descuentosJava );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstDescuentos;
    }


    public static DescuentosJava buscaDescuentosPorIdFacturaAndClaveVacia( String idFactura ){
      DescuentosJava descuentosJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from descuentos where id_factura = '%s' AND (clave is null OR clave = '');",
                StringUtils.trimToEmpty(idFactura));
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          descuentosJava = new DescuentosJava();
          descuentosJava = descuentosJava.mapeoDescuentos(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return descuentosJava;
    }


}
