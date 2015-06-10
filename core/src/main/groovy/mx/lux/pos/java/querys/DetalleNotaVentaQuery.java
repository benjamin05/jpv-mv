package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.DetalleNotaVentaJava;
import mx.lux.pos.model.Jb;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DetalleNotaVentaQuery {

	private static ResultSet rs;
    private static Statement stmt;
    private Jb jb;
	
	public static List<DetalleNotaVentaJava> busquedaDetallesNotaVenPorIdFactura(String idFactura) throws ParseException{
      List<DetalleNotaVentaJava> lstDetalleNotaVen = new ArrayList<DetalleNotaVentaJava>();

      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(idFactura).length() > 0){
          sql = String.format("SELECT * FROM detalle_nota_ven WHERE id_factura = '%s';", StringUtils.trimToEmpty(idFactura));
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            DetalleNotaVentaJava detalleNotaVentaJava = new DetalleNotaVentaJava();
            detalleNotaVentaJava.setValores( rs );
            	lstDetalleNotaVen.add(detalleNotaVentaJava);
          }
          con.close();
        } else {
          System.out.println( "No existen la nota: "+idFactura );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstDetalleNotaVen;
	}


    public static void updateDetalleNotaVenta (DetalleNotaVentaJava detalleNotaVentaJava) {
      String sql = String.format("update detalle_nota_ven set id_articulo = %d, id_tipo_detalle = '%s', cantidad_fac = %d, precio_unit_lista = %d," +
              "precio_unit_final = %d, fecha_mod = NOW(), surte = '%s', precio_calc_lista = %d, precio_calc_oferta = %d, precio_factura = %d, precio_conv = %d",
              detalleNotaVentaJava.getIdArticulo(), detalleNotaVentaJava.getIdTipoDetalle(), detalleNotaVentaJava.getCantidadFac(),
              detalleNotaVentaJava.getPrecioUnitLista(), detalleNotaVentaJava.getPrecioUnitFinal(), detalleNotaVentaJava.getSurte(),
              detalleNotaVentaJava.getPrecioCalcLista(), detalleNotaVentaJava.getPrecioCalcOferta(), detalleNotaVentaJava.getPrecioFactura(),
              detalleNotaVentaJava.getPrecioConv());
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }
}
