package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.DetalleNotaVentaJava;
import mx.lux.pos.java.repository.OrdenPromJava;
import mx.lux.pos.java.repository.PedidoLcDetJava;
import mx.lux.pos.java.repository.PedidoLcJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidoLcQuery {

	private static ResultSet rs;
    private static Statement stmt;

    public static PedidoLcJava buscaPedidoLcPorId( String idPedido ){
      PedidoLcJava pedidoLcJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from pedido_lc where id_pedido = '%s';", StringUtils.trimToEmpty(idPedido));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          pedidoLcJava = new PedidoLcJava();
          pedidoLcJava = pedidoLcJava.mapeoPedidoLc(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
         e.printStackTrace();
      }
        return pedidoLcJava;
    }


    public static List<PedidoLcDetJava> buscaPedidoLcDetPorIdPedido(String idFactura){
      List<PedidoLcDetJava> lstPedidosLcDet = new ArrayList<PedidoLcDetJava>();
      PedidoLcDetJava pedidoLcDetJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from pedido_lc_det where id_pedido = '%s';", StringUtils.trimToEmpty(idFactura));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          pedidoLcDetJava = new PedidoLcDetJava();
          pedidoLcDetJava = pedidoLcDetJava.mapeoPedidoLcDet(rs);
          lstPedidosLcDet.add(pedidoLcDetJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstPedidosLcDet;
    }


    public static PedidoLcDetJava buscaPedidoLcDetPorId(Integer id){
      PedidoLcDetJava pedidoLcDetJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from pedido_lc_det where num_reg = %d;", id);
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          pedidoLcDetJava = new PedidoLcDetJava();
          pedidoLcDetJava = pedidoLcDetJava.mapeoPedidoLcDet(rs);
        }
      } catch (SQLException err) {
            System.out.println( err );
      }
      return pedidoLcDetJava;
    }

	public static List<PedidoLcDetJava> buscaPedidoLcDetPorIdYModelo(String idFactura, String articulo){
	  List<PedidoLcDetJava> lstPedidosLcDet = new ArrayList<PedidoLcDetJava>();
      PedidoLcDetJava pedidoLcDetJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from pedido_lc_det where id_pedido = '%s' AND modelo = '%s';",
                StringUtils.trimToEmpty(idFactura),StringUtils.trimToEmpty(articulo));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          pedidoLcDetJava = new PedidoLcDetJava();
          pedidoLcDetJava = pedidoLcDetJava.mapeoPedidoLcDet(rs);
          lstPedidosLcDet.add(pedidoLcDetJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstPedidosLcDet;
	}


    public static void savePedidoLc(PedidoLcJava pedidoLcJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("INSERT INTO pedido_lc (id_pedido,folio,cliente,sucursal,fecha_alta)" +
              "VALUES('%s','%s','%s','%s',%s);", StringUtils.trimToEmpty(pedidoLcJava.getIdPedido()),
              StringUtils.trimToEmpty(pedidoLcJava.getFolio()), pedidoLcJava.getCliente(), pedidoLcJava.getSucursal(),
              Utilities.toString(pedidoLcJava.getFechaAlta() != null ? pedidoLcJava.getFechaAlta() : new Date(), formatTimeStamp));
      db.insertQuery( sql );
      db.close();
    }


    public static void savePedidoLcDet(PedidoLcDetJava pedidoLcDetJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("INSERT INTO pedido_lc_det (num_reg,id_pedido,curva_base,diametro,esfera,cilindro,modelo,eje,color,cantidad)" +
              "VALUES((SELECT NEXTVAL('pedido_lc_det_id_seq')),'%s','%s','%s','%s','%s','%s','%s','%s',%d);",
              StringUtils.trimToEmpty(pedidoLcDetJava.getIdPedido()), pedidoLcDetJava.getCurvaBase(), pedidoLcDetJava.getDiametro(),
              pedidoLcDetJava.getEsfera(), pedidoLcDetJava.getCilindro(), pedidoLcDetJava.getModelo(), pedidoLcDetJava.getEje(),
              pedidoLcDetJava.getColor(), pedidoLcDetJava.getCantidad());
      db.insertQuery( sql );
      db.close();
    }

    public static void updatePedidoLcDet(PedidoLcDetJava pedidoLcDetJava) throws ParseException {
        Connections db = new Connections();
        String sql = "";
        String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
        sql = String.format("UPDATE pedido_lc_det SET id_pedido = '%s', curva_base = '%s', diametro = '%s', esfera = '%s'," +
                "cilindro = '%s', modelo = '%s', eje = '%s', color = '%s' WHERE num_reg = %d;",
                pedidoLcDetJava.getIdPedido(), pedidoLcDetJava.getCurvaBase(), pedidoLcDetJava.getDiametro(),
                pedidoLcDetJava.getEsfera(), pedidoLcDetJava.getCilindro(), pedidoLcDetJava.getModelo(), pedidoLcDetJava.getEje(),
                pedidoLcDetJava.getColor(), pedidoLcDetJava.getId());
        db.insertQuery( sql );
        db.close();
    }


    public static void updatePedidoLc(PedidoLcJava pedidoLcJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("UPDATE pedido_lc SET fecha_entrega = %s WHERE id_pedido = '%s';",
              Utilities.toString(pedidoLcJava.getFechaEntrega(), formatTimeStamp), StringUtils.trimToEmpty(pedidoLcJava.getIdPedido()) );
      db.updateQuery(sql);
      db.close();
    }

    public static void eliminaPedidoLcDet( PedidoLcDetJava pedidoLcDetJava ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("DELETE FROM pedido_lc_det WHERE num_reg = %d;", pedidoLcDetJava.getId());
        stmt.executeUpdate(sql);
        con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }


    public static void eliminaPedidoLc( PedidoLcJava pedidoLcJava ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("DELETE FROM pedido_lc WHERE id_pedido = '%s';", pedidoLcJava.getIdPedido());
        stmt.executeUpdate(sql);
        con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }


}
