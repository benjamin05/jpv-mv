package mx.lux.pos.java.querys;

import mx.lux.pos.model.Jb;
import mx.lux.pos.java.repository.TerminalJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class TerminalQuery {

	private static ResultSet rs;
    private static Statement stmt;
    private Jb jb;
	
	public static TerminalJava busquedaTerminalPorIdTerm(String idTerm) throws ParseException{
      TerminalJava terminalJava = null;

      try {
        Connection con = Connections.doConnect();
        stmt = con.createStatement();
        String sql = "";
        if(StringUtils.trimToEmpty(idTerm).length() > 0){
          sql = String.format("SELECT * FROM pos WHERE id_terminal = '%s';", StringUtils.trimToEmpty(idTerm));
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            terminalJava = new TerminalJava();
            terminalJava.mapeoTerminal( rs );
          }
          con.close();
        }
      } catch (SQLException err) {
        System.out.println( err );
      }
	  return terminalJava;
	}

}
