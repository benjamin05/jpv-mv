package mx.lux.pos.querys;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

public class Connections {

    private Connection conexion;
    private PreparedStatement pst;
    private ResultSet rs;

    public Connections () {
        pst = null;
        this.conexion = doConnect();
    }


    static String getDataBase( ) {
      String database = "";
      URL url = null;

      try {
        url = new ClassPathResource( "database.properties" ).getURL();
        //System.out.println( "ruta del archivo de version: "+url.getPath() );
        if ( url != null ) {
          BufferedReader in = new BufferedReader( new InputStreamReader(url.openStream()) );
          String line = in.readLine();
          String[] elementos = line.split( "=" );
          if ( elementos.length >= 2 && elementos[0].equalsIgnoreCase("jdbc.url")) {
            database = String.format( "%s", elementos[ 1 ] );
          } else {
            System.out.println( String.format("No se pudo leer el archivo database.properties") );
          }
        } else {
          database = "jdbc:postgresql://localhost:5432/12004";
        }
      } catch ( Exception e ) {
        System.out.println( String.format("No se pudo leer el archivo database.properties \n%s", e.getMessage()) );
      }
        return database;
    }



    public static Connection doConnect( ) {

        Connection conexion = null;

//        String url = database.getProperty("jdbc.url");
//        String username = database.getProperty("jdbc.username");
//        String password = database.getProperty("jdbc.password");

        String dataBase = getDataBase();


        String url = StringUtils.trimToEmpty(dataBase).length() > 0 ? dataBase : "jdbc:postgresql://localhost:5432/12004";
        String username = "postgres";
        String password = "";

        try {
            conexion = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

       return conexion;
    }

    public void insertQuery(String sql) {

        try {
            System.out.println("Execute Insert: " + sql);
            this.pst = conexion.prepareStatement(sql);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateQuery(String sql) {

        try {
            System.out.println("Execute Update: " + sql);
            this.pst = conexion.prepareStatement(sql);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if ( this.pst != null) {
                this.pst.close();
            }
            if ( this.conexion != null) {
                this.conexion.close();
            }
        } catch (SQLException e) {

        }
    }

//    public static List<T> selectQuery(String rx ){
//    }
    public ResultSet selectQuery(String sql) {

        rs = null;
        System.out.println("Execute query: " + sql);
        try {
            this.pst = conexion.prepareStatement(sql);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }
}



