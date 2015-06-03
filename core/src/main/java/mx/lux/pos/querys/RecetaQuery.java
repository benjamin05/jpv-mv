package mx.lux.pos.querys;

import mx.lux.pos.Utilities;
import mx.lux.pos.repository.DetalleNotaVentaJava;
import mx.lux.pos.repository.EmpleadoJava;
import mx.lux.pos.repository.EstadoJava;
import mx.lux.pos.repository.RecetaJava;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RecetaQuery {
	
	private static ResultSet rs;
    private static Statement stmt;

	public static List<RecetaJava> buscaRecetasPorIdCliente( Integer idCliente ){
	  List<RecetaJava> lstRecetas = new ArrayList<RecetaJava>();
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(idCliente != null){
          sql = String.format("SELECT * FROM receta WHERE id_cliente = %d;", idCliente);
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            RecetaJava recetaJava = new RecetaJava();
            recetaJava.setValores( rs );
            lstRecetas.add(recetaJava);
          }
          rs.close();
          con.close();
        } else {
          System.out.println( "No existen el cliente: "+idCliente );
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
        System.out.println( e );
        e.printStackTrace();
      }
        return lstRecetas;
	}


    public static RecetaJava saveOrUpdateRx (RecetaJava receta) {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      RecetaJava recetaJava = new RecetaJava();
      if( receta.getIdReceta() != null ){
        sql = String.format("UPDATE receta SET examen = %d, id_cliente = %d, fecha_receta = '%s', s_uso_anteojos = '%s'," +
                "id_optometrista = '%s', tipo_opt = '%s', od_esf_r = '%s', od_cil_r = '%s', od_eje_r = '%s', od_adc_r = '%s', od_adi_r = '%s'," +
                "od_prisma_h = '%s', oi_esf_r = '%s', oi_cil_r = '%s', oi_eje_r = '%s', oi_adc_r = '%s', oi_adi_r = '%s', oi_prisma_h = '%s'," +
                "di_lejos_r = '%s', di_cerca_r = '%s', od_av_r = '%s', oi_av_r = '%s', alt_obl_r = '%s', observaciones_r = '%s'," +
                "f_impresa = '%s', id_sync = '%s', fecha_mod = '%s', id_mod = '%s', id_sucursal = %d, di_od = '%s', di_oi = '%s'," +
                "material_arm = '%s', od_prisma_v = '%s', oi_prisma_v = '%s', tratamientos = '%s', udf5 = '%s', udf6 = '%s', id_rx_ori = '%s'," +
                "folio = '%s' WHERE id_receta = %d;",
                receta.getExamen(), receta.getIdCliente(), Utilities.toString(receta.getFechaReceta(), formatTimeStamp),
                receta.getsUsoAnteojos(), receta.getIdOptometrista(), receta.getTipoOpt(), receta.getOdEsfR(),receta.getOdCilR(),
                receta.getOdEjeR(), receta.getOdAdcR(), receta.getOdAdiR(),receta.getOdPrismaH(),receta.getOiEsfR(), receta.getOiCilR(),
                receta.getOiEjeR(), receta.getOiAdcR(), receta.getOiAdiR(), receta.getOiPrismaH(), receta.getDiLejosR(),
                receta.getDiCercaR(), receta.getOdAvR(), receta.getOiAvR(), receta.getAltOblR(), receta.getObservacionesR(), receta.getfImpresa(),
                receta.getIdSync(), Utilities.toString(receta.getFechaMod(), formatTimeStamp), receta.getIdMod(), receta.getIdSucursal(),
                receta.getDiOd(), receta.getDiOi(), receta.getMaterialArm(), receta.getOdPrismaV(), receta.getOiPrismaV(), receta.getTratamientos(),
                receta.getUdf5(), receta.getUdf6(), receta.getIdRxOri(), receta.getFolio());
        db.updateQuery(sql);
      } else {
        sql = String.format("INSERT INTO receta (examen,id_cliente,fecha_receta,s_uso_anteojos,id_optometrista,tipo_opt,od_esf_r,od_cil_r,od_eje_r," +
                "od_adc_r,od_adi_r,od_prisma_h,oi_esf_r,oi_cil_r,oi_eje_r,oi_adc_r,oi_adi_r,oi_prisma_h,di_lejos_r,di_cerca_r,od_av_r,oi_av_r,alt_obl_r," +
                "observaciones_r,f_impresa,id_sync,fecha_mod,id_mod,id_sucursal,di_od,di_oi,material_arm,od_prisma_v,oi_prisma_v,tratamientos,udf5,udf6," +
                "id_rx_ori,folio)  VALUES(%d,%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s'," +
                "'%s','%s','%s','%s','%s','%s',%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
                receta.getExamen(), receta.getIdCliente(), Utilities.toString(receta.getFechaReceta(), formatTimeStamp),
                receta.getsUsoAnteojos(), receta.getIdOptometrista(), receta.getTipoOpt(), receta.getOdEsfR(),receta.getOdCilR(),
                receta.getOdEjeR(), receta.getOdAdcR(), receta.getOdAdiR(),receta.getOdPrismaH(),receta.getOiEsfR(), receta.getOiCilR(),
                receta.getOiEjeR(), receta.getOiAdcR(), receta.getOiAdiR(), receta.getOiPrismaH(), receta.getDiLejosR(),
                receta.getDiCercaR(), receta.getOdAvR(), receta.getOiAvR(), receta.getAltOblR(), receta.getObservacionesR(), receta.getfImpresa(),
                receta.getIdSync(), Utilities.toString(receta.getFechaMod(), formatTimeStamp), receta.getIdMod(), receta.getIdSucursal(),
                receta.getDiOd(), receta.getDiOi(), receta.getMaterialArm(), receta.getOdPrismaV(), receta.getOiPrismaV(), receta.getTratamientos(),
                receta.getUdf5(), receta.getUdf6(), receta.getIdRxOri(), receta.getFolio());
        db.insertQuery( sql );
      }
      db.close();
      BigDecimal id = BigDecimal.ZERO;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        sql = "";
        sql = String.format("SELECT last_value FROM receta_seq;");
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          id = rs.getBigDecimal("last_value");
        }
        con.close();
        if( id.compareTo(BigDecimal.ZERO) > 0 ){
          con = Connections.doConnect();
          stmt = con.createStatement();
          sql = "";
          sql = String.format("SELECT * FROM receta WHERE id_receta = %d;", id.intValue());
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            recetaJava = recetaJava.setValores( rs );
          }
          con.close();
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
          e.printStackTrace();
      }

        return recetaJava;
    }


    public static RecetaJava buscaRecetaPorIdReceta(Integer idReceta){
      RecetaJava recetaJava = null;
      if( idReceta != null ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM receta where id_receta = %d;", idReceta);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          recetaJava = new RecetaJava();
          recetaJava = recetaJava.setValores(rs);
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
          e.printStackTrace();
      }
      }
      return recetaJava;
    }

    public static Boolean exists( Integer idReceta ){
      Boolean exist = false;
      RecetaJava recetaJava = buscaRecetaPorIdReceta( idReceta );
      if( recetaJava != null ){
        exist = true;
      }
      return exist;
    }

}
