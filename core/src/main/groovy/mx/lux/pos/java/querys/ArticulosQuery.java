package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.model.Jb;
import mx.lux.pos.java.repository.ArticulosJava;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ArticulosQuery {

	private static ResultSet rs;
    private static Statement stmt;
    private Jb jb;
	
	public static ArticulosJava busquedaArticuloPorId(Integer idArticulo) throws ParseException{
      ArticulosJava articulosJava = null;

      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if( idArticulo != null ){
          sql = String.format("SELECT * FROM articulos WHERE id_articulo = '%d';", idArticulo);
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            articulosJava = new ArticulosJava();
            articulosJava.setValores( rs );
          }
          con.close();
        } else {
          System.out.println( "No existen el articulo: "+idArticulo );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return articulosJava;
	}


    public static List<ArticulosJava> busquedaArticuloPorArticulo(String articulo) throws ParseException{
      List<ArticulosJava> lstArticulos = new ArrayList<ArticulosJava>();
      ArticulosJava articulosJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(articulo).length() > 0 ){
          sql = String.format("SELECT * FROM articulos WHERE articulo = '%s';", StringUtils.trimToEmpty(articulo));
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            articulosJava = new ArticulosJava();
            articulosJava.setValores( rs );
            lstArticulos.add(articulosJava);
          }
          con.close();
        } else {
          System.out.println( "No existen el articulo: "+articulo );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstArticulos;
    }


    public static List<ArticulosJava> busquedaArticuloPorDescripcion(String descripcion) throws ParseException{
      List<ArticulosJava> lstArticulos = new ArrayList<ArticulosJava>();
      ArticulosJava articulosJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(descripcion).length() > 0 ){
          sql = "SELECT * FROM articulos WHERE desc_articulo like '%"+StringUtils.trimToEmpty(descripcion)+"%' AND s_articulo = 'V';";
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            articulosJava = new ArticulosJava();
            articulosJava.setValores( rs );
            lstArticulos.add(articulosJava);
          }
          con.close();
        } else {
          System.out.println( "No existen el articulo con descripcion: "+descripcion );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstArticulos;
    }


    public static List<ArticulosJava> busquedaArticuloPorIdGenerico(String idGenerico) throws ParseException{
      List<ArticulosJava> lstArticulos = new ArrayList<ArticulosJava>();
      ArticulosJava articulosJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(idGenerico).length() > 0 ){
          sql = String.format("SELECT * FROM articulos WHERE id_generico = '%s' AND s_articulo = 'V';", StringUtils.trimToEmpty(idGenerico));
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            articulosJava = new ArticulosJava();
            articulosJava.setValores( rs );
            lstArticulos.add(articulosJava);
          }
          con.close();
        } else {
          System.out.println( "No existen el generico: "+idGenerico );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstArticulos;
    }


    public static List<ArticulosJava> busquedaArticuloPorArticuloParecido(String articulo) throws ParseException{
      List<ArticulosJava> lstArticulos = new ArrayList<ArticulosJava>();
      ArticulosJava articulosJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(articulo).length() > 0 ){
          sql = "SELECT * FROM articulos WHERE articulo ILIKE '"+StringUtils.trimToEmpty(articulo)+"%' ORDER BY articulo ASC;";
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            articulosJava = new ArticulosJava();
            articulosJava.setValores( rs );
            lstArticulos.add(articulosJava);
          }
          con.close();
        } else {
          System.out.println( "No existen el articulo: "+articulo );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstArticulos;
    }


    public static void saveOrUpdateArticulos(ArticulosJava articulosJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      articulosJava.trim();
      if( busquedaArticuloPorId(articulosJava.getIdArticulo()) != null ){
        sql = String.format("UPDATE articulos SET articulo = '%s', color_code = '%s', desc_articulo = '%s', id_generico = '%s'," +
                "id_gen_tipo = '%s', id_gen_subtipo = '%s', precio = %s, precio_o = %s, s_articulo = '%s', color_desc = '%s'," +
                "id_cb = '%s', id_diseno_lente = '%s', existencia = %d, tipo = '%s', subtipo = '%s', marca = '%s', proveedor = '%s'," +
                "indice_dioptra = '%s' WHERE id_articulo = %d;", articulosJava.getArticulo(), articulosJava.getColorCode(),
                articulosJava.getDescArticulo(), articulosJava.getIdGenerico(), articulosJava.getIdGenTipo(), articulosJava.getIdGenSubtipo(),
                Utilities.toMoney(articulosJava.getPrecio()), Utilities.toMoney(articulosJava.getPrecioO()), articulosJava.getsArticulo(),
                articulosJava.getColorDesc(), articulosJava.getIdCb(), articulosJava.getIdDisenoLente(), articulosJava.getExistencia(),
                articulosJava.getTipo(), articulosJava.getSubtipo(), articulosJava.getMarca(), articulosJava.getProveedor(),
                articulosJava.getIndiceDioptra(), articulosJava.getIdArticulo());
      } else {
          sql = String.format("INSERT INTO articulos (id_articulo, articulo, color_code, desc_articulo, id_generico," +
                  "id_gen_tipo, id_gen_subtipo, precio, precio_o, s_articulo, color_desc, id_cb, id_diseno_lente, existencia," +
                  "tipo, subtipo, marca, proveedor, indice_dioptra)" +
                  "VALUES(%d,'%s','%s','%s','%s','%s','%s',%s,%s,'%s','%s','%s','%s',%d,'%s','%s','%s','%s','%s');",
                  articulosJava.getIdArticulo(), articulosJava.getArticulo(), articulosJava.getColorCode(), articulosJava.getDescArticulo(), articulosJava.getIdGenerico(), articulosJava.getIdGenTipo(), articulosJava.getIdGenSubtipo(),
                  Utilities.toMoney(articulosJava.getPrecio()), Utilities.toMoney(articulosJava.getPrecioO()), articulosJava.getArticulo(),
                  articulosJava.getColorDesc(), articulosJava.getIdCb(), articulosJava.getIdDisenoLente(), articulosJava.getExistencia(),
                  articulosJava.getTipo(), articulosJava.getSubtipo(), articulosJava.getMarca(), articulosJava.getProveedor(),
                  articulosJava.getIndiceDioptra());
      }

        db.insertQuery( sql );
        db.close();
    }


}
