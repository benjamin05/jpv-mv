package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RepoQuery {

	private static ResultSet rs;
    private static Statement stmt;


    public static Repo buscaRepoPorNumAndFactura( Integer num, String factura ){
      Repo repo = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM repo WHERE num_orden = %d AND factura = '%s';", num, StringUtils.trimToEmpty(factura));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          repo = new Repo();
          repo = repo.mapeoRepo(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return repo;
    }



    public static List<RepoDetJava> busquedaRepoDetByNumOrderAndFactura(Integer numOrden, String factura) throws ParseException{
      List<RepoDetJava> lstRepoDet = new ArrayList<RepoDetJava>();
      RepoDetJava repoDetJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("SELECT * FROM repo_det WHERE factura = '%s' AND num_orden = %d;", StringUtils.trimToEmpty(factura), numOrden);
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          repoDetJava = new RepoDetJava();
          repoDetJava.mapeoRepoDet(rs);
          lstRepoDet.add( repoDetJava );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstRepoDet;
    }


    public static List<RepoResp> busquedaRepoResp() throws ParseException{
      List<RepoResp> lstRepoResp = new ArrayList<RepoResp>();
      RepoResp repoResp = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("SELECT * FROM repo_resp;");
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          repoResp = new RepoResp();
          repoResp.mapeoRepoResp(rs);
          lstRepoResp.add( repoResp );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstRepoResp;
    }



    public static List<RepoCausa> busquedaRepoCausa() throws ParseException{
      List<RepoCausa> lstRepoCausa = new ArrayList<RepoCausa>();
      RepoCausa repoCausa = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("SELECT * FROM repo_causa;");
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          repoCausa = new RepoCausa();
          repoCausa.mapeoRepoCausa(rs);
          lstRepoCausa.add( repoCausa );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstRepoCausa;
    }



    public static List<RepoDetJava> busquedaRepoDetByFactura(String factura) throws ParseException{
      List<RepoDetJava> lstRepoDet = new ArrayList<RepoDetJava>();
      RepoDetJava repoDetJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        sql = String.format("SELECT * FROM repo_det WHERE factura = '%s';", StringUtils.trimToEmpty(factura));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          repoDetJava = new RepoDetJava();
          repoDetJava.mapeoRepoDet(rs);
          lstRepoDet.add( repoDetJava );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstRepoDet;
    }



    public static Integer buscaUltimoNumByFactura( String factura ){
      Repo repo = null;
      Integer number = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT MAX(num_orden) FROM repo WHERE factura = '%s';", StringUtils.trimToEmpty(factura));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
           number = rs.getInt("max");
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return number;
    }


    public static void saveRepo(Repo repo) {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("INSERT INTO repo (factura,num_orden,emp,resp,fecha,tipo,id_cliente,causa,problema,dx,instrucciones," +
              "s_uso_anteojos,od_esf,od_cil,od_eje,od_adc,od_adi,od_av,di_od,od_prisma,od_prisma_v,oi_esf,oi_cil,oi_eje,oi_adc," +
              "oi_adi,oi_av,di_oi,oi_prisma,oi_prisma_v,di_lejos,di_cerca,alt_obl,observaciones,area,folio,cliente,material," +
              "tratamientos,suc,ojo,altura_ind_der,altura_ind_izq,distancia_vertex,angulo_pantoscopico,angulo_facial," +
              "tamano_corredor,diametro_lenticular) VALUES('%s',%d,'%s','%s',%s,'%s',%d,'%s','%s','%s','%s','%s','%s','%s'," +
              "'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s'," +
              "'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');", StringUtils.trimToEmpty(repo.getFactura()),
              repo.getNumOrden(),StringUtils.trimToEmpty(repo.getEmp()),StringUtils.trimToEmpty(repo.getResp()),
              Utilities.toString(repo.getFecha(), formatTimeStamp), StringUtils.trimToEmpty(repo.getTipo()),
              repo.getIdCliente(),StringUtils.trimToEmpty(repo.getCausa()),StringUtils.trimToEmpty(repo.getProblema()),
              StringUtils.trimToEmpty(repo.getDx()),StringUtils.trimToEmpty(repo.getInstrucciones()),
              StringUtils.trimToEmpty(repo.getsUsoAnteojos()),StringUtils.trimToEmpty(repo.getOdEsf()),
              StringUtils.trimToEmpty(repo.getOdCil()),StringUtils.trimToEmpty(repo.getOdEje()),StringUtils.trimToEmpty(repo.getOdAdc()),
              StringUtils.trimToEmpty(repo.getOdAdi()),StringUtils.trimToEmpty(repo.getOdAv()),StringUtils.trimToEmpty(repo.getDiOd()),
              StringUtils.trimToEmpty(repo.getOdPrisma()),StringUtils.trimToEmpty(repo.getOdPrismaV()),StringUtils.trimToEmpty(repo.getOiEsf()),
              StringUtils.trimToEmpty(repo.getOiCil()),StringUtils.trimToEmpty(repo.getOiEje()),StringUtils.trimToEmpty(repo.getOiAdc()),
              StringUtils.trimToEmpty(repo.getOiAdi()),StringUtils.trimToEmpty(repo.getOiAv()),StringUtils.trimToEmpty(repo.getDiOi()),
              StringUtils.trimToEmpty(repo.getOiPrisma()),StringUtils.trimToEmpty(repo.getOiPrismaV()),StringUtils.trimToEmpty(repo.getDiLejos()),
              StringUtils.trimToEmpty(repo.getDiCerca()),StringUtils.trimToEmpty(repo.getAltObl()),StringUtils.trimToEmpty(repo.getObservaciones()),
              StringUtils.trimToEmpty(repo.getArea()),StringUtils.trimToEmpty(repo.getFolio()),StringUtils.trimToEmpty(repo.getCliente()),
              StringUtils.trimToEmpty(repo.getMaterial()),StringUtils.trimToEmpty(repo.getTratamientos()),StringUtils.trimToEmpty(repo.getSuc()),
              StringUtils.trimToEmpty(repo.getOjo()),StringUtils.trimToEmpty(repo.getAlturaIndDer()),StringUtils.trimToEmpty(repo.getAlturaIndIzq()),
              StringUtils.trimToEmpty(repo.getDistanciaVertex()),StringUtils.trimToEmpty(repo.getAnguloPantoscopico()),
              StringUtils.trimToEmpty(repo.getAnguloFacial()),StringUtils.trimToEmpty(repo.getTamanoCorredor()),
              StringUtils.trimToEmpty(repo.getDiametroLenticular()));
      db.insertQuery( sql );
      db.close();
    }


    public static void saveRepoDet(RepoDetJava repoDet) {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      sql = String.format("INSERT INTO repo_det (factura,num_orden,suc,ojo,tipo,v_old,v_new,campo,fecha) " +
              "VALUES('%s',%d,'%s','%s','%s','%s','%s','%s',%s);", StringUtils.trimToEmpty(repoDet.getFactura()),
              repoDet.getNumOrden(),StringUtils.trimToEmpty(repoDet.getSuc()),StringUtils.trimToEmpty(repoDet.getOjo()),
              StringUtils.trimToEmpty(repoDet.getTipo()),StringUtils.trimToEmpty(repoDet.getvOld()),
              StringUtils.trimToEmpty(repoDet.getvNew()),StringUtils.trimToEmpty(repoDet.getCampo()),
              Utilities.toString(repoDet.getFecha(),formatTimeStamp));
      db.insertQuery( sql );
      db.close();
    }
}
