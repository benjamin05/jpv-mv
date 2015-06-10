package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.model.Jb;
import mx.lux.pos.java.repository.ArticulosJava;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class ArticulosQuery {

	private static ResultSet rs;
    private static Statement stmt;
    private Jb jb;
	
	public static ArticulosJava busquedaArticuloPorId(Integer idArticulo) throws ParseException{
      ArticulosJava articulosJava = new ArticulosJava();

      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if( idArticulo != null ){
          sql = String.format("SELECT * FROM articulos WHERE id_articulo = '%d';", idArticulo);
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            articulosJava.setValores(rs.getInt("id_articulo"), rs.getString("articulo"), rs.getString("color_code"), rs.getString("desc_articulo"),
                    rs.getString("id_generico"), rs.getString("id_gen_tipo"), rs.getString("id_gen_subtipo"), Utilities.toBigDecimal(rs.getString("precio")),
                    Utilities.toBigDecimal(rs.getString("precio_o")), rs.getString("s_articulo"), rs.getString("id_sync"), rs.getDate("fecha_mod"),
                    rs.getString("id_mod"), rs.getInt("id_sucursal"), rs.getString("color_desc"), rs.getString("id_cb"), rs.getString("id_diseno_lente"),
                    rs.getInt("existencia"), rs.getString("tipo"), rs.getString("subtipo"), rs.getString("marca"), rs.getString("proveedor"),
                    rs.getString("indice_dioptra"));
          }
          con.close();
        } else {
          articulosJava = null;
          System.out.println( "No existen el articulo: "+idArticulo );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return articulosJava;
	}

}
