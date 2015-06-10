package mx.lux.pos.java.querys;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import mx.lux.pos.java.repository.JbEstados;
import mx.lux.pos.java.repository.JbEstadosGrupo;
import mx.lux.pos.java.repository.JbJava;
import mx.lux.pos.java.repository.JbTrack;
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
            while (rs.next()) {
            	JbEstadosGrupo jbEstado = new JbEstadosGrupo(); 
            	jbEstado.setIdEdoGrupo(rs.getInt("id_edo_grupo"));
            	jbEstado.setDescripcionGrupo(rs.getString("descripcion_grupo"));
                lstEstados.add(jbEstado);
            }            
            con.close();
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
            while (rs.next()) {
            	JbJava jb = new JbJava();
            	Double saldo = 0.00;
            	String saldoTmp = rs.getString("saldo");
            	saldoTmp = saldoTmp != null ? saldoTmp.replace("$", "") : "0.00";
            	saldoTmp = saldoTmp != null ? saldoTmp.replace(",", "") : "0.00";
            	try{
            		saldo = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(saldoTmp)).doubleValue();
            	} catch ( NumberFormatException e ){ System.out.println( e );}
            	jb.setValores(rs.getString("rx"), rs.getString("descr"), rs.getString("id_viaje"), rs.getString("caja"), 
            			rs.getString("id_cliente"), rs.getInt("roto"), rs.getString("emp_atendio"), rs.getInt("num_llamada"), 
            			rs.getString("material"), rs.getString("surte"), new BigDecimal(saldo), rs.getString("jb_tipo"), 
            			rs.getDate("volver_llamar"), rs.getDate("fecha_promesa"), rs.getDate("fecha_mod"), rs.getString("cliente"), 
            			rs.getString("id_mod"), rs.getString("obs_ext"), rs.getString("ret_auto"), rs.getBoolean("no_llamar"), 
            			rs.getString("tipo_venta"), rs.getDate("fecha_venta"), rs.getString("id_grupo"), rs.getBoolean("no_enviar"), 
            			rs.getString("externo"));
            	lstJbs.add(jb);
            }            
            con.close();
        } catch (SQLException err) {
            System.out.println( err );
        }
		return lstJbs;
	}
	
	
	public static JbJava buscarPorRx( String rx ){
		JbJava jb = new JbJava();
		try {			
            Connection con = Connections.doConnect();
            stmt = con.createStatement();
            String sql = "select * from jb where rx = '"+rx+"';";
            rs = stmt.executeQuery(sql);                                
            while (rs.next()) {
            	Double saldo = 0.00;            	
            	String saldoTmp = rs.getString("saldo");
            	saldoTmp = saldoTmp != null ? saldoTmp.replace("$", "") : "0.00";
            	saldoTmp = saldoTmp != null ? saldoTmp.replace(",", "") : "0.00";
            	try{
            		saldo = NumberFormat.getInstance().parse(StringUtils.trimToEmpty(saldoTmp)).doubleValue();
            	} catch ( ParseException e ){ System.out.println( e );}
            	jb.setValores(rs.getString("rx"), rs.getString("estado"), rs.getString("id_viaje"), rs.getString("caja"), 
            			rs.getString("id_cliente"), rs.getInt("roto"), rs.getString("emp_atendio"), rs.getInt("num_llamada"), 
            			rs.getString("material"), rs.getString("surte"), new BigDecimal(saldo), rs.getString("jb_tipo"), 
            			rs.getDate("volver_llamar"), rs.getDate("fecha_promesa"), rs.getDate("fecha_mod"), rs.getString("cliente"), 
            			rs.getString("id_mod"), rs.getString("obs_ext"), rs.getString("ret_auto"), rs.getBoolean("no_llamar"), 
            			rs.getString("tipo_venta"), rs.getDate("fecha_venta"), rs.getString("id_grupo"), rs.getBoolean("no_enviar"), 
            			rs.getString("externo"));
            }            
            con.close();
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
            while (rs.next()) {
            	jbEstados = jbEstados.setValores(rs.getString("id_edo"), rs.getString("llamada"), rs.getString("descr"));
            }            
            con.close();
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
            while (rs.next()) {
            	System.out.println(rs.getTimestamp("fecha"));
            	jbTrack = jbTrack.setValores(rs.getString("rx"), rs.getString("estado"), rs.getString("obs"), rs.getString("emp"), 
            			rs.getString("id_viaje"), rs.getTimestamp("fecha"), rs.getString("id_mod"), rs.getString("id_jbtrack"));
            	lstTracks.add(jbTrack);
            }            
            con.close();
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
}
