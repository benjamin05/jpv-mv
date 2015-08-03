package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.DescuentosJava;
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
          con.close();
          while (rs.next()) {
            DetalleNotaVentaJava detalleNotaVentaJava = new DetalleNotaVentaJava();
            detalleNotaVentaJava.setValores( rs );
            lstDetalleNotaVen.add(detalleNotaVentaJava);
          }
        } else {
          System.out.println( "No existen la nota: "+idFactura );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstDetalleNotaVen;
	}


    public static DetalleNotaVentaJava busquedaDetallesNotaVenPorIdFacturaEIdArticulo(String idFactura, Integer idArticulo) throws ParseException{
      DetalleNotaVentaJava detalleNotaVentaJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(idFactura).length() > 0){
          sql = String.format("SELECT * FROM detalle_nota_ven WHERE id_factura = '%s' AND id_articulo = %d;",
                  StringUtils.trimToEmpty(idFactura), idArticulo);
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            detalleNotaVentaJava = new DetalleNotaVentaJava();
            detalleNotaVentaJava.setValores( rs );
          }
          con.close();
        } else {
                System.out.println( "No existen la nota: "+idFactura );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return detalleNotaVentaJava;
    }


    public static DetalleNotaVentaJava updateDetalleNotaVenta (DetalleNotaVentaJava detalleNotaVentaJava) throws ParseException {
      String sql = "";
      DetalleNotaVentaJava detalleNotaVenta = null;
      if( detalleNotaVentaJava.getId() != null ){
        sql = String.format("UPDATE detalle_nota_ven SET id_articulo = %d, id_tipo_detalle = '%s', cantidad_fac = %f, precio_unit_lista = %s," +
                "precio_unit_final = %s, fecha_mod = NOW(), surte = '%s', precio_calc_lista = %s, precio_calc_oferta = %s, precio_factura = %s," +
                "precio_conv = %s, id_rep_venta = '%s' WHERE id_factura = '%s' AND id_articulo = %d",
                detalleNotaVentaJava.getIdArticulo(), detalleNotaVentaJava.getIdTipoDetalle(), detalleNotaVentaJava.getCantidadFac(),
                Utilities.toMoney(detalleNotaVentaJava.getPrecioUnitLista()), Utilities.toMoney(detalleNotaVentaJava.getPrecioUnitFinal()),
                detalleNotaVentaJava.getSurte(),Utilities.toMoney(detalleNotaVentaJava.getPrecioCalcLista()), Utilities.toMoney(detalleNotaVentaJava.getPrecioCalcOferta()),
                Utilities.toMoney(detalleNotaVentaJava.getPrecioFactura()),Utilities.toMoney(detalleNotaVentaJava.getPrecioConv()),
                StringUtils.trimToEmpty(detalleNotaVentaJava.getIdRepVenta()), StringUtils.trimToEmpty(detalleNotaVentaJava.getIdFactura()), detalleNotaVentaJava.getIdArticulo());
      } else {
        sql = String.format("INSERT INTO detalle_nota_ven (id_factura,id_articulo,id_tipo_detalle,cantidad_fac,precio_unit_lista," +
                "precio_unit_final,fecha_mod,surte,precio_calc_lista,precio_calc_oferta,precio_factura,precio_conv,id_rep_venta)" +
                "VALUES('%s',%d,'%s',%f,%s,%s,NOW(),'%s',%s,%s,%s,%s,'%s')",
                StringUtils.trimToEmpty(detalleNotaVentaJava.getIdFactura()),detalleNotaVentaJava.getIdArticulo(), detalleNotaVentaJava.getIdTipoDetalle(), detalleNotaVentaJava.getCantidadFac(),
                Utilities.toMoney(detalleNotaVentaJava.getPrecioUnitLista()), Utilities.toMoney(detalleNotaVentaJava.getPrecioUnitFinal()),
                detalleNotaVentaJava.getSurte(),Utilities.toMoney(detalleNotaVentaJava.getPrecioCalcLista()), Utilities.toMoney(detalleNotaVentaJava.getPrecioCalcOferta()),
                Utilities.toMoney(detalleNotaVentaJava.getPrecioFactura()),Utilities.toMoney(detalleNotaVentaJava.getPrecioConv()),
                StringUtils.trimToEmpty(detalleNotaVentaJava.getIdRepVenta()));
      }
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
      if( detalleNotaVentaJava.getId() != null ){
        detalleNotaVenta = detalleNotaVentaJava;
      } else {
        detalleNotaVenta = busquedaDetallesNotaVenPorIdFacturaEIdArticulo(detalleNotaVentaJava.getIdFactura(), detalleNotaVentaJava.getIdArticulo());
      }
      return detalleNotaVenta;
    }


    public static void eliminaDetalleNotaVenta( DetalleNotaVentaJava detalleNotaVentaJava ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("DELETE FROM detalle_nota_ven WHERE id = %d;", detalleNotaVentaJava.getId());
        stmt.executeUpdate(sql);
        con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
}
