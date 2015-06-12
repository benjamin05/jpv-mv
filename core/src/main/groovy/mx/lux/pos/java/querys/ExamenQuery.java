package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.DescuentosJava;
import mx.lux.pos.java.repository.ExamenJava;
import mx.lux.pos.java.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ExamenQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static List<ExamenJava> buscaExamenesPorIdCliente( Integer idCliente ){
	  List<ExamenJava> lstExamenes = new ArrayList<ExamenJava>();
      ExamenJava examenJava = null;
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM examen where id_cliente = %d ORDER BY fecha_alta ASC;", idCliente);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          examenJava = new ExamenJava();
          examenJava = examenJava.mapeoParametro(rs);
          lstExamenes.add(examenJava);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return lstExamenes;
	}


    public static ExamenJava buscaExamenesPorIdExamen( Integer idExamen ){
      ExamenJava examenJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM examen where id_examen = %d ;", idExamen);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          examenJava = new ExamenJava();
          examenJava = examenJava.mapeoParametro(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      return examenJava;
    }


    public static ExamenJava saveOrUpdateExamen(ExamenJava examenJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      ExamenJava examen = null;
      if( examenJava.getIdExamen() != null ){
        sql = String.format("UPDATE examen SET id_cliente = %d, id_atendio = '%s', av_sa_od_lejos_ex = '%s', av_sa_oi_lejos_ex = '%s'," +
                "obj_od_esf_ex = '%s', obj_od_cil_ex = '%s', obj_od_eje_ex = '%s', obj_oi_esf_ex = '%s', obj_oi_cil_ex = '%s'," +
                "obj_oi_eje_ex = '%s', obj_di_ex = '%s', sub_od_esf_ex = '%s', sub_od_cil_ex = '%s', sub_od_eje_ex = '%s'," +
                "sub_od_adc_ex = '%s', sub_od_adi_ex = '%s', sub_od_av_ex = '%s', sub_oi_esf_ex = '%s', sub_oi_cil_ex = '%s'," +
                "sub_oi_eje_ex = '%s', sub_oi_adc_ex = '%s', sub_oi_adi_ex = '%s', sub_oi_av_ex = '%s', observaciones_ex = '%s'," +
                "di_od = '%s', di_oi = '%s', udf1 = '%s', udf2 = '%s', udf3 = '%s', factura = '%s', tipo_cli = '%s', tipo_oft = '%s'," +
                "fecha_alta = %s, id_oftalmologo = %d, hora_alta = %s, id_ex_ori = '%s' WHERE id_examen = %d;",
                examenJava.getIdCliente(), examenJava.getIdAtendio(), examenJava.getAvSaOdLejosEx(), examenJava.getAvSaOiLejosEx(),
                examenJava.getObjOdEsfEx(), examenJava.getObjOdCilEx(), examenJava.getObjOdEjeEx(), examenJava.getObjOiEsfEx(),
                examenJava.getObjOiCilEx(), examenJava.getObjOiEjeEx(), examenJava.getObjDiEx(), examenJava.getSubOdEsfEx(),
                examenJava.getSubOdCilEx(), examenJava.getSubOdEjeEx(), examenJava.getSubOdAdcEx(), examenJava.getSubOdAdiEx(),
                examenJava.getSubOdAvEx(), examenJava.getSubOiEsfEx(), examenJava.getSubOiCilEx(), examenJava.getSubOiEjeEx(),
                examenJava.getSubOiAdcEx(), examenJava.getSubOiAdiEx(), examenJava.getSubOiAvEx(), examenJava.getObservacionesEx(),
                examenJava.getDiOd(), examenJava.getDiOi(), examenJava.getUdf1(), examenJava.getUdf2(), examenJava.getUdf3(),
                examenJava.getFactura(), examenJava.getTipoCli(), examenJava.getTipoOft(), Utilities.toString(examenJava.getFechaAlta(), formatDate),
                examenJava.getIdOftalmologo(), Utilities.toString(examenJava.getHoraAlta(), formatTime), examenJava.getIdExOri(),
                examenJava.getIdExamen());
            db.updateQuery(sql);
      } else {
            sql = String.format("INSERT INTO examen (id_cliente, id_atendio, av_sa_od_lejos_ex, av_sa_oi_lejos_ex," +
                    "obj_od_esf_ex, obj_od_cil_ex, obj_od_eje_ex, obj_oi_esf_ex, obj_oi_cil_ex, obj_oi_eje_ex, obj_di_ex," +
                    "sub_od_esf_ex, sub_od_cil_ex, sub_od_eje_ex, sub_od_adc_ex, sub_od_adi_ex, sub_od_av_ex," +
                    "sub_oi_esf_ex, sub_oi_cil_ex, sub_oi_eje_ex, sub_oi_adc_ex, sub_oi_adi_ex, sub_oi_av_ex, observaciones_ex," +
                    "di_od, di_oi, udf1, udf2, udf3, factura, tipo_cli, tipo_oft, fecha_alta, id_oftalmologo, hora_alta, id_ex_ori)" +
                    "VALUES(%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s,'%s'," +
                    "'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%s,%d,%s,'%s');",
                    examenJava.getIdCliente(), examenJava.getIdAtendio(), examenJava.getAvSaOdLejosEx(), examenJava.getAvSaOiLejosEx(),
                    examenJava.getObjOdEsfEx(), examenJava.getObjOdCilEx(), examenJava.getObjOdEjeEx(), examenJava.getObjOiEsfEx(),
                    examenJava.getObjOiCilEx(), examenJava.getObjOiEjeEx(), examenJava.getObjDiEx(), examenJava.getSubOdEsfEx(),
                    examenJava.getSubOdCilEx(), examenJava.getSubOdEjeEx(), examenJava.getSubOdAdcEx(), examenJava.getSubOdAdiEx(),
                    examenJava.getSubOdAvEx(), examenJava.getSubOiEsfEx(), examenJava.getSubOiCilEx(), examenJava.getSubOiEjeEx(),
                    examenJava.getSubOiAdcEx(), examenJava.getSubOiAdiEx(), examenJava.getSubOiAvEx(), examenJava.getObservacionesEx(),
                    examenJava.getDiOd(), examenJava.getDiOi(), examenJava.getUdf1(), examenJava.getUdf2(), examenJava.getUdf3(),
                    examenJava.getFactura(), examenJava.getTipoCli(), examenJava.getTipoOft(), Utilities.toString(examenJava.getFechaAlta(), formatDate),
                    examenJava.getIdOftalmologo(), Utilities.toString(examenJava.getHoraAlta(), formatTime), examenJava.getIdExOri());
            db.insertQuery( sql );
      }
      db.close();
      if( examenJava.getIdExamen() != null ){
        examen = buscaExamenesPorIdExamen( examenJava.getIdExamen() );
      } else {
        BigDecimal id = BigDecimal.ZERO;
        try {
          Connection con = Connections.doConnect();
          stmt = con.createStatement();
          sql = "";
          sql = String.format("SELECT last_value FROM examen_seq;");
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            id = rs.getBigDecimal("last_value");
          }
          con.close();
          if( id.compareTo(BigDecimal.ZERO) > 0 ){
            con = Connections.doConnect();
            stmt = con.createStatement();
            sql = "";
            sql = String.format("SELECT * FROM examen WHERE id_examen = %d;", id.intValue());
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
              examen = new ExamenJava();
              examen = examen.mapeoParametro(rs);
            }
            con.close();
          }
        } catch (SQLException err) {
          System.out.println( err );
        }
      }

      return examen;
    }
}
