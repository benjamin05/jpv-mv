package mx.lux.pos.ui.resources.utilidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by magno on 1/09/14.
 */
public class SolicitaUrl {

    public static List<String> getReponseFromURL( String urlString ) {
        List<String> content = new ArrayList<String>();
        String inputLine;
        URL url = null;
        HttpURLConnection conexion = null;
        BufferedReader br = null;

        try {

            url = new URL( urlString );
            conexion = (HttpURLConnection)url.openConnection();
            conexion.setReadTimeout(50000);
            conexion.connect();

            br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
//            BufferedReader in = new BufferedReader( new InputStreamReader( conexion.openStream() ) );

            Boolean agregar = false;

            while ( ( inputLine = br.readLine() ) != null ) {
                Matcher matI = Pattern.compile("<XX>").matcher(inputLine);
                Matcher matF = Pattern.compile("</XX>").matcher(inputLine);

                if ( matF.matches() )
                    agregar = false;

                if ( agregar )
                    content.add(inputLine.trim());

                if ( matI.matches() )
                    agregar = true;
            }

            br.close();
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        finally {
            conexion.disconnect();
            br = null;
            conexion = null;
        }
        return content;
    }

}
