package mx.lux.pos.ui.resources.utilidades;

import java.net.URL;

import javax.swing.ImageIcon;

public class Imagenes {

	public static ImageIcon urlImagen( String ruta, String descripcion ){
		URL imgURL = Imagenes.class.getResource(ruta);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, descripcion);
	    } else {
	        System.err.println("No se encontro el archivo: " + ruta);
	        return null;
	    }
	}
	
	
}
