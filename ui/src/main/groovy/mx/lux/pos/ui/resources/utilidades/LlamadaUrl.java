package mx.lux.pos.ui.resources.utilidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LlamadaUrl {

	public static List<String> getReponseFromURL( String urlString ) {
		List<String> content = new ArrayList<String>();
		String inputLine;
		try {
			URL url = new URL( urlString );
			BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );

			while ( ( inputLine = in.readLine() ) != null ) {
                content.add(inputLine.trim());
                System.out.println("linea:"+inputLine.trim());
            }

			in.close();
		} catch ( MalformedURLException e ) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return content;
	}
	
}
