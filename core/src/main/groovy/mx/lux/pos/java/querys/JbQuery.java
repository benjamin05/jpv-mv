package mx.lux.pos.java.querys;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.*;
import mx.lux.pos.model.Jb;
import org.apache.commons.lang3.StringUtils;

public class JbQuery {

	private static ResultSet rs;
    private static Statement stmt;
    private Jb jb;
	
	public static List<JbEstadosGrupo> listaJbEstadosGrupo(){
		List<JbEstadosGrupo> lstEstados = new ArrayList<JbEstadosGrupo>();
		try {			
            Connection con = Connections.doConnect();
            stmt = con.createStatement();
            String sql = "select * from jb_edo_grupo;";
            rs = stmt.executeQuery(sql);
            con.close();
            while (rs.next()) {
            	JbEstadosGrupo jbEstado = new JbEstadosGrupo(); 
            	jbEstado.setIdEdoGrupo(rs.getInt("id_edo_grupo"));
            	jbEstado.setDescripcionGrupo(rs.getString("descripcion_grupo"));
                lstEstados.add(jbEstado);
            }
        } catch (SQLException err) {
            System.out.println( err );
        }
		return lstEstados;
	}
	
	public static List<JbJava> busquedaJb(String rx, String cliente, String estado, String atendio) throws ParseException{

        List<JbJava> lstJbs = new ArrayList<JbJava>();

        try {
            Connection con = Connections.doConnect();
            stmt = con.createStatement();
            String sql = "";
            if(rx.length() <= 0 && cliente.length() <= 0 && estado.length() <= 0 && atendio.length() <= 0){
//            	sql = "SELECT * FROM jb INNER JOIN jb_edos ON (jb.estado = jb_edos.id_edo);";
                sql = "select * from jb left join jb_edos on id_edo = jb.estado order by fecha_mod desc limit 100";
            } else {
            	String queryRx = "";
            	Boolean tieneRx = false;
            	String queryCliente = "";
            	Boolean tieneCliente = false;
            	String queryEstado = "";
            	Boolean tieneEstado = false;
            	String queryAtendio = "";

            	if( rx.length() > 0 ){
            		queryRx = "rx = '"+rx+"'";
            		tieneRx = true;
            	}

            	if ( cliente.length() > 0 ){
            		queryCliente = (tieneRx ?" and ": "") +"cliente like '%"+cliente+"%'";
            	}

            	if ( estado.length() > 0 ) {
            		queryEstado = (tieneRx || tieneCliente ? " and " : "") + "jb_edos.descr = '"+estado+"'";
            	}

            	if ( atendio.length() > 0 ) {
            		queryAtendio = (tieneRx || tieneCliente || tieneEstado ? " and " : "") + "emp_atendio = '"+atendio+"'";
            	}

            	String parametros = queryRx+queryCliente+queryEstado+queryAtendio;
            	sql = String.format("SELECT * FROM jb INNER JOIN jb_edos ON (jb.estado = jb_edos.id_edo) WHERE %s order by rx asc;", parametros);            	
            }
            rs = stmt.executeQuery(sql);
            con.close();
            while (rs.next()) {
            	JbJava jb = new JbJava();
            	Double saldo = 0.00;
            	String saldoTmp = rs.getString("saldo");
            	saldoTmp = saldoTmp != null ? saldoTmp.replace("$", "") : "0.00";
            	saldoTmp = saldoTmp != null ? saldoTmp.replace(",", "") : "0.00";
            	try{
            		saldo = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(saldoTmp)).doubleValue();
            	} catch ( NumberFormatException e ){ System.out.println( e );}
            	jb.setValores(rs);
            	lstJbs.add(jb);
            }
        } catch (SQLException err) {
            System.out.println( err );
        }
		return lstJbs;
	}
	
	
	public static JbJava buscarPorRx( String rx ){
	JbJava jb = null;
	try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "select * from jb where rx = '"+rx+"';";
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jb = new JbJava();
          Double saldo = 0.00;
          String saldoTmp = rs.getString("saldo");
          saldoTmp = saldoTmp != null ? saldoTmp.replace("$", "") : "0.00";
          saldoTmp = saldoTmp != null ? saldoTmp.replace(",", "") : "0.00";
          try{
            saldo = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(saldoTmp)).doubleValue();
          } catch ( ParseException e ){ System.out.println( e );}
          jb.setValores( rs );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return jb;
	}
	
	public static JbEstados buscarEstadoPorId( String estado ){
		JbEstados jbEstados = new JbEstados();
		try {			
            Connection con = Connections.doConnect();
            stmt = con.createStatement();
            String sql = "select * from jb_edos where id_edo = '"+estado+"';";            
            rs = stmt.executeQuery(sql);
            con.close();
            while (rs.next()) {
            	jbEstados = jbEstados.setValores(rs.getString("id_edo"), rs.getString("llamada"), rs.getString("descr"));
            }
        } catch (SQLException err) {
            System.out.println( err );
        }
		return jbEstados;
	}
	
	
	public static List<JbTrack> buscarJbTrackPorRx( String rx ){
		List<JbTrack> lstTracks = new ArrayList<JbTrack>();
		try {
			JbTrack jbTrack = new JbTrack();
            Connection con = Connections.doConnect();
            stmt = con.createStatement();
            String sql = "select * from jb_track where rx = '"+rx+"';";
            rs = stmt.executeQuery(sql);
            con.close();
            while (rs.next()) {
            	System.out.println(rs.getTimestamp("fecha"));
            	jbTrack = jbTrack.setValores(rs.getString("rx"), rs.getString("estado"), rs.getString("obs"), rs.getString("emp"), 
            			rs.getString("id_viaje"), rs.getTimestamp("fecha"), rs.getString("id_mod"), rs.getString("id_jbtrack"));
            	lstTracks.add(jbTrack);
            }
        } catch (SQLException err) {
            System.out.println( err );
        }
		return lstTracks;
	}

    public static void updateEstadoJbRx (String rx, String estado) {
        String sql = "update jb set estado = '" + estado + "' where rx = '" + rx + "'";
        Connections db = new Connections();
        db.updateQuery(sql);
        db.close();
    }

    public static String getMaterialJbRx (String rx) {
        rs = null;
        String value = "";

        String sql = "select material from jb where rx = '"+rx+"'";
        Connections db = new Connections();
        rs = db.selectQuery(sql);

        try {
            while ( rs.next() ) {
                value = rs.getString("material");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static String getRotoJbRx (String rx) {
        rs = null;
        String value = "";

        String sql = "select roto from jb where rx = '"+rx+"'";
        Connections db = new Connections();
        rs = db.selectQuery(sql);

        try {
            while ( rs.next() ) {
                value = rs.getString("roto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static Jb getJbRxSimple (String rx) {

        Jb jb = null;
        Connections db = new Connections();
        ResultSetMap<Jb> resultSetMapper = new ResultSetMap<Jb>();
        ResultSet resultSet = null;

        String sql = "select * from jb where rx = '"+rx+"'";

        resultSet = db.selectQuery(sql);

        List<Jb> lista = resultSetMapper.mapRersultSetToObject(resultSet, Jb.class);

        if( lista != null ) {
            for(Jb pojo : lista){
                jb = pojo;
//                System.out.println(pojo);
            }
        }else{
//            System.out.println("ResultSet is empty. Please check if database table is empty");
        }

        db.close();

        return jb;
    }


    public static void eliminaJbLLamada( String rx ){
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("DELETE FROM jb_llamada where rx = '%s';", StringUtils.trimToEmpty(rx));
        stmt.executeUpdate(sql);
        con.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }


    public static List<JbJava> buscaJbPorIdGrupo( String idGrupo ){
      List<JbJava> lstJb = new ArrayList<JbJava>();
      JbJava jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb where id_grupo = '%s';", StringUtils.trimToEmpty(idGrupo));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jbJava = new JbJava();
          jbJava = jbJava.setValores( rs );
          lstJb.add(jbJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstJb;
    }


    public static JbLlamadaJava buscaJbLlamadaPorIdGrupo( String rx ){
      JbLlamadaJava jbLlamadaJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb_llamada where rx = '%s';", StringUtils.trimToEmpty(rx));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jbLlamadaJava = new JbLlamadaJava();
          jbLlamadaJava = jbLlamadaJava.mapeoParametro(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return jbLlamadaJava;
    }


    public static void saveJbTrack (JbTrack jbTrack) {
      String sql = String.format("INSERT INTO jb_track (rx,estado,obs,emp,id_viaje) VALUES('%s','%s','%s','%s','%s');",
              jbTrack.getRx(), jbTrack.getEstado(), jbTrack.getObs(), jbTrack.getEmp(), jbTrack.getIdViaje());
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }


    public static JbJava saveJb(JbJava jbJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      JbJava jb = null;
      sql = String.format("INSERT INTO jb (rx,estado,id_viaje,caja,id_cliente,roto,emp_atendio,num_llamada," +
              "material,surte,saldo,jb_tipo,volver_llamar,fecha_promesa,cliente,obs_ext,ret_auto,tipo_venta,fecha_venta," +
              "id_grupo,externo) VALUES('%s','%s','%s','%s','%s',%d,'%s',%d,'%s','%s',%s,'%s',%s,%s,'%s','%s','%s','%s'," +
              "%s,%s,'%s');", jbJava.getRx(), jbJava.getEstado(), jbJava.getIdViaje(), jbJava.getCaja(), jbJava.getIdCliente(),
              jbJava.getRoto(), jbJava.getEmpAtendio(), jbJava.getNumLlamada(), jbJava.getMaterial(), jbJava.getSurte(),
              Utilities.toMoney(jbJava.getSaldo()), jbJava.getJbTipo(), jbJava.getVolverLlamar(), Utilities.toString(jbJava.getFechaPromesa(), formatDate),
              jbJava.getCliente(), jbJava.getObsExt(), jbJava.getRetAuto(), jbJava.getTipoVenta(),
              Utilities.toString(jbJava.getFechaVenta(), formatTimeStamp), Utilities.toStringNull(jbJava.getIdGrupo()), jbJava.getExterno());
      db.insertQuery(sql);
      db.close();
      jb = buscarPorRx( jbJava.getRx() );

      return jb;
    }


    public static JbJava updateJb(JbJava jbJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      JbJava jb = null;
      sql = String.format("UPDATE jb SET estado = '%s',id_viaje = '%s',caja = '%s',id_cliente = '%s',roto = %d,emp_atendio = '%s',num_llamada = %d," +
            "material = '%s',surte = '%s',saldo = %s,jb_tipo = '%s',volver_llamar = %s,fecha_promesa = %s,cliente = '%s',obs_ext = '%s',ret_auto = '%s'," +
            "no_llamar = %s, tipo_venta = '%s',fecha_venta = %s,id_grupo = %s,no_enviar = %s,externo = '%s' WHERE rx = '%s';",
            jbJava.getEstado(), jbJava.getIdViaje(), jbJava.getCaja(), jbJava.getIdCliente(),jbJava.getRoto(), jbJava.getEmpAtendio(),
            jbJava.getNumLlamada(), jbJava.getMaterial(), jbJava.getSurte(), Utilities.toMoney(jbJava.getSaldo()), jbJava.getJbTipo(),
            Utilities.toString(jbJava.getVolverLlamar(), formatTimeStamp), Utilities.toString(jbJava.getFechaPromesa(), formatDate),
            jbJava.getCliente(), jbJava.getObsExt(), jbJava.getRetAuto(), Utilities.toBoolean(jbJava.getNoLlamar()), jbJava.getTipoVenta(),
            Utilities.toString(jbJava.getFechaVenta(), formatTimeStamp), Utilities.toStringNull(jbJava.getIdGrupo()), Utilities.toBoolean(jbJava.getNoEnviar()),
            jbJava.getExterno(), jbJava.getRx());
      db.insertQuery(sql);
      db.close();
      jb = buscarPorRx( jbJava.getRx() );

      return jb;
    }


    public static List<JbJava> buscarJbPorEstado( String estado ){
      List<JbJava> lstJb = new ArrayList<JbJava>();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "select * from jb where estado = '"+StringUtils.trimToEmpty(estado)+"';";
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          JbJava jb = new JbJava();
          jb = jb.setValores(rs);
          lstJb.add(jb);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstJb;
    }
}
