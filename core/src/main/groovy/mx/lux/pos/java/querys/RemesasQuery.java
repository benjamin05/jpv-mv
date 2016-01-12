package mx.lux.pos.java.querys;

import mx.lux.pos.java.Utilities;
import mx.lux.pos.java.repository.Parametros;
import mx.lux.pos.java.repository.RemesasJava;
import mx.lux.pos.java.repository.TransInvJava;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RemesasQuery {

	private static ResultSet rs;
    private static Statement stmt;

    public static void saveOrUpdateRemesas(RemesasJava remesasJava) throws ParseException {
      Connections db = new Connections();
      String sql = "";
      String formatTimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
      if( remesasJava.getIdRemesa() != null ){
        sql = String.format("UPDATE remesas SET id_tipo_docto = '%s', id_docto = '%s', docto = '%s', clave = '%s'," +
                "letra = '%s', archivo = '%s', articulos = %d, estado = '%s', sistema = '%s', fecha_mod = %s," +
                "fecha_recibido = %s, fecha_carga = %s WHERE id_remesa = %d;",
                remesasJava.getIdTipoDocto(), remesasJava.getIdDocto(), remesasJava.getDocto(), remesasJava.getClave(),
                remesasJava.getLetra(), remesasJava.getArchivo(), remesasJava.getArticulos(), remesasJava.getEstado(),
                remesasJava.getSistema(), Utilities.toString(remesasJava.getFechaMod(), formatTimeStamp),
                Utilities.toString( remesasJava.getFechaRecibido(), formatTimeStamp),
                Utilities.toString( remesasJava.getFechaCarga(), formatTimeStamp), remesasJava.getIdRemesa());
      } else {
        sql = String.format("INSERT INTO remesas (id_tipo_docto, id_docto, docto, clave, letra, archivo, articulos," +
                "estado, sistema, fecha_mod, fecha_recibido, fecha_carga)" +
                "VALUES('%s','%s','%s','%s','%s','%s',%d,'%s','%s',%s, %s, %s);",
                remesasJava.getIdTipoDocto(), remesasJava.getIdDocto(), remesasJava.getDocto(), remesasJava.getClave(),
                remesasJava.getLetra(), remesasJava.getArchivo(), remesasJava.getArticulos(), remesasJava.getEstado(),
                remesasJava.getSistema(), Utilities.toString(remesasJava.getFechaMod(), formatTimeStamp),
                Utilities.toString( remesasJava.getFechaRecibido(), formatTimeStamp),
                Utilities.toString( remesasJava.getFechaCarga(), formatTimeStamp));
      }
      db.insertQuery( sql );
      db.close();
    }
	
}
