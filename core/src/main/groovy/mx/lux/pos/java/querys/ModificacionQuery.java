package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.ModificacionImpJava;
import mx.lux.pos.java.repository.ModificacionJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModificacionQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<ModificacionJava> buscaModificaionPorIdFacturaAndTipo( String idFactura, String tipo ){
	  List<ModificacionJava> lstModificaciones = new ArrayList<ModificacionJava>();
      ModificacionJava modificacionJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from mod where id_factura = '%s' AND tipo = '%s';",
                StringUtils.trimToEmpty(idFactura), StringUtils.trimToEmpty(tipo));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          modificacionJava = new ModificacionJava();
          modificacionJava = modificacionJava.mapeoModificaion(rs);
          lstModificaciones.add(modificacionJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstModificaciones;
	}


    public static List<ModificacionJava> buscaModificaionPorFecha( Date fechaInicio, Date fechaFin ){
      List<ModificacionJava> lstModificaciones = new ArrayList<ModificacionJava>();
      ModificacionJava modificacionJava = null;
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from mod where fecha between %s AND %s;",
                Utilities.toString(fechaInicio, formatTimeStamp), Utilities.toString(fechaFin, formatTimeStamp));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          modificacionJava = new ModificacionJava();
          modificacionJava = modificacionJava.mapeoModificaion(rs);
          lstModificaciones.add(modificacionJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstModificaciones;
    }


    public static ModificacionImpJava buscaModificacionImpPorIdMod( Integer idMod ){
      ModificacionImpJava modificacionJava = null;
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from mod_imp where id_mod = %d;", idMod);
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          modificacionJava = new ModificacionImpJava();
          modificacionJava = modificacionJava.mapeoModificaionImp(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return modificacionJava;
    }
}
