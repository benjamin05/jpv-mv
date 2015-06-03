package mx.lux.pos.querys;

import mx.lux.pos.repository.BancoEmisorJava;
import mx.lux.pos.repository.Parametros;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BancoEmisorQuery {

	private static ResultSet rs;
    private static Statement stmt;

	public static BancoEmisorJava BuscaBancoEmisorPorId( Integer idBancoEmisor ){
      BancoEmisorJava bancoEmisorJava = null;
      if( idBancoEmisor != null ){
	  try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = String.format("select * from banco_emi where id_banco_emi = %d;", idBancoEmisor);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          bancoEmisorJava = new BancoEmisorJava();
          bancoEmisorJava = bancoEmisorJava.mapeoBancoEmisor( rs );
        }
        con.close();
      } catch (SQLException err) {
        System.out.println( err );
      }
      }
	  return bancoEmisorJava;
	}
	
}
