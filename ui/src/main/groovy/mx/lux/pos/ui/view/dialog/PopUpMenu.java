package mx.lux.pos.ui.view.dialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import mx.lux.pos.model.Jb;
import mx.lux.pos.java.querys.JbQuery;
import mx.lux.pos.java.querys.JbTrackQuery;
import mx.lux.pos.java.querys.ParametrosQuery;
import mx.lux.pos.java.repository.JbTrack;
import mx.lux.pos.java.repository.Parametros;
import mx.lux.pos.ui.model.Session;
import mx.lux.pos.ui.model.SessionItem;
import mx.lux.pos.ui.model.User;
import mx.lux.pos.ui.resources.utilidades.Constantes;
import mx.lux.pos.ui.resources.utilidades.SolicitaUrl;
import mx.lux.pos.ui.view.panel.ConsultaPanel;
import org.apache.commons.lang3.StringUtils;

import static mx.lux.pos.java.querys.JbQuery.getMaterialJbRx;
import static mx.lux.pos.java.querys.JbQuery.getRotoJbRx;
import static mx.lux.pos.java.querys.JbQuery.updateEstadoJbRx;
import static org.jfree.util.Log.debug;
import static org.jfree.util.Log.info;
import static org.jfree.util.Log.log;


public class PopUpMenu {

	private JPopupMenu pMenu;
	private JMenuItem itemConsultaTrabajo;
	private JMenuItem itemInfoPino;
    private JMenuItem itemRetener;
    private JMenuItem itemDesretener;
	
	public PopUpMenu( Component component, Integer x, Integer y, final String rx ){
	    pMenu = new JPopupMenu();

	    itemConsultaTrabajo = new JMenuItem("Consultar Trabajo");
	    itemInfoPino = new JMenuItem("Info Laboratorio");
        itemRetener = new JMenuItem("Retener");
        itemDesretener = new JMenuItem("Desretener");

        pMenu.add(itemConsultaTrabajo);
        pMenu.add(itemInfoPino);
        pMenu.add( itemRetener );
        pMenu.add(itemDesretener);

	    pMenu.show(component, x, y);
        //pMenu.setLocation(x,y);

        habilitaOpciones(rx);

	    itemConsultaTrabajo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConsultaTrabajoDialog consulta = new ConsultaTrabajoDialog( rx );				
			}
		});

	    itemInfoPino.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {

                Parametros idSucursal = ParametrosQuery.BuscaParametroPorId(Constantes.TAG_ID_SUCURSAL);
				Parametros urlInfoPino = ParametrosQuery.BuscaParametroPorId(Constantes.TAG_URL_INFO_PINO);

                if ( urlInfoPino.getValor() == null || urlInfoPino.getValor().equals("") ) {
                    System.err.println("Error, no se encontro pagina para INFO PINO");
                    return;
                }

				String url = String.format("%s?arg=%s|%s", StringUtils.trimToEmpty(urlInfoPino.getValor()),StringUtils.trimToEmpty(rx),
						StringUtils.trimToEmpty(idSucursal.getValor()));

				List<String> lstDatos = SolicitaUrl.getReponseFromURL(url);

				InfoLaboratorioDialog dialogo = new InfoLaboratorioDialog(lstDatos, rx);
			}
		});

        itemRetener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RetenidosDialog d = new RetenidosDialog(rx);
            }
        });

        itemDesretener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int confirm = JOptionPane.showConfirmDialog (null, "¿Desea enviar el trabajo?","Warning", JOptionPane.YES_NO_OPTION);

                if ( confirm == 0 ) {
                    desretenerJb(rx);
                    ConsultaPanel.buscar();
                }
            }
        });
	}

    private JMenuItem getItemRetener(String rx) {

        if ( itemRetener == null ) {
            itemRetener = new JMenuItem("Retener");

            itemRetener.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    String url = rx;
//                    InfoLaboratorioDialog dialogo = new InfoLaboratorioDialog(lstDatos, rx);
                }
            });
        }

        return itemRetener;
    }

    private void desretenerJb (String rx) {

        String material = getMaterialJbRx(rx);
        String roto = getRotoJbRx(rx);
        String emp = ( (User) Session.get(SessionItem.USER) ).getUsername();
        String tipo = "PE";

        if ( roto != null ) {
            if (!roto.equals("")) {
                tipo = "RPE";
            }
        }

        updateEstadoJbRx(rx, tipo);

        JbTrack jbTrack = new JbTrack();

        jbTrack.setEmp(emp);
        jbTrack.setEstado(tipo);
        jbTrack.setObs(material);
        jbTrack.setRx(rx);
        jbTrack.setIdModM("0");
        jbTrack.setIdViaje("");

        JbTrackQuery.insertJbTrack(jbTrack);
    }

    private void habilitaOpciones (String rx) {

        if ( rx == null )
            return;

        Jb jb = JbQuery.getJbRxSimple(rx);

        if ( jb == null )
            return;

        itemRetener.setEnabled(false);
        itemDesretener.setEnabled(false);

        if ( ! (jb.getEstado().equals("TE") || jb.getEstado().equals("CN") || jb.getEstado().equals("RTN")) ) {
            itemRetener.setEnabled(true);
        }

        if ( jb.getEstado().equals("RTN") ) {
            itemDesretener.setEnabled(true);
        }
    }
}
