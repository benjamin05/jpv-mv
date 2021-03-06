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
            	/*Double saldo = 0.00;
            	String saldoTmp = rs.getString("saldo");
            	saldoTmp = saldoTmp != null ? saldoTmp.replace("$", "") : "0.00";
            	saldoTmp = saldoTmp != null ? saldoTmp.replace(",", "") : "0.00";
            	try{
            		saldo = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(saldoTmp)).doubleValue();
            	} catch ( NumberFormatException e ){ System.out.println( e );}*/
            	jb.setValores(rs);
            	lstJbs.add(jb);
            }
        } catch (SQLException err) {
            System.out.println( err );
        }
		return lstJbs;
	}
	
	
	public static JbNotasJava buscarJbNotaPorIdNota( Integer idNota ){
	JbNotasJava jb = null;
	try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "select * from jb_notas where id_nota = "+idNota+";";
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jb = new JbNotasJava();
          jb.mapeoJbNotas(rs);
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
        e.printStackTrace();
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
            Connection con = Connections.doConnect();
            stmt = con.createStatement();
            String sql = "select * from jb_track where rx = '"+rx+"';";
            rs = stmt.executeQuery(sql);
            con.close();
            while (rs.next()) {
              JbTrack jbTrack = new JbTrack();
              jbTrack = jbTrack.mapeoJbTrack(rs);
              lstTracks.add(jbTrack);
            }
        } catch (SQLException err) {
            System.out.println( err );
        } catch (ParseException e) {
            System.out.println(e);
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
      } catch (ParseException e) {
        System.out.println(e);
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


    public static List<JbViaje> buscarJbViajesHoy( ){
      List<JbViaje> lstJbViaje = new ArrayList<JbViaje>();
      String formatDate = "yyyy-MM-dd";
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from jb_viaje where fecha = %s", Utilities.toString(new Date(), formatDate));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          JbViaje jbViaje = new JbViaje();
          jbViaje = jbViaje.setValores(rs);
          lstJbViaje.add(jbViaje);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstJbViaje;
    }


    public static List<JbJava> buscarJbPorTipo( String tipo ){
      List<JbJava> lstJb = new ArrayList<JbJava>();
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "select * from jb where jb_tipo = '"+StringUtils.trimToEmpty(tipo)+"';";
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

    public static List<JbJava> buscarJbPorEstados( String estado, String estado1 ){
      List<JbJava> lstJb = new ArrayList<JbJava>();
      try {
            Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "select * from jb where estado = '"+StringUtils.trimToEmpty(estado)+"' or estado = '"+StringUtils.trimToEmpty(estado1)+"';";
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


    public static List<JbSobres> buscaJbSobresPorFechaEnvioNullYRxNull( ){
      List<JbSobres> lstJb = new ArrayList<JbSobres>();
      JbSobres jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb_sobres where fecha_envio IS NULL AND (rx IS null OR rx = '');");
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jbJava = new JbSobres();
          jbJava = jbJava.mapeoJbSobres( rs );
          lstJb.add(jbJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstJb;
    }


    public static List<JbSobres> buscaJbSobresPorFechaEnvioNullYRxNotNull( ){
      List<JbSobres> lstJb = new ArrayList<JbSobres>();
      JbSobres jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb_sobres where fecha_envio IS NULL AND (rx IS NOT null AND rx != '');");
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jbJava = new JbSobres();
          jbJava = jbJava.mapeoJbSobres( rs );
          lstJb.add(jbJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstJb;
    }


    public static List<JbDev> buscaJbDevPorFechaEnvioNull( ){
      List<JbDev> lstJb = new ArrayList<JbDev>();
      JbDev jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb_dev where fecha_envio IS NULL;");
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jbJava = new JbDev();
          jbJava = jbJava.mapeoJbDev( rs );
          lstJb.add(jbJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstJb;
    }


    public static List<JbTrack> buscarJbTrackPorEstadoYFecha( String estado, Date fechaIni, Date fechaFin ){
      List<JbTrack> lstTracks = new ArrayList<JbTrack>();
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb_track WHERE estado = '%s' AND fecha BETWEEN %s AND %s;",
                StringUtils.trimToEmpty(estado), Utilities.toString(fechaIni,formatTimeStamp), Utilities.toString(fechaFin,formatTimeStamp));
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          JbTrack jbTrack = new JbTrack();
          jbTrack = jbTrack.mapeoJbTrack( rs );
          lstTracks.add(jbTrack);
        }
        for(JbTrack jbTrack : lstTracks){
          System.out.println( jbTrack.getFecha() );
          JbJava jb = buscarPorRx(jbTrack.getRx());
          if( jb != null ){
            jbTrack.setJb( jb );
          }
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
        System.out.println( e );
      }
        return lstTracks;
    }


    public static void saveJbLLamada (JbLlamadaJava jbLlamada) {
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      String sql = String.format("INSERT INTO jb_llamada (num_llamada,rx,fecha,estado,emp_atendio,tipo,id_mod)" +
              "VALUES(%d,'%s',%s,'%s','%s','%s','%s');",jbLlamada.getNumLlamada(), jbLlamada.getRx(),
              Utilities.toString(jbLlamada.getFecha(), formatTimeStamp), jbLlamada.getEstado(),jbLlamada.getEmpAtendio(),
              jbLlamada.getTipo(),jbLlamada.getIdMod());
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }


    public static List<JbLlamadaJava> buscaJbLlamadasPendientes( String empAtendio ){
      List<JbLlamadaJava> lstJb = new ArrayList<JbLlamadaJava>();
      JbLlamadaJava jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if( StringUtils.trimToEmpty(empAtendio).length() <= 0 ){
          sql = String.format("SELECT * FROM jb_llamada;");
        } else {
          sql = String.format("SELECT * FROM jb_llamada WHERE emp_atendio = '%s';", StringUtils.trimToEmpty(empAtendio) );
        }
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jbJava = new JbLlamadaJava();
          jbJava = jbJava.mapeoParametro( rs );
          lstJb.add(jbJava);
        }
        for(JbLlamadaJava jbLlamada : lstJb){
          jbLlamada.setJb( buscarPorRx(jbLlamada.getRx()));
          jbLlamada.setFormaContacto(FormaContactoQuery.buscaFormaContactoPorRx(jbLlamada.getRx()));
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
        System.out.println( e );
      }
      return lstJb;
    }



    public static void updateJbLLamada (JbLlamadaJava jbLlamada) {
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      String sql = String.format("UPDATE jb_llamada SET num_llamada = %d, fecha = %s, estado = '%s', emp_atendio = '%s'," +
              "tipo = '%s',id_mod = '%s' WHERE rx = '%s';",jbLlamada.getNumLlamada(), Utilities.toString(jbLlamada.getFecha(), formatTimeStamp),
              jbLlamada.getEstado(),jbLlamada.getEmpAtendio(), jbLlamada.getTipo(),jbLlamada.getIdMod(), jbLlamada.getRx());
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }


    public static void saveJbViaje (JbViaje jbViaje) {
      String formatDate = "yyyy-MM-dd";
      String formatTime = "HH:mm:ss.SSS";
      String sql = String.format("INSERT INTO jb_viaje VALUES('%s','%s',%s,%s,%s,'%s');",jbViaje.getIdViaje(), jbViaje.getFolio(),
              Utilities.toString(jbViaje.getFecha(), formatDate), Utilities.toString(jbViaje.getHora(), formatTime),
              Utilities.toBoolean(jbViaje.getAbierto()), jbViaje.getEmp() );
        Connections db = new Connections();
        db.updateQuery(sql);
        db.close();
    }


    public static void updateJbSobre (JbSobres jbSobres) {
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      String formatData = "yyyy-MM-dd";
      String sql = String.format("UPDATE jb_sobres SET folio_sobre = '%s', dest = '%s', emp = '%s', area = '%s', contenido = '%s'," +
              "id_viaje = '%s', fecha_envio = %s, fecha = %s, id_mod = '%s', rx = '%s' WHERE id = %d;",jbSobres.getFolioSobre(),
              jbSobres.getDest(), jbSobres.getEmp(), jbSobres.getArea(), jbSobres.getContenido(), jbSobres.getIdViaje(),
              Utilities.toString(jbSobres.getFechaEnvio(), formatData), Utilities.toString(jbSobres.getFecha(), formatTimeStamp),
              jbSobres.getIdMod(), jbSobres.getRx(), jbSobres.getId());
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }


    public static void updateJbDev (JbDev jbDev) {
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      String formatData = "yyyy-MM-dd";
      String sql = String.format("UPDATE jb_dev SET factura = '%s', sucursal = '%s', apartado = '%s', id_viaje = '%s', documento = '%s'," +
              "arm = '%s', col = '%s', fecha_envio = %s, fecha = %s, id_mod = '%s', rx = '%s', id_sobre = %d WHERE id_dev = %d;",
              jbDev.getFactura(), jbDev.getSucursal(), jbDev.getApartado(), jbDev.getIdViaje(), jbDev.getDocumento(),
              jbDev.getArm(), jbDev.getCol(), Utilities.toString(jbDev.getFechaEnvio(), formatData),
              Utilities.toString(jbDev.getFecha(), formatTimeStamp), jbDev.getIdMod(), jbDev.getRx(), jbDev.getIdSobre(), jbDev.getIdDev());
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }


    public static JbSobres buscaJbSobrePorRx( String rx ){
      JbSobres jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb_sobres where rx = '%s');", StringUtils.trimToEmpty(rx) );
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jbJava = new JbSobres();
          jbJava = jbJava.mapeoJbSobres( rs );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return jbJava;
    }


    public static JbSobres buscaUltimoSobre( ){
      JbSobres jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb_sobres WHERE id = (SELECT last_value FROM jb_sobres_id_seq);" );
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jbJava = new JbSobres();
          jbJava = jbJava.mapeoJbSobres( rs );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return jbJava;
    }




    public static JbSobres saveJbSobres (JbSobres jbSobres) {
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      String formatDate = "yyyy-MM-dd";
      String sql = String.format("INSERT INTO jb_sobres (folio_sobre,dest,emp,area,contenido,id_viaje,fecha_envio,fecha,rx) " +
              "VALUES('%s','%s','%s','%s','%s','%s',%s,%s,'%s');", jbSobres.getFolioSobre(),jbSobres.getDest(),jbSobres.getEmp(),
              jbSobres.getArea(),jbSobres.getContenido(),jbSobres.getIdViaje(),Utilities.toString(jbSobres.getFechaEnvio(),formatDate),
              Utilities.toString(jbSobres.getFecha(), formatTimeStamp),jbSobres.getRx());
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
      return buscaUltimoSobre();
    }


    public static void deleteJbSobres (Integer idJbSobre) {
      String sql = String.format("DELETE FROM jb_sobres WHERE id = %d;", idJbSobre);
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }

    public static Integer nextFolioJbSobre( ){
      Integer folio = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT nextval('jb_sobres_id_seq')");
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          folio = rs.getInt("nextval");
        }
      } catch (SQLException err) {
            System.out.println( err );
      }
      return folio;
    }


    public static List<JbJava> buscaJbRotosPendientes( ){
      List<JbJava> lstJb = new ArrayList<JbJava>();
      JbJava jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb WHERE estado = 'RS' AND rx IN (SELECT rx FROM jb_rotos);" );
        rs = stmt.executeQuery(sql);
        con.close();
        if( rs != null ){
          while (rs.next()) {
            jbJava = new JbJava();
            jbJava = jbJava.setValores( rs );
            lstJb.add(jbJava);
          }
        }
      } catch (SQLException err) {
           System.out.println( err );
      }
      return lstJb;
    }


    public static List<JbRotos> buscaJbRotosDetPendientes( String rx ){
      List<JbRotos> lstJb = new ArrayList<JbRotos>();
      JbRotos jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb_rotos WHERE rx = '%s';", StringUtils.trimToEmpty(rx) );
        rs = stmt.executeQuery(sql);
        con.close();
        if( rs != null ){
          while (rs.next()) {
            jbJava = new JbRotos();
            jbJava = jbJava.mapeoJbRotos( rs );
            lstJb.add(jbJava);
          }
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
        e.printStackTrace();
      }
        return lstJb;
    }


    public static void saveJbRotos (JbRotos jbRotos ) {
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      String formatDate = "yyyy-MM-dd";
      String sql = String.format("INSERT INTO jb_rotos (rx,tipo,material,causa,emp,num_roto,alta,fecha_prom,llamada,fecha,id_mod) " +
              "VALUES('%s','%s','%s','%s','%s',%d,%s,%s,%s,%s,'%s');",jbRotos.getRx(),jbRotos.getTipo(),jbRotos.getMaterial(),
              jbRotos.getCausa(),jbRotos.getEmp(),jbRotos.getNumRoto(),Utilities.toBoolean(jbRotos.getAlta()),
              Utilities.toString(jbRotos.getFecha(), formatDate), Utilities.toBoolean(jbRotos.getLlamada()),
              Utilities.toString(jbRotos.getFecha(), formatTimeStamp), jbRotos.getIdMod());
        Connections db = new Connections();
        db.updateQuery(sql);
        db.close();
    }


    public static List<JbJava> buscaJbOrdenesServicio( ){
      List<JbJava> lstJb = new ArrayList<JbJava>();
      JbJava jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "SELECT * FROM jb WHERE estado != 'TE' AND rx like 'S%';";
        rs = stmt.executeQuery(sql);
        con.close();
        if( rs != null ){
          while (rs.next()) {
            jbJava = new JbJava();
            jbJava = jbJava.setValores( rs );
            lstJb.add(jbJava);
          }
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstJb;
    }



    public static List<JbJava> buscaJbTodoOrdenesServicio( ){
      List<JbJava> lstJb = new ArrayList<JbJava>();
      JbJava jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "SELECT * FROM jb WHERE rx like 'S%';";
        rs = stmt.executeQuery(sql);
        con.close();
        if( rs != null ){
          while (rs.next()) {
            jbJava = new JbJava();
            jbJava = jbJava.setValores( rs );
            lstJb.add(jbJava);
          }
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      return lstJb;
    }



    public static List<JbServiciosJava> buscaJbServicios( ){
      List<JbServiciosJava> lstJb = new ArrayList<JbServiciosJava>();
      JbServiciosJava jbJava = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT * FROM jb_servicios;");
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          jbJava = new JbServiciosJava();
          jbJava = jbJava.mapeoJbServicio(rs);
          lstJb.add(jbJava);
        }
      } catch (SQLException err) {
        System.out.println( err );
      } catch (ParseException e) {
          e.printStackTrace();
      }
        return lstJb;
    }


    public static Integer buscaNextFolioJbNotas( ){
      Integer id = null;
      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("SELECT nextval('jb_notas_id_nota_seq');" );
        rs = stmt.executeQuery(sql);
        con.close();
        while (rs.next()) {
          id = rs.getInt("nextval");
        }
      } catch (SQLException err) {
            System.out.println( err );
      }
      return id;
    }


    public static void saveJbNotas (JbNotasJava jbNotas ) {
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      String formatDate = "yyyy-MM-dd";
      String sql = String.format("INSERT INTO jb_notas (id_nota,id_cliente,cliente,dejo,instruccion,emp,servicio,condicion,fecha_prom," +
              "fecha_orden,fecha_mod,tipo_serv,id_mod) VALUES(%d,'%s','%s','%s','%s','%s','%s','%s',%s,%s,%s,'%s','%s');",
              jbNotas.getIdNota(),jbNotas.getIdCliente(),jbNotas.getCliente(),jbNotas.getDejo(),jbNotas.getInstruccion(),
              jbNotas.getEmp(),jbNotas.getServicio(),jbNotas.getCondicion(),Utilities.toString(jbNotas.getFechaProm(), formatDate),
              Utilities.toString(jbNotas.getFechaOrden(), formatTimeStamp), Utilities.toString(jbNotas.getFechaMod(), formatTimeStamp),
              jbNotas.getTipoServ(),jbNotas.getIdMod());
      Connections db = new Connections();
      db.updateQuery(sql);
      db.close();
    }



    public static JbJava buscarPorRx( String rx ){
        JbJava jb = null;
        try {
            Connection con = Connections.doConnect();
            stmt = con.createStatement();
            String sql = "select * from jb where rx = '"+StringUtils.trimToEmpty(rx)+"';";
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


}
