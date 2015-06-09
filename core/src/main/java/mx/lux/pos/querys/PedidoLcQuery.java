package mx.lux.pos.querys;

import mx.lux.pos.repository.PedidoLcDetJava;
import mx.lux.pos.repository.PedidoLcJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        while (rs.next()) {
          pedidoLcJava = new PedidoLcJava();
          pedidoLcJava = pedidoLcJava.mapeoPedidoLc(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return pedidoLcJava;
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
        while (rs.next()) {
          pedidoLcDetJava = new PedidoLcDetJava();
          pedidoLcDetJava = pedidoLcDetJava.mapeoPedidoLcDet(rs);
          lstPedidosLcDet.add(pedidoLcDetJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstPedidosLcDet;
	}
	
}
