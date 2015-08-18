package mx.lux.pos.java.querys;

import mx.lux.pos.java.repository.BancoEmisorJava;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        con.close();
        while (rs.next()) {
          bancoEmisorJava = new BancoEmisorJava();
          bancoEmisorJava = bancoEmisorJava.mapeoBancoEmisor( rs );
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
      }
	  return bancoEmisorJava;
	}
	
}
